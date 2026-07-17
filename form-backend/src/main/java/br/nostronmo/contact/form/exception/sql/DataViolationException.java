package br.nostronmo.contact.form.exception.sql;

import org.springframework.http.HttpStatus;

import br.nostronmo.contact.form.exception.global.GlobalRuntimeException;

@SuppressWarnings("serial")
public class DataViolationException extends GlobalRuntimeException {

	public DataViolationException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public DataViolationException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT);
	}
}
