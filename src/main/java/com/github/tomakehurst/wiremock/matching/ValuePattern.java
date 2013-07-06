/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.matching;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;
import com.google.common.base.Objects;

@JsonSerialize(include=Inclusion.NON_NULL)
public class ValuePattern {

	private String equalTo;
	private String contains;
	private String matches;
	private String doesNotMatch;
    private Boolean absent;
    private String matchesJsonPaths;

    @JsonIgnore
    private PatternMatcher matcher;

    private void initMatcher() {
        checkNoMoreThanOneMatchTypeSpecified();
        if (absent != null) {
            if (absent) {
                matcher = PatternMatcher.none();
            } else {
                matcher = PatternMatcher.any();
            }
        } else if (equalTo != null) {
            matcher = PatternMatcher.equalsTo(equalTo);
        } else if (contains != null) {
            matcher = PatternMatcher.contains(contains);
        } else if (matches != null) {
            matcher = PatternMatcher.regex(matches);
        } else if (doesNotMatch != null) {
            matcher = PatternMatcher.regex(doesNotMatch).not();
        } else if (matchesJsonPaths != null) {
            matcher = PatternMatcher.jsonPath(matchesJsonPaths);
        } else {
            matcher = PatternMatcher.any();
        }
    }

    private void checkNoMoreThanOneMatchTypeSpecified() {
        if (countAllAttributes() > 1) {
            throw new IllegalStateException("Only one type of match may be specified");
        }
    }

    private int countAllAttributes() {
        return count(equalTo, contains, matches, doesNotMatch, absent, matchesJsonPaths);
    }

    private int count(Object... objects) {
        int counter = 0;
        for (Object obj: objects) {
            if (obj != null) {
                counter++;
            }
        }

        return counter;
    }

    public static ValuePattern equalTo(String value) {
		ValuePattern valuePattern = new ValuePattern();
		valuePattern.setEqualTo(value);
		return valuePattern;
	}
	
	public static ValuePattern containing(String value) {
		ValuePattern valuePattern = new ValuePattern();
		valuePattern.setContains(value);
		return valuePattern;
	}
	
	public static ValuePattern matches(String value) {
		ValuePattern valuePattern = new ValuePattern();
		valuePattern.setMatches(value);
		return valuePattern;
	}

    public static ValuePattern absent() {
        ValuePattern valuePattern = new ValuePattern();
        valuePattern.setAbsent(true);
        return valuePattern;
    }
	
	public PatternMatch isMatchFor(String value) {
		checkOneMatchTypeSpecified();
        return matcher.matches(value);
	}
	
	private void checkOneMatchTypeSpecified() {
		if (countAllAttributes() == 0) {
			throw new IllegalStateException("One match type must be specified");
		}
	}
	
	public void setEqualTo(String equalTo) {
		this.equalTo = equalTo;
        initMatcher();
	}
	
	public void setContains(String contains) {
		this.contains = contains;
        initMatcher();
	}
	
	public void setMatches(String matches) {
		this.matches = matches;
        initMatcher();
	}

	public void setDoesNotMatch(String doesNotMatch) {
		this.doesNotMatch = doesNotMatch;
        initMatcher();
	}

    public void setAbsent(Boolean absent) {
        this.absent = absent;
        initMatcher();
    }

    public void setMatchesJsonPaths(String matchesJsonPaths) {
        this.matchesJsonPaths = matchesJsonPaths;
        initMatcher();
    }

	public String getEqualTo() {
        return equalTo;
    }

    public String getContains() {
        return contains;
    }

    public String getMatches() {
        return matches;
    }

    public String getDoesNotMatch() {
        return doesNotMatch;
    }

    public Boolean isAbsent() {
        return absent;
    }

    public String getMatchesJsonPaths() {
        return matchesJsonPaths;
    }

    @Override
	public String toString() {
        return matcher.toString();
	}

	@Override
	public int hashCode() {
        return Objects.hashCode(contains, doesNotMatch, equalTo, matches, matchesJsonPaths);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ValuePattern other = (ValuePattern) obj;
        return Objects.equal(contains, other.contains)
                && Objects.equal(doesNotMatch, other.doesNotMatch)
                && Objects.equal(equalTo, other.equalTo)
                && Objects.equal(matches, other.matches)
                && Objects.equal(matchesJsonPaths, other.matchesJsonPaths);
	}

    public PatternMatcher getMatcher() {
        return matcher;
    }
}
