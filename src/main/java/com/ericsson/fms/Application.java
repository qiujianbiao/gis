package com.ericsson.fms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.ericsson.fms.filter.HTTPBasicAuthorizeAttribute;


@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Application.class, args);
    }

    @Bean  
    public FilterRegistrationBean  filterRegistrationBean() {  
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();  
        HTTPBasicAuthorizeAttribute httpBasicFilter = new HTTPBasicAuthorizeAttribute();  
        registrationBean.setFilter(httpBasicFilter);  
        List<String> urlPatterns = new ArrayList<String>();  
        urlPatterns.add("/gis/*");  
        registrationBean.setUrlPatterns(urlPatterns);  
        return registrationBean;  
    }
    
//    @Bean
//    public EmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() throws IOException {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.addAdditionalTomcatConnectors(httpConnector());
//        return tomcat;
//    }
//
//    public Connector httpConnector() throws IOException {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connector.getProtocolHandler();
//        connector.setPort(8080);
//        //设置最大线程数
//        http11NioProtocol.setMaxThreads(maxThreads);
//        //设置初始线程数  最小空闲线程数
//        http11NioProtocol.setMinSpareThreads(minSpareThreads);
//        //设置超时
//        http11NioProtocol.setConnectionTimeout(connectionTimeout);
//        return connector;
//    }
}
