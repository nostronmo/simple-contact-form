package br.nostronmo.contact.form.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import br.nostronmo.contact.form.model.Contact;
import br.nostronmo.contact.form.model.ContactStatus;
import jakarta.persistence.criteria.Predicate;

@Component
public class ContactSpecification {

	public static Specification<Contact> findByCriteria(String name, String phone, ContactStatus status) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), status));
			}

			if (name != null && !name.trim().isEmpty()) {
				predicates.add(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
			}

			if (phone != null && !phone.trim().isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("phone"), "%" + phone + "%"));
			}

			return predicates.isEmpty() ? criteriaBuilder.conjunction()
					: criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
