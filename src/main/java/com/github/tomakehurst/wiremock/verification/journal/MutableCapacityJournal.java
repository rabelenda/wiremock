/*
 * Copyright (C) 2014 Roger Abelenda
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
