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
package com.github.tomakehurst.wiremock.stubbing;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.MatchedGroups;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader;
import static junit.framework.Assert.assertFalse;
import static net.sf.json.test.JSONAssert.assertJsonEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ResponseDefinitionTest {

    private static final MatchedGroups MATCHED_GROUPS = new MatchedGroups("g1","g2");

    @Test
    public void resolvedProducesEqualObjectWhenStringBody() {
        ResponseDefinition response = getResponseWithNoBody();
        response.setBody("blah");
        assertEquals(response, response.resolved(MATCHED_GROUPS));
    }

    @Test
    public void resolvedProducesEqualObjectWhenBodyFileName() {
        ResponseDefinition response = getResponseWithNoBody();
        response.setBodyFileName("test.json");
        assertEquals(response, response.resolved(MATCHED_GROUPS));
    }

    @Test
    public void resolvedProducesEqualObjectWhenBase64Body() {
        ResponseDefinition response = getResponseWithNoBody();
        response.setBase64Body("dGVzdA==");
        assertEquals(response, response.resolved(MATCHED_GROUPS));
    }


    private ResponseDefinition getResponseWithNoBody() {
        ResponseDefinition response = new ResponseDefinition();
        response.setFault(Fault.EMPTY_RESPONSE);
        response.setHeaders(new HttpHeaders(httpHeader("thing", "thingvalue")));
        response.setFixedDelayMilliseconds(1112);
        response.setProxyBaseUrl("http://base.com");
        response.setStatus(222);
        return response;
    }

    @Test
    public void resolvedResolvesBodyTemplate() {
        ResponseDefinition response = getResponseWithNoBody();
        response.setBodyTemplate("blah %1$s -> (%1$s) %2$s");
        ResponseDefinition expected = getResponseWithNoBody();
        expected.setBody("blah g1 -> (g1) g2");
        assertEquals(expected, response.resolved(MATCHED_GROUPS));
    }
    
    @Test
    public void resolvedPreservesConfiguredFlag() {
        ResponseDefinition response = ResponseDefinition.notConfigured();
        ResponseDefinition copiedResponse = response.resolved(MATCHED_GROUPS);
        assertFalse(copiedResponse.wasConfigured());
    }

    private static final String STRING_BODY =
            "{	        								\n" +
            "		\"status\": 200,    				\n" +
            "		\"body\": \"String content\" 		\n" +
            "}											";

    @Test
    public void correctlyUnmarshalsFromJsonWhenBodyIsAString() {
        ResponseDefinition responseDef = Json.read(STRING_BODY, ResponseDefinition.class);
        assertThat(responseDef.getBase64Body(), is(nullValue()));
        assertThat(responseDef.getBody(), is("String content"));
    }

    @Test
    public void correctlyMarshalsToJsonWhenBodyIsAString() {
        ResponseDefinition responseDef = new ResponseDefinition();
        responseDef.setStatus(200);
        responseDef.setBody("String content");

        assertJsonEquals(STRING_BODY, Json.write(responseDef));
    }

    private static final byte[] BODY = new byte[] {1, 2, 3};
    private static final String BASE64_BODY = "AQID";
    private static final String BINARY_BODY =
            "{	        								        \n" +
            "		\"status\": 200,    				        \n" +
            "		\"base64Body\": \"" + BASE64_BODY + "\"     \n" +
            "}											        ";

    @Test
    public void correctlyUnmarshalsFromJsonWhenBodyIsBinary() {
        ResponseDefinition responseDef = Json.read(BINARY_BODY, ResponseDefinition.class);
        assertThat(responseDef.getBody(), is(nullValue()));
        assertThat(responseDef.getByteBody(), is(BODY));
    }

    @Test
    public void correctlyMarshalsToJsonWhenBodyIsBinary() {
        ResponseDefinition responseDef = new ResponseDefinition();
        responseDef.setStatus(200);
        responseDef.setBase64Body(BASE64_BODY);

        String actualJson = Json.write(responseDef);
        assertJsonEquals("Expected: " + BINARY_BODY + "\nActual: " + actualJson,
                BINARY_BODY, actualJson);
    }
}
