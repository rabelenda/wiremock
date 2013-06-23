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
package com.github.tomakehurst.wiremock.stubbing;

import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.ValuePattern;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;


public class ValuePatternTest {

    public static final String EQUAL_TO_VALUE = "my-value";
    public static final String NOT_EQUAL_TO_VALUE = "other-value";
    public static final String REGEX_WITHOUT_GROUPS = "[0-9]{6}";
    public static final String REGEX_WITH_GROUPS = "[0-9]{2}([0-9]{2})[0-9]{2}";
    public static final String REGEX_WITH_ANONYMOUS_GROUPS = "[0-9]{2}(?:[0-9]{2})[0-9]{2}";
    public static final String MATCHING_REGEX_VALUE = "938475";
    public static final String NOT_MATCHING_REGEX_VALUE = "abcde";
    public static final String CONTAINED_SUB_TEXT = "some text";
    public static final String TEXT_NOT_CONTAINING_SUB_TEXT = "Nothing to see here";
    public static final String TEXT_CONTAINING_SUB_TEXT = "There's some text here";
    public static final String SOME_VALUE = "blah";
    private ValuePattern valuePattern;

    private static final String[] EMPTY_GROUPS = {};
	
	@Before
	public void init() {
		valuePattern = new ValuePattern();
	}

	@Test
	public void matchesOnEqualToWithSameValue() {
        assertTrue(tryMatchingEqualToWith(EQUAL_TO_VALUE).isMatched());
	}

    private PatternMatch tryMatchingEqualToWith(String value) {
        valuePattern.setEqualTo(EQUAL_TO_VALUE);
        return valuePattern.isMatchFor(value);
    }

    @Test
    public void doesNotMatchOnEqualToWithOtherValue() {
        assertFalse(tryMatchingEqualToWith(NOT_EQUAL_TO_VALUE).isMatched());
    }

    @Test
    public void emptyGroupsOnMatchesOnEqualToWithSameValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchingEqualToWith(EQUAL_TO_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnMatchesOnEqualToWithOtherValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchingEqualToWith(NOT_EQUAL_TO_VALUE).getGroups());
    }

	@Test
	public void matchesOnRegexWithMatchingValue() {
        assertTrue(tryMatchRegexWith(MATCHING_REGEX_VALUE).isMatched());
	}

    private PatternMatch tryMatchRegexWith(String value) {
        valuePattern.setMatches(REGEX_WITHOUT_GROUPS);
        return valuePattern.isMatchFor(value);
    }

    @Test
    public void doesNotMatchOnNotMatchingRegexWithNotMatchingValue() {
        assertFalse(tryMatchRegexWith(NOT_MATCHING_REGEX_VALUE).isMatched());
    }

    @Test
    public void emptyGroupsOnRegexWithoutGroupsWithMatchingValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchRegexWith(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithoutGroupsWithNotMatchingValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchRegexWith(NOT_MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void matchingGroupsOnRegexWithGroupsWithMatchingValue() {
        valuePattern.setMatches(REGEX_WITH_GROUPS);
        assertArrayEquals(new String[]{"84"}, valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithAnonymousGroupsWithMatchingValue() {
        valuePattern.setMatches(REGEX_WITH_ANONYMOUS_GROUPS);
        assertArrayEquals(EMPTY_GROUPS, valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
	public void doesNotMatcheOnNegativeRegexWithMatchingValue() {
		assertFalse(tryMatchNegativeRegexWith(MATCHING_REGEX_VALUE).isMatched());
	}

    private PatternMatch tryMatchNegativeRegexWith(String value) {
        valuePattern.setDoesNotMatch(REGEX_WITHOUT_GROUPS);
        return valuePattern.isMatchFor(value);
    }

    @Test
    public void matchesOnNegativeRegexWithNotMatchingValue() {
        assertTrue(tryMatchNegativeRegexWith(NOT_MATCHING_REGEX_VALUE).isMatched());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithoutGroupsWithMatchingValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchNegativeRegexWith(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithoutGroupsWithNotMatchingValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchNegativeRegexWith(NOT_MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithMatchingValue() {
        valuePattern.setDoesNotMatch(REGEX_WITH_GROUPS);
        assertArrayEquals(EMPTY_GROUPS, valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithNotMatchingValue() {
        valuePattern.setDoesNotMatch(REGEX_WITH_GROUPS);
        assertArrayEquals(EMPTY_GROUPS, valuePattern.isMatchFor(NOT_MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
	public void matchesOnContainsWithTextContainingSubText() {
		assertTrue(tryMatchContainsWith(TEXT_CONTAINING_SUB_TEXT).isMatched());
	}

    private PatternMatch tryMatchContainsWith(String value) {
        valuePattern.setContains(CONTAINED_SUB_TEXT);
        return valuePattern.isMatchFor(value);
    }

    @Test
    public void doesNotMatchOnContainsWithTextNotContainingSubText() {
        assertFalse(tryMatchContainsWith(TEXT_NOT_CONTAINING_SUB_TEXT).isMatched());
    }

    @Test
    public void emptyGroupsOnContainsWithTextContainingSubText() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchContainsWith(TEXT_CONTAINING_SUB_TEXT).getGroups());
    }

    @Test
    public void emptyGroupsOnContainsWithTextNotContainingSubText() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchContainsWith(TEXT_NOT_CONTAINING_SUB_TEXT).getGroups());
    }

    @Test
    public void matchesOnAbsentWithNull() {
        assertTrue(tryMatchAbsentWith(null).isMatched());
    }

    private PatternMatch tryMatchAbsentWith(String value) {
        valuePattern = ValuePattern.absent();
        return valuePattern.isMatchFor(value);
    }

    @Test
    public void doesNotMatchOnAbsentWithSomeValue() {
        assertFalse(tryMatchAbsentWith(SOME_VALUE).isMatched());
    }

    @Test
    public void emptyGroupsOnAbsentWithNull() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchAbsentWith(null).getGroups());
    }

    @Test
    public void emptyGroupsOnAbsentWithSomeValue() {
        assertArrayEquals(EMPTY_GROUPS, tryMatchAbsentWith(SOME_VALUE).getGroups());
    }

    @Test
    public void trueOnIsAbsent() {
        valuePattern = ValuePattern.absent();
        assertTrue(valuePattern.isAbsent());
    }
	
	@Test(expected=IllegalStateException.class)
	public void doesNotPermitMoreThanOneTypeOfMatch() {
		valuePattern.setEqualTo(EQUAL_TO_VALUE);
		valuePattern.setMatches(REGEX_WITHOUT_GROUPS);
	}
	
	@Test(expected=IllegalStateException.class)
	public void doesNotPermitMoreThanOneTypeOfMatchWithOtherOrdering() {
		valuePattern.setMatches(REGEX_WITHOUT_GROUPS);
		valuePattern.setEqualTo(EQUAL_TO_VALUE);
	}
	
	@Test(expected=IllegalStateException.class)
	public void doesNotPermitMoreThanOneTypeOfMatchWithOtherDoesNotMatch() {
		valuePattern.setEqualTo(EQUAL_TO_VALUE);
		valuePattern.setDoesNotMatch(REGEX_WITHOUT_GROUPS);
	}
	
	@Test(expected=IllegalStateException.class)
	public void doesNotPermitZeroMatchTypes() {
		valuePattern.isMatchFor(SOME_VALUE);
	}
}
