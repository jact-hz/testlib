package cn.pinming.cadshow.cad.data;

import com.weqia.data.UtilData;

public class LayerInfo extends UtilData {

    private String strLayerName;
    private int iColor;
    private boolean show = true; //是否显示

    public String getStrLayerName() {
        return strLayerName;
    }

    public void setStrLayerName(String strLayerName) {
        this.strLayerName = strLayerName;
    }

    public int getiColor() {
        return iColor;
    }

    public void setiColor(int iColor) {
        this.iColor = iColor;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}