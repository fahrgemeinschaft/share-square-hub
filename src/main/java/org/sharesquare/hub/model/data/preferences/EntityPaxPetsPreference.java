package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.model.preferences.PaxPetsValues;
import org.sharesquare.model.preferences.Preference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "pax_pets_preference")
public class EntityPaxPetsPreference extends EntityPreference<PaxPetsValues> {

	public EntityPaxPetsPreference(Preference<PaxPetsValues> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = preference.getValue();
	}

	@Enumerated(EnumType.STRING)
	private PaxPetsValues value;
}
