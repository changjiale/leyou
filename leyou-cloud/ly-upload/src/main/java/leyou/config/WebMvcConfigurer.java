package leyou.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //addResourceHandler是指你想在url请求的路径
        //addResourceLocations是图片存放的真实路径

        registry.addResourceHandler("/static/**").addResourceLocations("E:/code/java_oncedemo/leyou/leyou-cloud/ly-upload/src/main/resources/static/upload/").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}

