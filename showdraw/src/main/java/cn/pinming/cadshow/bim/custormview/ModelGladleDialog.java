package cn.pinming.cadshow.bim.custormview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weqia.component.rcmode.RcBaseViewHolder;
import com.weqia.component.rcmode.adapter.RcFastAdapter;
import com.weqia.component.rcmode.recyclerView.LuRecyclerView;
import com.weqia.component.rcmode.recyclerView.LuRecyclerViewAdapter;
import com.weqia.utils.DeviceUtil;
import com.weqia.utils.StrUtil;
import com.weqia.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;

import cn.pinming.cadshow.ShowDrawUtil;
import cn.pinming.cadshow.bim.MobileSurfaceActivity;
import cn.pinming.cadshow.bim.tree.DrawerTreeData;
import cn.pinming.cadshow.bim.tree.DrawerTreeHelper;
import cn.pinming.cadshow.library.R;

/**
 * Created by 20161005 on 2017/9/14.
 */

public abstract class ModelGladleDialog extends PopupWindow implements View.OnClickListener {
    private MobileSurfaceActivity ctx;

    private PopupWindow popupWindow;
    private TextView tvClear;
    private TextView tvTitle;
    private TextView tvSelect;
    private TextView btnSure;
    private RecyclerView recyclerView;

    private RcFastAdapter<DrawerTreeData> drawerAdapter;
    private boolean isFloor;
    /**
     * 侧边栏初始字符串数据
     */
    private ArrayList<DrawerTreeData> sortDarwerDatas = new ArrayList<>();
    private ArrayList<DrawerTreeData> visibleDarwerDatas = new ArrayList<>();
    private ArrayList<DrawerTreeData> allErrorDatas = new ArrayList<>();
    private ArrayList<DrawerTreeData> noRepeatDatas = new ArrayList<>();

    public ModelGladleDialog(MobileSurfaceActivity ctx, boolean isFloor) {
        this.ctx = ctx;
        this.isFloor = isFloor;
        initView();
        // 实例化一个ColorDrawable颜色为全透明
        ColorDrawable dw = new ColorDrawable(ctx.getResources().getColor(android.R.color.transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public abstract void onSureClickLintener();

    public void refreshPopWindowSize() {
        WindowManager manager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display metrics = manager.getDefaultDisplay();
        View rootView = LayoutInflater.from(ctx).inflate(R.layout.cad_op_dialog_floor_view, null);
        int width = (int) (metrics.getWidth());
        int hight = (int) (metrics.getHeight());
        int popWidth = (int) (metrics.getWidth() * 0.7);
        int popHeight = (int) (metrics.getHeight() * 0.6);
        popupWindow.setWidth(popWidth);
        popupWindow.setHeight(popHeight);
    }


    private void initView() {
        WindowManager manager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display metrics = manager.getDefaultDisplay();
        View rootView = LayoutInflater.from(ctx).inflate(R.layout.cad_op_dialog_floor_view, null);
        int width = (int) (metrics.getWidth());
        int hight = (int) (metrics.getHeight());
        int popWidth = (int) (metrics.getWidth() * 0.7);
        int popHeight = (int) (metrics.getHeight() * 0.6);
//        L.e("width :" + width + " hight :" + hight + " popWidth :" + popWidth + " popHeight : " + popHeight);
        popupWindow = new PopupWindow(rootView, popWidth, popHeight);
        // 实例化一个ColorDrawable颜色为全透明
        ColorDrawable dw = new ColorDrawable(ctx.getResources().getColor(android.R.color.transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);
        tvClear = (TextView) rootView.findViewById(R.id.tv_clear);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        if (isFloor) {
            tvTitle.setText("楼层选择");
        } else {
            tvTitle.setText("构件选择");
        }
        tvSelect = (TextView) rootView.findViewById(R.id.tv_select);
        btnSure = (TextView) rootView.findViewById(R.id.btn_sure);
        recyclerView = (LuRecyclerView) rootView.findViewById(R.id.rcFloor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        drawerAdapter = new RcFastAdapter<DrawerTreeData>(ctx, R.layout.cad_cell_gradle_view) {
            @Override
            public void bindingData(RcBaseViewHolder rcBaseViewHolder, final DrawerTreeData drawerTreeData) {
                LinearLayout allView = rcBaseViewHolder.getView(R.id.allView);
                FrameLayout allSelectView = rcBaseViewHolder.getView(R.id.fl_select_box);
                final CheckBox selectBox = rcBaseViewHolder.getView(R.id.select_box);
                TextView tvTitle = rcBaseViewHolder.getView(R.id.tv_msg);
                ImageView ivExpand = rcBaseViewHolder.getView(R.id.iv_expand);
                tvTitle.setText(drawerTreeData.getTypeString());
                if (isFloor) {
//                    ViewUtils.hideView(ivExpand);
//                    tvTitle.setPadding(ShowDrawUtil.dip2px(ctx, 12), 0, 0, 0);
                    if (drawerTreeData.isbExpand()) {
                        ivExpand.setImageResource(R.drawable.cad_op_icon_fenlei_xia);
                    } else {
                        ivExpand.setImageResource(R.drawable.cad_op_icon_fenlei_you);
                    }
                    int marginLeft;
                    if (drawerTreeData.getLevel() == 1) {
                        ViewUtils.hideView(ivExpand);
                        marginLeft = ShowDrawUtil.dip2px(ctx, 10 * 3);
//                        tvTitle.setPadding(ShowDrawUtil.dip2px(ctx, 30), 0, 0, 0);
                        tvTitle.setPadding(0, 0, 0, 0);
                    } else {
                        ViewUtils.showView(ivExpand);
                        marginLeft = ShowDrawUtil.dip2px(ctx, 10 * 1);
                        tvTitle.setPadding(0, 0, 0, 0);
                    }
                    int marginRght = ShowDrawUtil.dip2px(ctx, 10);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShowDrawUtil.dip2px(ctx, 40));
                    params.leftMargin = marginLeft;
                    params.rightMargin = marginRght;
                    allView.setOrientation(LinearLayout.HORIZONTAL);
                    allView.setLayoutParams(params);
                } else {
                    if (drawerTreeData.isbExpand()) {
                        ivExpand.setImageResource(R.drawable.cad_op_icon_fenlei_xia);
                    } else {
                        ivExpand.setImageResource(R.drawable.cad_op_icon_fenlei_you);
                    }
                    int marginLeft;
                    if (drawerTreeData.getLevel() == 3) {
                        ViewUtils.hideView(ivExpand);
                        marginLeft = ShowDrawUtil.dip2px(ctx, 10 * (drawerTreeData.getLevel()));
//                        tvTitle.setPadding(ShowDrawUtil.dip2px(ctx, 30), 0, 0, 0);
                        tvTitle.setPadding(0, 0, 0, 0);
                    } else {
                        ViewUtils.showView(ivExpand);
                        marginLeft = ShowDrawUtil.dip2px(ctx, 10 * (drawerTreeData.getLevel() - 1));
                        tvTitle.setPadding(0, 0, 0, 0);
                    }
                    int marginRght = ShowDrawUtil.dip2px(ctx, 10);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShowDrawUtil.dip2px(ctx, 40));
                    params.leftMargin = marginLeft;
                    params.rightMargin = marginRght;
                    allView.setOrientation(LinearLayout.HORIZONTAL);
                    allView.setLayoutParams(params);
                }
                selectBox.setSelected(drawerTreeData.isbSelected());
                allSelectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFloor) {
//                            selectBox.setSelected(!drawerTreeData.isbExpand());
//                            drawerTreeData.setbSelected(!drawerTreeData.isbExpand());
//                            drawerTreeData.setbExpand(!drawerTreeData.isbExpand());
                            selectBox.setSelected(!selectBox.isSelected());
                            drawerTreeData.setbSelected(selectBox.isSelected());
                            toOnRootItemClickAction();
                        } else {
                            selectBox.setSelected(!selectBox.isSelected());
                            drawerTreeData.setbSelected(selectBox.isSelected());
                            toOnItemClickAction();
                        }
                    }
                });
                selectBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFloor) {
//                            selectBox.setSelected(!drawerTreeData.isbExpand());
//                            drawerTreeData.setbSelected(!drawerTreeData.isbExpand());
//                            drawerTreeData.setbExpand(!drawerTreeData.isbExpand());
                            selectBox.setSelected(!selectBox.isSelected());
                            drawerTreeData.setbSelected(selectBox.isSelected());
                            toOnRootItemClickAction();
                        } else {
                            selectBox.setSelected(!selectBox.isSelected());
                            drawerTreeData.setbSelected(selectBox.isSelected());
                            toOnItemClickAction();
                        }
                    }
                });

                allView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFloor) {
//                            selectBox.setSelected(!drawerTreeData.isbExpand());
//                            drawerTreeData.setbSelected(!drawerTreeData.isbExpand());
                            drawerTreeData.setbExpand(!drawerTreeData.isbExpand());
                            toOnRootItemClickAction();
                        } else {
                            drawerTreeData.setbExpand(!drawerTreeData.isbExpand());
                            toOnItemClickAction();
                        }
                    }
                });
            }
        };
        LuRecyclerViewAdapter mAdapter = new LuRecyclerViewAdapter(drawerAdapter);
        recyclerView.setAdapter(mAdapter);
        ViewUtils.bindClickListenerOnViews(this, tvClear, tvSelect, btnSure);
    }

    @Override
    public void onClick(View v) {
        if (v == tvClear) {
            for (DrawerTreeData data : sortDarwerDatas) {
                data.setbExpand(false);
                data.setbSelected(false);
            }
            if (isFloor) {
//                refreshLeftDrawerVIew();
                toOnRootItemClickAction();
            } else {
                toOnItemClickAction();
            }
        } else if (v == tvSelect) {
            for (DrawerTreeData data : sortDarwerDatas) {
                data.setbSelected(true);
            }
            if (isFloor) {
//                refreshLeftDrawerVIew();
                toOnRootItemClickAction();
            } else {
                toOnItemClickAction();
            }
        } else if (v == btnSure) {
            onSureClickLintener();
            popupWindow.dismiss();
        }
    }

    public void showPopSaixuan(View parent) {
        refreshLeftDrawerVIew();
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        View rootView = ((ViewGroup) ctx.getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        int mWidth = (DeviceUtil.getDeviceWidth()-popupWindow.getWidth())/2;
        int mHeight = (DeviceUtil.getDeviceHeight()-popupWindow.getHeight())/2;
        popupWindow.showAtLocation(rootView, Gravity.LEFT|Gravity.TOP, mWidth, mHeight);
//        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        popupWindow.update();
    }

    public ArrayList<DrawerTreeData> getSortDarwerDatas() {
        return sortDarwerDatas;
    }

    /**
     * 刷新侧边栏列表
     */
    private void refreshLeftDrawerVIew() {
        if (isFloor) {
//            visibleDarwerDatas = DrawerTreeHelper.filterRootNode(sortDarwerDatas);
            visibleDarwerDatas = DrawerTreeHelper.allSelRootNode(sortDarwerDatas);
        } else {
            // 当缓存没有数据的时候， 对显示的构件进行重组，去掉重复的！有缓存数据，就直接取缓存数据展示
            if (StrUtil.listIsNull(visibleDarwerDatas)) {
                allErrorDatas = DrawerTreeHelper.allSelLefaNode(sortDarwerDatas);
                if (StrUtil.listNotNull(allErrorDatas)) {
                    HashSet<String> twoLevelLabel = new HashSet<>();
                    for (DrawerTreeData twoData : allErrorDatas) {
                        if (twoData.getLevel() == 2) {
                            boolean isHave = twoLevelLabel.add(twoData.getTypeIndex() + "");
                            if (isHave) {
                                twoData.getChildDatas().clear();
                                visibleDarwerDatas.add(twoData);
                                noRepeatDatas.add(twoData);
                                HashSet<String> threeLevelLabel = new HashSet<>();
                                for (DrawerTreeData threeLevelData : allErrorDatas) {
                                    if (threeLevelData.getLevel() == 3 && threeLevelData.getParentData().getTypeIndex() == twoData.getTypeIndex()) {
                                        boolean isDataHave = threeLevelLabel.add(threeLevelData.getTypeIndex() + "");
                                        threeLevelData.setParentData(twoData);
                                        if (isDataHave) {
                                            noRepeatDatas.add(threeLevelData);
                                            twoData.getChildDatas().add(threeLevelData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        drawerAdapter.setAll(visibleDarwerDatas);
    }

    public void toOnItemClickAction() {
        visibleDarwerDatas = DrawerTreeHelper.filterVisibleLeafNode(noRepeatDatas);
//        L.e("visibleDarwerDatas : " + visibleDarwerDatas.toString());
        drawerAdapter.setAll(visibleDarwerDatas);
    }

    public void toOnRootItemClickAction() {
        visibleDarwerDatas = DrawerTreeHelper.allSelRootNode(sortDarwerDatas);
//        L.e("visibleDarwerDatas : " + visibleDarwerDatas.toString());
        drawerAdapter.setAll(visibleDarwerDatas);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setSortDarwerDatas(ArrayList<DrawerTreeData> sortDarwerDatas) {
        this.sortDarwerDatas = sortDarwerDatas;
    }

    public ArrayList<DrawerTreeData> getVisibleDarwerDatas() {
        return visibleDarwerDatas;
    }

    public ArrayList<DrawerTreeData> getNoRepeatDatas() {
        return noRepeatDatas;
    }

    public void setNoRepeatDatas(ArrayList<DrawerTreeData> noRepeatDatas) {
        this.noRepeatDatas = noRepeatDatas;
    }

    public void setVisibleDarwerDatas(ArrayList<DrawerTreeData> visibleDarwerDatas) {
        this.visibleDarwerDatas = visibleDarwerDatas;
    }

    public int dip2px(float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

