package com.autoconfigure.email.notificationspringbootautoconfigure;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@ConditionalOnClass(MailSender.class)
@PropertySource("classpath:application.properties")
public class NotificationAutoConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware {
	 @Value("${mail.protocol}")
	    private String protocol;
	    @Value("${mail.host}")
	    private String host;
	    @Value("${mail.port}")
	    private int port;
	    @Value("${mail.smtp.auth}")
	    private boolean auth;
	    @Value("${mail.smtp.starttls.enable}")
	    private boolean starttls;
	    @Value("${mail.to}")
	    private String to;
	    @Value("${mail.username}")
	    private String username;
	    @Value("${mail.password}")
	    private String password;
		//private static final String EMAIL_TEXT_TEMPLATE_NAME = "html/email-simple";

	    
	    private ApplicationContext applicationContext;

	    @Override
	    public void setApplicationContext(ApplicationContext applicationContext) {
	        this.applicationContext = applicationContext;
	    }
	    
	    @Bean
	    public JavaMailSender notificationMailSender() {
	        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	        Properties mailProperties = new Properties();
	        mailProperties.put("mail.smtp.auth", auth);
	        mailProperties.put("mail.smtp.starttls.enable", starttls);
	        mailSender.setJavaMailProperties(mailProperties);
	        mailSender.setHost(host);
	        mailSender.setPort(port);
	        mailSender.setProtocol(protocol);
	        mailSender.setUsername(username);
	        mailSender.setPassword(password);
	        
	        return mailSender;
	    }
	    @Bean
	    public ResourceBundleMessageSource emailMessageSource() throws IOException {
	        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	        messageSource.setBasename("mail/MailMessages");
	        return messageSource;
	    }
	    
	  /*  @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	      if (!registry.hasMappingForPattern("/templates/**")) {
	         registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
	      }
	    }*/
	    /**
	     * Thymeleaf beans
	     * @throws IOException 
	     * 
	     */
	    
	    
	    @Bean
	    public TemplateEngine emailTemplateEngine() throws IOException {
	        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	        
	       templateEngine.addTemplateResolver(textTemplateResolver());
	        
	        // Resolver for HTML emails (except the editable one)
	       templateEngine.addTemplateResolver(htmlTemplateResolver());
	        
	        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
	        return templateEngine;
	    }
	    
	    private ITemplateResolver textTemplateResolver() {
	        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	        templateResolver.setOrder(Integer.valueOf(1));
	        templateResolver.setResolvablePatterns(Collections.singleton("text/*"));
	        templateResolver.setPrefix("/mail/");
	        templateResolver.setSuffix(".txt");
	        templateResolver.setTemplateMode(TemplateMode.TEXT);
	        templateResolver.setCharacterEncoding(String.valueOf(Charset.forName("UTF-8")));
	        templateResolver.setCacheable(false);
	        return templateResolver;
	    }
	    private ITemplateResolver htmlTemplateResolver() {
	        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	        templateResolver.setOrder(Integer.valueOf(2));
	      templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
	        templateResolver.setPrefix("/mail/");
	        templateResolver.setSuffix(".html");
	        templateResolver.setTemplateMode(TemplateMode.HTML);
	        templateResolver.setCharacterEncoding(String.valueOf(Charset.forName("UTF-8")));
	        templateResolver.setCacheable(false);
	        return templateResolver;
	    }
	  /*  @Bean
	    public ThymeleafViewResolver thymeleafViewResolver() throws IOException {
	        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();

	        thymeleafViewResolver.setTemplateEngine(emailTemplateEngine());
	        thymeleafViewResolver.setCharacterEncoding("UTF-8");

	        return thymeleafViewResolver;
	    }*/
	    
	   /* @Bean
	    public SpringResourceTemplateResolver templateResolver(){
	        // SpringResourceTemplateResolver automatically integrates with Spring's own
	        // resource resolution infrastructure, which is highly recommended.
	        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	        templateResolver.setApplicationContext(this.applicationContext);
	        templateResolver.setPrefix("/templates/");
	        templateResolver.setSuffix(".html");
	        // HTML is the default value, added here for the sake of clarity.
	        templateResolver.setTemplateMode("html");
	        // Template cache is true by default. Set to false if you want
	        // templates to be automatically updated when modified.
	        templateResolver.setCacheable(true);
	        return templateResolver;
	    }*/

	    
}
