package cn.pinming.modelsdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.weqia.BaseInit;
import com.weqia.BitmapInit;
import com.weqia.HttpInit;
import com.weqia.utils.datastorage.db.DaoConfig;
import com.weqia.utils.datastorage.file.NativeFileUtil;


import java.io.File;

import cn.pinming.cadshow.bim.MobileSurfaceActivity;
import cn.pinming.cadshow.bim.MobileSurfaceHandler;
import common.GlobalUtil;
import common.Hks;
import common.WPfCommon;
import common.WeqiaApplication;
import common.db.DBHelper;
import common.db.WeqiaDbUtil;
import common.request.ResultEx;
import common.request.ServiceParams;
import common.request.UserService;

/**
 * Created by lgf on 2019/1/21.
 */

public class CCBimSdkUtil {
    public static void setVisOverAllView(boolean isOverallView) {           //配置底部功能栏按钮——整体页面 ，true为显示，false为隐藏
        MobileSurfaceHandler.isOverallView = isOverallView;
    }
    public static void setVisThreeD(boolean isThreeD) {                     //配置底部功能栏按钮——区域三维 ，true为显示，false为隐藏
        MobileSurfaceHandler.isThreeD = isThreeD;
    }
    public static void setVisMark(boolean isMark) {                      //配置底部功能栏按钮——标记评论 ，true为显示，false为隐藏
        MobileSurfaceHandler.isMark = isMark;
    }
    public static void setVisAction(boolean isAction) {                  ///配置底部功能栏按钮——功能 ，true为显示，false为隐藏
        MobileSurfaceHandler.isAction = isAction;
    }

    public static void initSdk(Application application) {
        BaseInit.getInstance().init(application, "CCBIM", "download", true);
        BitmapInit.getInstance().init(application, false, false);
        HttpInit.getInstance().init(application);
        UserService.getHttpUtil();
        initDb(application);
        WeqiaApplication.getInstance(application);
    }

    public static void initDb(Application ctx) {
        DaoConfig config = new DaoConfig();
        WeqiaApplication weqiaApplication = WeqiaApplication.getInstance();
        if (weqiaApplication == null) {
            return;
        }
        config.setContext(ctx);
        config.setDbName("ccbimDb");
        int dbVersion = WPfCommon.getInstance().get(Hks.db_version, Integer.class, 0);
        if (dbVersion != WeqiaDbUtil.getDbVersion()) {
            if (dbVersion > WeqiaDbUtil.getDbVersion()) {
                NativeFileUtil.delFolder(new File(GlobalUtil.getDbFile(ctx)));
            }
            DBHelper.createAllTable(ctx);
            WPfCommon.getInstance().put(Hks.db_version, WeqiaDbUtil.getDbVersion());
        }
        final WeqiaDbUtil util = WeqiaDbUtil.create(config);
        WeqiaApplication.getInstance().setDbUtil(util);
    }

    public static void openModel(Context context,String versionId) {
        Intent intent = new Intent(context, MobileSurfaceActivity.class);
        intent.putExtra("versionId", versionId);
        intent.putExtra("title", "模型");
        context.startActivity(intent);
    }

    public static String findConvertInfo(Context context,String fileId) {
        ServiceParams params = new ServiceParams(10000);
        params.put("fileId", fileId);
        ResultEx resultEx = UserService.getSyncInfo(params, UrlPathEnum.FINDCONVERTINFO.getValue());
        if (resultEx.getSuccess()) {
            return resultEx.getResult();
        } else {
            return "";
        }
    }
}
