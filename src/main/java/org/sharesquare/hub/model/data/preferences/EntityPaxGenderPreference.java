package org.sharesquare.hub.model.data.preferences;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sharesquare.model.preferences.PaxGenderValues;
import org.sharesquare.model.preferences.Preference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "pax_gender_preference")
public class EntityPaxGenderPreference extends EntityPreference<PaxGenderValues> {

	public EntityPaxGenderPreference(Preference<PaxGenderValues> preference) {
		super.setId(preference.getId());
		super.setKey(preference.getKey());
		this.value = preference.getValue();
	}

	@Enumerated(EnumType.STRING)
	private PaxGenderValues value;
}
