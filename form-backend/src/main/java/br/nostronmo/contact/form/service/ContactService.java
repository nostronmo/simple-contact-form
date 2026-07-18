package br.nostronmo.contact.form.service;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.nostronmo.contact.form.dto.ContactPageResponse;
import br.nostronmo.contact.form.dto.ContactRequest;
import br.nostronmo.contact.form.dto.ContactResponse;
import br.nostronmo.contact.form.mail.MailDeliver;
import br.nostronmo.contact.form.mapper.ContactMapper;
import br.nostronmo.contact.form.model.Contact;
import br.nostronmo.contact.form.model.ContactStatus;
import br.nostronmo.contact.form.repository.ContactRepository;
import br.nostronmo.contact.form.repository.ContactSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

	@Value("${application.page.size:20}")
	private int PAGE_SIZE;

	private final ContactRepository repository;
	private final ContactMapper mapper;
	private final ContactComponent component;
	private final MailDeliver mailSender;

	@Transactional(readOnly = true)
	public ContactResponse findContactById(UUID query) {
		var mail = component.findById(query);
		return mapper.toResponse(mail);
	}

	@Transactional(readOnly = true)
	public ContactPageResponse searchContactByParams(int page, String name, String phone, ContactStatus status) {
		Specification<Contact> spec = ContactSpecification.findByCriteria(name, phone, status);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Contact> contact = repository.findAll(spec, pageable);
		return mapper.toResponse(contact);
	}

	@Transactional
	public ContactResponse createContact(ContactRequest request) {

		ZonedDateTime cooldownThreshold = ZonedDateTime.now().minusMinutes(5);

		if (repository.existsByVariablesAfterTime(request.name(), request.phone(), cooldownThreshold)) {
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
					"You have already submitted a contact request recently. Please wait a few minutes before trying again.");
		}
		var contact = mapper.build(request);
		repository.save(contact);
		mailSender.sendEmailToDefault(contact.getId());
		return mapper.toResponse(contact);
	}

	@Transactional
	public ContactResponse sendMail(UUID mailId) {
		var contact = component.findById(mailId);
		mailSender.sendEmailToDefault(contact.getId());
		return mapper.toResponse(contact);
	}

	@Transactional
	public ContactResponse changeContactStatus(UUID query, ContactStatus status) {
		var contact = component.findById(query);
		contact.setStatus(status);
		repository.save(contact);
		return mapper.toResponse(contact);
	}

	@Transactional
	public void deleteContact(UUID query) {
		var contact = component.findById(query);
		repository.delete(contact);
	}

}
