package com.santosh.blog.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.santosh.blog.profile.entity.FollowEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    Optional<FollowEntity> findByFolloweeIdAndFollowerId(Long followeeId, Long followerId);

    List<FollowEntity> findByFollowerId(Long id);

    List<FollowEntity> findByFollowerIdAndFolloweeIdIn(Long id, List<Long> authorIds);
}
