package com.app.medel;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@Data
public class OrderDto {
    
    private Integer id;

    private String orderTime;

    private String account;

    private String name;

    private String userName;

    private String code;

    private String date;

    private BigDecimal price;

    private BigDecimal total;

    List<OrderDetil> list;

    List<OrderDetil> list2;

}
