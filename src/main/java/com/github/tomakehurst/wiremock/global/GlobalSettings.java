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
package com.github.tomakehurst.wiremock.global;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.google.common.base.Objects;

@JsonSerialize(include=Inclusion.NON_NULL)
public class GlobalSettings {

	private Integer fixedDelay;
    private Integer journalCapacity;

	public Integer getFixedDelay() {
		return fixedDelay;
	}

    public void setFixedDelay(Integer fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public Integer getJournalCapacity() {
        return journalCapacity;
    }

    public void setJournalCapacity(Integer journalCapacity) {
        this.journalCapacity = journalCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalSettings that = (GlobalSettings) o;

        if (!Objects.equal(fixedDelay, that.fixedDelay)) {
            return false;
        }
        return Objects.equal(journalCapacity, that.journalCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fixedDelay, journalCapacity);
    }
}
