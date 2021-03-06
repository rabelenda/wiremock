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
import com.github.tomakehurst.wiremock.standalone.StubFilesRepository;
import com.github.tomakehurst.wiremock.stubbing.*;
import com.github.tomakehurst.wiremock.verification.FindRequestsResult;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.RequestJournalDisabledException;
import com.github.tomakehurst.wiremock.verification.VerificationResult;
import com.github.tomakehurst.wiremock.verification.journal.MutableCapacityJournal;
import java.util.List;

public class WireMockApp implements StubServer, Admin {

    private final StubMappings stubMappings;
    private final MutableCapacityJournal requestJournal;
    private final GlobalSettingsHolder globalSettingsHolder;
    private final RequestDelayControl requestDelayControl;
    private final boolean browserProxyingEnabled;
    private final MappingsLoader defaultMappingsLoader;
    private final Container container;
    private final MappingsSaver mappingsSaver;
    private final StubFilesRepository stubFilesRepository;

    public WireMockApp(
            RequestDelayControl requestDelayControl,
            boolean browserProxyingEnabled,
            MappingsLoader defaultMappingsLoader,
            MappingsSaver mappingsSaver,
            StubFilesRepository stubFilesRepository,
            Integer journalCapacity,
            Container container) {
        this.requestDelayControl = requestDelayControl;
        this.browserProxyingEnabled = browserProxyingEnabled;
        this.defaultMappingsLoader = defaultMappingsLoader;
        this.mappingsSaver = mappingsSaver;
        globalSettingsHolder = new GlobalSettingsHolder();
        setupGlobalSettings(journalCapacity);
        stubMappings = new InMemoryStubMappings();
        this.stubFilesRepository = stubFilesRepository;
        requestJournal = new MutableCapacityJournal(journalCapacity);
        this.container = container;
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
        requestJournal.requestReceived(request);
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
    public ListStubMappingsResult listAllStubMappings() {
        return new ListStubMappingsResult(stubMappings.getAll());
    }

    @Override
    public void saveMappings() {
        mappingsSaver.saveMappings(stubMappings);
    }

    @Override
    public void resetMappings() {
        stubMappings.reset();
        requestDelayControl.clearDelay();
        resetRequestsJournal();
    }

    @Override
    public void resetToDefaultMappings() {
        stubMappings.reset();
        loadDefaultMappings();
    }

    @Override
    public void resetScenarios() {
        stubMappings.resetScenarios();
    }

    @Override
    public ListStubFilesResult listAllStubFiles() {
        return stubFilesRepository.list();
    }

    @Override
    public VerificationResult countRequestsMatching(RequestPattern requestPattern) {
        try {
            return VerificationResult.withCount(requestJournal.countRequestsMatching(requestPattern));
        } catch (RequestJournalDisabledException e) {
            return VerificationResult.withRequestJournalDisabled();
        }
    }

    @Override
    public FindRequestsResult findRequestsMatching(RequestPattern requestPattern) {
        try {
            List<LoggedRequest> requests = requestJournal.getRequestsMatching(requestPattern);
            return FindRequestsResult.withRequests(requests);
        } catch (RequestJournalDisabledException e) {
            return FindRequestsResult.withRequestJournalDisabled();
        }
    }

    @Override
    public void resetRequestsJournal() {
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

    @Override
    public void shutdownServer() {
        container.shutdown();
    }

}
