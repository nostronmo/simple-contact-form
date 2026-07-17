package br.nostronmo.contact.form.mail;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.model.Contact;
import br.nostronmo.contact.form.model.ContactStatus;
import br.nostronmo.contact.form.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailDeliver {

	private final MailBuilder mailBuilder;
	private final ContactRepository repository;
	
	public void sendEmailToDefault(Contact contact) {
		try {
			String rawTemplate = new String(contact.getContent(), StandardCharsets.UTF_8);
			mailBuilder.buildMail(contact, rawTemplate);
		} catch (Exception e) {
			contact.setStatus(ContactStatus.MAIL_NOT_WORKING_SPRING);
			repository.save(contact);
			log.error("Error processing template for contact ID: " + contact.getId(), e);
		}
	}

}
