package cn.pinming.cadshow.view;

import android.animation.Animator;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.weqia.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by berwin on 2018/3/14.
 */

public class FlowBtn {

    private View fabBGLayout;
    boolean isFABOpen = false;
    private ArrayList<View> fabs;

    private Activity ctx;
    private int btnNum;
    private float tranceY;
    private View tvFlow;
    private FlowBtnClickInterface flowBtnClickInterface;

    public FlowBtn(Activity ctx) {
        this.ctx = ctx;
    }

    public interface FlowBtnClickInterface{
        void onClick(View view);

        void onFlowOpen();

        void onFlowClose();
    }

    public void initFlowBtn(float tranceY, ArrayList<View> fabs, View fabBg,
                            @Nullable final FlowBtnClickInterface flowBtnClickInterface) {
        btnNum = fabs.size() - 1;
        this.fabs = new ArrayList<>(btnNum);
        this.tranceY = tranceY;
        this.fabBGLayout = fabBg;
        this.flowBtnClickInterface = flowBtnClickInterface;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == tvFlow) {
                    if (!isFABOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                        if (flowBtnClickInterface != null)
                            flowBtnClickInterface.onClick(v);
                    }
                } else {
                    closeFABMenu();
                    if (flowBtnClickInterface != null)
                        flowBtnClickInterface.onClick(v);
                }
            }
        };
        for (int i = 0; i < fabs.size(); i++) {
            View tv = fabs.get(i);
            if (i == 0) {
                tvFlow = tv;
                ViewUtils.showView(tv);
            } else {
                ViewUtils.hideView(tv);
                this.fabs.add(tv);
            }
            tv.setOnClickListener(onClickListener);
        }

        if (fabBGLayout != null)
            fabBGLayout.setOnClickListener(onClickListener);
    }

    public void showFABMenu() {
        if (flowBtnClickInterface != null)
            flowBtnClickInterface.onFlowOpen();
        isFABOpen = true;
        tvFlow.animate().rotation(90);
        for (int i = 0; i < btnNum; i++) {
            View tv = fabs.get(i);
            ViewUtils.showView(tv);
            tv.animate().translationY(-(i + 1) * tranceY);
        }
        ViewUtils.showView(fabBGLayout);
    }

    public void closeFABMenu() {
        if (flowBtnClickInterface != null)
            flowBtnClickInterface.onFlowClose();
        isFABOpen = false;
        ViewUtils.hideView(fabBGLayout);
        tvFlow.animate().rotation(-90);
        for (int i = 0; i < btnNum; i++) {
            View tv = fabs.get(i);
            if (i == btnNum - 1) {
                tv.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!isFABOpen) {
                            for (View tv : fabs) {
                                ViewUtils.hideView(tv);
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            } else
                tv.animate().translationY(0);
        }

    }
}
