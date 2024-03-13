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
public class Lottery {
    
    private Integer id;

    private Integer area;

    private String name;

    private String code;

    private String date;

    private BigDecimal odds;

    List<Order> list;

}
