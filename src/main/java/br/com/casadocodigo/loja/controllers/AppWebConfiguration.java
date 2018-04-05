package br.com.casadocodigo.loja.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@EnableWebMvc
@ComponentScan(basePackageClasses = { HomeController.class })
public class AppWebConfiguration {
	private static final String PREFIX_VIEWS = "/WEB-INF/views/";
	private static final String SUFFIX_VIEWS = ".jsp";

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix(PREFIX_VIEWS);
		resolver.setSuffix(SUFFIX_VIEWS);
		return resolver;
	}
}
