package com.chriniko.rxjavaexample.kickstart.observer;

import com.chriniko.rxjavaexample.domain.Article;
import rx.Subscriber;

import java.util.function.Consumer;

public class NewsApiObserver extends Subscriber<Article> {

    private final Consumer<Article> onNextArticleHandler;

    public NewsApiObserver(Consumer<Article> onNextArticleHandler) {
        this.onNextArticleHandler = onNextArticleHandler;
    }

    @Override
    public void onCompleted() {
        System.out.println("[threadName = "
                + Thread.currentThread().getName()
                + "]NewsApiObserver#onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        System.err.println("[threadName = "
                + Thread.currentThread().getName()
                + "]NewsApiObserver#onError --- error = "
                + e);
    }

    @Override
    public void onNext(Article article) {
        System.out.println("[threadName = "
                + Thread.currentThread().getName()
                + "]NewsApiObserver#onNext --- article = "
                + article);

        onNextArticleHandler.accept(article);
    }
}
