package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

public abstract class PatternMatcher {

    public abstract PatternMatch matches(String str);

    public static PatternMatcher any() {
        return AnyPatternMatcher.INSTANCE;
    }

    public static PatternMatcher none() {
        return NonePatternMatcher.INSTANCE;
    }

    public static PatternMatcher regex(String regex) {
        return new RegexPatternMatcher(regex);
    }

    public static PatternMatcher contains(String containedString) {
        return new ContainsPatternMatcher(containedString);
    }

    public static PatternMatcher equalsTo(String value) {
        return new EqualityPatternMatcher(value);
    }

    /**
     * @return the short circuit match. Groups are concatenated when matches.
     */
    public PatternMatcher and(PatternMatcher that) {
        return new AndPatternMatcher(this, that);
    }

    /**
     * @return the short circuit match (so groups are not concatenated, since if matches the first pattern the second one is not evaluated)
     */
    public PatternMatcher or(PatternMatcher that) {
        return new OrPatternMatcher(this, that);
    }

    public PatternMatcher not() {
        return new NotPatternMatcher(this);
    }

    public static PatternMatcher jsonPath(String jsonPath) {
        return new JsonPathPatternMatcher(jsonPath);
    }
}
