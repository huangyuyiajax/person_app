package com.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.app.medel.User;
import com.app.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: TODO
 * @author huangyuyi
 * @date 2024/1/5 19:25
 * @version 1.0
 */
 
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
@RestController
@RequestMapping({"/pay"})
@Slf4j
public class PayController {

	@Autowired
	private PayService payService;

	/**
	 * 微信支付下单
	 * @return
	 */
	@PostMapping({"/unifiedorder"})
	public JSONObject unifiedorder(HttpServletRequest request, BigDecimal amount, String openid, HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			User user = (User)session.getAttribute("user");
			Map<String, String> payMap = payService.unifiedorder(request,amount,openid,user);
			res.put("data",payMap);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	/**
	 * 微信支付回调
	 * @return
	 */
	@RequestMapping(value = "/notify", produces = "application/json;charset=UTF-8")
	public void notify(HttpServletRequest request, HttpServletResponse response) {
		payService.notify(request,response);
	}


	/**
	 * 微信提现
	 * @return
	 */
	@PostMapping({"/transfers"})
	public JSONObject transfers(HttpServletRequest request, BigDecimal amount, String openid,HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			User user = (User)session.getAttribute("user");
			payService.transfers(request,amount,openid,user);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}
}
