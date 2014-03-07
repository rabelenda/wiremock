package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;

public class UrlMatcher extends RequestMatcher {

    private PatternMatcher matcher;

    public static UrlMatcher fromPatternAndUrl(String urlPattern, String url) {
        return new UrlMatcher(urlPattern, url);
    }

    private UrlMatcher(String urlPattern, String url) {
        matcher = getMatcher(urlPattern, url);
    }

    private static PatternMatcher getMatcher(String urlPattern, String url) {
        if (url != null && urlPattern != null) {
            throw new IllegalStateException("URL and URL pattern may not be set simultaneously");
        } else if (url != null) {
            return PatternMatcher.equalsTo(url);
        } else if (urlPattern != null) {
            return PatternMatcher.regex(urlPattern);
        } else {
            return PatternMatcher.any();
        }
    }

    @Override
    public PatternMatch matches(Request request) {
        return matcher.matches(request.getUrl());
    }

}
