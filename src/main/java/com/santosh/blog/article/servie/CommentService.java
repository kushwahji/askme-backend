package com.santosh.blog.article.servie;

import java.util.List;

import com.santosh.blog.article.dto.CommentDto;
import com.santosh.blog.user.dto.UserDto;

public interface CommentService {
    CommentDto addCommentsToAnArticle(final String slug, final CommentDto comment, final UserDto.Auth authUser);

    void delete(final String slug, final Long commentId, final UserDto.Auth authUser);

    List<CommentDto> getCommentsBySlug(final String slug, final UserDto.Auth authUser);
}
