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
import com.google.common.base.Objects;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

class JsonEqualityPatternMatcher extends PatternMatcher {

    private final String value;
    private final JSONCompareMode compareMode;

    public JsonEqualityPatternMatcher(String value, JSONCompareMode compareMode) {
       this.value = value;
       this.compareMode = Objects.firstNonNull(compareMode, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Override
    public PatternMatch matches(String str) {
        try {
            JSONCompareResult result = compareJSON(value, str, compareMode);
            return PatternMatch.fromMatched(result.passed());
        } catch (JSONException e) {
            return PatternMatch.notMatched();
        }
    }

    @Override
    public String toString() {
        return "equal to JSON " + value + " with mode " + compareMode;
    }

}
