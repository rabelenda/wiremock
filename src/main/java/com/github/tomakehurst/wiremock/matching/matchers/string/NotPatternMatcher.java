package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class NotPatternMatcher extends PatternMatcher {

    private final PatternMatcher m;

    public NotPatternMatcher(PatternMatcher m) {
        this.m = m;
    }

    @Override
    public PatternMatch matches(String str) {
        return m.matches(str).not();
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher not() {
        return m;
    }

    @Override
    public String toString() {
        return "not (" + m.toString() + ")";
    }
}
