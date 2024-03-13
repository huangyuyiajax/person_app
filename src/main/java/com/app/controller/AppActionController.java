package com.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.app.medel.*;
import com.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

@CrossOrigin(origins = {"*"}, maxAge = 3600L)
@RestController
@RequestMapping({"/action"})
@Slf4j
public class AppActionController {

	@Autowired
	private UserService userService;

	@PostMapping({"/loginAction"})
	public JSONObject loginAction(String account,String password,Integer area, HttpSession session) {
		log.info("登录account:"+account+";password:"+password+";area:"+area);
		JSONObject res = new JSONObject();
		try {
			if(area==null){
				throw new Exception("请选择地区");
			}
			User user = userService.login(account, password);
			session.setAttribute("user", user);
			session.setAttribute("area", area);
			session.setMaxInactiveInterval(24*60*60);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

    @PostMapping({"/saveUser"})
    public JSONObject saveUser(User user, HttpSession session) {
        JSONObject res = new JSONObject();
        try {
			User user1 = (User)session.getAttribute("user");
			user.setId(user1.getId());
			User user2 = userService.updateUser(user);
			session.setAttribute("user", user2);
            res.put("code",0);
            res.put("message","成功");
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code",-1);
            res.put("message",e.getMessage());
        }
        return res;
    }

	@RequestMapping({"/logout"})
	public JSONObject loginAction(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			session.removeAttribute("user");
			res.put("code",0);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/findLottery"})
	public JSONObject findLottery(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			HashMap<String,Object> resData = userService.findNowLottery(area);
			res.put("code",0);
			res.put("data",resData);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@RequestMapping({"/loadPerson"})
	public JSONObject loadPerson(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			User user = (User)session.getAttribute("user");
			user = userService.selectUserById(user.getId());
			res.put("code",0);
			res.put("data",user);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/saveOrder"})
	public JSONObject saveOrder(@RequestBody Order order, HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			userService.saveOrder(order,session);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/delOrder"})
	public JSONObject delOrder(Integer orderId, HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			userService.delOrder(orderId,session);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/findOrder"})
	public JSONObject findOrder(Integer pageNumber,Integer pageSize,HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			List<OrderDto> list = userService.findOrder(pageNumber, pageSize,session);
			res.put("code",0);
			res.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@RequestMapping({"/selectBillList"})
	public JSONObject selectBillList(Integer pageNumber,Integer pageSize,HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			User user = (User)session.getAttribute("user");
			List<Bill> list = userService.selectBillList(pageNumber, pageSize,user.getId());
			res.put("code",0);
			res.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@RequestMapping({"/selectBillListReport"})
	public JSONObject selectBillListReport(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			User user = (User)session.getAttribute("user");
			UserGather userGather = userService.selectBillListReport(user.getId());
			res.put("code",0);
			res.put("data",userGather);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/findSetup"})
	public JSONObject findSetup(HttpSession session) {
		JSONObject res = new JSONObject();
		Integer area = (Integer)session.getAttribute("area");
		res.put("area",area);
		try {
			Setup setup = userService.findNowSetup(area);
			res.put("code",0);
			res.put("data",setup);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

}
