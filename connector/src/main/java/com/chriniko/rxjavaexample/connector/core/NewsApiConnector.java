package com.chriniko.rxjavaexample.connector.core;

import com.chriniko.rxjavaexample.connector.exception.NewsApiConnectionException;
import com.chriniko.rxjavaexample.domain.NewsApiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * This class consumes the endpoints provided by:
 * <link>https://newsapi.org/docs/endpoints</link>
 */
public class NewsApiConnector {

    private static final String COULD_NOT_CONNECT_TO_NEWS_API_ERROR_MESSAGE = "Could not connect to NewsApi";

    private static final String X_API_KEY_VALUE = "357bce065f39408495ba156b71aedb42";
    private static final String X_API_KEY_LABEL = "X-Api-Key";

    private RestTemplate restTemplate;

    public NewsApiConnector() {
        restTemplate = new RestTemplate();
    }

    //TODO enrich with parameters in order to consume all the available info that this endpoint provides to us.
    public NewsApiResponse getTopHeadlines(Country country,
                                           Category category,
                                           List<String> q /* Note: Keywords or a phrase to search for. */,
                                           int pageSize /* Note: 20 is the default, 100 is the maximum. */) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(X_API_KEY_LABEL, X_API_KEY_VALUE);

            HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<NewsApiResponse> result
                    = restTemplate.exchange(
                    "https://newsapi.org/v2/top-headlines?"
                            + "country=" + Country.getValue(country)
                            + "&category=" + Category.getValue(category)
                            + "&pageSize=" + pageSize,
                    HttpMethod.GET,
                    httpEntity,
                    NewsApiResponse.class);


            return result.getBody();
        } catch (RestClientException error) {
            throw new NewsApiConnectionException(COULD_NOT_CONNECT_TO_NEWS_API_ERROR_MESSAGE, error);
        }
    }

}
