package org.example.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ProvinceCityLatLonData {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2026, 11, 26, 15, 16, 42);
    private static final String CREATE_BY = "system";
    private static final int STATUS_VALID = 1;

    public static final List<ProvinceCityLatLon> LIST = Arrays.asList(
            // ========== 甘肃省（14） ==========
            new ProvinceCityLatLon(1,  "兰州市",     36.061089, 103.834303, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(2,  "嘉峪关市",   39.773130,  98.289152, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(3,  "金昌市",     38.520089, 102.187888, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(4,  "白银市",     36.544700, 104.138488, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(5,  "天水市",     34.580889, 105.724988, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(6,  "武威市",     37.928289, 102.634688, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(7,  "张掖市",     38.925689, 100.449688, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(8,  "平凉市",     35.542789, 106.665288, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(9,  "酒泉市",     39.732089,  98.494088, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(10, "庆阳市",     35.708989, 107.643588, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(11, "定西市",     35.580089, 104.626288, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(12, "陇南市",     33.400689, 104.921688, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(13, "临夏回族自治州", 35.601189, 103.210088, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(14, "甘南藏族自治州", 34.983389, 102.911088, "甘肃省", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 内蒙古自治区（12） ==========
            new ProvinceCityLatLon(15, "呼和浩特市",   40.842389, 111.749188, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(16, "包头市",       40.657089, 109.840088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(17, "乌海市",       39.653289, 106.794288, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(18, "赤峰市",       42.260089, 118.956088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(19, "通辽市",       43.652089, 122.243088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(20, "鄂尔多斯市",   39.608189, 109.781088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(21, "呼伦贝尔市",   49.212089, 119.765088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(22, "巴彦淖尔市",   40.743089, 107.387088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(23, "乌兰察布市",   40.993089, 113.132088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(24, "兴安盟",       46.083089, 122.037088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(25, "锡林郭勒盟",   43.933089, 116.086088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(26, "阿拉善盟",     38.833089, 105.729088, "内蒙古自治区", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 天津市（1） ==========
            new ProvinceCityLatLon(27, "天津市",       39.125589, 117.190188, "天津市", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 河北省（11） ==========
            new ProvinceCityLatLon(28, "石家庄市",     38.042789, 114.514888, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(29, "唐山市",       39.630089, 118.180088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(30, "秦皇岛市",     39.935089, 119.600088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(31, "邯郸市",       36.625089, 114.539088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(32, "邢台市",       37.070089, 114.504088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(33, "保定市",       38.873089, 115.464088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(34, "张家口市",     40.767089, 114.886088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(35, "承德市",       40.954089, 117.963088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(36, "沧州市",       38.303089, 116.838088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(37, "廊坊市",       39.518089, 116.683088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(38, "衡水市",       37.734089, 115.670088, "河北省", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 山西省（11） ==========
            new ProvinceCityLatLon(39, "太原市",       37.870089, 112.548088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(40, "大同市",       40.076089, 113.300088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(41, "阳泉市",       37.856089, 113.580088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(42, "长治市",       36.195089, 113.116088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(43, "晋城市",       35.490089, 112.851088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(44, "朔州市",       39.331089, 112.432088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(45, "晋中市",       37.687089, 112.752088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(46, "运城市",       35.026089, 111.006088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(47, "忻州市",       38.416089, 112.733088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(48, "临汾市",       36.088089, 111.518088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(49, "吕梁市",       37.518089, 111.144088, "山西省", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 山东省（16） ==========
            new ProvinceCityLatLon(50, "济南市",       36.651089, 116.995088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(51, "青岛市",       36.067089, 120.382088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(52, "淄博市",       36.813089, 118.054088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(53, "枣庄市",       34.810089, 117.323088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(54, "东营市",       37.434089, 118.674088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(55, "烟台市",       37.463089, 121.447088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(56, "潍坊市",       36.706089, 119.161088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(57, "济宁市",       35.414089, 116.587088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(58, "泰安市",       36.195089, 117.088088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(59, "威海市",       37.513089, 122.120088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(60, "日照市",       35.416089, 119.526088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(61, "临沂市",       35.104089, 118.356088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(62, "德州市",       37.434089, 116.357088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(63, "聊城市",       36.456089, 115.985088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(64, "滨州市",       37.382089, 117.970088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(65, "菏泽市",       35.233089, 115.480088, "山东省", STATUS_VALID, CREATE_TIME, CREATE_BY),

            // ========== 浙江省（11） ==========
            new ProvinceCityLatLon(66, "杭州市",       30.274089, 120.155088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(67, "宁波市",       29.868089, 121.549088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(68, "温州市",       27.993089, 120.699088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(69, "嘉兴市",       30.746089, 120.755088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(70, "湖州市",       30.872089, 120.093088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(71, "绍兴市",       30.030089, 120.580088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(72, "金华市",       29.078089, 119.647088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(73, "衢州市",       28.935089, 118.871088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(74, "舟山市",       30.036089, 122.106088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(75, "台州市",       28.656089, 121.420088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY),
            new ProvinceCityLatLon(76, "丽水市",       28.467089, 119.922088, "浙江省", STATUS_VALID, CREATE_TIME, CREATE_BY)
    );
}