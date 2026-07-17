package br.nostronmo.contact.form.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import br.nostronmo.contact.form.dto.ContactPageResponse;
import br.nostronmo.contact.form.dto.ContactRequest;
import br.nostronmo.contact.form.dto.ContactResponse;
import br.nostronmo.contact.form.model.Contact;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {
		java.nio.charset.StandardCharsets.class })
public interface ContactMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", constant = "NOT_SENT")
	@Mapping(target = "content", expression = "java(request.content() != null ? request.content().getBytes(StandardCharsets.UTF_8) : null)")
	Contact build(ContactRequest request);

	@Mapping(target = "content", expression = "java(contact.getContent() != null ? new String(contact.getContent(), StandardCharsets.UTF_8) : null)")
	ContactResponse toResponse(Contact contact);

	List<ContactResponse> toResponseList(List<Contact> contact);

	default ContactPageResponse toResponse(Page<Contact> page) {
		if (page == null) {
			return null;
		}
		List<ContactResponse> content = toResponseList(page.getContent());
		return new ContactPageResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());

	}

}
