package com.santosh.blog.tag.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.blog.tag.dto.TagDto;
import com.santosh.blog.tag.service.TagService;
@CrossOrigin
@RestController
@RequestMapping("/tags")
public class TagController {
	@Autowired
    private TagService tagService;

    @GetMapping
    public TagDto.TagList listOfTags() {
        return TagDto.TagList.builder().tags(tagService.listOfTags()).build();
    }
}
