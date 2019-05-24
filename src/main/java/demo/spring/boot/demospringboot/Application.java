package demo.spring.boot.demospringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.charset.Charset;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /**
     * springboot 默认的就是 utf-8
     * 修改为iso-8859-1就会出现乱码
     * @return
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter(){
        //StringHttpMessageConverter converter  = new StringHttpMessageConverter(Charset.forName("ISO-8859-1"));
        StringHttpMessageConverter converter  = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }

}
