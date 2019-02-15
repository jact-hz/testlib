package cn.pinming.cadshow.bim.custormview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by ML on 2017/5/6.
 */

public class CustormFrameLayout extends FrameLayout {

    public CustormFrameLayout(Context context) {
        super(context);
    }

    public CustormFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        L.w("CustormFrameLayout  dispatchTouchEvent   " + MotionEventType.getEnventTypeMsg(ev));
        boolean isDispatch = super.dispatchTouchEvent(ev);
//        L.w("CustormFrameLayout  dispatchTouchEvent   " + MotionEventType.getEnventTypeMsg(ev) + "   " + isDispatch);
        return isDispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        L.w("CustormFrameLayout  onInterceptTouchEvent   " + MotionEventType.getEnventTypeMsg(ev));
        boolean isIntercept = super.onInterceptTouchEvent(ev);
//        L.w("CustormFrameLayout  onInterceptTouchEvent   " + MotionEventType.getEnventTypeMsg(ev) + "   " + isIntercept);
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        L.w("CustormFrameLayout  onTouchEvent   " + MotionEventType.getEnventTypeMsg(event));
        boolean isTouch = super.onTouchEvent(event);
//        L.w("CustormFrameLayout  onTouchEvent   " + MotionEventType.getEnventTypeMsg(event) + "   " + isTouch);
        return isTouch;
    }
}
