package com.santosh.blog.profile.service;

import com.santosh.blog.profile.dto.ProfileDto;
import com.santosh.blog.user.dto.UserDto;

public interface ProfileService {
    ProfileDto getProfile(final String username, final UserDto.Auth authUser);

    ProfileDto followUser(final String name, final UserDto.Auth authUser);

    ProfileDto unfollowUser(final String name, final UserDto.Auth authUser);
}
