package com.example.instaclone.domain.post.repository;

import com.example.instaclone.domain.post.entity.Post;
import com.example.instaclone.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedateDesc();
    Page<Post> findByIdLessThanOrderByIdDesc(Long lastPostId, PageRequest pageRequest);
    Page<Post> findByUserOrderByCreatedateDesc(User user, Pageable pageable);

    List<Post> findAllByUser(User user);
}
