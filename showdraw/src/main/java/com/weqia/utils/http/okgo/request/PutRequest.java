package com.weqia.utils.http.okgo.request;

import com.weqia.utils.http.okgo.model.HttpHeaders;
import com.weqia.utils.http.okgo.utils.OkLogger;
import com.weqia.utils.http.okgo.utils.ParamsUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;

public class PutRequest extends BaseBodyRequest<PutRequest> {

    public PutRequest(String url) {
        super(url);
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        try {
            headers.put(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            OkLogger.e(e);
        }
        Request.Builder requestBuilder = ParamsUtils.appendHeaders(headers);
        return requestBuilder.put(requestBody).url(url).tag(tag).build();
    }
}