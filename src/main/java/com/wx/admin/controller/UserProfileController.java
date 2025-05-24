package com.wx.admin.controller;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.PageResponse;
import com.wx.common.model.request.PasswordUpdateRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.UserProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // 在 Controller 类开头添加
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.setDisallowedFields("createTime"); // 禁止前端修改创建时间
//    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @PostMapping("/find")
    public PageResponse<UserProfileDO> findUsers(@RequestBody UserProfileRequest request) {
        QueryWrapper<UserProfileDO> wrapper = new QueryWrapper<>();
        // 按用户来源过滤（假设数据库字段名为 source）
        if (StringUtils.isNotBlank(request.getSource())) {
            wrapper.eq("source", request.getSource());
        }

        // 创建分页对象（注意：需要提前配置 MyBatisPlus 分页插件）
        Page<UserProfileDO> page = new Page<>(request.getPage(), request.getPageSize());
        Page<UserProfileDO> pageResult = userProfileService.page(page, wrapper);
        pageResult.getRecords().forEach(user -> user.setPassword(null));
        // 将 pageResult 转成 统一的响应体

        return ApiResponse.page(pageResult);
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @PostMapping("/findBy")
    public UserProfileDO getUserById(@RequestBody UserProfileRequest request) {
        return userProfileService.getUserBy(request);
    }

    private static final UUIDGenerator uuidGen = new UUIDGenerator();

    /**
     * 新增用户
     *
     * @param userProfile 用户信息
     * @return 是否成功
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> addUser(@RequestBody UserProfileDO userProfile) {
        userProfile.setCreateTime(new Date());
        userProfile.setPassword(userProfile.getPassword());
        boolean saved = userProfileService.save(userProfile);
        if (saved) {
            userProfile.setToken(uuidGen.next());
            userProfileService.updateById(userProfile);
        }
        return ApiResponse.success(saved);
    }

    /**
     * 修改用户信息
     *
     * @param userProfile 用户信息
     * @return 是否成功
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> updateUser(@RequestBody UserProfileDO userProfile) {
        try {
            // 参数基础校验
            if (userProfile.getId() == null) {
                return ApiResponse.fail(400, "用户ID不能为空");
            }
            if (StringUtils.isBlank(userProfile.getPhone())) {
                return ApiResponse.fail(400, "手机号不能为空");
            }
            if (!userProfile.getPhone().matches("^1[3-9]\\d{9}$")) {
                return ApiResponse.fail(400, "手机号格式不正确");
            }
            if (StringUtils.isBlank(userProfile.getSource())) {
                return ApiResponse.fail(400, "用户来源不能为空");
            }

            // 检查手机号重复性（排除自己）
            QueryWrapper<UserProfileDO> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", userProfile.getPhone())
                    .ne("id", userProfile.getId());
            if (userProfileService.count(wrapper) > 0) {
                return ApiResponse.fail(409, "手机号已被其他用户使用");
            }

            // 保留原始密码（如果未修改密码）
            UserProfileDO existingUser = userProfileService.getById(userProfile.getId());
            if (StringUtils.isBlank(userProfile.getPassword())) {
                userProfile.setPassword(existingUser.getPassword());
            } else {
                // 对新密码进行加密
                userProfile.setPassword(BCrypt.hashpw(userProfile.getPassword(), BCrypt.gensalt()));
            }

            // 设置更新时间并保存
            userProfile.setModifyTime(new Date());
            boolean result = userProfileService.updateById(userProfile);

            // 返回前清除敏感信息
            userProfile.setPassword(null);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "用户更新失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 根据 ID 删除用户
     *
     * @param id 用户 ID
     * @return 是否成功
     */
    @PostMapping("/del")
    public ApiResponse<Boolean> deleteUser(@RequestBody UserProfileRequest request) {
        try {
            // 参数基础校验
            if (request.getId() == null) {
                return ApiResponse.fail(400, "用户ID不能为空");
            }
            return ApiResponse.success(userProfileService.removeById(request.getId()));
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }

    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @PostMapping("/phone")
    public UserProfileDO getUserByPhone(@RequestBody UserProfileRequest request) {
        QueryWrapper<UserProfileDO> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", request.getPhone());
        return userProfileService.getOne(wrapper);
    }

    /**
     * 修改用户密码
     *
     * @param request 包含旧密码、新密码的请求体
     * @return 修改结果
     */
    @PostMapping("/updatePassword")
    public ApiResponse<Boolean> updatePassword(@RequestBody PasswordUpdateRequest request) {
        try {
            // 参数基础校验
            if (StringUtils.isBlank(request.getOldPassword())) {
                return ApiResponse.fail(400, "旧密码不能为空");
            }
            if (StringUtils.isBlank(request.getNewPassword())) {
                return ApiResponse.fail(400, "新密码不能为空");
            }
            if (request.getNewPassword().length() < 8) {
                return ApiResponse.fail(400, "新密码长度至少8位");
            }
            if (request.getNewPassword().equals(request.getOldPassword())) {
                return ApiResponse.fail(400, "新密码不能与旧密码相同");
            }
            UserProfileRequest userProfileRequest = new UserProfileRequest();
            userProfileRequest.setToken(request.getToken());
            // 通过token获取当前用户
            UserProfileDO currentUser = userProfileService.getUserBy(
                    userProfileRequest
            );
            if (currentUser == null) {
                return ApiResponse.fail(401, "用户未登录或token无效");
            }

            // 验证旧密码
            if (!BCrypt.checkpw(request.getOldPassword(), currentUser.getPassword())) {
                return ApiResponse.fail(403, "旧密码验证失败");
            }

            // 更新密码
            UserProfileDO updateUser = new UserProfileDO();
            updateUser.setId(currentUser.getId());
            updateUser.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
            updateUser.setLastPasswordUpdate(new Date());
            boolean result = userProfileService.updateById(updateUser);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "密码更新失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

}