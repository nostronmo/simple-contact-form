package br.nostronmo.contact.form.repository;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.nostronmo.contact.form.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {

	@Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.name = :name AND c.phone = :phone AND c.createdAt > :time")
	boolean existsByVariablesAfterTime(@Param("name") String name, @Param("phone") String phone,
			@Param("time") ZonedDateTime time);
}
