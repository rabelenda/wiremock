package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.ValuePattern;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;

import java.util.List;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

public class BodyMatcher extends RequestMatcher {

    private PatternMatcher matcher;

    public BodyMatcher(List<ValuePattern> bodyPatterns) {
        matcher = getMatcher(bodyPatterns);
    }

    private static PatternMatcher getMatcher(List<ValuePattern> bodyPatterns) {
        PatternMatcher matcher = PatternMatcher.any();
        if (bodyPatterns != null) {
            for (ValuePattern bodyPattern : bodyPatterns) {
                matcher = matcher.and(bodyPattern.getMatcher());
            }
        }
        return matcher;
    }

    @Override
    public PatternMatch matches(Request request) {
        //Done to avoid getting value in case is slow
        if (matcher == PatternMatcher.any() || matcher == PatternMatcher.none()) {
            return matcher.matches("");
        }

        String body = request.getBodyAsString();
        PatternMatch match = matcher.matches(body);
        if (!match.isMatched()) {
            notifier().info(String.format("URL %s is match, but body is not: %s", request.getUrl(), body));
        }
        return match;
    }

}
