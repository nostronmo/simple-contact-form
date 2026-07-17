package br.nostronmo.contact.form.global.constraints;

public final class ContactConstraints {

	public static final int MAX_NAME_LENGTH = 200;
	public static final int MAX_PHONE_LENGTH = 80;
	public static final int MAX_CONTENT_LENGTH = 4000;
	public static final String[] TEMPLATE_FILE_EXTENSION = { ".md", ".html" };
	public static final String[] VARIABLES_FILE_EXTENSION = { ".csv", ".txt" };

	private ContactConstraints() {
	}
}
