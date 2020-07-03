package org.sharesquare.hub.model.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.model.ContactOption.ContactType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contact_option")
public class EntityContactOption extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "contact_type")
	private ContactType contactType;

	@Column(name = "contact_identifier")
	private String contactIdentifier;
}
