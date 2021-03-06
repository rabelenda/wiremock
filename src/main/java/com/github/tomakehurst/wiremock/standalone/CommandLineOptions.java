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
package com.github.tomakehurst.wiremock.standalone;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.*;
import com.github.tomakehurst.wiremock.core.Options;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class CommandLineOptions implements Options {
	
	private static final String HELP = "help";
	private static final String RECORD_MAPPINGS = "record-mappings";
	private static final String PROXY_ALL = "proxy-all";
    private static final String PROXY_VIA = "proxy-via";
	private static final String PORT = "port";
        private static final String BIND_ADDRESS = "bind-address";
    private static final String HTTPS_PORT = "https-port";
    private static final String HTTPS_KEYSTORE = "https-keystore";
	private static final String VERBOSE = "verbose";
	private static final String ENABLE_BROWSER_PROXYING = "enable-browser-proxying";
    private static final String DISABLE_REQUEST_JOURNAL = "no-request-journal";
    private static final String JOURNAL_CAPACITY = "journal-capacity";
    private static final String ROOT_DIR = "root-dir";

    private final OptionSet optionSet;
	private String helpText;

    public CommandLineOptions(String... args) {
		OptionParser optionParser = new OptionParser();
		optionParser.accepts(PORT, "The port number for the server to listen on").withRequiredArg();
        optionParser.accepts(HTTPS_PORT, "If this option is present WireMock will enable HTTPS on the specified port").withRequiredArg();
        optionParser.accepts(BIND_ADDRESS, "The IP to listen connections").withRequiredArg();
        optionParser.accepts(HTTPS_KEYSTORE, "Path to an alternative keystore for HTTPS. Must have a password of \"password\".").withRequiredArg();
		optionParser.accepts(PROXY_ALL, "Will create a proxy mapping for /* to the specified URL").withRequiredArg();
        optionParser.accepts(PROXY_VIA, "Specifies a proxy server to use when routing proxy mapped requests").withRequiredArg();
		optionParser.accepts(RECORD_MAPPINGS, "Enable recording of all (non-admin) requests as mapping files");
		optionParser.accepts(ROOT_DIR, "Specifies path for storing recordings (parent for " + WireMockServer.MAPPINGS_ROOT + " and " + WireMockServer.FILES_ROOT + " folders)").withRequiredArg().defaultsTo(".");
		optionParser.accepts(VERBOSE, "Enable verbose logging to stdout");
		optionParser.accepts(ENABLE_BROWSER_PROXYING, "Allow wiremock to be set as a browser's proxy server");
        optionParser.accepts(DISABLE_REQUEST_JOURNAL, "Disable the request journal (to avoid heap growth when running wiremock for long periods without reset)");
        optionParser.accepts(JOURNAL_CAPACITY, "Specify the maximum amount of requests maintained in the journal, older are discarded. If not set then journal is unbounded.").withRequiredArg();
		optionParser.accepts(HELP, "Print this message");
		
		optionSet = optionParser.parse(args);
        validate();
		captureHelpTextIfRequested(optionParser);
	}

    private void validate() {
        if (optionSet.has(HTTPS_KEYSTORE) && !optionSet.has(HTTPS_PORT)) {
            throw new IllegalArgumentException("HTTPS port number must be specified if specifying the keystore path");
        }

        if (recordMappingsEnabled() && (requestJournalDisabled() || Integer.valueOf(0).equals(journalCapacity()))) {
            throw new IllegalArgumentException("Request journal must be enabled to record stubs");
        }

        if (!isValidJournalCapacity()) {
            throw new IllegalArgumentException("Journal capacity, when specified, must be greater or equal to 0");
        }

        if (optionSet.has(RECORD_MAPPINGS) && optionSet.has(DISABLE_REQUEST_JOURNAL)) {
            throw new IllegalArgumentException("Request journal must be enabled to record stubs");
        }
    }

    private boolean isValidJournalCapacity() {
        Integer c = journalCapacity();
        return c == null || c >= 0;
    }

    private void captureHelpTextIfRequested(OptionParser optionParser) {
		if (optionSet.has(HELP)) {
			StringWriter out = new StringWriter();
			try {
				optionParser.printHelpOn(out);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			helpText = out.toString();
		}
	}
	
	public boolean verboseLoggingEnabled() {
		return optionSet.has(VERBOSE);
	}
	
	public boolean recordMappingsEnabled() {
		return optionSet.has(RECORD_MAPPINGS);
	}
	
	private boolean specifiesPortNumber() {
		return optionSet.has(PORT);
	}
	
	@Override
    public int portNumber() {
        if (specifiesPortNumber()) {
            return Integer.parseInt((String) optionSet.valueOf(PORT));
        }

        return DEFAULT_PORT;
	}

    @Override
    public String bindAddress(){
	if (optionSet.has(BIND_ADDRESS)) {
            return (String) optionSet.valueOf(BIND_ADDRESS);
        }

        return DEFAULT_BIND_ADDRESS;
    }

    @Override
    public HttpsSettings httpsSettings() {
        if (!optionSet.has(HTTPS_PORT)) {
            return HttpsSettings.NO_HTTPS;
        }

        if (optionSet.has(HTTPS_KEYSTORE)) {
            return new HttpsSettings(httpsPortNumber(), (String) optionSet.valueOf(HTTPS_KEYSTORE));
        }

        return new HttpsSettings(httpsPortNumber());
    }

    private int httpsPortNumber() {
        return Integer.parseInt((String) optionSet.valueOf(HTTPS_PORT));
    }

    public boolean help() {
		return optionSet.has(HELP);
	}
	
	public String helpText() {
		return helpText;
	}
	
	public boolean specifiesProxyUrl() {
		return optionSet.has(PROXY_ALL);
	}
	
	public String proxyUrl() {
		return (String) optionSet.valueOf(PROXY_ALL);
	}
	
	@Override
    public boolean browserProxyingEnabled() {
		return optionSet.has(ENABLE_BROWSER_PROXYING);
	}

    @Override
    public ProxySettings proxyVia() {
        if (optionSet.has(PROXY_VIA)) {
            String proxyVia = (String) optionSet.valueOf(PROXY_VIA);
            return ProxySettings.fromString(proxyVia);
        }

        return ProxySettings.NO_PROXY;
    }

    @Override
    public FileSource filesRoot() {
        return new SingleRootFileSource((String) optionSet.valueOf(ROOT_DIR));
    }

    @Override
    public Notifier notifier() {
        return new Log4jNotifier();
    }

    private boolean specifiesJournalCapacity() {
        return optionSet.has(JOURNAL_CAPACITY);
    }

    @Override
    public boolean requestJournalDisabled() {
        return optionSet.has(DISABLE_REQUEST_JOURNAL);
    }

    @Override
    public Integer journalCapacity() {
        if (specifiesJournalCapacity()) {
            return Integer.valueOf((String) optionSet.valueOf(JOURNAL_CAPACITY));
        }

        return null;
    }

    @Override
    public String toString() {
        return Joiner.on(", ").withKeyValueSeparator("=").join(
                ImmutableMap.builder()
                        .put("port", portNumber())
                        .put("https", httpsSettings())
                        .put("fileSource", filesRoot())
                        .put("proxyVia", nullToString(proxyVia()))
                        .put("proxyUrl", nullToString(proxyUrl()))
                        .put("recordMappingsEnabled", recordMappingsEnabled())
                        .put("journalCapacity", nullToString(journalCapacity()) )
                        .build());
    }

    private String nullToString(Object value) {
        if (value == null) {
            return "(null)";
        }

        return value.toString();
    }

}
