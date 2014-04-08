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

/**
 *
 */
package com.github.tomakehurst.wiremock.http;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class HttpParameters {

    private final Multimap<CaseInsensitiveKey, String> parameters;

    public HttpParameters() {
        parameters = ImmutableMultimap.of();
    }

    public HttpParameters(HttpParameter... parameters) {
        this(ImmutableList.copyOf(parameters));
    }

    public HttpParameters(Iterable<HttpParameter> parameters) {
        ImmutableMultimap.Builder<CaseInsensitiveKey, String> builder = ImmutableMultimap.builder();
        for (HttpParameter parameter: parameters) {
            builder.putAll(caseInsensitive(parameter.key()), parameter.values());
        }

        this.parameters = builder.build();
    }

    public HttpParameters(HttpParameters parameters) {
        this(parameters.all());
    }

    public static HttpParameters noParameters() {
        return new HttpParameters();
    }

    public HttpParameter getParameter(String key) {
        if (!parameters.containsKey(caseInsensitive(key))) {
            return HttpParameter.absent(key);
        }

        Collection<String> values = parameters.get(caseInsensitive(key));
        return new HttpParameter(key, values);
    }

    public Collection<HttpParameter> all() {
        List<HttpParameter> httpParameterList = newArrayList();
        for (CaseInsensitiveKey key: parameters.keySet()) {
            httpParameterList.add(new HttpParameter(key.key, parameters.get(key)));
        }

        return httpParameterList;
    }

    public Set<String> keys() {
        return newHashSet(transform(parameters.keySet(), new Function<CaseInsensitiveKey, String>() {
            public String apply(CaseInsensitiveKey input) {
                return input.key;
            }
        }));
    }

    public static HttpParameters copyOf(HttpParameters source) {
        return new HttpParameters(source);
    }

    public int size() {
        return parameters.asMap().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpParameters that = (HttpParameters) o;

        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    private CaseInsensitiveKey caseInsensitive(String key) {
        return new CaseInsensitiveKey(key);
    }

    private static class CaseInsensitiveKey {
        final String key;

        CaseInsensitiveKey(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CaseInsensitiveKey that = (CaseInsensitiveKey) o;

            if (key != null ? !key.toLowerCase().equals(that.key.toLowerCase()) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key != null ? key.toLowerCase().hashCode() : 0;
        }
    }

}
