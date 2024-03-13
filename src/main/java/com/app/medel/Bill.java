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
public class Bill {
    
    private Integer id;

    private Integer userId;

    private Integer orderId;

    private Integer paybillId;

    private String outTradeNo;

    private String type;
    
    private BigDecimal bala;
    
    private BigDecimal price;
    
    private BigDecimal cash;

    private String date;

}
