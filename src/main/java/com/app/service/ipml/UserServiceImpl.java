package com.app.service.ipml;

import com.alibaba.fastjson.JSONObject;
import com.app.dao.UserDao;
import com.app.medel.*;
import com.app.service.UserService;
import com.app.util.ConfigUtils;
import com.app.util.DateUtils;
import com.app.util.HttpsUtil;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author huangyuyi
 */
@Service("userServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String account, String password) throws Exception{
        if(StringUtils.isEmpty(account)){
            throw new Exception("请输入账号");
        }
        if(StringUtils.isEmpty(password)){
            throw new Exception("请输入密码");
        }
        User result = userDao.selectUser(account);
        if (result == null) {
            User user = new User();
            user.setAccount(account);
            user.setPassword(password);
            userDao.insertUser(user);
            return user;
        }
        if (!result.getPassword().equals(password)) {
            throw new Exception("密码错误");
        }
        return result;
    }

    @Override
    public User selectUserById(Integer id){
        return userDao.selectUserById(id);
    }

    @Override
    public User updateUser(User user) throws Exception{
        if(StringUtils.isEmpty(user.getPassword())){
            throw new Exception("密码不能为空");
        }
        Integer cout = userDao.updateUser(user);
        if (cout<0) {
            throw new Exception("保存失败");
        }
        return userDao.selectUserById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public void saveOrder(Order order, HttpSession session) throws Exception{
        Integer area = (Integer)session.getAttribute("area");
        if(order==null||order.getList().size()<=0){
            throw new Exception("没有号码和金额");
        }
        Lottery lottery = userDao.selectLottery(order.getLotteryId());
        if(StringUtils.isNotEmpty(lottery.getCode())){
            throw new Exception("已经开码，不能下单");
        }
        Integer minutes = 15;
        Setup nowSetup = userDao.findNowSetup(area);
        if(nowSetup!=null){
            minutes = nowSetup.getMinutes();
        }
        String date = lottery.getDate();
        if(StringUtils.isNotEmpty(date)){
            if(DateUtils.compareStrDate(date+ ConfigUtils.OPEN_CODE)){
                throw new Exception("已过开码时间，不能下单");
            }
            if(DateUtils.compareStrDate(DateUtils.timeProcess(date+ConfigUtils.OPEN_CODE,-minutes))){
                throw new Exception("开码前"+minutes+"分钟内，不能下单");
            }
        }
        User user = (User)session.getAttribute("user");
        user = userDao.selectUserById(user.getId());
        if(user==null){
            throw new Exception("没有会员信息");
        }
        BigDecimal totalPrice = new BigDecimal(0);
        List<OrderDetil> orderDetils = order.getList();
        for(OrderDetil e:orderDetils){
            if(StringUtils.isEmpty(e.getCode())){
                throw new Exception("下单号码有误：为空");
            }
            if(Integer.valueOf(e.getCode())>ConfigUtils.MAX_CODE||Integer.valueOf(e.getCode())<=0){
                throw new Exception("下单号码有误："+e.getCode());
            }
            if(e.getPrice().compareTo(BigDecimal.ZERO)<=0){
                throw new Exception("下单金额有误："+e.getPrice());
            }
            if(e.getCode().length()==1){
                e.setCode("0"+e.getCode());
            }
            totalPrice = totalPrice.add(e.getPrice());
        }
        if(user.getCash().compareTo(totalPrice)<0){
            throw new Exception("当前余额"+user.getCash()+"元，余额不足，不能下单");
        }
        order.setUserId(user.getId());
        Integer count = userDao.insertOrder(order);
        if(count>0){
            Bill bill = new Bill();
            bill.setUserId(user.getId());
            bill.setOrderId(order.getId());
            bill.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
            bill.setBala(user.getCash());
            bill.setType("2");
            BigDecimal total = new BigDecimal(0);
            List<OrderDetil> list = order.getList();
            for(OrderDetil e:list){
                e.setOrderId(order.getId());
                total = total.add(e.getPrice());
            }
            bill.setPrice(total);
            userDao.insertOrderDetilBatch(order.getList());
            user.setCash(user.getCash().subtract(total));
            bill.setCash(user.getCash());
            userDao.updateUserCash(user);
            userDao.insertBill(bill);
            session.setAttribute("user", user);
        }else {
            throw new Exception("下单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public void delOrder(Integer orderId, HttpSession session) throws Exception{
        Integer area = (Integer)session.getAttribute("area");
        User user = (User)session.getAttribute("user");
        user = userDao.selectUserById(user.getId());
        if(user==null){
            throw new Exception("没有会员信息");
        }
        if(orderId==null){
            throw new Exception("没有单子");
        }
        Order order = userDao.selectOrderById(orderId,user.getId());
        if(order==null){
            throw new Exception("没有单子");
        }
        Lottery lottery = userDao.selectLottery(order.getLotteryId());
        if(StringUtils.isNotEmpty(lottery.getCode())){
            throw new Exception("已经开码，不能退单");
        }
        Integer minutes = 15;
        Setup nowSetup = userDao.findNowSetup(area);
        if(nowSetup!=null){
            minutes = nowSetup.getMinutes();
        }
        String date = lottery.getDate();
        if(StringUtils.isNotEmpty(date)){
            if(DateUtils.compareStrDate(date+ConfigUtils.OPEN_CODE)){
                throw new Exception("已过开码时间，不能退单");
            }
            if(DateUtils.compareStrDate(DateUtils.timeProcess(date+ConfigUtils.OPEN_CODE,-minutes))){
                throw new Exception("开码前"+minutes+"分钟内，不能退单");
            }
        }
        Integer count = userDao.deleteOrder(orderId);
        if(count>0){
            Bill bill = new Bill();
            bill.setUserId(user.getId());
            bill.setOrderId(order.getId());
            bill.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
            bill.setBala(user.getCash());
            bill.setType("3");
            BigDecimal total = new BigDecimal(0);
            List<OrderDetil> list = userDao.selectOrderDetilByOrderId(orderId);
            for(OrderDetil e:list){
                total = total.add(e.getPrice());
            }
            bill.setPrice(total);
            userDao.deleteOrderDetil(orderId);
            user.setCash(user.getCash().add(total));
            bill.setCash(user.getCash());
            userDao.updateUserCash(user);
            userDao.insertBill(bill);
            session.setAttribute("user", user);
        }else {
            throw new Exception("退单失败");
        }
    }

    @Override
    public List<OrderDto> findOrder(Integer pageNumber,Integer pageSize,HttpSession session) {
        User user = (User)session.getAttribute("user");
        Integer area = (Integer)session.getAttribute("area");
        Integer currIndex = (pageNumber-1)*pageSize;
        List<OrderDto> list = userDao.selectOrderByUserId(user.getId(),area,currIndex,pageSize);
        list.forEach(e->{
            BigDecimal total = new BigDecimal(0);
            List<OrderDetil> orderDetils = userDao.selectOrderDetilByOrderId(e.getId());
            for(OrderDetil orderDetil:orderDetils){
                total = total.add(orderDetil.getPrice());
            }
            e.setTotal(total);
            e.setList(orderDetils);
            e.setList2(userDao.selectOrderDetilByOrderId2(e.getId()));
        });
        return list;
    }
    @Override
    public HashMap<String,Object> findNowLottery(Integer area){
        HashMap<String,Object> res = new HashMap<>(2);
        Setup setup = userDao.findNowSetup(area);
        Lottery lottery = userDao.findNowLottery(area);
        if(lottery!=null&&StringUtils.isNumeric(lottery.getName())){
            res.put("nextLotteryName",Integer.valueOf(lottery.getName())+1);
        }else {
            Lottery maxLottery = userDao.findMaxLotteryName(area);
            if(maxLottery!=null&&StringUtils.isNumeric(maxLottery.getName())){
                res.put("nextLotteryName",Integer.valueOf(maxLottery.getName())+1);
            }
        }
        res.put("setup",setup);
        res.put("lottery",lottery);
        return res;
    }

    @Override
    public  List<Bill> selectBillList(Integer pageNumber,Integer pageSize,Integer userId){
        Integer currIndex = (pageNumber-1)*pageSize;
        return userDao.selectBillList(userId,currIndex,pageSize);
    }

    @Override
    public void saveLottery(Lottery lottery) throws Exception{
        if(StringUtils.isEmpty(lottery.getName())){
            throw new Exception("开码期数不能为空");
        }
        if(StringUtils.isEmpty(lottery.getDate())){
            throw new Exception("开码日期不能为空");
        }
        Lottery nowLottery = userDao.findNowLottery(lottery.getArea());
        Setup nowSetup = userDao.findNowSetup(lottery.getArea());
        if(nowSetup!=null){
            lottery.setOdds(nowSetup.getOdds());
        }
        if(nowLottery!=null){
            nowLottery.setCode(lottery.getCode());
            nowLottery.setName(lottery.getName());
            nowLottery.setDate(lottery.getDate());
            nowLottery.setOdds(lottery.getOdds());
            userDao.updateLottery(nowLottery);
        }else {
            userDao.insertLottery(lottery);
        }
    }

    @Override
    public void kaijiang(String code, String date, Integer area) throws Exception{
        if(StringUtils.isEmpty(code)){
            throw new Exception("开码号码不能为空");
        }
        if(!StringUtils.isNumeric(code)){
            throw new Exception("开码号码应该为01到49");
        }
        if(Integer.valueOf(code)<=0||Integer.valueOf(code)>ConfigUtils.MAX_CODE){
            throw new Exception("开码号码应该为01到49");
        }
        if(StringUtils.isEmpty(date)){
            throw new Exception("开码日期不能为空");
        }
        Lottery nowLottery = userDao.findNowLottery(area);
        if(nowLottery==null) {
            throw new Exception("当前没有进行排期");
        }
        nowLottery.setDate(date);
        nowLottery.setCode(code);
        Setup nowSetup = userDao.findNowSetup(nowLottery.getArea());
        if(nowSetup!=null){
            nowLottery.setOdds(nowSetup.getOdds());
        }
        Integer count = userDao.updateLottery(nowLottery);
        if(count>0){
            List<Bill> bills = userDao.findLotteryIdBillList(code,nowLottery.getId());
            for(Bill bill:bills){
                User user = userDao.selectUserById(bill.getUserId());
                if(user!=null){
                    bill.setBala(user.getCash());
                    user.setCash(user.getCash().add(bill.getPrice()));
                    bill.setCash(user.getCash());
                    bill.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
                    userDao.insertBill(bill);
                    userDao.updateUserCash(user);
                }
            }
        }
    }

    @Override
    public List<LotteryDto> loadRecordDetilList(Integer area,Integer pageNumber,Integer pageSize){
        Integer currIndex = (pageNumber-1)*pageSize;
        List<LotteryDto> list = userDao.selectAllLotteryDtoList(area,currIndex,pageSize);
        list.forEach(e-> e.setList(userDao.selectOrderDtosByLotteryId(e.getId())));
        return list;
    }

    @Override
    public ReportDto loadRecordDetilRepList(){
        ReportDto rep = userDao.loadRecordDetilRepListSum();
        if(rep!=null){
            rep.setReportDtos(userDao.loadRecordDetilRepList());
        }
        return rep;
    }

    @Override
    public List<OrderDto> findRecordAllList(Integer area,Integer pageNumber,Integer pageSize){
        Integer currIndex = (pageNumber-1)*pageSize;
        List<OrderDto> list = userDao.findRecordAllList(area,currIndex,pageSize);
        list.forEach(e->{
            e.setList(userDao.selectOrderDetilByOrderId(e.getId()));
            e.setList2(userDao.selectOrderDetilByOrderId2(e.getId()));
        });
        return list;
    }

    @Override
    public List<User> findUserAllList(Integer pageNumber,Integer pageSize){
        Integer currIndex = (pageNumber-1)*pageSize;
        return userDao.findUserAllList(currIndex,pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public void recharge(Integer id, BigDecimal amount, HttpSession session) throws Exception{
        if(id==null){
            throw new Exception("没有会员信息");
        }
        if(amount==null||amount.compareTo(BigDecimal.ZERO)==0){
            throw new Exception("请输入充值金额");
        }
        User user = userDao.selectUserById(id);
        if(user==null){
            throw new Exception("没有会员信息");
        }
        Bill bill = new Bill();
        bill.setUserId(user.getId());
        bill.setBala(user.getCash());
        if(amount.compareTo(BigDecimal.ZERO)<0){
            bill.setType("5");
            bill.setPrice(amount.negate());
        }else {
            bill.setType("1");
            bill.setPrice(amount);
        }
        bill.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setCash(user.getCash().add(amount));
        bill.setCash(user.getCash());
        userDao.updateUserCash(user);
        userDao.insertBill(bill);
    }

    @Override
    public HashMap<String,Object> findCalculationList(Integer area) throws Exception{
        HashMap<String,Object> res = new HashMap<>(3);
        List<LotteryDto> list = userDao.findCalculationList(area);
        BigDecimal total = new BigDecimal(0);
        Integer number = 0;
        for(LotteryDto lotteryDto:list){
            total = total.add(lotteryDto.getTotal());
            number += lotteryDto.getNumber();
        }
        res.put("number",number);
        res.put("total",total);
        res.put("list",list);
        return res;
    }


    @Override
    public HashMap<String,Object> findAllCalculationList(Integer area) throws Exception{
        HashMap<String,Object> res = new HashMap<>(3);
        List<LotteryDto> list = userDao.findAllCalculationList(area);
        BigDecimal total = new BigDecimal(0);
        Integer number = 0;
        for(LotteryDto lotteryDto:list){
            total = total.add(lotteryDto.getTotal());
            number += lotteryDto.getNumber();
        }
        res.put("number",number);
        res.put("total",total);
        res.put("list",list);
        return res;
    }

    @Override
    public UserGather findAllPersonCalculationList(Integer area) throws Exception{
        UserGather userGather = userDao.findAllPersonCalculationListSum(area);
        if(userGather!=null){
            userGather.setList(userDao.findAllPersonCalculationList(area));
        }
        return userGather;
    }

    @Override
    public void taskKaiMa(Integer area,String url) {
        Lottery nowLottery = userDao.findNowLottery(area);
        if(nowLottery==null) {
            return;
        }
        JSONObject resJon = HttpsUtil.httpGet(url);
        if(resJon==null){
            return;
        }
        //2023042,20,28,27,23,46,30,29,2023043,04,20,四,21点30分
        String k = resJon.getString("k");
        if(StringUtils.isEmpty(k)){
            return;
        }
        String nowName = Calendar.getInstance().get(Calendar.YEAR)+k.split(",")[0];
        if(!nowName.equals(nowLottery.getName().trim())){
            return;
        }
        Lottery lottery = new Lottery();
        lottery.setArea(area);
        String code = k.split(",")[7];
        //不是数字
        if(!StringUtils.isNumeric(code)){
            return;
        }
        lottery.setName(k.split(",")[8]);
        lottery.setDate(k.split(",")[8].substring(0,4)+"-"+k.split(",")[9]+"-"+k.split(",")[10]);
        if(StringUtils.isEmpty(nowLottery.getDate())){
            nowLottery.setDate(DateUtils.getCurrentTimeAs10String());
        }
        nowLottery.setName(nowName);
        nowLottery.setCode(code);
        Setup nowSetup = userDao.findNowSetup(nowLottery.getArea());
        if(nowSetup!=null){
            nowLottery.setOdds(nowSetup.getOdds());
        }
        Integer count = userDao.updateLottery(nowLottery);
        if(count>0){
            List<Bill> bills = userDao.findLotteryIdBillList(code,nowLottery.getId());
            for(Bill bill:bills){
                User user = userDao.selectUserById(bill.getUserId());
                if(user!=null){
                    bill.setBala(user.getCash());
                    user.setCash(user.getCash().add(bill.getPrice()));
                    bill.setCash(user.getCash());
                    bill.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
                    userDao.insertBill(bill);
                    userDao.updateUserCash(user);
                }
            }
        }
        Lottery nowLottery2 = userDao.findNowLottery(area);
        if(nowSetup!=null){
            lottery.setOdds(nowSetup.getOdds());
        }
        if(nowLottery2!=null){
            nowLottery2.setName(lottery.getName());
            nowLottery2.setDate(lottery.getDate());
            nowLottery2.setOdds(lottery.getOdds());
            userDao.updateLottery(nowLottery2);
        }else {
            userDao.insertLottery(lottery);
        }
    }

    @Override
    public  UserGather selectBillListReport(Integer userId){
        return userDao.selectBillListReport(userId);
    }

    @Override
    public Setup findNowSetup(Integer area){
        return userDao.findNowSetup(area);
    }

    @Override
    public void saveSetup(Setup setup) throws Exception{
        if(setup.getOdds()==null){
            throw new Exception("赔率不能为空");
        }
        if(setup.getMinutes()==null){
            throw new Exception("买码停止前分钟数不能为空");
        }
        Setup nowSetup = userDao.findNowSetup(setup.getArea());
        if(nowSetup!=null){
            nowSetup.setOdds(setup.getOdds());
            nowSetup.setMinutes(setup.getMinutes());
            userDao.updateSetup(nowSetup);
        }else {
            userDao.insertSetup(setup);
        }
        Lottery nowLottery = userDao.findNowLottery(setup.getArea());
        if(nowLottery!=null) {
            nowLottery.setOdds(setup.getOdds());
            userDao.updateLottery(nowLottery);
        }
    }

}
