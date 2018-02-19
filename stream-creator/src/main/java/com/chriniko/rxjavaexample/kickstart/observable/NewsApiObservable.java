package com.chriniko.rxjavaexample.kickstart.observable;

import com.chriniko.rxjavaexample.connector.core.Category;
import com.chriniko.rxjavaexample.connector.core.Country;
import com.chriniko.rxjavaexample.connector.core.NewsApiConnector;
import com.chriniko.rxjavaexample.domain.Article;
import com.chriniko.rxjavaexample.domain.NewsApiResponse;
import rx.Emitter;
import rx.Observable;
import rx.Subscriber;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/*
    Note: this is a cold + infinite observable.
 */
public class NewsApiObservable {

    public static Observable<Article> get() {

        return Observable
                .<Article>create(
                        emitter -> {

                            final NewsApiConnector newsApiConnector = new NewsApiConnector();

                            Observable
                                    .interval(30, TimeUnit.SECONDS)
                                    .subscribe(new Subscriber<Long>() {

                                        @Override
                                        public void onCompleted() {
                                            emitter.onCompleted();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            emitter.onError(e);
                                        }

                                        @Override
                                        public void onNext(Long idx) {
                                            System.out.println("\n [threadName = "
                                                    + Thread.currentThread().getName()
                                                    + "]iteration: "
                                                    + idx
                                                    + "\n");

                                            final NewsApiResponse newsApiResponse = newsApiConnector.getTopHeadlines(Country.GR,
                                                    Category.GENERAL,
                                                    Collections.emptyList(),
                                                    100);

                                            newsApiResponse
                                                    .getArticles()
                                                    .forEach(emitter::onNext);
                                        }

                                    });
                        },
                        Emitter.BackpressureMode.ERROR
                )
                //.retry(5)
                .onErrorResumeNext(error -> {

                    System.out.println("\n [threadName = "
                            + Thread.currentThread().getName()
                            + "] --- error: "
                            + error.toString()
                            + "\n");

                    return NewsApiObservable.get();

                });
    }
}
