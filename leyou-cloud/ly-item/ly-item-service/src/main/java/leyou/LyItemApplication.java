package leyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableEurekaClient
@MapperScan("leyou.item.mapper")
//@MapperScan(basePackages ="leyou.item.mapper")
public class LyItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyItemApplication.class);
    }
}