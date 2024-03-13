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
public class ReportDto {
    
    private String area;

    private Integer tzs;

    private BigDecimal tz;

    private Integer zjs;

    private BigDecimal zj;

    private Integer wkjs;

    private BigDecimal wkj;

    private BigDecimal sr;

    private BigDecimal zc;

    private BigDecimal yl;

    List<ReportDto> reportDtos;

}
