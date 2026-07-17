package br.nostronmo.contact.form.dto;

import org.hibernate.validator.constraints.Length;

import br.nostronmo.contact.form.global.constraints.ContactConstraints;
import jakarta.validation.constraints.NotBlank;

public record ContactRequest(
		
		@NotBlank(message = "Mail title cannot be blank.")
		@Length(max = ContactConstraints.MAX_NAME_LENGTH)
		String name,
		
		@NotBlank(message = "Mail title cannot be blank.")
		@Length(max = ContactConstraints.MAX_CONTENT_LENGTH)
		String phone,
		
		@Length(max = ContactConstraints.MAX_CONTENT_LENGTH)
		String content
		
) {

}
