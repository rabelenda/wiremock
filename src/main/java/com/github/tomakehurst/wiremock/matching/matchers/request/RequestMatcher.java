package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;

public abstract class RequestMatcher {

    public abstract PatternMatch matches(Request request);

    public RequestMatcher and(RequestMatcher that) {
        return new AndRequestMatcher(this, that);
    }


}
