package com.github.tomakehurst.wiremock.matching;

import java.util.Arrays;

public class MatchedGroups {

    private static final MatchedGroups NO_GROUPS = new MatchedGroups(new String[]{});

    private final String[] groups;

    public MatchedGroups(String... groups) {
        this.groups = groups;
    }

    public static MatchedGroups noGroups() {
        return NO_GROUPS;
    }

    public String[] toArray() {
        return groups;
    }

    public String getGroup(int i) {
        return groups[i];
    }

    public MatchedGroups add(MatchedGroups other) {
        String[] result = Arrays.copyOf(groups, groups.length + other.groups.length);
        System.arraycopy(other.groups, 0, result, groups.length, other.groups.length);
        return new MatchedGroups(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchedGroups that = (MatchedGroups) o;

        if (!Arrays.equals(groups, that.groups)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(groups);
    }
}
