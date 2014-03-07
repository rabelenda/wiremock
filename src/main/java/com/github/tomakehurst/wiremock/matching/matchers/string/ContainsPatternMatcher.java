package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class ContainsPatternMatcher extends PatternMatcher {

    private final String containedString;

    ContainsPatternMatcher(String containedString) {
        this.containedString = containedString;
    }

    @Override
    public PatternMatch matches(String str) {
        return PatternMatch.fromMatched(str.contains(containedString));
    }

    @Override
    public String toString() {
        return "contains " + containedString;
    }

}
