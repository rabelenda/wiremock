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
package com.github.tomakehurst.wiremock.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.matching.MatchedGroups;
import com.google.common.base.Objects;

import java.nio.charset.Charset;

import static com.google.common.base.Charsets.UTF_8;
import static java.net.HttpURLConnection.*;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

@JsonSerialize(include=Inclusion.NON_NULL)
public class ResponseDefinition {

	private int status;
	private byte[] body;
    private boolean isBinaryBody = false;
    private String bodyTemplate;
	private String bodyFileName;
	private HttpHeaders headers;
	private Integer fixedDelayMilliseconds;
	private String proxyBaseUrl;
	private String browserProxyUrl;
	private Fault fault;
	
	private boolean wasConfigured = true;
	private Request originalRequest;
	
	public ResponseDefinition resolved(MatchedGroups groups) {
	    ResponseDefinition newResponseDef = new ResponseDefinition();
	    newResponseDef.status = status;
        if (bodyTemplate != null) {
            newResponseDef.body = resolvedBody(groups);
        } else {
	        newResponseDef.body = body;
        }
        newResponseDef.isBinaryBody = isBinaryBody;
	    newResponseDef.bodyFileName = bodyFileName;
	    newResponseDef.headers = headers;
	    newResponseDef.fixedDelayMilliseconds = fixedDelayMilliseconds;
	    newResponseDef.proxyBaseUrl = proxyBaseUrl;
	    newResponseDef.fault = fault;
	    newResponseDef.wasConfigured = wasConfigured;
	    return newResponseDef;
	}

    private byte[] resolvedBody(MatchedGroups groups) {
        return String.format(bodyTemplate, groups.toArray()).getBytes(Charset.forName(UTF_8.name()));
    }

    public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(final HttpHeaders headers) {
		this.headers = headers;
	}

	public ResponseDefinition(final int statusCode, final String bodyContent) {
		this.status = statusCode;
		this.body = (bodyContent==null) ? null : bodyContent.getBytes(Charset.forName(UTF_8.name()));
	}

    public ResponseDefinition(final int statusCode, final byte[] bodyContent) {
        this.status = statusCode;
        this.body = bodyContent;
        isBinaryBody = true;
    }
	
	public ResponseDefinition() {
		this.status = HTTP_OK;
	}

	public static ResponseDefinition notFound() {
		return new ResponseDefinition(HTTP_NOT_FOUND, (byte[])null);
	}
	
	public static ResponseDefinition ok() {
		return new ResponseDefinition(HTTP_OK, (byte[])null);
	}
	
	public static ResponseDefinition created() {
		return new ResponseDefinition(HTTP_CREATED, (byte[])null);
	}

    public static ResponseDefinition redirectTo(String path) {
        return new ResponseDefinitionBuilder()
                .withHeader("Location", path)
                .withStatus(HTTP_MOVED_TEMP)
                .build();
    }

	public static ResponseDefinition notConfigured() {
	    final ResponseDefinition response = new ResponseDefinition(HTTP_NOT_FOUND, (byte[])null);
	    response.wasConfigured = false;
	    return response;
	}
	
	public static ResponseDefinition browserProxy(Request originalRequest) {
		final ResponseDefinition response = new ResponseDefinition();
	    response.browserProxyUrl = originalRequest.getAbsoluteUrl();
	    return response;
	}
	
	public int getStatus() {
		return status;
	}

	public String getBody() {
		return (!isBinaryBody && body!=null) ? new String(body,Charset.forName(UTF_8.name())) : null;
	}

    @JsonIgnore
    public byte[] getByteBody() {
        return body;
    }

    public String getBase64Body() {
        if (isBinaryBody && body != null) {
            return printBase64Binary(body);
        }

        return null;
    }

    public void setBase64Body(String base64Body) {
        checkNotAlreadySetBody(base64Body);
        isBinaryBody = true;
        body = parseBase64Binary(base64Body);
    }

    // Needs to be explicitly marked as a property, since an overloaded setter with the same
    // name is marked as ignored (see currently open JACKSON-783 bug)
    @JsonProperty
	public void setBody(final String body) {
        checkNotAlreadySetBody(body);
		this.body = (body!=null) ? body.getBytes(Charset.forName(UTF_8.name())) : null;
        isBinaryBody = false;
	}

    @JsonIgnore
    public void setBody(final byte[] body) {
        checkNotAlreadySetBody(body);
        this.body = body;
        isBinaryBody = true;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        checkNotAlreadySetBody(bodyTemplate);
        this.bodyTemplate = bodyTemplate;
        isBinaryBody = false;
    }

    private void checkNotAlreadySetBody(Object value) {
        if (value != null && (body != null || bodyTemplate != null || bodyFileName != null)) {
            throw new IllegalStateException("Body was already set, can not be set again.");
        }
    }

    public void setStatus(final int status) {
        if (status == 0) {
            this.status = HTTP_OK;
        } else {
            this.status = status;
        }
    }

	public void setFixedDelayMilliseconds(final Integer fixedDelayMilliseconds) {
	    this.fixedDelayMilliseconds = fixedDelayMilliseconds;
	}

	public String getBodyFileName() {
		return bodyFileName;
	}

	public void setBodyFileName(final String bodyFileName) {
        checkNotAlreadySetBody(bodyFileName);
		this.bodyFileName = bodyFileName;
	}
	
	public boolean wasConfigured() {
        return wasConfigured;
    }

    public Integer getFixedDelayMilliseconds() {
        return fixedDelayMilliseconds;
    }
    
    @JsonIgnore
    public String getProxyUrl() {
    	if (browserProxyUrl != null) {
    		return browserProxyUrl;
    	}
    	
    	return proxyBaseUrl + originalRequest.getUrl();
    }

	public String getProxyBaseUrl() {
		return proxyBaseUrl;
	}

	public void setProxyBaseUrl(final String proxyBaseUrl) {
		this.proxyBaseUrl = proxyBaseUrl;
	}
	
	@JsonIgnore
	public boolean specifiesBodyFile() {
		return bodyFileName != null;
	}
	
	@JsonIgnore
	public boolean specifiesBodyContent() {
		return body != null;
	}

    @JsonIgnore
    public boolean specifiesBinaryBodyContent() {
        return (body!=null && isBinaryBody);
    }

	@JsonIgnore
	public boolean isProxyResponse() {
		return browserProxyUrl != null || proxyBaseUrl != null;
	}

	public Request getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(final Request originalRequest) {
		this.originalRequest = originalRequest;
	}

	public Fault getFault() {
		return fault;
	}

	public void setFault(final Fault fault) {
		this.fault = fault;
	}

	@Override
	public int hashCode() {
        return Objects.hashCode(body, bodyFileName, bodyTemplate, fault, fixedDelayMilliseconds, headers, originalRequest, proxyBaseUrl, status, wasConfigured);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ResponseDefinition other = (ResponseDefinition) obj;
        if (body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!byteBodyEquals(body, other.body)) {
			return false;
		}
        return Objects.equal(bodyFileName, other.bodyFileName)
                && Objects.equal(bodyTemplate, other.bodyTemplate)
                && Objects.equal(fault, other.fault)
                && Objects.equal(fixedDelayMilliseconds, other.fixedDelayMilliseconds)
                && Objects.equal(headers, other.headers)
                && Objects.equal(proxyBaseUrl, other.proxyBaseUrl)
                && Objects.equal(status, other.status)
                && Objects.equal(wasConfigured, other.wasConfigured);
	}

    private static boolean byteBodyEquals(byte[] expecteds, byte[] actuals)
    {
        if (expecteds == actuals) return true;
        if (expecteds == null) return false;
        if (actuals == null) return false;

        int actualsLength= actuals.length;
        int expectedsLength= expecteds.length;
        if (actualsLength != expectedsLength)
            return false;

        for (int i= 0; i < expectedsLength; i++) {
            byte  expected= expecteds[i];
            byte  actual= actuals[i];
            if(expected!=actual) return false;
        }
        return true;
    }

    @Override
	public String toString() {
		return Json.write(this);
	}
}
