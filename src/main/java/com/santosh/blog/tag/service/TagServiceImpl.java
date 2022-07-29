package com.santosh.blog.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.santosh.blog.tag.entity.ArticleTagRelationEntity;
import com.santosh.blog.tag.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<String> listOfTags() {
        return tagRepository.findAll().stream().map(ArticleTagRelationEntity::getTag).distinct().collect(Collectors.toList());
    }
}
