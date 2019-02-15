package cn.pinming.cadshow.bim.data;



import java.util.ArrayList;
import java.util.List;

import common.BaseData;

public class BuildInfo extends BaseData {
    private String build;           //建筑名称
    private String value;             //楼层和构件信息
    private List<Integer> floorIds = new ArrayList<>();             //楼层集合

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Integer> getFloorIds() {
        return floorIds;
    }

    public void setFloorIds(List<Integer> floorIds) {
        this.floorIds = floorIds;
    }
}
