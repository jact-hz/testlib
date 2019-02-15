package cn.pinming.cadshow;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.weqia.utils.StrUtil;

import java.lang.ref.WeakReference;

import cn.pinming.cadshow.data.ShowDrawKey;
import cn.pinming.cadshow.library.R;

@SuppressLint("Registered")
public class SharedShowActivity extends AppCompatActivity implements OnClickListener {
    public Toolbar sharedTitleView;
    private SharedShowActivity ctx;
    protected MenuItem rightMenu;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        if (config == null) {
            config = new Configuration();
        }
        config.fontScale = 1f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        loadView();
    }


    public View getContentView() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        FrameLayout content = (FrameLayout) view.getChildAt(0);
        return content.getChildAt(0);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        loadView();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        loadView();
        ctx = this;
    }

    public void loadView() {
        sharedTitleView = (Toolbar) findViewById(R.id.toolbar);
//        doTintStatusBar(this);
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21 && getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        String title = getIntent().getStringExtra(ShowDrawKey.KEY_TOP_BANNER_TITLE);
        if (sharedTitleView != null) {
            sharedTitleView.setTitleMarginEnd(ShowDrawUtil.dip2px(this, 20));
            if (StrUtil.notEmptyOrNull(title)) {
                sharedTitleView.setTitle(title);
            }
            setSupportActionBar(sharedTitleView);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

//    public DbUtil getDbUtil() {
//        DbUtil tmpDbUtil = CADApplication.getInstance().getDbUtil();
//        return tmpDbUtil;
//    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    public static abstract class UIHandler extends Handler {
        WeakReference<SharedShowActivity> viewController;

        public UIHandler(SharedShowActivity activity) {
            viewController = new WeakReference<SharedShowActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SharedShowActivity baseViewController = viewController.get();
            handleMessage(msg, baseViewController);
        }

        public abstract void handleMessage(Message msg, SharedShowActivity viewController);
    }

    public SharedShowActivity getCtx() {
        return ctx;
    }

    public void setCtx(SharedShowActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onClick(View v) {
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME
                && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //设置ToolBar的选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void inflateMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cad_op_menu_text, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        rightMenu = menu.findItem(R.id.menu_right);
        if (rightMenu != null) {
            rightMenu.setVisible(false);
            optionMenuPrepared();
        }
        return true;
    }

    public void optionMenuPrepared() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_right) {
            rightClick();
        } else if (item.getItemId() == R.id.right_cancel) {
            toCancelAction();
        } else if (item.getItemId() == R.id.right_save) {
            toSaveAction();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void toSaveAction() {

    }

    protected void toCancelAction() {

    }

    public void rightClick() {
    }

    public MenuItem getRightMenu() {
        return rightMenu;
    }
}
