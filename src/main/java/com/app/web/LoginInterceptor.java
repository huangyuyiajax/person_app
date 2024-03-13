package com.app.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.medel.User;
import com.app.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
/**
 * @description: TODO 定义处理器拦截器
 * @author huangyuyi
 * @date 2023/4/21 10:52
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute(ConfigUtils.USER_KEY) == null) {
            response.sendRedirect("/app/login");
            return false;
        }
        User user = (User)request.getSession().getAttribute(ConfigUtils.USER_KEY);
        String url = request.getRequestURI();
        if (!ConfigUtils.ADMIN.equals(user.getAccount())&&url.startsWith(ConfigUtils.ADMIN_URL)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out;
            try {
                JSONObject res = new JSONObject();
                res.put("code",-1);
                res.put("message","当前接口没有权限调用");
                String json = JSON.toJSONString(res);
                response.setContentType("application/json");
                out = response.getWriter();
                out.append(json);
                out.flush();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
                return false;
            }
        }
        return true;
    }
}
