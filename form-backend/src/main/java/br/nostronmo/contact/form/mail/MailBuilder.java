package br.nostronmo.contact.form.mail;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.model.Contact;
import br.nostronmo.contact.form.model.ContactStatus;
import br.nostronmo.contact.form.repository.ContactRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailBuilder {

	private final JavaMailSender mailSender;
	private final ContactRepository repository;
	private final MailContentProcessor processor;

	@Value("${application.contact.admin-mail}")
	private String adminEmail;

	@Value("${spring.application.name:Spring Boot App}")
	private String appName;

	public void buildMail(Contact contact, String rawTemplate) {
		try {

			byte[] processedTemplateBytes = processor.processEmailTemplate();
			String htmlTemplate = new String(processedTemplateBytes, StandardCharsets.UTF_8);

			String userMessage = "No content provided";
			if (contact.getContent() != null) {
				userMessage = new String(contact.getContent(), StandardCharsets.UTF_8);
			}

			String contentToSent = htmlTemplate.replace("${phone}", contact.getPhone())
					.replace("{{phone}}", contact.getPhone()).replace("${name}", contact.getName())
					.replace("{{name}}", contact.getName()).replace("${content}", userMessage)
					.replace("{{content}}", userMessage);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

			mimeHelper.setTo(adminEmail);

			mimeHelper.setSubject(appName + " - New Contact Notification");

			mimeHelper.setText(contentToSent, true);

			mailSender.send(mimeMessage);
			contact.setStatus(ContactStatus.SENT);
			repository.save(contact);
		} catch (MessagingException e) {
			contact.setStatus(ContactStatus.MAIL_NOT_WORKING_SPRING);
			repository.save(contact);
			log.error("Mail was unable to be sent for contact ID: {}" + contact.getId());
		}
	}

}
