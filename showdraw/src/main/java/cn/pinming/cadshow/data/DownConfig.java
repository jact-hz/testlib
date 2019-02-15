package cn.pinming.cadshow.data;

import com.weqia.data.UtilData;

/**
 * Created by berwin on 2017/7/13.
 */

public class DownConfig extends UtilData {


    private String appKey;  //请求的appkey
    private Integer downItype;  //下载的itype
    private String severIp;     //服务器IP
    private String signKey;     //签名的Key
    private Integer sourceType; //sourcetype
    private Integer realUrlItype;   //下载真实地址的itype

    public Integer getGzItype() {
        return gzItype;
    }

    public void setGzItype(Integer gzItype) {
        this.gzItype = gzItype;
    }

    private Integer gzItype;    //构件列表的itype

    public DownConfig() {
    }

//    @Deprecated
//    public DownConfig(String appKey, Integer downItype, String severIp, String signKey, Integer sourceType, Integer realUrlItype) {
//        this.appKey = appKey;
//        this.downItype = downItype;
//        this.severIp = severIp;
//        this.signKey = signKey;
//        this.sourceType = sourceType;
//        this.realUrlItype = realUrlItype;
//    }

    public DownConfig(String appKey, Integer downItype, String severIp, String signKey, Integer sourceType, Integer realUrlItype, Integer gzItype) {
        this.appKey = appKey;
        this.downItype = downItype;
        this.severIp = severIp;
        this.signKey = signKey;
        this.sourceType = sourceType;
        this.realUrlItype = realUrlItype;
        this.gzItype = gzItype;
    }

    public String getSeverIp() {
//        return "http://121.199.31.7";
        return severIp;
    }

    public void setSeverIp(String severIp) {
        this.severIp = severIp;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getDownItype() {
        return downItype;
    }

    public void setDownItype(Integer downItype) {
        this.downItype = downItype;
    }

    public Integer getRealUrlItype() {
        return realUrlItype;
    }

    public void setRealUrlItype(Integer realUrlItype) {
        this.realUrlItype = realUrlItype;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }
}
