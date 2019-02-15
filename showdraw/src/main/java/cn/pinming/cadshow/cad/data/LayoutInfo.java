package cn.pinming.cadshow.cad.data;

import com.weqia.data.UtilData;

/**
 * Created by berwin on 2017/3/16.
 */

public class LayoutInfo extends UtilData {
    private String name;
    protected boolean show;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public LayoutInfo(String name, boolean show) {
        this.name = name;
        this.show = show;
    }

    public LayoutInfo() {
    }
}
