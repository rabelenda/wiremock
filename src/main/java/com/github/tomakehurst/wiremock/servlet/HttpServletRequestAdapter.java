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
package com.github.tomakehurst.wiremock.servlet;

import com.github.tomakehurst.wiremock.http.*;
import com.github.tomakehurst.wiremock.jetty.ServletContainerUtils;
import com.google.common.io.CharStreams;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.list;

public class HttpServletRequestAdapter implements Request {
	
	private final HttpServletRequest request;
	private String cachedBody;
	
	public HttpServletRequestAdapter(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String getUrl() {
		String url = request.getRequestURI();

		if (!isNullOrEmpty(request.getContextPath())) {
			url = url.replace(request.getContextPath(), "");
		}

		return withQueryStringIfPresent(url);
	}
	
	@Override
	public String getAbsoluteUrl() {
		return withQueryStringIfPresent(request.getRequestURL().toString());
	}

    private String withQueryStringIfPresent(String url) {
        return url + (isNullOrEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
    }

	@Override
	public RequestMethod getMethod() {
		return RequestMethod.valueOf(request.getMethod().toUpperCase());
	}

	@Override
	public String getBodyAsString() {
		if (cachedBody == null) {
			try {
                cachedBody = CharStreams.toString(request.getReader());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		return cachedBody;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getHeader(String key) {
	    List<String> headerNames = list(request.getHeaderNames());
		for (String currentKey: headerNames) {
			if (currentKey.toLowerCase().equals(key.toLowerCase())) {
				return request.getHeader(currentKey);
			}
		}
		
		return null;
	}

    @Override
    @SuppressWarnings("unchecked")
    public HttpHeader header(String key) {
        List<String> headerNames = list(request.getHeaderNames());
        for (String currentKey: headerNames) {
            if (currentKey.toLowerCase().equals(key.toLowerCase())) {
                List<String> valueList = list(request.getHeaders(currentKey));
                return new HttpHeader(key, valueList);
            }
        }

        return HttpHeader.absent(key);
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return getHeaders().getContentTypeHeader();
    }

    @Override
	public boolean containsHeader(String key) {
		return header(key).isPresent();
	}

    @Override
    public HttpHeaders getHeaders() {
        List<HttpHeader> headerList = newArrayList();
        for (String key: getAllHeaderKeys()) {
            headerList.add(header(key));
        }

        return new HttpHeaders(headerList);
    }

    @SuppressWarnings("unchecked")
	@Override
	public Set<String> getAllHeaderKeys() {
		LinkedHashSet<String> headerKeys = new LinkedHashSet<String>();
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			headerKeys.add(headerNames.nextElement());
		}
		
		return headerKeys;
	}

    @SuppressWarnings("unchecked")
    @Override
    public HttpParameter parameter(String key) {
        //cant directly use request.getParameterValues(key) since is case sensitive
        List<String> parameterNames = list(request.getParameterNames());
        for (String currentKey: parameterNames) {
            if (currentKey.toLowerCase().equals(key.toLowerCase())) {
                List<String> valueList = Arrays.asList(request.getParameterValues(currentKey));
                return new HttpParameter(key, valueList);
            }
        }

        return HttpParameter.absent(key);
    }

    @Override
    public HttpParameters getParameters() {
        List<HttpParameter> parameterList = newArrayList();
        for (String key: getAllParameterKeys()) {
            parameterList.add(parameter(key));
        }

        return new HttpParameters(parameterList);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAllParameterKeys() {
        LinkedHashSet<String> parameterKeys = new LinkedHashSet<String>();
        for (Enumeration<String> parameterNames = request.getParameterNames(); parameterNames
                .hasMoreElements();) {
            parameterKeys.add(parameterNames.nextElement());
        }

        return parameterKeys;
    }

    @Override
	public boolean isBrowserProxyRequest() {
		return ServletContainerUtils.isBrowserProxyRequest(request);
	}

}
