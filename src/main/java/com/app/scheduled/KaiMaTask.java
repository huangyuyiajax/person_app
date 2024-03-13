package com.app.scheduled;

import com.app.service.PayService;
import com.app.service.UserService;
import com.app.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@Component
@EnableScheduling
@Slf4j
public class KaiMaTask {

    @Autowired
    private PayService payService;
    @Autowired
    private UserService userService;
    /**
     * @Author huangyuyi
     * @Description 每天晚上21:36、37点执行一次 0 36,37 21 * * ?
     * @Date 10:57 2023/4/21
     * @Param
     * @return
     **/
    @Scheduled(cron="0 36,37,38,39,40,45,50,55 21,22 * * ?")
    public void taskKaiMa(){
        log.info("1.开码定时器开始执行，开始时间："+ DateUtils.getCurrentTime());
        try {
            userService.taskKaiMa(0,"https://374445.com/kj/xg.js");
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            userService.taskKaiMa(1,"https://374445.com/kj/am.js");
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            userService.taskKaiMa(2,"https://374445.com/kj/tw.js");
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("1.开码定时器执行结束，结束时间："+DateUtils.getCurrentTime());
    }

    /**
     * @Author huangyuyi
     * @Description 一分钟 执行一次 关闭支付
     * @Date 10:57 2023/4/21
     * @Param
     * @return
     **/
    @Scheduled(cron="0 */1 * * * ?")
    public void closeorder(){
        log.info("1.关闭支付定时器开始执行，开始时间："+ DateUtils.getCurrentTime());
        payService.closeorder();
        log.info("1.关闭支付定时器执行结束，结束时间："+DateUtils.getCurrentTime());
    }


}
