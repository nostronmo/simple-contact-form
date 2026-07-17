package br.nostronmo.contact.form.dto;

import java.util.UUID;

import br.nostronmo.contact.form.model.ContactStatus;

public record ContactResponse(
		
		UUID id,
		String name,
		String phone,
		String content,
		ContactStatus status
		
		) {

}
