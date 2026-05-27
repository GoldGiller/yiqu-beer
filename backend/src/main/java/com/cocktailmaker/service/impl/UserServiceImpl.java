package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.UserDto;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.enums.UserStatus;
import com.cocktailmaker.repository.UserRepository;
import com.cocktailmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ApiResponse<UserDto> register(UserDto userDto) {
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(userDto.getUsername())) {
                return ApiResponse.error("用户名已存在");
            }

            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(userDto.getEmail())) {
                return ApiResponse.error("邮箱已被注册");
            }

            // 创建新用户
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setNickname(userDto.getNickname() != null ? userDto.getNickname() : userDto.getUsername());
            user.setStatus(UserStatus.ACTIVE);

            User savedUser = userRepository.save(user);

            // 转换为DTO并返回
            UserDto resultDto = convertToDto(savedUser);
            return ApiResponse.success("注册成功", resultDto);
        } catch (Exception e) {
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> login(String username, String password) {
        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(username, username);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }

            User user = userOpt.get();

            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ApiResponse.error("密码错误");
            }

            // 更新最后登录时间
            user.setLastLoginAt(java.time.LocalDateTime.now());
            userRepository.save(user);

            // 生成token (这里简化处理，实际应该生成JWT)
            String token = "fake-jwt-token-for-" + user.getId();
            return ApiResponse.success("登录成功", token);
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> logout(String token) {
        // 简化处理，实际应该将token加入黑名单
        return ApiResponse.success("登出成功", null);
    }

    @Override
    public ApiResponse<UserDto> getUserInfo(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }

            UserDto userDto = convertToDto(userOpt.get());
            return ApiResponse.success(userDto);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<UserDto> updateUserInfo(Long userId, UserDto userDto) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> resetPassword(String email, String verificationCode, String newPassword) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> sendVerificationCode(String email, String type) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> verifyEmail(String email, String verificationCode) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<String> uploadAvatar(Long userId, byte[] avatarData) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<UserDto>> searchUsers(String keyword, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> deleteUser(Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> updateUserStatus(Long userId, UserStatus status) {
        return ApiResponse.error("未实现");
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 将User实体转换为UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}