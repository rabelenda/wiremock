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

import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsLoader;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.testsupport.WireMockResponse;
import com.github.tomakehurst.wiremock.testsupport.WireMockTestClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MappingsLoaderAcceptanceTest {
	
	private WireMockServer wireMockServer;
	private WireMockTestClient testClient;

    private File unorderedSourceFolder;

	@Before
	public void init() {
		constructWireMock();
		wireMockServer.start();
		testClient = new WireMockTestClient();
	}

    private void constructWireMock() {
        wireMockServer = new WireMockServer();
        Mockery context = new Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);

        final File[] unorderedFiles = {new File("src/test/resources/test-requests/401-example.json"),
                new File("src/test/resources/test-requests/default.json"),
                new File("src/test/resources/test-requests/200-example.json")};
        unorderedSourceFolder = context.mock(File.class);
        context.checking(new Expectations() {{
            allowing(unorderedSourceFolder).exists(); will(returnValue(true));
            allowing(unorderedSourceFolder).isDirectory(); will(returnValue(true));
            allowing(unorderedSourceFolder).listFiles(); will(onConsecutiveCalls(returnValue(unorderedFiles), returnValue(new File[0])));
        }});
        MappingsLoader mappingsLoader = new JsonFileMappingsLoader(new SingleRootFileSource(unorderedSourceFolder));
        wireMockServer.loadMappingsUsing(mappingsLoader);
    }

    @After
	public void stopWireMock() {
		wireMockServer.stop();
	}
	
	@Test
	public void mappingsLoadedFromJsonFiles() {
        WireMockResponse response = testClient.get("/canned/resource/1");
		assertThat(response.statusCode(), is(200));
		
		response = testClient.get("/canned/resource/2");
		assertThat(response.statusCode(), is(401));
	}

}
