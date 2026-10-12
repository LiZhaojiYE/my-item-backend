package com.lzj.service;

import com.lzj.dto.AccountLoginDTO;
import com.lzj.dto.UserInsertDTO;
import com.lzj.dto.UserUpdateDTO;
import com.lzj.dto.UserUpdatePasswordDTO;
import com.lzj.entity.User;
import com.lzj.vo.AccountLoginVO;
import com.mybatisflex.core.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户表 服务层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public interface UserService extends IService<User> {
    void insert(UserInsertDTO dto);
    AccountLoginVO loginByAccount(AccountLoginDTO dto);
    User select(Long id);
    void update(Long id, UserUpdateDTO dto);
    void updatePassword(Long id, UserUpdatePasswordDTO dto);
    String uploadAvatar(Long id, MultipartFile avatarFile);
    void delete(Long id);

}
