package cn.pinming.modelsdk;

/**
 * Created by lgf on 2019/1/23.
 */

public enum  UrlPathEnum {
    MODELDOWN("/front/bimfile/mobileModelConstructInfoCors", "模型下载地址"),
    FINDCONVERTINFO("/front/bimfile/findConvertInfo", "获取模型图纸转换状态"),
//    NO("2", "不显示"),
    //
    ;
    private String value;
    private String strName;

    UrlPathEnum(String value, String strName) {
        this.value = value;
        this.strName = strName;
    }

    public String getValue() {
        return value;
    }

    public String getStrName() {
        return strName;
    }

/*    public static UrlPathEnum valueOf(String value) {
        for (UrlPathEnum requestType : UrlPathEnum.values()) {
            if (requestType.getValue().equals(value) ) {
                return requestType;
            }
        }
        return UrlPathEnum.MODELDOWN;
    }*/
}
