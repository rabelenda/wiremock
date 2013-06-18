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
package com.github.tomakehurst.wiremock.core;

import com.github.tomakehurst.wiremock.global.GlobalSettings;
import com.github.tomakehurst.wiremock.global.GlobalSettingsHolder;
import com.github.tomakehurst.wiremock.global.RequestDelayControl;
import com.github.tomakehurst.wiremock.global.RequestDelaySpec;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.stubbing.InMemoryStubMappings;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import com.github.tomakehurst.wiremock.verification.FindRequestsResult;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.journal.MutableCapacityJournal;

import java.util.List;

public class WireMockApp implements StubServer, Admin {
    
    public static final String FILES_ROOT = "__files";
    public static final String ADMIN_CONTEXT_ROOT = "/__admin";

    private final StubMappings stubMappings;
    private final MutableCapacityJournal requestJournal;
    private final GlobalSettingsHolder globalSettingsHolder;
    private final RequestDelayControl requestDelayControl;
    private final boolean browserProxyingEnabled;
    private final MappingsLoader defaultMappingsLoader;

    public WireMockApp(
            RequestDelayControl requestDelayControl,
            boolean browserProxyingEnabled,
            MappingsLoader defaultMappingsLoader,
            Integer journalCapacity) {
        this.requestDelayControl = requestDelayControl;
        this.browserProxyingEnabled = browserProxyingEnabled;
        this.defaultMappingsLoader = defaultMappingsLoader;
        globalSettingsHolder = new GlobalSettingsHolder();
        setupGlobalSettings(journalCapacity);
        stubMappings = new InMemoryStubMappings();
        requestJournal = new MutableCapacityJournal(journalCapacity);
        loadDefaultMappings();
    }

    private void setupGlobalSettings(Integer journalCapactiy) {
        if (journalCapactiy != null) {
            GlobalSettings settings = new GlobalSettings();
            settings.setJournalCapacity(journalCapactiy);
            globalSettingsHolder.replaceWith(settings);
        }
    }

    public GlobalSettingsHolder getGlobalSettingsHolder() {
        return globalSettingsHolder;
    }

    private void loadDefaultMappings() {
        loadMappingsUsing(defaultMappingsLoader);
    }

    public void loadMappingsUsing(final MappingsLoader mappingsLoader) {
        mappingsLoader.loadMappingsInto(stubMappings);
    }
    
    @Override
    public ResponseDefinition serveStubFor(Request request) {
        ResponseDefinition responseDefinition = stubMappings.serveFor(request);
        requestJournal.requestReceived(request, null);
        if (!responseDefinition.wasConfigured() && request.isBrowserProxyRequest() && browserProxyingEnabled) {
            return ResponseDefinition.browserProxy(request);
        }

        return responseDefinition;
    }

    @Override
    public void addStubMapping(StubMapping stubMapping) {
        stubMappings.addMapping(stubMapping);
    }

    @Override
    public void resetMappings() {
        stubMappings.reset();
        requestJournal.reset();
        requestDelayControl.clearDelay();
    }

    @Override
    public void reloadMappings() {
        stubMappings.reset();
        loadDefaultMappings();
    }

    @Override
    public void resetScenarios() {
        stubMappings.resetScenarios();
    }

    @Override
    public int countRequestsMatching(RequestPattern requestPattern) {
        return requestJournal.countRequestsMatching(requestPattern);
    }

    @Override
    public FindRequestsResult findRequestsMatching(RequestPattern requestPattern) {
        List<LoggedRequest> requests = requestJournal.getRequestsMatching(requestPattern);
        return new FindRequestsResult(requests);
    }

    @Override
    public void clearRequests() {
        requestJournal.reset();
    }

    @Override
    public void updateGlobalSettings(GlobalSettings newSettings) {
        globalSettingsHolder.replaceWith(newSettings);
        requestJournal.setCapacity(newSettings.getJournalCapacity());
    }

    @Override
    public GlobalSettings getGlobalSettings() {
        return globalSettingsHolder.get();
    }

    @Override
    public void addSocketAcceptDelay(RequestDelaySpec delaySpec) {
        requestDelayControl.setDelay(delaySpec.milliseconds());
    }

}
