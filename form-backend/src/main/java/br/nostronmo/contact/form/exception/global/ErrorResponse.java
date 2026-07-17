package br.nostronmo.contact.form.exception.global;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "status", "error", "message", "path" })
public record ErrorResponse(Map<String, String> errorResponse) {

}
