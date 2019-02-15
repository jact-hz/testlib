package com.weqia.component.rcmode.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by jmx on 11/25 0025.
 */
public abstract class RcFastAdapter<T> extends RcBaseFastAdapter<T> {
    public RcFastAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public RcFastAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }

    public void setFilter(List<T> countryModels) {}
}
