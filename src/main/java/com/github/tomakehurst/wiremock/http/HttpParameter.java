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

/**
 *
 */
package com.github.tomakehurst.wiremock.http;

import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

public class HttpParameter {
    private final String key;
    private final List<String> values;

    public HttpParameter(String key, String... values) {
        this.key = key;
        this.values = newArrayList(values);
    }

    public HttpParameter(String key, Collection<String> values) {
        this.key = key;
        this.values = newArrayList(values);
    }

    public static HttpParameter httpParameter(String key, String... values) {
        return new HttpParameter(key, values);
    }

    public static HttpParameter absent(String key) {
        return new HttpParameter(key);
    }

    public static HttpParameter copyOf(HttpParameter parameter) {
        return new HttpParameter(parameter.key(), parameter.values());
    }

    public boolean isPresent() {
        return values.size() > 0;
    }

    public String key() {
        return key;
    }

    public String firstValue() {
        checkPresent();
        return values.get(0);
    }

    public List<String> values() {
        checkPresent();
        return values;
    }

    private void checkPresent() {
        checkState(isPresent(), "No value for parameter " + key);
    }

    public boolean isSingleValued() {
        return values.size() == 1;
    }

    public boolean containsValue(String expectedValue) {
        return values.contains(expectedValue);
    }

    public PatternMatch hasValueMatching(PatternMatcher matcher) {
        if (matcher == PatternMatcher.none() && !isPresent()) {
            return PatternMatch.matched();
        } else {
            return anyValueMatches(matcher);
        }
    }

    private PatternMatch anyValueMatches(PatternMatcher matcher) {
        for (String value: values) {
            PatternMatch match = matcher.matches(value);
            if (match.isMatched()) {
                return match;
            }
        }
        return PatternMatch.notMatched();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append("&").append(key).append("=").append(value);
        }
        String ret = sb.toString();
        return ret.isEmpty() ? "" : ret.substring(1);
    }
}
