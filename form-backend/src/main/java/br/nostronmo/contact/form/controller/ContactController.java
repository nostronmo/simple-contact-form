package br.nostronmo.contact.form.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.nostronmo.contact.form.dto.ContactRequest;
import br.nostronmo.contact.form.dto.ContactResponse;
import br.nostronmo.contact.form.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/v1/contact")
@RequiredArgsConstructor
public class ContactController {

	private final ContactService service;

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ContactResponse findById(@PathVariable(value = "id") UUID query) {
		return service.findContactById(query);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ContactResponse createContact(@RequestBody @Valid ContactRequest request) {
		return service.createContact(request);
	}

	@PostMapping(value = "/send/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ContactResponse sendMail(@PathVariable(value = "id") UUID query) {
		return service.sendMail(query);
	}

}
