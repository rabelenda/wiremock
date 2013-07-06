package com.github.tomakehurst.wiremock.admin;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.global.GlobalSettings;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import static com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader;
import static java.net.HttpURLConnection.HTTP_OK;

public class GlobalSettingsGetTask implements AdminTask {

    @Override
    public ResponseDefinition execute(Admin admin, Request request) {
        GlobalSettings result = admin.getGlobalSettings();
        ResponseDefinition response = new ResponseDefinition(HTTP_OK, Json.write(result));
        response.setHeaders(new HttpHeaders(httpHeader("Content-Type", "application/json")));
        return response;
    }
}
