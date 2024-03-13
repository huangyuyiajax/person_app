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
public class UserGather {
    
    private Integer id;

    private String account;

    private String name;

    private BigDecimal cash;

    private BigDecimal cz;

    private BigDecimal xd;

    private BigDecimal td;

    private BigDecimal zj;
    
    private BigDecimal tx;

    private BigDecimal zc;

    private BigDecimal sr;

    private BigDecimal yl;

    List<UserGather> list;
}
