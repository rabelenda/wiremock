package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;

public class AndRequestMatcher extends RequestMatcher {

    private final RequestMatcher m1;
    private final RequestMatcher m2;

    AndRequestMatcher(RequestMatcher m1, RequestMatcher m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public PatternMatch matches(Request request) {
        PatternMatch match = m1.matches(request);
        //done for short circuit
        if (match.isMatched()) {
            return match.and(m2.matches(request));
        } else {
            return match;
        }
    }

}
