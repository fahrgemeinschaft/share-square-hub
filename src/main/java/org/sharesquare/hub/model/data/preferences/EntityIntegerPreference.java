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
@Table(name = "integer_preference")
public class EntityIntegerPreference extends EntityPreference<Integer> {

	public EntityIntegerPreference(Preference<Integer> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = preference.getValue();
	}

	private Integer value;
}
