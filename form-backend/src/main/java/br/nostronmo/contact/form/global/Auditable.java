package br.nostronmo.contact.form.global;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private ZonedDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private ZonedDateTime updatedAt;

	@Version
	@Column(name = "version", nullable = false)
	private Long version = 0L;

}
