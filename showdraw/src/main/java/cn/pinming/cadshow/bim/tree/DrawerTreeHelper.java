package cn.pinming.cadshow.bim.tree;

import com.alibaba.fastjson.JSONArray;
import com.weqia.utils.L;
import com.weqia.utils.StrUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.pinming.cadshow.bim.AndroidUserMobileSurfaceView;
import cn.pinming.cadshow.bim.data.BuildInfo;
import cn.pinming.cadshow.bim.data.floor;
import osg.AndroidExample.EGLview;
import osg.AndroidExample.osgNativeLib;

/**
 * Created by ML on 2017/5/8.
 * 侧边栏数据转化类
 */

public class DrawerTreeHelper {
    /**
     * 原始数据
     * 1:101,102,201,401;
     * 2:101,102,201,401;
     * 3:101,102,201,401;
     * 4:101,102,201,401;
     * 5:101,102,201,401;
     * 6:101,102,201,401;
     * 7:101,102,201,401;
     * 8:101,102,201,401;
     * 9:101,102,201,401;
     * 10:101,102,201,401;
     * 11:101,102,201,401;
     * 12:101,102,201,401;
     * 13:101,102,201,401;
     * 15:101,102,201,401;
     * 16:101;
     */
//    private static AndroidUserMobileSurfaceView mSurfaceView;
    private static EGLview mEGLview;
    private static Map<String, String> sortFloor = new LinkedHashMap<>();
    private static List<BuildInfo> buildInfoList = new ArrayList<>();

/*    public static void setmSurfaceView(AndroidUserMobileSurfaceView mSurfaceView) {
        DrawerTreeHelper.mSurfaceView = mSurfaceView;
    }*/
    public static void setmEGLview(EGLview mEGLview) {
        DrawerTreeHelper.mEGLview = mEGLview;
    }

    private static String[] getStringArray(String msg, String splitStr) {
        if (StrUtil.isEmptyOrNull(msg) || StrUtil.isEmptyOrNull(splitStr)) {
            return null;
        }
        return msg.split(splitStr);
    }

    public static int[] getIntegerArray(String msg, String splitStr) {
        String[] str = getStringArray(msg, splitStr);
        if (str != null) {
            int[] indexs = new int[str.length];
            for (int i = 0; i < str.length; i++) {
                indexs[i] = Integer.parseInt(str[i]);
            }
            return indexs;
        } else {
            return null;
        }
    }

    /**
     * @param msg 初始数据
     * @return 返回每一层的类型总数据数据
     */
    public static String[] getFloorAllTypes(String msg) {
        return getStringArray(msg, ";");
    }

    public static String[] getFloorCount(String msg) {
        return getStringArray(msg, ":");
    }

    private static ArrayList<String[]> getFloorList(String msg, ArrayList<floor> floors) {
        ArrayList<String[]> strArray = new ArrayList<>();
        String[] resultSortTypes = getRealSortTypes(floors, msg);
        if (resultSortTypes != null && resultSortTypes.length > 0) {
            for (int i = 0; i < resultSortTypes.length; i++) {
                String[] arrays = getFloorCount(resultSortTypes[i]);
                if (arrays != null && arrays.length > 0) {
                    strArray.add(arrays);
                }
            }
        }
        return strArray;
    }

    public static ArrayList<String[]> getInitFloorList(String msg) {
        ArrayList<String[]> strArray = new ArrayList<>();
        String[] resultSortTypes = getFloorAllTypes(msg);
        if (resultSortTypes != null && resultSortTypes.length > 0) {
            for (int i = 0; i < resultSortTypes.length; i++) {
                String[] arrays = getFloorCount(resultSortTypes[i]);
                if (arrays != null && arrays.length > 0) {
                    strArray.add(arrays);
                }
            }
        }
        return strArray;
    }

    /**
     * @param floors 楼层类集合
     * @param msg    顺序有可能错乱的楼层类型信息
     */
    private static String[] getRealSortTypes(ArrayList<floor> floors, String msg) {
        String[] errorTypes = getFloorAllTypes(msg);
        String[] realSortTypes = new String[errorTypes.length];
//        Iterator listIterator = floors.listIterator();
//        while (listIterator.hasNext()) {
//            floor flo = (floor) listIterator.next();
//            if (flo.getShowid() == -1000) {
//                listIterator.remove();
//            }
//        }
        if (StrUtil.listNotNull(floors)) {
            //TODO: 根据标高进行排序
            if (floors.size() > 2) {
                Collections.sort(floors, new Comparator<floor>() {
                    @Override
                    public int compare(floor o1, floor o2) {
                        int elevation2 = o2.getShowid();
                        int elevation1 = o1.getShowid();
                        if (elevation2 > elevation1) {
                            return -1;
                        } else if (elevation2 < elevation1) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
            sortFloor.clear();
            //TODO: 2017/5/23  将id字段和 showId楼层字段建立正确顺序的映射关系
            for (int i = 0; i < floors.size(); i++) {
                String keyId = floors.get(i).getId() + "";
                String valueLcmc = floors.get(i).getLcmc();
                sortFloor.put(keyId, valueLcmc);
            }
            Set<String> sortKeys = new TreeSet<>();
            sortKeys = sortFloor.keySet();
            //TODO: 双层循环是为了排序
            Iterator it = sortKeys.iterator();
            int i = 0;
            while (it.hasNext()) {
                String realKey = (String) it.next();
                for (int j = 0; j < errorTypes.length; j++) {
                    String errorMsg = errorTypes[j];
                    int index = errorMsg.indexOf(":");
                    String startMsg = errorMsg.substring(0, index);
                    if (realKey.equals(startMsg)) {
//                        String realFloorName = sortFloor.get(realKey);
//                        String real = realFloorName.concat(errorMsg.substring(index));
                        realSortTypes[i++] = errorMsg;
                        break;
                    }
                }
            }
        }
        return realSortTypes;
    }

    /**
     * 过滤显示节点(经过排序且过滤之后要显示的数据集合)
     */
    public static ArrayList<DrawerTreeData> allSelLefaNode(ArrayList<DrawerTreeData> sortList) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        for (DrawerTreeData node : sortList) {
            //TODO: 只获取二级树和三级树的父布局展开的数据
            if ((node.getLevel() == 2 && node.getParentData().isbSelected()) || (node.getLevel() == 3 && node.getSuperParentData().isbSelected())) {
                resultNodes.add(node);
            }
        }
        return resultNodes;
    }
    public static ArrayList<DrawerTreeData> allSelRootNode(ArrayList<DrawerTreeData> sortList) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        for (DrawerTreeData node : sortList) {
            if ((node.getLevel() == 0)) {
                resultNodes.add(node);
            }
            if ((node.getLevel() == 1 && node.getParentData().isbExpand())) {
                resultNodes.add(node);
            }
        }
        return resultNodes;
    }

    public static ArrayList<DrawerTreeData> filterVisibleLeafNode(ArrayList<DrawerTreeData> sortList) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        L.e("sortList : " + sortList.toString());
        for (DrawerTreeData node : sortList) {
            //TODO: 只获取二级树和三级树的父布局展开的数据
            if (node.getLevel() == 2 || (node.getLevel() == 3 && node.getParentData().isbExpand())) {
                resultNodes.add(node);
            }
        }
        return resultNodes;
    }


    public static ArrayList<DrawerTreeData> filterAllLeafNode(ArrayList<DrawerTreeData> sortList) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        for (DrawerTreeData node : sortList) {
            if (node.getLevel() == 2 || node.getLevel() == 3) {
                resultNodes.add(node);
            }
        }
        return resultNodes;
    }

    /**
     * 过滤显示节点(经过排序且过滤之后要显示的数据集合)
     */
    public static ArrayList<DrawerTreeData> filterRootNode(ArrayList<DrawerTreeData> sortList) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        for (DrawerTreeData node : sortList) {
            //TODO: 只获取一级树所有的数据
            if (node.isRoot()) {
//                node.setbSelected(node.isbSelected());
                resultNodes.add(node);
            }
        }
        ArrayList<DrawerTreeData> resultNodesNew = new ArrayList<>();
        for (BuildInfo buildInfo : buildInfoList) {
            DrawerTreeData drawerTree= new DrawerTreeData(-1,buildInfo.getBuild(), 0);
            drawerTree.setbExpand(false);
            drawerTree.setbSelected(true);
            resultNodesNew.add(drawerTree);
            ArrayList<DrawerTreeData> childNodes = new ArrayList<>();
            for (int i = 0; i < buildInfo.getFloorIds().size(); i++) {
                for (int j = 0; j < resultNodes.size(); j++) {
                    if (buildInfo.getFloorIds().get(i) == resultNodes.get(j).getTypeIndex()) {
                        resultNodes.get(j).setParentData(drawerTree);
                        resultNodesNew.add(resultNodes.get(j));
                        childNodes.add(resultNodes.get(j));
                    }
                }
            }
            drawerTree.setChildDatas(childNodes);
        }

        return resultNodesNew;
    }


    /**
     * 节点排序
     */
    public static ArrayList<DrawerTreeData> getSortDraweDatas(String msg, ArrayList<floor> floors) {
        ArrayList<DrawerTreeData> resultNodes = new ArrayList<>();
        ArrayList<DrawerTreeData> relationNodes = getDrawerDatas(msg, floors);
        ArrayList<DrawerTreeData> rootNodes = getRootNodes(relationNodes);
        for (DrawerTreeData node : rootNodes) {
            addNodes(resultNodes, node);
        }
        return resultNodes;
    }

    private static void addNodes(List<DrawerTreeData> resultNodes, DrawerTreeData node) {
        resultNodes.add(node);
        if (node.isLeaf()) {
            return;
        }
        /**
         *递归添加达到排序效果
         */
        for (int i = 0; i < node.getChildDatas().size(); i++) {
            addNodes(resultNodes, node.getChildDatas().get(i));
        }
    }

    /**
     * 获取所有根节点
     *
     * @param lists
     * @return
     */
    private static ArrayList<DrawerTreeData> getRootNodes(ArrayList<DrawerTreeData> lists) {
        ArrayList<DrawerTreeData> rootList = new ArrayList<>();
        for (DrawerTreeData node : lists) {
            if (node.isRoot()) {
                rootList.add(node);
            }
        }
        return rootList;
    }

    /**
     * @param msg
     * @return 返回已经配置好关系的数据集
     */
    private static ArrayList<DrawerTreeData> getDrawerDatas(String msg, ArrayList<floor> floors) {
        List<BuildInfo> buildInfos = JSONArray.parseArray(msg, BuildInfo.class);
        String datas = "";
        for (int i = 0; i < buildInfos.size(); i++) {
            if (i == 0) {
                datas += (buildInfos.get(i).getValue());
            } else {
                datas += (";" + buildInfos.get(i).getValue());
            }
            String[] floorInfo = buildInfos.get(i).getValue().split(";");
            for (int j = 0; j < floorInfo.length; j++) {
                String[] data = floorInfo[j].split(":");
                buildInfos.get(i).getFloorIds().add(Integer.parseInt(data[0].toString().trim()));
            }
        }
        buildInfoList.clear();
        buildInfoList.addAll(buildInfos);
        ArrayList<String[]> floorTypes = getFloorList(datas, floors);
        ArrayList<DrawerTreeData> treeList = new ArrayList<>();
        if (StrUtil.listNotNull(floorTypes)) {
            for (String[] types : floorTypes) {
                if (types == null && types.length == 0) {
                    continue;
                }
                if (types.length == 1) {
                    int floorIndex = Integer.parseInt(types[0]);
                    String realFloorName = sortFloor.get(floorIndex + "");
                    DrawerTreeData oneLevelData = new DrawerTreeData(floorIndex, realFloorName + "楼层", 1);
                    treeList.add(oneLevelData);
                    continue;
                }
                int[] typeIndexs = getIntegerArray(types[1], ",");
                int floorIndex = Integer.parseInt(types[0]);
                String realFloorName = sortFloor.get(floorIndex + "");
                //TODO: 创建一级树数据类型
                DrawerTreeData oneLevelData = new DrawerTreeData(floorIndex, realFloorName + "楼层", 1);
                HashSet<String> twoLevelLabel = new HashSet<>();
                LinkedList twoLevelList = new LinkedList();
                if (typeIndexs == null && typeIndexs.length == 0) {
                    continue;
                }
                //* 1:101,102,201,401,801,805,806,808,902,903,5001
                for (int i = 0; i < typeIndexs.length; i++) {
                    int twoLevelIndex = 0;
                    if (typeIndexs[i] == 1001) {
                        twoLevelIndex = typeIndexs[i];
                    } else if (typeIndexs[i] == 5001) {
                        twoLevelIndex = 800;
                    } else {
                        if (typeIndexs[i] > 10000) {
                            if (typeIndexs[i] >= 10000 && typeIndexs[i] < 12000) {
                                twoLevelIndex = 10000;
                            } else if (typeIndexs[i] >= 12000 && typeIndexs[i] < 14000) {
                                twoLevelIndex = 12000;
                            } else if (typeIndexs[i] >= 14000 && typeIndexs[i] < 16000) {
                                twoLevelIndex = 14000;
                            } else if (typeIndexs[i] >= 16000 && typeIndexs[i] < 18000) {
                                twoLevelIndex = 16000;
                            } else if (typeIndexs[i] >= 18000 && typeIndexs[i] < 20000) {
                                twoLevelIndex = 18000;
                            } else if (typeIndexs[i] >= 20000 && typeIndexs[i] < 22000) {
                                twoLevelIndex = 20000;
                            }
                            else if (typeIndexs[i] >= 22000 && typeIndexs[i] < 24000) {
                                twoLevelIndex = 22000;
                            }else {
                                twoLevelIndex = typeIndexs[i] / 100 * 100;
                            }
                        } else {
                            twoLevelIndex = typeIndexs[i] / 100 * 100;
                        }
                    }
                    //TODO: 判断是否已经存在这个二级树类型
                    boolean isNOHave = twoLevelLabel.add(String.valueOf(twoLevelIndex));
                    if (isNOHave) {
                        String twoTypeMsg = null;
                         //先取comtypeinfo表里的数据
                        twoTypeMsg = (String) mEGLview.getComtypeMap().get(String.valueOf(twoLevelIndex));
                        if (StrUtil.isEmptyOrNull(twoTypeMsg) && mEGLview != null) {
//                            twoTypeMsg = (String) StructureTypeData.getStructureTypes().get(String.valueOf(twoLevelIndex));
                            twoTypeMsg = osgNativeLib.GetComtypeName(twoLevelIndex);
                        }
//                        L.e("twoLevelIndex :" + twoLevelIndex + "  twoTypeMsg :" + twoTypeMsg);
                        if (StrUtil.notEmptyOrNull(twoTypeMsg)) {
                            //TODO: 在一级树下创建二级树类型
                            DrawerTreeData twoLevelData = new DrawerTreeData(twoLevelIndex, twoTypeMsg, 2);
                            LinkedList threeLevelList = new LinkedList();
                            for (int j = 0; j < typeIndexs.length; j++) {
                                //TODO: 只去对应二级树下的三级树
                                if (twoLevelIndex == (typeIndexs[j] / 100 * 100) || (twoLevelIndex == 800 && typeIndexs[j] == 5001) || (twoLevelIndex >= 10000 && twoLevelIndex < typeIndexs[j] && typeIndexs[j] <= (twoLevelIndex + 2000))) {
                                    String threeTypeMsg = null;
                                    threeTypeMsg = (String) StructureTypeData.getStructureTypes().get(String.valueOf(typeIndexs[j]));
                                    if (StrUtil.isEmptyOrNull(threeTypeMsg) && mEGLview != null) {
//                                        threeTypeMsg = (String) mEGLview.getComtypeMap().get(String.valueOf(typeIndexs[j]));
                                        threeTypeMsg = osgNativeLib.GetComtypeName(typeIndexs[j]);
                                    }
//                                    L.e("threeLevelIndex :" + typeIndexs[j] + "  threeTypeMsg :" + threeTypeMsg);
                                    if (StrUtil.notEmptyOrNull(threeTypeMsg)) {
                                        //TODO: 在二级树下创建三级树类型
                                        DrawerTreeData threeLevelData = new DrawerTreeData(typeIndexs[j], threeTypeMsg, 3);
                                        threeLevelData.setParentData(twoLevelData);
                                        threeLevelData.setSuperParentData(oneLevelData);
                                        threeLevelList.add(threeLevelData);
                                        treeList.add(threeLevelData);
                                    }
                                }
                            }
                            twoLevelData.setParentData(oneLevelData);
                            twoLevelData.setChildDatas(threeLevelList);
                            twoLevelList.add(twoLevelData);
                            treeList.add(twoLevelData);
                        }
                    }
                }
                oneLevelData.setChildDatas(twoLevelList);
                treeList.add(oneLevelData);
            }
        }
        return treeList;
    }

}
