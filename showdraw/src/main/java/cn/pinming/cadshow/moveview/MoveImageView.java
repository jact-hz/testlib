package cn.pinming.cadshow.moveview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.weqia.utils.DeviceUtil;

public class MoveImageView extends android.support.v7.widget.AppCompatImageView {

    private int lastX = 0;
    private int lastY = 0;

    private MoveCallBack moveCallBack;

    private static final int screenWidth = DeviceUtil.getDeviceWidth();
    //屏幕宽度
    private static final int screenHeight = DeviceUtil.getDeviceHeight();
//屏幕高度

    public MoveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveCallBack getMoveCallBack() {
        return moveCallBack;
    }

    public void setMoveCallBack(MoveCallBack moveCallBack) {
        this.moveCallBack = moveCallBack;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                layout(left, top, right, bottom);
                if (moveCallBack != null)
                    moveCallBack.moveTo(left, top);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (moveCallBack != null) {
                    moveCallBack.moveUpTo();
                }
                break;
            default:
                break;
        }
        return true;
    }

}