package cn.pinming.cadshow;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.weqia.data.StatedPerference;
import com.weqia.utils.L;
import com.weqia.utils.StrUtil;
import com.weqia.utils.datastorage.file.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import cn.pinming.cadshow.data.ShowDrawKey;

// 业务相关工具类
public class ShowDrawUtil {

    public static final String dwg_loaded = "dwg_loaded";
    public static final String hsf_loaded = "hsf_loaded";

    /**
     * @param ctx
     * @param domain
     * @param provider
     * @param action
     * @param dataMap
     */
    public static void ronterActionSync(Context ctx, String domain,
                                        @NonNull String provider, @NonNull String action,
                                        HashMap<String, String> dataMap) {
        if (L.D) {
            String dataStr = "-";
            if (dataMap != null) dataStr = dataMap.toString();
            L.e("sync-----------------跳转 domain = [" + domain + "], provider = [" + provider + "], action = [" + action + "], 数据 = [" + dataStr + "]");
        }
        try {
/*            RouterRequest routerRequest;
            if (StrUtil.isEmptyOrNull(domain)) {
                routerRequest = RouterRequestUtil.obtain(ctx)
                        .provider(provider)
                        .action(action);
            } else {
                routerRequest = RouterRequestUtil.obtain(ctx)
                        .domain(domain)
                        .provider(provider)
                        .action(action);
            }
            if (dataMap != null)
                for (String key : dataMap.keySet()) {
                    routerRequest.data(key, dataMap.get(key));
                }

            LocalRouter.getInstance(MaApplication.getMaApplication())
                    .rxRoute(ctx, routerRequest);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ctx
     * @param domain
     * @param provider
     * @param action
     * @param dataMap
     */
 /*   public static void ronterAction(Context ctx, String domain,
                                    @NonNull String provider, @NonNull String action,
                                    HashMap<String, String> dataMap,
                                    Consumer<MaActionResult> successConsumer) {
        if (L.D) {
            String dataStr = "-";
            if (dataMap != null) dataStr = dataMap.toString();
            L.e("异步请求-----------------跳转 domain = [" + domain + "], provider = [" + provider + "], action = [" + action + "], 数据 = [" + dataStr + "]");
        }
        try {
            RouterRequest routerRequest;
            if (StrUtil.isEmptyOrNull(domain)) {
                routerRequest = RouterRequestUtil.obtain(ctx)
                        .provider(provider)
                        .action(action);
            } else {
                routerRequest = RouterRequestUtil.obtain(ctx)
                        .domain(domain)
                        .provider(provider)
                        .action(action);
            }
            if (dataMap != null)
                for (String key : dataMap.keySet()) {
                    routerRequest.data(key, dataMap.get(key));
                }

            if (successConsumer == null) {
                successConsumer = new Consumer<MaActionResult>() {
                    @Override
                    public void accept(MaActionResult maActionResult) throws Exception {
                        L.e("默认的成功consumer返回=[" + maActionResult.getMsg().toString() + "]");
                    }
                };
            }
            LocalRouter.getInstance(MaApplication.getMaApplication())
                    .rxRoute(ctx, routerRequest)
                    .subscribeOn(Schedulers.from(getThreadPoolSingleton()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(successConsumer, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            L.w("错误啦，需要查询下", throwable);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    *//**
     * 拷贝数据
     */
    public static void copyProfiles(Context ctx) {
        File mAppDirectory = ctx.getExternalFilesDir(null);
        String fontDir = mAppDirectory.getAbsolutePath();

        AssetManager assetManager = ctx.getAssets();
        List<String> assets = null;
        try {
            assets = Arrays.asList(assetManager.list(""));
        } catch (IOException e) {
            //e.printStackTrace();
            e.printStackTrace();
        }

        if (assets != null && assets.contains(ShowDrawKey.S_FONT)) {
            AssetsUtil.copyAsset(assetManager, ShowDrawKey.S_FONT, fontDir, false);
            L.i("fonts copied to sdcard" + fontDir);
        }

        if (assets != null && assets.contains(ShowDrawKey.S_MATERIALS)) {
            AssetsUtil.copyAsset(assetManager, ShowDrawKey.S_MATERIALS, fontDir, false);
            L.i("materials copied to sdcard" + fontDir);
        }
    }

    public static void copyDatas(Context ctx) {
        AssetManager assetManager = ctx.getAssets();
        List<String> assets = null;
        try {
            assets = Arrays.asList(assetManager.list(""));
        } catch (IOException e) {
            //e.printStackTrace();
            e.printStackTrace();
        }
        String fileDir = PathUtil.getFilePath();

        if (!StatedPerference.getInstance().get(dwg_loaded, Boolean.class, false)) {
            if (assets != null && assets.contains(ShowDrawKey.S_DWG)) {
                AssetsUtil.copyAsset(assetManager, ShowDrawKey.S_DWG, fileDir, false);
                L.e("dwg copied to sdcard" + fileDir);
            }
        }
        if (!StatedPerference.getInstance().get(hsf_loaded, Boolean.class, false)) {
            if (assets != null && assets.contains(ShowDrawKey.S_HSF)) {
                AssetsUtil.copyAsset(assetManager, ShowDrawKey.S_HSF, fileDir, false);
                L.e("hsf copied to sdcard" + fileDir);
            }
        }
    }

    public static int dip2px(Activity ctx, float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Activity ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void autoKeyBoardShow(final EditText editText) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 100);
    }

    public static void hideKeyBoard(final EditText editText) {
        // new Timer().schedule(new TimerTask() {
        // public void run() {
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        // }
        // }, 100);
    }

/*    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/

    private static ExecutorService executorService;

    public static ExecutorService getThreadPoolSingleton() {
        if (executorService == null) {
            synchronized (ShowDrawUtil.class) {
                executorService = Executors.newFixedThreadPool(3);
            }
        }
        return executorService;
    }
}
