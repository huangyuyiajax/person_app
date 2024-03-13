package com.app.service;

import com.app.medel.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: TODO
 * @author huangyuyi
 * @date 2024/1/5 19:23
 * @version 1.0
 */
@SuppressWarnings("ALL")
public interface PayService {

    /**
     * @Author huangyuyi
     * @Description 
     * @Date 19:27 2024/1/5
     * @Param 
     * @return 
     **/
    Map<String, String> unifiedorder(HttpServletRequest request, BigDecimal amount, String openid, User user)throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 19:27 2024/1/5
     * @Param
     * @return
     **/
    void notify(HttpServletRequest request, HttpServletResponse response);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 19:27 2024/1/5
     * @Param
     * @return
     **/
    void closeorder();
    /**
     * @Author huangyuyi
     * @Description
     * @Date 19:27 2024/1/5
     * @Param
     * @return
     **/
    void transfers(HttpServletRequest request,BigDecimal amount,String openid,User user)throws Exception;
}
