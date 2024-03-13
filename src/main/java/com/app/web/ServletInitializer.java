package com.app.web;

import com.PersonApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:56
 * @version 1.0
 */
 
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PersonApplication.class);
    }

}

