package com.santosh.blog.profile.entity;

import lombok.*;

import javax.persistence.*;

import com.santosh.blog.common.entity.BaseEntity;
import com.santosh.blog.user.entity.UserEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(name = "u_follow_followee_pair_must_be_unique", columnNames = {"followee", "follower"})
})
public class FollowEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "followee")
    private UserEntity followee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "follower")
    private UserEntity follower;
}
