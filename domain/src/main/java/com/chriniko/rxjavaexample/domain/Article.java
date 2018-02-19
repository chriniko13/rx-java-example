package com.chriniko.rxjavaexample.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Article implements Serializable {

    private Source source;

    private String author;

    private String title;

    private String description;

    private String url;

    private String urlToImage;

    private String publishedAt;

    public String produceKey() {

        String sourceName = getSource().getName();
        String title = getTitle();

        return publishedAt + " | " + sourceName + " -> " + title;
    }

    public static String getPublishedAt(String producedKey) {
        return producedKey.split(" -> ")[0].split(" \\| ")[0];
    }

    public static String getSourceName(String producedKey) {
        return producedKey.split(" -> ")[0].split(" \\| ")[1];
    }
}
