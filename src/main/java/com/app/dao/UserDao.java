package com.app.dao;

import com.app.medel.*;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@Repository
public interface UserDao {

   /**
    * @Author huangyuyi
    * @Description 查询会员信息
    * @Date 10:46 2023/4/21
    * @Param 账号
    * @return
    **/
    User selectUser(String account);
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
    Integer insertUser(User user);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer updateUser(User user);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer updateUserCash(User user);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertOrder(Order order);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer deleteOrder(Integer id);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertOrderDetilBatch(List<OrderDetil> list);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer deleteOrderDetil(Integer orderId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Order selectOrderById(Integer id,Integer userId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDto> selectOrderByUserId(Integer userId,Integer area,Integer currIndex,Integer pageSize);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDetil> selectOrderDetilByOrderId(Integer orderId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDetil> selectOrderDetilByOrderId2(Integer orderId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertLottery(Lottery lottery);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Lottery selectLottery(Integer id);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Lottery findNowLottery(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Lottery findMaxLotteryName(Integer area);

    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertBill(Bill bill);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<Bill> selectBillList(Integer userId,Integer currIndex,Integer pageSize);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer updateLottery(Lottery lottery);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<LotteryDto> selectAllLotteryDtoList(Integer area,Integer currIndex,Integer pageSize);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<ReportDto> loadRecordDetilRepList();
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    ReportDto loadRecordDetilRepListSum();
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDto> selectOrderDtosByLotteryId(Integer lotteryId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<OrderDto> findRecordAllList(Integer area,Integer currIndex,Integer pageSize);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<User> findUserAllList(Integer currIndex,Integer pageSize);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<Bill> findLotteryIdBillList(String code,Integer lotteryId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<LotteryDto> findCalculationList(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<LotteryDto> findAllCalculationList(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    List<UserGather> findAllPersonCalculationList(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    UserGather findAllPersonCalculationListSum(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    UserGather selectBillListReport(Integer userId);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertSetup(Setup setup);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Setup findNowSetup(Integer area);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer updateSetup(Setup setup);

    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    Integer insertPayBill(PayBill payBill);
    /**
     * @Author huangyuyi
     * @Description
     * @Date 10:46 2023/4/21
     * @Param
     * @return
     **/
    PayBill selectPayBill(String outTradeNo);
   /**
    * @Author huangyuyi
    * @Description
    * @Date 10:46 2023/4/21
    * @Param
    * @return
    **/
   Integer updatePayBill(PayBill payBill);
   /**
    * @Author huangyuyi
    * @Description
    * @Date 10:46 2023/4/21
    * @Param
    * @return
    **/
   List<PayBill> findUnPayBillList();
}
