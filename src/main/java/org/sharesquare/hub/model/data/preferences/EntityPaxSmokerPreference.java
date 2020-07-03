package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.hub.model.data.EntityPreference;
import org.sharesquare.model.Preference;
import org.sharesquare.model.preferences.PaxSmokerValues;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "pax_smoker_preference")
public class EntityPaxSmokerPreference extends EntityPreference<PaxSmokerValues> {

	public EntityPaxSmokerPreference(Preference<?> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = (PaxSmokerValues) preference.getValue();
	}

	@Enumerated(EnumType.STRING)
	private PaxSmokerValues value;
}
