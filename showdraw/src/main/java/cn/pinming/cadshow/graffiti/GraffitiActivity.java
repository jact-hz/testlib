package cn.pinming.cadshow.graffiti;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weqia.utils.L;
import com.weqia.utils.ViewUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.StatusBarUtil;
import cn.forward.androids.utils.ThreadUtil;
import cn.pinming.cadshow.library.R;
import cn.pinming.cadshow.view.FlowBtn;
import common.DialogUtil;

/**
 * 涂鸦界面，根据GraffitiView的接口，提供页面交互
 * （这边代码和ui比较粗糙，主要目的是告诉大家GraffitiView的接口具体能实现什么功能，实际需求中的ui和交互需另提别论）
 * https://github.com/1993hzw/Graffiti
 */
public class GraffitiActivity extends Activity {

    public static final String TAG = "Graffiti";

    public static final int RESULT_ERROR = -111; // 出现错误
    public static final int BASE_TRAN = 111;

    public static final int DEFAULE_FONTSIZE = 2;
    private GraffitiActivity ctx;
    private int lastLineSize = 2;

    /**
     * 启动涂鸦界面
     * @param activity
     * @param params      涂鸦参数
     * @param requestCode startActivityForResult的请求码
     * @see GraffitiParams
     */
    public static void startActivityForResult(Activity activity, GraffitiParams params, int requestCode) {
        Intent intent = new Intent(activity, GraffitiActivity.class);
        intent.putExtra(GraffitiActivity.KEY_PARAMS, params);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动涂鸦界面
     * @param activity
     * @param imagePath   　图片路径
     * @param savePath    　保存路径
     * @param isDir       　保存路径是否为目录
     * @param requestCode 　startActivityForResult的请求码
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, String savePath, boolean isDir, int requestCode) {
        GraffitiParams params = new GraffitiParams();
        params.mImagePath = imagePath;
        params.mSavePath = savePath;
        params.mSavePathIsDir = isDir;
        startActivityForResult(activity, params, requestCode);
    }

    /**
     * {@link GraffitiActivity#startActivityForResult(Activity, String, String, boolean, int)}
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, int requestCode) {
        GraffitiParams params = new GraffitiParams();
        params.mImagePath = imagePath;
        startActivityForResult(activity, params, requestCode);
    }

    //获取涂鸦参数的KEY
    public static final String KEY_PARAMS = "key_graffiti_params";
    //获取被涂鸦图片路径的KEY
    public static final String KEY_IMAGE_PATH = "key_image_path";

    private String mImagePath;
    private Bitmap mBitmap;

    private FrameLayout mFrameLayout;
    private GraffitiView mGraffitiView;

    private View.OnClickListener mOnClickListener;
    private ImageView mSizeColor;
//    private ImageView mAnchorSize, mMiddleSize, mBigSize;
    private boolean isOpening;
    private boolean mIsMovingPic = false; // 是否是平移缩放模式

    private boolean mIsScaling;
    private final float mMaxScale = 4f; // 最大缩放倍数
    private final float mMinScale = 0.25f; // 最小缩放倍数
    private final int TIME_SPAN = 40;
    private int paintColor = -1;
    //移动缩放模式按钮，整个涂鸦层总布局
    private View mBtnMovePic, mSettingsPanel;
    //可选项(文字和贴图)操作布局
    private View mSelectedTextEditContainer;
    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画
    private GraffitiParams mGraffitiParams;
    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    // 触摸屏幕超过一定时间才判断为需要显示设置面板
    private Runnable mShowDelayRunnable;
    private TouchGestureDetector mTouchGestureDetector;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARAMS, mGraffitiParams);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mGraffitiParams = savedInstanceState.getParcelable(KEY_PARAMS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        StatusBarUtil.setStatusBarTranslucent(this, true, false);
        if (mGraffitiParams == null) {
            mGraffitiParams = getIntent().getExtras().getParcelable(KEY_PARAMS);
        }
        if (mGraffitiParams == null) {
            LogUtil.e("TAG", "mGraffitiParams is null!");
            this.finish();
            return;
        }

        mImagePath = mGraffitiParams.mImagePath;
        if (mImagePath == null) {
            LogUtil.e("TAG", "mImagePath is null!");
            this.finish();
            return;
        }
        LogUtil.d("TAG", mImagePath);
        if (mGraffitiParams.mIsFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBitmap = ImageUtils.createBitmapFromPath(mImagePath, this);
        if (mBitmap == null) {
            LogUtil.e("TAG", "bitmap is null!");
            this.finish();
            return;
        }
        setContentView(R.layout.layout_graffiti);
        mFrameLayout = findViewById(R.id.graffiti_container);
        mGraffitiView = new GraffitiView(this, mBitmap, mGraffitiParams.mEraserPath, mGraffitiParams.mEraserImageIsResizeable,
                new GraffitiListener() {
                    @Override
                    public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) { // 保存图片为jpg格式
                        if (bitmapEraser != null) {
                            bitmapEraser.recycle(); // 回收图片，不再涂鸦，避免内存溢出
                        }
                        File graffitiFile = null;
                        File file = null;
                        String savePath = mGraffitiParams.mSavePath;
                        boolean isDir = mGraffitiParams.mSavePathIsDir;
                        if (TextUtils.isEmpty(savePath)) {
                            File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
                            graffitiFile = new File(dcimFile, "Graffiti");
                            //　保存的路径
                            file = new File(graffitiFile, System.currentTimeMillis() + ".png");
                        } else {
                            if (isDir) {
                                graffitiFile = new File(savePath);
                                //　保存的路径
                                file = new File(graffitiFile, System.currentTimeMillis() + ".png");
                            } else {
                                file = new File(savePath);
                                graffitiFile = file.getParentFile();
                            }
                        }
                        graffitiFile.mkdirs();

                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 95, outputStream);
                            ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
                            Intent intent = new Intent();
                            intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            onError(GraffitiView.ERROR_SAVE, e.getMessage());
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String msg) {
                        setResult(RESULT_ERROR);
                        finish();
                    }

                    @Override
                    public void onReady() {
                        // 设置初始值
                        mGraffitiView.setPaintSize(mGraffitiParams.mPaintSize > 0 ? mGraffitiParams.mPaintSize : mGraffitiView.getPaintSize());
                        onHollRectShape();
                    }

                    @Override
                    public void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected) {
                        if (selected) {
                            mSelectedTextEditContainer.setVisibility(View.VISIBLE);
                        } else {
                            mSelectedTextEditContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y) {
                        if (pen == GraffitiView.Pen.TEXT) {
                            createGraffitiText(null, x, y);
                        } else if (pen == GraffitiView.Pen.BITMAP) {
//                            createGraffitiBitmap(null, x, y);
                        }
                    }
                });

        mGraffitiView.setIsDrawableOutside(mGraffitiParams.mIsDrawableOutside);
        mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mOnClickListener = new GraffitiOnClickListener();
        mTouchGestureDetector = new TouchGestureDetector(this, new GraffitiGestureListener());
        initView();
        initPaint();
    }

    // 添加文字
    private void createGraffitiText(final GraffitiText graffitiText, final float x, final float y) {
        Activity activity = this;
        if (isFinishing()) {
            return;
        }

        boolean fullScreen = (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        Dialog dialog = null;
        if (fullScreen) {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
        final Dialog finalDialog1 = dialog;
        activity.getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                finalDialog1.dismiss();
            }
        });

        ViewGroup container = (ViewGroup) View.inflate(getApplicationContext(), R.layout.graffiti_create_text, null);
        final Dialog finalDialog = dialog;
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        dialog.setContentView(container);

        final EditText textView = (EditText) container.findViewById(R.id.graffiti_selectable_edit);
        final View cancelBtn = container.findViewById(R.id.graffiti_text_cancel_btn);
        final TextView enterBtn = (TextView) container.findViewById(R.id.graffiti_text_enter_btn);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    enterBtn.setEnabled(false);
                    enterBtn.setTextColor(0xffb3b3b3);
                } else {
                    enterBtn.setEnabled(true);
                    enterBtn.setTextColor(0xff232323);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(graffitiText == null ? "" : graffitiText.getText());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setSelected(true);
                finalDialog.dismiss();
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (cancelBtn.isSelected()) {
                    mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                    return;
                }
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (graffitiText == null) {
                    mGraffitiView.addSelectableItem(new GraffitiText(mGraffitiView.getPen(), text, mGraffitiView.getPaintSize(), mGraffitiView.getColor().copy(),
                            0, mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY()));
                } else {
                    graffitiText.setText(text);
                }
                mGraffitiView.invalidate();
            }
        });

        if (graffitiText == null) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
        }
    }

    // 添加贴图
//    private void createGraffitiBitmap(final GraffitiBitmap graffitiBitmap, final float x, final float y) {
//        Activity activity = this;
//
//        boolean fullScreen = (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
//        Dialog dialog = null;
//        if (fullScreen) {
//            dialog = new Dialog(activity,
//                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        } else {
//            dialog = new Dialog(activity,
//                    android.R.style.Theme_Translucent_NoTitleBar);
//        }
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        dialog.show();
//        ViewGroup container = (ViewGroup) View.inflate(getApplicationContext(), R.layout.graffiti_create_bitmap, null);
//        final Dialog finalDialog = dialog;
//        container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finalDialog.dismiss();
//            }
//        });
//        dialog.setContentView(container);

//        ViewGroup selectorContainer = (ViewGroup) finalDialog.findViewById(R.id.graffiti_image_selector_container);
//        ImageSelectorView selectorView = new ImageSelectorView(this, false, 1, null, new ImageSelectorView.ImageSelectorListener() {
//            @Override
//            public void onCancel() {
//                finalDialog.dismiss();
//            }
//
//            @Override
//            public void onEnter(List<String> pathList) {
//                finalDialog.dismiss();
//                Bitmap bitmap = ImageUtils.createBitmapFromPath(pathList.get(0), mGraffitiView.getWidth() / 4, mGraffitiView.getHeight() / 4);
//
//                if (graffitiBitmap == null) {
//                    mGraffitiView.addSelectableItem(new GraffitiBitmap(mGraffitiView.getPen(), bitmap, mGraffitiView.getPaintSize(), mGraffitiView.getColor().copy(),
//                            0, mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY()));
//                } else {
//                    graffitiBitmap.setBitmap(bitmap);
//                }
//                mGraffitiView.invalidate();
//            }
//        });
//        selectorContainer.addView(selectorView);
//    }

    private void initView() {
        ViewUtils.bindClickListenerOnViews(this, mOnClickListener,
                //手绘画图
                R.id.im_pen_hand,
                //添加文字
                R.id.im_pen_text,
                //文字重新编辑
                R.id.graffiti_selectable_edit,
                //文字移除
                R.id.graffiti_selectable_remove,
                //橡皮擦
                R.id.im_eraser,
                //清屏
                R.id.graffiti_btn_clean,
                //撤销
                R.id.im_undo,
                //保存涂鸦
                R.id.graffiti_btn_finish,
                //返回上一页
                R.id.graffiti_btn_back,
                //移动缩放涂鸦背景图模式
                R.id.im_pen_move,
                //画笔颜色
                R.id.im_paint_red,
                R.id.im_paint_yellow,
                R.id.im_paint_green,
                R.id.im_paint_blue,
                R.id.im_paint_white,
                //画笔的样式
                R.id.btn_arrow,
                R.id.btn_line,
                R.id.btn_holl_circle,
                R.id.btn_holl_rect,
                R.id.btn_hand_write
                //画笔尺寸
//                R.id.im_paint_big,
//                R.id.im_paint_middle,
//                R.id.im_paint_small,
                //显示尺寸的颜色
//                R.id.im_size_color
        );

        mSelectedTextEditContainer = findViewById(R.id.graffiti_selectable_edit_container);
        mBtnMovePic = findViewById(R.id.im_pen_move);

        mSizeColor = findViewById(R.id.im_size_color);
        final ImageView tmAnchorSize = findViewById(R.id.im_paint_small);
        final ImageView tmMiddleSize = findViewById(R.id.im_paint_middle);
        final ImageView tmBigSize = findViewById(R.id.im_paint_big);
        ArrayList<View> views = new ArrayList<View>(){};
//        views.add(tmSizeColor);
        views.add(tmAnchorSize);
        views.add(tmMiddleSize);
        views.add(tmBigSize);
        final FlowBtn flowBtn = new FlowBtn(ctx);
        final View nullBg = findViewById(R.id.fabBGLayout);
        flowBtn.initFlowBtn(getResources().getDimension(R.dimen.standard_45), views, nullBg, new FlowBtn.FlowBtnClickInterface() {
            @Override
            public void onClick(View v) {
                if (v == tmAnchorSize) {
                    L.e("small");
                    mGraffitiView.setPaintSize(2);
                    lastLineSize = 2;
                    mSizeColor.setImageResource(R.drawable.icon_xiaoyuan);
                } else if(v == tmMiddleSize) {
                    L.e("middle");
                    mGraffitiView.setPaintSize(8);
                    lastLineSize = 0;
                    mSizeColor.setImageResource(R.drawable.icon_zhongyuan);
                } else if(v == tmBigSize) {
                    L.e("big");
                    mGraffitiView.setPaintSize(20);
                    lastLineSize = 20;
                    mSizeColor.setImageResource(R.drawable.icon_dayuan);
                } else if(v == nullBg) {
                    L.e("空界面");
                }
            }

            @Override
            public void onFlowOpen() {
                ViewUtils.hideView(mSizeColor);
            }

            @Override
            public void onFlowClose() {
                ViewUtils.showView(mSizeColor);
            }
        });

        mSettingsPanel = findViewById(R.id.graffiti_panel);

        // 添加涂鸦的触摸监听器，移动图片位置
        mGraffitiView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏设置面板
                if (mGraffitiParams.mChangePanelVisibilityDelay > 0) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                            mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                            //触摸屏幕超过一定时间才判断为需要隐藏设置面板
                            mSettingsPanel.postDelayed(mHideDelayRunnable, mGraffitiParams.mChangePanelVisibilityDelay);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                            mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                            //离开屏幕超过一定时间才判断为需要显示设置面板
                            mSettingsPanel.postDelayed(mShowDelayRunnable, mGraffitiParams.mChangePanelVisibilityDelay);
                            break;
                    }
                } else if (mGraffitiView.getAmplifierScale() > 0) {
                    mGraffitiView.setAmplifierScale(-1);
                }

                if (!mIsMovingPic) { // 非移动缩放模式
                    return false;  // 交给下一层的涂鸦View处理
                }
                // 处理手势
                mTouchGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        // 长按标题栏显示原图
        findViewById(R.id.graffiti_txt_title).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mGraffitiView.setJustDrawOriginal(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mGraffitiView.setJustDrawOriginal(false);
                        break;
                }
                return true;
            }
        });

        mViewShowAnimation = new AlphaAnimation(0, 1);
        mViewShowAnimation.setDuration(500);
        mViewHideAnimation = new AlphaAnimation(1, 0);
        mViewHideAnimation.setDuration(500);
        mHideDelayRunnable = new Runnable() {
            public void run() {
                hideView(mSettingsPanel);
            }

        };
        mShowDelayRunnable = new Runnable() {
            public void run() {
                showView(mSettingsPanel);
            }
        };
    }

    private void initPaint() {
        paintColor = getResources().getColor(R.color.red);
    }

    private void onOpenSizePanel() {
        if (isOpening) {
            onCloseSizePanel();
            isOpening = false;
        } else {
//            mMiddleSize.animate().translationY(BASE_TRAN);
//            mBigSize.animate().translationY(BASE_TRAN);
            isOpening = true;
        }
    }

    private void onCloseSizePanel() {
//        mMiddleSize.animate().translationY(-BASE_TRAN);
//        mBigSize.animate().translationY(-2 * BASE_TRAN);
        isOpening = false;
    }

    private class GraffitiOnClickListener implements View.OnClickListener {

        private View mLastPenView, mLastPaintColorView, mLastPaintStyleView, mLastShapeView, mLastPaintSizeView;
        private boolean mDone = false;

        @Override
        public void onClick(View v) {
            mDone = false;
            //涂鸦模式设置: 移动、手绘、文字、橡皮擦
            if (v.getId() == R.id.im_pen_hand) {
                mDone = onPenHand();
                onHollRectShape();
            } else if (v.getId() == R.id.im_eraser) {
                mDone = onPenEraser();
            } else if (v.getId() == R.id.im_pen_text) {
                mDone = onPenText();
            } else if (v.getId() == R.id.im_pen_move) {
                //设置图片手动缩放模式
                mIsMovingPic = onPicMoveScale(true);
                Toast.makeText(getApplicationContext(), R.string.graffiti_moving_pic, Toast.LENGTH_SHORT).show();
                mDone = true;
            }
            if (mDone) {
                if (mLastPenView != null) {
                    mLastPenView.setSelected(false);
                }
                v.setSelected(true);
                mLastPenView = v;
                return;
            }
            //画笔颜色的设置：红色、黄色、绿色、蓝色、变色
            if (v.getId() == R.id.im_paint_red) {
                paintColor = getResources().getColor(R.color.red);
                mDone = true;
            } else if (v.getId() == R.id.im_paint_yellow) {
                paintColor = getResources().getColor(R.color.yellow);
                mDone = true;
            } else if (v.getId() == R.id.im_paint_green) {
                paintColor = getResources().getColor(R.color.green);
                mDone = true;
            } else if (v.getId() == R.id.im_paint_blue) {
                paintColor = getResources().getColor(R.color.blue);
                mDone = true;
            } else if (v.getId() == R.id.im_paint_white) {
                paintColor = getResources().getColor(R.color.white);
                mDone = true;
            }
            if (mDone) {
                mSizeColor.setColorFilter(paintColor);
                mGraffitiView.setColor(paintColor);
                if (mLastPaintColorView != null) {
                    mLastPaintColorView.setScaleX(1.0f);
                    mLastPaintColorView.setScaleY(1.0f);
                    mLastPaintColorView.setSelected(false);
                }
                v.setSelected(true);
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
                v.setScaleX(1.3f);
                v.setScaleY(1.3f);
                mLastPaintColorView = v;
                return;
            }

            //画笔大小设置：最小尺寸作为锚点，
//            if (v.getId() == R.id.im_paint_small) {
//                onOpenSizePanel();
//            } else if (v.getId() == R.id.im_paint_middle) {
//                onCloseSizePanel();
//            } else if (v.getId() == R.id.im_paint_big) {
//                onCloseSizePanel();
//            }

            //画笔的样式
            if (v.getId() == R.id.btn_hand_write) {
                mDone = onHandShape();
            } else if (v.getId() == R.id.btn_arrow) {
                mDone = onArrowShape();
            } else if (v.getId() == R.id.btn_line) {
                mDone = onLineShape();
            } else if (v.getId() == R.id.btn_holl_circle) {
//                mDone = onHollCircleShape();
                mDone = onCloudLineShape();
            } else if (v.getId() == R.id.btn_holl_rect) {
                mDone = onHollRectShape();
            }

            if (mDone) {
                if (mLastPaintStyleView != null) {
                    mLastPaintStyleView.setSelected(false);
                }
                v.setSelected(true);
                mLastPaintStyleView = v;
                return;
            }

            //清理所有涂鸦
            if (v.getId() == R.id.graffiti_btn_clean) {
                if (!(GraffitiParams.getDialogInterceptor() != null
                        && GraffitiParams.getDialogInterceptor().onShow(GraffitiActivity.this, mGraffitiView, GraffitiParams.DialogType.CLEAR_ALL))) {

                    Dialog mDialog = DialogUtil.initCommonDialog(ctx, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case -2:
                                    break;
                                case -1:
                                    mGraffitiView.clear();
                                    break;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }, getString(R.string.graffiti_cant_undo_after_clearing));
                    mDialog.show();
                }
                mDone = true;
            } else if (v.getId() == R.id.im_undo) {
                mDone = onUndo();
            }
            if (mDone) {
                return;
            }
            if (v.getId() == R.id.graffiti_btn_finish) {
                //保存涂鸦之后的图片
                mDone = onGraffitiSave();
            } else if (v.getId() == R.id.graffiti_btn_back) {
                //点击返回键，弹出提示框！
                if (!mGraffitiView.isModified()) {
                    finish();
                    return;
                }
                if (!(GraffitiParams.getDialogInterceptor() != null
                        && GraffitiParams.getDialogInterceptor().onShow(GraffitiActivity.this, mGraffitiView, GraffitiParams.DialogType.SAVE))) {

                    Dialog mDialog = DialogUtil.initCommonDialog(ctx, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case -2:
                                    finish();
                                    break;
                                case -1:
                                    mGraffitiView.save();
                                    break;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }, getString(R.string.graffiti_saving_picture));
                    mDialog.show();


//                    DialogController.showEnterCancelDialog(GraffitiActivity.this, getString(R.string.graffiti_saving_picture), null, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mGraffitiView.save();
//                        }
//                    }, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            finish();
//                        }
//                    });
                }
                mDone = true;
            }
            if (mDone) {
                return;
            }
            //涂鸦可选项编辑
            if (v.getId() == R.id.graffiti_selectable_edit) {
                //编辑文字
                if (mGraffitiView.getSelectedItem() instanceof GraffitiText) {
                    createGraffitiText((GraffitiText) mGraffitiView.getSelectedItem(), -1, -1);
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_selectable_remove) {
                //移除文字或者贴图
                mGraffitiView.removeSelectedItem();
                mDone = true;
            }
            if (mDone) {
                return;
            }
            //涂鸦形状配置
            if (v.getId() == R.id.btn_hand_write) {
                onHandShape();
            } else if (v.getId() == R.id.btn_arrow) {
                onArrowShape();
            } else if (v.getId() == R.id.btn_line) {
                onLineShape();
            } else if (v.getId() == R.id.btn_holl_circle) {
//                onHollCircleShape();
                onCloudLineShape();
            } else if (v.getId() == R.id.btn_fill_circle) {
                onFillCircleShape();
            } else if (v.getId() == R.id.btn_holl_rect) {
                onHollRectShape();
            } else if (v.getId() == R.id.btn_fill_rect) {
                onFillRectShape();
            }
            if (mLastShapeView != null) {
                mLastShapeView.setSelected(false);
            }
            v.setSelected(true);
            mLastShapeView = v;
        }
    }

    //设置当前模式是否是移动缩放模式
    public boolean onPicMoveScale(boolean doMove) {
        return doMove;
    }

    public boolean onSelectItemTop() {
        //可选项置顶
        mGraffitiView.topSelectedItem();
        return true;
    }


    public boolean onCentrePic() {
        //图片定位居中
        mGraffitiView.centrePic();
        return true;
    }

    public boolean onFillRectShape() {
        mGraffitiView.setShape(GraffitiView.Shape.FILL_RECT);
        return true;
    }

    public boolean onHollRectShape() {
        mGraffitiView.setShape(GraffitiView.Shape.HOLLOW_RECT);
        return true;
    }

    public boolean onFillCircleShape() {
        mGraffitiView.setShape(GraffitiView.Shape.FILL_CIRCLE);
        return true;
    }

    public boolean onHollCircleShape() {
        mGraffitiView.setShape(GraffitiView.Shape.HOLLOW_CIRCLE);
        return true;
    }

    public boolean onCloudLineShape() {
        mGraffitiView.setShape(GraffitiView.Shape.CLOUD_LINE);
        return true;
    }

    public boolean onLineShape() {
        mGraffitiView.setShape(GraffitiView.Shape.LINE);
        return true;
    }

    public boolean onArrowShape() {
        mGraffitiView.setShape(GraffitiView.Shape.ARROW);
        return true;
    }

    public boolean onHandShape() {
        mGraffitiView.setShape(GraffitiView.Shape.HAND_WRITE);
        return true;
    }

    public boolean onGraffitiSave() {
        mGraffitiView.save();
        return true;
    }

    public boolean onUndo() {
        mGraffitiView.undo();
        return true;
    }

    public boolean onPenBitmap() {
        configShapeModeLayVisible(false);
        mGraffitiView.setPen(GraffitiView.Pen.BITMAP);
        return true;
    }

    public boolean onPenText() {
        mIsMovingPic = false;
//        mPaintSizeBar.setProgress((int) (25 + 0.5f));
        mGraffitiView.setPaintSize(20);
        configShapeModeLayVisible(false);
        configGraffitiBgLayVisible(false);
        mGraffitiView.setPen(GraffitiView.Pen.TEXT);
        return true;
    }

    public boolean onPenEraser() {
        mIsMovingPic = false;
//        mPaintSizeBar.setProgress((int) (mGraffitiView.getPaintSize() + 0.5f));
        configShapeModeLayVisible(false);
        configGraffitiBgLayVisible(true);
        configEditItemLayVisible(false);
        mGraffitiView.setPen(GraffitiView.Pen.ERASER);
        onHandShape();
        return true;
    }

    public boolean onPenCopy() {
//        mPaintSizeBar.setProgress((int) (mGraffitiView.getPaintSize() + 0.5f));
        configShapeModeLayVisible(false);
        mGraffitiView.setPen(GraffitiView.Pen.COPY);
        return true;
    }

    public boolean onPenHand() {
        mIsMovingPic = false;
//        mPaintSizeBar.setProgress((int) (7 + 0.5f));
        mGraffitiView.setPaintSize(lastLineSize);
        configShapeModeLayVisible(false);
        configGraffitiBgLayVisible(true);
        configEditItemLayVisible(false);
        mGraffitiView.setPen(GraffitiView.Pen.HAND);
        onHollRectShape();
        return true;
    }

    //设置涂鸦类别和形状操作布局的可见性
    public void configShapeModeLayVisible(boolean show) {
    }

    //设置可选项(文字和贴图)操作布局的可见性
    public void configEditItemLayVisible(boolean show) {
        if (mSelectedTextEditContainer != null) {
            mSelectedTextEditContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    //设置涂鸦背景图的操作布局的可见性
    public void configGraffitiBgLayVisible(boolean show) {
    }

    @Override
    public void onBackPressed() { // 返回键监听
        if (mBtnMovePic.isSelected()) { // 当前是移动缩放模式，则退出该模式
            mBtnMovePic.performClick();
            return;
        } else { // 退出涂鸦
            findViewById(R.id.graffiti_btn_back).performClick();
        }
    }

    /**
     * 放大缩小
     */
    private class ScaleOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (v.getId() == R.id.btn_amplifier) {
                        scalePic(0.05f);
                    } else if (v.getId() == R.id.btn_reduce) {
                        scalePic(-0.05f);
                    }
                    v.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsScaling = false;
                    v.setSelected(false);
                    break;
            }
            return true;
        }
    }

    /**
     * 缩放
     * @param scaleStep 缩放步进
     */
    private void scalePic(final float scaleStep) {
        if (mIsScaling)
            return;
        mIsScaling = true;

        final float x = mGraffitiView.toX(mGraffitiView.getWidth() / 2);
        final float y = mGraffitiView.toY(mGraffitiView.getHeight() / 2);

        final Runnable task = new Runnable() {
            public void run() {
                if (!mIsScaling)
                    return;
                float scale = mGraffitiView.getScale();
                scale += scaleStep;
                if (scale > mMaxScale) {
                    scale = mMaxScale;
                    mIsScaling = false;
                } else if (scale < mMinScale) {
                    scale = mMinScale;
                    mIsScaling = false;
                }
                // 围绕屏幕中心缩放
                mGraffitiView.setScale(scale, x, y);

                if (mIsScaling) {
                    ThreadUtil.getInstance().runOnMainThread(this, TIME_SPAN);
                }
            }
        };
        task.run();
    }


    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.clearAnimation();
        view.startAnimation(mViewShowAnimation);
        view.setVisibility(View.VISIBLE);
        if (view == mSettingsPanel) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            if (view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0) {
                mGraffitiView.setAmplifierScale(-1);
            }
            return;
        }
        view.clearAnimation();
        view.startAnimation(mViewHideAnimation);
        view.setVisibility(View.GONE);

        if (view == mSettingsPanel && !mBtnMovePic.isSelected()) {
            // 当设置面板隐藏时才显示放大器
            mGraffitiView.setAmplifierScale(mGraffitiParams.mAmplifierScale);
        } else if ((view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0)) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }

    private class GraffitiGestureListener extends TouchGestureDetector.OnTouchGestureListener {

        private Float mLastFocusX;
        private Float mLastFocusY;
        // 手势操作相关
        private float mToucheCentreXOnGraffiti, mToucheCentreYOnGraffiti, mTouchCentreX, mTouchCentreY;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mGraffitiView.setTrans(mGraffitiView.getTransX() - distanceX, mGraffitiView.getTransY() - distanceY);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastFocusX = null;
            mLastFocusY = null;
            return true;
        }

        // 手势缩放
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // 屏幕上的焦点
            mTouchCentreX = detector.getFocusX();
            mTouchCentreY = detector.getFocusY();
            // 对应的图片上的焦点
            mToucheCentreXOnGraffiti = mGraffitiView.toX(mTouchCentreX);
            mToucheCentreYOnGraffiti = mGraffitiView.toY(mTouchCentreY);

            if (mLastFocusX != null && mLastFocusY != null) { // 焦点改变
                final float dx = mTouchCentreX - mLastFocusX;
                final float dy = mTouchCentreY - mLastFocusY;
                // 移动图片
                mGraffitiView.setTrans(mGraffitiView.getTransX() + dx, mGraffitiView.getTransY() + dy);
            }

            // 缩放图片
            float scale = mGraffitiView.getScale() * detector.getScaleFactor();
            if (scale > mMaxScale) {
                scale = mMaxScale;
            } else if (scale < mMinScale) {
                scale = mMinScale;
            }
            mGraffitiView.setScale(scale, mToucheCentreXOnGraffiti, mToucheCentreYOnGraffiti);

            mLastFocusX = mTouchCentreX;
            mLastFocusY = mTouchCentreY;
            return true;
        }
    }
}
