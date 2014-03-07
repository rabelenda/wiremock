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
import com.github.tomakehurst.wiremock.matching.matchers.string.PatternMatcher;

public class UrlMatcher extends RequestMatcher {

    private PatternMatcher matcher;

    public static UrlMatcher fromPatternAndUrl(String urlPattern, String url) {
        return new UrlMatcher(urlPattern, url);
    }

    private UrlMatcher(String urlPattern, String url) {
        matcher = getMatcher(urlPattern, url);
    }

    private static PatternMatcher getMatcher(String urlPattern, String url) {
        if (url != null && urlPattern != null) {
            throw new IllegalStateException("URL and URL pattern may not be set simultaneously");
        } else if (url != null) {
            return PatternMatcher.equalsTo(url);
        } else if (urlPattern != null) {
            return PatternMatcher.regex(urlPattern);
        } else {
            return PatternMatcher.any();
        }
    }

    @Override
    public PatternMatch matches(Request request) {
        return matcher.matches(request.getUrl());
    }

}
