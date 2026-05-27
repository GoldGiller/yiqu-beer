package com.cocktailmaker.dto;

import com.cocktailmaker.enums.UserRole;
import com.cocktailmaker.enums.UserStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据传输对象
 */
@Data
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private UserProfileDto profile;
    private List<String> roles;
    
    // Constructors
    public UserDto() {
    }
    
    public UserDto(Long id, String username, String email, String nickname, 
                   String avatar, UserRole role, UserStatus status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
        this.status = status;
    }
    


    /**
     * 用户详情DTO
     */
    public static class UserProfileDto {
        private String bio;
        private String location;
        private String website;
        private LocalDate birthDate;
        private Integer preferenceSweetness;
        private Integer preferenceSourness;
        private Integer preferenceAlcohol;
        private Integer preferenceFruitiness;
        

    }
}