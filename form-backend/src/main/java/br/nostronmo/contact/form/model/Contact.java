package br.nostronmo.contact.form.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import br.nostronmo.contact.form.global.Auditable;
import br.nostronmo.contact.form.global.constraints.ContactConstraints;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "contact", indexes = { @Index(name = "index_contact_name", columnList = "name"), })
public class Contact extends Auditable {

	@Id
	@ToString.Include
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "name", length = ContactConstraints.MAX_NAME_LENGTH, nullable = false)
	private String name;

	@Column(name = "phone", length = ContactConstraints.MAX_PHONE_LENGTH, nullable = false)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ContactStatus status;

	@Column(name = "sent_time")
	private ZonedDateTime sentTime;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content", length = ContactConstraints.MAX_CONTENT_LENGTH, nullable = true, unique = false, columnDefinition = "BYTEA")
	private byte[] content;

}
