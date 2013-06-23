/*
 * Copyright (C) 2011 Thomas Akehurst
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
package com.github.tomakehurst.wiremock.matching;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.matchers.request.*;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;

@JsonSerialize(include=Inclusion.NON_NULL)
public class RequestPattern {

    private String urlPattern;
	private String url;
	private RequestMethod method;
	private Map<String, ValuePattern> headerPatterns;
	private List<ValuePattern> bodyPatterns;

    @JsonIgnore
    private RequestMatcher matcher;

    public RequestPattern(RequestMethod method, String url, Map<String, ValuePattern> headerPatterns) {
		this.url = url;
		this.method = method;
		this.headerPatterns = headerPatterns;
        initMatcher();
	}
	
	public RequestPattern(RequestMethod method) {
		this.method = method;
        initMatcher();
	}
	
	public RequestPattern(RequestMethod method, String url) {
		this.url = url;
		this.method = method;
        initMatcher();
	}
	
	public RequestPattern() {
        initMatcher();
    }

    public static RequestPattern buildRequestPatternFrom(String json) {
        return Json.read(json, RequestPattern.class);
    }

    private void initMatcher() {
        matcher = UrlMatcher.fromPatternAndUrl(urlPattern, url).
                and(new MethodMatcher(method)).
                and(new HeadersMatcher(headerPatterns)).
                and(new BodyMatcher(bodyPatterns));
    }

    public PatternMatch isMatchedBy(Request request) {
		return matcher.matches(request);
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
        initMatcher();
	}
	
	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
        initMatcher();
	}

	public Map<String, ValuePattern> getHeaders() {
		return headerPatterns;
	}
	
	public void addHeader(String key, ValuePattern pattern) {
		if (headerPatterns == null) {
			headerPatterns = newLinkedHashMap();
		}
		
		headerPatterns.put(key, pattern);
        initMatcher();
	}
	
	public void setHeaders(Map<String, ValuePattern> headers) {
		this.headerPatterns = headers;
        initMatcher();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
        initMatcher();
	}
	
	public List<ValuePattern> getBodyPatterns() {
		return bodyPatterns;
	}

	public void setBodyPatterns(List<ValuePattern> bodyPatterns) {
		this.bodyPatterns = bodyPatterns;
        initMatcher();
	}

	@Override
	public int hashCode() {
        return Objects.hashCode(bodyPatterns, headerPatterns, method, url, urlPattern);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RequestPattern other = (RequestPattern) obj;
        return Objects.equal(bodyPatterns, other.bodyPatterns)
                && Objects.equal(headerPatterns, other.headerPatterns)
                && Objects.equal(method, other.method)
                && Objects.equal(url, other.url)
                && Objects.equal(urlPattern, other.urlPattern);
	}

	@Override
	public String toString() {
		return Json.write(this);
	}

}
