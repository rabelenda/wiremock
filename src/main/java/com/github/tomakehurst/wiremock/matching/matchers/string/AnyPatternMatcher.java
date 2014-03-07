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

class AnyPatternMatcher extends PatternMatcher {

    static AnyPatternMatcher INSTANCE = new AnyPatternMatcher();

    @Override
    public PatternMatch matches(String str) {
        return PatternMatch.matched();
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher not() {
        return NonePatternMatcher.INSTANCE;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher and(PatternMatcher that) {
        return that;
    }

    @Override
    /**
     * Overwritten to avoid unnecessary objects
     */
    public PatternMatcher or(PatternMatcher that) {
        return this;
    }

    @Override
    public String toString() {
        return "any";
    }
}
