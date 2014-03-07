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
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

class JsonPathPatternMatcher extends PatternMatcher {

    private final String jsonPath;

    public JsonPathPatternMatcher(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public PatternMatch matches(String value) {
        try {
            Object obj = JsonPath.read(value, jsonPath);
            if (obj instanceof JSONArray) {
                JSONArray jsonArr = (JSONArray) obj;
                if (jsonArr.size() > 0) {
                    List<String> jsonStrs = Lists.newArrayList();
                    for (Object o: jsonArr) {
                        jsonStrs.add(o.toString());
                    }
                    return PatternMatch.matched(new MatchedGroups(jsonStrs.toArray(new String[]{})));
                } else {
                    return PatternMatch.notMatched();
                }
            }

            if (obj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) obj;
                if (jsonObj.size() > 0) {
                    return PatternMatch.matched(new MatchedGroups(jsonObj.toString()));
                } else {
                    return PatternMatch.notMatched();
                }
            }

            if (obj != null) {
                return PatternMatch.matched(new MatchedGroups(obj.toString()));
            } else {
                return PatternMatch.notMatched();
            }

        } catch (Exception e) {
            String error;
            if (e.getMessage().equalsIgnoreCase("invalid path")) {
                error = "the JSON path didn't match the document structure";
            }
            else if (e.getMessage().equalsIgnoreCase("invalid container object")) {
                error = "the JSON document couldn't be parsed";
            } else {
                error = "of error '" + e.getMessage() + "'";
            }

            String message = String.format(
                    "Warning: JSON path expression '%s' failed to match document '%s' because %s",
                    jsonPath, value, error);
            notifier().info(message);
            return PatternMatch.notMatched();
        }
    }

    @Override
    public String toString() {
        return "matches JSON path " + jsonPath;
    }
}
