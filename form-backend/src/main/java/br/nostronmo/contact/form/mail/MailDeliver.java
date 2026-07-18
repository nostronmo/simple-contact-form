package br.nostronmo.contact.form.mail;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.model.ContactStatus;
import br.nostronmo.contact.form.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailDeliver {

	private final MailBuilder mailBuilder;
	private final ContactRepository repository;

	@Async
	@Transactional
	public void sendEmailToDefault(UUID contactId) {
		var contact = repository.findById(contactId).orElse(null);
		if (contact == null) {
			log.error("Aborting async mail delivery. Contact ID not found: " + contactId);
			return;
		}

		try {
			String rawTemplate = new String(contact.getContent(), StandardCharsets.UTF_8);
			mailBuilder.buildMail(contact, rawTemplate);
			contact.setSentTime(ZonedDateTime.now());
			repository.save(contact);
			log.info("Async mail delivered successfully for contact name: " + contact.getName());
		} catch (Exception e) {
			contact.setStatus(ContactStatus.MAIL_NOT_WORKING_SPRING);
			repository.save(contact);
			log.error("Error processing template for contact ID: " + contact.getId(), e);
		}
	}

}
