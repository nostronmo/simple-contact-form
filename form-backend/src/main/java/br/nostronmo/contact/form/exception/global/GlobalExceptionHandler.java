package br.nostronmo.contact.form.exception.global;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import br.nostronmo.contact.form.model.ContactStatus;
import jakarta.mail.MessagingException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private record ApiErrorResponse(String message) {
	}

	@ExceptionHandler(GlobalRuntimeException.class)
	public ResponseEntity<ApiErrorResponse> handleCustomExceptions(GlobalRuntimeException ex) {
		var errorResponse = new ApiErrorResponse(ex.getMessage());
		return ResponseEntity.status(ex.getStatus()).body(errorResponse);
	}

	@ExceptionHandler(MailAuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleMailAuthException(MailAuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiErrorResponse(ContactStatus.MAIL_NOT_WORKING_PROVIDER.toString()));
	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ApiErrorResponse> handleJakartaMessagingExceptions(MessagingException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiErrorResponse(ContactStatus.MAIL_NOT_WORKING_SPRING.toString()));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ApiErrorResponse> handleMultipartException(MultipartException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(
				"Current request is not a multipart request. Please ensure you are sending 'form-data'."));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex) {
		String message = String.format("Invalid Media Type Ensure you are using 'form-data' in your request body: ",
				ex.getCause());

		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new ApiErrorResponse(message));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiErrorResponse("Missing required parameter: " + ex.getParameterName()));
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exp) {
		Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
	}

}
