package com.github.tomakehurst.wiremock.matching;

import java.util.Arrays;

public class PatternMatch {

    private final boolean matched;
    private final String[] groups;

    /**
     * Stored instance to not create an instance each time
     */
    private static final PatternMatch NOT_MATCHED = new PatternMatch(false, new String[]{});
    /**
     * Stored instance to not create an instance each time
     */
    private static final PatternMatch MATCHED_WITHOUT_GROUPS = new PatternMatch(true, new String[]{});

    private PatternMatch(boolean matched, String[] groups) {
        this.matched = matched;
        this.groups = groups;
    }

    public static PatternMatch matched(String[] groups) {
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

    public String[] getGroups() {
        return groups;
    }

    public PatternMatch and(PatternMatch that) {
        if (this.matched && that.matched) {
            return matched(concatGroups(groups, that.groups));
        } else {
            return NOT_MATCHED;
        }
    }

    private String[] concatGroups(String[] a1, String[] a2) {
        String[] result = Arrays.copyOf(a1, a1.length + a2.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
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
            return matched(concatGroups(groups, that.groups));
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

        if (matched != that.matched) return false;
        if (!Arrays.equals(groups, that.groups)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (matched ? 1 : 0);
        result = 31 * result + (groups != null ? Arrays.hashCode(groups) : 0);
        return result;
    }
}
