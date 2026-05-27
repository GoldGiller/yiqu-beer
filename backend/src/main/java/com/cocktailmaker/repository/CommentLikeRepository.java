package com.cocktailmaker.repository;

import com.cocktailmaker.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    Long countByCommentId(Long commentId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByCommentId(Long commentId);
}
