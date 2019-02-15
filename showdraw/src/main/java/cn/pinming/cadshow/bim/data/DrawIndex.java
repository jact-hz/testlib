package cn.pinming.cadshow.bim.data;

import com.weqia.data.UtilData;
import com.weqia.utils.annotation.sqlite.Id;
import com.weqia.utils.annotation.sqlite.Table;

/**
 * Created by berwin on 2017/3/17.
 */

@Table(name = "drawindex")
public class DrawIndex extends UtilData {

    private @Id int id;
    private int floorid;
    private int comtype;
    private String name;
    private int showindex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowindex() {
        return showindex;
    }

    public void setShowindex(int showindex) {
        this.showindex = showindex;
    }
}
