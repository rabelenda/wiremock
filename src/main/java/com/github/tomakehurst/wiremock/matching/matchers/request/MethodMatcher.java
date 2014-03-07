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

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.PatternMatch;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static com.github.tomakehurst.wiremock.http.RequestMethod.ANY;

public class MethodMatcher extends RequestMatcher {

    private final RequestMethod method;

    public MethodMatcher(RequestMethod method) {
        this.method = method;
    }

    @Override
    public PatternMatch matches(Request request) {
        boolean matched = method == null || method == ANY || request.getMethod() == method;
        if (!matched) {
            notifier().info(String.format("URL %s is match, but method %s is not", request.getUrl(), request.getMethod()));
        }
        return PatternMatch.fromMatched(matched);
    }

}
