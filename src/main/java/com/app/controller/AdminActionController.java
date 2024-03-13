package com.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.app.medel.*;
import com.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:56
 * @version 1.0
 */
 
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
@RestController
@RequestMapping({"/admin"})
@Slf4j
public class AdminActionController {

	@Autowired
	private UserService userService;

	@PostMapping({"/saveLottery"})
	public JSONObject saveLottery(@RequestBody Lottery lottery,HttpSession session) {
		JSONObject res = new JSONObject();
		Integer area = (Integer)session.getAttribute("area");
		res.put("area",area);
		try {
			lottery.setArea(area);
			userService.saveLottery(lottery);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/kaijiang"})
	public JSONObject kaijiang(String code,String date,HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			userService.kaijiang(code, date, area);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/loadRecordDetilList"})
	public JSONObject loadRecordDetilList(Integer pageNumber,Integer pageSize,HttpSession session) {
		JSONObject res = new JSONObject();
		Integer area = (Integer)session.getAttribute("area");
		res.put("area",area);
		try {
			List<LotteryDto> list = userService.loadRecordDetilList(area,pageNumber,pageSize);
			res.put("code",0);
			res.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/loadRecordDetilRepList"})
	public JSONObject loadRecordDetilRepList(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
            ReportDto rep = userService.loadRecordDetilRepList();
			res.put("code",0);
			res.put("data",rep);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/findRecordAllList"})
	public JSONObject findRecordAllList(Integer pageNumber,Integer pageSize,HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			List<OrderDto> list = userService.findRecordAllList(area, pageNumber, pageSize);
			res.put("code",0);
			res.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/loadPersonDetil"})
	public JSONObject loadPersonDetil(Integer pageNumber,Integer pageSize) {
		JSONObject res = new JSONObject();
		try {
			List<User> list = userService.findUserAllList(pageNumber, pageSize);
			res.put("code",0);
			res.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/recharge"})
	public JSONObject recharge(Integer id,BigDecimal amount, HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			userService.recharge(id, amount,session);
			res.put("code",0);
			res.put("message","成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/findCalculationList"})
	public JSONObject findCalculationList(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			HashMap<String,Object> data = userService.findCalculationList(area);
			res.put("code",0);
			res.put("data",data);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}
	@PostMapping({"/findAllCalculationList"})
	public JSONObject findAllCalculationList(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			HashMap<String,Object> data = userService.findAllCalculationList(area);
			res.put("code",0);
			res.put("data",data);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}
	@PostMapping({"/findAllPersonCalculationList"})
	public JSONObject findAllPersonCalculationList(HttpSession session) {
		JSONObject res = new JSONObject();
		try {
			Integer area = (Integer)session.getAttribute("area");
			UserGather data = userService.findAllPersonCalculationList(area);
			res.put("code",0);
			res.put("data",data);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code",-1);
			res.put("message",e.getMessage());
		}
		return res;
	}

	@PostMapping({"/saveSetup"})
	public JSONObject saveSetup(@RequestBody Setup setup,HttpSession session) {
		JSONObject res = new JSONObject();
		Integer area = (Integer)session.getAttribute("area");
		res.put("area",area);
		try {
			setup.setArea(area);
			userService.saveSetup(setup);
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
