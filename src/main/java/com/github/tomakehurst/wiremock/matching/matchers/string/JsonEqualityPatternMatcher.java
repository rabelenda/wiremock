/**
 * Copyright (C) 2014 Roger Abelenda - 3cinteractive.
 *
 * All Rights Reserved.
 *
 * Unauthorized use is strictly forbidden.
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
       this.compareMode = compareMode;
    }

    @Override
    public PatternMatch matches(String str) {
        try {
            JSONCompareResult result = compareJSON(value, str, Objects.firstNonNull(compareMode,
                    JSONCompareMode.NON_EXTENSIBLE));
            return PatternMatch.fromMatched(result.passed());
        } catch (JSONException e) {
            return PatternMatch.notMatched();
        }
    }
}
