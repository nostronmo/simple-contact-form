package br.nostronmo.contact.form.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.nostronmo.contact.form.dto.ContactPageResponse;
import br.nostronmo.contact.form.dto.ContactRequest;
import br.nostronmo.contact.form.dto.ContactResponse;
import br.nostronmo.contact.form.model.ContactStatus;
import br.nostronmo.contact.form.service.ContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/v1/int")
@RequiredArgsConstructor
public class InternalController {

	private final ContactService service;

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ContactResponse findById(@PathVariable(value = "id") UUID query) {
		return service.findContactById(query);
	}

	@GetMapping(value = "/search")
	@ResponseStatus(HttpStatus.OK)
	public ContactPageResponse searchByParams(@RequestParam(defaultValue = "0", required = false) @Min(0) int page,
			@RequestParam(required = false) String name, @RequestParam(required = false) String phone,
			@RequestParam(required = false) ContactStatus status) {
		return service.searchContactByParams(page, name, phone, status);
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

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ContactResponse changeContactStatus(@PathVariable(value = "id") UUID query,
			@RequestParam ContactStatus status) {
		return service.changeContactStatus(query, status);
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteContact(@PathVariable(value = "id") UUID query) {
		service.deleteContact(query);
	}
	
}
