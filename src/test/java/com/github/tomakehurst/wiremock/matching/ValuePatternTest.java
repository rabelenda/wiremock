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

import com.github.tomakehurst.wiremock.common.LocalNotifier;
import com.github.tomakehurst.wiremock.common.Notifier;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JMock.class)
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
    public static final String BASIC_JSON_PATH = "$.one";
    public static final String JSON_PATH_WITH_FILTER = "$.numbers[?(@.number == '2')]";
    public static final String JSON_PATH_WITH_FILTER_ON_NESTED_OBJECT = "$..*[?(@.innerOne == 11)]";
    public static final String NOT_A_JSON_DOCUMENT = "Not a JSON document";

    private ValuePattern valuePattern;
    private Mockery context;

    @Before
    public void init() {
        valuePattern = new ValuePattern();
        context = new Mockery();
    }

    @After
    public void cleanUp() {
        LocalNotifier.set(null);
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
        assertEquals(MatchedGroups.noGroups(), tryMatchingEqualToWith(EQUAL_TO_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnMatchesOnEqualToWithOtherValue() {
        assertEquals(MatchedGroups.noGroups(), tryMatchingEqualToWith(NOT_EQUAL_TO_VALUE).getGroups());
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
        assertEquals(MatchedGroups.noGroups(), tryMatchRegexWith(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithoutGroupsWithNotMatchingValue() {
        assertEquals(MatchedGroups.noGroups(), tryMatchRegexWith(NOT_MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void matchingGroupsOnRegexWithGroupsWithMatchingValue() {
        valuePattern.setMatches(REGEX_WITH_GROUPS);
        assertEquals(new MatchedGroups("84"), valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithAnonymousGroupsWithMatchingValue() {
        valuePattern.setMatches(REGEX_WITH_ANONYMOUS_GROUPS);
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
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
        assertEquals(MatchedGroups.noGroups(), tryMatchNegativeRegexWith(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithoutGroupsWithNotMatchingValue() {
        assertEquals(MatchedGroups.noGroups(), tryMatchNegativeRegexWith(NOT_MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithMatchingValue() {
        valuePattern.setDoesNotMatch(REGEX_WITH_GROUPS);
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor(MATCHING_REGEX_VALUE).getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithNotMatchingValue() {
        valuePattern.setDoesNotMatch(REGEX_WITH_GROUPS);
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor(NOT_MATCHING_REGEX_VALUE).getGroups());
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
        assertEquals(MatchedGroups.noGroups(), tryMatchContainsWith(TEXT_CONTAINING_SUB_TEXT).getGroups());
    }

    @Test
    public void emptyGroupsOnContainsWithTextNotContainingSubText() {
        assertEquals(MatchedGroups.noGroups(), tryMatchContainsWith(TEXT_NOT_CONTAINING_SUB_TEXT).getGroups());
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
        assertEquals(MatchedGroups.noGroups(), tryMatchAbsentWith(null).getGroups());
    }

    @Test
    public void emptyGroupsOnAbsentWithSomeValue() {
        assertEquals(MatchedGroups.noGroups(), tryMatchAbsentWith(SOME_VALUE).getGroups());
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

    @Test
    public void matchesOnBasicJsonPathsWithMatchingJson() {
        assertTrue(tryMatchingBasicJsonPath("{ \"one\": 1 }").isMatched());
    }

    private PatternMatch tryMatchingBasicJsonPath(String json) {
        valuePattern.setMatchesJsonPaths(BASIC_JSON_PATH);
        return valuePattern.isMatchFor(json);
    }

    @Test
    public void doesNotMatchOnBasicJsonPathsWithNotMatchingJson() {
        assertFalse(tryMatchingBasicJsonPath("{ \"two\": 2 }").isMatched());
    }

    @Test
    public void matchesOnJsonPathsWithFiltersAndMatchingJson() {
        assertTrue(tryMatchingJsonPathWithFilter("{ \"numbers\": [ {\"number\": 1}, {\"number\": 2} ]}").isMatched());
    }

    private PatternMatch tryMatchingJsonPathWithFilter(String json) {
        valuePattern.setMatchesJsonPaths(JSON_PATH_WITH_FILTER);
        return valuePattern.isMatchFor(json);
    }

    @Test
    public void doesNotMatchOnJsonPathsWithFiltersAndNotMatchingJson() {
        assertFalse(tryMatchingJsonPathWithFilter("{ \"numbers\": [{\"number\": 7} ]}").isMatched());
    }

    @Test
    public void matchesOnJsonPathsWithFiltersOnNestedObjects() {
        assertTrue(tryMatchingOnJsonPathWithFilterOnNestedObject("{ \"things\": { \"thingOne\": { \"innerOne\": 11 }, \"thingTwo\": 2 }}").isMatched());
    }

    private PatternMatch tryMatchingOnJsonPathWithFilterOnNestedObject(String json) {
        valuePattern.setMatchesJsonPaths(JSON_PATH_WITH_FILTER_ON_NESTED_OBJECT);
        return valuePattern.isMatchFor(json);
    }

    @Test
    public void doesNotMatchWhenJsonMatchWithInvalidJson() {
        assertFalse(tryMatchingBasicJsonPath(NOT_A_JSON_DOCUMENT).isMatched());
    }

    @Test
    public void providesSensibleNotificationWhenJsonMatchFailsDueToInvalidJson() {
        expectInfoNotification("Warning: JSON path expression '$.one' failed to match document 'Not a JSON document' because the JSON document couldn't be parsed");
        tryMatchingBasicJsonPath(NOT_A_JSON_DOCUMENT);
    }

    private void expectInfoNotification(final String message) {
        final Notifier notifier = context.mock(Notifier.class);
        context.checking(new Expectations() {{
            one(notifier).info(message);
        }});
        LocalNotifier.set(notifier);
    }

    @Test
    public void providesSensibleNotificationWhenJsonDoesNotMatch() {
        expectInfoNotification("Warning: JSON path expression '$.one' failed to match document '{ \"two\": 2 }' because the JSON path didn't match the document structure");

        tryMatchingBasicJsonPath("{ \"two\": 2 }");
    }

}
