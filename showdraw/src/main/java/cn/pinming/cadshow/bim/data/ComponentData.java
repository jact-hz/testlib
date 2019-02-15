package cn.pinming.cadshow.bim.data;


import common.BaseData;

public class ComponentData extends BaseData {
    private String strName;   //构件名
    private String strValue;         //构件的值

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }
}
