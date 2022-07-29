package com.santosh.blog.tag.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.santosh.blog.article.entity.ArticleEntity;
import com.santosh.blog.common.entity.BaseEntity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags")
public class ArticleTagRelationEntity extends BaseEntity {
    @Column(name = "tag", nullable = false)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article", nullable = false)
    private ArticleEntity article;
}
