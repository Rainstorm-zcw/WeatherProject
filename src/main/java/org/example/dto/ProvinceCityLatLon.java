package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProvinceCityLatLon {
    private Integer id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String province;
    private Integer status;      // 1-有效
    private LocalDateTime createTime;
    private String createBy;

    public ProvinceCityLatLon(Integer id, String name, Double latitude, Double longitude,
                              String province, Integer status, LocalDateTime createTime, String createBy) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    // getter / setter 省略，按需生成或改用 Lombok @Data
}