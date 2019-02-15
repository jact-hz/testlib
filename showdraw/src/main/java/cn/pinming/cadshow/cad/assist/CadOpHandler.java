package cn.pinming.cadshow.cad.assist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weqia.component.rcmode.RcBaseViewHolder;
import com.weqia.component.rcmode.adapter.RcFastAdapter;
import com.weqia.utils.L;
import com.weqia.utils.ViewUtils;
import com.weqia.utils.view.CommonImageView;

import java.util.ArrayList;
import java.util.List;

import cn.pinming.cadshow.library.R;
import cn.pinming.cadshow.cad.TeighaDWGJni;
import cn.pinming.cadshow.cad.TeighaDwgActivity;
import cn.pinming.cadshow.cad.data.LayerInfo;
import cn.pinming.cadshow.cad.data.LayoutInfo;

/**
 * Created by berwin on 2017/3/15.
 */

public class CadOpHandler {

    private TeighaDwgActivity ctx;
    private PopupWindow layerPop;
    private ArrayList<LayerInfo> infos;

    private PopupWindow layoutPop;
    private ArrayList<LayoutInfo> layoutInfos;

    private PopupWindow countPop;
//    private ArrayList<CountInfo> countInfos;

    public CadOpHandler(TeighaDwgActivity ctx) {
        this.ctx = ctx;
    }


    public void countCloseClick() {
        ViewUtils.hideViews(ctx, R.id.ll_count_view);
        ViewUtils.showView(ctx.getRcBottom());
        if (ctx.getRightMenu() != null)
            ctx.getRightMenu().setVisible(true);
    }

    public void showCountWindow(View parent, final List<String> tmpInfos, final CountClickInterface clickInterface) {
        RecyclerView lv_group = null;
        View dlgView = null;
        final RcFastAdapter layerAdapter = new RcFastAdapter<String>(ctx, R.layout.cad_op_cell_dialog_layerinfo, tmpInfos) {
            @Override
            public void bindingData(RcBaseViewHolder holder, final String item) {
                CommonImageView ivCount = holder.getView(R.id.iv_catelog_image);
                TextView tvCountName = holder.getView(R.id.tv_catelog_title);
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
            LayoutInflater layoutInflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dlgView = layoutInflater.inflate(R.layout.cad_op_layer_full_screen_dialog, null);
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
            // 创建一个PopuWidow对象
            countPop =
                    new PopupWindow(dlgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                            true);
        }
        lv_group.setAdapter(layerAdapter);
        layerAdapter.setAll(tmpInfos);

        // 使其聚集
        countPop.setFocusable(true);
        // 设置允许在外点击消失
        countPop.setOutsideTouchable(true);
        countPop.setOnDismissListener(dismissListener);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        countPop.setBackgroundDrawable(new BitmapDrawable());
        countPop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示
     *
     * @param parent
     */
    public void showLayerWindow(View parent, ArrayList<LayerInfo> tmpInfos) {
        this.infos = tmpInfos;
        RecyclerView lv_group = null;
        View dlgView = null;
        final RcFastAdapter layerAdapter = new RcFastAdapter<LayerInfo>(ctx, R.layout.cad_op_cell_dialog_layerinfo, infos) {
            @Override
            public void bindingData(RcBaseViewHolder holder, final LayerInfo item) {
                View view = holder.getView(R.id.ll_layinfo_content);
                CommonImageView ivCatelog = holder.getView(R.id.iv_catelog_image);
                TextView tvCatelogName = holder.getView(R.id.tv_catelog_title);
                ImageView btSelected = holder.getView(R.id.ib_catelog_select);

                int color = item.getiColor();
                int red = color >> 16;
                int green = ((char) color) >> 8;
                int blue = color & 0x0000ff;
                ivCatelog.setBackgroundColor(Color.rgb(red, green, blue));
                tvCatelogName.setText(item.getStrLayerName());
                btSelected.setSelected(item.isShow());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item != null) {
                            TeighaDWGJni.changeLayerShowHide(item.getStrLayerName());
                            item.setShow(!item.isShow());
                            setAll(infos);
                            ctx.requestRender();
                        }
                    }
                });
            }
        };
        if (layerPop == null || lv_group == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dlgView = layoutInflater.inflate(R.layout.cad_op_layer_full_screen_dialog, null);
            lv_group = (RecyclerView) dlgView.findViewById(R.id.lv_pic_item);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
            lv_group.setLayoutManager(layoutManager);

            ViewUtils.bindClickListenerOnViews(dlgView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.view_empty || v.getId() == R.id.view_empty_two) {
                        if (layerPop != null) {
                            dismisPop(layerPop);
                        }
                    }
                }
            }, R.id.view_empty, R.id.view_empty_two);
            // 创建一个PopuWidow对象
            layerPop =
                    new PopupWindow(dlgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                            true);
        }
        lv_group.setAdapter(layerAdapter);
        layerAdapter.setAll(tmpInfos);

        // 使其聚集
        layerPop.setFocusable(true);
        // 设置允许在外点击消失
        layerPop.setOutsideTouchable(true);
        layerPop.setOnDismissListener(dismissListener);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        layerPop.setBackgroundDrawable(new BitmapDrawable());
        layerPop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public void showLayoutWindow(View parent, ArrayList<LayoutInfo> layouts) {
        this.layoutInfos = layouts;
        RecyclerView lv_group = null;
        View dlgView = null;
        final RcFastAdapter layerAdapter = new RcFastAdapter<LayoutInfo>(ctx, R.layout.cad_op_cell_dialog_layerinfo, layoutInfos) {
            @Override
            public void bindingData(RcBaseViewHolder holder, final LayoutInfo item) {
                View view = holder.getView(R.id.ll_layinfo_content);
                CommonImageView ivCatelog = holder.getView(R.id.iv_catelog_image);
                TextView tvCatelogName = holder.getView(R.id.tv_catelog_title);
                ImageView btSelected = holder.getView(R.id.ib_catelog_select);
                ViewUtils.hideView(ivCatelog);
                tvCatelogName.setText(item.getName());
                btSelected.setSelected(item.isShow());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (layoutPop != null)
                            dismisPop(layoutPop);
                        if (item != null) {
                            String curName = item.getName();
                            if (curName.equalsIgnoreCase(TeighaDWGJni.getActiveLayout())) {
                                L.e("已经是现有布局");
                                return;
                            }
                            TeighaDWGJni.setActiveLayout(curName);
                            ctx.requestRender();
                            //model布局才显示功能按钮
                            if (curName.equalsIgnoreCase("Model"))
                                ctx.showbottom();
                            else
//                                ctx.hidebottom();
                            for (LayoutInfo info : layoutInfos) {
                                if (curName.equalsIgnoreCase(info.getName()))
                                    info.setShow(true);
                                else
                                    info.setShow(false);
                            }
                            setAll(layoutInfos);
                        }
                    }
                });
            }
        };
        if (layoutPop == null || lv_group == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dlgView = layoutInflater.inflate(R.layout.cad_op_layer_full_screen_dialog, null);
            lv_group = (RecyclerView) dlgView.findViewById(R.id.lv_pic_item);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
            lv_group.setLayoutManager(layoutManager);

            ViewUtils.bindClickListenerOnViews(dlgView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.view_empty || v.getId() == R.id.view_empty_two) {
                        if (layoutPop != null) {
                            dismisPop(layoutPop);
                            layoutPop = null;
                        }
                    }
                }
            }, R.id.view_empty, R.id.view_empty_two);
            // 创建一个PopuWidow对象
            layoutPop =
                    new PopupWindow(dlgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                            true);
        }
        lv_group.setAdapter(layerAdapter);
        layerAdapter.setAll(layoutInfos);

        // 使其聚集
        layoutPop.setFocusable(true);
        // 设置允许在外点击消失
        layoutPop.setOutsideTouchable(true);
        layoutPop.setOnDismissListener(dismissListener);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        layoutPop.setBackgroundDrawable(new BitmapDrawable());
        layoutPop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            ctx.resetSelectView();
        }
    };

    private void dismisPop(PopupWindow pop) {
        pop.dismiss();
    }
}
