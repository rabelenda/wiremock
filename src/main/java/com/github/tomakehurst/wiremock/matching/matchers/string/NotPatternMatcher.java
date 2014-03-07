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

class NotPatternMatcher extends PatternMatcher {

    private final PatternMatcher m;

    public NotPatternMatcher(PatternMatcher m) {
        this.m = m;
    }

    @Override
    public PatternMatch matches(String str) {
        return m.matches(str).not();
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher not() {
        return m;
    }

    @Override
    public String toString() {
        return "not (" + m.toString() + ")";
    }
}
