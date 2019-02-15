package cn.pinming.cadshow.bim;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weqia.component.rcmode.RcBaseViewHolder;
import com.weqia.component.rcmode.adapter.RcFastAdapter;
import com.weqia.component.rcmode.recyclerView.LuRecyclerView;
import com.weqia.component.rcmode.recyclerView.LuRecyclerViewAdapter;
import com.weqia.utils.DeviceUtil;
import com.weqia.utils.L;
import com.weqia.utils.RefreshObjEvent;
import com.weqia.utils.StrUtil;
import com.weqia.utils.TimeUtils;
import com.weqia.utils.ViewUtils;
import com.weqia.utils.ZipUtils;
import com.weqia.utils.datastorage.db.DaoConfig;
import com.weqia.utils.datastorage.db.DbUtil;
import com.weqia.utils.datastorage.file.FileUtil;
import com.weqia.utils.datastorage.file.PathUtil;
import com.weqia.utils.dialog.SharedCommonDialog;
import com.weqia.utils.http.HttpUtil;
import com.weqia.utils.http.okgo.OkGo;
import com.weqia.utils.http.okgo.callback.FileCallback;
import com.weqia.utils.http.okgo.model.RequestParams;
import com.weqia.utils.http.okserver.download.DownloadManager;
import com.weqia.utils.http.okserver.download.DownloadPress;
import com.weqia.utils.view.CommonImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pinming.cadshow.AssetsUtil;
import cn.pinming.cadshow.ShowDraw;
import cn.pinming.cadshow.ShowDrawUtil;
import cn.pinming.cadshow.bim.custormview.CustormFrameLayout;
import cn.pinming.cadshow.bim.custormview.RockerView;
import cn.pinming.cadshow.bim.custormview.TEDrawerLayout;
import cn.pinming.cadshow.bim.data.ModelPinInfo;
import cn.pinming.cadshow.bim.data.Tasks;
import cn.pinming.cadshow.bim.tree.DrawerTreeData;
import cn.pinming.cadshow.bim.tree.DrawerTreeHelper;
import cn.pinming.cadshow.cad.assist.CountClickInterface;
import cn.pinming.cadshow.data.ActionTypeEnum;
import cn.pinming.cadshow.data.BottomViewData;
import cn.pinming.cadshow.data.SourceDownData;
import cn.pinming.cadshow.library.R;
import okhttp3.Call;
import okhttp3.Response;
import osg.AndroidExample.EGLview;
import osg.AndroidExample.osgNativeLib;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by berwin on 2017/5/27.
 */

public abstract class MobileSurfaceHandler {

    private View rockerView;
    private RelativeLayout rlRoamControl;
    private RockerView rockerview;
    private ImageView ivUp;
    private ImageView ivDown;
    private boolean isOnTouch = false;
    private int touchType = 0; //0代表没有触摸，1代表向上方向键，2代表向下方向键

    private String rooPath = PathUtil.getFilePath() + "/hsf";
    private String objResource = rooPath + "/objresource";
    private String textrue = rooPath + "/textrue";
    private String tree = textrue + "/tree";
    private String textureImage = rooPath + "/textureimage";
    private MobileSurfaceActivity ctx;

    private LuRecyclerView rcBottom;
    private LuRecyclerView rcDrawer;
    private RcFastAdapter bottomAdapter;
    private RcFastAdapter drawerAdapter;

    private TEDrawerLayout drawerLayout;

    enum BackType {
        Nomal, Share, Viewport, Roaming
    }

    private BackType backType = BackType.Nomal;
    private boolean isNetDownZip = false;
    private HashMap<String, String> pinMap;

    public static boolean isOverallView = true;   //底部功能栏按钮配置，整体页面
    public static boolean isThreeD = true;        //底部功能栏按钮配置，区域三维
    public static boolean isMark = false;            //底部功能栏按钮配置，标记评论
    public static boolean isAction = true;          //底部功能栏按钮配置，功能



    /**
     * 侧边栏初始字符串数据
     */
    private String drawerStr;
    private ArrayList<DrawerTreeData> sortDarwerDatas = new ArrayList<>();

    /**
     * 底部数据集
     */
    private ArrayList<BottomViewData> itemList = new ArrayList<>();

//    private AndroidUserMobileSurfaceView mSurfaceView;
    private EGLview egLview;
    private FrameLayout mMainLayout;
    private CustormFrameLayout flCustorm;
    private FrameLayout sideView;

    private SharedCommonDialog dlg, sourceDlg;
    private TextView tvSourceShow;

    private PopupWindow countPop;
    private String mPath;
    private boolean showCullplanes = true;

    public static final int UNZIP_COMPLETE = 61;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {
                drawerAdapter.setAll(ctx.getPinDatas());
            } else if (msg.what == 102) {
                if (tvSourceShow != null) {
                    tvSourceShow.setText("正在解压...");
                }
            }
        }
    };

    public LuRecyclerView getRcBottom() {
        return rcBottom;
    }

    private ArrayList<DrawerTreeData> rootDarwerDatas = new ArrayList<>();
    private ArrayList<DrawerTreeData> leafDarwerDatas = new ArrayList<>();

    /**
     * 获取树
     */
    public void getDrawerInfo() {
        DrawerTreeHelper.setmEGLview(egLview);
        drawerStr = osgNativeLib.getLayerInfo();
//        L.e("模型楼层构建原始信息：" + drawerStr);
        if (StrUtil.notEmptyOrNull(drawerStr)) {
            sortDarwerDatas = DrawerTreeHelper.getSortDraweDatas(drawerStr, ctx.getFloorList());

        }
        if (StrUtil.listNotNull(sortDarwerDatas)) {
            rootDarwerDatas = DrawerTreeHelper.filterRootNode(sortDarwerDatas);
            leafDarwerDatas = DrawerTreeHelper.filterAllLeafNode(sortDarwerDatas);
        }
    }

    public ArrayList<DrawerTreeData> getRootDarwerDatas() {
        return rootDarwerDatas;
    }

    public ArrayList<DrawerTreeData> getLeafDarwerDatas() {
        return leafDarwerDatas;
    }

    private final String PIBIM_RESOUCE_LAST_TIME_KEY = "PIBIM_RESOUCE_LAST_TIME_KEY";
    private final int FREQUENCY_OF_CHECK_UPDATE = 1;//检查更新的频率，单位为：天,默认为1天;

    /**
     * 是否要下载资源
     */
    public boolean wantDownSource(int mode) {
        // if (checkIsCDBZ(db)) {
        if (7 == mode) {
//                    同步下载 判断文件是否存在，不存在则下载
//            if (ShowDrawUtil.isWiFiActive(ctx)) {
                if (updateToday() || isNeedDownFile()) {
                    //如果没有文件或者今天没有更新过，就去更新资源包
                    return sycDownZipFile();
                }
//            }
        }
        return true;
    }

    private boolean updateToday() {
        boolean isUpdate = true; //是否需要更新
        SharedPreferences preferences = ctx.getSharedPreferences("PIBIM_RESOUCE", MODE_PRIVATE);
        Long lastCheckTime = preferences.getLong(PIBIM_RESOUCE_LAST_TIME_KEY, 0);
//        L.e("上一次时间：：：" + lastCheckTime);
        long dayOver = TimeUtils.getDayOver(-1 * FREQUENCY_OF_CHECK_UPDATE);
//        L.e("今天时间：：：" + dayOver);
        if (lastCheckTime != 0 && lastCheckTime >= dayOver) {
            L.i("今天已经更新过了，不用来更新了");
            isUpdate = false;
        }
        return isUpdate;
    }

    protected boolean checkIsCDBZ(DbUtil db) {
        try {
            int count = 0;
            String tableName = "projectcode";
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
            Cursor cursor = db.getDb().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
                cursor.close();
            }
            if (count > 0) {
                sql = "select parmvalue from " + tableName + " where parmname='CurProjectMode'";
                cursor = db.getDb().rawQuery(sql, null);
                if (cursor.moveToNext()) {
                    String parmValue = cursor.getString(0);
                    if (0 == parmValue.compareToIgnoreCase("7|场地布置"))
                        return true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        }

        return false;
    }

/*    public MobileSurfaceHandler(MobileSurfaceActivity ctx, AndroidUserMobileSurfaceView mSurfaceView, String mPath) {
        this.ctx = ctx;
        this.mSurfaceView = mSurfaceView;
        this.mPath = mPath;
    }*/

    public MobileSurfaceHandler(MobileSurfaceActivity ctx, EGLview egLview, String mPath, HashMap<String, String> pinMap) {
        this.ctx = ctx;
        this.egLview = egLview;
        this.mPath = mPath;
        this.pinMap = pinMap;
    }

/*    public MobileSurfaceHandler(MobileSurfaceActivity ctx, AndroidUserMobileSurfaceView mSurfaceView, String mPath, HashMap<String, String> pinMap) {
        this.ctx = ctx;
        this.mSurfaceView = mSurfaceView;
        this.mPath = mPath;
        this.pinMap = pinMap;
    }*/

    public void initView() {
        sideView = (FrameLayout) ctx.findViewById(R.id.side_view);
        sideView.getBackground().setAlpha(100);
        flCustorm = (CustormFrameLayout) ctx.findViewById(R.id.fl_custorm);
        mMainLayout = (FrameLayout) ctx.findViewById(R.id.fl_bim_content);
        drawerLayout = (TEDrawerLayout) ctx.findViewById(R.id.drawer_layout);
        if (ctx.getActionType() >= ActionTypeEnum.NO.value()) {
            //没有操作权限 不能滑动
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (StrUtil.listNotNull(ctx.getPinDatas())) {
                    ViewUtils.hideView(ctx.sharedTitleView);
                    ViewUtils.hideView(rcBottom);
                    drawerAdapter.setAll(ctx.getPinDatas());
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        mMainLayout.removeAllViews();
//        mMainLayout.addView(mSurfaceView);
        mMainLayout.addView(egLview);
        setSlideDisabled();
        initBottom();
        initDrawerView();
        initBackListener();
    }

    public void initBottom() {
        rcBottom = (LuRecyclerView) ctx.findViewById(R.id.rc_bim_bottom);
        LinearLayoutManager titleManager = new LinearLayoutManager(ctx);
        titleManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcBottom.setLayoutManager(titleManager);
        ViewUtils.showView(rcBottom);

        if (isOverallView)
            itemList.add(new BottomViewData(100, "整体页面", R.drawable.cad_op_selor_bim_all));
        if (isThreeD)
            itemList.add(new BottomViewData(200, "区域三维", R.drawable.cad_op_selor_bim_dynamic));
        if (ctx.getActionType() == ActionTypeEnum.YES.value()) {
            if (isMark)
                itemList.add(new BottomViewData(300, "标记评论", R.drawable.cad_op_selor_bim_plugs));
        }
        if (isAction)
            itemList.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));
//        itemList.add(new BottomViewData(500, "标注", R.drawable.selor_bim_mark));
//        itemList.add(new BottomViewData(600, "测距", R.drawable.selor_space));
//        itemList.add(new BottomViewData(700, "截屏", R.drawable.selor_bim_clip));

        bottomAdapter = new RcFastAdapter<BottomViewData>(ctx, R.layout.cad_op_view_reused_bottom_button) {

            @Override
            public void bindingData(RcBaseViewHolder holder, final BottomViewData item) {
                int averageCount = 0;
                FrameLayout cellFl = holder.findViewById(R.id.cell_all);
                TextView tvContent = holder.findViewById(R.id.tv_cad_range);
                ImageView ivPort = holder.findViewById(R.id.iv_viewport);
                if (tvContent == null || cellFl == null) {
                    L.i("出现了空的情况");
                    return;
                }
                if (backType == BackType.Viewport) {
                    ViewUtils.showView(ivPort);
                    ViewUtils.hideView(tvContent);
                    ivPort.setImageResource(item.getDrawId());
                    averageCount = 7;
                } else {
                    ViewUtils.hideView(ivPort);
                    ViewUtils.showView(tvContent);
                    Drawable drawable = ctx.getResources().getDrawable(item.getDrawId());
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    tvContent.setText(item.getTitle());
                    tvContent.setCompoundDrawables(null, drawable, null, null);
                    if (ctx.getActionType() == ActionTypeEnum.YES.value()) {
                        averageCount = 4;
                    } else {
                        averageCount = 3;
                    }
                }
                cellFl.setLayoutParams(new FrameLayout.LayoutParams((DeviceUtil.getDeviceWidth() / averageCount), ShowDrawUtil.dip2px(ctx, 47)));
                cellFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomClickDo(item);
                    }
                });
            }
        };
        LuRecyclerViewAdapter mAdapter = new LuRecyclerViewAdapter(bottomAdapter);
        rcBottom.setAdapter(mAdapter);
        bottomAdapter.setAll(itemList);
    }

    private void initDrawerView() {
        rcDrawer = (LuRecyclerView) ctx.findViewById(R.id.rc_drawer_view);
        LinearLayoutManager titleManager = new LinearLayoutManager(ctx);
        titleManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcDrawer.setLayoutManager(titleManager);
        drawerAdapter = new RcFastAdapter<ModelPinInfo>(ctx, R.layout.cad_op_cell_rc_drawer_left) {
            @Override
            public void bindingData(RcBaseViewHolder holder, final ModelPinInfo item) {
                TextView tvTitle = holder.getView(R.id.tv_msg);
                TextView tvIndex = holder.getView(R.id.tv_index);
                LinearLayout allView = holder.getView(R.id.allView);
                tvTitle.setText(item.getName());
                tvIndex.setText(item.getOrderId() + "");
                allView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item == null) {
                            return;
                        }
                        String tkId = "";
                        if (StrUtil.notEmptyOrNull(item.getTasks())) {
                            List<Tasks> tasks = JSON.parseArray(item.getTasks(), Tasks.class);
                            Tasks tmpTask = null;
                            if (StrUtil.listNotNull(tasks)) {
                                tmpTask = tasks.get(0);
                            }
                            if (tmpTask != null && StrUtil.notEmptyOrNull(tmpTask.getTkId())) {
                                tkId = tmpTask.getTkId();
                            }
                            if (StrUtil.notEmptyOrNull(tkId)) {
                                HashMap<String, String> pinMap = new HashMap<>();
                                pinMap.put("toClass", "ApprovalDetailActivity");
                                pinMap.put("tkId", tkId);
                                pinMap.put("pjId", tmpTask.getPjId());
                                if (StrUtil.notEmptyOrNull(item.getFlowId())) {
                                    pinMap.put("flowId", item.getFlowId());
                                }
                                if (StrUtil.notEmptyOrNull(item.getBehavior())) {
                                    pinMap.put("behavior", item.getBehavior());
                                }
                                ShowDrawUtil.ronterActionSync(ctx, null, "pvapproval", "acnewapproval", pinMap);
                            }
                        } else {
                            /**
                             *不是任务就是跳转到历史信息
                             */
                            HashMap<String, String> pinMap = new HashMap<>();
                            pinMap.put("mpId", item.getMpId());
                            pinMap.put("floorName", item.getFloorName());
                            pinMap.put("floorId", item.getFloorId()+"");
                            pinMap.put("name", item.getName());
                            pinMap.put("handle", item.getHandle());
                            pinMap.put("nodeId", item.getNodeId());
                            pinMap.put("viewInfo", osgNativeLib.getViewPortInfo());
                            pinMap.put("type", item.getType());
                            pinMap.put("info", item.getInfo());
                            ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodelhis", pinMap);
                        }
                    }
                });
            }
        };
        LuRecyclerViewAdapter mAdapter = new LuRecyclerViewAdapter(drawerAdapter);
        rcDrawer.setAdapter(mAdapter);
    }

    /**
     * 重写这个活动的后退事件
     */
    private void initBackListener() {
        ctx.sharedTitleView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backType == BackType.Share) {   //标注分享
                    toShareBack();
                } else if (backType == BackType.Viewport) {
                    itemList.clear();
                    if (isOverallView)
                        itemList.add(new BottomViewData(100, "整体页面", R.drawable.cad_op_selor_bim_all));
                    if (isThreeD)
                        itemList.add(new BottomViewData(200, "区域三维", R.drawable.cad_op_selor_bim_dynamic));
                    if (ctx.getActionType() == ActionTypeEnum.YES.value()) {
                        if (isMark)
                            itemList.add(new BottomViewData(300, "标记评论", R.drawable.icon_bottom_plfx));
                    }
                    if (isAction)
                        itemList.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));
                    bottomAdapter.setAll(itemList);
                    ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
                } else if (backType == BackType.Roaming) {
                    ViewUtils.showView(rcBottom);
                    ViewUtils.hideView(rlRoamControl);
                    mMainLayout.removeView(rockerView);
                    osgNativeLib.endWalk();
                    ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
                    ctx.showButtons();
                    egLview.requestRender();
                } else {
                    ctx.backClick();
                }
                backType = BackType.Nomal;
            }
        });
    }

    public SharedCommonDialog getDlg(DialogInterface.OnCancelListener onCancelListener) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.cad_op_view_dwg_dialog_loading, null);
        TextView tvShow = (TextView) view.findViewById(R.id.loading_view_show);
        tvShow.setText("正在打开模型...");
        SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
        dlg = builder.setTitle("请等待").showBar(true).setContentView(view).create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.setOnCancelListener(onCancelListener);
        return dlg;
    }


    public void closeDialog() {
        if (dlg != null && !isNetDownZip) {
            dlg.dismiss();
            dlg = null;
        }
    }


    public void onMessageEvent(RefreshEvent event) {
        if (event.type == UNZIP_COMPLETE) {
            isNetDownZip = false;
            closeDialog();
        }
    }

    public void onMessageEvent(RefreshObjEvent event) {
        if (event.type == OkGo.DOWNLOAD_PROGRESS && event.getObj() instanceof DownloadPress) {
            DownloadPress progress = (DownloadPress) event.getObj();
            String per = String.format("%.2f", progress.getProgress() * 100) + "%";
            if (tvSourceShow != null)
                tvSourceShow.setText("已下载  " + per);
        }
    }

    public boolean isShowingRocker() {
        if (mMainLayout != null && rockerView != null) {
            if (mMainLayout.indexOfChild(rockerview) != -1) {
                return true;
            }
        }
        return false;
    }


    public void initRoamRocker() {
        if (rockerView != null) {
            mMainLayout.removeView(rockerView);
        }
        rockerView = LayoutInflater.from(ctx).inflate(R.layout.cad_op_ac_surface_roaming_rocker, mMainLayout, false);
        mMainLayout.addView(rockerView);
        rlRoamControl = (RelativeLayout) ctx.findViewById(R.id.rl_roaming_control);
        rockerview = (RockerView) ctx.findViewById(R.id.rv_rocker);
        ivUp = (ImageView) ctx.findViewById(R.id.iv_up);
        ivDown = (ImageView) ctx.findViewById(R.id.iv_down);
        rockerview.setRockerChangeListener(new RockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {
                osgNativeLib.onWalkMove(x, y);
                egLview.requestRender();
            }
        });
    }

    private void initDirctionTouch() {
        ivUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    isOnTouch = true;
                    touchType = 1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isOnTouch = false;
                    touchType = 0;
                }
                return true;
            }
        });
        ivDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    isOnTouch = true;
                    touchType = 2;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isOnTouch = false;
                    touchType = 0;
                }
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isOnTouch && touchType != 0) {
                        if (touchType == 1) {
                            toUpAction();
                        } else if (touchType == 2) {
                            toDownAction();
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
                return;
            }
            for (int i = 0; i < item.getChildDatas().size(); i++) {
                osgNativeLib.setLayerShow(item.getParentData().getTypeIndex(), item.getChildDatas().get(i).getTypeIndex(), bShow);
            }
        } else {
            osgNativeLib.setLayerShow(item.getSuperParentData().getTypeIndex(), item.getTypeIndex(), bShow);
        }
    }

    /**
     * 刷新侧边栏列表
     */
    public void refreshLeftDrawerVIew() {
        if (StrUtil.listNotNull(ctx.getPinDatas())) {
            ViewUtils.hideView(ctx.sharedTitleView);
            ViewUtils.hideView(rcBottom);
            drawerAdapter.setAll(ctx.getPinDatas());
        }
        drawerLayout.openDrawer(GravityCompat.END);
    }


    /**
     * 关闭侧边栏
     */
    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    private DrawerTreeData refreshClickedItemData(DrawerTreeData item) {
        DrawerTreeData refreshData = null;
        for (DrawerTreeData data : sortDarwerDatas) {
            if (item.getLevel() == 2) {
                if (data.getLevel() == 2 && data.getParentData().getTypeIndex() == item.getParentData().getTypeIndex()) {
                    refreshData = data;
                    break;
                }
            } else if (item.getLevel() == 1) {
                if (data.getTypeIndex() == item.getTypeIndex()) {
                    refreshData = data;
                    break;
                }
            } else if (item.getLevel() == 3) {
                if (data.getLevel() == 3 && data.getSuperParentData().getTypeIndex() == item.getSuperParentData().getTypeIndex() && data.getTypeIndex() == item.getTypeIndex()) {
                    refreshData = data;
                    break;
                }
            }
        }
        return refreshData;
    }

    private void toRoamingAction() {
        ctx.resetState();
        ctx.hideButtons();
        ctx.refreshMenuShareItem(false);
        ViewUtils.hideView(rcBottom);
        initRoamRocker();
        initDirctionTouch();
        backType = BackType.Roaming;
        osgNativeLib.beginWalk();
        ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_guanbi);
        egLview.requestRender();
    }

    private void toViewportAction() {
        //处理视口相关操作
        ctx.refreshMenuShareItem(false);
        backType = BackType.Viewport;
        itemList.clear();
        itemList.add(new BottomViewData(1001, "", R.drawable.cad_op_youshitu));
        itemList.add(new BottomViewData(1002, "", R.drawable.cad_op_zuoshitu));
        itemList.add(new BottomViewData(1003, "", R.drawable.cad_op_qianshitu));
        itemList.add(new BottomViewData(1004, "", R.drawable.cad_op_houshitu));
        itemList.add(new BottomViewData(1005, "", R.drawable.cad_op_dingshitu));
        itemList.add(new BottomViewData(1006, "", R.drawable.cad_op_fushitu));
        itemList.add(new BottomViewData(1007, "", R.drawable.cad_op_quantu));
        bottomAdapter.setAll(itemList);
        ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_guanbi);
    }

    private void toAnnotatedAction() {
        //处理标注分享相关操作
        ctx.resetState();
        ctx.hideButtons();
        ctx.refreshMenuDimensionItem(false);
        ctx.refreshMenuShareItem(true);
        ViewUtils.hideView(rcBottom);
        ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_guanbi);
        egLview.setMode(EGLview.DrawMode.eDimText);
//        mSurfaceView.setOperatorOrbit();
        osgNativeLib.hideCullPlanes();
        egLview.requestRender();
    }

    public void onBackPressed() {
        EGLview.DrawMode mode = egLview.getMode();
        if (EGLview.DrawMode.eCullPlanes == mode) {
            osgNativeLib.cancelCullPlanes();
            egLview.setMode(EGLview.DrawMode.eStardard);
            ctx.refreshMenuDimensionItem(false);
            ViewUtils.showView(ctx.sharedTitleView);
            ViewUtils.showView(rcBottom);
            ctx.showButtons();
            egLview.requestRender();
        } else if (EGLview.DrawMode.eDimText == mode) {
            osgNativeLib.deleteAllDimTexts();
//            mSurfaceView.setOperatorSelectPoint();
            egLview.setMode(EGLview.DrawMode.eStardard);
            ctx.refreshMenuShareItem(false);
            ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
            backType = BackType.Nomal;
            ViewUtils.showView(ctx.sharedTitleView);
            ViewUtils.showView(rcBottom);
            ctx.showButtons();
        }
        //定位
        else if (EGLview.DrawMode.eMarkUp == mode) {
            ctx.toCancelMarkUp();
        }
        //选择
        else if (osgNativeLib.getSelectState()) {
            ctx.toCancelSelect();
        } else {
            if (backType == BackType.Viewport) {
                itemList.clear();
                itemList.add(new BottomViewData(100, "整体页面", R.drawable.cad_op_selor_bim_all));
                itemList.add(new BottomViewData(200, "区域三维", R.drawable.cad_op_selor_bim_dynamic));
                if (ctx.getActionType() == ActionTypeEnum.YES.value()) {
                    itemList.add(new BottomViewData(300, "标记评论", R.drawable.icon_bottom_plfx));
                }
                itemList.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));
                bottomAdapter.setAll(itemList);
                backType = BackType.Nomal;
                ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
            } else if (backType == BackType.Roaming) {
                ViewUtils.showViews(ctx.sharedTitleView, rcBottom);
                ViewUtils.hideView(rlRoamControl);
                mMainLayout.removeView(rockerView);
                backType = BackType.Nomal;
                ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
                osgNativeLib.endWalk();
                ctx.showButtons();
                egLview.requestRender();
            } else {
                if (ctx.isMbTaskExit())
                    ctx.backClick();
            }
        }
    }

    private void setSlideEnable() {
        // API版本在4.3以上
    }

    private void setSlideDisabled() {
        //API版本在4.3以上
    }

    private void toShareBack() {
        ctx.sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
        ctx.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewUtils.showView(ctx.sharedTitleView);
        ViewUtils.showView(rcBottom);
        ctx.refreshMenuShareItem(false);
        osgNativeLib.deleteAllDimTexts();
//        mSurfaceView.setOperatorSelectPoint();
        egLview.setMode(EGLview.DrawMode.eStardard);
        backType = BackType.Nomal;
        ctx.showButtons();
    }


    private void toUpAction() {
        osgNativeLib.onWalkCameraUp();
        egLview.requestRender();
    }


    private void toDownAction() {
        osgNativeLib.onWalkCameraDown();
        egLview.requestRender();
    }


    private void bottomClickDo(final BottomViewData item) {
        switch (item.getvId()) {
            case 100:
                setSlideDisabled();
                ctx.refreshMenuDimensionItem(false);
                ctx.refreshMenuShareItem(false);
                osgNativeLib.setHoopsViewMode(0, true);
                egLview.requestRender();
                break;
            case 200:
                ctx.resetState();
                ctx.hideButtons();
                setSlideDisabled();
                ctx.refreshMenuDimensionItem(true);
                ctx.refreshMenuShareItem(false);
                osgNativeLib.setOperatorCullSection();
                egLview.setMode(EGLview.DrawMode.eCullPlanes);
                ViewUtils.hideView(rcBottom);
                egLview.requestRender();
                break;
            case 300:
                if (osgNativeLib.getSelectState())
                    return;
                ctx.toSelectAction();
                setSlideEnable();
                ctx.refreshMenuDimensionItem(false);
                ctx.refreshMenuShareItem(false);
                ViewUtils.hideViews(ctx.sharedTitleView, rcBottom);
                ctx.isSendProgerss = true;
                break;
            case 400:
                setSlideDisabled();
                ctx.refreshMenuDimensionItem(false);
                String[] opBims = ctx.getResources().getStringArray(R.array.cad_pbim);
                ArrayList<String> countInfos = new ArrayList<>();
                for (String str : opBims) {
                    if (str.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_cullplanes)) ||
                            str.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_cullplanes_open))) {
                        if (showCullplanes)
                            countInfos.add(ctx.getString(R.string.cad_fuc_cullplanes));
                        else
                            countInfos.add(ctx.getString(R.string.cad_fuc_cullplanes_open));
                        continue;
                    }
                    countInfos.add(str);
                }

                String end = mPath.substring(mPath.lastIndexOf(".") + 1, mPath.length()).toLowerCase();
                if (end.equals("pbim")) {
                    DaoConfig config = new DaoConfig();
                    config.setContext(ctx.getApplicationContext());
                    config.setDbName(mPath);
                    config.setDbUpdateListener(null);
                    DbUtil db = DbUtil.createDb(config);
                    if (db != null) {
                        if (checkIsCDBZ(db)) {
//                            if (isNeedDownFile()) {
                            String str = ctx.getString(R.string.cad_fuc_down_file);
                            countInfos.add(str);
//                            }
                        }
                    }
                }
                showCountWindow(rcBottom, countInfos, new CountClickInterface() {
                    @Override
                    public void countItemClick(String name) {
//                        ViewUtils.hideView(rcBottom);
                        if (name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_window))) {
                            //TODO: 视口
                            toViewportAction();
                        } else if (name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_roam))) {
                            //TODO: 漫游
                            toRoamingAction();
                        } else if (name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_anote))) {
                            //TODO: 标注分享
                            backType = BackType.Share;
                            toAnnotatedAction();
                        } else if (name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_cullplanes)) ||
                                name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_cullplanes_open))) {
                            if (showCullplanes) {
                                osgNativeLib.hideCullPlanes();
                            } else
                                osgNativeLib.showCullPlanes();
                            showCullplanes = !showCullplanes;
                            egLview.requestRender();
                        } else if (name.equalsIgnoreCase(ctx.getString(R.string.cad_fuc_down_file))) {
                            LayoutInflater inflater = LayoutInflater.from(ctx);
                            View view = inflater.inflate(R.layout.cad_op_view_dwg_dialog_loading, null);
                            tvSourceShow = (TextView) view.findViewById(R.id.loading_view_show);
                            tvSourceShow.setText("正在下载...");
                            SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
                            sourceDlg = builder.setTitle("请等待").showBar(true).setContentView(view).create();
                            sourceDlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                            sourceDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (StrUtil.notEmptyOrNull(ctx.getDownloadSourceStr())) {
                                        L.e(ctx.getDownloadSourceStr());
                                        DownloadManager.getInstance().pauseTask(ctx.getDownloadSourceStr());
                                    }
                                }
                            });
                            sourceDlg.setCanceledOnTouchOutside(false);
                            sourceDlg.show();

                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    sycDownZipFile();
                                    return null;
                                }
                            }.execute();
                        }
                    }
                });
                break;
            case 1001:
                //TODO: 右视口点击事件
                osgNativeLib.setHoopsViewMode(3, true);
                egLview.requestRender();
                break;
            case 1002:
                //TODO: 左视口点击事件
                osgNativeLib.setHoopsViewMode(4, true);
                egLview.requestRender();
                break;
            case 1003:
                //TODO: 前视口点击事件
                osgNativeLib.setHoopsViewMode(1, true);
                egLview.requestRender();
                break;
            case 1004:
                //TODO: 后视口点击事件
                osgNativeLib.setHoopsViewMode(2, true);
                egLview.requestRender();
                break;
            case 1005:
                //TODO: 上视口点击事件
                osgNativeLib.setHoopsViewMode(5, true);
                egLview.requestRender();
                break;
            case 1006:
                //TODO: 下视口点击事件
                osgNativeLib.setHoopsViewMode(6, true);
                egLview.requestRender();
                break;
            case 1007:
                //TODO: 全视口点击事件
                osgNativeLib.setHoopsViewMode(0, true);
                egLview.requestRender();
                break;
            default:
                break;
        }
    }

    private boolean sycDownZipFile() {
        if (ShowDraw.downConfig == null) {
            L.e("没有配置下载需要的资料");
            return true;
        }
        RequestParams params = new RequestParams();
        params.put("itype", ShowDraw.downConfig.getDownItype());
        params.put("appKey", ShowDraw.downConfig.getAppKey());
        params.put("s", "1");
        params.put("sourceType", ShowDraw.downConfig.getSourceType());
        params = HttpUtil.getInstance().buildParam(params, ShowDraw.downConfig.getSignKey());
        String resp = (String) HttpUtil.getInstance().postSync(ShowDraw.downConfig.getSeverIp() + "/gateWay.do", params);
        if (StrUtil.isEmptyOrNull(resp)) {
            L.e("获取下载数据失败");
            return true;
        }

        if (resp.contains("object")) {
            String objectStr = JSONObject.parseObject(resp).getString("object");
            SourceDownData downData = SourceDownData.fromString(SourceDownData.class, objectStr);
            SharedPreferences preferences = ctx.getSharedPreferences("PIBIM_RESOUCE", MODE_PRIVATE);
            String hsfMD5 = preferences.getString("hsfMD5","f35d93e2faff187075527b70a3a35056");
            L.d(hsfMD5);
            L.d(downData.getMd5());
            if ((downData != null && downData.getMd5() != null && !hsfMD5.equals(downData.getMd5())) || isNeedDownFile()) {
                @SuppressLint("WrongConstant") SharedPreferences.Editor editor = ctx.getSharedPreferences("PIBIM_RESOUCE", MODE_APPEND).edit();
                editor.putString("hsfMD5", downData.getMd5());
                editor.commit();
                ctx.setDownloadSourceStr(downData.getUrl());
                File file = new File(PathUtil.getFilePath() + "/" + downData.getDocName());
                String fileMds = null;
                if (file.exists()) {
                    fileMds = AssetsUtil.getFileMD5ToString(file);
                    fileMds = fileMds.toLowerCase();
                }
                double fileStr = AssetsUtil.formetFileSize(file.length(), FileUtil.SIZETYPE_KB);
                if (L.D) L.e(fileStr + "---- file length");
                if (L.D) L.e(downData.getFileSize() + "----------nh");
                if (file.exists() && fileStr == Double.parseDouble(downData.getFileSize())
                        && StrUtil.notEmptyOrNull(downData.getMd5()) && downData.getMd5().equalsIgnoreCase(fileMds)) {
                    L.d("已下载且文件大小对，直接解压");
                    unZipSources(file);
                } else {
                    L.e("download file");
                    DownloadManager.getInstance().removeTask(downData.getUrl(), true);
                    return downloadAttach(downData);
                }
            }
        } else {
            L.i(resp);
        }
        return true;
    }


    private void unZipSources(File file) {
        try {
            ZipUtils.unZip(file, PathUtil.getFilePath() + "/hsf/", "utf-8");
        } catch (IOException e) {
            L.e("素材压缩包解压失败");
            e.printStackTrace();
        }
        //下载材质后不关闭加载对话框
        //EventBus.getDefault().post(new RefreshEvent(UNZIP_COMPLETE));
        if (sourceDlg != null) {
            sourceDlg.dismiss();
        }
    }

    private boolean downloadAttach(final SourceDownData sdata) {
        // 得到真实下载路径
        RequestParams params = new RequestParams();
        params.put("itype", ShowDraw.downConfig.getRealUrlItype());
        params.put("appKey", ShowDraw.downConfig.getAppKey());
        params.put("s", "1");
        params.put("sourceType", ShowDraw.downConfig.getSourceType());
        params.put("accountType", "1");
        params.put("urls", sdata.getUrl());
        L.e("请求下载地址==" + params.toString());
        params = HttpUtil.getInstance().buildParam(params, ShowDraw.downConfig.getSignKey());
        String returnPath = (String) HttpUtil.getInstance().postSync(ShowDraw.downConfig.getSeverIp() + "/fileUrl.do", params);
        if (StrUtil.isEmptyOrNull(returnPath)) {
            L.e("downfile 失败， 路径为空");
            return true;
        }
        if (returnPath.contains("object") && returnPath.contains("url")) {
            String realUrl = JSONObject.parseObject(returnPath).getJSONObject("object").getString("url");
            L.d("下载文件信息为" + sdata.getUrl());
            L.e(realUrl);
            String filePath = PathUtil.getFilePath() + File.separator + sdata.getDocName();
            HttpUtil.getInstance().download(realUrl, filePath, sdata.getUrl(), new FileCallback() {

                @Override
                public void onSuccess(final File file) {
                    // 刷新下载的数据
                    @SuppressLint("WrongConstant") SharedPreferences.Editor editor = ctx.getSharedPreferences("PIBIM_RESOUCE", MODE_APPEND).edit();
                    editor.putLong(PIBIM_RESOUCE_LAST_TIME_KEY, System.currentTimeMillis());
                    editor.commit();
                    MediaScannerConnection.scanFile(ctx, new String[]{file.getAbsoluteFile().toString()}, null, null);
                    handler.sendEmptyMessage(102);
                    unZipSources(file);
                    L.d("下载完成，返回True，让程序继续往下走");
                    ctx.getmTask().setbOpenMode(true);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    L.e("下载错误，这次不再下载");
                    ctx.getmTask().setbOpenMode(true);
                }
            });
            L.d("开始下载资源压缩包，返回False，不让程序继续往下走");
            return false;
        } else {
            L.i(returnPath);
            return true;
        }
    }


    private boolean isNeedDownFile() {
        boolean isDown = true;
        if (isFileExist(textrue)
                && isFileExist(tree)
                && isFileExist(textureImage)) {
            L.e("无需下载");
            isDown = false;
        }
        return isDown;
    }

    private static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }


    public void showCountWindow(View parent, final List<String> tmpInfos, final CountClickInterface clickInterface) {
        RecyclerView lv_group = null;
        View dlgView = null;
        final RcFastAdapter layerAdapter = new RcFastAdapter<String>(ctx, R.layout.cad_op_cell_dialog_layerinfo, tmpInfos) {
            @Override
            public void bindingData(RcBaseViewHolder holder, final String item) {
                CommonImageView ivCount = holder.getView(R.id.iv_catelog_image);
                TextView tvCountName = holder.getView(R.id.tv_catelog_title);
                ImageView ivSelect = holder.getView(R.id.ib_catelog_select);
//                ivCount.setImageResource(item.getrId());/
                ViewUtils.hideView(ivCount);
                tvCountName.setText(item);
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismisPop(countPop);
                        if (clickInterface != null)
                            clickInterface.countItemClick(item);
                    }
                });
            }
        };
        if (countPop == null || lv_group == null) {
            dlgView = LayoutInflater.from(ctx).inflate(R.layout.cad_op_layer_full_screen_dialog, null);
            lv_group = (RecyclerView) dlgView.findViewById(R.id.lv_pic_item);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
            lv_group.setLayoutManager(layoutManager);

            ViewUtils.bindClickListenerOnViews(dlgView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.view_empty || v.getId() == R.id.view_empty_two) {
                        if (countPop != null) {
                            dismisPop(countPop);
                        }
                    }
                }
            }, R.id.view_empty, R.id.view_empty_two);
            countPop = new PopupWindow(dlgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lv_group.setAdapter(layerAdapter);
        layerAdapter.setAll(tmpInfos);
        countPop.setOutsideTouchable(true);
        countPop.setOnDismissListener(dismissListener);
        countPop.setBackgroundDrawable(new BitmapDrawable());
        countPop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public abstract void refreshPopView();

    PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            refreshPopView();
        }
    };


    private void dismisPop(PopupWindow pop) {
        pop.dismiss();
    }

    public ArrayList<BottomViewData> getItemList() {
        return itemList;
    }
}
