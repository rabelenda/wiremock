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

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.MatchedGroups;
import com.github.tomakehurst.wiremock.matching.PatternMatch;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static com.github.tomakehurst.wiremock.stubbing.StubMapping.NOT_CONFIGURED;


public class InMemoryStubMappings implements StubMappings {
	
	private final SortedConcurrentMappingSet mappings = new SortedConcurrentMappingSet();
	private final ConcurrentHashMap<String, Scenario> scenarioMap = new ConcurrentHashMap<String, Scenario>();

    private class MatchingStubMapping {
        private StubMapping mapping;
        private MatchedGroups groups;

        public MatchingStubMapping(StubMapping mapping, MatchedGroups groups) {
            this.mapping = mapping;
            this.groups = groups;
        }
    }

	@Override
	public ResponseDefinition serveFor(Request request) {
        MatchingStubMapping match = findMatchingMapping(request);
        StubMapping matchingMapping = match.mapping;

		notifyIfResponseNotConfigured(request, matchingMapping);
		matchingMapping.updateScenarioStateIfRequired();
		return matchingMapping.getResponse().resolved(match.groups);
	}

    private MatchingStubMapping findMatchingMapping(Request request) {
        for (StubMapping mapping: mappings) {
            if (mapping.isIndependentOfScenarioState() || mapping.requiresCurrentScenarioState()) {
                PatternMatch match = mapping.getRequest().isMatchedBy(request);
                if (match.isMatched()) {
                    return new MatchingStubMapping(mapping, match.getGroups());
                }
            }
        }
        return new MatchingStubMapping(NOT_CONFIGURED, MatchedGroups.noGroups());
    }

    private void notifyIfResponseNotConfigured(Request request, StubMapping matchingMapping) {
		if (matchingMapping == NOT_CONFIGURED) {
		    notifier().info("No mapping found matching URL " + request.getUrl());
		}
	}

	@Override
	public void addMapping(StubMapping mapping) {
		if (mapping.isInScenario()) {
			scenarioMap.putIfAbsent(mapping.getScenarioName(), Scenario.inStartedState());
			Scenario scenario = scenarioMap.get(mapping.getScenarioName());
			mapping.setScenario(scenario);
		}
		
		mappings.add(mapping);
	}

	@Override
	public void reset() {
		mappings.clear();
        scenarioMap.clear();
	}
	
	@Override
	public void resetScenarios() {
		for (Scenario scenario: scenarioMap.values()) {
			scenario.reset();
		}
	}

    @Override
    public List<StubMapping> getAll() {
        return ImmutableList.copyOf(mappings);
    }

}
