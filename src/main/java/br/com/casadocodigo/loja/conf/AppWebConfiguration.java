package br.com.casadocodigo.loja.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.cache.CacheBuilder;

import br.com.casadocodigo.loja.controllers.HomeController;
import br.com.casadocodigo.loja.daos.ProdutoDAO;
import br.com.casadocodigo.loja.infra.FileSaver;
import br.com.casadocodigo.loja.models.CarrinhoCompras;

@EnableWebMvc
@EnableCaching
@ComponentScan(basePackageClasses = { HomeController.class, ProdutoDAO.class, FileSaver.class, CarrinhoCompras.class })
public class AppWebConfiguration extends WebMvcConfigurerAdapter {
	private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String MESSAGES = "/WEB-INF/messages";
	private static final String PREFIX_VIEWS = "/WEB-INF/views/";
	private static final String SUFFIX_VIEWS = ".jsp";

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix(PREFIX_VIEWS);
		resolver.setSuffix(SUFFIX_VIEWS);
		resolver.setExposedContextBeanNames("carrinhoCompras");
		return resolver;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES);
		messageSource.setDefaultEncoding(DEFAULT_ENCODING);
		messageSource.setCacheSeconds(1);

		return messageSource;
	}

	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		DateFormatterRegistrar formatterRegistrar = new DateFormatterRegistrar();
		formatterRegistrar.setFormatter(new DateFormatter(DEFAULT_DATETIME_FORMAT));
		formatterRegistrar.registerFormatters(conversionService);

		return conversionService;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CacheManager cacheManager() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(5,
				TimeUnit.MINUTES);
		GuavaCacheManager manager = new GuavaCacheManager();
		manager.setCacheBuilder(builder);
		return manager;
	}

	@Bean
	public ViewResolver contentNegotiationViewResolver(ContentNegotiationManager manager) {
		List<ViewResolver> viewResolvers = new ArrayList<>();
		viewResolvers.add(internalResourceViewResolver());
		viewResolvers.add(new JsonViewResolver());
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(viewResolvers);
		resolver.setContentNegotiationManager(manager);
		return resolver;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
