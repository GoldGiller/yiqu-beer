package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.UserDto;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    ApiResponse<UserDto> register(UserDto userDto);
    
    /**
     * 用户登录
     */
    ApiResponse<String> login(String username, String password);
    
    /**
     * 用户登出
     */
    ApiResponse<Void> logout(String token);
    
    /**
     * 获取用户信息
     */
    ApiResponse<UserDto> getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    ApiResponse<UserDto> updateUserInfo(Long userId, UserDto userDto);
    
    /**
     * 修改密码
     */
    ApiResponse<Void> changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    ApiResponse<Void> resetPassword(String email, String verificationCode, String newPassword);
    
    /**
     * 发送验证码
     */
    ApiResponse<Void> sendVerificationCode(String email, String type);
    
    /**
     * 验证邮箱
     */
    ApiResponse<Void> verifyEmail(String email, String verificationCode);
    
    /**
     * 上传头像
     */
    ApiResponse<String> uploadAvatar(Long userId, byte[] avatarData);
    
    /**
     * 搜索用户
     */
    ApiResponse<Page<UserDto>> searchUsers(String keyword, Pageable pageable);
    
    /**
     * 删除用户
     */
    ApiResponse<Void> deleteUser(Long userId);
    
    /**
     * 更新用户状态
     */
    ApiResponse<Void> updateUserStatus(Long userId, UserStatus status);
    
    /**
     * 根据用户名获取用户
     */
    User findByUsername(String username);
    
    /**
     * 根据ID获取用户
     */
    User findById(Long userId);
    
    /**
     * 检查用户是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 验证密码
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * 编码密码
     */
    String encodePassword(String rawPassword);
}