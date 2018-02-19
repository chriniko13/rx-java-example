package com.chriniko.rxjavaexample.domain;

import lombok.Data;

import java.util.Collection;

@Data
public class NewsApiResponse {

    private String status;

    private int totalResults;

    private Collection<Article> articles;

}
