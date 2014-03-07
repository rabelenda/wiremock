package com.github.tomakehurst.wiremock.verification.journal;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.util.List;

public interface ImmutableCapacityJournal extends RequestJournal {
    void load(List<LoggedRequest> loggedRequests);
    List<LoggedRequest> getAllRequests();
}
