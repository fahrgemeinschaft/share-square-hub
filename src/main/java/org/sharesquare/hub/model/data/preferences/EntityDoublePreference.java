package org.sharesquare.hub.model.data.preferences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.sharesquare.model.preferences.Preference;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "double_preference")
public class EntityDoublePreference extends EntityPreference<Double> {

	public EntityDoublePreference(Preference<Double> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = preference.getValue();
	}

	private Double value;
}
