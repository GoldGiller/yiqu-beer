package com.cocktailmaker.repository;

import com.cocktailmaker.entity.User;
import com.cocktailmaker.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(String username);

    /**
    * 检查邮箱是否存在
    */
    Boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 根据用户名模糊搜索用户
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
    Page<User> searchByUsernameOrNickname(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 统计活跃用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") UserStatus status);

    /**
     * 查找最近注册的用户
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(Pageable pageable);
}