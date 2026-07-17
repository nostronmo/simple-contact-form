package br.nostronmo.contact.form.global.system;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ControllerConfig {

	private final ConfigInfo config;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public SystemVariables showSystemInfo() {
		return config.fetchSystemInfo();
	}

}
