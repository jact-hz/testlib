package cn.pinming.cadshow.bim.tree;

import com.weqia.utils.StrUtil;

import java.util.List;

/**
 * Created by ML on 2017/5/8.
 */

public class DrawerTreeData {

    public DrawerTreeData(int typeIndex, String typeString, int level) {
        this.typeIndex = typeIndex;
        this.typeString = typeString;
        this.level = level;
    }

    @Override
    public String toString() {
        return "typeIndex: " + typeIndex + "   typeString: " + typeString + "    level " + level + "  bExpand :" + bExpand + "  bSelected : " + bSelected;
    }

    private boolean bExpand;
    public boolean bSelected;  //默认全部选中

    private int level;
    private int typeIndex;
    private String typeString;

    private List<DrawerTreeData> childDatas;
    private DrawerTreeData parentData;
    //一级树数据
    private DrawerTreeData superParentData;

    /**
     * 判断是否是根节点
     *
     * @return
     */
    public boolean isRoot() {
        if (parentData == null) {
            return true;
        }
        return false;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parentData == null) {
            return false;
        }
        return parentData.isbExpand();
    }

    /**
     * 判断是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf() {

        if (StrUtil.listIsNull(childDatas)) {
            return true;
        }
        return false;
    }

    /**
     * 计算当前节点的层级
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    public boolean isbExpand() {
        return bExpand;
    }

    public void setbExpand(boolean bExpand) {
        this.bExpand = bExpand;
        if (!bExpand) {
            if (StrUtil.listNotNull(childDatas)) {
                for (DrawerTreeData data : childDatas) {
                    data.setbExpand(false);
                }
            }
        }
    }

    public boolean isbSelected() {
        return bSelected;
    }

    public void setbSelected(boolean bSelected) {
        this.bSelected = bSelected;
        if (StrUtil.listNotNull(childDatas)) {
            for (DrawerTreeData data : childDatas) {
                data.setbSelected(bSelected);
            }
        }
        if (level == 3 && getParentData() != null) {
            if (bSelected) {
                //如果是选中的话，就判断该父类下的所有子类是否都是选中状态，如果是父类也自动勾选上
                if (StrUtil.listNotNull(getParentData().getChildDatas())) {
                    for (DrawerTreeData data : getParentData().getChildDatas()) {
                        getParentData().bSelected = data.isbSelected();
                        if (!data.isbSelected()) {
                            break;
                        }
                    }
                }
            } else {
                //如果是取消选中，那父类自动取消选中
                getParentData().bSelected = false;
            }
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public void setChildDatas(List<DrawerTreeData> childDatas) {
        this.childDatas = childDatas;
    }

    public List<DrawerTreeData> getChildDatas() {
        return childDatas;
    }

    public DrawerTreeData getParentData() {
        return parentData;
    }

    public void setParentData(DrawerTreeData parentData) {
        this.parentData = parentData;
    }

    public DrawerTreeData getSuperParentData() {
        return superParentData;
    }

    public void setSuperParentData(DrawerTreeData superParentData) {
        this.superParentData = superParentData;
    }
}
