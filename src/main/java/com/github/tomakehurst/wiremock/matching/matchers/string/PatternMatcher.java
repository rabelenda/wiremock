/*
 * Copyright (C) 2014 Roger Abelenda
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

package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;
import org.skyscreamer.jsonassert.JSONCompareMode;

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

    public static PatternMatcher equalsToJson(String value, JSONCompareMode compareMode) {
        return new JsonEqualityPatternMatcher(value, compareMode);
    }

    public static PatternMatcher equalsToXml(String equalToXml) {
        return new XmlEqualityPatternMatcher(equalToXml);
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
