package com.santosh.blog.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.santosh.blog.tag.entity.ArticleTagRelationEntity;

@Repository
public interface TagRepository extends JpaRepository<ArticleTagRelationEntity, Long> {
}
