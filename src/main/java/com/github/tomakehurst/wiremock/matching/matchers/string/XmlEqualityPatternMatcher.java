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

/**
 *
 */
package com.github.tomakehurst.wiremock.matching.matchers.string;

import com.github.tomakehurst.wiremock.matching.PatternMatch;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import java.io.IOException;

public class XmlEqualityPatternMatcher extends PatternMatcher {

    static {
        XMLUnit.setIgnoreWhitespace(true);
    }

    private final String value;

    public XmlEqualityPatternMatcher(String value) {
        this.value = value;
    }

    @Override
    public PatternMatch matches(String str) {
        try {
            Diff diff = XMLUnit.compareXML(value, str);
            return PatternMatch.fromMatched(diff.similar());
        } catch (SAXException e) {
            return PatternMatch.notMatched();
        } catch (IOException e) {
            return PatternMatch.notMatched();
        }
    }

    @Override
    public String toString() {
        return "equal to XML " + value;
    }

}
