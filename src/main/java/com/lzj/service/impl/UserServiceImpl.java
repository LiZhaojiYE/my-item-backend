package com.lzj.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.lzj.component.MyRedis;
import com.lzj.dto.AccountLoginDTO;
import com.lzj.dto.UserInsertDTO;
import com.lzj.dto.UserUpdateDTO;
import com.lzj.dto.UserUpdatePasswordDTO;
import com.lzj.entity.User;
import com.lzj.exception.ServiceException;
import com.lzj.mapper.UserMapper;
import com.lzj.result.ResultCode;
import com.lzj.service.UserService;
import com.lzj.util.MinioUtil;
import com.lzj.vo.AccountLoginVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import  java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lzj.entity.table.UserTableDef.USER;

/** @author 李兆基 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private MyRedis myRedis;

    @Value("${myitem.minio.default-avatar}")
    private String MINIO_DEFAULT_AVATAR;
    @Value("${myitem.minio.bucket-name}")
    private String MINIO_BUCKET_NAME;
    @Value("${myitem.minio.avatar-dir}")
    private String MINIO_AVATAR_DIR;


    @Value("${myitem.token.access-token-prefix}")
    private String ACCESS_TOKEN_PREFIX;

    @Value("${myitem.token.refresh-token-prefix}")
    private String REFRESH_TOKEN_PREFIX;

    @Value("${myitem.token.access-token-map-prefix}")
    private String ACCESS_TOKEN_MAP_PREFIX;

    @Value("${myitem.token.refresh-token-map-prefix}")
    private String REFRESH_TOKEN_MAP_PREFIX;

    @Value("${myitem.token.access-token-expire}")
    private Integer ACCESS_TOKEN_EXPIRE;

    @Value("${myitem.token.refresh-token-expire}")
    private Integer REFRESH_TOKEN_EXPIRE;

    @Override
    public void insert(UserInsertDTO dto) {
        // DQL - 判断登录账号是否重复
        String username = dto.username();
        if (QueryChain.of(mapper)
                .where(USER.USERNAME.eq(username))
                .exists()) {
            throw new ServiceException(ResultCode.USERNAME_REPEAT, "登录账号重复：username=" + username);
        }

        // 构建用户 ORM 实体
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setAvatarUrl(MINIO_DEFAULT_AVATAR);
        user.setPasswordHash(BCrypt.hashpw(dto.password()));
        user.setVersion(0);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // DML - 插入用户记录
        if (mapper.insert(user) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库添加失败");
        }
    }
    @Override
    public AccountLoginVO loginByAccount(AccountLoginDTO dto) {
        String username = dto.username();

        // DQL - 判断登录账号是否存在
        User user = QueryChain.of(mapper)
                .where(USER.USERNAME.eq(username))
                .one();
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException(ResultCode.ACCOUNT_ILLEGAL, "账号不存在：username=" + username);
        }

        // 判断登录密码是否正确
        if (!BCrypt.checkpw(dto.password(), user.getPasswordHash())) {
            throw new ServiceException(ResultCode.ACCOUNT_ILLEGAL, "密码错误");
        }

        // 构建 VO 实体
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        user.setPasswordHash(null);
        AccountLoginVO vo = AccountLoginVO.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // 在 redis 中，更新用户登录双令牌
        this.updateTokens(accessToken, refreshToken, user.getId().toString());
        return vo;
    }
    @Override
    public User select(Long id) {

        // DQL - 根据 ID 查询用户记录
        User user = QueryChain.of(mapper)
                .where(USER.ID.eq(id))
                .one();
        if (ObjUtil.isNull(user)) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "用户不存在：id=" + id);
        }

        // 密码脱敏
        user.setPasswordHash(null);
        return user;
    }
    @Override
    public void update(Long id, UserUpdateDTO dto) {

        // DQL - 判断用户是否存在
        User user = this.select(id);

        // 构建用户 ORM 实体
        BeanUtils.copyProperties(dto, user);
        user.setUpdateTime(LocalDateTime.now());

        // DML - 更新用户记录
        if (mapper.update(user) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uploadAvatar(Long id, MultipartFile avatarFile) {

        // DQL - 根据主键查询用户记录
        User user = this.select(id);

        // 备份旧文件地址
        String oldAvatarUrl = user.getAvatarUrl();
        log.info("✅【uploadAvatar】获取到旧头像，avatarUrl={}", oldAvatarUrl);

        // 上传新文件到 MinIO
        String newAvatarUrl = MinioUtil.upload(avatarFile, MINIO_AVATAR_DIR, MINIO_BUCKET_NAME);
        log.info("✅【uploadAvatar】上传新头像到 MinIO，avatarUrl={}", newAvatarUrl);

        // DML - 更新用户头像
        user.setAvatarUrl(newAvatarUrl);
        user.setUpdateTime(LocalDateTime.now());
        if (mapper.update(user) <= 0) {
            log.warn("✅【uploadAvatar】更新用户头像失败，userId={}", id);
            MinioUtil.delete(newAvatarUrl, MINIO_AVATAR_DIR, MINIO_BUCKET_NAME);
            log.warn("✅【uploadAvatar】从 MinIO 中删除新上传的头像，avatarUrl={}", newAvatarUrl);
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
        log.info("✅【uploadAvatar】更新用户头像成功，userId={}", id);

        // 从 MinIO 中删除旧文件（数据库更新成功后）
        if (ObjectUtil.isNotNull(oldAvatarUrl) && !oldAvatarUrl.equals(MINIO_DEFAULT_AVATAR)) {
            MinioUtil.delete(oldAvatarUrl, MINIO_AVATAR_DIR, MINIO_BUCKET_NAME);
            log.info("✅【uploadAvatar】从 MinIO 中删除旧头像，avatarUrl={}", oldAvatarUrl);
        } else {
            log.info("✅【uploadAvatar】旧头像为空或默认头像，无需删除");
        }

        // 返回文件名称
        return newAvatarUrl;
    }

    @Override
    public void delete(Long id) {

        // DQL - 判断用户是否存在
        User user = this.select(id);
        Long userId = user.getId();

        // 构建用户 ORM 实体
        user.setIsDeleted(userId.intValue());
        user.setUpdateTime(LocalDateTime.now());

        // DML - 删除用户记录
        if (mapper.update(user) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }

        // 删除该用户的 AccessToken 及映射
        String accessToken = myRedis.get(ACCESS_TOKEN_MAP_PREFIX + userId);
        if (StrUtil.isNotBlank(accessToken)) {
            myRedis.del(ACCESS_TOKEN_PREFIX + accessToken);
            myRedis.del(ACCESS_TOKEN_MAP_PREFIX + userId);
        }

        // 删除该用户的 refreshToken 及映射
        String refreshToken = myRedis.get(REFRESH_TOKEN_MAP_PREFIX + userId);
        if (StrUtil.isNotBlank(refreshToken)) {
            myRedis.del(REFRESH_TOKEN_PREFIX + refreshToken);
            myRedis.del(REFRESH_TOKEN_MAP_PREFIX + userId);
        }
    }

    @Override
    public void updatePassword(Long id, UserUpdatePasswordDTO dto) {

        // DQL - 判断用户是否存在：不要调用 this.select() 方法，该方法末尾将密码隐藏了
        User user = QueryChain.of(mapper)
                .where(USER.ID.eq(id))
                .one();
        if (ObjUtil.isNull(user)) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "用户不存在：id=" + id);
        }

        // 判断登录密码（旧）是否正确
        if (!BCrypt.checkpw(dto.oldPassword(), user.getPasswordHash())) {
            throw new ServiceException(ResultCode.ACCOUNT_ILLEGAL, "原始密码错误");
        }

        // 构建用户 ORM 实体
        BeanUtils.copyProperties(dto, user);
        user.setPasswordHash(BCrypt.hashpw(dto.newPassword()));
        user.setUpdateTime(LocalDateTime.now());

        // DML - 更新用户记录
        if (mapper.update(user) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }

    /**
     * 在 redis 中，更新用户登录双令牌。
     *
     * @param accessToken  访问令牌
     * @param refreshToken 刷新令牌
     * @param userId       用户ID
     */
    private void updateTokens(String accessToken, String refreshToken, String userId) {

        // 删除该用户旧的 AccessToken
        String oldAccessToken = myRedis.get(ACCESS_TOKEN_MAP_PREFIX + userId);
        if (StrUtil.isNotBlank(oldAccessToken)) {
            myRedis.del(ACCESS_TOKEN_PREFIX + oldAccessToken);
        }

        // 删除该用户旧的 RefreshToken
        String oldRefreshToken = myRedis.get(REFRESH_TOKEN_MAP_PREFIX + userId);
        if (StrUtil.isNotBlank(oldRefreshToken)) {
            myRedis.del(REFRESH_TOKEN_PREFIX + oldRefreshToken);
        }

        // 存储该用户新的双令牌
        // key = 前缀 + accessToken，value = 用户主键
        // key = 前缀 + refreshToken，value = 用户主键
        myRedis.setEx(ACCESS_TOKEN_PREFIX + accessToken, userId, ACCESS_TOKEN_EXPIRE, TimeUnit.MINUTES);
        myRedis.setEx(REFRESH_TOKEN_PREFIX + refreshToken, userId, REFRESH_TOKEN_EXPIRE, TimeUnit.DAYS);

        // 更新该用户新的双令牌映射
        // key = 前缀 + 用户主键，value = accessToken
        // key = 前缀 + 用户主键，value = refreshToken
        myRedis.setEx(ACCESS_TOKEN_MAP_PREFIX + userId, accessToken, ACCESS_TOKEN_EXPIRE, TimeUnit.MINUTES);
        myRedis.setEx(REFRESH_TOKEN_MAP_PREFIX + userId, refreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.DAYS);
    }

}
