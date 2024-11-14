package com.ruoyi.system.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.io.File;
import java.io.FileNotFoundException;

/**
 * 功能：
 * 作者：邵文博
 * 日期：2024/11/14 17:31
 */
@Configuration
public class FileHandleConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String gitPath = path.getParent()+ File.separator + "logistics" + File.separator + "uploads" + File.separator;
//        打包成jar包就用下面的
//        String gitPath = path.getParentFile().getParentFile().getParent() + File.separator + "logistics" + File.separator + "uploads" + File.separator;
        registry.addResourceHandler("/uploads/**").addResourceLocations(gitPath);
        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/PlotsPic/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

}
