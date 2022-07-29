package com.santosh.blog.article.model;

import lombok.*;

import javax.validation.constraints.AssertTrue;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedParams {
    protected Integer offset;
    protected Integer limit;
}
