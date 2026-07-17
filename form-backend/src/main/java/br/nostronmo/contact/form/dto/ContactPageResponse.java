package br.nostronmo.contact.form.dto;

import java.util.List;

public record ContactPageResponse(
		
		List<ContactResponse> content,
		Integer page,
		Integer size,
		Integer totalPages
		
) {

}
