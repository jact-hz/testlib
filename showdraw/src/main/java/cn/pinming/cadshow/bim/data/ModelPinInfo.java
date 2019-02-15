package cn.pinming.cadshow.bim.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.weqia.data.UtilData;
import com.weqia.utils.StrUtil;

/**
 * Created by berwin on 2017/9/5.
 * {
 * "mpId": 160,
 * "nodeId": "a7fa0148-01b9-3927-755e-9e53684da56e",
 * "floorName": "1",
 * "name": "1",
 * "type": 2,
 * "info": "1A20",
 * "tasks": [
 * {
 * "tkId": 413,
 * "title": "10",
 * "prinId": "402880825ea3766e015ea4303ed30099",
 * "perm": true
 * }
 * ],
 * "cId": "7647",
 * "cDate": 1506503121000
 * }
 */


public class ModelPinInfo extends UtilData {

    private String handle;
    private String tasks;

    private String mpId;
    private String nodeId;
    private String floorName;
    @JSONField(name = "queryFloorId")
    private int floorId;
    private String name;
    /**
     *标注序号
     */
    private String text;
    private String type;  //视口0   标注1   构件2'
    private String info;
    private String viewInfo;
    private String photo;
    private String versionId;
    private String bucket;
    private String key;
    private Integer accountType;
    private String cId;
    private String cDate;
    private Integer fileSize;
    private String fileName;
    private String orderId;
    private String flowId;
    private String behavior;

    private boolean isQr = false;   //判断是否通过构建二维码扫描进入模型，是就高亮显示否则就红点标记

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public enum ModePinType {
        SEEPOS(0, "视口"),
        MARK(1, "标注"),
        COMPONENT(2, "构件"),
        MARK_DELETE(3, "标注删除");

        private String strName;
        private int value;

        private ModePinType(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public String strName() {
            return strName;
        }

        public int value() {
            return value;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }


    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getViewInfo() {
        return viewInfo;
    }

    public void setViewInfo(String viewInfo) {
        this.viewInfo = viewInfo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getText() {
        return StrUtil.notEmptyOrNull(text)?text:"";
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public boolean isQr() {
        return isQr;
    }

    public void setQr(boolean qr) {
        isQr = qr;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }
}