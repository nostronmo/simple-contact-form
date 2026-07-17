package br.nostronmo.contact.form.exception.system;

import org.springframework.http.HttpStatus;

import br.nostronmo.contact.form.exception.global.GlobalRuntimeException;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class InternalException extends GlobalRuntimeException {
	public InternalException(String message) {
		super(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
