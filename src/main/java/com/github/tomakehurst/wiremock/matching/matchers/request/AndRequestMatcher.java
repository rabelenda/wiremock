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
import com.github.tomakehurst.wiremock.matching.PatternMatch;

public class AndRequestMatcher extends RequestMatcher {

    private final RequestMatcher m1;
    private final RequestMatcher m2;

    AndRequestMatcher(RequestMatcher m1, RequestMatcher m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public PatternMatch matches(Request request) {
        PatternMatch match = m1.matches(request);
        //done for short circuit
        if (match.isMatched()) {
            return match.and(m2.matches(request));
        } else {
            return match;
        }
    }

}
