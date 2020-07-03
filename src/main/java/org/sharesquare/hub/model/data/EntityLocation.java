package org.sharesquare.hub.model.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.model.Location;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "location")
public class EntityLocation extends BaseEntity {

	private double latitude;

	private double longitude;

	private String name;

	@Enumerated(EnumType.STRING)
	private Location.type type;
}
