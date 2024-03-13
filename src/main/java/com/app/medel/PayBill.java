package com.app.medel;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@Data
public class PayBill {
    
    private Integer id;

    private Integer userId;

    private String outTradeNo;

    private String type;
    
    private BigDecimal price;
    
    private String date;


}
