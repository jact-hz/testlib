package cn.pinming.cadshow.bim.data;

import com.weqia.data.UtilData;
import com.weqia.utils.annotation.sqlite.Table;

/**
 * Created by berwin on 2017/3/17.
 */

@Table(name = "TransValue")
public class TransValue extends UtilData {

    private int floorid;
    private int comtype;
    private String parmname;
    private String parmvalue;

    public int getFloorid() {
        return floorid;
    }

    public void setFloorid(int floorid) {
        this.floorid = floorid;
    }

    public int getComtype() {
        return comtype;
    }

    public void setComtype(int comtype) {
        this.comtype = comtype;
    }

    public String getParmname() {
        return parmname;
    }

    public void setParmname(String parmname) {
        this.parmname = parmname;
    }

    public String getParmvalue() {
        return parmvalue;
    }

    public void setParmvalue(String parmvalue) {
        this.parmvalue = parmvalue;
    }
}
