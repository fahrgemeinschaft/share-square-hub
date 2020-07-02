package org.sharesquare.hub.model.data.preferences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.sharesquare.hub.model.data.EntityPreference;
import org.sharesquare.model.Preference;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "double_preference")
public class EntityDoublePreference extends EntityPreference<Double> {

	public EntityDoublePreference(Preference<?> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = (Double) preference.getValue();
	}

	private Double value;
}
