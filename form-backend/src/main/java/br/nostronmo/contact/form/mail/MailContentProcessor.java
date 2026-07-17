package br.nostronmo.contact.form.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import br.nostronmo.contact.form.exception.system.InternalException;
import br.nostronmo.contact.form.global.constraints.ContactConstraints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailContentProcessor {

	@Value("${mail.templates.base-path:templates}")
	private String basePath;

	@Value("template")
	private String templateDefaultName;

	@Value("variables")
	private String variablesDefaultName;

	private final Parser parser = Parser.builder().build();
	private final HtmlRenderer renderer = HtmlRenderer.builder().build();

	private Map<String, String> parseVariables(Path variablesPath) throws IOException {
		Map<String, String> map = new HashMap<>();

		if (variablesPath == null || !Files.exists(variablesPath)) {
			log.info("No variables file found. Proceeding without variable injection.");
			return map;
		}

		Files.lines(variablesPath, StandardCharsets.UTF_8).filter(line -> line != null && line.contains(":"))
				.forEach(line -> {
					String[] parts = line.split(":", 2);
					map.put(parts[0].trim(), parts[1].trim());
				});

		return map;
	}

	private Optional<Path> findFileByBaseName(Path directory, String baseName, String[] allowedExtensions) {
		if (!Files.exists(directory)) {
			return Optional.empty();
		}

		for (String ext : allowedExtensions) {
			Path potentialPath = directory.resolve(baseName + ext);
			if (Files.exists(potentialPath)) {
				return Optional.of(potentialPath);
			}
		}

		return Optional.empty();
	}

	public byte[] processEmailTemplate() {
		try {
			Path directory = Paths.get(basePath);

			Path templatePath = findFileByBaseName(directory, templateDefaultName,
					ContactConstraints.TEMPLATE_FILE_EXTENSION)
					.orElseThrow(() -> new InternalException("Template file not found in " + directory.toAbsolutePath()
							+ ". Must be named '" + templateDefaultName + "' with one of these extensions: "
							+ Arrays.toString(ContactConstraints.TEMPLATE_FILE_EXTENSION)));

			Path variablesPath = findFileByBaseName(directory, variablesDefaultName,
					ContactConstraints.VARIABLES_FILE_EXTENSION).orElse(null);

			String content = Files.readString(templatePath, StandardCharsets.UTF_8);

			Map<String, String> parsedVariables = parseVariables(variablesPath);
			for (Map.Entry<String, String> entry : parsedVariables.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				content = content.replace("${" + key + "}", value);
				content = content.replace("{{" + key + "}}", value);
			}

			var document = parser.parse(content);
			return renderer.render(document).getBytes(StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error("Failed to process the email template: " + e.getMessage());
			return null;
		}
	}
}