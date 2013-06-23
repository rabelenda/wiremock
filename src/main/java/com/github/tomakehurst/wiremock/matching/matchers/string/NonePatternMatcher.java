package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class NonePatternMatcher extends PatternMatcher {

    static NonePatternMatcher INSTANCE = new NonePatternMatcher();

    @Override
    public PatternMatch matches(String str) {
        if (str != null) {
            return PatternMatch.notMatched();
        } else {
            return PatternMatch.matched();
        }
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher not() {
        return AnyPatternMatcher.INSTANCE;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher and(PatternMatcher that) {
        return this;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher or(PatternMatcher that) {
        return that;
    }

    @Override
    public String toString() {
        return "none";
    }
}
