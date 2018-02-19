package com.chriniko.rxjavaexample.persistence.core;

import com.chriniko.rxjavaexample.domain.Article;
import com.chriniko.rxjavaexample.persistence.exception.MethodExecutionException;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ArticlesDB {

    private static final String ARTICLES_MAP_NAME = "articles";


    private static final int CONCURRENCY = 8;
    private static final int DIR_SIZE = 16;
    private static final int LEVELS = 4;

    private static final String ARTICLES_DB = "articles";

    private static final String GUARDS_NOT_SATISFIED_ERROR_MESSAGE = "Guards not satisfied!";
    private static final String QUERY_EXECUTION_ERROR_MESSAGE = "Could not execute query.";

    private final int totalRecordsThreshold;

    public ArticlesDB() {
        totalRecordsThreshold = (int) Math.pow(CONCURRENCY * DIR_SIZE, LEVELS);
    }

    // --- START: queries ---

    public boolean insertIfNotExists(Article article) {

        return update(
                articlesMap -> {

                    Article _article = articlesMap.get(article.produceKey());
                    if (_article == null) {
                        articlesMap.put(article.produceKey(), article);
                        return true;
                    }
                    return false;

                },
                articlesMap -> articlesMap.size() == totalRecordsThreshold
        );
    }

    public Collection<Article> findAll() {
        return query(Collections.emptyList());
    }

    // --- END: queries ---


    // --- START: infrastructure ---

    // Note: Template pattern.
    private boolean update(Work work, WorkGuard... workGuards) {

        final DB db = openDb();
        final HTreeMap<String, Article> articlesMap = getArticlesMap(db);

        try {

            // guards
            if (workGuards != null && workGuards.length != 0) {
                boolean guardsNotSatisfied = Stream.of(workGuards)
                        .map(workGuard -> workGuard.protect(articlesMap))
                        .reduce(false, (acc, in) -> acc && in);

                if (guardsNotSatisfied) {
                    throw new MethodExecutionException(GUARDS_NOT_SATISFIED_ERROR_MESSAGE);
                }
            }

            // actual work
            boolean workPerformedSuccessfully = work.perform(articlesMap);
            db.commit();

            return workPerformedSuccessfully;

        } catch (Throwable error) {

            db.rollback();
            return false;

        } finally {

            articlesMap.close();
            db.close();

        }
    }

    // Note: Template pattern.
    private Collection<Article> query(Collection<Predicate<Article>> predicates) {
        final DB db = openDb();
        final HTreeMap<String, Article> articlesMap = getArticlesMap(db);

        try {

            if (!predicates.isEmpty()) {

                return articlesMap
                        .getEntries()
                        .stream()
                        .filter(
                                article -> predicates
                                        .stream()
                                        .map(predicate -> predicate.test(article.getValue()))
                                        .reduce(true, (acc, eval) -> acc && eval)
                        )
                        .collect(
                                () -> new ArrayList<>(articlesMap.size()),
                                (articles, article) -> articles.add(article.getValue()),
                                ArrayList::addAll
                        );

            } else {

                return articlesMap
                        .getEntries()
                        .stream()
                        .collect(
                                () -> new ArrayList<>(articlesMap.size()),
                                (articles, article) -> articles.add(article.getValue()),
                                ArrayList::addAll
                        );
            }

        } catch (Throwable error) {
            throw new MethodExecutionException(QUERY_EXECUTION_ERROR_MESSAGE, error);
        } finally {
            articlesMap.close();
            db.close();
        }

    }

    private DB openDb() {
        return DBMaker
                .fileDB(ARTICLES_DB)
                //
                .fileMmapEnableIfSupported() //use mmap instead of raf.
                .cleanerHackEnable()
                .fileMmapPreclearDisable()
                //
                .transactionEnable()
                //
                //.allocateStartSize(10L * 1024L * 1024L * 1024L)  // 10GB
                //.allocateIncrement(512L * 1024L * 1024L)       // 512MB
                //
                .closeOnJvmShutdown()
                .make();
    }

    @SuppressWarnings("unchecked")
    private HTreeMap<String, Article> getArticlesMap(DB db) {

        return db
                .hashMap(ARTICLES_MAP_NAME)
                .layout(CONCURRENCY, DIR_SIZE, LEVELS)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .counterEnable()
                .createOrOpen();
    }

    // --- END: infrastructure ---


    // --- START: core helpers ---
    @FunctionalInterface
    interface Work {
        boolean perform(HTreeMap<String, Article> articlesMap);
    }

    @FunctionalInterface
    interface WorkGuard {
        boolean protect(HTreeMap<String, Article> articlesMap);
    }
    // --- END: core helpers ---

}
