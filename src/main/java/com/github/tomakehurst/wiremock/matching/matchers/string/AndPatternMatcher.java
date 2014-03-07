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

class AndPatternMatcher extends PatternMatcher {

    private final PatternMatcher m1;
    private final PatternMatcher m2;

    AndPatternMatcher(PatternMatcher m1, PatternMatcher m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public PatternMatch matches(String str) {
        PatternMatch match = m1.matches(str);
        //done for short circuit
        if (match.isMatched()) {
            return match.and(m2.matches(str));
        } else {
            return match;
        }
    }

    @Override
    public String toString() {
        return "(" + m1.toString() + " and " + m2.toString() + ")";
    }
}
