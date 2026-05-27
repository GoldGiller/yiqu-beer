package com.cocktailmaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户详情实体类
 */
@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Size(max = 100, message = "位置长度不能超过100个字符")
    private String location;

    @Size(max = 255, message = "网站链接长度不能超过255个字符")
    private String website;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "preference_sweetness", nullable = false)
    private Integer preferenceSweetness = 5;

    @Column(name = "preference_sourness", nullable = false)
    private Integer preferenceSourness = 5;

    @Column(name = "preference_alcohol", nullable = false)
    private Integer preferenceAlcohol = 5;

    @Column(name = "preference_fruitiness", nullable = false)
    private Integer preferenceFruitiness = 5;

    // Constructors
    public UserProfile() {
    }

    public UserProfile(User user) {
        this.user = user;
    }

}