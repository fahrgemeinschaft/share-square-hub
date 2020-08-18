package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.sharesquare.model.preferences.Preference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "string_preference")
public class EntityStringPreference extends EntityPreference<String> {

	public EntityStringPreference(Preference<String> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = preference.getValue();
	}

	private String value;
}
