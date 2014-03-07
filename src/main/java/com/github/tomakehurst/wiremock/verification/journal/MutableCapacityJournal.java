package com.github.tomakehurst.wiremock.verification.journal;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.base.Objects;

import java.util.List;

public class MutableCapacityJournal implements RequestJournal {

    private Integer capacity;
    private ImmutableCapacityJournal impl;

    public MutableCapacityJournal(Integer capacity) {
        this.capacity = capacity;
        impl = getImplFromCapacity(capacity);
    }

    private ImmutableCapacityJournal getImplFromCapacity(Integer capacity) {
        if (capacity == null) {
            return new UnboundedInMemoryRequestJournal();
        } else if (capacity > 0) {
            return new BoundedInMemoryRequestJournal(capacity);
        } else if (capacity == 0) {
            return new DisabledRequestJournal();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public synchronized void setCapacity(Integer capacity) {
        if (!Objects.equal(capacity, this.capacity)) {
            List<LoggedRequest> reqs = impl.getAllRequests();
            impl = getImplFromCapacity(capacity);
            impl.load(reqs);
        }
    }

    @Override
    public synchronized int countRequestsMatching(RequestPattern requestPattern) {
        return impl.countRequestsMatching(requestPattern);
    }

    @Override
    public synchronized List<LoggedRequest> getRequestsMatching(RequestPattern requestPattern) {
        return impl.getRequestsMatching(requestPattern);
    }

    @Override
    public synchronized void reset() {
        impl.reset();
    }

    @Override
    public synchronized void requestReceived(Request request) {
        impl.requestReceived(request);
    }
}
