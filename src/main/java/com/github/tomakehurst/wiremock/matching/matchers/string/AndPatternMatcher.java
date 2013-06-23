package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

class AndPatternMatcher extends PatternMatcher {

    private final PatternMatcher m1;
    private final PatternMatcher m2;

    AndPatternMatcher(PatternMatcher m1, PatternMatcher m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public PatternMatch matches(String str) {
        PatternMatch match = m1.matches(str);
        //done for short circuit
        if (match.isMatched()) {
            return match.and(m2.matches(str));
        } else {
            return match;
        }
    }

    @Override
    public String toString() {
        return "(" + m1.toString() + " and " + m2.toString() + ")";
    }
}
