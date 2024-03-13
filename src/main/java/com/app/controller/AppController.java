package com.app.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.app.medel.User;
import com.app.util.ConfigUtils;
import com.app.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@CrossOrigin(origins = {"*"}, maxAge = 3600L)
@Controller
@RequestMapping({"/app"})
@Slf4j
public class AppController {

	@Value("${wxpay.appid}")
	private String appid;//	微信分配的公众账号ID（企业号corpid即为此appid）
	@Value("${wxpay.mch_id}")
	private String mch_id;//微信支付分配的商户号
	@Value("${wxpay.key}")
	private String key;//密钥

	@RequestMapping({"/login"})
	public String login() {
		return "app/login.html";
	}

	@RequestMapping({"/index"})
	public String index() {
		return "app/index.html";
	}

	@RequestMapping({"/admin"})
	public String admin(HttpSession session) {
		User user = (User)session.getAttribute("user");
		if(user!=null&& ConfigUtils.ADMIN.equals(user.getAccount())){
			return "app/admin.html";
		}else {
			return "app/index.html";
		}
	}

	@RequestMapping({"/pay"})
	public String pay(HttpServletRequest request, String code, BigDecimal amount, ModelMap model) {
		log.info("微信授权code="+code+";amount="+amount);
		if(StringUtils.isEmpty(code)){
			return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+ WXPayUtil.getTempContextUrl(request)+"/app/pay?amount="+amount+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
		}
		String res = HttpRequest.post("https://api.weixin.qq.com/sns/oauth2/access_token")
				.form("appid",appid)
				.form("secret",key)
				.form("code",code)
				.form("grant_type","authorization_code")
				.execute().body();
		JSONObject json = JSONObject.parseObject(res);
		log.info("微信授权返回"+json.toJSONString());
		if(json.containsKey("errmsg")){
			log.info("微信授权失败"+json.getString("errmsg"));
			return "app/index.html";
		}
		String openid = json.getString("openid");
		model.addAttribute("openid",openid);
//		model.addAttribute("openid","o7KFbs-JFnIDBapvFQ0x-e7XvgVU");s
		if(amount==null){
			amount = BigDecimal.ZERO;
		}
		model.addAttribute("amount",amount);
		return "app/pay.html";
	}

	@RequestMapping({"/transfers"})
	public String transfers(HttpServletRequest request, String code, BigDecimal amount, ModelMap model) {
		log.info("微信授权code="+code+";amount="+amount);
		if(StringUtils.isEmpty(code)){
			return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+ WXPayUtil.getTempContextUrl(request)+"/app/transfers?amount="+amount+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
		}
		String res = HttpRequest.post("https://api.weixin.qq.com/sns/oauth2/access_token")
				.form("appid",appid)
				.form("secret",key)
				.form("code",code)
				.form("grant_type","authorization_code")
				.execute().body();
		JSONObject json = JSONObject.parseObject(res);
		log.info("微信授权返回"+json.toJSONString());
		if(json.containsKey("errmsg")){
			log.info("微信授权失败"+json.getString("errmsg"));
			return "app/index.html";
		}
		String openid = json.getString("openid");
		model.addAttribute("openid",openid);
//		model.addAttribute("openid","o7KFbs-JFnIDBapvFQ0x-e7XvgVU");
		if(amount==null){
			amount = BigDecimal.ZERO;
		}
		model.addAttribute("amount",amount);
		return "app/transfers.html";
	}

}
