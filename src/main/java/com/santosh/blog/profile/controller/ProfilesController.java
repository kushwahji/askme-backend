package com.santosh.blog.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.santosh.blog.profile.dto.ProfileDto;
import com.santosh.blog.profile.service.ProfileService;
import com.santosh.blog.user.dto.UserDto;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {
    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ProfileDto getProfile(@PathVariable("username") String name, @AuthenticationPrincipal UserDto.Auth authUser) {
        return profileService.getProfile(name, authUser);
    }

    @PostMapping("/{username}/follow")
    public ProfileDto followUser(@PathVariable("username") String name, @AuthenticationPrincipal UserDto.Auth authUser) {
        return profileService.followUser(name, authUser);
    }

    @DeleteMapping("/{username}/follow")
    public ProfileDto unfollowUser(@PathVariable("username") String name, @AuthenticationPrincipal UserDto.Auth authUser) {
        return profileService.unfollowUser(name, authUser);
    }
}
