package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

abstract class SimpleRequestMatcher extends RequestMatcher {

    private PatternMatcher matcher;

    public SimpleRequestMatcher(PatternMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public PatternMatch matches(Request request) {
        //Done to avoid getting value in case is slow
        if (matcher == PatternMatcher.any() || matcher == PatternMatcher.none()) {
            return matcher.matches("");
        }

        String value = getValue(request);
        PatternMatch match = matcher.matches(value);
        if (!match.isMatched()) {
            notifier().info(String.format(getMatchFailureNotification(), request.getUrl(), value));
        }
        return match;
    }

    protected abstract String getValue(Request request);

    protected abstract String getMatchFailureNotification();

}
