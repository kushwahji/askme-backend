package com.santosh.blog.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.santosh.blog.exception.AppException;
import com.santosh.blog.exception.Error;
import com.santosh.blog.profile.dto.ProfileDto;
import com.santosh.blog.profile.entity.FollowEntity;
import com.santosh.blog.profile.repository.FollowRepository;
import com.santosh.blog.user.dto.UserDto;
import com.santosh.blog.user.entity.UserEntity;
import com.santosh.blog.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Override
    public ProfileDto getProfile(String name, UserDto.Auth authUser) {
        UserEntity user = userRepository.findByName(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        Boolean following = followRepository.findByFolloweeIdAndFollowerId(user.getId(), authUser.getId()).isPresent();

        return convertToProfile(user, following);
    }

    @Transactional
    @Override
    public ProfileDto followUser(String name, UserDto.Auth authUser) {
        UserEntity followee = userRepository.findByName(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        UserEntity follower = UserEntity.builder().id(authUser.getId()).build(); // myself

        followRepository.findByFolloweeIdAndFollowerId(followee.getId(), follower.getId())
                .ifPresent(follow -> {throw new AppException(Error.ALREADY_FOLLOWED_USER);});

        FollowEntity follow =  FollowEntity.builder().followee(followee).follower(follower).build();
        followRepository.save(follow);

        return convertToProfile(followee, true);
    }

    @Transactional
    @Override
    public ProfileDto unfollowUser(String name, UserDto.Auth authUser) {
        UserEntity followee = userRepository.findByName(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        UserEntity follower = UserEntity.builder().id(authUser.getId()).build(); // myself

        FollowEntity follow = followRepository.findByFolloweeIdAndFollowerId(followee.getId(), follower.getId())
                .orElseThrow(() -> new AppException(Error.FOLLOW_NOT_FOUND));
        followRepository.delete(follow);

        return convertToProfile(followee, false);
    }

    private ProfileDto convertToProfile(UserEntity user, Boolean following) {
        return ProfileDto.builder()
                .name(user.getName())
                .bio(user.getBio())
                .image(user.getImage())
                .following(following)
                .build();
    }
}
