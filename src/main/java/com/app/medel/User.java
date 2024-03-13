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
public class User {
    
    private Integer id;

    private String account;

    private String password;
    
    private String name;
    
    private String sex;
    
    private String idCard;
    
    private String picture;

    private BigDecimal cash;
    
}
