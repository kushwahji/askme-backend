package com.santosh.blog.user.service;

import com.santosh.blog.user.dto.UserDto;

public interface UserService {
    UserDto registration(final UserDto.Registration registration);

    UserDto login(final UserDto.Login login);

    UserDto currentUser(final UserDto.Auth authUser);

    UserDto update(final UserDto.Update update, final UserDto.Auth authUser);
}
