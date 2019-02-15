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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import com.weqia.utils.L;

import java.io.FileOutputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

public class TeighaDwgView extends GLSurfaceView {
    private static int GLESVER = 1;
    private int mWidth = 0;
    private int mHeight = 0;
    public Renderer mRenderer;
    private static boolean isComplete = false;
    private String path;
    private boolean mContextCreated = false;
    private boolean mRebuildVBO = false;
    private volatile boolean savescreen = false;
    private String savePath = new String();
    private TeighaDwgActivity ctx;


    public void onLoad(TeighaDwgActivity ctx) {
        this.ctx = ctx;
        mRenderer.onLoad(ctx);
    }
    //    @Override
//    public void onPause() {
//        if(mContextCreated)
//        {
//            mSavaVBO = true;
//            while (mSavaVBO)
//            {
//            }
//        }
//
//        super.onPause();
//    }

    void saveScreenShot(String path) {
        //截图在opengles线程执行
        savescreen = true;
        savePath = path;
        requestRender();

        while(savescreen)
        {
        }
    }

    //
    @Override
    public void onResume() {
        if (mContextCreated) {
            mRebuildVBO = true;
        }
        super.onResume();
    }


    public TeighaDwgView(Context context, String path) {
        this(context);
        this.path = path;
    }

    public TeighaDwgView(Context context) {
        super(context);
        init(false, 24, 2);
        isComplete = false;
    }

    public TeighaDwgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false, 24, 2);
        isComplete = false;
    }

    public TeighaDwgView(Context context, boolean translucent, int depth, int stencil) {
        super(context);
        init(translucent, depth, stencil);
//        dwgViewCtx = context;
        isComplete = false;
    }

    private void init(boolean translucent, int depth, int stencil) {
        if (translucent) {
            this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
//        setDebugFlags(DEBUG_CHECK_GL_ERROR|DEBUG_LOG_GL_CALLS);
        setEGLContextFactory(new ContextFactory());

//        setEGLConfigChooser(translucent ?
//                new ConfigChooser(8, 8, 8, 8, depth, stencil) :
//                new ConfigChooser(5, 6, 5, 0, depth, stencil));
        setEGLConfigChooser(new ConfigChooser(8, 8, 8, 8, 16, 0));

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        mRenderer = new Renderer();
        setRenderer(mRenderer);

    }

    private static class ConfigChooser implements GLSurfaceView.EGLConfigChooser {

        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
            mRedSize = r;
            mGreenSize = g;
            mBlueSize = b;
            mAlphaSize = a;
            mDepthSize = depth;
            mStencilSize = stencil;
        }

        private static int EGL_OPENGL_ES2_BIT = 4;
        private static int[] s_configAttribs1 =
                {
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        // EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                        EGL10.EGL_NONE
                };
        private static int[] s_configAttribs2 =
                {
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                        EGL10.EGL_NONE
                };

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

            int[] num_config = new int[1];
            if (GLESVER != 2)
                egl.eglChooseConfig(display, s_configAttribs1, null, 0, num_config);
            else
                egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);

            int numConfigs = num_config[0];

            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }

            EGLConfig[] configs = new EGLConfig[numConfigs];
            if (GLESVER != 2)
                egl.eglChooseConfig(display, s_configAttribs1, configs, numConfigs, num_config);
            else
                egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs, num_config);

            return chooseConfig(egl, display, configs);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                                      EGLConfig[] configs) {
            int checkStencil = 2;
            while ((checkStencil--) != 0) {
                for (EGLConfig config : configs) {
                    int d = findConfigAttrib(egl, display, config,
                            EGL10.EGL_DEPTH_SIZE, 0);
                    int s = findConfigAttrib(egl, display, config,
                            EGL10.EGL_STENCIL_SIZE, 0);
                    if (d < mDepthSize || ((checkStencil != 0) && (s < mStencilSize)))
                        continue;
                    int r = findConfigAttrib(egl, display, config,
                            EGL10.EGL_RED_SIZE, 0);
                    int g = findConfigAttrib(egl, display, config,
                            EGL10.EGL_GREEN_SIZE, 0);
                    int b = findConfigAttrib(egl, display, config,
                            EGL10.EGL_BLUE_SIZE, 0);
                    int a = findConfigAttrib(egl, display, config,
                            EGL10.EGL_ALPHA_SIZE, 0);

                    if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize)
                        return config;
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                     EGLConfig config, int attribute, int defaultValue) {

            if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
                return mValue[0];
            }
            return defaultValue;
        }

        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

    }

    private static class ContextFactory implements GLSurfaceView.EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            if (GLESVER != 2)
                L.w("creating OpenGL ES 1.1 context");
            else
                L.w("creating OpenGL ES 2.0 context");
            checkEglError("Before eglCreateContext", egl);
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, GLESVER, EGL10.EGL_NONE};
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            checkEglError("After eglCreateContext", egl);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    private static void checkEglError(String prompt, EGL10 egl) {
        int error;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            Log.e(TAG, String.format("%s: EGL error: 0x%x", prompt, error));
        }
    }

    private class Renderer implements GLSurfaceView.Renderer {
        private boolean mLoaded = false;
        //            	private TeighaToolbar mToolbar = new TeighaToolbar();
        private TeighaDwgActivity dwgViewCtx;

        public void onDrawFrame(GL10 gl) {
//            L.e("----------------- ");
            if (mContextCreated == true) {

                if (mRebuildVBO) {
                    TeighaDWGJni.rebuildVBOData();
                    mRebuildVBO = false;
                }

                if (!TeighaDWGJni.renderFrame(path)) {
                } else {
                    if (!isComplete) {
                        isComplete = true;
                        dwgViewCtx.cancelDlg();
                        ctx.configAngle();
                        ctx.getPosDatas(true);
                        //  调用 requestRender() 才会执行绘制;
                        setRenderMode(RENDERMODE_WHEN_DIRTY);
                    }
                }

                //截图
                if (savescreen) {
                    SavePixels(0, 0, mWidth, mHeight, gl);
                    savescreen = false;
                }

            } else {
                if (mLoaded == true && mWidth != 0 && mHeight != 0) {
                    mContextCreated = true;
                    TeighaDWGJni.createRenderer(mWidth, mHeight, path);
                }
            }
        }

        public boolean SavePixels(int x, int y, int w, int h, GL10 gl) {
            int b[] = new int[w * (y + h)];
            int bt[] = new int[w * h];
            IntBuffer ib = IntBuffer.wrap(b);
            ib.position(0);
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

            for (int i = 0, k = 0; i < h; i++, k++) {//remember, that OpenGL bitmap is incompatible with Android bitmap
                //and so, some correction need.
                for (int j = 0; j < w; j++) {
                    int pix = b[i * w + j];
                    int pb = (pix >> 16) & 0xff;
                    int pr = (pix << 16) & 0x00ff0000;
                    int pix1 = (pix & 0xff00ff00) | pr | pb;
                    bt[(h - k - 1) * w + j] = pix1;
                }
            }


            Bitmap bitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
            try {
                FileOutputStream fos = new FileOutputStream(savePath);
                String ext = savePath.substring(savePath.lastIndexOf(".") + 1);
                Bitmap.CompressFormat format;
                if(ext.equalsIgnoreCase("jpg") )
                    format = Bitmap.CompressFormat.JPEG;
                else
                    format = Bitmap.CompressFormat.PNG;
                bitmap.compress(format, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
//            mWidth = width;
//            mHeight = height;
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            { // check extensions
                if (GLESVER != 2) {
                    String checkStr = gl.glGetString(GLES10.GL_EXTENSIONS);
                    L.i("GL10" + checkStr);
                } else {
                    String checkStr = GLES20.glGetString(GLES20.GL_EXTENSIONS);
                    L.i("GL20" + checkStr);
                }
            }
        }

        public void onLoad(TeighaDwgActivity ctx) {
            mLoaded = true;
            dwgViewCtx = ctx;
//            mWidth = DeviceUtil.getDeviceWidth();
//            mHeight = DeviceUtil.getDeviceHeight();

//            mContextCreated = true;
//            TeighaDWGJni.createRenderer(mWidth, mHeight,path);
//            TeighaDWGJni.renderFrame(path);
//            L.e("renderFrame完毕" + path);
//            EventBus.getDefault().post(
//                    new RefreshEvent(EnumDataTwo.RefreshEnum.DWG_LOAD_END.getValue(), path));

//            new AsyncTask<String, Integer, String>() {
//                @Override
//                protected String doInBackground(String... params) {
//                    L.e("执行" + path);
//                    mContextCreated = true;
//                    TeighaDWGJni.createRenderer(mWidth, mHeight);
//                    TeighaDWGJni.renderFrame();
//                    L.e("renderFrame完毕" + path);
//                    EventBus.getDefault().post(
//                            new RefreshEvent(EnumDataTwo.RefreshEnum.DWG_LOAD_END.getValue(), path));
//                    return null;
//                }
//            }.execute();
        }

        public boolean onToolbarClick(float touchX, float touchY) {
//        	return mToolbar.click(touchX, touchY, mWidth, mHeight);
            return false;
        }

        public void onDestroyContext() {
            if (mContextCreated == true) {
//                mContextCreated = false;
//        	  mToolbar.destroy(GLESVER);
//                mNeedDelete = true;
                TeighaDWGJni.destroyRenderer();
            } else {
                TeighaDWGJni.destroyRenderer();
                TeighaDWGJni.close(path);
            }

        }
    }

    public void onDestroy() {
        mRenderer.onDestroyContext();
    }

    public boolean onToolbarClick(float touchX, float touchY) {
        return mRenderer.onToolbarClick(touchX, touchY);
    }
}

