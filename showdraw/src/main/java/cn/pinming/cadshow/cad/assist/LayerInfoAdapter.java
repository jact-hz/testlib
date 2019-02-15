package cn.pinming.cadshow.cad.assist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weqia.utils.view.CommonImageView;

import java.util.ArrayList;
import java.util.List;

import cn.pinming.cadshow.library.R;
import cn.pinming.cadshow.cad.data.LayerInfo;

public class LayerInfoAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<LayerInfo> items;

    public LayerInfoAdapter(Context context) {
        this.ctx = context;
    }

    public void setItems(ArrayList<LayerInfo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<LayerInfo> getItems() {
        return items;
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (items != null) {
            return items.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PicViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(ctx);
            convertView = mInflater.inflate(R.layout.cad_op_cell_dialog_layerinfo, null);
            holder = new PicViewHolder();
            holder.ivCatelog = (CommonImageView) convertView.findViewById(R.id.iv_catelog_image);
            holder.tvCatelogName = (TextView) convertView.findViewById(R.id.tv_catelog_title);
            holder.btSelected = (ImageButton) convertView.findViewById(R.id.ib_catelog_select);
            convertView.setTag(holder);
        } else {
            holder = (PicViewHolder) convertView.getTag();
        }
        setDatas(position, holder);
        return convertView;
    }


    public void setDatas(int position, PicViewHolder holder) {
        LayerInfo info = (LayerInfo) getItem(position);
        if (info == null) {
            return;
        }
//        L.e(info.getiColor() + "-----");
        int color = info.getiColor();
        int red = color >> 16;
        int green = ((char)color) >> 8;
        int blue = color & 0x0000ff;
        holder.ivCatelog.setBackgroundColor(Color.rgb(red, green, blue));
        holder.tvCatelogName.setText(info.getStrLayerName());
        holder.btSelected.setSelected(info.isShow());

    }

    public class PicViewHolder {
        public CommonImageView ivCatelog;
        public TextView tvCatelogName;
        public ImageButton btSelected;
    }
}
