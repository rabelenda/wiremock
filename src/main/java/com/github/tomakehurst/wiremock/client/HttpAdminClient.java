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
package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.global.GlobalSettings;
import com.github.tomakehurst.wiremock.global.RequestDelaySpec;
import com.github.tomakehurst.wiremock.http.HttpClientFactory;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.FindRequestsResult;
import com.github.tomakehurst.wiremock.verification.VerificationResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import static com.github.tomakehurst.wiremock.common.HttpClientUtils.getEntityAsStringAndCloseStream;
import static com.github.tomakehurst.wiremock.http.MimeType.JSON;
import static com.github.tomakehurst.wiremock.verification.VerificationResult.buildVerificationResultFrom;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class HttpAdminClient implements Admin {
	
	private static final String ADMIN_URL_PREFIX = "http://%s:%d%s/__admin";
	private static final String LOCAL_WIREMOCK_NEW_RESPONSE_URL = ADMIN_URL_PREFIX + "/mappings/new";
    private static final String LOCAL_WIREMOCK_RELOAD_MAPPINGS_URL = ADMIN_URL_PREFIX + "/mappings/reload";
	private static final String LOCAL_WIREMOCK_RESET_URL = ADMIN_URL_PREFIX + "/reset";
    private static final String LOCAL_WIREMOCK_RESET_SCENARIOS_URL = ADMIN_URL_PREFIX + "/scenarios/reset";
    private static final String LOCAL_WIREMOCK_COUNT_REQUESTS_URL = ADMIN_URL_PREFIX + "/requests/count";
    private static final String LOCAL_WIREMOCK_FIND_REQUESTS_URL = ADMIN_URL_PREFIX + "/requests/find";
    private static final String LOCAL_WIREMOCK_CLEAR_REQUESTS_URL = ADMIN_URL_PREFIX + "/requests/clear";
	private static final String WIREMOCK_SET_GLOBAL_SETTINGS_URL = ADMIN_URL_PREFIX + "/settings/set";
    private static final String WIREMOCK_GET_GLOBAL_SETTINGS_URL = ADMIN_URL_PREFIX + "/settings/get";
    private static final String SOCKET_ACCEPT_DELAY_URL = ADMIN_URL_PREFIX + "/socket-delay";
	
	private final String host;
	private final int port;
	private final String urlPathPrefix;
	
	private final HttpClient httpClient;
	
	public HttpAdminClient(String host, int port, String urlPathPrefix) {
		this.host = host;
		this.port = port;
		this.urlPathPrefix = urlPathPrefix;
		
		httpClient = HttpClientFactory.createClient();
	}
	
	public HttpAdminClient(String host, int port) {
		this(host, port, "");
	}

	@Override
	public void addStubMapping(StubMapping stubMapping) {
        String json = Json.write(stubMapping);
		int status = postJsonAndReturnStatus(getAdminUrl(LOCAL_WIREMOCK_NEW_RESPONSE_URL), json);
		if (status != HTTP_CREATED) {
			throw new RuntimeException("Returned status code was " + status);
		}
	}

    private String getAdminUrl(String urlTemplate) {
        return String.format(urlTemplate, host, port, urlPathPrefix);
    }

    private int postJsonAndReturnStatus(String url, String json) {
        HttpPost post = new HttpPost(url);
        try {
            if (json != null) {
                post.setEntity(new StringEntity(json, JSON.toString(), "utf-8"));
            }
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            getEntityAsStringAndCloseStream(response);

            return statusCode;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
	public void resetMappings() {
		int status = postEmptyBodyAndReturnStatus(getAdminUrl(LOCAL_WIREMOCK_RESET_URL));
		assertStatusOk(status);
	}

    private int postEmptyBodyAndReturnStatus(String url) {
        return postJsonAndReturnStatus(url, null);
    }

    @Override
	public void resetScenarios() {
		int status = postEmptyBodyAndReturnStatus(getAdminUrl(LOCAL_WIREMOCK_RESET_SCENARIOS_URL));
		assertStatusOk(status);
	}

    @Override
    public void reloadMappings() {
        int status = postEmptyBodyAndReturnStatus(getAdminUrl(LOCAL_WIREMOCK_RELOAD_MAPPINGS_URL));
        assertStatusOk(status);
    }

    private void assertStatusOk(int status) {
		if (status != HTTP_OK) {
			throw new RuntimeException("Returned status code was " + status);
		}
	}
	
	@Override
	public int countRequestsMatching(RequestPattern requestPattern) {
		String json = Json.write(requestPattern);
		String body = postJsonAssertOkAndReturnBody(getAdminUrl(LOCAL_WIREMOCK_COUNT_REQUESTS_URL), json, HTTP_OK);
		VerificationResult verificationResult = buildVerificationResultFrom(body);
		return verificationResult.getCount();
	}

    private String postJsonAssertOkAndReturnBody(String url, String json, int expectedStatus) {
        HttpPost post = new HttpPost(url);
        try {
            if (json != null) {
                post.setEntity(new StringEntity(json, JSON.toString(), "utf-8"));
            }
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != expectedStatus) {
                throw new VerificationException(
                        "Expected status " + expectedStatus + " for " + url + " but was " + statusCode);
            }

            return getEntityAsStringAndCloseStream(response);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FindRequestsResult findRequestsMatching(RequestPattern requestPattern) {
        String json = Json.write(requestPattern);
        String body = postJsonAssertOkAndReturnBody(getAdminUrl(LOCAL_WIREMOCK_FIND_REQUESTS_URL), json, HTTP_OK);
        return Json.read(body, FindRequestsResult.class);
    }

    @Override
    public void clearRequests() {
        int status = postEmptyBodyAndReturnStatus(getAdminUrl(LOCAL_WIREMOCK_CLEAR_REQUESTS_URL));
        assertStatusOk(status);
    }

    @Override
	public void updateGlobalSettings(GlobalSettings settings) {
		String json = Json.write(settings);
		postJsonAssertOkAndReturnBody(getAdminUrl(WIREMOCK_SET_GLOBAL_SETTINGS_URL), json, HTTP_OK);
	}

    @Override
    public GlobalSettings getGlobalSettings() {
        String body = postJsonAssertOkAndReturnBody(getAdminUrl(WIREMOCK_GET_GLOBAL_SETTINGS_URL), null, HTTP_OK);
        return Json.read(body, GlobalSettings.class);
    }

    @Override
    public void addSocketAcceptDelay(RequestDelaySpec spec) {
        String json = Json.write(spec);
        postJsonAssertOkAndReturnBody(getAdminUrl(SOCKET_ACCEPT_DELAY_URL), json, HTTP_OK);
    }

}
