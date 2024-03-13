package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:49
 * @version 1.0
 */

@SpringBootApplication
@MapperScan(basePackages = {"com.app.dao"})
public class PersonApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PersonApplication.class);
        builder.headless(false).web(WebApplicationType.SERVLET).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}
