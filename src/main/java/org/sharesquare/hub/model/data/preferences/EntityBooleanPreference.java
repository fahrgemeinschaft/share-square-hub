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
@Table(name = "boolean_preference")
public class EntityBooleanPreference extends EntityPreference<Boolean> {

	public EntityBooleanPreference(Preference<?> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = (Boolean) preference.getValue();
	}

	private Boolean value;
}
