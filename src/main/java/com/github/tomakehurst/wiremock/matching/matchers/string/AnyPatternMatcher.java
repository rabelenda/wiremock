package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class AnyPatternMatcher extends PatternMatcher {

    static AnyPatternMatcher INSTANCE = new AnyPatternMatcher();

    @Override
    public PatternMatch matches(String str) {
        return PatternMatch.matched();
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher not() {
        return NonePatternMatcher.INSTANCE;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher and(PatternMatcher that) {
        return that;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher or(PatternMatcher that) {
        return this;
    }

    @Override
    public String toString() {
        return "any";
    }
}
