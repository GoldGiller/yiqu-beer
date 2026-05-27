package com.cocktailmaker.repository;

import com.cocktailmaker.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<UserActivity> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
