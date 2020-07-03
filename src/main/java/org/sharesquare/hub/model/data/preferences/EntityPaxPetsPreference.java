package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.hub.model.data.EntityPreference;
import org.sharesquare.model.Preference;
import org.sharesquare.model.preferences.PaxPetsValues;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "pax_pets_preference")
public class EntityPaxPetsPreference extends EntityPreference<PaxPetsValues> {

	public EntityPaxPetsPreference(Preference<?> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = (PaxPetsValues) preference.getValue();
	}

	@Enumerated(EnumType.STRING)
	private PaxPetsValues value;
}
