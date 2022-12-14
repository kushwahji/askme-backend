package com.santosh.blog.article.servie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.santosh.blog.article.dto.ArticleDto;
import com.santosh.blog.article.entity.ArticleEntity;
import com.santosh.blog.article.entity.FavoriteEntity;
import com.santosh.blog.article.model.ArticleQueryParam;
import com.santosh.blog.article.model.FeedParams;
import com.santosh.blog.article.repository.ArticleRepository;
import com.santosh.blog.article.repository.FavoriteRepository;
import com.santosh.blog.common.entity.BaseEntity;
import com.santosh.blog.exception.AppException;
import com.santosh.blog.exception.Error;
import com.santosh.blog.profile.entity.FollowEntity;
import com.santosh.blog.profile.repository.FollowRepository;
import com.santosh.blog.profile.service.ProfileService;
import com.santosh.blog.tag.entity.ArticleTagRelationEntity;
import com.santosh.blog.user.dto.UserDto;
import com.santosh.blog.user.dto.UserDto.Auth;
import com.santosh.blog.user.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private ProfileService profileService;

	@Transactional
	@Override
	public ArticleDto createArticle(ArticleDto article, UserDto.Auth authUser) {
		String slug = String.join("-", article.getTitle().split(" "));
		UserEntity author = UserEntity.builder().id(authUser.getId()).name(authUser.getName()).bio(authUser.getBio())
				.image(authUser.getImage()).build();

		ArticleEntity articleEntity = ArticleEntity.builder().slug(slug).title(article.getTitle())
				.description(article.getDescription()).body(article.getBody()).author(author).build();
		List<ArticleTagRelationEntity> tagList = new ArrayList<>();
		for (String tag : article.getTagList()) {
			tagList.add(ArticleTagRelationEntity.builder().article(articleEntity).tag(tag).build());
		}
		articleEntity.setTagList(tagList);
		articleEntity = articleRepository.save(articleEntity);
		return convertEntityToDto(articleEntity, false, 0L, false);
	}

	@Override
	public ArticleDto getArticle(String slug, UserDto.Auth authUser) {
		ArticleEntity found = articleRepository.findBySlug(slug)
				.orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
		Boolean following = profileService.getProfile(found.getAuthor().getName(), authUser).getFollowing();
		List<FavoriteEntity> favorites = found.getFavoriteList();
		Boolean favorited = favorites.stream()
				.anyMatch(favoriteEntity -> favoriteEntity.getUser().getId().equals(authUser.getId()));
		int favoriteCount = favorites.size();
		return convertEntityToDto(found, favorited, (long) favoriteCount, following);
	}

	private ArticleDto convertEntityToDto(ArticleEntity entity, Boolean favorited, Long favoritesCount,
			Boolean following) {
		return ArticleDto.builder().slug(entity.getSlug()).title(entity.getTitle()).description(entity.getDescription())
				.body(entity.getBody())
				.author(ArticleDto.Author.builder().name(entity.getAuthor().getName()).bio(entity.getAuthor().getBio())
						.image(entity.getAuthor().getImage()).following(following).build())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).favorited(favorited)
				.favoritesCount(favoritesCount)
				.tagList(
						entity.getTagList().stream().map(ArticleTagRelationEntity::getTag).collect(Collectors.toList()))
				.build();
	}

	@Transactional
	@Override
	public ArticleDto updateArticle(String slug, ArticleDto.Update article, UserDto.Auth authUser) {
		ArticleEntity found = articleRepository.findBySlug(slug)
				.filter(entity -> entity.getAuthor().getId().equals(authUser.getId()))
				.orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

		if (article.getTitle() != null) {
			String newSlug = String.join("-", article.getTitle().split(" "));
			found.setTitle(article.getTitle());
			found.setSlug(newSlug);
		}

		if (article.getDescription() != null) {
			found.setDescription(article.getDescription());
		}

		if (article.getBody() != null) {
			found.setBody(article.getBody());
		}

		articleRepository.save(found);

		return getArticle(slug, authUser);
	}

	@Transactional
	@Override
	public void deleteArticle(String slug, UserDto.Auth authUser) {
		ArticleEntity found = articleRepository.findBySlug(slug)
				.filter(entity -> entity.getAuthor().getId().equals(authUser.getId()))
				.orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
		articleRepository.delete(found);
	}

	@Override
	public List<ArticleDto> feedArticles(UserDto.Auth authUser, FeedParams feedParams) {
		List<Long> feedAuthorIds = followRepository.findByFollowerId(authUser.getId()).stream()
				.map(FollowEntity::getFollowee).map(BaseEntity::getId).collect(Collectors.toList());
		return articleRepository.findByAuthorIdInOrderByCreatedAtDesc(feedAuthorIds,
				PageRequest.of(feedParams.getOffset(), feedParams.getLimit())).stream().map(entity -> {
					List<FavoriteEntity> favorites = entity.getFavoriteList();
					Boolean favorited = favorites.stream()
							.anyMatch(favoriteEntity -> favoriteEntity.getUser().getId().equals(authUser.getId()));
					int favoriteCount = favorites.size();
					return convertEntityToDto(entity, favorited, (long) favoriteCount, true);
				}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public ArticleDto favoriteArticle(String slug, UserDto.Auth authUser) {
		ArticleEntity found = articleRepository.findBySlug(slug)
				.orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

		favoriteRepository.findByArticleIdAndUserId(found.getId(), authUser.getId()).ifPresent(favoriteEntity -> {
			throw new AppException(Error.ALREADY_FAVORITED_ARTICLE);
		});

		FavoriteEntity favorite = FavoriteEntity.builder().article(found)
				.user(UserEntity.builder().id(authUser.getId()).build()).build();
		favoriteRepository.save(favorite);

		return getArticle(slug, authUser);
	}

	@Transactional
	@Override
	public ArticleDto unfavoriteArticle(String slug, UserDto.Auth authUser) {
		ArticleEntity found = articleRepository.findBySlug(slug)
				.orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
		FavoriteEntity favorite = found.getFavoriteList().stream()
				.filter(favoriteEntity -> favoriteEntity.getArticle().getId().equals(found.getId())
						&& favoriteEntity.getUser().getId().equals(authUser.getId()))
				.findAny().orElseThrow(() -> new AppException(Error.FAVORITE_NOT_FOUND));
		found.getFavoriteList().remove(favorite); // cascade REMOVE
		return getArticle(slug, authUser);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArticleDto> listArticle(ArticleQueryParam articleQueryParam, UserDto.Auth authUser) {
		Pageable pageable = null;
		if (articleQueryParam.getOffset() != null) {
			pageable = PageRequest.of(articleQueryParam.getOffset(), articleQueryParam.getLimit());
		}

		List<ArticleEntity> articleEntities;
		if (articleQueryParam.getTag() != null) {
			articleEntities = articleRepository.findByTag(articleQueryParam.getTag(), pageable);
		} else if (articleQueryParam.getAuthor() != null) {
			articleEntities = articleRepository.findByAuthorName(articleQueryParam.getAuthor(), pageable);
		} else if (articleQueryParam.getFavorited() != null) {
			articleEntities = articleRepository.findByFavoritedUsername(articleQueryParam.getFavorited(), pageable);
		} else {
			articleEntities = articleRepository.findListByPaging(pageable);
		}

		return convertToArticleList(articleEntities, authUser);
	}

	private List<ArticleDto> convertToArticleList(List<ArticleEntity> articleEntities, UserDto.Auth authUser) {

		List<Long> authorIds = articleEntities.stream().map(ArticleEntity::getAuthor).map(BaseEntity::getId)
				.collect(Collectors.toList());

		if (authUser == null) {
			List<Long> followeeIds = followRepository.findByFollowerIdAndFolloweeIdIn(0L, authorIds).stream()
					.map(FollowEntity::getFollowee).map(BaseEntity::getId).collect(Collectors.toList());
			return articleEntities.stream().map(entity -> {
				List<FavoriteEntity> favorites = entity.getFavoriteList();
				Boolean favorited = favorites.stream()
						.anyMatch(favoriteEntity -> favoriteEntity.getUser().getId().equals(0L));
				int favoriteCount = favorites.size();
				Boolean following = followeeIds.stream()
						.anyMatch(followeeId -> followeeId.equals(entity.getAuthor().getId()));
				return convertEntityToDto(entity, favorited, (long) favoriteCount, following);
			}).collect(Collectors.toList());
		}
		List<Long> followeeIds = followRepository.findByFollowerIdAndFolloweeIdIn(authUser.getId(), authorIds).stream()
				.map(FollowEntity::getFollowee).map(BaseEntity::getId).collect(Collectors.toList());

		return articleEntities.stream().map(entity -> {
			List<FavoriteEntity> favorites = entity.getFavoriteList();
			Boolean favorited = favorites.stream()
					.anyMatch(favoriteEntity -> favoriteEntity.getUser().getId().equals(authUser.getId()));
			int favoriteCount = favorites.size();
			Boolean following = followeeIds.stream()
					.anyMatch(followeeId -> followeeId.equals(entity.getAuthor().getId()));
			return convertEntityToDto(entity, favorited, (long) favoriteCount, following);
		}).collect(Collectors.toList());
	}
}
