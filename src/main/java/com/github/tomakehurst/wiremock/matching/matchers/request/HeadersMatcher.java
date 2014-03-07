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

package com.github.tomakehurst.wiremock.matching.matchers.request;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.github.tomakehurst.wiremock.matching.ValuePattern;
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

public class HeadersMatcher extends RequestMatcher {

    private final Map<String, PatternMatcher> matchers;

    public HeadersMatcher(Map<String,ValuePattern> headerPatterns) {
        matchers = getMatchers(headerPatterns);
    }

    private Map<String, PatternMatcher> getMatchers(Map<String, ValuePattern> headerPatterns) {
        ImmutableMap.Builder<String, PatternMatcher> matchersBuilder = new ImmutableMap.Builder<String, PatternMatcher>();
        if (headerPatterns != null) {
            for (Map.Entry<String, ValuePattern> entry : headerPatterns.entrySet()) {
                if (entry.getValue() == null) {
                    matchersBuilder.put(entry.getKey(), PatternMatcher.none());
                } else {
                    matchersBuilder.put(entry.getKey(), entry.getValue().getMatcher());
                }
            }
        }
        return matchersBuilder.build();
    }

    @Override
    public PatternMatch matches(Request request) {
        PatternMatch matched = PatternMatch.matched();
        for (Map.Entry<String, PatternMatcher> matcher: matchers.entrySet()) {
            HttpHeader header = request.header(matcher.getKey());
            PatternMatch match = header.hasValueMatching(matcher.getValue());
            if (!match.isMatched()) {
                notifier().info(String.format(
                        "URL %s is match, but header %s is not. For a match, value should %s",
                        request.getUrl(),
                        matcher.getKey(),
                        matcher.getValue().toString()));
                return match;
            } else {
                matched.and(match);
            }
        }
        return matched;
    }

}
