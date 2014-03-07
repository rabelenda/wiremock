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

import com.github.tomakehurst.wiremock.matching.MatchedGroups;
import com.github.tomakehurst.wiremock.matching.PatternMatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexPatternMatcher extends PatternMatcher {

    private final Pattern pattern;

    public RegexPatternMatcher(String regex) {
        pattern = Pattern.compile(regex, Pattern.DOTALL);
    }

    @Override
    public PatternMatch matches(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            String[] groups = new String[matcher.groupCount()];
            for (int i=0; i<matcher.groupCount(); i++) {
                groups[i] = matcher.group(i+1);
            }
            return PatternMatch.matched(new MatchedGroups(groups));
        } else {
            return PatternMatch.notMatched();
        }
    }

    @Override
    public String toString() {
        return "matches " + pattern;
    }
}
