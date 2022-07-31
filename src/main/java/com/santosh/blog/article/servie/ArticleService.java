package com.santosh.blog.article.servie;

import java.util.List;

import com.santosh.blog.article.dto.ArticleDto;
import com.santosh.blog.article.model.ArticleQueryParam;
import com.santosh.blog.article.model.FeedParams;
import com.santosh.blog.user.dto.UserDto;
import com.santosh.blog.user.dto.UserDto.Auth;

public interface ArticleService {
    ArticleDto createArticle(final ArticleDto article, final UserDto.Auth authUser);

    ArticleDto getArticle(final String slug, final UserDto.Auth authUser);

    ArticleDto updateArticle(final String slug, final ArticleDto.Update article, final UserDto.Auth authUser);

    void deleteArticle(final String slug, final UserDto.Auth authUser);

    List<ArticleDto> feedArticles(final UserDto.Auth authUser, final FeedParams feedParams);

    ArticleDto favoriteArticle(final String slug, final UserDto.Auth authUser);

    ArticleDto unfavoriteArticle(final String slug, final UserDto.Auth authUser);

    List<ArticleDto> listArticle(final ArticleQueryParam articleQueryParam, final UserDto.Auth authUser);
}
