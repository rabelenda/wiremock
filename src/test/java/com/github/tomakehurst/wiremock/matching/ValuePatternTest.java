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
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JMock.class)
public class ValuePatternTest {

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
        valuePattern.setEqualTo("my-value");
        assertTrue(valuePattern.isMatchFor("my-value").isMatched());
	}
	
    @Test
    public void doesNotMatchOnEqualToWithOtherValue() {
        valuePattern.setEqualTo("my-value");
        assertFalse(valuePattern.isMatchFor("other-value").isMatched());
    }

    @Test
    public void emptyGroupsOnEqualToWithSameValue() {
        valuePattern.setEqualTo("my-value");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("my-value").getGroups());
    }

    @Test
    public void emptyGroupsOnEqualToWithOtherValue() {
        valuePattern.setEqualTo("my-value");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("other-value").getGroups());
    }

    @Test
    public void matchesOnRegexWithMatchingValue() {
        valuePattern.setMatches("[0-9]{6}");
        assertTrue(valuePattern.isMatchFor("938475").isMatched());
    }

    @Test
    public void doesNotMatchOnNotMatchingRegexWithNotMatchingValue() {
        valuePattern.setMatches("[0-9]{6}");
        assertFalse(valuePattern.isMatchFor("abcde").isMatched());
    }

    @Test
    public void emptyGroupsOnRegexWithoutGroupsWithMatchingValue() {
        valuePattern.setMatches("[0-9]{6}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("938475").getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithoutGroupsWithNotMatchingValue() {
        valuePattern.setMatches("[0-9]{6}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("abcde").getGroups());
    }

    @Test
    public void matchingGroupsOnRegexWithGroupsWithMatchingValue() {
        valuePattern.setMatches("[0-9]{2}([0-9]{2})[0-9]{2}");
        assertEquals(new MatchedGroups("84"), valuePattern.isMatchFor("938475").getGroups());
    }

    @Test
    public void emptyGroupsOnRegexWithAnonymousGroupsWithMatchingValue() {
        valuePattern.setMatches("[0-9]{2}(?:[0-9]{2})[0-9]{2}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("938475").getGroups());
    }

    @Test
    public void doesNotMatchOnNegativeRegexWithMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{6}");
        assertFalse(valuePattern.isMatchFor("938475").isMatched());
    }

    @Test
    public void matchesOnNegativeRegexWithNotMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{6}");
        assertTrue(valuePattern.isMatchFor("abcde").isMatched());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithoutGroupsWithMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{6}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("938475").getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithoutGroupsWithNotMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{6}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("abcde").getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{2}([0-9]{2})[0-9]{2}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("938475").getGroups());
    }

    @Test
    public void emptyGroupsOnNegativeRegexWithGroupsWithNotMatchingValue() {
        valuePattern.setDoesNotMatch("[0-9]{2}([0-9]{2})[0-9]{2}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("abcde").getGroups());
    }

    @Test
    public void matchesOnContainsWithTextContainingSubText() {
        valuePattern.setContains("some text");
        assertTrue(valuePattern.isMatchFor("There's some text here").isMatched());
    }

    @Test
    public void doesNotMatchOnContainsWithTextNotContainingSubText() {
        valuePattern.setContains("some text");
        assertFalse(valuePattern.isMatchFor("Nothing to see here").isMatched());
    }

    @Test
    public void emptyGroupsOnContainsWithTextContainingSubText() {
        valuePattern.setContains("some text");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("There's some text here")
                .getGroups());
    }

    @Test
    public void emptyGroupsOnContainsWithTextNotContainingSubText() {
        valuePattern.setContains("some text");
        assertEquals(MatchedGroups.noGroups(),
                valuePattern.isMatchFor("Nothing to see here").getGroups());
    }

    @Test
    public void matchesOnAbsentWithNull() {
        valuePattern = ValuePattern.absent();
        assertTrue(valuePattern.isMatchFor(null).isMatched());
    }

    @Test
    public void doesNotMatchOnAbsentWithSomeValue() {
        valuePattern = ValuePattern.absent();
        assertFalse(valuePattern.isMatchFor("blah").isMatched());
    }

    @Test
    public void emptyGroupsOnAbsentWithNull() {
        valuePattern = ValuePattern.absent();
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor(null).getGroups());
    }

    @Test
    public void emptyGroupsOnAbsentWithSomeValue() {
        valuePattern = ValuePattern.absent();
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("blah").getGroups());
    }

    @Test
    public void trueOnIsAbsent() {
        valuePattern = ValuePattern.absent();
        assertTrue(valuePattern.isAbsent());
    }

    @Test(expected=IllegalStateException.class)
    public void doesNotPermitMoreThanOneTypeOfMatch() {
        valuePattern.setEqualTo("my-value");
        valuePattern.setMatches("[0-9]{6}");
    }

    @Test(expected=IllegalStateException.class)
    public void doesNotPermitMoreThanOneTypeOfMatchWithOtherOrdering() {
        valuePattern.setMatches("[0-9]{6}");
        valuePattern.setEqualTo("my-value");
    }

    @Test(expected=IllegalStateException.class)
    public void doesNotPermitMoreThanOneTypeOfMatchWithOtherDoesNotMatch() {
        valuePattern.setEqualTo("my-value");
        valuePattern.setDoesNotMatch("[0-9]{6}");
    }

    @Test(expected=IllegalStateException.class)
    public void doesNotPermitZeroMatchTypes() {
        valuePattern.isMatchFor("blah");
    }

    @Test
    public void matchesOnIsEqualToJsonWithSameJson() {
        valuePattern.setEqualToJson("{\"x\":0}");
        assertTrue(valuePattern.isMatchFor("{\"x\":0}").isMatched());
    }

    @Test
    public void emptyGroupsOnIsEqualToJsonWithSameJson() {
        valuePattern.setEqualToJson("{\"x\":0}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("{\"x\":0}").getGroups());
    }

    @Test
    public void emptyGroupsOnIsEqualToJsonWithDifferentJson() {
        valuePattern.setEqualToJson("{\"x\":0}");
        assertEquals(MatchedGroups.noGroups(), valuePattern.isMatchFor("{\"x\":1}").getGroups());
    }

    @Test
    public void matchesOnIsEqualToJsonWithJsonWithNumberMatch() {
        valuePattern.setEqualToJson("{\"x\":0}");
        assertTrue(valuePattern.isMatchFor("{\"x\":0.0}").isMatched());
    }
    
    @Test
    public void matchesOnIsEqualToJsonMoveFields() {
        valuePattern.setEqualToJson("{\"x\":0,\"y\":1}");
        assertTrue(valuePattern.isMatchFor("{\"y\":1,\"x\":0.0}").isMatched());
    }

    @Test
    public void permitsExtraFieldsWhenJsonCompareModeIsLENIENT() {
        valuePattern.setEqualToJson("{ \"x\": 0 }");
        valuePattern.setJsonCompareMode(JSONCompareMode.LENIENT);
        assertTrue(valuePattern.isMatchFor("{ \"x\": 0, \"y\": 1 }").isMatched());
    }

    @Test
    public void doesNotMatchOnEqualToJsonWhenFieldMissing() {
        valuePattern.setEqualToJson("{ \"x\": 0 }");
        assertFalse(valuePattern.isMatchFor("{ \"x\": 0, \"y\": 1 }").isMatched());
    }

    @Test
    public void matchesOnBasicJsonPathsWithMatchingJson() {
        valuePattern.setMatchesJsonPaths("$.one");
        assertTrue(valuePattern.isMatchFor("{ \"one\": 1 }").isMatched());
    }

    @Test
    public void doesNotMatchOnBasicJsonPathsWithNotMatchingJson() {
        valuePattern.setMatchesJsonPaths("$.one");
        assertFalse(valuePattern.isMatchFor("{ \"two\": 2 }").isMatched());
    }

    @Test
    public void matchesOnJsonPathsWithFiltersAndMatchingJson() {
        assertTrue(tryMatchingJsonPathWithFilter(
                "{ \"numbers\": [ {\"number\": 1}, {\"number\": 2} ]}").isMatched());
    }

    private PatternMatch tryMatchingJsonPathWithFilter(String json) {
        valuePattern.setMatchesJsonPaths("$.numbers[?(@.number == '2')]");
        return valuePattern.isMatchFor(json);
    }

    @Test
    public void doesNotMatchOnJsonPathsWithFiltersAndNotMatchingJson() {
        assertFalse(tryMatchingJsonPathWithFilter("{ \"numbers\": [{\"number\": 7} ]}").isMatched());
    }

    @Test
    public void matchesOnJsonPathsWithFiltersOnNestedObjects() {
        assertTrue(tryMatchingOnJsonPathWithFilterOnNestedObject(
                "{ \"things\": { \"thingOne\": { \"innerOne\": 11 }, \"thingTwo\": 2 }}").isMatched());
    }

    private PatternMatch tryMatchingOnJsonPathWithFilterOnNestedObject(String json) {
        valuePattern.setMatchesJsonPaths("$..*[?(@.innerOne == 11)]");
        return valuePattern.isMatchFor(json);
    }

    @Test
    public void doesNotMatchWhenJsonMatchWithInvalidJson() {
        valuePattern.setMatchesJsonPaths("$.one");
        assertFalse(valuePattern.isMatchFor("Not a JSON document").isMatched());
    }

    @Test
    public void providesSensibleNotificationWhenJsonMatchFailsDueToInvalidJson() {
        expectInfoNotification("Warning: JSON path expression '$.one' failed to match document 'Not a JSON document' because the JSON document couldn't be parsed");
        valuePattern.setMatchesJsonPaths("$.one");
        valuePattern.isMatchFor("Not a JSON document");
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
        valuePattern.setMatchesJsonPaths("$.one");
        valuePattern.isMatchFor("{ \"two\": 2 }");
    }

}
