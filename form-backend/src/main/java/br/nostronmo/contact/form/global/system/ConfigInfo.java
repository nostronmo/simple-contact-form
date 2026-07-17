package br.nostronmo.contact.form.global.system;

import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.global.constraints.ContactConstraints;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfigInfo {

	public SystemVariables fetchSystemInfo() {
		return new SystemVariables(ContactConstraints.MAX_NAME_LENGTH, ContactConstraints.MAX_PHONE_LENGTH,
				ContactConstraints.MAX_CONTENT_LENGTH);
	}

}
