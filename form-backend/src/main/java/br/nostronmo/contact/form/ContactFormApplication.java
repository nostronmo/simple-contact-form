package br.nostronmo.contact.form;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import br.nostronmo.contact.form.config.CustomBanner;

@EnableAsync
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class ContactFormApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ContactFormApplication.class);
		app.setBanner(new CustomBanner());
		app.run(args);
	}

}
