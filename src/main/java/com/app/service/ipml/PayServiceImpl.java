package com.app.service.ipml;

import com.app.dao.UserDao;
import com.app.medel.*;
import com.app.service.PayService;
import com.app.wxpay.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author huangyuyi
 */
@Service("payServiceImpl")
@Slf4j
public class PayServiceImpl extends WXPayConfig implements PayService {

    @Value("${wxpay.appid}")
    private String appid;//	微信分配的公众账号ID（企业号corpid即为此appid）
    @Value("${wxpay.mch_id}")
    private String mch_id;//微信支付分配的商户号
    @Value("${wxpay.key}")
    private String key;//密钥

    @Autowired
    private UserDao userDao;

    @Override
    public Map<String, String> unifiedorder(HttpServletRequest request, BigDecimal amount, String openid, User user) throws Exception{
        if(amount==null||amount.compareTo(BigDecimal.ZERO)==0){
            throw new Exception("充值金额不能为空");
        }
        if(amount.compareTo(BigDecimal.ZERO)<0){
            throw new Exception("充值金额需要大于0");
        }
        if(StringUtils.isEmpty(openid)){
            throw new Exception("openId不能为空");
        }
        WXPay wxPay = new WXPay(this,WXPayUtil.getTempContextUrl(request)+"/pay/notify");
        Map<String, String> paraMap = new TreeMap<String, String>();
        String outTradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        paraMap.put("body", "账户充值");
        paraMap.put("openid", openid);
        paraMap.put("out_trade_no", outTradeNo);
        paraMap.put("spbill_create_ip", WXPayUtil.getIpAddr(request));
        paraMap.put("device_info", "WEB");
        paraMap.put("total_fee", String.valueOf(amount));
        paraMap.put("trade_type", "MWEB");
        log.info("支付下单入参："+paraMap.toString());
        Map<String, String> map = wxPay.unifiedOrder(paraMap);
        log.info("支付下单出参："+map.toString());
        if(WXPayConstants.FAIL.equals(map.get("return_code"))||WXPayConstants.FAIL.equals(map.get("result_code"))){
           throw new Exception(map.get("return_msg"));
        }
        Map<String, String> payMap = new HashMap<>();
        payMap.put("appId", appid);
        payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp()+"");
        payMap.put("nonceStr", WXPayUtil.generateNonceStr());
        payMap.put("signType", "MD5");
        payMap.put("package", "prepay_id=" + map.get("prepay_id"));
        String paySign = WXPayUtil.generateSignature(payMap, key);
        payMap.put("paySign", paySign);
        log.info("支付下单返回页面参数："+payMap.toString());
        PayBill payBill = new PayBill();
        payBill.setUserId(user.getId());
        payBill.setType("1");
        payBill.setPrice(amount);
        payBill.setOutTradeNo(outTradeNo);
        userDao.insertPayBill(payBill);
        return payMap;
    }

    @Override
    public synchronized void notify(HttpServletRequest request, HttpServletResponse response){
        String resultTemplate = "<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[%s]]></return_msg></xml>";
        String returnCode;
        String returnMsg;
        try (BufferedReader reader = request.getReader()) {
            String line;
            StringBuilder input = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                input.append(line);
            }
            String xmlString = input.toString();
            log.info("----------------支付回调Xml:---------{}", xmlString);
            Map<String, String> map = WXPayUtil.xmlToMap(xmlString);
            log.info("----------------支付回调map:---------{}", map.toString());
            if (WXPayConstants.SUCCESS.equals(map.get("result_code")) && WXPayConstants.SUCCESS.equals(map.get("return_code"))) {
                // 检查签名，防止数据泄露导致出现“假通知”，造成资金损失。（微信支付推荐）
                if (WXPayUtil.isSignatureValid(new TreeMap<>(map), key)) {
                    returnCode = WXPayConstants.SUCCESS;
                    returnMsg = WXPayConstants.SUCCESS_MESSAGE;
                    // 更新订单状态，
					String outTradeNo = map.get("out_trade_no");
                    PayBill payBill = userDao.selectPayBill(outTradeNo);
                    if(payBill!=null){
                        payBill.setType("2");
                        userDao.updatePayBill(payBill);
                        User user = userDao.selectUserById(payBill.getUserId());
                        Bill bill = new Bill();
                        bill.setUserId(payBill.getUserId());
                        BigDecimal amount = payBill.getPrice();
                        bill.setType("1");
                        bill.setPrice(amount);
                        bill.setOutTradeNo(payBill.getOutTradeNo());
                        bill.setPaybillId(payBill.getId());
                        if(user!=null){
                            bill.setBala(user.getCash());
                            user.setCash(user.getCash().add(amount));
                            bill.setCash(user.getCash());
                            userDao.updateUserCash(user);
                        }
                        userDao.insertBill(bill);
                    }
                } else {
                    log.error("支付回调处理失败, 签名不正确，param: {}", map);
                    returnCode = WXPayConstants.FAIL;
                    returnMsg = "签名不正确";
                }
            } else {
                returnCode = WXPayConstants.FAIL;
                returnMsg = "参数校验不正确";
            }
            String result = String.format(resultTemplate, returnCode, returnMsg);
            log.info("----------------处理回调后返回的信息:---------{}", result);
            response.getWriter().write(result);
        } catch (Exception e) {
            log.error("支付回调处理异常", e);
            try (PrintWriter writer = response.getWriter()) {
                writer.write(String.format(resultTemplate, WXPayConstants.FAIL, "回调处理异常"));
            } catch (Exception e2) {
                log.error("支付回调处理异常", e2);
            }
        }
    }

    @Override
    public synchronized void closeorder(){
        List<PayBill> list = userDao.findUnPayBillList();
        WXPay wxPay = new WXPay(this);
        Map<String, String> paraMap = new TreeMap<String, String>();
        for(PayBill payBill:list){
            String outTradeNo = payBill.getOutTradeNo();
            try {
                paraMap.put("out_trade_no", outTradeNo);
                Map<String, String> map = wxPay.closeOrder(paraMap);
                if(map.get("return_code").equals(WXPayConstants.SUCCESS)&&WXPayConstants.SUCCESS.equals(map.get("result_code"))){
                    payBill.setType("3");
                    userDao.updatePayBill(payBill);
                }else {
                    payBill.setType("4");
                    userDao.updatePayBill(payBill);
                    log.error(outTradeNo+"订单关闭异常：return_code="+map.get("return_code")+";return_msg"+map.get("return_msg"));
                }
            } catch (Exception e) {
                log.error(outTradeNo+"订单关闭异常",e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transfers(HttpServletRequest request,BigDecimal amount,String openid,User user) throws Exception{
        if(user==null){
            throw new Exception("没有会员信息");
        }
        if(StringUtils.isEmpty(openid)){
            throw new Exception("openid不能为空");
        }
        if(amount==null||amount.compareTo(BigDecimal.ZERO)==0){
            throw new Exception("提现金额不能为空");
        }
        if(amount.compareTo(BigDecimal.ZERO)<0){
            throw new Exception("提现金额需要大于0");
        }
        if(user.getCash().compareTo(amount)<0){
            throw new Exception("当前余额"+user.getCash()+"元，余额不足，不能提现");
        }
        WXPay wxPay = new WXPay(this,true);
        Map<String, String> paraMap = new TreeMap<String, String>();
        String outTradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        paraMap.put("desc", "账户提现");
        paraMap.put("openid", openid);
        paraMap.put("partner_trade_no", outTradeNo);
        paraMap.put("check_name", "NO_CHECK");
        paraMap.put("amount", String.valueOf(amount));
        paraMap.put("spbill_create_ip", WXPayUtil.getIpAddr(request));
        log.info("提现入参："+paraMap.toString());
        Map<String, String> map = wxPay.transfers(paraMap);
        log.info("提现出参："+map.toString());
        if(WXPayConstants.FAIL.equals(map.get("return_code"))||WXPayConstants.FAIL.equals(map.get("result_code"))){
            throw new Exception(map.get("return_msg"));
        }
        Bill bill = new Bill();
        bill.setUserId(user.getId());
        bill.setBala(user.getCash());
        bill.setType("5");
        bill.setPrice(amount);
        bill.setOutTradeNo(outTradeNo);
        user.setCash(user.getCash().subtract(amount));
        bill.setCash(user.getCash());
        userDao.updateUserCash(user);
        userDao.insertBill(bill);
    }

    @Override
    public String getAppID() {
        return appid;
    }

    @Override
    public String getMchID() {
        return mch_id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {//证书 证书文件放项目里
       return this.getClass().getClassLoader().getResourceAsStream("piclient_cert.p12");
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }
            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com",false);
            }
        };
    }
}
