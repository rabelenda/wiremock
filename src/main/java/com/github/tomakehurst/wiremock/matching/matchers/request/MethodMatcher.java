package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.PatternMatch;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static com.github.tomakehurst.wiremock.http.RequestMethod.ANY;

public class MethodMatcher extends RequestMatcher {

    private final RequestMethod method;

    public MethodMatcher(RequestMethod method) {
        this.method = method;
    }

    @Override
    public PatternMatch matches(Request request) {
        boolean matched = method == null || method == ANY || request.getMethod() == method;
        if (!matched) {
            notifier().info(String.format("URL %s is match, but method %s is not", request.getUrl(), request.getMethod()));
        }
        return PatternMatch.fromMatched(matched);
    }

}
