package ds.spring.mycrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.Arrays;

@SpringBootApplication
public class MycrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycrudApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean hiddenHttpMethodFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new HiddenHttpMethodFilter());
		filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
		return filterRegistrationBean;
	}

}
