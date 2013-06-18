/* Copyright (C) 2013 Roger Abelenda
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
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.global.GlobalSettings;
import com.github.tomakehurst.wiremock.testsupport.WireMockTestClient;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.After;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.hasExactly;
import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.withUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JournalCapacityTest {

    private WireMockServer server;
    private WireMockTestClient testClient;

    @After
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetLastRequestsWhenRequestQueryWithBoundedJournal() {
        setupJournalWithCapacity(3);
        generateRequests();
        assertThat(getAllRequests(), hasExactly(withUrl("/use/2"), withUrl("/use/3"), withUrl("/use/4")));
    }

    private void setupJournalWithCapacity(Integer capacity) {
        server = new WireMockServer(new WireMockConfiguration().journalCapacity(capacity));
        server.start();
        testClient = new WireMockTestClient();
    }

    private void generateRequests() {
        testClient.get("/use/1");
        testClient.get("/use/2");
        testClient.get("/use/3");
        testClient.get("/use/4");
    }

    private List<LoggedRequest> getAllRequests() {
        return findAll(getRequestedFor(urlMatching("/.*")));
    }

    @Test(expected = VerificationException.class)
    public void shouldFailWhenRequestQueryWithDisabledJournal() {
        setupJournalWithCapacity(0);
        generateRequests();
        getAllRequests();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAllRequestsWhenRequestQueryWithUnboundedJournal() {
        setupJournalWithCapacity(null);
        generateRequests();
        assertThat(getAllRequests(), hasExactly(withUrl("/use/1"), withUrl("/use/2"), withUrl("/use/3"), withUrl("/use/4")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenRequestQueryWithNegativeCapacityJournal() {
        setupJournalWithCapacity(-1);
    }

    @Test
    public void shouldGetNoRequestsWhenQueryJournalWithBoundedJournalFromDisabledJournal() {
        setupJournalWithCapacity(0);
        try {
            generateRequests();
        } catch (VerificationException e) {
            //This is expected this code is just left to make it clear that the notifications will not appear after changing journal
        }
        updateCapacity(3);
        assertEquals(Collections.emptyList(), getAllRequests());
    }

    private void updateCapacity(Integer capacity) {
        GlobalSettings settings = new GlobalSettings();
        settings.setJournalCapacity(capacity);
        server.updateGlobalSettings(settings);
    }

    @Test
    public void shouldGetLastRequestsWhenQueryJournalWithNewRequestsOnBoundedJournalFromDisabledJournal() {
        setupJournalWithCapacity(0);
        updateCapacity(3);
        generateNewRequests();
        assertThat(getAllRequests(), hasExactly(withUrl("/use/6"), withUrl("/use/7"), withUrl("/use/8")));
    }

    private void generateNewRequests() {
        testClient.get("/use/5");
        testClient.get("/use/6");
        testClient.get("/use/7");
        testClient.get("/use/8");
    }

    @Test
    public void shouldGetNoRequestsWhenQueryJournalWithUnboundedJournalFromDisabledJournal() {
        setupJournalWithCapacity(0);
        try {
            generateRequests();
        } catch (VerificationException e) {
            //This is expected this code is just left to make it clear that the notifications will not appear after changing journal
        }
        updateCapacity(null);
        assertEquals(Collections.emptyList(), getAllRequests());
    }

    @Test
    public void shouldGetAllRequestsWhenQueryJournalWithNewRequestsOnUnboundedJournalFromDisabledJournal() {
        setupJournalWithCapacity(0);
        updateCapacity(null);
        generateNewRequests();
        assertThat(getAllRequests(), hasExactly(withUrl("/use/5"), withUrl("/use/6"), withUrl("/use/7"), withUrl("/use/8")));
    }

    @Test
    public void shouldGetLastRequestsWhenQueryJournalWithNewRequestsOnBoundedJournalFromUnboundedJournal() {
        setupJournalWithCapacity(null);
        generateRequests();
        updateCapacity(3);
        testClient.get("/use/5");
        assertThat(getAllRequests(), hasExactly(withUrl("/use/3"), withUrl("/use/4"), withUrl("/use/5")));
    }

    @Test(expected = VerificationException.class)
    public void shouldFailWhenQueryJournalWithDisabledJournalFromUnboundedJournal() {
        setupJournalWithCapacity(null);
        generateRequests(); //This code is just left to make it clear that the notifications will not change the behavior
        updateCapacity(0);
        generateNewRequests(); //This code is just left to make it clear that the notifications will not change the behavior
        getAllRequests();
    }

    @Test
    public void shouldGetLastRequestsWhenQueryJournalWithNewRequestsOnBoundedJournalFromLowerBound() {
        setupJournalWithCapacity(3);
        generateRequests();
        updateCapacity(5);
        generateNewRequests();
        assertThat(getAllRequests(), hasExactly(withUrl("/use/4"), withUrl("/use/5"), withUrl("/use/6"), withUrl("/use/7"), withUrl("/use/8")));
    }

    @Test
    public void shouldGetLastRequestsWhenQueryJournalWithNewRequestsOnBoundedJournalFromGreaterBound() {
        setupJournalWithCapacity(5);
        generateRequests();
        updateCapacity(3);
        testClient.get("/use/5");
        assertThat(getAllRequests(), hasExactly(withUrl("/use/3"), withUrl("/use/4"), withUrl("/use/5")));
    }

    @Test(expected = VerificationException.class)
    public void shouldFailWhenQueryJournalWithDisabledJournalFromBoundedJournal() {
        setupJournalWithCapacity(3);
        generateRequests(); //This code is just left to make it clear that the notifications will not change the behavior
        updateCapacity(0);
        generateNewRequests(); //This code is just left to make it clear that the notifications will not change the behavior
        getAllRequests();
    }

}
