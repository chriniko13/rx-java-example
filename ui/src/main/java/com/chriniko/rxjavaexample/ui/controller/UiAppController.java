package com.chriniko.rxjavaexample.ui.controller;

import com.chriniko.rxjavaexample.domain.Article;
import com.chriniko.rxjavaexample.kickstart.observable.NewsApiObservable;
import com.chriniko.rxjavaexample.kickstart.observer.NewsApiObserver;
import com.chriniko.rxjavaexample.persistence.core.ArticlesDB;
import com.chriniko.rxjavaexample.ui.core.UiUpdater;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UiAppController implements UiUpdater {

    private Observable<Article> articleObservable;

    private NewsApiObserver newsApiObserver;

    private ArticlesDB articlesDB;

    @FXML
    private ListView<String> articlesList;

    @FXML
    public void initialize() {

        // create the db...
        articlesDB = new ArticlesDB();

        // load stored data if exist...
        Collection<Article> storedArticles = articlesDB.findAll();
        if (storedArticles != null && !storedArticles.isEmpty()) {

            articlesList.setItems(storedArticles
                    .stream()
                    .map(Article::produceKey)
                    .collect(
                            Collectors.toCollection(
                                    () -> FXCollections.observableList(new ArrayList<>(storedArticles.size()))
                            )
                    )
            );
        }

        // create observable and observer...
        articleObservable = NewsApiObservable.get();

        final Consumer<Article> articleConsumer = article -> {

            boolean inserted = articlesDB.insertIfNotExists(article);
            if (inserted) {
                update(() -> articlesList.getItems().add(article.produceKey()));
            }

        };
        newsApiObserver = new NewsApiObserver(articleConsumer);

        // create subscription...
        Subscription newsApiSubscription = articleObservable
                .subscribeOn(Schedulers.io()) // Note: background thread - observable
                .observeOn(Schedulers.computation()) // Note: ui thread - observer(s)
                .subscribe(newsApiObserver);

        System.out.println("newsApiSubscription.isUnsubscribed ? " + newsApiSubscription.isUnsubscribed());
    }

    public void sortArticles() {

        // Note: sort by published date descending and then by source name ascending.
        articlesList
                .getItems()
                .sort(
                        Comparator
                                .comparing(
                                        Function.identity(),
                                        (o1, o2) -> {
                                            String publishedAt1 = Article.getPublishedAt((String) o1);
                                            String publishedAt2 = Article.getPublishedAt((String) o2);
                                            return ZonedDateTime
                                                    .parse(publishedAt1)
                                                    .compareTo(ZonedDateTime.parse(publishedAt2));
                                        })
                                .reversed()
                                .thenComparing(
                                        (o1, o2) -> {
                                            String sourceName1 = Article.getSourceName((String) o1);
                                            String sourceName2 = Article.getSourceName((String) o2);
                                            return sourceName1.compareTo(sourceName2);
                                        })
                );

    }


}
