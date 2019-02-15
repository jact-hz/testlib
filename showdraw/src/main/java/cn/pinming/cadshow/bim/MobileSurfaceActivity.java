package cn.pinming.cadshow.bim;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.weqia.utils.L;
import com.weqia.utils.MD5Util;
import com.weqia.utils.StrUtil;
import com.weqia.utils.TimeUtils;
import com.weqia.utils.ViewUtils;
import com.weqia.utils.datastorage.db.DaoConfig;
import com.weqia.utils.datastorage.db.DbUtil;
import com.weqia.utils.datastorage.file.PathUtil;
import com.weqia.utils.dialog.SharedCommonDialog;
import com.weqia.utils.http.HttpUtil;
import com.weqia.utils.http.okgo.model.RequestParams;
import com.weqia.utils.http.okserver.download.DownloadInfo;
import com.weqia.utils.http.okserver.download.DownloadManager;



import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.pinming.cadshow.SharedShowActivity;
import cn.pinming.cadshow.ShowDraw;
import cn.pinming.cadshow.ShowDrawInterface;
import cn.pinming.cadshow.ShowDrawRouterUtil;
import cn.pinming.cadshow.ShowDrawUtil;
import cn.pinming.cadshow.TaskApprovalUtil;
import cn.pinming.cadshow.bim.custormview.ModelGladleDialog;
import cn.pinming.cadshow.bim.data.ComponentData;
import cn.pinming.cadshow.bim.data.ModelPinInfo;
import cn.pinming.cadshow.bim.data.floor;
import cn.pinming.cadshow.bim.tree.DrawerTreeData;
import cn.pinming.cadshow.bim.tree.DrawerTreeHelper;
import cn.pinming.cadshow.bim.tree.StructureTypeData;
import cn.pinming.cadshow.data.ActionTypeEnum;
import cn.pinming.cadshow.data.ShowDrawKey;
import cn.pinming.cadshow.graffiti.GraffitiActivity;
import cn.pinming.cadshow.graffiti.GraffitiParams;
import cn.pinming.cadshow.library.R;
import cn.pinming.cadshow.moveview.PreviewPicDialog;
import cn.pinming.modelsdk.UrlPathEnum;
import common.AttachMsgReceiver;
import common.AttachService;
import common.AttachmentData;
import common.BucketFileData;
import common.CoConfig;
import common.ComponentReqEnum;
import common.EnumData;
import common.GlobalConstants;
import common.GlobalUtil;
import common.NetworkUtil;
import common.WPf;
import common.WeqiaApplication;
import common.request.ResultEx;
import common.request.ServiceParams;
import common.request.ServiceRequester;
import common.request.UserService;
import osg.AndroidExample.EGLview;
import osg.AndroidExample.osgNativeLib;




public class MobileSurfaceActivity extends SharedShowActivity implements View.OnClickListener,
        View.OnTouchListener, View.OnKeyListener {
    enum moveTypes {NONE, DRAG, MDRAG, ZOOM, ACTUALIZE}

    enum navType {PRINCIPAL, SECONDARY}

    enum lightType {ON, OFF}

    moveTypes mode = moveTypes.NONE;
    navType navMode = navType.PRINCIPAL;
    lightType lightMode = lightType.ON;

    PointF oneFingerOrigin = new PointF(0, 0);
    long timeOneFinger = 0;
    PointF twoFingerOrigin = new PointF(0, 0);
    long timeTwoFinger = 0;
    float distanceOrigin;
    public static final String TAG = "tag";



    private final String LAST_TIME_KEY = "LAST_TIME_KEY";
    public static final String STRUCTURE_TYPE_DATA = "STRUCTURE_TYPE_DATA";
    private final int FREQUENCY_OF_CHECK_UPDATE = 1;//检查更新的频率，单位为：天,默认为1天;
    public String openPath;
    public static ShowDrawInterface showDrawInterface;

    //    private AndroidUserMobileSurfaceView mSurfaceView;
    private EGLview egLview;
    //    private String mPath = "";
    private boolean mShouldLoadFile = false;

    static final int MOBILE_SURFACE_GUI_ID = 0;
    static final String MOBILE_SURFACE_POINTER_KEY = "mobileSurfaceId";
    private MobileSurfaceActivity ctx;

    private static boolean mNativeLibsLoaded = false;

    private LoadFileAsyncTask mTask;
    private boolean mbTaskExit = false;

    private MenuItem cancelItem;
    private MenuItem saveItem;
    private MenuItem delItem;
    private MenuItem shareItem;
    private MobileSurfaceHandler mobileSurfaceHandler;
    private ArrayList<floor> floorList = new ArrayList<>();

    private String downloadSourceStr;

    private ModelGladleDialog floorDialog;
    private ModelGladleDialog gradleDialog;
    private String selGradleMsg;

    private boolean isFileLoadFinish = false;
    private String viewInfo;
    private String nodeType;
    private TextView tvTopSel;

    private TextView fab;
    private View fabBGLayout;
    boolean isFABOpen = false;
    private TextView fabSelect1;
    private TextView fabLocation2;
    private TextView fabList3;
    private ProgressBar modeloadProgress;
    private TextView tvProgress;
    //单击时的当前坐标
    public static PointF curentPt = new PointF(0, 0);
    private int iFloorId;     //选择的楼层ID
    private String strFloorName;
    private String strHandle;
    private String strInfo;
    private String strName;
    private int iComid;
    private int iType;

    private String title;
    private int count = 0;
    private int counts = 1;
    private int fileNum = 0;
    private int fileSeniorNum = 0;
    private int downNum = 0;                            //之前已经下载完的文件数
    private List<AttachmentData> dataSeniorList = new ArrayList<>();
    private List<AttachmentData> dataOtherList = new ArrayList<>();
    private boolean isSenior = false;                 //判断必要的文件有没有下载完
    private boolean bOpenMode;
    List<String> urls = new ArrayList<>();
    private GestureDetector mGestureDetector;
    private AttachmentData data = new AttachmentData();
    private boolean bNeedDown = false;
    private Dialog taskDialog;    //任务选择的dialog
    private List<AttachmentData> dataList = new ArrayList<>();
    private Handler handler = new Handler();


    public int getActionType() {
        return actionType;
    }

    public String getSelGradleMsg() {
        return selGradleMsg;
    }

    public void setSelGradleMsg(String selGradleMsg) {
        this.selGradleMsg = selGradleMsg;
//        L.e("selGradleMsg: " + selGradleMsg);
        if (StrUtil.notEmptyOrNull(selGradleMsg)) {
            ArrayList<String[]> initTypes = DrawerTreeHelper.getInitFloorList(selGradleMsg);
            if (StrUtil.listNotNull(initTypes)) {
                for (String[] types : initTypes) {
                    int[] typeIndexs = null;
                    if (types.length > 1) {
                        typeIndexs = DrawerTreeHelper.getIntegerArray(types[1], ",");
                    }
                    int floorIndex = Integer.parseInt(types[0]);
                    for (DrawerTreeData data : mobileSurfaceHandler.getRootDarwerDatas()) {
                        if (data.getTypeIndex() == floorIndex) {
                            //只用于初始化显示楼层使用
                            data.bSelected = true;
                            data.setbExpand(true);
                            selectFloor.add(data);
                            if (typeIndexs != null) {
                                for (int i = 0; i < typeIndexs.length; i++) {
                                    for (DrawerTreeData leafData : mobileSurfaceHandler.getLeafDarwerDatas()) {
                                        if (leafData.getLevel() == 3) {
                                            if (leafData.getSuperParentData().getTypeIndex() == floorIndex) {
                                                if (leafData.getTypeIndex() == typeIndexs[i]) {
                                                    leafData.setbSelected(true);
                                                }
                                            }
                                        } else {
                                            if (leafData.getParentData().getTypeIndex() == floorIndex) {
                                                if (leafData.getTypeIndex() == 1001 && leafData.getTypeIndex() == typeIndexs[i]) {
                                                    leafData.setbSelected(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //初始化楼层完成，执行显示动作
                for (DrawerTreeData leaf : mobileSurfaceHandler.getLeafDarwerDatas()) {
                    if (leaf.getLevel() == 3 || leaf.getTypeIndex() == 1001) {
                        showFloorCADFromSelect(leaf, leaf.isbSelected());
                    }
                }
            }

            if (getActionType() == ActionTypeEnum.YES.value()) {
                getPosDatas(true);
            }
        }
    }

    public boolean isMbTaskExit() {
        return mbTaskExit;
    }

    public void setMbTaskExit(boolean mbTaskExit) {
        this.mbTaskExit = mbTaskExit;
    }

    public void setFloorList(ArrayList<floor> list) {
        floorList = list;
    }

    public ArrayList<floor> getFloorList() {
        return floorList;
    }

    public String getDownloadSourceStr() {
        return downloadSourceStr;
    }

    private FrameLayout rootLayout;
    private View popView;

    private String nodeId;
    private String selectType;
    public boolean selectMode;  //不弹出其他窗口，任务的时候新增
    public HashMap<String, String> pinMap;
    private String portInfo;
    private int actionType = ActionTypeEnum.YES.value();  //是否可以操作模型,默认可以操作  1 可以操作  2  不可以, 3，本地本间，不可操作，且操作栏变化
    private String pjId;

    public void setDownloadSourceStr(String downloadSourceStr) {
        this.downloadSourceStr = downloadSourceStr;
    }

    public static void LoadNativeLibs() {
        if (!mNativeLibsLoaded) {
            System.loadLibrary("gnustl_shared");
            mNativeLibsLoaded = true;
        }
    }

    static {
        LoadNativeLibs();
    }

    private PreviewPicDialog dialog;

    private void showPop() {
        if (StrUtil.isEmptyOrNull(portInfo) || !CoConfig.is_progress_shikou) {
            return;
        }
        ModelPinInfo pinInfo = JSON.parseObject(portInfo, ModelPinInfo.class);
        if (pinInfo == null || StrUtil.isEmptyOrNull(pinInfo.getPhoto())) {
            return;
        }
        AttachmentData data = JSONObject.parseObject(pinInfo.getPhoto(), AttachmentData.class);
        CoConfig.is_progress_shikou = false;
        dialog = new PreviewPicDialog.Builder(ctx).setUrl(data.getUrl()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).build();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        AttachService.bStop = false;
        ctx = this;
        if (savedInstanceState != null) {
            mbTaskExit = savedInstanceState.getBoolean("mbTaskExit");
        }
//        ShowDrawRouterUtil.routerActionSync(ctx, "pvmain", "acclosefilescan");
        setContentView(R.layout.cad_op_ac_bim);
        egLview = new EGLview(this);
        egLview.setPreserveEGLContextOnPause(true);
        osgNativeLib.bindView(this);
//        egLview = (EGLview) findViewById(R.id.surfaceGLES);
        egLview.setActivity(this);
//        egLview.initData(this);
        egLview.setOnTouchListener(this);
        egLview.setOnKeyListener(this);
        mGestureDetector = new GestureDetector(ctx, new CustomGestureDetector());
        rootLayout = (FrameLayout) findViewById(R.id.fl_root);
        File mAppDirectory = getExternalFilesDir(null);
        String fontDir = mAppDirectory.getAbsolutePath();
//        MobileApp.setFontDirectory(fontDir + "/" + ShowDrawKey.S_FONT);
//        MobileApp.setMaterialsDirectory(fontDir + "/" + ShowDrawKey.S_MATERIALS);

        new StructureTypeData(ctx);
        initView(savedInstanceState);
        getMobileSurfaceHandler().initView();
        initMode();
        if (NetworkUtil.detect(ctx))
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    initData();
//                getPosDatas();
                    return null;
                }
            }.execute();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActionType() >=ActionTypeEnum.NO.value() && !selectMode) {
                    if (osgNativeLib.getSelectState())
                        return;
//                    toSelectAction();
//                    configTvStyle(fabSelect1);
//                    closeFABMenu();
                    resetState();
                    osgNativeLib.setSelectOn(true);
                    egLview.setSelected(true);
                    ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
                    fab.setEnabled(false);
                } else {
                    if (!isFABOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                    }
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //控制界面
            if (egLview.getMode() == EGLview.DrawMode.eStardard) {
                //尝试选择
//                if (!osgNativeLib.trySelectItem(e.getX(), e.getY())) {
                    singleCLick();
//                }
            }
            return true;
        }
    }
    private void initMode() {
        if (downReceive != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(GlobalConstants.DOWNLOAD_COUNT_SERVICE_NAME);
            filter.setPriority(Integer.MAX_VALUE);
            registerReceiver(downReceive, filter);
        }
        List<BucketFileData> files = WeqiaApplication.getInstance().getDbUtil().findAllByKeyWhere(BucketFileData.class, "convertTime = '" + getIntent().getStringExtra("convertTime") + "'");
        BucketFileData bucketFileData = null;
//        files.clear();
        if (StrUtil.listNotNull(files)) {
            bucketFileData = files.get(0);
            File dir = null;
            if (StrUtil.notEmptyOrNull(getIntent().getStringExtra("versionId"))) {
                dir = new File(PathUtil.getFilePath() + "/" + MD5Util.md32(getIntent().getStringExtra("versionId")));
            }else {
                dir = new File(PathUtil.getFilePath() + "/" + title);
            }
            if(!dir.exists()){
                dir.mkdir();
            }
            boolean isOpen = true;
            List<BucketFileData> fileConvertResults = JSONArray.parseArray(bucketFileData.getFileConvertResultsString(), BucketFileData.class);
            List<BucketFileData> fileConvertResultsSenior = JSONArray.parseArray(bucketFileData.getFileConvertResultsSeniorString(), BucketFileData.class);
//            List<BucketFileData> fileConvertResults = bucketFileData.getFileConvertResults();
//            List<BucketFileData> fileConvertResultsSenior = bucketFileData.getFileConvertResultsSenior();
            openPath = dir.getAbsolutePath() + "/ProjectInfo.jsonb";
//            openPath = dir.getAbsolutePath() + "/PMBIMData.bin";
//                    List<AttachmentData> dataList = new ArrayList<>();
            for (BucketFileData buck : fileConvertResultsSenior) {
                AttachmentData data = new AttachmentData();
                String path = GlobalUtil.wrapBucketUrl(null,
                        bucketFileData.getAccountType(),
                        buck.getFileBucket(),
                        buck.getFileKey());
                data.setUrl(path);
                data.setName(buck.getFileKey().substring(buck.getFileKey().indexOf("_") + 1));
                data.setNodeId(nodeId);
                data.setDownloadType(EnumData.DownloadType.WEQIA.value());
                data.setFileSize(buck.getFileSize());
                data.setType(EnumData.AttachType.NONE.value());
                data.setModeName(title);
                data.setPjId(pjId);
                data.setVersionId(getIntent().getStringExtra("versionId"));
                if (DownloadManager.getInstance().isExistDown(data.getUrl())) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(data.getUrl());
                    if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())
                            && new File(downloadInfo.getTargetPath()).exists()
                            && downloadInfo.getState() == DownloadManager.FINISH) {
                        downNum++;
                    } else {
                        isOpen = false;
                        fileNum++;
                        fileSeniorNum++;
                        dataSeniorList.add(data);
                        dataList.add(data);
                        DownloadManager.getInstance().removeTask(data.getUrl());
                        if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())) {
                            deleteFile(new File(downloadInfo.getTargetPath()));
                        }
                    }
                } else {
                    isOpen = false;
                    fileNum++;
                    fileSeniorNum++;
                    dataSeniorList.add(data);
                    dataList.add(data);
                }
            }
            for (BucketFileData buck : fileConvertResults) {
                AttachmentData data = new AttachmentData();
                String path = GlobalUtil.wrapBucketUrl(null,
                        bucketFileData.getAccountType(),
                        buck.getFileBucket(),
                        buck.getFileKey());
                data.setUrl(path);
                data.setName(buck.getFileKey().substring(buck.getFileKey().indexOf("_") + 1));
                data.setNodeId(nodeId);
                data.setDownloadType(EnumData.DownloadType.WEQIA.value());
                data.setFileSize(buck.getFileSize());
                data.setType(EnumData.AttachType.NONE.value());
                data.setModeName(title);
                data.setPjId(pjId);
                data.setVersionId(getIntent().getStringExtra("versionId"));
                if (DownloadManager.getInstance().isExistDown(data.getUrl())) {
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(data.getUrl());
                    if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())
                            && new File(downloadInfo.getTargetPath()).exists()
                            && downloadInfo.getState() == DownloadManager.FINISH) {
                        downNum++;
                    } else {
                        isOpen = false;
                        fileNum++;
                        dataOtherList.add(data);
                        dataList.add(data);
                        DownloadManager.getInstance().removeTask(data.getUrl());
                        if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())) {
                            deleteFile(new File(downloadInfo.getTargetPath()));
                        }
                    }
                } else {
                    isOpen = false;
                    fileNum++;
                    dataOtherList.add(data);
                    dataList.add(data);
                }
            }

            if (StrUtil.listNotNull(dataList)) {
                bNeedDown = true;
                modeloadProgress.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);
                int progress = downNum / (downNum + fileNum);
                modeloadProgress.setProgress(progress);
                tvProgress.setText(progress + "%");
                if (StrUtil.listNotNull(dataSeniorList)) {
                    isSenior = true;
                    Intent intent = new Intent(ctx, AttachService.class);
                    intent.putExtra(GlobalConstants.KEY_ATTACH_OP, (Serializable) dataSeniorList);
                    ctx.startService(intent);
                } else {
                    isSenior = false;
                    loadSeniorFile();
                }

            } else {
                LoadFileTask(true);
            }
            return;

        }
        ServiceParams convParam = new ServiceParams(ComponentReqEnum.CONVERT_URL.order());
        convParam.put("versionId", getIntent().getStringExtra("versionId"));
        convParam.put("convertVersion", 1 + "");          //osg新版本传1，为了兼容老数据
        UserService.getDataFromServer(true, UrlPathEnum.MODELDOWN.getValue(), convParam, new ServiceRequester() {
            @Override
            public void onResult(ResultEx resultEx) {
//                final BucketFileData bucketFileData = resultEx.getDataObject(BucketFileData.class);
                final BucketFileData bucketFileData = JSON.parseObject(resultEx.getResult(), BucketFileData.class);
                if (bucketFileData != null) {
                    File dir = null;
                    if (StrUtil.notEmptyOrNull(getIntent().getStringExtra("versionId"))) {
                        dir = new File(PathUtil.getFilePath() + "/" + MD5Util.md32(getIntent().getStringExtra("versionId")));
                    } else {
                        dir = new File(PathUtil.getFilePath() + "/" + title);
                    }
                    if (getIntent().getStringExtra("convertTime") != null) {
                        List<BucketFileData> files = WeqiaApplication.getInstance().getDbUtil().findAllByKeyWhere(BucketFileData.class, "convertTime = '" + getIntent().getStringExtra("convertTime") + "'");
                        if (StrUtil.listIsNull(files)) {
                            if (dir.exists()) {
                                deleteFile(dir);
                            }
                        }
                    } else {

                    }
                    if (getIntent().getStringExtra("versionId") != null) {
                        List<BucketFileData> files = WeqiaApplication.getInstance().getDbUtil().findAllByKeyWhere(BucketFileData.class, "versionId = '" + getIntent().getStringExtra("versionId") + "'");
                        if (StrUtil.listIsNull(files)) {
                            if (dir.exists()) {
                                deleteFile(dir);
                            }
                        }
                    } else {

                    }

                    bucketFileData.setVersionId(getIntent().getStringExtra("versionId"));
                    bucketFileData.setConvertTime(getIntent().getStringExtra("convertTime"));
                    bucketFileData.setFileConvertResultsString(bucketFileData.getFileConvertResults().toString());
                    bucketFileData.setFileConvertResultsSeniorString(bucketFileData.getFileConvertResultsSenior().toString());
                    WeqiaApplication.getInstance().getDbUtil().save(bucketFileData);
                    final List<BucketFileData> fileConvertResults = bucketFileData.getFileConvertResults();
                    final List<BucketFileData> fileConvertResultsSenior = bucketFileData.getFileConvertResultsSenior();

                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    boolean isOpen = true;
                    openPath = dir.getAbsolutePath() + "/ProjectInfo.jsonb";
//                    openPath = dir.getAbsolutePath() + "/PMBIMData.bin";
//                    List<AttachmentData> dataList = new ArrayList<>();
                    for (BucketFileData buck : fileConvertResultsSenior) {
                        AttachmentData data = new AttachmentData();
                        String path = GlobalUtil.wrapBucketUrl(null,
                                bucketFileData.getAccountType(),
                                buck.getFileBucket(),
                                buck.getFileKey());
                        data.setUrl(path);
                        data.setRealUrl(buck.getURI());
                        data.setName(buck.getFileKey().substring(buck.getFileKey().indexOf("_") + 1));
                        data.setNodeId(nodeId);
                        data.setDownloadType(EnumData.DownloadType.WEQIA.value());
                        data.setFileSize(buck.getFileSize());
                        data.setType(EnumData.AttachType.NONE.value());
                        data.setModeName(title);
                        data.setPjId(pjId);
                        data.setVersionId(getIntent().getStringExtra("versionId"));
                        if (DownloadManager.getInstance().isExistDown(data.getUrl())) {
                            DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(data.getUrl());
                            if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())
                                    && new File(downloadInfo.getTargetPath()).exists()
                                    && downloadInfo.getState() == DownloadManager.FINISH) {
                                downNum++;
                            } else {
                                isOpen = false;
                                fileNum++;
                                fileSeniorNum++;
                                dataSeniorList.add(data);
                                dataList.add(data);
                                DownloadManager.getInstance().removeTask(data.getUrl());
                                if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())) {
                                    deleteFile(new File(downloadInfo.getTargetPath()));
                                }
                            }
                        } else {
                            isOpen = false;
                            fileNum++;
                            fileSeniorNum++;
                            dataSeniorList.add(data);
                            dataList.add(data);
                        }
                    }
                    for (BucketFileData buck : fileConvertResults) {
                        AttachmentData data = new AttachmentData();
                        String path = GlobalUtil.wrapBucketUrl(null,
                                bucketFileData.getAccountType(),
                                buck.getFileBucket(),
                                buck.getFileKey());
                        data.setUrl(path);
                        data.setRealUrl(buck.getURI());
                        data.setName(buck.getFileKey().substring(buck.getFileKey().indexOf("_") + 1));
                        data.setNodeId(nodeId);
                        data.setDownloadType(EnumData.DownloadType.WEQIA.value());
                        data.setFileSize(buck.getFileSize());
                        data.setType(EnumData.AttachType.NONE.value());
                        data.setModeName(title);
                        data.setPjId(pjId);
                        data.setVersionId(getIntent().getStringExtra("versionId"));
                        if (DownloadManager.getInstance().isExistDown(data.getUrl())) {
                            DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(data.getUrl());
                            if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())
                                    && new File(downloadInfo.getTargetPath()).exists()
                                    && downloadInfo.getState() == DownloadManager.FINISH) {
                                downNum++;
                            } else {
                                isOpen = false;
                                fileNum++;
                                dataOtherList.add(data);
                                dataList.add(data);
                                DownloadManager.getInstance().removeTask(data.getUrl());
                                if (StrUtil.notEmptyOrNull(downloadInfo.getTargetPath())) {
                                    deleteFile(new File(downloadInfo.getTargetPath()));
                                }
                            }
                        } else {
                            isOpen = false;
                            fileNum++;
                            dataOtherList.add(data);
                            dataList.add(data);
                        }
                    }

                    if (StrUtil.listNotNull(dataList)) {
                        bNeedDown = true;
                        modeloadProgress.setVisibility(View.VISIBLE);
                        tvProgress.setVisibility(View.VISIBLE);
                        int progress = downNum / (downNum + fileNum);
                        modeloadProgress.setProgress(progress);
                        tvProgress.setText(progress + "%");
                        if (StrUtil.listNotNull(dataSeniorList)) {
                            isSenior = true;
                            Intent intent = new Intent(ctx, AttachService.class);
                            intent.putExtra(GlobalConstants.KEY_ATTACH_OP, (Serializable) dataSeniorList);
                            ctx.startService(intent);
                        } else {
                            isSenior = false;
                            loadSeniorFile();
                        }

                    } else {
                        LoadFileTask(true);
                    }


//                        fileOpen(bucketFileData, attachmentData, ctx, ivIcon, isSaveLocalFile);
                } else {
                    if (bucketFileData != null && bucketFileData.getConvertStatus() != null && bucketFileData.getConvertStatus().intValue() == BucketFileData.BucketFileType.FAIL.value()) {
                        L.toastShort("模型转化失败，请联系客服QQ:626896894！");
                    } else {
                        L.toastShort("模型还没有转换完成，请稍候查看！");
                    }
                }
            }

            @Override
            public void onError(Integer errCode) {
                if (!NetworkUtil.detect(ctx)) {
                    L.toastShort(R.string.lose_network_hint);
                } else {
                    L.toastShort("模型还没有转换完成，请稍候查看！");
                }
            }
        });
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
    private void showFABMenu() {
        isFABOpen = true;
//        fabLayout1.setVisibility(View.VISIBLE);
//        fabLayout2.setVisibility(View.GONE);
//        fabLayout3.setVisibility(View.VISIBLE);
        ViewUtils.showViews(fabSelect1, fabList3);
        ViewUtils.hideView(fabLocation2);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotation(90);
//        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        fabList3.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
//        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
//        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        fabSelect1.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        if (getActionType() >=ActionTypeEnum.NO.value()) {
//            fabLayout3.setVisibility(View.GONE);
//            fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_60));

            ViewUtils.hideView(fabList3);
            fabSelect1.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        }
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(-90);
//        fabLayout1.animate().translationY(0);
        fabSelect1.animate().translationY(0);
//        fabLayout2.animate().translationY(0);
//        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
        fabList3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
//                    fabLayout1.setVisibility(View.GONE);
//                    fabLayout2.setVisibility(View.GONE);
//                    fabLayout3.setVisibility(View.GONE);
                    ViewUtils.hideViews(fabSelect1, fabLocation2, fabList3);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void initView(Bundle savedInstanceState) {
        sharedTitleView.setBackgroundColor(getResources().getColor(R.color.cad_bottom_banner));
        title = getIntent().getStringExtra(ShowDrawKey.KEY_TOP_BANNER_TITLE);
        openPath = getIntent().getStringExtra(ShowDrawKey.KEY_OPEN_PATH);
        nodeId = getIntent().getStringExtra(ShowDrawKey.KEY_OPEN_NODEID);
        portInfo = getIntent().getStringExtra(ShowDrawKey.KEY_PORT_INFO);
        selectType = getIntent().getStringExtra(ShowDrawKey.KEY_SELECT_TYPE);
        String actionStr = getIntent().getStringExtra(ShowDrawKey.KEY_CAN_ACTION);
        pjId = getIntent().getStringExtra("pjId");
        data.setName(title);
        data.setNodeId(nodeId);
        data.setModeName(title);
        data.setPjId(pjId);
        data.setProject_id(pjId);
        data.setVersionId(getIntent().getStringExtra("versionId"));
        data.setFileSize(getIntent().getStringExtra("fileSize"));         //设置下载到本地文件的一些属性
        if (StrUtil.isEmptyOrNull(actionStr)) {
            actionType = ActionTypeEnum.NO.value();
        } else {
            actionType = Integer.parseInt(actionStr);
        }
//        if (WeqiaApplication.getInstance().isTourist()) {
//            actionType = ActionTypeEnum.NO.value();
//        }
        nodeType = getIntent().getStringExtra(ShowDrawKey.KEY_NODE_TYPE);
        if (StrUtil.notEmptyOrNull(nodeType) && nodeType.equals("2")) {
            //文件列表的模型一律不能操作
            actionType = ActionTypeEnum.NO.value();
        }
        tvTopSel = (TextView) findViewById(R.id.tv_top_floor);
        if (StrUtil.notEmptyOrNull(getIntent().getStringExtra(ShowDrawKey.KEY_SELECT_MODE)))
            selectMode = true;
        else
            selectMode = false;
//        L.e("nodeId == " + nodeId);
        ViewUtils.bindClickListenerOnViews(this, this, R.id.tv_top_floor, R.id.tv_top_gradle, R.id.tv_location, R.id.tv_select, R.id.tv_msg);
//        if (StrUtil.isEmptyOrNull(openPath))
//            return;
        mShouldLoadFile = true;
        long mobileSurfacePointer = 0;
/*        mSurfaceView = new AndroidUserMobileSurfaceView(getApplicationContext(), this, MOBILE_SURFACE_GUI_ID, mobileSurfacePointer) {
            @Override
            public void singleCLick() {
                if (sharedTitleView.getVisibility() == View.VISIBLE)
                    ViewUtils.hideViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
                else
                    ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
            }
        };
        mSurfaceView.setActivity(this);*/


        fabSelect1 = (TextView) findViewById(R.id.tv_select);
        fabLocation2 = (TextView) findViewById(R.id.tv_location);
        fabList3 = (TextView) findViewById(R.id.tv_msg);
        fab = (TextView) findViewById(R.id.fab);
        if (ShowDraw.isSkp(title)) {
            actionType = ActionTypeEnum.NO.value();          //如果模型是skp后缀的没有操作权限
            fab.setVisibility(View.GONE);
        }
//        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
//        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
//        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        modeloadProgress = (ProgressBar) findViewById(R.id.modeload_progress);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        if (getActionType() >= ActionTypeEnum.NO.value() && !selectMode) {
            fab.setText("选择");
            fab.setBackgroundResource(R.drawable.shape_tv_select);
        }
        if (savedInstanceState != null) {
            mobileSurfacePointer = savedInstanceState.getLong(MOBILE_SURFACE_POINTER_KEY);
            mShouldLoadFile = false;
        } else {
/*            getMobileSurfaceHandler().getDlg(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mSurfaceView.cancelLoading();
                    boolean state = mTask.cancel(true);
//                    CADApplication.addRf(WorkEnum.RefeshKey.HSF_REFRESH);
                    if (showDrawInterface != null)
                        showDrawInterface.loadEnd(openPath);
                }
            }).show();             //先取消打开模型的dialog
            showPop();*/
            showPop();
        }
        initDialogView();
    }

    private final int REQUEST_MEDIA_PROJECTION = 18;

    private boolean notModeFile() {
        return StrUtil.notEmptyOrNull(nodeType) && nodeType.equalsIgnoreCase("2");
    }

    private List<DrawerTreeData> selectFloor = new ArrayList<>();


    private void initDialogView() {
        floorDialog = new ModelGladleDialog(ctx, true) {
            @Override
            public void onSureClickLintener() {
                //当重新选择楼层的时候，要清空上次构建对话框显示的缓存数据
                gradleDialog.getNoRepeatDatas().clear();
                gradleDialog.getVisibleDarwerDatas().clear();
                selectFloor.clear();

                for (DrawerTreeData data : floorDialog.getSortDarwerDatas()) {
                    if (data.isbSelected()) {
                        //配置一级列表下的所有子项都选中
//                        data.setbSelected(true);
                        selectFloor.add(data);
                        if (data.getLevel() == 1) {
                            data.setbSelected(true);
                        }
                    }
                    showFloorCADFromSelect(data, data.isbSelected());
                }

/*                if (StrUtil.listIsNull(selectFloor)) {
                    for (DrawerTreeData data : floorDialog.getSortDarwerDatas()) {
                        showFloorCADFromSelect(data, data.isbSelected());
                    }
                }*/
                if (getActionType() == ActionTypeEnum.YES.value()) {
                    getPosDatas(true);
                }
            }
        };

        gradleDialog = new ModelGladleDialog(ctx, false) {
            @Override
            public void onSureClickLintener() {
                List<DrawerTreeData> showGredleList = gradleDialog.getVisibleDarwerDatas();
                if (StrUtil.listNotNull(showGredleList)) {
                    for (DrawerTreeData leaf : showGredleList) {
                        for (DrawerTreeData rootData : mobileSurfaceHandler.getRootDarwerDatas()) {
                            if (rootData.isbSelected()) {
                                if (leaf.getLevel() == 2) {
                                    if (leaf.getTypeIndex() == 1001) {
                                        osgNativeLib.setLayerShow(rootData.getTypeIndex(), 1001, leaf.isbSelected());
                                        continue;
                                    }
                                    for (int i = 0; i < leaf.getChildDatas().size(); i++) {
                                        osgNativeLib.setLayerShow(rootData.getTypeIndex(), leaf.getChildDatas().get(i).getTypeIndex(), leaf.isbSelected());
                                    }
                                } else {
                                    osgNativeLib.setLayerShow(rootData.getTypeIndex(), leaf.getTypeIndex(), leaf.isbSelected());
                                }
                            }
                        }
                    }
                } else {
                    for (DrawerTreeData data : mobileSurfaceHandler.getRootDarwerDatas()) {
                        showFloorCADFromSelect(data, false);
                    }
                }
            }
        };
    }

    /**
     * @param item  被选中的楼层
     * @param bShow 是否展示
     */
    private void showFloorCADFromSelect(DrawerTreeData item, boolean bShow) {
        if (item.getLevel() == 1) {
            osgNativeLib.setLayerShow(item.getTypeIndex(), -1, bShow);
        } else if (item.getLevel() == 2) {
            if (item.getTypeIndex() == 1001) {
                osgNativeLib.setLayerShow(item.getParentData().getTypeIndex(), 1001, bShow);
                egLview.requestRender();
                return;
            }
            for (int i = 0; i < item.getChildDatas().size(); i++) {
                osgNativeLib.setLayerShow(item.getParentData().getTypeIndex(), item.getChildDatas().get(i).getTypeIndex(), bShow);
            }
        } else if (item.getLevel() == 0) {
            return;
        } else {
            osgNativeLib.setLayerShow(item.getSuperParentData().getTypeIndex(), item.getTypeIndex(), bShow);
        }
        egLview.requestRender();
    }

    public void toSelectFloorAction(View view) {
        //楼层选择
        floorDialog.setSortDarwerDatas(mobileSurfaceHandler.getRootDarwerDatas());
        floorDialog.showPopSaixuan(view);
    }

    public void toSelectGradleAction(View view) {
        // 构件选择
        gradleDialog.setSortDarwerDatas(mobileSurfaceHandler.getLeafDarwerDatas());
        gradleDialog.showPopSaixuan(view);
    }


    private void initData() {
        SharedPreferences preferences = getSharedPreferences("CADShow", MODE_PRIVATE);
        Long lastCheckTime = preferences.getLong(LAST_TIME_KEY, 0);
        long dayOver = TimeUtils.getDayOver(-1 * FREQUENCY_OF_CHECK_UPDATE);
        if (lastCheckTime != 0 && lastCheckTime >= dayOver) {
            L.i("今天已经更新过了，不用来更新了");
            return;
        }
        if (ShowDraw.downConfig == null) {
            L.i("没有配置下载需要的资料");
            return;
        }
        RequestParams params = new RequestParams();
        params.put("itype", ShowDraw.downConfig.getGzItype());
        params.put("appKey", ShowDraw.downConfig.getAppKey());
        params.put("s", "1");
        params.put("sourceType", ShowDraw.downConfig.getSourceType());
        params = HttpUtil.getInstance().buildParam(params, "BIM");
        ShowDraw.downConfig.getSeverIp();
        String resp = (String) HttpUtil.getInstance().postSync(ShowDraw.downConfig.getSeverIp() + "/gateWay.do", params);
        if (StrUtil.notEmptyOrNull(resp) && resp.contains("object")) {
            String objectStr = JSONObject.parseObject(resp).getString("object");
            SharedPreferences.Editor editor = getSharedPreferences("CADShow", MODE_PRIVATE).edit();
            editor.putString(STRUCTURE_TYPE_DATA, objectStr);
            editor.putLong(LAST_TIME_KEY, System.currentTimeMillis());
            editor.commit();
            HashMap<String, String> map = JSONObject.parseObject(objectStr, HashMap.class);
            if (map != null) {
                StructureTypeData.setStructureTypes(map);
            }
        } else {
            L.e("获取下载数据失败");
        }
    }

    private List<ModelPinInfo> pinDatas;

    public List<ModelPinInfo> getPinDatas() {
        return pinDatas;
    }

    public void fileLoadFinish() {
        isFileLoadFinish = true;
/*        if (StrUtil.listNotNull(dataOtherList)) {
            Intent otherIntent = new Intent(ctx, AttachService.class);
            otherIntent.putExtra(GlobalConstants.KEY_ATTACH_OP, (Serializable) dataOtherList);
            ctx.startService(otherIntent);
        }*/
        if (StrUtil.isEmptyOrNull(portInfo)) {
            osgNativeLib.setHoopsViewMode(0, false);
        } else {
            L.e("视口数据----" + portInfo);
            ModelPinInfo pinInfo = JSON.parseObject(portInfo, ModelPinInfo.class);
            if (pinInfo != null) {
                addPinView(pinInfo);
                if (StrUtil.notEmptyOrNull(CoConfig.getQrPjId())) {
                    //扫面二维码进去的模型预览界面
                    if (StrUtil.notEmptyOrNull(pinInfo.getViewInfo())) {
                        //如果视口数据不为空且是项目成员，进行视口操作
                        osgNativeLib.setViewPortInfo(pinInfo.getViewInfo());
                    } else {
                        osgNativeLib.setHoopsViewMode(0, false);
                    }
                } else {
                    //正常进去模型预览界面
                    if (StrUtil.notEmptyOrNull(pinInfo.getViewInfo())) {
                        //如果视口数据不为空，进行视口操作
                        osgNativeLib.setViewPortInfo(pinInfo.getViewInfo());
                    } else {
                        osgNativeLib.setHoopsViewMode(0, false);
                    }
                }
            }
        }
        egLview.requestRender();
    }

    private void getPosDatas(final boolean isAddAll) {
        if (!NetworkUtil.detect(ctx)) {
            L.e("网络错误，直接返回");
            return;
        }
        if (notModeFile())
            return;
        if (StrUtil.isEmptyOrNull(nodeId)) {
            L.e("nodeId为空，不请求接口");
            return;
        }
        ServiceParams params = new ServiceParams(3702);
        if (StrUtil.notEmptyOrNull(nodeId)) {
            params.put("nodeId", nodeId);
        }
        /**
         *floorNames
         * 根据楼层查询标注点
         */
//        dataMap.put("floorNames", "3702");
        StringBuffer sbInfo = new StringBuffer();
        StringBuffer sbInfoId = new StringBuffer();
        if (selectFloor != null && selectFloor.size() > 0) {
            for (DrawerTreeData floor : selectFloor) {
                String floorName = floor.getTypeString();
                String floorId = floor.getTypeIndex() + "";
                if (floorName.endsWith("楼层")) {
                    floorName = floorName.substring(0, floorName.length() - 2);
                }
                sbInfo.append(floorName).append(",");
                sbInfoId.append(floorId).append(",");
            }
        }
        String floorNames = sbInfo.toString();
        String floorIds = sbInfoId.toString();
        if (StrUtil.notEmptyOrNull(floorNames)) {
            /**
             *去掉末尾逗号
             */
            if (floorNames.endsWith(",")) {
                floorNames = floorNames.substring(0, floorNames.length() - 1);
            }
            params.put("floorNames", floorNames);
        }
        if (StrUtil.notEmptyOrNull(floorIds)) {
            /**
             *去掉末尾逗号
             */
            if (floorIds.endsWith(",")) {
                floorIds = floorIds.substring(0, floorIds.length() - 1);
            }
            params.put("floorIds", floorIds);
        }

        UserService.getDataFromServer(params, new ServiceRequester() {

            @Override
            public void onResult(ResultEx resultEx) {
                //刷新标记前删除所有标记
                if (isAddAll) {
                    osgNativeLib.deleteAllMarkUp();
                }
                if (resultEx.isSuccess()) {
                    pinDatas = resultEx.getDataArray(ModelPinInfo.class);
                    if (StrUtil.listNotNull(pinDatas)) {
                        if (isAddAll) {
                            for (ModelPinInfo pinInfo : pinDatas) {
                                addPinView(pinInfo);
                            }
                        }
                    }
                    Message message = new Message();
                    message.what = 101;
                    getMobileSurfaceHandler().handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Throwable t, String strMsg) {
                L.i(strMsg.toString());
                super.onFailure(t, strMsg);
            }
        });
    }

    private void addPinView(ModelPinInfo pinInfo) {
        if (StrUtil.isEmptyOrNull(pinInfo.getType())) {
            return;
        }
        int pinType = Integer.parseInt(pinInfo.getType());
        if (pinType == ModelPinInfo.ModePinType.SEEPOS.value()) {
            L.e("视口");
            if (StrUtil.isEmptyOrNull(pinInfo.getFloorName())) {
                return;
            }
            if (StrUtil.isEmptyOrNull(pinInfo.getInfo())) {
                return;
            }
            if (StrUtil.isEmptyOrNull(pinInfo.getOrderId())) {
                pinInfo.setOrderId(1 + "");
//                return;
            }
//            mSurfaceView.setEntMark(pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
            if (pinInfo.getFloorId() == 0) {
                osgNativeLib.setEntMark(-1, pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
            } else {
                osgNativeLib.setEntMark(pinInfo.getFloorId(), pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
            }
            egLview.requestRender();

            return;
        } else if (pinType == ModelPinInfo.ModePinType.MARK.value()) {

            if (StrUtil.isEmptyOrNull(pinInfo.getFloorName())) {
                return;
            }
            if (StrUtil.isEmptyOrNull(pinInfo.getInfo())) {
                return;
            }
            L.e("标注信息" + pinInfo.toString());
            if (StrUtil.isEmptyOrNull(pinInfo.getOrderId())) {
                pinInfo.setOrderId(1 + "");
//                return;
            }
            if (pinInfo.getFloorId() == 0) {
                osgNativeLib.setEntMark(-1, pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
            } else {
                osgNativeLib.setEntMark(pinInfo.getFloorId(), pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
            }
            egLview.requestRender();
//            if (StrUtil.notEmptyOrNull(pinInfo.getInfo())) {
//                mSurfaceView.addMarkUpFromData(pinInfo.getInfo().toString(), pinInfo.getFloorName(), pinInfo.getText());
//            }
        } else if (pinType == ModelPinInfo.ModePinType.COMPONENT.value()) {
            if (StrUtil.isEmptyOrNull(pinInfo.getFloorName())) {
                return;
            }
            if (StrUtil.isEmptyOrNull(pinInfo.getInfo())) {
                return;
            }

            if (StrUtil.isEmptyOrNull(pinInfo.getOrderId()) || pinInfo.getOrderId().equals("0")) {
                pinInfo.setOrderId(1 + "");
//                return;
            }
            if (pinInfo.isQr()) {
               // L.e("1组件信息" + pinInfo.getOrderId());
                if (pinInfo.getFloorId() == 0) {
                    osgNativeLib.highLightEnt(-1, pinInfo.getFloorName(), pinInfo.getInfo());
                } else {
                    osgNativeLib.highLightEnt(pinInfo.getFloorId(), pinInfo.getFloorName(), pinInfo.getInfo());
                }
            } else {
                //L.e("2组件信息" + pinInfo.getOrderId());
                if (pinInfo.getFloorId() == 0) {
                    osgNativeLib.setEntMark(-1, pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
                } else {
                    osgNativeLib.setEntMark(pinInfo.getFloorId(), pinInfo.getFloorName(), pinInfo.getInfo(), pinInfo.getOrderId());
                }
            }
            egLview.requestRender();
        }
    }

    public MobileSurfaceHandler getMobileSurfaceHandler() {
        if (mobileSurfaceHandler == null)
            mobileSurfaceHandler = new MobileSurfaceHandler(ctx, egLview, openPath, pinMap) {
                @Override
                public void refreshPopView() {

                }
            };
        return mobileSurfaceHandler;
    }

    public void backClick() {
//        mSurfaceView.releaseView();
        if (showDrawInterface != null)
            showDrawInterface.loadEnd(openPath);
//        CADApplication.addRf(WorkEnum.RefeshKey.HSF_REFRESH);
        if (StrUtil.notEmptyOrNull(getDownloadSourceStr())) {
            DownloadManager.getInstance().pauseTask(getDownloadSourceStr());
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        getMobileSurfaceHandler().onBackPressed();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof RefreshEvent) {
            RefreshEvent tmp = (RefreshEvent) event;
            getMobileSurfaceHandler().onMessageEvent(tmp);
        }
        if (event != null && event.equals("getPosDatas")) {
            if (getActionType() == ActionTypeEnum.YES.value()) {
                getPosDatas(false);
                String orderId = WPf.getInstance().get("orderId", String.class);
                if (StrUtil.isEmptyOrNull(orderId) || orderId.equals("0")) {
                    return;
                }
//                mSurfaceView.setEntMark(mSurfaceView.getStrFloorName(), mSurfaceView.getStrHandle(), orderId + "");
                osgNativeLib.setEntMark(iFloorId, strFloorName, strInfo, orderId + "");
                egLview.requestRender();
            }
        }
        if (event != null && event.equals("refreshList")) {
            if (getActionType() == ActionTypeEnum.YES.value()) {
                getPosDatas(false);
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(RefreshObjEvent event) {
//        getMobileSurfaceHandler().onMessageEvent(event);
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putLong(MOBILE_SURFACE_POINTER_KEY, mSurfaceView.getSurfacePointer());
        savedInstanceState.putBoolean("mbTaskExit", mbTaskExit);
    }

/*
    @Override
    public void onSurfaceBind(boolean bindRet) {
        if (bindRet == false) {
            L.toastLong("C++ bind() failed to initialize");
            return;
        }
        if (mShouldLoadFile) {
            mbTaskExit = false;
            mTask = new LoadFileAsyncTask(ctx, mSurfaceView, openPath);
            mTask.execute(openPath);
            mShouldLoadFile = false;
        }
    }
*/

    public LoadFileAsyncTask getmTask() {
        return mTask;
    }

/*    @Override
    public void onShowKeyboard() {

    }

    @Override
    public void eraseKeyboardTriggerField() {

    }*/

    public void onShowPerformanceTestResult(float fps) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Performance Test")
                .setMessage("FPS = " + String.format("%.2f", fps))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infater = getMenuInflater();
        infater.inflate(R.menu.cad_op_menu_right_text_cancel_save, menu);
        cancelItem = menu.findItem(R.id.right_cancel);
        saveItem = menu.findItem(R.id.right_save);
        delItem = menu.findItem(R.id.right_del);
        shareItem = menu.findItem(R.id.right_share);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.right_cancel) {
            toCancelAction();
        } else if (i == R.id.right_save) {
            toSaveAction();
        } else if (i == R.id.right_del) {
            toDelAction();
        } else if (i == R.id.right_share) {
            toShareAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.tv_top_floor) {
            toSelectFloorAction(v);
        } else if (viewId == R.id.tv_top_gradle) {
            toSelectGradleAction(v);
        } else if (viewId == R.id.tv_location) {
            if (osgNativeLib.getShowFloorCount() != 1) {
                SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
                LayoutInflater mInflater = LayoutInflater.from(ctx);
                View view = mInflater.inflate(R.layout.cad_op_view_new_fold, null);
                builder.setMessage("请选择楼层数为1");
                builder.showBar(false);
                builder.setTitleAttr(true, null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setContentView(view);
                builder.create().show();
                return;
            }

            if (egLview.getMode() == EGLview.DrawMode.eMarkUp)
                return;
            toLocationAction();
            configTvStyle(fabLocation2);
            closeFABMenu();
            fab.setEnabled(false);
        } else if (viewId == R.id.tv_select) {
            if (osgNativeLib.getSelectState())
                return;
            toSelectAction();
            configTvStyle(fabSelect1);
            closeFABMenu();
            fab.setEnabled(false);
        } else if (viewId == R.id.tv_msg) {
            mobileSurfaceHandler.refreshLeftDrawerVIew();
            closeFABMenu();
        }
    }

    public boolean isSendProgerss;

    public void toSendProgress() {
        pinMap = new HashMap<>();
        pinMap.put("type", "0");
        final String tmpPath = PathUtil.getPicturePath() + "/tmp.png";
        String editPath = PathUtil.getPicturePath() + "/" + SystemClock.uptimeMillis() + "mode.png";
        deleteFile(new File(tmpPath));
        egLview.outPutImage(tmpPath);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 涂鸦参数
        GraffitiParams params = new GraffitiParams();
        // 图片路径
        params.mImagePath = tmpPath;
        params.mSavePath = editPath;
        // 初始画笔大小
        params.mPaintSize = GraffitiActivity.DEFAULE_FONTSIZE;
        // 启动涂鸦页面
        GraffitiActivity.startActivityForResult(ctx, params, 101);
        isSendProgerss = false;
    }

    public void addOpView(float x, float y, final HashMap<String, String> dataMap) {
        //删除上一个弹框
        rootLayout.removeView(popView);

        pinMap = dataMap;

        if (selectMode) {
//            ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
            cancelItem.setVisible(true);
            saveItem.setVisible(true);
            ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
            return;
        }

        cancelItem.setVisible(true);
        saveItem.setVisible(false);
        delItem.setVisible(false);
//        shareItem.setVisible(false);
        popView = LayoutInflater.from(ctx).inflate(R.layout.model_op_popup_window_view, null);
        popView.setAlpha(0.9f);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, ShowDrawUtil.dip2px(ctx, 35));
        params.leftMargin = ShowDrawUtil.px2dip(ctx, x);
        if (params.leftMargin > 248) {
            params.leftMargin = 248;
        }
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        params.topMargin = (int) (height - y);//ShowDrawUtil.px2dip(ctx, y);        //之前hoops坐标原点是左上角，现在osg原点是在左下角，因此要转换一下
        rootLayout.addView(popView, params);

        boolean isComponent = false;
        final String tmpType = pinMap.get("type");
        if (tmpType != null) {
            int pinType = Integer.parseInt(tmpType);
            if (pinType == ModelPinInfo.ModePinType.SEEPOS.value()) {
            } else if (pinType == ModelPinInfo.ModePinType.MARK.value()) {
            } else if (pinType == ModelPinInfo.ModePinType.COMPONENT.value()) {
                isComponent = true;
            }
        }

        /**
         *定位，隐藏查询，历史信息，按钮
         */
        if (fabLocation2.isSelected()) {
            ViewUtils.hideViews(popView, R.id.bt_seedetail, R.id.bt_history);
        }

        if (fabSelect1.isSelected() && !isComponent) {
            ViewUtils.hideViews(popView, R.id.bt_seedetail);
        }
        if (getActionType() >= ActionTypeEnum.NO.value()) {
            ViewUtils.hideViews(popView, R.id.bt_task, R.id.bt_history, R.id.bt_addinfo, R.id.dv_addinfo, R.id.dv_task, R.id.dv_seedetail);
            TextView tv = popView.findViewById(R.id.bt_seedetail);
            tv.setBackgroundResource(R.drawable.cad_op_bg_popwindow_round);
        }
        ViewUtils.bindClickListenerOnViews(popView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                pinMap.put("nodeId", nodeId);
                if (i == R.id.bt_seedetail) {
                    seeDetail(dataMap);
                } else if (i == R.id.bt_task) {
                    findViewById(R.id.bt_task).setEnabled(false);
                    newTaskDo();
                } else if (i == R.id.bt_addinfo) {
//                    HashMap<String, String> tmap = new HashMap<String, String>();
                    pinMap.put("acptype", "addinfo");
                    pinMap.put("viewInfo", osgNativeLib.getViewPortInfo());
                    ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodel", pinMap);
                } else if (i == R.id.bt_history) {
                    /**
                     *构件需要根据handle，查到这个构件的mpId
                     * 如果不是构件是点，需要根据handle拿到对应的点
                     */
                    String handle = dataMap.get("handle");
                    String mpId = handle;
                    boolean isMark = false;
                    final String tmpType = pinMap.get("type");
                    if (tmpType != null) {
                        int pinType = Integer.parseInt(tmpType);

                        if (pinType == ModelPinInfo.ModePinType.SEEPOS.value()) {

                        } else if (pinType == ModelPinInfo.ModePinType.MARK.value()) {
                            /**
                             *找到对应的点
                             */
                            if (StrUtil.listNotNull(pinDatas)) {
                                String name = dataMap.get("name");
                                try {
                                    Integer index = Integer.parseInt(name);
                                    if (index != null) {
                                        index = index - 1;
                                        if (index < pinDatas.size()) {
                                            ModelPinInfo pinInfo = pinDatas.get(index);
                                            if (pinInfo != null && StrUtil.notEmptyOrNull(pinInfo.getMpId())) {
                                                mpId = pinInfo.getMpId();
                                                isMark = true;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (pinType == ModelPinInfo.ModePinType.COMPONENT.value()) {
                        }
                    }
                    if (isMark) {
                        pinMap.put("mpId", mpId);
                    } else {
                        pinMap.put("handle", handle);
                    }
                    pinMap.put("acptype", "history");
                    ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodel", pinMap);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *点击后去掉取消状态
                         */
                        toCancelAction();
                    }
                }, 1000);

            }
        }, R.id.bt_seedetail, R.id.bt_task, R.id.bt_addinfo, R.id.bt_history);
    }

    public void seeDetail(HashMap<String, String> dataMap) {
        pinMap = dataMap;
        pinMap.put("nodeId", nodeId);
        pinMap.put("acptype", "search");
        String handle = dataMap.get("handle");
        if (StrUtil.notEmptyOrNull(handle)) {
//            HashMap<String, String> tmap = new HashMap<String, String>();
//            HashMap<String, String> handInfo = getHandleInfoMap(dataMap.get("handle"));
//            if (handInfo != null && handInfo.size() != 0)
//                tmap.putAll(handInfo);
/*            String components = osgNativeLib.GetComponents(Integer.parseInt(dataMap.get("floorId")), dataMap.get("handle"));
            if (StrUtil.notEmptyOrNull(components)) {
                List<ComponentData> list = JSONArray.parseArray(components,ComponentData.class);
                if (StrUtil.listNotNull(list)) {
                    for (ComponentData componentData : list) {
                        tmap.put(componentData.getName(), componentData.getValue());
                    }
                }
            }*/
            ServiceParams serviceParams = new ServiceParams(ComponentReqEnum.COMPONENT_INFO.order());
            serviceParams.put("versionId", getIntent().getStringExtra("versionId"));
            serviceParams.put("floorId", dataMap.get("floorId"));
            serviceParams.put("handle", dataMap.get("handle"));
            UserService.getDataFromServer(serviceParams, new ServiceRequester() {
                @Override
                public void onResult(ResultEx resultEx) {
                    if (resultEx.isSuccess()) {
                        HashMap<String, String> tmap = new HashMap<String, String>();
                        List<ComponentData> list = resultEx.getDataArray(ComponentData.class);
                        if (StrUtil.listNotNull(list)) {
                            for (ComponentData componentData : list) {
                                tmap.put(componentData.getStrName(), componentData.getStrValue());
                            }
                        }
                        pinMap.put("baseMap", JSONObject.toJSONString(tmap));
                        ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodel", pinMap);
                    }
                }
            });

        }
//        ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodel", pinMap);
    }

    private void newTaskDo() {
        final String posPath = PathUtil.getPicturePath() + "/" + nodeId + ".png";
//        osgNativeLib.outPutImage(posPath);
        new Thread(){
            @Override
            public void run() {
                super.run();
                egLview.outPutImage(posPath);          //截图放在子线程，优化跳转的时间
            }
        }.start();
        toPublishTask(posPath);
    }

    private void toPublishTask(String posPath) {
        /**
         *修复选择模型添加视口，没有nodeId问题
         */
        if (!pinMap.containsKey("nodeId")) {
            pinMap.put("nodeId", nodeId);
        }

        pinMap.put("posfile", posPath);
        pinMap.put("selectType", selectType);
        pinMap.put("pjId", pjId);

        pinMap.put("viewInfo", osgNativeLib.getViewPortInfo());

        if (selectMode) {
            pinMap.put("selectMode", "1");
            ShowDrawUtil.ronterActionSync(ctx, null, "pvapproval", "acnewapproval", pinMap);
        }else {
/*            final String[] list = {"安全任务","质量任务","进度任务","其他任务"};
            taskDialog = DialogUtil.initLongClickDialog(ctx, "任务类型", list, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    taskDialog.dismiss();
                    String key = (String) view.getTag(-1);
                    pinMap.put("taskType", key);
                    ShowDrawUtil.ronterActionSync(ctx, null, "pvtask", "acnewtask", pinMap);
                }
            });
            taskDialog.show();*/
//            TaskApprovalUtil.toTask(MobileSurfaceActivity.this, pjId, pinMap);
        }
        /**
         *选择必须关闭模型界面，不是选择可以不关闭
         */
        if (selectMode) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    mSurfaceView.releaseView();         注释之前的释放view
                }
            }, 800);
            this.finish();
        }
    }

    private void toPublicProgress(String posPath) {
        isSendProgerss = false;
        if (!pinMap.containsKey("nodeId")) {
            pinMap.put("nodeId", nodeId);
        }
        pinMap.put("posfile", posPath);
        pinMap.put("nodeType", nodeType);
        pinMap.put("pjId", pjId);
        pinMap.put("viewInfo", osgNativeLib.getViewPortInfo());
        ShowDrawUtil.ronterActionSync(ctx, null, "pvmain", "acpublicprogress", pinMap);
    }


    private void configTvStyle(TextView tv) {
        if (!tv.isSelected()) {
            fabSelect1.setSelected(false);
            fabLocation2.setSelected(false);
        }
        tv.setSelected(!tv.isSelected());
    }

    public void toLocationAction() {
        //  定位操作
        resetState();
        egLview.setMode(EGLview.DrawMode.eMarkUp);
        cancelItem.setVisible(true);
        tvTopSel.setEnabled(false);
        ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
    }

    public void toSelectAction() {
        //  选择操作
        resetState();
        osgNativeLib.setSelectOn(true);
        cancelItem.setVisible(true);
        ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
    }

    //回复操作到默认状态
    public void resetState() {
        EGLview.DrawMode mode = egLview.getMode();
        //定位
        if (EGLview.DrawMode.eMarkUp == mode) {
            toCancelMarkUp();
        }
        //选择
        if (osgNativeLib.getSelectState()) {
            toCancelSelect();
        }
    }

    public void hideButtons() {
//        fabSelect1.setVisibility(View.GONE);
//        fabLocation2.setVisibility(View.GONE);
//        fabList3.setVisibility(View.GONE);
//        fab.setVisibility(View.GONE);
        ViewUtils.hideViews(fab, fabSelect1, fabLocation2, fabList3);
    }

    public void showButtons() {
        if (getActionType() >= ActionTypeEnum.NO.value()) {
            ViewUtils.showView(fabSelect1);
            return;
        }
        //非视口进入显示
//        fabSelect1.setVisibility(View.VISIBLE);
//        fabLocation2.setVisibility(View.VISIBLE);
//        fabList3.setVisibility(View.VISIBLE);
//        fab.setVisibility(View.VISIBLE);
        ViewUtils.hideViews(fabSelect1, fabLocation2, fabList3);
        ViewUtils.showView(fab);

    }

    public void toCancelMarkUp() {
        osgNativeLib.deleteCurMarkUp();
        egLview.setMode(EGLview.DrawMode.eStardard);
        egLview.setHasAddMarkUp(false);
        rootLayout.removeView(popView);
        cancelItem.setVisible(false);
        fabLocation2.setSelected(false);
        tvTopSel.setEnabled(true);
        fab.setEnabled(true);
    }

    public void toCancelSelect() {
        osgNativeLib.setSelectOn(false);
        rootLayout.removeView(popView);
        cancelItem.setVisible(false);
        saveItem.setVisible(false);
        fabSelect1.setSelected(false);
        tvTopSel.setEnabled(true);
        fab.setEnabled(true);
    }

    public void toCancelAction() {
        EGLview.DrawMode mode = egLview.getMode();
        //定位
        if (EGLview.DrawMode.eMarkUp == mode) {
            toCancelMarkUp();
            return;
        }
        //选择
        if (osgNativeLib.getSelectState()) {
            toCancelSelect();
            return;
        }

        if (EGLview.DrawMode.eCullPlanes == mode) {
            osgNativeLib.cancelCullPlanes();
            egLview.requestRender();
        }
        egLview.setMode(EGLview.DrawMode.eStardard);
        refreshMenuDimensionItem(false);
        ViewUtils.hideViews(ctx.sharedTitleView, getMobileSurfaceHandler().getRcBottom());
        showButtons();
    }


    protected void toSaveAction() {

        rightMenu.setEnabled(false);

        if (EGLview.DrawMode.eMarkUp == egLview.getMode() || osgNativeLib.getSelectState()) {
            if (pinMap != null) {
                newTaskDo();
            }
            return;
        }
        EGLview.DrawMode mode = egLview.getMode();
        if (EGLview.DrawMode.eCullPlanes == mode) {
            osgNativeLib.insertCullPlanes();
            egLview.setMode(EGLview.DrawMode.eStardard);
            refreshMenuDimensionItem(false);
            ViewUtils.hideViews(ctx.sharedTitleView, getMobileSurfaceHandler().getRcBottom());
            egLview.requestRender();
        }

        showButtons();
    }

    private void toDelAction() {
        // 标注分享删除操作
        osgNativeLib.deleteSelectDimText();
    }

    private void toShareAction() {
        //标注分享-----分享操作
//        EGLview.DrawMode mode = egLview.getMode();
//        if (EGLview.DrawMode.eDimText == mode) {
//
//            String strPath = PathUtil.getFilePath() + "/hsf/image_output.png";
//            File file = new File(strPath);
//            if (file.isFile() && file.exists()) {
//                file.delete();
//            }
//            if (osgNativeLib.outPutImage(strPath)) {
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(strPath)));
//                shareIntent.setType("*/*");
//                ctx.startActivity(Intent.createChooser(shareIntent, "发送到"));
//            }
//        }
        HashMap<String, String> map = new HashMap<>();
        map.put("nodeId", nodeId);
        map.put("pjId", pjId);
        map.put("nodeType", nodeType);
        map.put("name", title);
//        RouterUtil.routerActionSync(ctx, "pvmain", "acsharemode", map);
//        ModeFileHandle.shareModeFileAction(ctx,nodeId, pjId, nodeType, title);
    }

    public void refreshMenuDimensionItem(boolean isShow) {
        cancelItem.setVisible(isShow);
        saveItem.setVisible(isShow);
    }

    public void refreshMenuShareItem(boolean isShow) {
        delItem.setVisible(isShow);
        shareItem.setVisible(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        osgNativeLib.setEnableFileLoad(false);
        osgNativeLib.release();
        if (downReceive != null) {
            unregisterReceiver(downReceive);
        }
//        toCancelAction();
        ctx.stopService(new Intent(ctx, AttachService.class));
        int removenum = 0;
        if (count < fileNum) {
            AttachService.bStop = true;
            Iterator<AttachmentData> iterator = dataList.iterator();
            while(iterator.hasNext()){
                AttachmentData data = iterator.next();
                for (String url : urls) {
                    if (url.equals(data.getUrl())) {
                        iterator.remove();
                    }
                }
            }
            for (AttachmentData data : dataList) {
                DownloadManager.getInstance().removeTask(data.getUrl());
                removenum++;
            }
        }
        CoConfig.setQrPjId(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (egLview != null) {
            egLview.onResume();
        }

//        MobclickAgent.onResume(this);
        resetState();
//        mSurfaceView.clearTouches();
        /**
         *通任务打开的一个新视口，把新的视口信息更新显示到界面
         */
        if (isFileLoadFinish && StrUtil.notEmptyOrNull(viewInfo)) {
            /**
             *关闭侧边栏
             */
            mobileSurfaceHandler.closeDrawer();
            osgNativeLib.setViewPortInfo(viewInfo);
            viewInfo = "";
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                egLview.requestRender();
            }
        }, 500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                egLview.requestRender();
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (egLview != null) {
            egLview.onPause();
        }
//        MobclickAgent.onPause(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getMobileSurfaceHandler().getItemList().clear();
        getMobileSurfaceHandler().initBottom();
        if (floorDialog != null && floorDialog.getPopupWindow() != null) {
            floorDialog.getPopupWindow().dismiss();
        }
        if (gradleDialog != null && gradleDialog.getPopupWindow() != null) {
            gradleDialog.getPopupWindow().dismiss();
        }
        initDialogView();
    }

    @Override
    public void optionMenuPrepared() {
        super.optionMenuPrepared();
//        rightMenu.setIcon(R.drawable.cad_op_big);
    }


    @Override
    public void rightClick() {
//        ViewUtils.hideViews(sharedTitleView, mobileSurfaceHandler.getRcBottom());
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

    public HashMap<String, String> getHandleInfoMap(String strHandle) {
        HashMap<String, String> infoMap = new HashMap();
        DaoConfig config = new DaoConfig();
        config.setContext(ctx.getApplicationContext());
        config.setDbUpdateListener(null);
        config.setDbName(openPath);
        DbUtil db = DbUtil.createDb(config);
        if (db != null && checkTableExist(db, "gjindexinfo") && checkTableExist(db, "gjparminfo")) {
            String sql = null;
            sql = String.format("select id from gjindexinfo where handle='%s'", strHandle);
            Cursor cursor = db.getDb().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int gjid = cursor.getInt(0);
                sql = String.format("select * from gjparminfo where gjid=%d", gjid);
                Cursor cursor1 = db.getDb().rawQuery(sql, null);
                while (cursor1.moveToNext()) {
                    int parmnameid = cursor1.getInt(4);
                    int parmvalueid = cursor1.getInt(5);
                    String parmname = getParmidName(db, parmnameid);
                    String parmvalue = getParmidName(db, parmvalueid);
                    if (parmname != null || parmvalue != null)
                        infoMap.put(parmname, parmvalue);
                }
                cursor1.close();
            }
            cursor.close();
        }

        return infoMap;
    }

    protected String getParmidName(DbUtil db, int id) {
        String value = null;
        String sql = null;
        sql = String.format("select name from parmtextid where id=%d", id);
        Cursor cursor = db.getDb().rawQuery(sql, null);
        if (cursor.moveToNext()) {
            value = cursor.getString(0);
        }
        return value;
    }

    private boolean bCapture;
    private Intent captureData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            bCapture = false;
            return;
        }
        if (requestCode == 10000) {
            viewInfo = data.getStringExtra("viewInfo");
        } else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (data != null) {
                captureData = data;
                bCapture = true;
            } else {
                captureData = null;
                bCapture = false;
            }
        } else if (requestCode == 101) {
            if (data == null) {
                return;
            }
            String newFilePath = data.getStringExtra(GraffitiActivity.KEY_IMAGE_PATH);
            if (TextUtils.isEmpty(newFilePath)) {
                L.e("涂鸦保存路径出现错误！");
                return;
            }
            toPublicProgress(newFilePath);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public HashMap<String, String> getPinMap() {
        return pinMap;
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    PointF ptStart = new PointF(0,0);
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean state = mGestureDetector.onTouchEvent(event);
        curentPt.set(event.getX(), event.getY());
        long time_arrival = event.getEventTime();
        int n_points = event.getPointerCount();
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch(n_points){
            case 1:
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        ptStart.set(event.getX(0), event.getY(0));
                        osgNativeLib.touchBegan(event.getX(0), event.getY(0));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        osgNativeLib.touchEnded(event.getX(0), event.getY(0));
                        egLview.requestRender();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        PointF temp = new PointF(ptStart.x - event.getX(0),ptStart.y - event.getY(0));
                        float length = temp.length();
                        if(length > 10)
                        {
                            osgNativeLib.touchMoved(event.getX(0), event.getY(0));
                            egLview.requestRender();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        osgNativeLib.touchEnded(event.getX(0), event.getY(0));
                        egLview.requestRender();
                        break;
                    default :
                        Log.e(TAG,"1 point Action not captured");
                }
                break;
            case 2:
                switch (action){
                    case MotionEvent.ACTION_POINTER_DOWN:
                        osgNativeLib.touchBegans(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                    case MotionEvent.ACTION_MOVE:
                        osgNativeLib.touchMoveds(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                        egLview.requestRender();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        osgNativeLib.touchEndeds(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                        egLview.requestRender();
                        break;
                    case MotionEvent.ACTION_UP:
                        osgNativeLib.touchEndeds(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                        egLview.requestRender();
                        break;
                    default :
                        Log.e(TAG,"2 point Action not captured");
                }
                break;
        }


        return true;

    }
    private float sqrDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)(Math.sqrt(x * x + y * y));
    }

    public void onSelect(final float x, final float y, int iFloorId, String strFloorName, final String handle, String strName, int iComid, final int iType) {

        this.iFloorId = iFloorId;
        this.strFloorName = strFloorName;
        strInfo = handle;                  //info为空间点的位置
        if (handle.contains(";")) {
            strHandle = handle.substring(0,handle.indexOf(";"));
        } else {
            this.strHandle = handle;
        }
        L.e(this.strHandle);
        //当前坐标
//        float x = curentPt.x;
//        float y = curentPt.y;
//        L.e("x:" + ShowDrawUtil.px2dip(ctx, x) + "y:" + ShowDrawUtil.px2dip(ctx, y));
        final HashMap<String, String> dataMap = new HashMap();
        dataMap.put("handle", strHandle);
        dataMap.put("componentId", iComid + "");
        if (1 == iType) {          //标记
            dataMap.put("floorName", strFloorName);
            dataMap.put("floorId", iFloorId + "");
            dataMap.put("name", strName);
            dataMap.put("type", "1");            //  视口0   标注1   构件2
            dataMap.put("info", strInfo);
        } else {
            //构件
            dataMap.put("floorName", strFloorName);
            dataMap.put("floorId", iFloorId + "");
            dataMap.put("name", "");
            dataMap.put("type", "2");            //  视口0   标注1   构件2
            dataMap.put("info", strInfo);
        }

        (MobileSurfaceActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (-1 == iType) {
                    /**
                     *取消选择状态
                     */
                    ctx.toCancelAction();
                } else {
                    egLview.requestRender();
                    // 发进展- 截图
                    if (StrUtil.isEmptyOrNull(strHandle)) {
                        L.toastShort("该构件无法操作！");
                        return;
                    }
                    if (ctx.isSendProgerss) {
                        ctx.toSendProgress();
                    } else {
                        if (ctx.getActionType() >= ActionTypeEnum.NO.value() && !ctx.selectMode) {
                            ctx.seeDetail(dataMap);
                        } else {
                            ctx.addOpView(x, y, dataMap);
                        }
                    }
                }
            }
        });
    }
    public void onFileLoadCompleted() {
        ctx.getMobileSurfaceHandler().closeDialog();
        egLview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void requestGLRender(){
        egLview.requestRender();
    }

    private void singleCLick() {
        if (!selectMode) {
            if (sharedTitleView.getVisibility() == View.VISIBLE)
                ViewUtils.hideViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
            else
                ViewUtils.showViews(sharedTitleView, getMobileSurfaceHandler().getRcBottom());
        }
    }

    private AttachMsgReceiver downReceive = new AttachMsgReceiver() {

        @Override
        public void downloadCountReceived(Intent intent) {
            if (intent != null) {
                String urlStr = intent.getStringExtra(GlobalConstants.KEY_DOWN_ID);
//                if (!urlStr.equalsIgnoreCase(data.getUrl())) {
//                    return;
//                }
                String downPercent = intent.getStringExtra(GlobalConstants.KEY_DOWN_PERCENT);
                Boolean bComplete =
                        intent.getBooleanExtra(GlobalConstants.KEY_DOWN_COMPLETE, false);
                if (StrUtil.isEmptyOrNull(urlStr) || StrUtil.isEmptyOrNull(downPercent)
                        || bComplete == null) {
                    return;
                }

                if (bComplete) {
                    File file = (File) intent.getSerializableExtra(GlobalConstants.KEY_DOWN_FILE);
                    String path = intent.getStringExtra(GlobalConstants.KEY_DOWN_ID);
                    L.e("fileNum:" + counts++);
                    for (int i = 0; i < urls.size(); i++) {
                        if (path.equals(urls.get(i))) {
                            return;
                        }
                    }
                    urls.add(path);
                    count++;
                    if (isSenior) {
                        if (count == dataSeniorList.size()) {
                            isSenior = false;
                            loadSeniorFile();
                        }
                    } else {
//                        osgNativeLib.LoadFileEx(file.getAbsolutePath());
                        Log.e("LoadFileEx",file.getAbsolutePath());
                    }
                    Log.e(TAG, "count: " + count + "" + "fileNum:" + fileNum);
                    Log.e(TAG, file.getAbsolutePath());
                    modeloadProgress.setVisibility(View.VISIBLE);
                    tvProgress.setVisibility(View.VISIBLE);
                    int progress = (count + downNum) * 100 / (fileNum + downNum);
                    if (progress >= 100) {
                        modeloadProgress.setProgress(100);
                        tvProgress.setText("100%");
                    } else{
                        modeloadProgress.setProgress(progress);
                        tvProgress.setText(progress + "%");
                    }
//                    if (file.getAbsolutePath().contains("ProjectInfo.jsonb")||file.getAbsolutePath().contains("Components")||file.getAbsolutePath().contains("Octree_ref5.jsonb")) {
//                        osgNativeLib.loadFile(file.getAbsolutePath());
//                    }else {
//                        osgNativeLib.LoadFileEx(file.getAbsolutePath());
//                    }
                    if (count == fileNum) {
                        Log.e(TAG, "count == fileNum");
                        LoadFileTask(true);
                        modeloadProgress.setProgress(100);
                        tvProgress.setText("100%");
                        try {
                            Thread.sleep(1000);
                            modeloadProgress.setVisibility(View.GONE);
                            tvProgress.setVisibility(View.GONE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

/*                        if (getActionType() == ActionTypeEnum.YES.value() && bNeedDown) {
                            L.e("getPosDatas");
                            getPosDatas(true);
                        }*/
                        data.setLoaclUrl(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")));
                        data.setConvertTime(getIntent().getStringExtra("convertTime"));
//                        MaApplication.getMaApplication().onDownloadFileOp(data.toString(), 1);
//                        mTask = new LoadFileAsyncTask(ctx, egLview, openPath);
//                        mTask.execute(openPath);
                    }
                }


            }
        }
    };

    private void loadSeniorFile() {
        String strPath = PathUtil.getFilePath() + "/hsf";
        osgNativeLib.setSysPath(strPath);
        try {
//            osgNativeLib.loadFile(openPath);
            Log.e("LoadFile",openPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StrUtil.listNotNull(dataOtherList)) {
            Intent otherIntent = new Intent(ctx, AttachService.class);
            otherIntent.putExtra(GlobalConstants.KEY_ATTACH_OP, (Serializable) dataOtherList);
            ctx.startService(otherIntent);
        }
    }

    private void LoadFileTask(boolean isLoad) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isLoad) {
            getMobileSurfaceHandler().getDlg(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
//                    mSurfaceView.cancelLoading();
                    boolean state = mTask.cancel(true);
//                    CADApplication.addRf(WorkEnum.RefeshKey.HSF_REFRESH);
                    if (showDrawInterface != null)
                        showDrawInterface.loadEnd(openPath);
                }
            }).show();
        }
        mTask = new LoadFileAsyncTask(ctx, egLview, openPath, isLoad);
        mTask.execute(openPath);
    }
}
