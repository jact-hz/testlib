package cn.pinming.cadshow.moveview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.weqia.utils.StrUtil;
import com.weqia.utils.bitmap.BitmapUtil;


import cn.pinming.cadshow.library.R;
import common.EnumData;


/**
 * Created by 20161005 on 2017/9/6.
 */

public class PreviewPicDialog extends Dialog {

    private Context mContext;

    public PreviewPicDialog( Context context) {
        super(context, 0);

    }


    public PreviewPicDialog( Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = (int) (display.getWidth());
        params.height = (int) (display.getHeight());
        getWindow().setAttributes(params);
    }

    public static class Builder {

        private Context context;
        private View.OnClickListener onClickListener;
        private String url;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public PreviewPicDialog build() {
            View rootView = LayoutInflater.from(context).inflate(R.layout.lay_pop_image, null);
            ImageView im = rootView.findViewById(R.id.im);
            TextView tvClose = rootView.findViewById(R.id.tv_close);
            PreviewPicDialog dialog = new PreviewPicDialog(context, R.style.dialog_common);
            dialog.addContentView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (StrUtil.notEmptyOrNull(url)) {
                BitmapUtil.getInstance().load(im, url, EnumData.ImageThumbTypeEnums.THUMB_BIG.value());
            }
            if (onClickListener != null) {
                tvClose.setOnClickListener(onClickListener);
            }
            return dialog;
        }
    }
}
