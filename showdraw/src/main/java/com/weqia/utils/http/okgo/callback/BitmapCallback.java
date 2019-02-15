package com.weqia.utils.http.okgo.callback;

import android.graphics.Bitmap;

import com.weqia.utils.http.okgo.convert.BitmapConvert;

import okhttp3.Response;

public abstract class BitmapCallback extends RequestCallBack<Bitmap> {

    @Override
    public Bitmap convertSuccess(Response response) throws Exception {
        Bitmap bitmap = BitmapConvert.create().convertSuccess(response);
        response.close();
        return bitmap;
    }
}