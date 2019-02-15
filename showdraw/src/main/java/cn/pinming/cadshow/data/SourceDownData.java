package cn.pinming.cadshow.data;

import com.weqia.data.UtilData;
import com.weqia.utils.annotation.sqlite.Id;
import com.weqia.utils.http.okserver.download.DownloadManager;

public class SourceDownData extends UtilData {

    private static final long serialVersionUID = 1L;
    private String url;
    private Long cdate;
    private
    @Id
    String docId;
    private String docName;
    private Integer docType; //1文件,2目录
    private String fileSize;
    private int type;
    private String mime;
    private String mid;
    private Integer orderNum;
    private String md5;
    private int dstate = DownloadManager.NONE;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private String parentId;

    public SourceDownData() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCdate() {
        return cdate;
    }

    public void setCdate(Long cdate) {
        this.cdate = cdate;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public int getDstate() {
        return dstate;
    }

    public void setDstate(int dstate) {
        this.dstate = dstate;
    }

}
