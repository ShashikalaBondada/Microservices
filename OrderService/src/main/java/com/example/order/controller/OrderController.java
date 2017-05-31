package com.example.order.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@Slf4j
@RestController
public class OrderController {
	private final Logger LOGGER = LoggerFactory
			.getLogger(OrderController.class);

	/*
	 * public OrderRepository orderRepository;
	 * 
	 * @Autowired public OrderController(OrderRepository orderRepository){
	 * this.orderRepository=orderRepository; }
	 */
	@Value("${mail.email}")
	private String email;
	@Autowired
	JavaMailSender mailSender;
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	@Autowired
	TemplateEngine emailTemplateEngine;
	@Autowired
	ResourceBundleMessageSource messageResource;

	private static final String EMAIL_TEXT_TEMPLATE_NAME = "html/email-simple";

	@RequestMapping(value = "/simpleEmail", method = RequestMethod.POST)
	@ResponseBody
	public String placeOrder() {
		LOGGER.debug("Test Mail");

		// try{
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
				"UTF-8");
		try {
			message.setTo(email);
			message.setSubject("AutoConfig test");
			message.setText("This is the message body");
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "failure";

		}

		return "success";
	}

	@RequestMapping(value = "/sendEmailWithTemplate", method = RequestMethod.POST)
	@ResponseBody
	public String sendTextMail(@RequestParam String receipientName,
			@RequestParam String receipientEmail, Locale locale) {
		LOGGER.debug("Sending email with html Template");

		final Context ctx = new Context(locale);
		ctx.setVariable("name", receipientName);
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
				"UTF-8");
		try {
			message.setSubject("Example plain TEXT email");

			message.setTo(receipientEmail);

			// Create the plain TEXT body using Thymeleaf
			final String textContent = emailTemplateEngine.process(
					EMAIL_TEXT_TEMPLATE_NAME, ctx);

			message.setText(textContent);

			mailSender.send(mimeMessage);

		} catch (MessagingException e) {
			LOGGER.error("Exception while sending email with template "
					+ e.getMessage());
			e.printStackTrace();
			return "failure in sending email";
		}
		return "successfully sent email";
	}

}
