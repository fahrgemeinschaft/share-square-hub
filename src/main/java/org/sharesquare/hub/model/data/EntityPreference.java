package org.sharesquare.hub.model.data;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class EntityPreference<T> {

	@Id
	private UUID id;

	private String key;

	public abstract T getValue();
}
