package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class EqualityPatternMatcher extends PatternMatcher {

    private final String value;

    public EqualityPatternMatcher(String value) {
        this.value = value;
    }

    @Override
    public PatternMatch matches(String str) {
        return PatternMatch.fromMatched(value.equals(str));
    }

    @Override
    public String toString() {
        return "equal " + value;
    }
}
