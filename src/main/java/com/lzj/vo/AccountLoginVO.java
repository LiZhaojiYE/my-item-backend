package com.lzj.vo;
import com.lzj.entity.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/** @author 李兆基 */
@Data
@Builder
public class AccountLoginVO implements Serializable {
    /** 用户信息 */
    private User user;
    /** 访问令牌 */
    private String accessToken;
    /** 刷新令牌 */
    private String refreshToken;
}
