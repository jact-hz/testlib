package cn.pinming.cadshow.cad.data;

import com.weqia.data.UtilData;

/**
 * Created by berwin on 2017/4/24.
 */

public class CountInfo extends UtilData {

    private int rId;
    private String name;

    public CountInfo() {
    }

    public CountInfo(int rId, String name) {
        this.rId = rId;
        this.name = name;
    }

    public int getrId() {
        return rId;
    }

    public void setrId(int rId) {
        this.rId = rId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
