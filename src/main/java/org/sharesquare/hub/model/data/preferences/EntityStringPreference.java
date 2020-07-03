package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.sharesquare.hub.model.data.EntityPreference;
import org.sharesquare.model.Preference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "string_preference")
public class EntityStringPreference extends EntityPreference<String> {

	public EntityStringPreference(Preference<?> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = (String) preference.getValue();
	}

	private String value;
}
