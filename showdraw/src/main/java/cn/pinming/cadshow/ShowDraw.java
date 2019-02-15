package cn.pinming.cadshow;

import android.content.Context;
import android.content.Intent;

import com.weqia.utils.L;
import com.weqia.utils.StrUtil;

import java.io.File;

import cn.pinming.cadshow.bim.MobileSurfaceActivity;
import cn.pinming.cadshow.cad.TeighaDwgActivity;
import cn.pinming.cadshow.data.DownConfig;
import cn.pinming.cadshow.data.ShowDrawKey;

/**
 * Created by berwin on 2017/6/28.
 */

public class ShowDraw {

    public static DownConfig downConfig = null;
    public static Context ctx;

//    public static void initShowDraw(Context ctx) {
//        ShowDraw.ctx = ctx;
//        ShowDrawUtil.copyProfiles(ctx);
//        ShowDrawUtil.copyDatas(ctx);
//    }

    public static void initShowDraw(Context ctx, String appKey, String downItypeStr, String severIp, String signKey,
                                    String sourceTypeStr, String realUrlItypeStr, String gzItypeStr) {
        ShowDraw.ctx = ctx;
        ShowDrawUtil.copyProfiles(ctx);
        ShowDrawUtil.copyDatas(ctx);

        try {
            int downItype = Integer.parseInt(downItypeStr);
            int sourceType = Integer.parseInt(sourceTypeStr);
            int realUrlItype = Integer.parseInt(realUrlItypeStr);
            int gzItype = Integer.parseInt(gzItypeStr);

            DownConfig downConfig = new DownConfig(appKey, downItype, severIp, signKey, sourceType, realUrlItype, gzItype);
            ShowDraw.downConfig = downConfig;
            L.e("下载数据配置:" + downConfig.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

//    public static void initShowDwon(DownConfig dConfig) {
//        if (dConfig.getDownItype() != null
//                && dConfig.getRealUrlItype() != null
//                && StrUtil.notEmptyOrNull(dConfig.getSeverIp())
//                && StrUtil.notEmptyOrNull(dConfig.getSignKey())
//                && dConfig.getSourceType() != null) {
//            ShowDraw.downConfig = dConfig;
//            L.e("下载数据配置:" + dConfig.toString());
//        }
//    }

//    public static boolean openFile(Context ctx, String nodeId, String path,
//                                   String selectMode, String selectType, String portInfo) {
//        return openFile(ctx, nodeId, path, null, selectMode, null, selectType, portInfo);
//    }

    public static boolean openFile(Context ctx, String nodeId, String path, String fileName,
                                   String selectMode, String selectType, String portInfo, String nodeType) {
        return openFile(ctx, nodeId, path, fileName, selectMode, null, selectType, portInfo, nodeType, "1", null);
    }


    /**
     * @param ctx
     * @param nodeId
     * @param path
     * @param fileName
     * @param selectMode
     * @param showDrawInterface
     * @param selectType
     * @param portInfo
     * @param nodeType
     * @param bCanAction        打开的模型是否能编辑：1 可以 ； 2 不可以
     * @return
     */
    public static boolean openFile(Context ctx, String nodeId, String path,
                                   String fileName, String selectMode, ShowDrawInterface showDrawInterface,
                                   String selectType, String portInfo, String nodeType, String bCanAction, String pjId) {
//        L.e("打开路径 == " + path);
        Class<?> cls = null;
        if (isDwg(path)) {
            if (showDrawInterface != null)
                TeighaDwgActivity.showDrawInterface = showDrawInterface;
            else
                TeighaDwgActivity.showDrawInterface = null;
            cls = TeighaDwgActivity.class;
        } else if (isHsf(path) || isDb(path)) {
            if (showDrawInterface != null)
                MobileSurfaceActivity.showDrawInterface = showDrawInterface;
            else
                MobileSurfaceActivity.showDrawInterface = null;
            cls = MobileSurfaceActivity.class;
        }

        if (cls == null) {
            L.e("没有可打开的文件");
            return false;
        }

        Intent newIntent = new Intent(ctx, cls);
        newIntent.putExtra(ShowDrawKey.KEY_TOP_BANNER_TITLE, StrUtil.notEmptyOrNull(fileName) ? fileName : new File(path).getName());
        newIntent.putExtra(ShowDrawKey.KEY_OPEN_PATH, path);
        newIntent.putExtra(ShowDrawKey.KEY_OPEN_NODEID, nodeId);
        newIntent.putExtra(ShowDrawKey.KEY_SELECT_MODE, selectMode);
        newIntent.putExtra(ShowDrawKey.KEY_SELECT_TYPE, selectType);
        newIntent.putExtra(ShowDrawKey.KEY_PORT_INFO, portInfo);
        newIntent.putExtra(ShowDrawKey.KEY_NODE_TYPE, nodeType);
        newIntent.putExtra(ShowDrawKey.KEY_CAN_ACTION, bCanAction);
        newIntent.putExtra("pjId", pjId);
        ctx.startActivity(newIntent);
        return true;
    }

    public static boolean canOpenFile(String filePath) {
        if (isDwg(filePath) || isHsf(filePath) || isDb(filePath))
            return true;
        return false;
    }

    public static boolean isDwg(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
        return end.equals("dwg");
    }

    public static boolean isDb(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
        return end.equals("db");
    }


    public static boolean isHsf(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
        return end.equals("hsf") ||
                end.equals("pbim") ||
                end.equals("pmlod");
    }
    public static boolean isMode(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
        return end.equals("pbim") ||
                end.equals("skp") ||
                end.equals("720z") ||
                end.equals("ifc") ||
                end.equals("nwc") ||
                end.equals("nwd") ||
                end.equals("pmlink") ||
                end.equals("rte") ||
                end.equals("rfa") ||
                end.equals("rvt");
    }
    public static boolean isSkp(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
        return end.equals("skp");
    }
}
