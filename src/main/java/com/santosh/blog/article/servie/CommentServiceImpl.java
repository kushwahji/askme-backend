package com.santosh.blog.article.servie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.santosh.blog.article.dto.ArticleDto;
import com.santosh.blog.article.dto.CommentDto;
import com.santosh.blog.article.entity.ArticleEntity;
import com.santosh.blog.article.entity.CommentEntity;
import com.santosh.blog.article.repository.ArticleRepository;
import com.santosh.blog.article.repository.CommentRepository;
import com.santosh.blog.common.entity.BaseEntity;
import com.santosh.blog.exception.AppException;
import com.santosh.blog.exception.Error;
import com.santosh.blog.profile.service.ProfileService;
import com.santosh.blog.user.dto.UserDto;
import com.santosh.blog.user.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ProfileService profileService;

    @Transactional
    @Override
    public CommentDto addCommentsToAnArticle(String slug, CommentDto comment, UserDto.Auth authUser) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug).orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        CommentEntity commentEntity = CommentEntity.builder()
                .body(comment.getBody())
                .author(UserEntity.builder()
                        .id(authUser.getId())
                        .name(authUser.getName())
                        .bio(authUser.getBio())
                        .image(authUser.getImage())
                        .build())
                .article(articleEntity)
                .build();
        commentRepository.save(commentEntity);

        return CommentDto.builder()
                .id(commentEntity.getId())
                .createdAt(commentEntity.getCreatedAt())
                .updatedAt(commentEntity.getUpdatedAt())
                .body(commentEntity.getBody())
                .author(ArticleDto.Author.builder()
                        .name(commentEntity.getAuthor().getName())
                        .bio(commentEntity.getAuthor().getBio())
                        .image(commentEntity.getArticle().getAuthor().getImage())
                        .following(false)
                        .build())
                .build();
    }

    @Transactional
    @Override
    public void delete(String slug, Long commentId, UserDto.Auth authUser) {
        Long articleId = articleRepository.findBySlug(slug).map(BaseEntity::getId).orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .filter(comment -> comment.getArticle().getId().equals(articleId))
                .orElseThrow(() -> new AppException(Error.COMMENT_NOT_FOUND));

        commentRepository.delete(commentEntity);
    }

    @Override
    public List<CommentDto> getCommentsBySlug(String slug, UserDto.Auth authUser) {
        Long articleId = articleRepository.findBySlug(slug).map(BaseEntity::getId).orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

        List<CommentEntity> commentEntities = commentRepository.findByArticleId(articleId);
        return commentEntities.stream().map(commentEntity -> {
            Boolean following = profileService.getProfile(commentEntity.getAuthor().getName(), authUser).getFollowing();
            return CommentDto.builder()
                    .id(commentEntity.getId())
                    .createdAt(commentEntity.getCreatedAt())
                    .updatedAt(commentEntity.getUpdatedAt())
                    .body(commentEntity.getBody())
                    .author(ArticleDto.Author.builder()
                            .name(commentEntity.getAuthor().getName())
                            .bio(commentEntity.getAuthor().getBio())
                            .image(commentEntity.getArticle().getAuthor().getImage())
                            .following(following)
                            .build())
                    .build();
        }).collect(Collectors.toList());
    }
}
