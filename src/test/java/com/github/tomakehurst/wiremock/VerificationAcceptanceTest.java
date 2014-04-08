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
package com.github.tomakehurst.wiremock;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.MimeType;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.RequestJournalDisabledException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.testsupport.TestHttpHeader.withHeader;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

@RunWith(Enclosed.class)
public class VerificationAcceptanceTest {

    private enum HttpHeaderName {
        CONTENT_TYPE("Content-Type"), ENCODING("Encoding");

        private final String headerName;

        HttpHeaderName(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public String toString() {
            return headerName;
        }
    }
    
    private enum HttpEncoding {
        UTF("UTF-8"), LATIN("LATIN-1");

        private final String encoding;

        HttpEncoding(String encoding) {
            this.encoding = encoding;
        }

        @Override
        public String toString() {
            return encoding;
        }
    }

    public static class JournalEnabled extends AcceptanceTestBase {

        @Test
        public void verifiesRequestBasedOnUrlOnly() {
            testClient.get("/this/got/requested");
            verify(getRequestedFor(urlEqualTo("/this/got/requested")));
        }

        @Test(expected=VerificationException.class)
        public void throwsVerificationExceptionWhenNoMatch() {
            testClient.get("/this/got/requested");
            verify(getRequestedFor(urlEqualTo("/this/did/not")));
        }

        @Test
        public void verifiesWithHeaders() {
            testClient.put("/update/this", withHeader(HttpHeaderName.CONTENT_TYPE.toString(), 
                    MimeType.JSON.toString()), 
                    withHeader(HttpHeaderName.ENCODING.toString(), HttpEncoding.UTF.toString()));
            verify(putRequestedFor(urlMatching("/[a-z]+/this"))
                    .withHeader(HttpHeaderName.CONTENT_TYPE.toString(), 
                            equalTo(MimeType.JSON.toString()))
                    .withHeader(HttpHeaderName.ENCODING.toString(),
                            notMatching(HttpEncoding.LATIN.toString())));
        }

        @Test
        public void verifiesWithMultiValueHeaders() {
            testClient.get("/multi/value/header",
                    withHeader("X-Thing", "One"),
                    withHeader("X-Thing", "Two"),
                    withHeader("X-Thing", "Three"));

            verify(getRequestedFor(urlEqualTo("/multi/value/header"))
                .withHeader("X-Thing", equalTo("Two"))
                .withHeader("X-Thing", matching("Thr.*")));

            verify(getRequestedFor(urlEqualTo("/multi/value/header"))
                    .withHeader("X-Thing", equalTo("Three")));
        }

        @Test(expected=VerificationException.class)
        public void throwsVerificationExceptionWhenHeadersDoNotMatch() {
            testClient.put("/to/modify", withHeader(HttpHeaderName.CONTENT_TYPE.toString(), 
                    MimeType.JSON.toString()), 
                    withHeader(HttpHeaderName.ENCODING.toString(), HttpEncoding.LATIN.toString()));
            verify(putRequestedFor(urlEqualTo("/to/modify"))
                    .withHeader(HttpHeaderName.CONTENT_TYPE.toString(), 
                            equalTo(MimeType.JSON.toString()))
                    .withHeader(HttpHeaderName.ENCODING.toString(),
                            notMatching(HttpEncoding.LATIN.toString())));
        }

        private static final String SAMPLE_JSON =
            "{ 													\n" +
            "	\"thing\": {									\n" +
            "		\"importantKey\": \"Important value\",		\n" +
            "	}												\n" +
            "}													";


        @Test
        public void verifiesWithBody() {
            testClient.postWithBody("/add/this", SAMPLE_JSON, MimeType.JSON.toString(), 
                    HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/add/this"))
                    .withRequestBody(matching(".*\"importantKey\": \"Important value\".*")));
        }

        @Test
        public void verifiesWithBodyContainingJson() {
            testClient.postWithBody("/body/contains", SAMPLE_JSON, MimeType.JSON.toString(), 
                    HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/body/contains"))
                    .withRequestBody(matchingJsonPath("$.thing"))
                    .withRequestBody(matchingJsonPath(
                            "$..*[?(@.importantKey == 'Important " + "value')]")));
        }

        @Test
        public void verifiesWithBodyEquallingJson() {
            testClient.postWithBody("/body/json", SAMPLE_JSON, MimeType.JSON.toString(), 
                    HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/body/json"))
                    .withRequestBody(equalToJson(SAMPLE_JSON)));
        }

        @Test
        public void verifiesWithBodyEquallingJsonWithCompareMode() {
            testClient.postWithBody("/body/json/lenient", "{ \"message\": \"Hello\", " +
                    "\"key\": \"welcome.message\" }", MimeType.JSON.toString(), 
                    HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/body/json/lenient"))
                    .withRequestBody(equalToJson("{ \"message\": \"Hello\" }", LENIENT)));
        }

        @Test
        public void verifiesWithBodyEquallingXml() {
            testClient.postWithBody("/body/xml", "<thing><subThing>The stuff</subThing></thing>",
                    MimeType.XML.toString(), HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/body/xml"))
                    .withRequestBody(equalToXml(
                            "<thing>     <subThing>The stuff\n</subThing>\n\n" + "    </thing>")));
        }

        @Test
        public void verifiesWithBodyContainingString() {
            testClient.postWithBody("/body/json",
                    SAMPLE_JSON,
                    MimeType.JSON.toString(),
                    HttpEncoding.UTF.toString());
            verify(postRequestedFor(urlEqualTo("/body/json"))
                    .withRequestBody(containing("Important value")));
        }

        @Test(expected=VerificationException.class)
        public void resetErasesCounters() {
            testClient.get("/count/this");
            testClient.get("/count/this");
            testClient.get("/count/this");

            WireMock.reset();

            verify(getRequestedFor(urlEqualTo("/count/this")));
        }

        @Test
        public void verifiesArbitraryRequestCount() {
            testClient.get("/add/to/count");
            testClient.get("/add/to/count");
            testClient.get("/add/to/count");
            testClient.get("/add/to/count");

            verify(4, getRequestedFor(urlEqualTo("/add/to/count")));
        }

        @Test
        public void verifiesHeaderAbsent() {
            testClient.get("/without/header", withHeader(HttpHeaderName.CONTENT_TYPE.toString(), 
                    MimeType.JSON.toString()));
            verify(getRequestedFor(urlEqualTo("/without/header"))
                    .withHeader(HttpHeaderName.CONTENT_TYPE.toString(),
                            equalTo(MimeType.JSON.toString()))
                    .withoutHeader("Accept"));
        }

        @Test(expected=VerificationException.class)
        public void failsVerificationWhenAbsentHeaderPresent() {
            testClient.get("/without/another/header",
                    withHeader(HttpHeaderName.CONTENT_TYPE.toString(), MimeType.JSON.toString()));
            verify(getRequestedFor(urlEqualTo("/without/another/header"))
                    .withoutHeader(HttpHeaderName.CONTENT_TYPE.toString()));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void showsExpectedAndReceivedRequestsOnVerificationException() {
            testClient.put("/some/request", withHeader("X-My-Stuff", "things"));

            try {
                verify(getRequestedFor(urlEqualTo("/specific/thing")));
                fail();
            } catch (VerificationException e) {
                assertThat(e.getMessage(), allOf(
                        containsString("Expected at least one request matching: {"),
                        containsString("/specific/thing"),
                        containsString("Requests received: "),
                        containsString("/some/request")));
            }
        }

        @Test
        @SuppressWarnings("unchecked")
        public void showsReceivedRequestsOnVerificationException() {
            testClient.put("/some/request", withHeader("X-My-Stuff", "things"));

            try {
                verify(14, getRequestedFor(urlEqualTo("/specific/thing")));
                fail();
            } catch (VerificationException e) {
                assertThat(e.getMessage(), allOf(
                        containsString("Expected exactly 14 requests matching: {"),
                        containsString("/specific/thing"),
                        containsString("Requests received: "),
                        containsString("/some/request")));
            }
        }

        @Test
        public void verifiesPatchRequests() {
            testClient.patchWithBody("/patch/this", SAMPLE_JSON, MimeType.JSON.toString());
            verify(patchRequestedFor(urlEqualTo("/patch/this"))
                    .withRequestBody(matching(".*\"importantKey\": \"Important value\".*")));
        }

        @Test
        public void verifiesWithParametersInUrl() {
            testClient.put("/update/this?p1=val1&p2=val2");
            verify(putRequestedFor(urlMatching("/update/this.*"))
                    .withParameter("p1", equalTo("val1"))
                    .withParameter("p2", notMatching("valX")));
        }

        @Test
        public void verifiesWithMultiValueParameters() {
            testClient.get("/multi/value/parameter?p1=val1&p1=val2");

            verify(getRequestedFor(urlMatching("/multi/value/parameter.*"))
                    .withParameter("p1", equalTo("val1"))
                    .withParameter("p1", matching(".*2")));

            verify(getRequestedFor(urlMatching("/multi/value/parameter.*"))
                    .withParameter("p1", equalTo("val2")));
        }

        @Test(expected=VerificationException.class)
        public void throwsVerificationExceptionWhenParametersDoNotMatch() {
            testClient.put("/to/modify?p1=val1&p2=val2");
            verify(putRequestedFor(urlEqualTo("/to/modify"))
                    .withParameter("p1", equalTo("val1"))
                    .withParameter("p2", notMatching("val2")));
        }

        @Test
        public void verifiesParameterAbsent() {
            testClient.get("/without/header?p1=val1");
            verify(getRequestedFor(urlMatching("/without/header.*"))
                    .withParameter("p1", equalTo("val1"))
                    .withoutParameter("p2"));
        }

        @Test(expected=VerificationException.class)
        public void failsVerificationWhenAbsentParameterPresent() {
            testClient.get("/without/another/header?p1=val1");
            verify(getRequestedFor(urlMatching("/without/header.*"))
                    .withoutParameter("p1"));
        }


    }

    public static class JournalDisabled {

        @Rule
        public WireMockRule wireMockRule = new WireMockRule(wireMockConfig()
                .disableRequestJournal());

        @Test(expected=RequestJournalDisabledException.class)
        public void verifyThrowsExceptionWhenVerificationAttemptedAndRequestJournalDisabled() {
            verify(getRequestedFor(urlEqualTo("/whatever")));
        }

        @Test(expected=RequestJournalDisabledException.class)
        public void findAllThrowsExceptionWhenVerificationAttemptedAndRequestJournalDisabled() {
            findAll(getRequestedFor(urlEqualTo("/whatever")));
        }
    }
}
