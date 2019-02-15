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

import android.util.Log;

import cn.pinming.cadshow.cad.data.LayerInfo;

public class TeighaDWGJni {
    public enum LIBRARY {
        NONE, TEIGHA, ARCHITECTURE, CIVIL
    }
    public static LIBRARY loadedLibrary = LIBRARY.NONE;

    static {
        try {
            System.loadLibrary("PMcad");
            loadedLibrary = LIBRARY.TEIGHA;
        } catch (UnsatisfiedLinkError err3) {
            Log.e("JNI", "WARNING: Could not load PMcad.so");
        }
    }


    public static native boolean init (String path);

    public static native boolean finit ();

    public static native boolean open ( String file );

    public static native int createRenderer(int width, int height,String file);

    public static native boolean renderFrame (String file);

    public static native boolean destroyRenderer ();

    public static native boolean close (String file);

    public static native boolean viewTranslate ( float xAxis, float yAxis );

    public static native boolean viewScale ( float sCoef );

    public static native boolean viewScalePoint ( float xAxis, float yAxis );

    public static native boolean viewCanRotate ();

    public static native boolean viewRotate ( float rAngle );

    public static native boolean viewOrbit ( float xAxis, float yAxis );

    public static native int viewGetRenderMode ();

    public static native boolean viewSetRenderMode ( int nMode );

    public static native boolean viewRegen ();

    public static native LayerInfo[] GetLayersInfo();

    public static native boolean changeLayerShowHide(String strLayerName);

    public static native boolean zoomAll();

    //保存数据库
    public static native boolean saveDabatase();

    //布局控制
    public static native String getLayoutInfo();
    public static native String getActiveLayout();
    public static native boolean setActiveLayout(String name);

    public static native boolean tempPointMove(float x, float y);
    //测量距离
    public static native boolean distanceFirst(float x, float y);
    public static native boolean distanceMove(float x, float y);
    public static native float distanceSecond(float x, float y);
    public static native boolean distanceCancel();

    //测量面积
    public static native boolean measureAreaBegin(float x, float y);
    public static native String measureAreaFirst(float x, float y);
    public static native String measureAreaNext(float x, float y);
    public static native String measureAreaMove(float x, float y);
    public static native boolean measureAreaFinish();

    //文字标注
    public static native boolean rectTextBegin(float x, float y);
    public static native boolean rectTextMove(float x, float y);
    public static native boolean setRectText(String strText);
    public static native String getRectText();
    public static native boolean resetRectText(String strText);
    public static native boolean rectTextCancel();

    //坐标标注
    public static native String pointMove(float x, float y);
    public static native String pointMoveEnd();
    public static native boolean pointDimensionSet(float x, float y);

    //选择 0未选择 1线性标注 2文字标注 3点标注 x,y标注分享
    public static native String trySelect(float x, float y);
    public static native int hitSelectGripPoint(float x, float y);
    public static native boolean moveSelectGripPoint(float x, float y,int index);
    public static native boolean moveSelectGripPointEnd(float x, float y,int index);

    //停止图纸打开
    public static native boolean disDraw();
    public static native boolean clearSelect();
    public static native boolean deleteSelect();
    //刷新默认视角
    public static native boolean invalideViewPoint();

    //保存 读取VBO
    public static native boolean saveVBOData();
    public static native boolean transVBOData();
    public static native boolean rebuildVBOData();

    //添加 标注分享
    public static native String addMarkUp(float x, float y,String text);
    //取消上一个添加的标注
    public static native void cancelAddMarkUp();
    //根据info添加标注
    public static native void addMarkUpFromData(String info,String text);
    //获取视口信息
    public static native String getViewPortInfo();
    //设置视口信息
    public static native void setViewPortInfo(String info);
}
