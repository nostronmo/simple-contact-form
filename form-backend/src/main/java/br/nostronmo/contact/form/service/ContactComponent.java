package br.nostronmo.contact.form.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.exception.sql.NotFoundException;
import br.nostronmo.contact.form.model.Contact;
import br.nostronmo.contact.form.repository.ContactRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContactComponent {

	private final ContactRepository repository;

	public Contact findById(UUID query) {
		return repository.findById(query)
				.orElseThrow(() -> new NotFoundException("Contact with id: " + query + " was not found."));
	}

}
