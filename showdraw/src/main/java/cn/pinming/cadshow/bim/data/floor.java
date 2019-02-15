package cn.pinming.cadshow.bim.data;

import com.weqia.data.UtilData;
import com.weqia.utils.annotation.sqlite.Id;
import com.weqia.utils.annotation.sqlite.Table;

/**
 * 数据库的floor表
 */

@Table(name = "floor")
public class floor extends UtilData {
    private @Id int id;
    private int showid;
    private String lcmc;        //楼层名称
    private String lcxz;
    private Integer cs;         //层数
    private String cg;      //层高
    private int ldmbg;          //楼地面标高
    private String tqd;
    private String zdj;
    private String sjdj;
    private String filename;
    private String bz;
    private String ztjzmj;
    private String ytjzmj;
    private String jss;
    private Integer bh;
    private String dmj;
    private String tqd_qz;
    private String tqd_lb;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShowid() {
        return showid;
    }

    public void setShowid(int showid) {
        this.showid = showid;
    }

    public String getLcmc() {
        return lcmc;
    }

    public void setLcmc(String lcmc) {
        this.lcmc = lcmc;
    }

    public String getLcxz() {
        return lcxz;
    }

    public void setLcxz(String lcxz) {
        this.lcxz = lcxz;
    }

    public Integer getCs() {
        return cs;
    }

    public void setCs(Integer cs) {
        this.cs = cs;
    }

    public String getCg() {
        return cg;
    }

    public void setCg(String cg) {
        this.cg = cg;
    }

    public int getLdmbg() {
        return ldmbg;
    }

    public void setLdmbg(int ldmbg) {
        this.ldmbg = ldmbg;
    }

    public String getTqd() {
        return tqd;
    }

    public void setTqd(String tqd) {
        this.tqd = tqd;
    }

    public String getZdj() {
        return zdj;
    }

    public void setZdj(String zdj) {
        this.zdj = zdj;
    }

    public String getSjdj() {
        return sjdj;
    }

    public void setSjdj(String sjdj) {
        this.sjdj = sjdj;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getZtjzmj() {
        return ztjzmj;
    }

    public void setZtjzmj(String ztjzmj) {
        this.ztjzmj = ztjzmj;
    }

    public String getYtjzmj() {
        return ytjzmj;
    }

    public void setYtjzmj(String ytjzmj) {
        this.ytjzmj = ytjzmj;
    }

    public String getJss() {
        return jss;
    }

    public void setJss(String jss) {
        this.jss = jss;
    }

    public Integer getBh() {
        return bh;
    }

    public void setBh(Integer bh) {
        this.bh = bh;
    }

    public String getDmj() {
        return dmj;
    }

    public void setDmj(String dmj) {
        this.dmj = dmj;
    }

    public String getTqd_qz() {
        return tqd_qz;
    }

    public void setTqd_qz(String tqd_qz) {
        this.tqd_qz = tqd_qz;
    }

    public String getTqd_lb() {
        return tqd_lb;
    }

    public void setTqd_lb(String tqd_lb) {
        this.tqd_lb = tqd_lb;
    }
}
