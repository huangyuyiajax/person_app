package com.app.service;

import com.app.medel.*;
import com.app.medel.User;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@SuppressWarnings("ALL")
public interface UserService {
    /**
     * @Author huangyuyi
     * @Description 查询会员信息
     * @Date 10:46 2023/4/21
     * @Param 账号
     * @return
     **/
    User login(String account, String password)throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    User selectUserById(Integer id);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    User updateUser(User user)throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void saveOrder(Order order, HttpSession session) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void delOrder(Integer orderId, HttpSession session) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDto> findOrder(Integer pageNumber,Integer pageSize,HttpSession session) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    HashMap<String,Object> findNowLottery(Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<Bill> selectBillList(Integer pageNumber,Integer pageSize,Integer userId) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void saveLottery(Lottery lottery) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void kaijiang(String code,String date,Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<LotteryDto> loadRecordDetilList(Integer area,Integer pageNumber,Integer pageSize) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    ReportDto loadRecordDetilRepList() throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDto> findRecordAllList(Integer area,Integer pageNumber,Integer pageSize) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<User> findUserAllList(Integer pageNumber,Integer pageSize) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void recharge(Integer id, BigDecimal amount, HttpSession session) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    HashMap<String,Object> findCalculationList(Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    HashMap<String,Object> findAllCalculationList(Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    UserGather findAllPersonCalculationList(Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void taskKaiMa(Integer area,String url);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    UserGather selectBillListReport(Integer userId) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Setup findNowSetup(Integer area) throws Exception;
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    void saveSetup(Setup setup) throws Exception;
}
