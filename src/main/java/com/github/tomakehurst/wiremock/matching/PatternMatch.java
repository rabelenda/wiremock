package com.github.tomakehurst.wiremock.matching;

import com.google.common.base.Objects;

public class PatternMatch {

    private final boolean matched;
    private final MatchedGroups groups;

    /**
     * Stored instance to not create an instance each time
     */
    private static final PatternMatch NOT_MATCHED = new PatternMatch(false, MatchedGroups.noGroups());
    /**
     * Stored instance to not create an instance each time
     */
    private static final PatternMatch MATCHED_WITHOUT_GROUPS = new PatternMatch(true, MatchedGroups.noGroups());

    private PatternMatch(boolean matched, MatchedGroups groups) {
        this.matched = matched;
        this.groups = groups;
    }

    public static PatternMatch matched(MatchedGroups groups) {
        return new PatternMatch(true, groups);
    }

    public static PatternMatch fromMatched(boolean matched) {
        //done this way because does not create instances without the need
        if (matched) {
            return MATCHED_WITHOUT_GROUPS;
        } else {
            return NOT_MATCHED;
        }
    }

    public boolean isMatched() {
        return matched;
    }

    public MatchedGroups getGroups() {
        return groups;
    }

    public PatternMatch and(PatternMatch that) {
        if (this.matched && that.matched) {
            return matched(groups.add(that.groups));
        } else {
            return NOT_MATCHED;
        }
    }

    /**
     * @return The negated pattern, without matched groups. So p is not
     * equal to p.not().not(), because first one could contain groups,
     * but the result of the second operation could not.
     */
    public PatternMatch not() {
        return fromMatched(!matched);
    }

    public PatternMatch or(PatternMatch that) {
        if (this.matched || that.matched) {
            return matched(groups.add(that.groups));
        } else {
            return NOT_MATCHED;
        }
    }

    public static PatternMatch matched() {
        return MATCHED_WITHOUT_GROUPS;
    }

    public static PatternMatch notMatched() {
        return NOT_MATCHED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternMatch that = (PatternMatch) o;

        return Objects.equal(matched, that.matched)
                && Objects.equal(groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(matched, groups);
    }
}
