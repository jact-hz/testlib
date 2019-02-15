package osg.AndroidExample;
import cn.pinming.cadshow.bim.MobileSurfaceActivity;

import cn.pinming.cadshow.bim.MobileSurfaceActivity;

public class osgNativeLib {
	
	static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("PM3dLib");
    }

   //初始化长宽
    public static native void 		init(int width, int height);
    public static native void 		release();
    public static native void 		bindView(MobileSurfaceActivity view);
    public static native void 		step();

    public static native void 		setEnableFileLoad(boolean bEnable);

    //屏幕触摸参数传递
    public static native void 		touchBegan(float x,float y);
    public static native void 		touchBegans(float x1,float y1,float x2,float y2);
    public static native void 		touchMoved(float x,float y);
    public static native void 		touchMoveds(float x1,float y1,float x2,float y2);
    public static native void 		touchEnded(float x,float y);
    public static native void 		touchEndeds(float x1,float y1,float x2,float y2);
    //读取主要模型文件
    public static native void		loadFile(String address);
    //读取其余文件
    public static native void		LoadFileEx(String address);
    //设置材质路径
    public static native void		setSysPath(String strPath);

    //设置视角
    /*
    HViewIsoFrontRightTop = 0,
    HViewFront,
    HViewBack,
    HViewRight,
    HViewLeft,
    HViewTop,
    HViewBottom,
    HViewCenterFront,
    */
    public static native void setHoopsViewMode(int iMode, boolean bAnimated);
    //选择
    public static native boolean trySelectItem(float x, float y);

    //开启选择
    public static native void setSelectOn(boolean bOn);
    public static native boolean getSelectState();
    public static native void changeSelectState();

    //获取/设置视角信息
    public static native String getViewPortInfo();
    public static native void setViewPortInfo(String strInfo);

    //构件标记
    public static native void setEntMark(int iFloorId,String strFloorName, String strHandle, String strText);

    //构件高亮
    public static native void highLightEnt(int iFloorId,String strFloorName, String strHandle);

    //获取图层信息
    public static native String getLayerInfo();
    //获取显示的图层信息
    public static native String getShowLayerInfo();
    //显示控制
    public static native void setLayerShow(int iFloorId, int iComtype, boolean bShow);
    public static native void setAllLayersShow(boolean bShow);

    //截图
    public static native boolean outPutImage(String imagePath);
    public static native boolean getImageOutput();

    //剖切
    public static native void setOperatorCullSection();
    public static native void insertCullPlanes();
    public static native void cancelCullPlanes();
    public static native void hideCullPlanes();
    public static native void showCullPlanes();

    //漫游
    public static native void beginWalk();
    public static native void endWalk();
    public static native void onWalkMove(float x,float y);
    public static native void onWalkCameraUp();
    public static native void onWalkCameraDown();

    //根据floorid获取楼层名称
    public static native String GetFloorName(int floorid);
    public static native int GetShowId(int floorid);
    //根据comtype获取构件类型
    public static native String GetComtypeName(int comtype);
    //获取构件信息
    public static native String GetComponents(int floorid,String handle);
    //（没用的）
    public static native int getShowFloorCount();
    public static native void setOperatorWalk();
    public static native void setProjectMode(int mode);
    public static native void addDimText(float x, float y, String strText);
    public static native void deleteAllDimTexts();
    public static native boolean trySelectDimText(float x, float y);
    public static native String getSelectDimText();
    public static native boolean setSelectDimText(String strText);
    public static native void unhightLightSelectDimTexts();
    public static native boolean hightLightSelectDimText();
    public static native boolean deleteSelectDimText();
    public static native String addMarkUp(float x, float y, String strText);
    public static native boolean deleteCurMarkUp();
    public static native void deleteAllMarkUp();
    public static native void addMarkUpFromData(String strData, String strFloorName, String strText);
    public static native String deleteSelectMarkUp();
    public static native boolean setMarkUpText(String strText);
    public static native boolean setViewPortByMark(String strText);
}
