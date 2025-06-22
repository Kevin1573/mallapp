package com.wx.miniapp.service;

import com.wx.common.model.ApiResponse;
import com.wx.common.utils.JsonUtil;
import com.wx.miniapp.config.WxMiniAppConfig;
import com.wx.miniapp.dto.AuthRegisterRequest;
import com.wx.miniapp.dto.WxUserInfo;
import com.wx.miniapp.entity.SessionInfo;
import com.wx.miniapp.util.WxMiniAppUtil;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MiniAppAuthService {

    @Autowired
    private WxMiniAppConfig wxMiniAppConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private SessionService sessionService;

    /**
     * 微信小程序登录处理
     * @param code 临时登录凭证
     * @param encryptedData 加密的用户数据
     * @param iv 加密算法的初始向量
     * @return 登录结果
     */
    public ApiResponse<?> login(String code, String encryptedData, String iv) {
        try {
            // 1. 校验参数
            if (!StringUtils.hasText(code)) {
                return ApiResponse.fail(400, "code不能为空");
            }

            log.info("code: {}", code);
            log.info("encryptedData: {}", encryptedData);
            log.info("wxMiniAppConfig.getAppSecret(): {}", wxMiniAppConfig.getAppSecret());
            log.info("wxMiniAppConfig.getAppId(): {}", wxMiniAppConfig.getAppId());
            // 2. 使用code换取session_key和openid
//            Map<String, String> authInfo = WxMiniAppUtil.code2Session(
//                    wxMiniAppConfig.getAppId(),
//                    wxMiniAppConfig.getAppSecret(),
//                    code);
            Map<String, String> authInfo = new HashMap<>();
            authInfo.put("openid", "oua2r7fL0g5w79eyUQ0qcYIR-wsQ");
            authInfo.put("session_key", "0UjGy1Hks9fjtr8TBSFTdA==");
            log.info("{}", JsonUtil.toStr(authInfo));
            if (!StringUtils.hasText(authInfo.get("openid"))) {
                return ApiResponse.fail(500, "获取openid失败");
            }

            String openid = authInfo.get("openid");
            String sessionKey = authInfo.get("session_key");

            // 3. 处理用户信息
            WxUserInfo userInfo = null;
            if (StringUtils.hasText(encryptedData) && StringUtils.hasText(iv)) {
                // 解密用户数据
                userInfo = WxMiniAppUtil.decryptUserInfo(
                        encryptedData,
                        sessionKey,
                        iv);
            }

            // 4. 创建或更新用户信息
//            Long userId = userService.createOrUpdateUser(openid, userInfo);

            UserProfileDO userByOpenId = userProfileService.getUserByOpenId(openid);

            // 5. 生成自定义登录态
            SessionInfo session = sessionService.createSession(openid, sessionKey, userByOpenId == null ? null : userByOpenId.getToken());

            Long userId = userProfileService.createOrUpdateUser(openid,session.getToken(), userInfo);

            // 6. 返回登录结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", session.getToken());
            result.put("userId", userId);

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail(500, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 检查登录状态
     * @param token 用户token
     * @return 检查结果
     */
    public ApiResponse<?> checkSession(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return ApiResponse.fail(400, "token不能为空");
            }

            boolean isValid = sessionService.validateSession(token);
            if (!isValid) {
                return ApiResponse.fail(403, "登录已过期");
            }

            return ApiResponse.success("登录有效");
        } catch (Exception e) {
            return ApiResponse.fail(500, "检查登录状态失败");
        }
    }

    public ApiResponse<?> register(AuthRegisterRequest request) {
        String token = request.getToken();
        // 1. 根据token查询openid
        UserProfileDO userProfileDO = userProfileService.getUserByToken(token);
        // 2. 根据code查询用户信息

        return null;
    }
}
