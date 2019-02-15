/////////////////////////////////////////////////////////////////////////////// 
// Copyright (C) 2002-2016, Open Design Alliance (the "Alliance"). 
// All rights reserved. 
// 
// This software and its documentation and related materials are owned by 
// the Alliance. The software may only be incorporated into application 
// programs owned by members of the Alliance, subject to a signed 
// Membership Agreement and Supplemental Software License Agreement with the
// Alliance. The structure and organization of this software are the valuable  
// trade secrets of the Alliance and its suppliers. The software is also 
// protected by copyright law and international treaty provisions. Application  
// programs incorporating this software must include the following statement 
// with their copyright notices:
//   
//   This application incorporates Teigha(R) software pursuant to a license 
//   agreement with Open Design Alliance.
//   Teigha(R) Copyright (C) 2002-2016 by Open Design Alliance. 
//   All rights reserved.
//
// By use of this software, its documentation or related materials, you 
// acknowledge and accept the above terms.
///////////////////////////////////////////////////////////////////////////////
package cn.pinming.cadshow.cad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.weqia.component.rcmode.RcBaseViewHolder;
import com.weqia.component.rcmode.adapter.RcFastAdapter;
import com.weqia.component.rcmode.recyclerView.LuRecyclerView;
import com.weqia.component.rcmode.recyclerView.LuRecyclerViewAdapter;
import com.weqia.utils.DeviceUtil;
import com.weqia.utils.L;
import com.weqia.utils.StrUtil;
import com.weqia.utils.ViewUtils;
import com.weqia.utils.datastorage.file.PathUtil;
import com.weqia.utils.dialog.SharedCommonDialog;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pinming.cadshow.SharedShowActivity;
import cn.pinming.cadshow.ShowDrawInterface;
import cn.pinming.cadshow.ShowDrawRouterUtil;
import cn.pinming.cadshow.ShowDrawUtil;
import cn.pinming.cadshow.TaskApprovalUtil;
import cn.pinming.cadshow.bim.custormview.TEDrawerLayout;
import cn.pinming.cadshow.bim.data.ModelPinInfo;
import cn.pinming.cadshow.bim.data.Tasks;
import cn.pinming.cadshow.cad.assist.CadOpHandler;
import cn.pinming.cadshow.cad.assist.CountClickInterface;
import cn.pinming.cadshow.cad.data.LayerInfo;
import cn.pinming.cadshow.cad.data.LayoutInfo;
import cn.pinming.cadshow.data.ActionTypeEnum;
import cn.pinming.cadshow.data.BottomViewData;
import cn.pinming.cadshow.data.ShowDrawKey;
import cn.pinming.cadshow.graffiti.GraffitiActivity;
import cn.pinming.cadshow.graffiti.GraffitiParams;
import cn.pinming.cadshow.library.R;
import cn.pinming.cadshow.moveview.MoveCallBack;
import cn.pinming.cadshow.moveview.MoveImageView;
import cn.pinming.cadshow.moveview.PreviewPicDialog;
import common.AttachmentData;
import common.CoConfig;
import common.NetworkUtil;
import common.WPf;
import common.WeqiaApplication;
import common.request.ResultEx;
import common.request.ServiceParams;
import common.request.ServiceRequester;
import common.request.UserService;

public class TeighaDwgActivity extends SharedShowActivity implements View.OnTouchListener{

    private String openPath;
    public static ShowDrawInterface showDrawInterface;

    private SharedCommonDialog dlg;
    private TeighaDwgActivity ctx;
    private CadOpHandler cadOpHandler;
    private DrawMode m_drawMode = DrawMode.eStardard;

    private RecyclerView rcBottom;
    private RcFastAdapter bottomAdapter;
    private LinearLayout llContent;
    private TeighaDwgView mView;
    private ImageView ivSmall;
    private TextView leftName, leftValue, rightName, rightValue;
    private LinearLayout countView;
    private ArrayList<BottomViewData> bottomViewDatas = new ArrayList<>();
    private MenuItem deleteMenu, modifyMenu, shareMenu;
    protected MenuItem saveItem;
    protected MenuItem cancelItem;
    private static boolean countMode = false;
    private MoveImageView ivPoint;

    //选择的类型   0未选择 1线性标注 2文字标注 3点标注 x,y标注分享
    private String mSelectType = new String("0");
    //选中的夹点
    private int mSelectIndex = -1;
    //图纸操作的偏移量
    private float mOffset = 0.0f;
    private GestureDetector mGestureDetector;

    private LuRecyclerView rcDrawer;
    private RcFastAdapter drawerAdapter;
    private TEDrawerLayout drawerLayout;
    private String nodeId;
    private String selectType;
    private boolean selectMode;  //不弹出其他窗口，任务的时候新增
    private String nodeType;
    private HashMap<String, String> pinMap;
    private String portInfo;
    private int actionType = ActionTypeEnum.YES.value();
    private String info;
    private String pjId;
    private FrameLayout rootLayout;
    private Dialog taskDialog;    //任务选择的dialog

    enum DrawMode {
        eStardard,
        eMeasure,       //长度测量
        eMeasureArea,   //面积测量
        eRectText,          //标注
        ePoint,         //坐标测量
        eShare,             //标注分享
    }

    private boolean bOpen = false;
    private boolean bNeedSave = false;      //是否要保存
    private List<ModelPinInfo> pinDatas;

    public List<ModelPinInfo> getPinDatas() {
        if (pinDatas == null) {
            pinDatas = new ArrayList<>();
        }
        return pinDatas;
    }

    public int getActionType() {
        return actionType;
    }


    @SuppressLint("HandlerLeak")
    private Handler mPdHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                drawerAdapter.setAll(ctx.getPinDatas());
            } else {
                mView.onLoad(ctx);
                bOpen = true;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event != null && event.equals("getPosDatas")) {
            if (getActionType() == ActionTypeEnum.YES.value()) {
                getPosDatas(false);
                String orderId = WPf.getInstance().get("orderId", String.class);
                String info = WPf.getInstance().get("info", String.class);
                if (StrUtil.isEmptyOrNull(orderId) || orderId.equals("0") || StrUtil.isEmptyOrNull(info)) {
                    return;
                }
                TeighaDWGJni.addMarkUpFromData(info, orderId + "");
            }
        }
    }

    //触发绘制
    public void requestRender() {
        mView.requestRender();
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //关闭下载文件页面
//        ShowDrawRouterUtil.routerActionSync(ctx, "pvmain", "acclosefilescan");    // TODO: 2019/1/28 下载页面finish
        setContentView(R.layout.cad_op_ac_cad);
        ctx = this;
        EventBus.getDefault().register(this);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        rootLayout = (FrameLayout) findViewById(R.id.fl_root);
        sharedTitleView.setBackgroundColor(getResources().getColor(R.color.cad_bottom_banner));
        openPath = getIntent().getStringExtra(ShowDrawKey.KEY_OPEN_PATH);
        if (StrUtil.isEmptyOrNull(openPath))
            return;
        //初始化图纸设置
        File mAppDirectory = ctx.getExternalFilesDir(null);
        String sPath = mAppDirectory.getAbsolutePath() + File.separator + ShowDrawKey.S_FONT;
        TeighaDWGJni.init(sPath);
        mView = new TeighaDwgView(ctx, openPath);
        mView.setOnTouchListener(this);
        llContent.addView(mView);
        if (!new File(openPath).exists()) {
            L.e("路径不存在文件");
            return;
        }
        initView();
        initDrawerlayout();
        initDrawerView();
        mGestureDetector = new GestureDetector(this, new CustomGestureDetector());
        mOffset = getWindowManager().getDefaultDisplay().getHeight()/10;
    }

    private void initDrawerlayout() {
        drawerLayout = (TEDrawerLayout) ctx.findViewById(R.id.drawer_layout);
        if (getActionType() >= ActionTypeEnum.NO.value()) {
            //没有操作权限 不能滑动
            drawerLayout.setbInterception(true);
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
        setSlideDisabled();
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
                            pinMap.put("floorId", item.getFloorId() + "");
                            pinMap.put("handle", item.getHandle());
                            pinMap.put("nodeId", item.getNodeId());
                            ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodelhis", pinMap);
                        }
                    }
                });
            }
        };
        LuRecyclerViewAdapter mAdapter = new LuRecyclerViewAdapter(drawerAdapter);
        rcDrawer.setAdapter(mAdapter);
    }

    @Override
    public void inflateMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cad_op_menu_cad, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        deleteMenu = menu.findItem(R.id.menu_delete);
        modifyMenu = menu.findItem(R.id.menu_modify);
        shareMenu = menu.findItem(R.id.menu_share);
        rightMenu = menu.findItem(R.id.menu_right);
        cancelItem = menu.findItem(R.id.right_cancel);
        saveItem = menu.findItem(R.id.right_save);
        setMenusVisual(true, rightMenu, shareMenu);
        setMenusVisual(false, deleteMenu, modifyMenu, cancelItem, saveItem);
        ViewUtils.showViews();
        return true;
    }


    private void setMenusVisual(boolean viual, MenuItem... menus) {
        for (MenuItem menuItem : menus) {
            menuItem.setVisible(viual);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onOptionsItemSelectedDo(item);
        return super.onOptionsItemSelected(item);
    }

    public void onOptionsItemSelectedDo(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (countMode) {
                sureMenuClick();
                return;
            } else {
                onBackPressed();
            }
        } else if (item.getItemId() == R.id.menu_delete) {
            deleteMenuClick();
        } else if (item.getItemId() == R.id.menu_modify) {
            modifyMenuClick();
        } else if (item.getItemId() == R.id.menu_share) {
            if (isClick) {
                isClick = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //每次点击需要间隔500毫秒才能点击，防止多次点击；
                        isClick = true;
                    }
                }, 1000);
                shareMenuClick();
            }
        }
    }

    private boolean isClick = true;

    @Override
    public void rightClick() {
        ViewUtils.hideViews(sharedTitleView, rcBottom);
    }

    public void sureMenuClick() {
        if (m_drawMode == DrawMode.eMeasureArea) {
            TeighaDWGJni.measureAreaFinish();
        } else if (m_drawMode == DrawMode.eMeasure) {
            TeighaDWGJni.distanceCancel();
        } else if (m_drawMode == DrawMode.eRectText) {
            TeighaDWGJni.rectTextCancel();
        } else if (m_drawMode == DrawMode.ePoint) {
            TeighaDWGJni.pointDimensionSet(ivPoint.getLeft(), ivPoint.getTop() + rcBottom.getHeight()/2);
            bNeedSave = true;
            ViewUtils.hideView(ivPoint);
        }
        m_drawMode = DrawMode.eStardard;
        m_iCount = 0;
        countMode = false;
        ViewUtils.hideView(countView);
        ViewUtils.showView(rcBottom);
        sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
        mView.requestRender();
    }

    public void deleteMenuClick() {
        if (!mSelectType.equals("0")) {
            TeighaDWGJni.deleteSelect();
            bNeedSave = true;
            setMenusVisual(false, deleteMenu, modifyMenu);
            setMenusVisual(true, shareMenu);
        }
        mView.requestRender();
    }

    public void modifyMenuClick() {
        if (mSelectType.equals("2")) {
            String title = "输入标注文字";
            SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
            LayoutInflater mInflater = LayoutInflater.from(ctx);
            View view = mInflater.inflate(R.layout.cad_op_view_new_fold, null);
            final EditText etInput = (EditText) view.findViewById(R.id.et_Input);
            etInput.setHint("文字内容");
            String oldName = TeighaDWGJni.getRectText();

            etInput.setText(oldName);
            etInput.setSelection(oldName.length());

            ShowDrawUtil.autoKeyBoardShow(etInput);
            builder.setTitle(title);
            builder.showBar(false);
            builder.setTitleAttr(true, null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input = etInput.getText().toString();
                    TeighaDWGJni.resetRectText(input);
                    bNeedSave = true;
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setContentView(view);
            builder.create().show();
        }
    }

    public void shareMenuClick() {
//        if (mSelectType != "0") {
//            TeighaDWGJni.invalideViewPoint();
//            TeighaDWGJni.saveDabatase();
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(openPath)));
//            shareIntent.setType("*/*");
//            ctx.startActivity(Intent.createChooser(shareIntent, "发送到"));
//        }
        HashMap<String, String> map = new HashMap<>();
        map.put("nodeId", nodeId);
        map.put("pjId", pjId);
        map.put("nodeType", nodeType);
        map.put("name", sharedTitleView.getTitle().toString());
//        RouterUtil.routerActionSync(ctx, "pvmain", "acsharemode", map);
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

    public void configAngle() {
        if (StrUtil.isEmptyOrNull(portInfo)) {
            return;
        }
        ModelPinInfo pinInfo = JSON.parseObject(portInfo, ModelPinInfo.class);
        if (pinInfo == null) {
            return;
        }
        if (StrUtil.notEmptyOrNull(pinInfo.getViewInfo())) {
            TeighaDWGJni.setViewPortInfo(pinInfo.getViewInfo());
        }
        if (StrUtil.notEmptyOrNull(pinInfo.getInfo())) {
            TeighaDWGJni.addMarkUpFromData(pinInfo.getInfo(), "");
        }
        mView.requestRender();
    }

    private void initView() {
        sharedTitleView.setBackgroundColor(getResources().getColor(R.color.cad_bottom_banner));
        openPath = getIntent().getStringExtra(ShowDrawKey.KEY_OPEN_PATH);
        nodeId = getIntent().getStringExtra(ShowDrawKey.KEY_OPEN_NODEID);
        //如果 selectType = -1的话，代表是新建任务选择视口进来的，要把m_drawMode标记为 Share
        selectType = getIntent().getStringExtra(ShowDrawKey.KEY_SELECT_TYPE);
        portInfo = getIntent().getStringExtra(ShowDrawKey.KEY_PORT_INFO);
        nodeType = getIntent().getStringExtra(ShowDrawKey.KEY_NODE_TYPE);
        String actionStr = getIntent().getStringExtra(ShowDrawKey.KEY_CAN_ACTION);
        pjId = getIntent().getStringExtra("pjId");
        if (StrUtil.isEmptyOrNull(actionStr)) {
            actionType = ActionTypeEnum.NO.value();
        } else {
            actionType = Integer.parseInt(actionStr);
        }
//        if (WeqiaApplication.getInstance().isTourist()) {
//            actionType = ActionTypeEnum.NO.value();
//        }
        if (StrUtil.notEmptyOrNull(selectType) && selectType.equals("SELECT_TASK")) {
            m_drawMode = DrawMode.eShare;
            selectMode = true;
        }
//        if (StrUtil.notEmptyOrNull(nodeType) && nodeType.equals("2")) {
//            //文件列表的模型一律不能操作
//            bCanAction = "2";
//        }
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.cad_op_view_dwg_dialog_loading, null);
        TextView tvShow = (TextView) view.findViewById(R.id.loading_view_show);
        tvShow.setText("图纸正在打开...");
        SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
        dlg = builder.setTitle("请等待").showBar(true).setContentView(view).create();
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (bOpen)
                    TeighaDWGJni.disDraw();
                closeFile();
            }
        });
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        showPop();
        bOpen = false;
        new Thread(new Runnable() {
            public void run() {
//                if (CADApplication.cadLoadSize() == 0) {
                if (TeighaDWGJni.open(openPath))
                    mPdHandler.sendEmptyMessage(0);
            }
        }).start();

        ivSmall = (ImageView) findViewById(R.id.iv_small);
        ivSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showViews(sharedTitleView, rcBottom);
            }
        });

        rcBottom = (RecyclerView) findViewById(R.id.rc_cad_bottom);
        LinearLayoutManager titleManager = new LinearLayoutManager(ctx);
        titleManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcBottom.setLayoutManager(titleManager);

        ivPoint = (MoveImageView) findViewById(R.id.iv_cad_range_arrow);
        ivPoint.setMoveCallBack(new MoveCallBack() {
            @Override
            public void moveTo(int left, int top) {
                //L.e(left + ",,," + top);
                if (m_drawMode == DrawMode.ePoint) {
                    int bottomHeight =  rcBottom.getHeight();
                    //Util.dpToPx(R.id.rc_cad_bottom,)
                    String string = TeighaDWGJni.pointMove(left, top+bottomHeight/2);
                    String[] sourceStrArray = string.split(",");
                    if (2 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                    }
                    mView.requestRender();
                }
            }
            @Override
            public void moveUpTo() {
                //L.e(left + ",,," + top);
                if (m_drawMode == DrawMode.ePoint) {
                    String string = TeighaDWGJni.pointMoveEnd();
                    String[] sourceStrArray = string.split(",");
                    if (4 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                        int newLeft=Integer.parseInt(sourceStrArray[2]);
                        int newTop=Integer.parseInt(sourceStrArray[3]) - rcBottom.getHeight()/2;
                        int newRight = newLeft + ivPoint.getWidth();
                        int newBottom = newTop + ivPoint.getHeight();
                        ivPoint.layout(newLeft,newTop,newRight,newBottom);
                    }
                    mView.requestRender();
                }
            }
        });

        ViewUtils.hideView(ivPoint);

        countView = (LinearLayout) findViewById(R.id.ll_count_view);
        leftName = (TextView) findViewById(R.id.tv_left_name);
        leftValue = (TextView) findViewById(R.id.tv_left_value);
        rightName = (TextView) findViewById(R.id.tv_right_name);
        rightValue = (TextView) findViewById(R.id.tv_right_value);


        bottomViewDatas.add(new BottomViewData(100, "返回全图", R.drawable.cad_op_selor_quantu));
        bottomViewDatas.add(new BottomViewData(200, "选择图层", R.drawable.cad_op_selor_layer));
        if (getActionType() == ActionTypeEnum.YES.value()) {
            bottomViewDatas.add(new BottomViewData(300, "定位", R.drawable.cad_op_label_layer));
        }
        bottomViewDatas.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));

        bottomAdapter = new RcFastAdapter<BottomViewData>(ctx, R.layout.cad_op_view_reused_bottom_button) {
            @Override
            public void bindingData(RcBaseViewHolder holder, BottomViewData item) {
                bindDataDo(holder, item);
            }
        };
        rcBottom.setAdapter(bottomAdapter);
        bottomAdapter.setAll(bottomViewDatas);
    }

    public void showbottom() {
        bottomViewDatas.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));
        bottomAdapter.add(new BottomViewData(400, "功能", R.drawable.cad_op_selor_space));
    }

    public void hidebottom() {
        if (bottomViewDatas.size() > 3) {
            bottomViewDatas.remove(3);
            bottomAdapter.remove(3);
        }
    }

    private void bindDataDo(RcBaseViewHolder holder, final BottomViewData item) {
        FrameLayout cellFl = holder.findViewById(R.id.cell_all);
        TextView tvContent = holder.findViewById(R.id.tv_cad_range);
        Drawable drawable = getResources().getDrawable(item.getDrawId());
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
        cellFl.setLayoutParams(new FrameLayout.LayoutParams(DeviceUtil.getDeviceWidth() / bottomViewDatas.size(), ShowDrawUtil.dip2px(ctx, 47)));
        tvContent.setText(item.getTitle());
        tvContent.setCompoundDrawables(null, drawable, null, null);
        if (item.isSelected())
            tvContent.setSelected(true);
        else
            tvContent.setSelected(false);
//        if (item.getvId() == 200)
//            layerView = tvContent;
//        else if (item.getvId() == 300)
//            layoutView = tvContent;
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomClickDo(item);
            }
        });
    }

    public void resetSelectView() {
        for (int i = 0; i < bottomViewDatas.size(); i++) {
            BottomViewData data = bottomViewDatas.get(i);
            if (data.isSelected()) {
                data.setSelected(false);
                bottomAdapter.notifyItemChanged(i);
            }
        }
    }

    private void selectItem(BottomViewData item) {
        resetSelectView();
        int index = bottomAdapter.getPos(item);
        bottomViewDatas.get(index).setSelected(true);
        bottomAdapter.notifyItemChanged(index);
    }

    private void bottomClickDo(final BottomViewData item) {
        switch (item.getvId()) {
            case 100:
                TeighaDWGJni.zoomAll();
                mView.requestRender();
                break;
            case 200:
                LayerInfo[] infos = TeighaDWGJni.GetLayersInfo();
                if (infos != null) {
                    List<LayerInfo> tmpList = Arrays.asList(infos);
                    ArrayList<LayerInfo> infoArrs = new ArrayList<LayerInfo>(tmpList);
                    infos = null;
                    tmpList = null;
                    selectItem(item);
                    getCadOpHandler().showLayerWindow(rcBottom, infoArrs);
                }
                break;
            case 300:
                //设置图纸状态为，可选择状态
                m_drawMode = DrawMode.eShare;
                isSendProgerss = true;
                break;
            case 400:
                selectItem(item);
                List<String> countInfos = Arrays.asList(getResources().getStringArray(R.array.cad_fucnc));
                getCadOpHandler().showCountWindow(rcBottom, countInfos, new CountClickInterface() {

                    @Override
                    public void countItemClick(String name) {
                        countMode = true;
                        rightMenu.setVisible(false);
                        setMenusVisual(false, rightMenu);

                        ViewUtils.hideView(rcBottom);
                        if (name.equalsIgnoreCase(getString(R.string.cad_fuc_length)) || name.equalsIgnoreCase(getString(R.string.cad_fuc_mark))) {
                            ViewUtils.hideView(countView);
                        } else {
                            ViewUtils.showView(countView);
                        }
                        leftValue.setText("");
                        rightValue.setText("");
                        if (name.equalsIgnoreCase(getString(R.string.cad_fuc_mark))) {
                            m_drawMode = DrawMode.eRectText;
                        } else if (name.equalsIgnoreCase(getString(R.string.cad_fuc_length))) {
                            m_drawMode = DrawMode.eMeasure;
                        } else if (name.equalsIgnoreCase(getString(R.string.cad_fuc_area))) {
                            sharedTitleView.setNavigationIcon(R.drawable.cad_op_queding);
                            leftName.setText("面积(mm²)");
                            rightName.setText("周长(mm)");
                            m_drawMode = DrawMode.eMeasureArea;
                        } else if (name.equalsIgnoreCase(getString(R.string.cad_fuc_point))) {
                            sharedTitleView.setNavigationIcon(R.drawable.cad_op_queding);
                            leftName.setText("X坐标(mm)");
                            rightName.setText("Y坐标(mm)");
                            m_drawMode = DrawMode.ePoint;
                            ViewUtils.showView(ivPoint);
                            String string = TeighaDWGJni.pointMove(ivPoint.getLeft(), ivPoint.getTop() + rcBottom.getHeight()/2);
                            String[] sourceStrArray = string.split(",");
                            if (2 == sourceStrArray.length) {
                                leftValue.setText(sourceStrArray[0]);
                                rightValue.setText(sourceStrArray[1]);
                            }
                        } else if (name.equalsIgnoreCase(getString(R.string.cad_fuc_space))) {
                            String layouts = TeighaDWGJni.getLayoutInfo();
                            if (StrUtil.notEmptyOrNull(layouts)) {
                                String[] layoutInofs = layouts.split(";");
                                String acName = TeighaDWGJni.getActiveLayout();
                                if (StrUtil.isEmptyOrNull(acName))
                                    acName = "";
                                ArrayList<LayoutInfo> layoutInfos = new ArrayList<>();
                                if (layoutInofs != null)
                                    for (String str : layoutInofs) {
                                        if (str.equalsIgnoreCase(acName)) {
                                            layoutInfos.add(new LayoutInfo(str, true));
                                        } else
                                            layoutInfos.add(new LayoutInfo(str, false));
                                    }
//                                selectItem(item);
                                getCadOpHandler().showLayoutWindow(rcBottom, layoutInfos);
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void countOver() {
        countMode = false;
        rightMenu.setVisible(true);
        setMenusVisual(true, rightMenu, shareMenu);
        setMenusVisual(false, deleteMenu, modifyMenu);

        ViewUtils.hideView(countView);
        ViewUtils.showView(rcBottom);
        sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);

    }

    private void closeFile() {
//        CADApplication.addRf(WorkEnum.RefeshKey.DWG_REFRESH);
        if (showDrawInterface != null)
            showDrawInterface.loadEnd(openPath);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 50);
    }

    public void backClick() {
        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        mView.onDestroy();
//        CADApplication.addRf(WorkEnum.RefeshKey.DWG_REFRESH);
        if (showDrawInterface != null)
            showDrawInterface.loadEnd(openPath);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (countMode) {
            if (m_drawMode == DrawMode.eStardard) {
                if (bNeedSave) {
                    TeighaDWGJni.saveDabatase();
                }
                backClick();
            } else if (m_drawMode == DrawMode.eMeasureArea) {
                TeighaDWGJni.measureAreaFinish();
            } else if (m_drawMode == DrawMode.eMeasure) {
                TeighaDWGJni.distanceCancel();
            } else if (m_drawMode == DrawMode.eRectText) {
                TeighaDWGJni.rectTextCancel();
            } else if (m_drawMode == DrawMode.ePoint) {
                ViewUtils.hideView(ivPoint);
            }

            m_drawMode = DrawMode.eStardard;
            m_iCount = 0;
            countMode = false;
            ViewUtils.hideView(countView);
            ViewUtils.showView(rcBottom);
            sharedTitleView.setNavigationIcon(R.drawable.cad_op_title_back);
        } else {
            if (bNeedSave) {
                TeighaDWGJni.saveDabatase();
            }
            backClick();
        }
        mView.requestRender();
    }

//    private void viewClick() {
//        if (sharedTitleView.getVisibility() == View.VISIBLE)
//            ViewUtils.hideViews(sharedTitleView, rcBottom);
//        else {
//            ViewUtils.showViews(sharedTitleView, rcBottom);
//        }
//    }

    public void cancelDlg() {
        if (dlg != null)
            dlg.dismiss();
    }

    @Override
    public void finalize() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
//        if(bNeedSave)
//            TeighaDWGJni.saveDabatase();
        super.onDestroy();
        if (isFinishing()) {
            //           mView.onDestroy();
            TeighaDWGJni.close(openPath);
        }
        TeighaDWGJni.finit();
    }

    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ORBIT = 3;
    private int mTouchMode = NONE;
    // remember some things for zooming
    private PointF mTouchStart = new PointF();
    private PointF mTouchMid = new PointF();
    private float mTouchOldDist = 1f;
    private float mTouchOldRot = 0f;
    private float[] mTouchLastEvent = null;
    private long mTouchLastTime = -1;
    private PointF mTouchInit = new PointF();

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = true;
        switch (m_drawMode) {
            case eStardard:
                ret = DoStandardDraw(event);
                break;
            case eMeasure:          //测量
                ret = DoMeasure(event);
                break;
            case eMeasureArea:
                ret = DoMeasureArea(event);
                break;
            case eRectText:
                ret = DoRectText(event);
                break;
            default:
                ret = DoStandardDraw(event);
        }
        //执行绘制
        mView.requestRender();
        return ret;
    }


    private boolean DoStandardDraw(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchInit.x = event.getX();
                mTouchInit.y = event.getY();
                long thisTime = System.currentTimeMillis();
                mTouchStart.set(event.getX(), event.getY());
                if (thisTime - mTouchLastTime < 200 && mTouchMode == NONE) {
                    // Double click
                    mTouchMode = NONE;
                    TeighaDWGJni.viewScalePoint(event.getX(), event.getY());
                    mTouchLastTime = -1;
                } else {
                    mTouchMode = DRAG;
                    mTouchLastTime = thisTime;
                }
                mSelectIndex = TeighaDWGJni.hitSelectGripPoint(event.getX(), event.getY());
                mTouchLastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    mTouchOldDist = spacing(event);
                    if (mTouchOldDist > 10f) {
                        midPoint(mTouchMid, event);
                        mTouchMode = ZOOM;
                    }
                    mTouchLastEvent = new float[4];
                    mTouchLastEvent[0] = event.getX(0);
                    mTouchLastEvent[1] = event.getX(1);
                    mTouchLastEvent[2] = event.getY(0);
                    mTouchLastEvent[3] = event.getY(1);
                    mTouchOldRot = rotation(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mTouchMode == DRAG && (Math.abs(mTouchStart.x - mTouchInit.x) + Math.abs(mTouchStart.y - mTouchInit.y)) <= 10 && m_drawMode == DrawMode.eStardard) {
                    if (mSelectIndex < 0) {
                        mSelectType = TeighaDWGJni.trySelect(event.getX(), event.getY());
                        if ("1".equals(mSelectType)  || "3".equals(mSelectType))
                            setMenusVisual(true, deleteMenu, shareMenu);
                        else if ("2".equals(mSelectType))
                            setMenusVisual(true, deleteMenu, modifyMenu, shareMenu);
                        else if ("0".equals(mSelectType)) {
                            setMenusVisual(true, shareMenu);
                            setMenusVisual(false, deleteMenu, modifyMenu);
                        }
                        else        //坐标分享
                        {
                            setMenusVisual(true, shareMenu);
                            setMenusVisual(false, deleteMenu, modifyMenu);
                        }
                    }
                }

                if (mTouchMode == DRAG && mSelectIndex > 0) {
                    TeighaDWGJni.moveSelectGripPointEnd(event.getX(), event.getY(), mSelectIndex);
                }
                else if (m_drawMode == DrawMode.ePoint) {
                    String string = TeighaDWGJni.pointMoveEnd();
                    String[] sourceStrArray = string.split(",");
                    if (4 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                        int newLeft=Integer.parseInt(sourceStrArray[2]);
                        int newTop=Integer.parseInt(sourceStrArray[3]) - rcBottom.getHeight()/2;
                        int newRight = newLeft + ivPoint.getWidth();
                        int newBottom = newTop + ivPoint.getHeight();
                        ivPoint.layout(newLeft,newTop,newRight,newBottom);
                    }
                }
                mTouchMode = NONE;
                mTouchLastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == DRAG) {
                    if (mSelectIndex > 0) {
//                        mView.queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                TeighaDWGJni.moveSelectGripPoint(event.getX(), event.getY(), mSelectIndex);
//                            }
//                        });

                        TeighaDWGJni.moveSelectGripPoint(event.getX(), event.getY(), mSelectIndex);

                        bNeedSave = true;
                    } else {
                        float dx = event.getX() - mTouchStart.x;
                        float dy = event.getY() - mTouchStart.y;
                        TeighaDWGJni.viewTranslate(dx, dy);
                        mTouchStart.x += dx;
                        mTouchStart.y += dy;
                        //刷新坐标
                        if (m_drawMode == DrawMode.ePoint) {
                            String string = TeighaDWGJni.pointMove(ivPoint.getLeft(), ivPoint.getTop() + rcBottom.getHeight()/2);
                            String[] sourceStrArray = string.split(",");
                            if (2 == sourceStrArray.length) {
                                leftValue.setText(sourceStrArray[0]);
                                rightValue.setText(sourceStrArray[1]);
                            }
                        }

                    }

                } else if (mTouchMode == ZOOM) {
                    if (event.getPointerCount() > 1) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            float scale = (newDist / mTouchOldDist);
                            TeighaDWGJni.viewScale(scale);
                            mTouchOldDist = newDist;
                            //刷新坐标
                            if (m_drawMode == DrawMode.ePoint) {
                                String string = TeighaDWGJni.pointMove(ivPoint.getLeft(), ivPoint.getTop() + rcBottom.getHeight()/2);
                                String[] sourceStrArray = string.split(",");
                                if (2 == sourceStrArray.length) {
                                    leftValue.setText(sourceStrArray[0]);
                                    rightValue.setText(sourceStrArray[1]);
                                }
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    int m_iCount = 0;

    private boolean DoMeasure(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (0 == m_iCount) {
                    TeighaDWGJni.distanceFirst(event.getX(), event.getY()-mOffset);
                    m_iCount++;
                } else if (1 == m_iCount) {
                    TeighaDWGJni.distanceSecond(event.getX(), event.getY()-mOffset);
                    m_iCount = 0;
                    m_drawMode = DrawMode.eStardard;
                    countMode = false;
                    bNeedSave = true;
                    setMenusVisual(true, deleteMenu, shareMenu);
                    getCadOpHandler().countCloseClick();
                }
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchOldDist = spacing(event);
                if (mTouchOldDist > 10f) {
                    midPoint(mTouchMid, event);
                    mTouchMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = (newDist / mTouchOldDist);
                        TeighaDWGJni.viewScale(scale);
                        mTouchOldDist = newDist;
                    }
                }
                if (1 == m_iCount) {
                    TeighaDWGJni.distanceMove(event.getX(), event.getY()-mOffset);
                }
                else if (0 == m_iCount){
                    TeighaDWGJni.tempPointMove(event.getX(), event.getY()-mOffset);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean DoMeasureArea(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (m_iCount > 0) {
                    String strValue = TeighaDWGJni.measureAreaFirst(event.getX(), event.getY()-mOffset);
                    String[] sourceStrArray = strValue.split(",");
                    if (2 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (0 == m_iCount) {
                    TeighaDWGJni.measureAreaBegin(event.getX(), event.getY()-mOffset);

                    m_iCount++;
                } else if (m_iCount > 0) {
                    String strValue = TeighaDWGJni.measureAreaNext(event.getX(), event.getY()-mOffset);
                    String[] sourceStrArray = strValue.split(",");
                    if (2 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                    }
                    m_iCount++;
                }
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchOldDist = spacing(event);
                if (mTouchOldDist > 10f) {
                    midPoint(mTouchMid, event);
                    mTouchMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = (newDist / mTouchOldDist);
                        TeighaDWGJni.viewScale(scale);
                        mTouchOldDist = newDist;
                    }
                }
                if (m_iCount > 0) {
                    String strValue = TeighaDWGJni.measureAreaMove(event.getX(), event.getY()-mOffset);
                    String[] sourceStrArray = strValue.split(",");
                    if (2 == sourceStrArray.length) {
                        leftValue.setText(sourceStrArray[0]);
                        rightValue.setText(sourceStrArray[1]);
                    }
                }
                else {
                    TeighaDWGJni.tempPointMove(event.getX(), event.getY()-mOffset);
                }

                break;
            default:
                break;
        }
        return true;
    }

    private boolean DoRectText(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (0 == m_iCount) {
                    TeighaDWGJni.rectTextBegin(event.getX(), event.getY());
                    m_iCount++;
                } else if (m_iCount == 1) {
                    TeighaDWGJni.rectTextMove(event.getX(), event.getY());
                    m_iCount = 0;
                    m_drawMode = DrawMode.eStardard;
                    countMode = false;
                    bNeedSave = true;
                    setTextDialog();
                    getCadOpHandler().countCloseClick();
                }
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchOldDist = spacing(event);
                if (mTouchOldDist > 10f) {
                    midPoint(mTouchMid, event);
                    mTouchMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = (newDist / mTouchOldDist);
                        TeighaDWGJni.viewScale(scale);
                        mTouchOldDist = newDist;
                    }
                }
                if (m_iCount == 1) {
                    TeighaDWGJni.rectTextMove(event.getX(), event.getY());
                }
                break;
            default:
                break;
        }
        return true;
    }


    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            //菜单点击
            if (mView.onToolbarClick(event.getX(), event.getY()))
                return true;
            if (m_drawMode == DrawMode.eShare) {
                if (isSendProgerss) {
                    //显示弹框
                    int max = getPinDatas().size() + 1;
                    TeighaDWGJni.cancelAddMarkUp();
                    info = TeighaDWGJni.addMarkUp(event.getX(), event.getY(), String.valueOf(max));
                    addOpView(event.getX(), event.getY());
                } else {
                    //显示确定取消按钮
                    int max = getPinDatas().size() + 1;
                    TeighaDWGJni.cancelAddMarkUp();
                    info = TeighaDWGJni.addMarkUp(event.getX(), event.getY(), String.valueOf(max));
                    setMenusVisual(true, cancelItem, saveItem);
                    setMenusVisual(false, rightMenu);
                }
                WPf.getInstance().put("info", info);
                mView.requestRender();
                return true;
            }
            return false;
        }
    }

    public boolean isSendProgerss;
    private View popView;

    public void toSendProgress() {
        if (pinMap == null) {
            pinMap = new HashMap<>();
        }
        pinMap.put("type", "1");  //   标注1
        pinMap.put("info", info);
        String posPath = PathUtil.getPicturePath() + "/tmp.png";
        String editPath = PathUtil.getPicturePath() + "/" + SystemClock.uptimeMillis() + "cad.png";
        mView.saveScreenShot(posPath);
        // 涂鸦参数
        GraffitiParams params = new GraffitiParams();
        // 图片路径
        params.mImagePath = posPath;
        params.mSavePath = editPath;
        // 初始画笔大小
        params.mPaintSize = GraffitiActivity.DEFAULE_FONTSIZE;
        // 启动涂鸦页面
        GraffitiActivity.startActivityForResult(ctx, params, 1001);
        isSendProgerss = false;
    }

    private void addOpView(float x, float y) {
        //删除上一个弹框
        if (getActionType() >= ActionTypeEnum.NO.value()) {
            return;
        }
        cancelItem.setVisible(true);
        if (pinMap == null) {
            pinMap = new HashMap<>();
        }
        rootLayout.removeView(popView);
        popView = LayoutInflater.from(ctx).inflate(R.layout.cad_op_popup_window_view, null);
        popView.setAlpha(0.9f);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, ShowDrawUtil.dip2px(ctx, 35));
        params.leftMargin = ShowDrawUtil.px2dip(ctx, x);
        if (params.leftMargin > 248) {
            params.leftMargin = 248;
        }
        params.topMargin = (int) y;//ShowDrawUtil.px2dip(ctx, y);
        rootLayout.addView(popView, params);
        ViewUtils.bindClickListenerOnViews(popView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                pinMap.put("nodeId", nodeId);
                if (i == R.id.bt_task) {
                    findViewById(R.id.bt_task).setEnabled(false);
                    toPublishTask();
                } else if (i == R.id.bt_addinfo) {
                    pinMap.put("acptype", "addinfo");
                    pinMap.put("info", info);
                    pinMap.put("type", "1");
                    pinMap.put("viewInfo", TeighaDWGJni.getViewPortInfo());
                    ShowDrawUtil.ronterActionSync(ctx, null, "pvmodel", "acmodel", pinMap);
                } else if (i == R.id.bt_progress) {
                    onSelect();
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
        }, R.id.bt_task, R.id.bt_addinfo, R.id.bt_progress);
    }

    @Override
    protected void toCancelAction() {
        super.toCancelAction();
        if (StrUtil.notEmptyOrNull(selectType) && selectType.equals("SELECT_TASK")) {
            TeighaDWGJni.cancelAddMarkUp();
        } else {
            TeighaDWGJni.cancelAddMarkUp();
            m_drawMode = DrawMode.eStardard;
            cancelItem.setVisible(false);
            rootLayout.removeView(popView);
        }
        mView.requestRender();
    }

    public void onSelect() {
        toSendProgress();
    }

    public RecyclerView getRcBottom() {
        return rcBottom;
    }

    public void getPosDatas(final boolean isAddAll) {
        if (!NetworkUtil.detect(ctx)) {
            L.e("网络错误，直接返回");
            return;
        }
        if (StrUtil.isEmptyOrNull(nodeId)) {
            L.e("nodeId为空，不请求接口");
            return;
        }
        if (getActionType() >= ActionTypeEnum.NO.value()) {
            //不能操作的  不需要请求标注点
            return;
        }
        ServiceParams params = new ServiceParams(3702);
        if (StrUtil.notEmptyOrNull(nodeId)) {
            params.put("nodeId", nodeId);
        }
        UserService.getDataFromServer(params, new ServiceRequester() {

            @Override
            public void onResult(ResultEx resultEx) {
                if (resultEx.isSuccess()) {
                    pinDatas = resultEx.getDataArray(ModelPinInfo.class);
                    if (StrUtil.listNotNull(pinDatas)) {
                        if (isAddAll) {
                            for (ModelPinInfo pinInfo : pinDatas) {
                                if (StrUtil.notEmptyOrNull(pinInfo.getInfo()) && StrUtil.notEmptyOrNull(pinInfo.getOrderId()) && !pinInfo.getOrderId().equals("0")) {
                                    TeighaDWGJni.addMarkUpFromData(pinInfo.getInfo(), pinInfo.getOrderId());
                                }
                            }
                        }
                    }
                    Message message = new Message();
                    message.what = 101;
                    mPdHandler.sendMessage(message);
                    mView.requestRender();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == 1001) {
                TeighaDWGJni.cancelAddMarkUp();
                rootLayout.removeView(popView);
                mView.requestRender();
            }
            return;
        }
        if (requestCode == 1001) {
            String newFilePath = data.getStringExtra(GraffitiActivity.KEY_IMAGE_PATH);
            if (TextUtils.isEmpty(newFilePath)) {
                L.e("涂鸦保存路径出现错误！");
                return;
            }
            toPublicProgress(newFilePath);
        }
    }

    private void toPublishTask() {
        m_drawMode = DrawMode.eStardard;
        if (pinMap == null) {
            pinMap = new HashMap<>();
        }
        final String posPath = PathUtil.getPicturePath() + "/" + SystemClock.uptimeMillis() + "task_cad.jpg";
        new Thread() {
            @Override
            public void run() {
                super.run();
                mView.saveScreenShot(posPath);
            }
        }.start();
        if (!pinMap.containsKey("nodeId")) {
            pinMap.put("nodeId", nodeId);
        }
        pinMap.put("posfile", posPath);
        pinMap.put("info", info);
        pinMap.put("type", "1");
        pinMap.put("selectType", selectType);
        pinMap.put("pjId", pjId);
        pinMap.put("viewInfo", TeighaDWGJni.getViewPortInfo());
        if (selectMode) {
            pinMap.put("selectMode", "1");
            ShowDrawUtil.ronterActionSync(ctx, null, "pvapproval", "acnewapproval", pinMap);
        } else {
/*            final String[] list = {"安全任务", "质量任务", "进度任务", "其他任务"};
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
//            TaskApprovalUtil.toTask(TeighaDwgActivity.this, pjId, pinMap);
        }

        /**
         *选择必须关闭模型界面，不是选择可以不关闭
         */
        if (selectMode) {
            this.finish();
        }
    }

    private void toPublicProgress(String posPath) {
        toCancelSelect();
        if (!pinMap.containsKey("nodeId")) {
            pinMap.put("nodeId", nodeId);
        }
        pinMap.put("posfile", posPath);
        pinMap.put("selectType", selectType);
        pinMap.put("nodeType", nodeType);
        pinMap.put("pjId", pjId);
        pinMap.put("viewInfo", TeighaDWGJni.getViewPortInfo());
        ShowDrawUtil.ronterActionSync(ctx, null, "pvmain", "acpublicprogress", pinMap);
        m_drawMode = DrawMode.eStardard;
    }

    public void toShowSelect() {
        setMenusVisual(true, cancelItem, saveItem);
        setMenusVisual(false, rightMenu);
    }


    public void toCancelSelect() {
        TeighaDWGJni.cancelAddMarkUp();
        setMenusVisual(false, cancelItem, saveItem);
        setMenusVisual(true, rightMenu);
        mView.requestRender();
    }

    @Override
    protected void toSaveAction() {
        super.toSaveAction();
        //当点击确定的时候，发任务
        toCancelSelect();
        toPublishTask();
    }

    public void setTextDialog() {
        String title = "输入标注文字";
        SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
        LayoutInflater mInflater = LayoutInflater.from(ctx);
        View view = mInflater.inflate(R.layout.cad_op_view_new_fold, null);
        final EditText etInput = view.findViewById(R.id.et_Input);
        etInput.setHint("文字内容");
        ShowDrawUtil.autoKeyBoardShow(etInput);
        builder.setTitle(title);
        builder.showBar(false);
        builder.setTitleAttr(true, null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = etInput.getText().toString();
                TeighaDWGJni.setRectText(input);
                dialog.dismiss();
            }
        });

        builder.setContentView(view);
        SharedCommonDialog textDlg = builder.create();
        textDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TeighaDWGJni.rectTextCancel();
                mView.requestRender();
            }
        });
        textDlg.show();
    }

    public CadOpHandler getCadOpHandler() {
        if (cadOpHandler == null)
            cadOpHandler = new CadOpHandler(ctx);
        return cadOpHandler;
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

    private void setSlideEnable() {
        // API版本在4.3以上
    }

    private void setSlideDisabled() {
        //API版本在4.3以上
    }

    /**
     * 关闭侧边栏
     */
    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean ret = true;
        switch (m_drawMode) {
            case eStardard:
                ret = DoStandardDraw(event);
                break;
            case eMeasure:          //测量
                ret = DoMeasure(event);
                break;
            case eMeasureArea:
                ret = DoMeasureArea(event);
                break;
            case eRectText:
                ret = DoRectText(event);
                break;
            default:
                ret = DoStandardDraw(event);
        }
        //执行绘制
        mView.requestRender();
        return ret;
    }
}

