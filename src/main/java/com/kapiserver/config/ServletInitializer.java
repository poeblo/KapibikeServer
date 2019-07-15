package com.kapiserver.config;

import com.kapiserver.filter.CORSFilter;
import com.kapiserver.filter.RestAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    RestAuthenticationFilter auth=new RestAuthenticationFilter();
    @Override
    @RequestMapping("/")
    protected Filter[] getServletFilters() {
        return new Filter[]{ new CORSFilter() ,auth};
    }



}
