
package cn.pinming.cadshow.bim;

import android.database.Cursor;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSONArray;
import com.weqia.utils.L;
import com.weqia.utils.StrUtil;
import com.weqia.utils.datastorage.db.DaoConfig;
import com.weqia.utils.datastorage.db.DbUtil;
import com.weqia.utils.datastorage.file.PathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pinming.cadshow.bim.data.BuildInfo;
import cn.pinming.cadshow.bim.data.floor;
import osg.AndroidExample.EGLview;
import osg.AndroidExample.osgNativeLib;

public class LoadFileAsyncTask extends AsyncTask<String, Void[], Boolean> {

//    private AndroidUserMobileSurfaceView mSurfaceView;
    private static EGLview mEGLview;
    private MobileSurfaceActivity ctx;
    private String mPath;
    private boolean isLoad = true;

    private boolean bOpenMode;

    /*    public LoadFileAsyncTask(MobileSurfaceActivity ctx, AndroidUserMobileSurfaceView mSurfaceView, String mPath) {
            this.mSurfaceView = mSurfaceView;
            this.ctx = ctx;
            this.mPath = mPath;
        }*/
    public LoadFileAsyncTask(MobileSurfaceActivity ctx, EGLview mEGLview, String mPath, boolean isLoad) {

        this.mEGLview = mEGLview;
        this.ctx = ctx;
        this.mPath = mPath;
        this.isLoad = isLoad;
    }


    @Override
    protected Boolean doInBackground(String... paths) {
        if (isCancelled())
            return true;
        //
        //设置系统路径
        String strPath = PathUtil.getFilePath() + "/hsf";
        String projectPath = mPath.substring(0, mPath.lastIndexOf("/"));
        osgNativeLib.setSysPath(strPath);
        if (isCancelled())
            return true;


        //从数据库导入信息
        String end = mPath.substring(mPath.lastIndexOf(".") + 1,
                mPath.length()).toLowerCase();

/*
        if (end.equals("jsonb") || end.equals("db")) {
            DaoConfig config = new DaoConfig();
            config.setContext(ctx.getApplicationContext());
            config.setDbUpdateListener(null);
            config.setDbName(mPath);
            DbUtil db = getNewDbInstance(config);
            if (db != null) {
                //建立楼层id对应名称
                buildFloorMap(db);
                buildComtypeMap(db);

                boolean isNewPbim = false;
                if (checkTableExist(db, "showenttable")) {
//                    mSurfaceView.setIsNewPbim(true);
                    isNewPbim = true;
                }
                int mode = setProjectMode(db, isNewPbim);
                bOpenMode = ctx.getMobileSurfaceHandler().wantDownSource(mode);
                while (true) {
                    if (bOpenMode) {
                        break;
                    }
                    try {
                        new Thread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        L.d("程序出错！");
                        break;
                    }
                    L.e("卡住程序，等在下载");
                }
                L.d("下载完成，继续程序");
                osgNativeLib.loadFile(paths[0]);
                if (!isNewPbim) {
                    //整栋
                    //java端读取有大小限制 移到jni执行
//                if (-99999 == importAllFloor(db))
//                    return true;
                    //楼层
                    int iMinId = importFloor(db);
                    if (-99999 == iMinId)
                        return true;
                    //轴网 导入最底层
                    if (-99999 == importAxis(db, iMinId))
                        return true;
                } else {
                    if (-99999 == importNewPbimAxis(db))
                        return true;
                }
            }
        } else {
            osgNativeLib.loadFile(paths[0]);
            return true;
        }
*/
//        mSurfaceView.onModeFrameRate();
        bOpenMode = ctx.getMobileSurfaceHandler().wantDownSource(7);
        while (true) {
            if (bOpenMode) {
                break;
            }
            try {
                new Thread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                L.d("程序出错！");
                break;
            }
            L.e("卡住程序，等在下载");
        }
        L.d("下载完成，继续程序");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            if (isLoad) {
                osgNativeLib.loadFile(paths[0]);
            }
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildData();
        ctx.getMobileSurfaceHandler().getDrawerInfo();
        ctx.fileLoadFinish();
        String info = osgNativeLib.getShowLayerInfo();
        ctx.setSelGradleMsg(info);
        return true;
    }

    private void buildData() {
        ArrayList<floor> floorList = new ArrayList<floor>();
        HashMap<String, String> comtypeMap = new HashMap();
        String drawerStr = osgNativeLib.getLayerInfo();
        if (StrUtil.isEmptyOrNull(drawerStr)) {
            return;
        }
        List<BuildInfo> buildInfos = JSONArray.parseArray(drawerStr, BuildInfo.class);
        for (BuildInfo buildInfo : buildInfos) {
            String[] floorInfo = buildInfo.getValue().split(";");
            int[] floorIds = new int[floorInfo.length];
            List<String> comtypes = new ArrayList<>();
            for (int i=0;i<floorIds.length;i++) {
                String[] data = floorInfo[i].split(":");
                floorIds[i] = Integer.parseInt(data[0].toString().trim());
                String[] comtype = data[1].split(",");
                comtypes.addAll(Arrays.asList(comtype));
            }
            for (int i = 0; i < floorIds.length; i++) {
                floor data = new floor();
                data.setId(floorIds[i]);
                data.setShowid(osgNativeLib.GetShowId(floorIds[i]));
                data.setLcmc(osgNativeLib.GetFloorName(floorIds[i]));
                floorList.add(data);
            }
            ctx.setFloorList(floorList);
            for (int i = 0; i < comtypes.size(); i++) {
                int comtype = Integer.parseInt(comtypes.get(i).toString().trim());
                String name = osgNativeLib.GetComtypeName(comtype);
                comtypeMap.put(String.valueOf(comtype), name);
            }
        }

        mEGLview.setComtypeMap(comtypeMap);
    }

    public void setbOpenMode(boolean bOpenMode) {
        this.bOpenMode = bOpenMode;
    }

    protected boolean checkTableExist(DbUtil db, String tableName) {
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
        Cursor cursor = db.getDb().rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        if (count > 0)
            return true;
        return false;
    }

    protected int setProjectMode(DbUtil db, boolean isNewPbim) {
        int mode = -1;
        if (!checkTableExist(db, "projectcode"))
            return mode;

        if (isNewPbim) {
            String sql = "select parmvalue from projectcode where parmname='appid'";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String smode = cursor.getString(0);
                mode = Integer.parseInt(smode);
                osgNativeLib.setProjectMode(mode);
            }
        } else {
            String sql = "select parmvalue from projectcode where parmname='CurProjectMode'";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String smode = cursor.getString(0);
                String[] arry = smode.split("\\|");
                if (arry.length > 0) {
                    mode = Integer.parseInt(arry[0]);
                    osgNativeLib.setProjectMode(mode);
                }
            }
        }

        return mode;
    }

    protected void buildFloorMap(DbUtil db) {
        if (!checkTableExist(db, "floor"))
            return;

        ArrayList<floor> floorList = new ArrayList<floor>();
        String sql = "select * from floor order by showid asc";
        Cursor cursor = db.getDb().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int showid = cursor.getInt(1);
            String strLcmc = cursor.getString(2);

            floor data = new floor();
            data.setId(id);
            data.setShowid(showid);
            data.setLcmc(strLcmc);

            floorList.add(data);
        }
        ctx.setFloorList(floorList);
        cursor.close();
    }

    protected void buildComtypeMap(DbUtil db) {
        if (!checkTableExist(db, "comtypeinfo"))
            return;

        HashMap<String, String> comtypeMap = new HashMap();
        String sql = "select * from comtypeinfo";
        Cursor cursor = db.getDb().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int comtype = cursor.getInt(1);
            String name = cursor.getString(2);
            comtypeMap.put(String.valueOf(comtype), name);
        }
        mEGLview.setComtypeMap(comtypeMap);
        cursor.close();
    }

    protected int importNewPbimAxis(DbUtil db) {
        if (!checkTableExist(db, "axisshowinfo"))
            return 1;

        String sql = "select * from axisshowinfo";
        Cursor cursor = db.getDb().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            if (isCancelled())
                return -99999;

            int floorid = cursor.getInt(2);
            int ldmbg = getFloorLDMBG(db, floorid);
//            mSurfaceView.setHeight(ldmbg);

            String buffer = cursor.getString(3);

            try {
                JSONObject jsonObj = new JSONObject(buffer);

/*                String arcData = jsonObj.getString("ARC");
                mSurfaceView.setLineData_ARC(arcData);

                String horData = jsonObj.getString("HOR");
                mSurfaceView.setLineData_HOR(horData);

                String verData = jsonObj.getString("VER");
                mSurfaceView.setLineData_VER(verData);

                String oblData = jsonObj.getString("OBL");
                mSurfaceView.setLineData_OBL(oblData);

                String otherData = jsonObj.getString("OTHER");
                mSurfaceView.setLineData_OTHER(otherData);

                String symbolData = jsonObj.getString("SYMBOL");
                mSurfaceView.setSymbolData(symbolData);

                mSurfaceView.drawAxis(floorid);*/
            } catch (JSONException e) {
            }

        }
        cursor.close();

        return 1;
    }

    protected int getFloorLDMBG(DbUtil db, int floorid) {
        if (!checkTableExist(db, "floor"))
            return 0;
        int ldmbg = 0;
        String sql = "select * from floor where id=" + floorid;
        Cursor cursor = db.getDb().rawQuery(sql, null);
        if (cursor.moveToNext()) {
            ldmbg = cursor.getInt(6);
        }
        cursor.close();
        return ldmbg;
    }

    protected int importAllFloor(DbUtil db) {
        try {
//           List<SimFloorEntAllFloor> itemList = db.findAll(SimFloorEntAllFloor.class);
//            if (StrUtil.listNotNull(itemList)) {
//                for (int i = 0; i < itemList.size(); i++) {
//                    if (isCancelled())
//                        return -99999;
//                    byte[] entInfo = itemList.get(i).getEntinfo();
//                    mSurfaceView.readSimEntInfo(entInfo, entInfo.length, 1, "LIN", 111, 0);
//                }
//            }
            String tableName = "SimFloorEnt_AllFloor";
            int count = 0;
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            if (count > 0) {
                sql = "select * from " + tableName;
                cursor = db.getDb().rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    if (isCancelled())
                        return -99999;
                    byte[] entInfo = cursor.getBlob(3);
//                    mSurfaceView.readSimEntInfo(entInfo, entInfo.length, 1, "LIN", 111, 0);
                }
            }
        } catch (Exception e) {
            return 1;
        }

        return 1;
    }

    class entData {
        public String strHandle = "";
        public int iComtype = 0;
    }

    HashMap simFloorHandleMap = new HashMap();
    HashMap simFloorComidMap = new HashMap();
    HashMap componentsMap = new HashMap();

    protected int importFloor(DbUtil db) {
        int iMinid = 1;
        try {
            int iMinHeight = 999999999;
            List<floor> floorItems = db.findAll(floor.class);
            ctx.getFloorList().addAll(floorItems);
            if (StrUtil.listNotNull(floorItems)) {
                HashMap floorMap = new HashMap();
                for (int i = 0; i < floorItems.size(); i++) {
                    if (isCancelled())
                        return -99999;

                    int id = floorItems.get(i).getId();
//                    String strLcmc = floorItems.get(i).getLcmc();
//                    floorMap.put(id,strLcmc);
                    int iLdmbg = floorItems.get(i).getLdmbg();

                    String tableName = "SimFloorEnt_" + id;

                    //表是否存在
                    int count = 0;
                    String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
                    Cursor cursor = db.getDb().rawQuery(sql, null);
                    if (cursor.moveToNext()) {
                        count = cursor.getInt(0);
                        cursor.close();
                    }

                    boolean bHasEnt = false;
                    if (count > 0) {
                        sql = "select * from " + tableName;
                        cursor = db.getDb().rawQuery(sql, null);
                        while (cursor.moveToNext()) {
                            if (isCancelled())
                                return -99999;
                            entData newData = new entData();
                            byte[] entInfo = cursor.getBlob(3);
                            int iSloidType = cursor.getInt(2);
                            int iVersion = entInfo[0];
                            if (1 == iVersion)       //老版本需要联表查询数据
                            {
                                int iObjId = cursor.getInt(1);
                                getHandleAndComtype(db, id, iObjId, newData);
                            }
//                            mSurfaceView.readSimEntInfo(entInfo, entInfo.length, id, newData.strHandle, newData.iComtype, iSloidType);
                            bHasEnt = true;
                        }
                    }
                    cursor.close();
                    simFloorHandleMap.clear();
                    simFloorComidMap.clear();

                    //取最底层的有构件的floorid
                    if (iLdmbg < iMinHeight && bHasEnt) {
                        iMinHeight = iLdmbg;
                        iMinid = id;
                        //设置轴网高度
//                        mSurfaceView.setHeight(iMinHeight);
                    }
                }
            }
        } catch (Exception e) {
            return iMinid;
        } finally {
            return iMinid;
        }
    }

    protected int getHandleAndComtype(DbUtil db, int iFloorId, int iObjId, entData newData) {
        if (0 == simFloorHandleMap.size()) {
            String tableName = "SimFloor_" + iFloorId;

            //表是否存在
            int count = 0;
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
                cursor.close();
            }
            if (count > 0) {
                sql = "select * from " + tableName;
                cursor = db.getDb().rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    if (isCancelled())
                        return -99999;
                    int objid = cursor.getInt(0);
                    int iComid = cursor.getInt(1);
                    String strHandle = cursor.getString(2);
                    simFloorHandleMap.put(objid, strHandle);
                    simFloorComidMap.put(objid, iComid);
                }
            }
            cursor.close();
        }

        newData.strHandle = (String) simFloorHandleMap.get(iObjId);
        int iComid = (int) simFloorComidMap.get(iObjId);
        newData.iComtype = getComtype(db, iComid);

        return 1;
    }

    protected int getComtype(DbUtil db, int iComid) {
        int iComtype = 0;
        try {
            if (0 == componentsMap.size()) {
                String tableName = "components";

                //表是否存在
                int count = 0;
                String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
                Cursor cursor = db.getDb().rawQuery(sql, null);
                if (cursor.moveToNext()) {
                    count = cursor.getInt(0);
                    cursor.close();
                }
                if (count > 0) {
                    sql = "select * from " + tableName;
                    cursor = db.getDb().rawQuery(sql, null);
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        int comtype = cursor.getInt(3);
                        componentsMap.put(id, comtype);
                    }
                }
                cursor.close();
            }

            iComtype = (int) componentsMap.get(iComid);

        } catch (Exception e) {
            throw e;
        }

        return iComtype;
    }

    protected int importAxis(DbUtil db, int iMinId) {
        try {
            int count = 0;
            String tableName = "SimFloor_" + iMinId;
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
                cursor.close();
            }
            if (count > 0) {

                sql = "select objid from " + tableName + " where comid=-1000";
                cursor = db.getDb().rawQuery(sql, null);
                if (cursor.moveToNext()) {          //有轴网
                    //轴符
                    {
                        if (isCancelled())
                            return -99999;
                        sql = "select valueid from SimFloor_Detail_" + iMinId + " where key=40026";
                        Cursor cursor_detail = db.getDb().rawQuery(sql, null);
                        if (cursor_detail.moveToNext()) {
                            int valueid = cursor_detail.getInt(0);

                            sql = "select bufvalue from SimFloor_Detail_Buf_" + iMinId + " where valueid=" + valueid;
                            Cursor cursor_buf = db.getDb().rawQuery(sql, null);
                            if (cursor_buf.moveToNext()) {
                                if (isCancelled())
                                    return -99999;
                                byte[] bufvalue = cursor_buf.getBlob(0);
                                String strValue = new String(bufvalue);
//                                mSurfaceView.setSymbolData(strValue);
                            }
                        }
                    }
                    //轴线
                    for (int i = 40028; i <= 40032; i++) {
                        if (isCancelled())
                            return -99999;
                        sql = "select valueid from SimFloor_Detail_" + iMinId + " where key=" + i;
                        Cursor cursor_detail = db.getDb().rawQuery(sql, null);
                        if (cursor_detail.moveToNext()) {
                            if (isCancelled())
                                return -99999;
                            int valueid = cursor_detail.getInt(0);

                            sql = "select bufvalue from SimFloor_Detail_Buf_" + iMinId + " where valueid=" + valueid;
                            Cursor cursor_buf = db.getDb().rawQuery(sql, null);
                            if (cursor_buf.moveToNext()) {
                                if (isCancelled())
                                    return -99999;
                                byte[] bufvalue = cursor_buf.getBlob(0);
                                String strValue = new String(bufvalue);
//                                if (40028 == i)
//                                    mSurfaceView.setLineData_HOR(strValue);
//                                else if (40029 == i)
//                                    mSurfaceView.setLineData_VER(strValue);
//                                else if (40030 == i)
//                                    mSurfaceView.setLineData_OBL(strValue);
//                                else if (40031 == i)
//                                    mSurfaceView.setLineData_ARC(strValue);
//                                else if (40032 == i)
//                                    mSurfaceView.setLineData_OTHER(strValue);
                            }
                        }
                    }
                }
                //构件只显示最底层
//                mSurfaceView.drawAxis(iMinId);
                cursor.close();
            }
        } catch (Exception e) {
            return 1;
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        ctx.setMbTaskExit(true);
//        if (result == false) {
//            L.toastLong("File failed to load");
//        }
//        ctx.getMobileSurfaceHandler().closeDialog();
    }

    @Override
    protected void onCancelled(Boolean result) {
        ctx.setMbTaskExit(true);
        ctx.backClick();
        if (result == null || result == false)
            L.toastLong("File failed to Cancel");
    }


    private static synchronized DbUtil getNewDbInstance(DaoConfig daoConfig) {
        DbUtil dao = null;
        if (dao == null) {
            dao = new DbUtil(daoConfig);
            dao.setDbName(daoConfig.getDbName());
        }
        return dao;
    }

}