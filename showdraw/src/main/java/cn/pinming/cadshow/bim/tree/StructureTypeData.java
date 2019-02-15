package cn.pinming.cadshow.bim.tree;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.weqia.utils.L;
import com.weqia.utils.StrUtil;

import java.util.HashMap;

import cn.pinming.cadshow.bim.MobileSurfaceActivity;

import static android.content.Context.MODE_PRIVATE;
import static cn.pinming.cadshow.bim.MobileSurfaceActivity.STRUCTURE_TYPE_DATA;

/**
 * Created by ML on 2017/5/9.
 */

public class StructureTypeData {
    /**
     * K : 代表建筑类型下标
     * V： 代表建筑类型名称
     */
    private static HashMap<String, String> structureTypes;
    private static SharedPreferences preferences;

    public StructureTypeData(MobileSurfaceActivity ctx) {
        preferences = ctx.getSharedPreferences("CADShow", MODE_PRIVATE);

    }

    public static HashMap<String, String> getStructureTypes() {
        if (structureTypes == null) {
            String dataMsg = preferences.getString(STRUCTURE_TYPE_DATA, null);
            if (StrUtil.notEmptyOrNull(dataMsg)) {
                HashMap<String, String> map = JSONObject.parseObject(dataMsg, HashMap.class);
                if (map != null) {
                    StructureTypeData.setStructureTypes(map);
                    L.e("采用网上数据>>>>>>>>>>");
                    return structureTypes;
                }
            }
            L.e("没有请求到数据,在本地添加");
            structureTypes = new HashMap();
            structureTypes.put("-10", "定位");
            /***** 梁系列 *****/
            structureTypes.put("100", "梁");
            structureTypes.put("101", "框架梁");
            structureTypes.put("102", "次梁");
            structureTypes.put("103", "独立梁");
            structureTypes.put("104", "基础梁");
            structureTypes.put("105", "圈梁");
            structureTypes.put("106", "过梁");
            structureTypes.put("107", "基础连梁");
            structureTypes.put("108", "承台梁");
            structureTypes.put("109", "吊筋");
            structureTypes.put("110", "转角筋");
            structureTypes.put("111", "连梁");
            structureTypes.put("112", "暗梁");
            structureTypes.put("113", "地框梁");
            structureTypes.put("114", "基础梁加腋");
            structureTypes.put("116", "扁梁核心区");
            /***** 柱系列 *****/
            structureTypes.put("200", "柱");
            structureTypes.put("201", "砼柱");
            structureTypes.put("202", "构造柱");
            structureTypes.put("203", "暗柱");
            structureTypes.put("204", "砖柱");
            structureTypes.put("205", "柱帽");
            /***** 墙系列 *****/
            structureTypes.put("300", "墙");
            structureTypes.put("301", "0墙");
            structureTypes.put("302", "砼外墙");
            structureTypes.put("303", "砼内墙");
            structureTypes.put("304", "砖外墙");
            structureTypes.put("305", "砖内墙");
            structureTypes.put("306", "填充墙");
            structureTypes.put("307", "间壁墙");
            structureTypes.put("308", "电梯井墙");
            structureTypes.put("309", "幕墙");
            structureTypes.put("312", "砌体加筋");
            structureTypes.put("313", "剪力墙");
            structureTypes.put("314", "砌体墙");
            structureTypes.put("315", "外墙外边线");
            structureTypes.put("316", "满铺");
            structureTypes.put("317", "人防墙");
            structureTypes.put("318", "墙墙加腋");
            structureTypes.put("319", "墙板加腋");
            /***** 板系列 *****/
            structureTypes.put("400", "板.楼梯");
            structureTypes.put("401", "现浇板");
            structureTypes.put("402", "预制板");
            structureTypes.put("403", "楼梯");
            structureTypes.put("404", "拱形板");
            structureTypes.put("405", "球形板");
            structureTypes.put("406", "螺旋板");
            structureTypes.put("407", "板洞");
            structureTypes.put("408", "梯板");
            structureTypes.put("409", "屋脊线");
            structureTypes.put("410", "梁板加腋");
            structureTypes.put("1002", "板洞");
            /***** 基础系列 *****/
            structureTypes.put("500", "基础");
            structureTypes.put("501", "砼条基");
            structureTypes.put("502", "砖石条基");
            structureTypes.put("503", "独立基础");
            structureTypes.put("504", "承台");
            structureTypes.put("505", "人工挖孔灌注桩");
            structureTypes.put("506", "其他桩");
            structureTypes.put("507", "满堂基础");
            structureTypes.put("508", "集水井");
            structureTypes.put("509", "标准承台");
            structureTypes.put("510", "井坑");
            structureTypes.put("511", "实体集水井");
            structureTypes.put("512", "柱墩");
            structureTypes.put("513", "灌注桩");
            /***** 门窗系列 *****/
            structureTypes.put("600", "门窗");
            structureTypes.put("601", "门");
            structureTypes.put("602", "窗");
            structureTypes.put("603", "墙洞");
            structureTypes.put("604", "飘窗");
            structureTypes.put("605", "转角飘窗");
            structureTypes.put("606", "老虎窗");
            structureTypes.put("607", "壁龛");
            structureTypes.put("608", "窗台");
            structureTypes.put("609", "门剁");
            structureTypes.put("610", "门窗套");
            structureTypes.put("1004", "飘窗洞");
            structureTypes.put("1005", "转角飘窗洞");
            /***** 装饰系列 *****/
            structureTypes.put("700", "装饰");
            structureTypes.put("701", "房间");
            structureTypes.put("702", "楼地面");
            structureTypes.put("703", "踢脚线");
            structureTypes.put("704", "内墙面");
            structureTypes.put("705", "外墙面");
            structureTypes.put("706", "墙裙");
            structureTypes.put("707", "柱面");
            structureTypes.put("708", "柱墙裙");
            structureTypes.put("709", "柱踢脚");
            structureTypes.put("710", "天棚");
            structureTypes.put("711", "屋面");
            structureTypes.put("1003", "屋面轮宽阔线");
            structureTypes.put("712", "保温层");
            structureTypes.put("713", "单梁装饰");
            /***** 零星系列 *****/
            structureTypes.put("800", "零星");
            structureTypes.put("801", "阳台");
            structureTypes.put("802", "雨篷");
            structureTypes.put("803", "栏杆");
            structureTypes.put("804", "散水");
            structureTypes.put("805", "檐沟");
            structureTypes.put("806", "台阶");
            structureTypes.put("807", "坡道");
            structureTypes.put("808", "排水沟");
            structureTypes.put("809", "后浇带");
            structureTypes.put("810", "天井");
            structureTypes.put("811", "外墙节点");
            structureTypes.put("812", "建筑面积");
            structureTypes.put("813", "压顶");
            structureTypes.put("814", "栏板");
            structureTypes.put("815", "阳台雨棚折实");
            structureTypes.put("816", "实体台阶");
            structureTypes.put("817", "实体坡道");
            structureTypes.put("818", "施工段");
            /***** 其它系列 *****/
            structureTypes.put("900", "其它");
            structureTypes.put("901", "点构件");
            structureTypes.put("902", "线构件");
            structureTypes.put("903", "面构件");
            structureTypes.put("904", "体构件");
            structureTypes.put("1001", "轴网");
            structureTypes.put("1002", "工程设置");
            structureTypes.put("1003", "报表");
            structureTypes.put("1004", "CAD转化");
            structureTypes.put("1005", "三维体ComType，用于特殊三维体定制");
            structureTypes.put("1006", "模板支架");
            /***** 板筋系列 *****/
            structureTypes.put("1100", "板筋系列");
            structureTypes.put("1101", "底筋");
            structureTypes.put("1102", "面筋");
            structureTypes.put("1103", "跨板负筋");
            structureTypes.put("1104", "双层双向钢筋");
            structureTypes.put("1105", "支座负筋");
            structureTypes.put("1106", "温度筋");
            structureTypes.put("1107", "楼层板带:跨中板带和柱上板带");
            structureTypes.put("1108", "撑筋");
            structureTypes.put("1109", "板筋区域");
            structureTypes.put("1110", "放射筋");
            /***** 筏筋系列 *****/
            structureTypes.put("1200", "筏筋系列");
            structureTypes.put("1201", "筏板底筋");
            structureTypes.put("1202", "筏板面筋");
            structureTypes.put("1203", "跨筏板筋");
            structureTypes.put("1204", "中层筋");
            structureTypes.put("1205", "筏板负筋");
            structureTypes.put("1206", "U形封边筋");
            structureTypes.put("1207", "基础板带: 跨中板带和柱上板带");
            structureTypes.put("1208", "撑筋");
            structureTypes.put("1209", "筏板筋区域");
            structureTypes.put("1210", "筏板放射筋");

            structureTypes.put("1300", "土方");
            structureTypes.put("1301", "大开挖");
            structureTypes.put("1302", "地下室范围");
            structureTypes.put("1303", "中心岛");
            structureTypes.put("1304", "出土道路");
            structureTypes.put("1305", "土方分段");
            structureTypes.put("1306", "支撑梁");
            structureTypes.put("1307", "支撑柱");
            structureTypes.put("1308", "支撑系统，用于在Drawbar中进行分类");
            structureTypes.put("1309", "场布土方回填");

            structureTypes.put("1400", "预留洞");
            structureTypes.put("1401", "预留洞");

            structureTypes.put("1500", "地形");
            structureTypes.put("1501", "地形");
            structureTypes.put("1502", "构件布置区");

            //BIM软件
            structureTypes.put("5001", "施工段");
            /***** BIM安全模块系列 *****/
            structureTypes.put("5100", "模板支架");
            structureTypes.put("5101", "立杆");
            structureTypes.put("5102", "横杆");
            structureTypes.put("5103", "竖向剪刀撑");
            structureTypes.put("5104", "横向剪刀撑");
            structureTypes.put("5105", "梁侧模板");
            structureTypes.put("5106", "连墙件");
            structureTypes.put("5120", "墙模板");
            structureTypes.put("5130", "柱模板");
            /***** 三维显示生成图层系列 *****/
            structureTypes.put("5110", "面板");
            structureTypes.put("5111", "小梁");
            structureTypes.put("5112", "主梁");
            structureTypes.put("5113", "扣件");
            structureTypes.put("5201", "剖切面实体");
            structureTypes.put("5202", "框选剖切实体");
            structureTypes.put("5203", "图纸备注实体");
            structureTypes.put("5204", "配模结果实体");
            /***** BIM外脚手架模块系列 *****/
            structureTypes.put("6100", "外脚手架");
            structureTypes.put("6101", "多排落地脚手架");
            structureTypes.put("6102", "悬挑脚手架");
            structureTypes.put("6103", "悬挑脚手架阳角");
            structureTypes.put("6104", "悬挑搁置主梁");
            structureTypes.put("6119", "外脚手架绘制线");
            structureTypes.put("6120", "外脚手架分段边线");
            structureTypes.put("6121", "外脚手架竖向剪刀撑");
            structureTypes.put("6122", "外脚手架立杆");
            structureTypes.put("6123", "外脚手架水平杆");
            structureTypes.put("6124", "外脚手架连墙件");
            structureTypes.put("6125", "外脚手架型钢");
            structureTypes.put("6126", "外脚手架脚手板");
            structureTypes.put("6127", "上拉钢丝绳");
            structureTypes.put("6128", "横向斜撑");
            structureTypes.put("6129", "安全网 ");
            structureTypes.put("6130", "外脚手架扶手栏杆");
            structureTypes.put("6131", "外脚手架附加水平杆");
            structureTypes.put("6132", "脚手架扣件");
            structureTypes.put("6133", "上拉下撑");
            structureTypes.put("6134", "底座/垫木");
            /***** BIM场地布置模块系列 *****/
            structureTypes.put("8000", "BIM场地布置模块系列");
            structureTypes.put("8100", "建筑及构筑物");
            structureTypes.put("8101", "硬化地面");
            structureTypes.put("8102", "砌体围墙");
            structureTypes.put("8103", "彩钢瓦围挡");
            structureTypes.put("8104", "广告牌围墙");
            structureTypes.put("8105", "围栏式围挡");
            structureTypes.put("8106", "木栅栏围挡");
            structureTypes.put("8107", "矩形门梁大门");
            structureTypes.put("8108", "无门梁大门");
            structureTypes.put("8109", "三角形梁大门");
            structureTypes.put("8110", "拱形门梁大门");
            structureTypes.put("8111", "电动伸缩大门");
            structureTypes.put("8112", "角门");
            structureTypes.put("8113", "拟建建筑");
            structureTypes.put("8114", "装修外立面");
            structureTypes.put("8115", "原有建筑");
            structureTypes.put("8116", "单双层板房");
            structureTypes.put("8117", "多层板房");
            structureTypes.put("8118", "食堂");
            structureTypes.put("8119", "卫浴");
            structureTypes.put("8120", "岗亭");
            structureTypes.put("8121", "卫生间");
            structureTypes.put("8122", "移动厕所");
            structureTypes.put("8123", "集装箱不带走廊");
            structureTypes.put("8124", "集装箱带走廊");
            structureTypes.put("8125", "单跑楼梯");
            structureTypes.put("8126", "双跑楼梯");
            structureTypes.put("8127", "敞开板房式");
            structureTypes.put("8128", "矩形凉亭");
            structureTypes.put("8129", "六边形凉亭");
            structureTypes.put("8130", "道路");
            structureTypes.put("8131", "面域道路");
            structureTypes.put("8132", "地磅");
            structureTypes.put("8133", "洗车槽");
            structureTypes.put("8134", "洗轮机");
            structureTypes.put("8135", "防撞墩");
            structureTypes.put("8136", "防撞桶");
            structureTypes.put("8137", "反光锥");
            structureTypes.put("8138", "旗台");
            structureTypes.put("8139", "路口");
            structureTypes.put("8140", "拟建建筑洞口");


            structureTypes.put("8200", "生活区临建");
            structureTypes.put("8201", "水源");
            structureTypes.put("8202", "水管");
            structureTypes.put("8203", "水管立管");
            structureTypes.put("8204", "明排水沟");
            structureTypes.put("8205", "暗排水沟");
            structureTypes.put("8206", "集水井");
            structureTypes.put("8207", "排污管");
            structureTypes.put("8208", "排污管立管");
            structureTypes.put("8209", "排污井");
            structureTypes.put("8210", "沉淀池");
            structureTypes.put("8211", "化粪池");
            structureTypes.put("8212", "隔油池");
            structureTypes.put("8213", "单侧洗漱台");
            structureTypes.put("8214", "两侧洗漱台");
            structureTypes.put("8215", "简易晾衣区");
            structureTypes.put("8216", "晾衣棚");
            structureTypes.put("8217", "停车场");
            structureTypes.put("8218", "垃圾站");
            structureTypes.put("8219", "不锈钢垃圾桶");
            structureTypes.put("8220", "环卫垃圾桶");
            structureTypes.put("8221", "水塔");

            structureTypes.put("8300", "加工场及堆场");
            structureTypes.put("8301", "木方堆场");
            structureTypes.put("8302", "模板堆场");
            structureTypes.put("8303", "钢筋堆场");
            structureTypes.put("8304", "扣件堆场");
            structureTypes.put("8305", "钢筋成品堆");
            structureTypes.put("8306", "钢筋半成品堆");
            structureTypes.put("8307", "石子堆场");
            structureTypes.put("8308", "砂子堆场");
            structureTypes.put("8309", "砌体堆场");
            structureTypes.put("8310", "装饰堆场");
            structureTypes.put("8311", "电气堆场");
            structureTypes.put("8312", "给排水堆场");
            structureTypes.put("8313", "临时堆场");
            structureTypes.put("8314", "刚结构堆场");
            structureTypes.put("8315", "大模板堆场");
            structureTypes.put("8316", "装配式堆场");
            structureTypes.put("8317", "废料堆场");
            structureTypes.put("8318", "立罐式水泥桶");
            structureTypes.put("8319", "预拌砂浆桶");
            structureTypes.put("8320", "堆场标记");

            structureTypes.put("8400", "安全防护");
            structureTypes.put("8401", "多排型钢立柱式防护棚");
            structureTypes.put("8402", "单排型钢立柱式防护棚");
            structureTypes.put("8403", "多排钢管立柱式防护棚");
            structureTypes.put("8404", "笼式防护棚");
            structureTypes.put("8405", "混凝土搅拌棚");
            structureTypes.put("8406", "钢管悬挑卸料平台");
            structureTypes.put("8407", "工具是悬挑卸料平台");
            structureTypes.put("8408", "钢管式");
            structureTypes.put("8409", "网片式");
            structureTypes.put("8410", "栅栏式");
            structureTypes.put("8411", "移动式围栏");
            structureTypes.put("8412", "铁马围栏");
            structureTypes.put("8413", "水平安全防护");
            structureTypes.put("8414", "双跑施工斜道");
            structureTypes.put("8415", "单跑施工斜道");
            structureTypes.put("8416", "爬梯式斜道");
            structureTypes.put("8417", "安全通道");
            structureTypes.put("8418", "安全警示牌");
            structureTypes.put("8419", "安全警示灯");
            structureTypes.put("8420", "钢管式");
            structureTypes.put("8421", "网片式");
            structureTypes.put("8422", "栅栏式");
            structureTypes.put("8423", "爬升式外架");
            structureTypes.put("8424", "满堂脚手架");
            structureTypes.put("8425", "爬模");
            structureTypes.put("8426", "爬升式外架");
            structureTypes.put("8427", "线性安全通道");


            structureTypes.put("8500", "绿色文明施工构件");
            structureTypes.put("8501", "九排一图");
            structureTypes.put("8502", "安全讲评台");
            structureTypes.put("8503", "闸机");
            structureTypes.put("8504", "横幅");
            structureTypes.put("8505", "条幅");
            structureTypes.put("8506", "材料标识牌");
            structureTypes.put("8507", "标牌");
            structureTypes.put("8508", "路牌指示牌");
            structureTypes.put("8509", "平顶简易宣传栏");
            structureTypes.put("8510", "拱顶简易宣传栏");
            structureTypes.put("8511", "可移动圆柱带棚宣传窗");
            structureTypes.put("8512", "方管立柱带棚宣传窗");
            structureTypes.put("8513", "固定圆柱带棚宣传窗");
            structureTypes.put("8514", "LED屏");
            structureTypes.put("8515", "彩旗");
            structureTypes.put("8516", "篮球场");
            structureTypes.put("8517", "篮球场半场");
            structureTypes.put("8518", "花坛");
            structureTypes.put("8519", "草坪");
            structureTypes.put("8520", "树");
            structureTypes.put("8521", "灌木");
            structureTypes.put("8522", "花");
            structureTypes.put("8523", "盆栽");
            structureTypes.put("8524", "水域");
            structureTypes.put("8525", "样板展示区");
            structureTypes.put("8526", "洒水车");
            structureTypes.put("8527", "雾炮");
            structureTypes.put("8528", "雨水收集池");
            structureTypes.put("8529", "台球桌");
            structureTypes.put("8530", "乒乓球桌");
            structureTypes.put("8531", "单人漫步机");
            structureTypes.put("8532", "单人坐拉训练器");
            structureTypes.put("8533", "单柱椭圆机");
            structureTypes.put("8534", "双人大转轮");
            structureTypes.put("8535", "双人坐蹬训练器");
            structureTypes.put("8536", "坐立扭腰器");
            structureTypes.put("8537", "肩关节康复器");
            structureTypes.put("8538", "立式腰背按摩器");
            structureTypes.put("8539", "伸背肩关节组合");
            structureTypes.put("8540", "肋木云梯");
            structureTypes.put("8541", "车辆转弯反光镜");

            structureTypes.put("8600", "消防安全构件");
            structureTypes.put("8601", "消防灭火器箱");
            structureTypes.put("8602", "消防器材柜");
            structureTypes.put("8603", "消防器材架");
            structureTypes.put("8604", "消防水池");
            structureTypes.put("8605", "消防砂箱");
            structureTypes.put("8606", "消火栓箱");
            structureTypes.put("8607", "消防栓");
            structureTypes.put("8608", "灭火器");
            structureTypes.put("8609", "消防铲");
            structureTypes.put("8610", "消防桶");

            structureTypes.put("8700", "临时用水、临时用电系统");
            structureTypes.put("8701", "变压器");
            structureTypes.put("8702", "总配电箱");
            structureTypes.put("8703", "分配电箱");
            structureTypes.put("8704", "开关箱");
            structureTypes.put("8705", "电缆电线");
            structureTypes.put("8706", "电缆电线立管");
            structureTypes.put("8707", "电线杆");
            structureTypes.put("8708", "总配电室");
            structureTypes.put("8709", "横担");
            structureTypes.put("8710", "外电防护架");
            structureTypes.put("8711", "灯塔/灯架");
            structureTypes.put("8712", "双悬臂路灯");
            structureTypes.put("8713", "单悬臂路灯");
            structureTypes.put("8714", "太阳能路灯");

            structureTypes.put("8800", "机械设备");
            structureTypes.put("8801", "平顶塔吊");
            structureTypes.put("8802", "尖顶塔吊");
            structureTypes.put("8803", "动壁塔吊");
            structureTypes.put("8804", "施工电梯");
            structureTypes.put("8805", "井架");
            structureTypes.put("8806", "吊篮");
            structureTypes.put("8807", "轿车");
            structureTypes.put("8808", "货车");
            structureTypes.put("8809", "吊车");
            structureTypes.put("8810", "叉车");
            structureTypes.put("8811", "铲车");
            structureTypes.put("8812", "平板汽车");
            structureTypes.put("8813", "翻斗车");
            structureTypes.put("8814", "履带吊");
            structureTypes.put("8815", "挖机");
            structureTypes.put("8816", "压路机");
            structureTypes.put("8817", "推土机");
            structureTypes.put("8818", "平地机");
            structureTypes.put("8819", "渣土运输车");
            structureTypes.put("8820", "闪光对焊机");
            structureTypes.put("8821", "钢筋切断机");
            structureTypes.put("8822", "钢筋调直机");
            structureTypes.put("8823", "钢筋弯曲机");
            structureTypes.put("8824", "钢筋直螺纹滚丝机");
            structureTypes.put("8825", "操作台");
            structureTypes.put("8826", "混凝土运输车");
            structureTypes.put("8827", "混凝土泵车");
            structureTypes.put("8828", "混凝土搅拌机");
            structureTypes.put("8829", "混凝土地泵");
            structureTypes.put("8830", "混凝土振捣棒");
            structureTypes.put("8831", "泵管");
            structureTypes.put("8832", "泵管立管");
            structureTypes.put("8833", "平刨");
            structureTypes.put("8834", "圆盘锯");
            structureTypes.put("8835", "推刨");
            structureTypes.put("8836", "电焊机");
            structureTypes.put("8837", "氧气推车");
            structureTypes.put("8838", "弯管器");
            structureTypes.put("8839", "乙炔推车");
            structureTypes.put("8840", "砂轮切割机");
            structureTypes.put("8841", "混凝土布料机");
            structureTypes.put("8842", "龙门吊");

            structureTypes.put("8900", "安全体验区");
            structureTypes.put("8901", "平衡木体验");
            structureTypes.put("8902", "安全带体验");
            structureTypes.put("8903", "安全鞋冲击体验");
            structureTypes.put("8904", "洞口坠落体验");
            structureTypes.put("8905", "综合用电体验");
            structureTypes.put("8906", "灭火器演示体验");
            structureTypes.put("8907", "物料堆放及大模板倾倒体验");
            structureTypes.put("8908", "安全帽冲击体验");
            structureTypes.put("8909", "操作平台体验");
            structureTypes.put("8910", "塔吊超高限重体验");
            structureTypes.put("8911", "急救体验");
            structureTypes.put("8912", "防护栏杆推倒体验");
            structureTypes.put("8913", "垂直爬梯体验");
            structureTypes.put("8914", "重物搬运体验");
            structureTypes.put("8915", "钢丝绳安全体验");
            structureTypes.put("8916", "马道体验");

            structureTypes.put("9100", "基础构件");
            structureTypes.put("9101", "文字");
            structureTypes.put("9102", "箭头");
            structureTypes.put("9103", "线条");
            structureTypes.put("9104", "指北针");
            structureTypes.put("9105", "水准点");
            structureTypes.put("9106", "长方体");
            structureTypes.put("9107", "圆柱");
            structureTypes.put("9108", "球体");
            structureTypes.put("9109", "圆锥");
            structureTypes.put("9110", "棱锥");
            structureTypes.put("9111", "圆台");
            structureTypes.put("9112", "棱台");

            structureTypes.put("9200", "其他");
            structureTypes.put("9001", "obj导入");
            structureTypes.put("9002", "组合构件");
            structureTypes.put("9003", "机械运动轨迹");
            structureTypes.put("9004", "路径漫游轨迹");
            structureTypes.put("9005", "自定义截面线性");
            structureTypes.put("9006", "自定义截面柱型");
            structureTypes.put("9007", "云构件");
            structureTypes.put("9008", "本地族");
            structureTypes.put("9009", "文件导入");


            /***** 安装算量模块系列 *****/
            ///电气
            structureTypes.put("10000", "电气");
            structureTypes.put("10101", "设备及电器");
            structureTypes.put("10102", "灯具");



            structureTypes.put("10201", "接线盒");
            structureTypes.put("10301", "配电箱、柜");
            structureTypes.put("10401", "电缆");
            structureTypes.put("10501", "电线");
            structureTypes.put("10601", "配管");
            structureTypes.put("10701", "电线*配管");
            structureTypes.put("10801", "电线*配管");
            structureTypes.put("10901", "母线");
            structureTypes.put("11001", "桥架");
            structureTypes.put("11101", "防雷接地零件");
            structureTypes.put("11201", "防雷接地线");
            structureTypes.put("11301", "配管支吊架");
            structureTypes.put("11401", "桥架支吊架");
            structureTypes.put("11501", "管道套管");
            structureTypes.put("11601", "桥架管件");
            structureTypes.put("11701", "灯带");
            structureTypes.put("11990", "表格算量");
            /// 给排水
            structureTypes.put("12000", "给排水");
            structureTypes.put("12101", "水管");
            structureTypes.put("12201", "设备");
            structureTypes.put("12301", "管件");
            structureTypes.put("12401", "配件");
            structureTypes.put("12501", "卫生器具");
            structureTypes.put("12601", "水管支吊架");
            structureTypes.put("12701", "管道套管");
            structureTypes.put("13999", "相交点");
            /// 消防水
            structureTypes.put("14000", "消防水");
            structureTypes.put("14101", "水管");
            structureTypes.put("14201", "喷头");
            structureTypes.put("14301", "设备");
            structureTypes.put("14401", "管件");
            structureTypes.put("14501", "配件");
            structureTypes.put("14601", "水管支吊架");
            structureTypes.put("14701", "管道套管");
            structureTypes.put("14801", "挡烟垂壁");
            structureTypes.put("15998", "相交点");
            structureTypes.put("15999", "入水口");
            /// 暖通
            structureTypes.put("16000", "暖通");
            structureTypes.put("16101", "风管");
            structureTypes.put("16201", "风管管件");
            structureTypes.put("16301", "风阀");
            structureTypes.put("16401", "风帽");
            structureTypes.put("16501", "风口/检查孔");
            structureTypes.put("16601", "弯头导流片");
            structureTypes.put("16701", "风管支吊架");
            structureTypes.put("16801", "风管套管");
            structureTypes.put("16901", "暖通水管");
            structureTypes.put("17001", "水管管件");
            structureTypes.put("17101", "水管配件");
            structureTypes.put("17201", "水管支吊架");
            structureTypes.put("17301", "水管套管");
            structureTypes.put("17401", "设备及其他");
            structureTypes.put("17501", "帆布接口");
            /// 智能化
            structureTypes.put("18000", "智能化");
            structureTypes.put("18101", "设备及电器");
            structureTypes.put("18201", "接线盒");
            structureTypes.put("18301", "配电箱、柜");
            structureTypes.put("18401", "弱电线");
            structureTypes.put("18501", "配管");
            structureTypes.put("18601", "弱电线*配管");
            structureTypes.put("18701", "母线");
            structureTypes.put("18801", "桥架");
            structureTypes.put("18901", "配管支吊架");
            structureTypes.put("19001", "桥架支吊架");
            structureTypes.put("19101", "管道套管");
            structureTypes.put("19201", "桥架管件");
            structureTypes.put("19401", "电缆*配管");
            ///消防电
            structureTypes.put("20000", "消防电");
            structureTypes.put("20101", "设备及电器");
            structureTypes.put("20201", "接线盒");
            structureTypes.put("20301", "配电箱、柜");
            structureTypes.put("20401", "消防线");
            structureTypes.put("20501", "配管");
            structureTypes.put("20601", "消防线*配管");
            structureTypes.put("20701", "桥架");
            structureTypes.put("20801", "配管支吊架");
            structureTypes.put("20901", "桥架支吊架");
            structureTypes.put("21001", "管道套管");
            structureTypes.put("21101", "桥架管件");
            structureTypes.put("21201", "电缆");
            structureTypes.put("21301", "电缆*配管");
            ////零星项目
            structureTypes.put("22000", "零星项目");
            structureTypes.put("22101", "电缆沟槽");
            structureTypes.put("22201", "综合支吊架");
            structureTypes.put("23101", "零星 点");
            structureTypes.put("23201", "零星 线");
            structureTypes.put("23301", "零星 面");
        }
        return structureTypes;
    }

    public static void setStructureTypes(HashMap<String, String> structureTypes) {
        StructureTypeData.structureTypes = structureTypes;
    }
}
