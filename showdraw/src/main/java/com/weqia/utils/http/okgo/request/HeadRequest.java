package com.weqia.utils.http.okgo.request;

import com.weqia.utils.http.okgo.utils.ParamsUtils;

import okhttp3.Request;
import okhttp3.RequestBody;

public class HeadRequest extends BaseRequest<HeadRequest> {

    public HeadRequest(String url) {
        super(url);
    }

    @Override
    public RequestBody generateRequestBody() {
        return null;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = ParamsUtils.appendHeaders(headers);
        url = ParamsUtils.createUrlFromParams(baseUrl, params.urlParams);
        return requestBuilder.head().url(url).tag(tag).build();
    }
}