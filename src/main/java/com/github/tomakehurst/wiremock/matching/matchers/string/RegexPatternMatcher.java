package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexPatternMatcher extends PatternMatcher {

    private final Pattern pattern;

    public RegexPatternMatcher(String regex) {
        pattern = Pattern.compile(regex, Pattern.DOTALL);
    }

    @Override
    public PatternMatch matches(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            String[] groups = new String[matcher.groupCount()];
            for (int i=0; i<matcher.groupCount(); i++) {
                groups[i] = matcher.group(i+1);
            }
            return PatternMatch.matched(groups);
        } else {
            return PatternMatch.notMatched();
        }
    }

    @Override
    public String toString() {
        return "matches " + pattern;
    }
}
