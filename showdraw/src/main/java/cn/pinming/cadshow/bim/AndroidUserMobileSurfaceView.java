package cn.pinming.cadshow.bim;

// Auto-generated file

import android.content.Context;
import android.util.AttributeSet;

import java.util.HashMap;

public abstract class AndroidUserMobileSurfaceView extends AndroidMobileSurfaceView {
    private static native boolean loadFileS(long ptr, String fileName);

    private static native void setOperatorOrbitV(long ptr);

    private static native void setOperatorZoomAreaV(long ptr);

    private static native void setOperatorFlyV(long ptr);

    private static native void setOperatorSelectPointV(long ptr);

    private static native void setOperatorSelectAreaV(long ptr);

    private static native void onModeSimpleShadowZ(long ptr, boolean enable);

    private static native void onModeSmoothV(long ptr);

    private static native void onModeHiddenLineV(long ptr);

    private static native void onModeFrameRateV(long ptr);

    private static native void onUserCode1V(long ptr);

    private static native void onUserCode2V(long ptr);

    private static native void onUserCode3V(long ptr);

    private static native void onUserCode4V(long ptr);

    private static native boolean readSimEntInfoV(long ptr, byte[] pBuffer, long lBufferSize, int iFloorId, String strHandle, int iComtype,int iSolidType);

    private static native void setOperatorWalkV(long ptr);

    private static native void setOperatorCullSectionV(long ptr);

	private static native void zoomAllV(long ptr);
	private static native void setSysPathV(long ptr,String sysPath,String projectPath);
	private static native void setLineData_OTHERV(long ptr,String pBuffer);
	private static native void setLineData_HORV(long ptr,String pBuffer);
	private static native void setLineData_VERV(long ptr,String pBuffer);
	private static native void setLineData_OBLV(long ptr,String pBuffer);
	private static native void setLineData_ARCV(long ptr,String pBuffer);
	private static native void setSymbolDataV(long ptr,String pBuffer);
	private static native void drawAxisV(long ptr,int iFloorId);
	private static native void setHeightV(long ptr,int iHeight);
	private static native void cancelLoadingV(long ptr);
    private static native String getLayerInfoV(long ptr);
	private static native boolean outPutImageV(long ptr,String imagePath);
    private static native boolean outPutImageSizeV(long ptr,String imagePath,int height,int width);
    private static native void setLayerShowV(long ptr, int iFloorId, int iComtype, boolean bShow);
    private static native void insertCullPlanesV(long ptr);
    private static native void cancelCullPlanesV(long ptr);
    private static native void hideCullPlanesV(long ptr);
    private static native void showCullPlanesV(long ptr);

    private static native void beginWalkV(long ptr);
    private static native void endWalkV(long ptr);
    private static native void onWalkMoveV(long ptr,float x,float y);
    private static native void onWalkCameraUpV(long ptr);
    private static native void onWalkCameraDownV(long ptr);

    private static native void setIsNewPbimV(long ptr,boolean bTrue);


    public AndroidUserMobileSurfaceView(Context context, Callback svcb, int guiSurfaceId, long savedSurfacePointer) {
        super(context, svcb, guiSurfaceId, savedSurfacePointer);
    }

    public boolean loadFile(String fileName) {
        return loadFileS(mSurfacePointer, fileName);
    }


    public void setOperatorOrbit() {
        setOperatorOrbitV(mSurfacePointer);
    }


    public void setOperatorZoomArea() {
        setOperatorZoomAreaV(mSurfacePointer);
    }


    public void setOperatorFly() {
        setOperatorFlyV(mSurfacePointer);
    }


    public void setOperatorSelectPoint() {
        setOperatorSelectPointV(mSurfacePointer);
    }


    public void setOperatorSelectArea() {
        setOperatorSelectAreaV(mSurfacePointer);
    }


    public void onModeSimpleShadow(boolean enable) {
        onModeSimpleShadowZ(mSurfacePointer, enable);
    }


    public void onModeSmooth() {
        onModeSmoothV(mSurfacePointer);
    }


    public void onModeHiddenLine() {
        onModeHiddenLineV(mSurfacePointer);
    }


    public void onModeFrameRate() {
        onModeFrameRateV(mSurfacePointer);
    }


    public void onUserCode1() {
        onUserCode1V(mSurfacePointer);
    }


    public void onUserCode2() {
        onUserCode2V(mSurfacePointer);
    }


    public void onUserCode3() {
        onUserCode3V(mSurfacePointer);
    }


    public void onUserCode4() {
        onUserCode4V(mSurfacePointer);
    }

    public boolean readSimEntInfo(byte[] pBuffer, long lBufferSize, int iFloorId, String strHandle, int iComtype,int iSolidType) {
        return readSimEntInfoV(mSurfacePointer, pBuffer, lBufferSize, iFloorId, strHandle, iComtype,iSolidType);
    }

    public void setOperatorWalk() {
        setOperatorWalkV(mSurfacePointer);
    }

    public void zoomAll() {
        zoomAllV(mSurfacePointer);
    }

	public  void cancelLoading()
	{
		cancelLoadingV(mSurfacePointer);
	}
    public void setSysPath(String sysPath,String projectPath) {
        setSysPathV(mSurfacePointer, sysPath, projectPath);
    }

    /*
        设置轴网信息
     */
    public void setLineData_OTHER(String pBuffer) {
        setLineData_OTHERV(mSurfacePointer, pBuffer);
    }

    public void setLineData_HOR(String pBuffer) {
        setLineData_HORV(mSurfacePointer, pBuffer);
    }

    public void setLineData_VER(String pBuffer) {
        setLineData_VERV(mSurfacePointer, pBuffer);
    }

    public void setLineData_OBL(String pBuffer) {
        setLineData_OBLV(mSurfacePointer, pBuffer);
    }

    public void setLineData_ARC(String pBuffer) {
        setLineData_ARCV(mSurfacePointer, pBuffer);
    }

    public void setSymbolData(String pBuffer) {
        setSymbolDataV(mSurfacePointer, pBuffer);
    }

    public void drawAxis(int iFloorId) {
        drawAxisV(mSurfacePointer,iFloorId);
    }
    //轴网绘制高度
    public void setHeight(int iHeight) {
        setHeightV(mSurfacePointer, iHeight);
    }

    /*
        获取图层信息  floorid：comtype,comtype;
     */
    public String getLayerInfo() {
        return getLayerInfoV(mSurfacePointer);
    }

    /*
        图层显示控制
     */

    public void setLayerShow(int iFloorId, int iComtype, boolean bShow) {
        setLayerShowV(mSurfacePointer, iFloorId, iComtype, bShow);
    }


    /*
        截图导出
    */
	public boolean outPutImage(String imagePath)
	{
        return outPutImageSizeV(mSurfacePointer,imagePath, 512, 512);
//		return  outPutImageV(mSurfacePointer,imagePath);
	}

    public boolean outPutImageSize(String imagePath,int width,int height)
    {
        return  outPutImageSizeV(mSurfacePointer,imagePath,width,height);
    }
    /*
        设置剖切操作
     */
    public void setOperatorCullSection() {
        setOperatorCullSectionV(mSurfacePointer);
    }

	/*
	    插入切割面
	 */
    public  void insertCullPlanes()
    {
        insertCullPlanesV(mSurfacePointer);
    }
    /*
        取消剖切操作
     */
    public  void cancelCullPlanes()
    {
        cancelCullPlanesV(mSurfacePointer);
    }
    /*
        显示隐藏剖切面
     */
    public  void hideCullPlanes()
    {
        hideCullPlanesV(mSurfacePointer);
    }
    public  void showCullPlanes()
    {
        showCullPlanesV(mSurfacePointer);
    }

    /*
        漫游操作
     */
    public  void beginWalk()
    {
        beginWalkV(mSurfacePointer);
    }
    public  void endWalk()
    {
        endWalkV(mSurfacePointer);
    }
    public  void onWalkMove(float x,float y)
    {
        onWalkMoveV(mSurfacePointer,x,y);
    }
    public  void onWalkCameraUp()
    {
        onWalkCameraUpV(mSurfacePointer);
    }
    public  void onWalkCameraDown()
    {
        onWalkCameraDownV(mSurfacePointer);
    }
    //是否是新版的pbim
    public  void setIsNewPbim(boolean bTrue)
    {
        setIsNewPbimV(mSurfacePointer,bTrue);
    }

}

