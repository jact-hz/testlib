package cn.pinming.cadshow.data;

import com.weqia.data.UtilData;

/**
 * Created by berwin on 2017/3/23.
 */

public class BottomViewData extends UtilData {

    private int vId;
    private String title;
    private int drawId;
    private boolean selected = false;

    public BottomViewData() {
    }

    public BottomViewData(int vId, String title, int drawId) {
        this.vId = vId;
        this.title = title;
        this.drawId = drawId;
    }

    public int getvId() {
        return vId;
    }

    public void setvId(int vId) {
        this.vId = vId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
