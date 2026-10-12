package com.lzj.interceptor;

import cn.hutool.core.util.StrUtil;
import com.lzj.component.MyRedis;
import com.lzj.exception.ServiceException;
import com.lzj.result.ResultCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** @author 李兆基 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Value("${myitem.token.access-token-prefix}")
    private String ACCESS_TOKEN_PREFIX;
    @Value("${myitem.token.refresh-token-prefix}")
    private String REFRESH_TOKEN_PREFIX;
    @Resource
    private MyRedis myRedis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 统一响应格式
        response.setContentType("application/json;charset=utf-8");

        // 放行非控制器请求
        if (!(handler instanceof HandlerMethod)) {
            log.info("✅【TokenInterceptor】非控制器请求，直接放行");
            return true;
        }

        // 获取 X-AccessToken
        String accessToken = this.getAccessToken(request);
        log.info("✅【TokenInterceptor】获取 X-AccessToken: {}", accessToken);

        // 获取 X-RefreshToken
        String refreshToken = this.getRefreshToken(request);
        log.info("✅【TokenInterceptor】获取 X-RefreshToken: {}", refreshToken);

        // 校验 X-AccessToken 是否有效
        String userIdInAccessToken = myRedis.get(ACCESS_TOKEN_PREFIX + accessToken);
        // userId 解析成功 → 证明 X-AccessToken 有效
        if (StrUtil.isNotBlank(userIdInAccessToken)) {
            log.info("✅【TokenInterceptor】X-AccessToken 检查通过，userId: {}", userIdInAccessToken);
            // 将 userId 放入请求域中，方便后续使用
            request.setAttribute("userId", userIdInAccessToken);
            log.info("✅【TokenInterceptor】将 userId 放入请求域，并放行请求");
            return true;
        }
        log.info("✅【TokenInterceptor】X-AccessToken 检查失败，开始检查 X-RefreshToken");

        // 校验 X-RefreshToken 是否有效
        String userIdInRefreshToken = this.checkRefreshToken(refreshToken);
        log.info("✅【TokenInterceptor】X-RefreshToken 检查通过，开始续期 X-AccessToken");

        // 续期 X-AccessToken
        String newAccessToken = this.newAccessToken(userIdInRefreshToken);
        log.info("✅【TokenInterceptor】X-AccessToken 续期完成");

        // 返回新 X-AccessToken 给前端（X-RefreshToken 保持不变）
        response.getWriter().print(Map.of(
                "code", 6001,
                "message", "AccessToken续期成功",
                "accessToken", newAccessToken,
                "refreshToken", refreshToken
        ));

        return false;
    }

    /**
     * 获取请求头中的 X-AccessToken
     *
     * @param request HttpServletRequest
     * @return X-AccessToken 值，不存在则抛出异常
     */
    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("X-AccessToken");
        if (StrUtil.isBlank(accessToken)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED, "X-AccessToken 缺失");
        }
        return accessToken;
    }

    /**
     * 获取请求头中的 X-RefreshToken
     *
     * @param request HttpServletRequest
     * @return X-RefreshToken 值，不存在则抛出异常
     */
    private String getRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("X-RefreshToken");
        if (StrUtil.isBlank(refreshToken)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED, "X-RefreshToken 缺失");
        }
        return refreshToken;
    }

    /**
     * 校验 X-RefreshToken 是否有效
     *
     * @param refreshToken X-RefreshToken 值
     * @return 用户 ID
     */
    private String checkRefreshToken(String refreshToken) {
        // 校验 X-RefreshToken 是否有效
        String userId = myRedis.get(REFRESH_TOKEN_PREFIX + refreshToken);
        // userId 为空 → 证明 X-RefreshToken 无效
        if (StrUtil.isBlank(userId)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED, "X-RefreshToken 无效");
        }
        return userId;
    }

    /**
     * 生成新的 X-AccessToken
     *
     * @param userIdInRefreshToken 从 X-RefreshToken 中解析出来的用户 ID
     * @return 新的 X-AccessToken 值
     */
    private String newAccessToken(String userIdInRefreshToken) {
        
        // 生成新的 X-AccessToken
        String newAccessToken = UUID.randomUUID().toString();
        log.info("✅【TokenInterceptor】生成新的 X-AccessToken: {}", newAccessToken);
        
        // 将新 X-AccessToken 存入 redis，过期时间为 30 分钟
        myRedis.setEx(ACCESS_TOKEN_PREFIX + newAccessToken, userIdInRefreshToken, 30, TimeUnit.MINUTES);
        log.info("✅【TokenInterceptor】将新 X-AccessToken 存入 redis，过期时间为 30 分钟");
        return newAccessToken;
    }
}
