package cn.pinming.cadshow.bim;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.weqia.utils.StrUtil;
import com.weqia.utils.dialog.SharedCommonDialog;

import java.util.HashMap;

import cn.pinming.cadshow.ShowDrawUtil;
import cn.pinming.cadshow.data.ActionTypeEnum;
import cn.pinming.cadshow.library.R;

/**
 * AndroidMobileSurfaceView is the SurfaceView which HPS will render on to.
 * <p>
 * This class serves as a base class for AndroidUserMobileSurfaceView, and
 * users should instead create an instance of AndroidUserMobileSurfaceView.
 * <p>
 * AndroidMobileSurfaceView handles surface creation/destruction and touch input
 * by communicating with the C++ MobileSurface class.
 */
public class AndroidMobileSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static native long create(AndroidMobileSurfaceView view, int surfaceId);

    public static native void onTextInputJS(long ptr, String text);

    public static native void onKeyboardHiddenJ(long ptr);

    public static native boolean bind(long ptr, Object context, Object surface);

    public static native void release(long ptr, int flags);

    public static native void refresh(long ptr);

    public static native void touchDown(long ptr, int numTouches, int[] xArray, int[] yArray, long[] posArray);

    public static native void touchMove(long ptr, int numTouches, int[] xArray, int[] yArray, long[] posArray);

    public static native void touchUp(long ptr, int numTouches, int[] xArray, int[] yArray, long[] posArray);

    public static native void touchesCancel(long ptr);

    public static native void singleTap(long ptr, int x, int y);

    public static native void doubleTap(long ptr, int x, int y, long id);

    private static native void setHoopsViewModeV(long ptr, int iMode, boolean bAnimated);

    public static native void deleteAllDimTexts(long ptr);

    public static native void addDimText(long ptr, float x, float y, String strText);

    public static native boolean trySelectDimText(long ptr, float x, float y);

    public static native String getSelectDimText(long ptr);

    public static native boolean setSelectDimText(long ptr, String strText);

    public static native void unhightLightSelectDimTexts(long ptr);

    public static native boolean hightLightSelectDimText(long ptr);

    public static native boolean deleteSelectDimText(long ptr);

    public static native boolean trySelectItem(long ptr, float x, float y);

    public static native void setSelectOn(long ptr, boolean bOn);

    public static native boolean getSelectState(long ptr);

    public static native void changeSelectState(long ptr);

    /**
     * return    iFloorId,x,y,z,视口信息
     */

    public static native String addMarkUp(long ptr, float x, float y, String strText);

    // 删除刚创建的标记
    public static native boolean deleteCurMarkUp(long ptr);

    public boolean deleteCurMarkUp() {
        return deleteCurMarkUp(mSurfacePointer);
    }

    public static native void deleteAllMarkUp(long ptr);

    public void deleteAllMarkUp() {
        deleteAllMarkUp(mSurfacePointer);
    }

    public static native void addMarkUpFromData(long ptr, String strData, String strFloorName, String strText);

    public static native String deleteSelectMarkUp(long ptr);

    public static native boolean setMarkUpText(long ptr, String strText);

    public static native boolean setViewPortByMark(long ptr, String strText);

    private static native String getViewPortInfoV(long ptr);

    private static native void setViewPortInfoV(long ptr, String strInfo);

    //设置当前打开的pbim类型
    private static native void setProjectMode(long ptr, int mode);

    //获取显示的图层信息
    private static native String getShowLayerInfo(long ptr);

    public String getShowLayerInfo() {
        return getShowLayerInfo(mSurfacePointer);
    }

    //获取显示的楼层熟数
    private static native int getShowFloorCount(long ptr);

    public int getShowFloorCount() {
        return getShowFloorCount(mSurfacePointer);
    }

    private static native void setAllLayersShow(long ptr, boolean bShow);

    public void setAllLayersShow(boolean bShow) {
        setAllLayersShow(mSurfacePointer, bShow);
    }

    //构件标记
    private static native void setEntMark(long ptr, int iFloorId, String strFloorName, String strHandle, String strText);
    public void setEntMark(int iFloorId,String strFloorName, String strHandle, String strText) {
        setEntMark(mSurfacePointer, iFloorId, strFloorName, strHandle, strText);
    }

    //高亮构件
    private static native void highLightEnt(long ptr, int iFloorId, String strFloorName, String strHandle);
    public void highLightEnt(int iFloorId,String strFloorName, String strHandle) {
        highLightEnt(mSurfacePointer, iFloorId, strFloorName, strHandle);
    }

    public void setProjectMode(int iMode) {
        setProjectMode(mSurfacePointer, iMode);
    }

    public void setHoopsViewMode(int iMode,boolean bAnimated) {
        setHoopsViewModeV(mSurfacePointer, iMode, bAnimated);
    }
    public void setHoopsViewMode(int iMode) {
        setHoopsViewModeV(mSurfacePointer, iMode, true);
    }

    //改变选择状态
    public void changeSelectState() {
        changeSelectState(mSurfacePointer);
    }

    //设置选择状态
    public void setSelectOn(boolean bOn) {
        setSelectOn(mSurfacePointer, bOn);
    }

    //获取选择状态
    public boolean getSelectState() {
        return getSelectState(mSurfacePointer);
    }


    private static int SCREEN_ROTATING = 0x00000001;
    private int lastRotation;

    // C++ 端保存的ID
    private int mGuiSurfaceId;
    // C++ 端保存的指针
    protected long mSurfacePointer;

    private GestureDetector mGestureDetector;
    private AndroidMobileSurfaceView.Callback mSurfaceViewCallback;
    private MobileSurfaceActivity ctx;

    //单击时的当前坐标
    public static PointF curentPt = new PointF(0, 0);
    //操作方式
    protected DrawMode m_drawMode = DrawMode.eStardard;
    //标记状态下是否添加过标记
    protected boolean hasAddMarkUp = false;

    public enum DrawMode {
        eStardard,              //常规操作
        eDimText,               //文字标注
        eCullPlanes,                //剖切
        eMarkUp                 //标记
    }
    // 楼层Id与名称的对应
//    HashMap floorMap = new HashMap();
//    public void setFloorMap(HashMap newFloorMap)
//    {
//        floorMap = newFloorMap;
//    }
//    public HashMap getFloorMap()
//    {
//        return floorMap;
//    }

    //revit导出pbim comtype与构件名称对应
    HashMap<String, String> comtypeMap = new HashMap();

    public void setComtypeMap(HashMap newMap) {
        comtypeMap = newMap;
    }

    public HashMap getComtypeMap() {
        return comtypeMap;
    }

    public void setMode(DrawMode mode) {
        m_drawMode = mode;
    }

    public DrawMode getMode() {
        return m_drawMode;
    }

    public void setHasAddMarkUp(boolean state) {
        hasAddMarkUp = state;
    }

    public boolean getHasAddMarkUp() {
        return hasAddMarkUp;
    }

    public void setActivity(MobileSurfaceActivity activity) {
        ctx = activity;
    }

    public void onTextInput(String text) {
        onTextInputJS(mSurfacePointer, text);
    }

    public void onKeyboardHidden() {
        onKeyboardHiddenJ(mSurfacePointer);
    }


    public interface Callback {
        // Called with return value of MobileSurface::bind()
        public void onSurfaceBind(boolean bindRet);

        public void onShowKeyboard();

        public void eraseKeyboardTriggerField();

        public void onShowPerformanceTestResult(float fps);
    }

    public void ShowKeyboard() {
        mSurfaceViewCallback.onShowKeyboard();
    }

    public void ShowPerformanceTestResult(float fps) {
        mSurfaceViewCallback.onShowPerformanceTestResult(fps);
    }

    public int getLastRotation() {
        return lastRotation;
    }

    public void setLastRotation(int lastRotation) {
        this.lastRotation = lastRotation;
    }

    public int getmGuiSurfaceId() {
        return mGuiSurfaceId;
    }

    public void setmGuiSurfaceId(int mGuiSurfaceId) {
        this.mGuiSurfaceId = mGuiSurfaceId;
    }

    public long getmSurfacePointer() {
        return mSurfacePointer;
    }

    public void setmSurfacePointer(long mSurfacePointer) {
        this.mSurfacePointer = mSurfacePointer;
    }

    public GestureDetector getmGestureDetector() {
        return mGestureDetector;
    }

    public void setmGestureDetector(GestureDetector mGestureDetector) {
        this.mGestureDetector = mGestureDetector;
    }

    public Callback getmSurfaceViewCallback() {
        return mSurfaceViewCallback;
    }

    public void setmSurfaceViewCallback(Callback mSurfaceViewCallback) {
        this.mSurfaceViewCallback = mSurfaceViewCallback;
    }

    // Constructor should only be called by derived class
    protected AndroidMobileSurfaceView(Context context, AndroidMobileSurfaceView.Callback svcb, int guiSurfaceId, long savedSurfacePointer) {
        super(context);

        mGuiSurfaceId = guiSurfaceId;

        if (savedSurfacePointer != 0)
            mSurfacePointer = savedSurfacePointer;
        else
            mSurfacePointer = create(this, mGuiSurfaceId);

        mSurfaceViewCallback = svcb;
        getHolder().addCallback(this);

        mGestureDetector = new GestureDetector(context, new CustomGestureDetector());


    }

    public int getGuiSurfaceId() {
        return mGuiSurfaceId;
    }

    public long getSurfacePointer() {
        return mSurfacePointer;
    }

    public void clearTouches() {
        touchesCancel(mSurfacePointer);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        lastRotation = display.getRotation();

        boolean ret = bind(mSurfacePointer, getContext(), getHolder().getSurface());
        if (mSurfaceViewCallback != null)
            mSurfaceViewCallback.onSurfaceBind(ret);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfacePointer != 0) {
            int flags = 0;
            flags |= SCREEN_ROTATING;
            release(mSurfacePointer, flags);
            bind(mSurfacePointer, getContext(), getHolder().getSurface());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        //屏幕旋转
        int flags = 0;
//		if (rotation != lastRotation) {
//			flags |= SCREEN_ROTATING;
//			release(mSurfacePointer, flags);
//		}
        //设置Hoops状态
        if (mSurfacePointer != 0) {
            flags |= SCREEN_ROTATING;
            release(mSurfacePointer, flags);
        }

        lastRotation = rotation;
    }

    public void releaseView() {
        if (mSurfacePointer != 0) {
            release(mSurfacePointer, 0);
            mSurfacePointer = 0;
        }
//		//大文件删除较慢 另起线程
//		new Thread(new Runnable() {
//			public void run() {
//				release(mSurfacePointer, 0);
//			}
//		}).start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        switch (m_drawMode) {
//            case eStardard:
//                return onStandardEvent(e);
//            case eDimText:
//                return onDimTextEvent(e);
//            default:
        return onStandardEvent(e);
//        }
    }

    public boolean onStandardEvent(MotionEvent e) {
        boolean state = mGestureDetector.onTouchEvent(e);
//		if (mGestureDetector.onTouchEvent(e))
//			return true;

        final int action = e.getActionMasked();

        // Only track multiple move changes.  Each separate touch up/down gets its own action.
        int pointerCount = 1;
        if (action == MotionEvent.ACTION_MOVE) {
            pointerCount = e.getPointerCount();
        }

        int[] xposArray = new int[pointerCount];
        int[] yposArray = new int[pointerCount];
        long[] idArray = new long[pointerCount];

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Single touch down
                xposArray[0] = (int) e.getX();
                yposArray[0] = (int) e.getY();
                idArray[0] = e.getPointerId(0);
                touchDown(mSurfacePointer, pointerCount, xposArray, yposArray, idArray);
                break;
            }
            case MotionEvent.ACTION_UP: {
                // Last touch went up
                xposArray[0] = (int) e.getX();
                yposArray[0] = (int) e.getY();
                idArray[0] = e.getPointerId(0);
                touchUp(mSurfacePointer, pointerCount, xposArray, yposArray, idArray);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                // Additional touch down
                final int index = e.getActionIndex();
                xposArray[0] = (int) e.getX(index);
                yposArray[0] = (int) e.getY(index);
                idArray[0] = e.getPointerId(index);
                touchDown(mSurfacePointer, pointerCount, xposArray, yposArray, idArray);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                // Touch went up, but not last
                final int index = e.getActionIndex();
                xposArray[0] = (int) e.getX(index);
                yposArray[0] = (int) e.getY(index);
                idArray[0] = e.getPointerId(index);
                touchUp(mSurfacePointer, pointerCount, xposArray, yposArray, idArray);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Multiple touches move
                for (int i = 0; i < pointerCount; i++) {
                    xposArray[i] = (int) e.getX(i);
                    yposArray[i] = (int) e.getY(i);
                    idArray[i] = e.getPointerId(i);
                }
                touchMove(mSurfacePointer, pointerCount, xposArray, yposArray, idArray);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                touchesCancel(mSurfacePointer);
                break;
            }
            default:
                return super.onTouchEvent(e);
        }

        return true;
    }

    public boolean onDimTextEvent(MotionEvent e) {
        final float X = e.getX(0);
        final float Y = e.getY(0);
        String title = "输入文字";
        SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
        LayoutInflater mInflater = LayoutInflater.from(ctx);
        View view = mInflater.inflate(R.layout.cad_op_view_new_fold, null);
        final EditText etInput = (EditText) view.findViewById(R.id.et_Input);
        etInput.setHint("文字内容");
        ShowDrawUtil.autoKeyBoardShow(etInput);
        builder.setTitle(title);
        builder.showBar(false);
        builder.setTitleAttr(true, null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDrawUtil.hideKeyBoard(etInput);
                String input = etInput.getText().toString();
                if (!input.isEmpty())
                    addDimText(mSurfacePointer, X, Y, input);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDrawUtil.hideKeyBoard(etInput);
                dialog.dismiss();
            }
        });
        builder.setContentView(view);
        builder.create().show();

        return true;
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//			AndroidMobileSurfaceView sv = AndroidMobileSurfaceView.this;
//			AndroidMobileSurfaceView.doubleTap(sv.mSurfacePointer, (int)e.getX(), (int)e.getY(), (int)e.getPointerId(0));
            if (m_drawMode == DrawMode.eStardard) {
                setHoopsViewMode(0);        //双击默认视角
            } else if (m_drawMode == DrawMode.eDimText) {
                final float X = e.getX(0);
                final float Y = e.getY(0);
                if (trySelectDimText(mSurfacePointer, X, Y)) {
                    String title = "修改文字";
                    SharedCommonDialog.Builder builder = new SharedCommonDialog.Builder(ctx);
                    LayoutInflater mInflater = LayoutInflater.from(ctx);
                    View view = mInflater.inflate(R.layout.cad_op_view_new_fold, null);
                    final EditText etInput = (EditText) view.findViewById(R.id.et_Input);
                    etInput.setHint("文字内容");
                    String strText = getSelectDimText(mSurfacePointer);
                    etInput.setText(strText);
                    etInput.setSelection(strText.length());

                    ShowDrawUtil.autoKeyBoardShow(etInput);
                    builder.setTitle(title);
                    builder.showBar(false);
                    builder.setTitleAttr(true, null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShowDrawUtil.hideKeyBoard(etInput);
                            String input = etInput.getText().toString();
                            if (!input.isEmpty())
                                setSelectDimText(mSurfacePointer, input);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShowDrawUtil.hideKeyBoard(etInput);
                            dialog.dismiss();
                        }
                    });
                    builder.setContentView(view);
                    builder.create().show();
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //控制界面
            curentPt.set(e.getX(), e.getY());
            if (m_drawMode == DrawMode.eStardard) {
                //尝试选择
                if (!trySelectItem(mSurfacePointer, e.getX(), e.getY()))
                    singleCLick();
            } else if (m_drawMode == DrawMode.eDimText)        //插入文字标注
            {
                final float X = e.getX(0);
                final float Y = e.getY(0);
                unhightLightSelectDimTexts(mSurfacePointer);
                if (!trySelectDimText(mSurfacePointer, X, Y))
                    onDimTextEvent(e);
                else {
                    hightLightSelectDimText(mSurfacePointer);
                }
            } else if (m_drawMode == DrawMode.eMarkUp)        //插入标记
            {
                //删除前一个标记
                if (hasAddMarkUp)
                    deleteCurMarkUp();

                // iFloorId,x,y,z,视口信息

                /**
                 *最大点序号
                 */
                String text = "1";
                if (StrUtil.listNotNull(ctx.getPinDatas())) {
                    text = ctx.getPinDatas().size() + 1 + "";
                }
                String strInfo = addMarkUp(mSurfacePointer, e.getX(0), e.getY(0), text);
                String[] splitArr = strInfo.split("@#@");
                if (2 == splitArr.length) {
                    hasAddMarkUp = true;
                    onSelect(-1,splitArr[0], splitArr[1], "", -1, 1);
                }
            }
            return true;
        }
    }

    //Activity重写方法
    public void singleCLick() {

    }

    /*
            hoops选中响应
            iType = 1 标记
            iType = 2 构件
     */
    private String strFloorName;
    private String strHandle;

    public String getStrFloorName() {
        return strFloorName;
    }

    public String getStrHandle() {
        return strHandle;
    }

    public void onSelect(int iFloorId,String strFloorName, String strHandle, String strName, int iComid, int iType) {
        this.strFloorName = strFloorName;
        this.strHandle = strHandle;
        //当前坐标
        float x = curentPt.x;
        float y = curentPt.y;
//        L.e("x:" + ShowDrawUtil.px2dip(ctx, x) + "y:" + ShowDrawUtil.px2dip(ctx, y));
        HashMap<String, String> dataMap = new HashMap();
        dataMap.put("handle", strHandle);
        dataMap.put("componentId", iComid + "");
        if (1 == iType) {          //标记
            dataMap.put("floorName", strFloorName);
            dataMap.put("floorId", iFloorId + "");
            dataMap.put("name", strName);
            dataMap.put("type", "1");            //  视口0   标注1   构件2
            dataMap.put("info", strHandle);
        } else {
            //构件
            dataMap.put("floorName", strFloorName);
            dataMap.put("floorId", iFloorId + "");
            dataMap.put("name", "");
            dataMap.put("type", "2");            //  视口0   标注1   构件2
            dataMap.put("info", strHandle);
        }
        if (-1 == iType) {
            /**
             *取消选择状态
             */
            ctx.toCancelAction();
        } else {
            // 发进展- 截图
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

    /*
        获取视口信息
        out_width,out_height,ptPosition.x,ptPosition.y,ptPosition.z,ptTarget.x,ptTarget.y,ptTarget.z,out_up_vector.x,out_up_vector.y,out_up_vector.z
     */

    public String getViewPortInfo() {
        return getViewPortInfoV(mSurfacePointer);
    }

    /*
        设置视口信息
     */
    public void setViewPortInfo(String strInfo) {
        setViewPortInfoV(mSurfacePointer, strInfo);
    }

    /*
      设置标记视口信息
   */
    public void setViewPortByMark(String strInfo) {
        setViewPortByMark(mSurfacePointer, strInfo);
    }

    /*
        添加标记
     */
    public void addMarkUpFromData(String strData, String strFloorName, String strText) {
        addMarkUpFromData(mSurfacePointer, strData, strFloorName, strText);
    }

    /**
     * @return 返回视口信息
     */

    public HashMap getViewPortDataMap() {
        HashMap<String, String> dataMap = new HashMap();
        dataMap.put("floorName", "");
        dataMap.put("floorId", "");
        dataMap.put("name", "");
        dataMap.put("type", "0");            //  视口0   标注1   构件2
        String strInfo = getViewPortInfoV(mSurfacePointer);
        dataMap.put("info", strInfo);
        return dataMap;
    }
}
