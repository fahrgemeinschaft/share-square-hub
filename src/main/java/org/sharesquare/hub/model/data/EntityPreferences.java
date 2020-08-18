package org.sharesquare.hub.model.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ManyToAny;
import org.sharesquare.hub.model.data.preferences.EntityPreference;
import org.sharesquare.model.preferences.PaxGenderValues;
import org.sharesquare.model.preferences.PaxPetsValues;
import org.sharesquare.model.preferences.PaxSmokerValues;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "preferences")
public class EntityPreferences extends BaseEntity {

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_boolean_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "boolean_preference_id"))
	private List<EntityPreference<Boolean>> booleanPreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_double_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "double_preference_id"))
	private List<EntityPreference<Double>> doublePreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_integer_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "integer_preference_id"))
	private List<EntityPreference<Integer>> integerPreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_pax_gender_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "pax_gender_preference_id"))
	private List<EntityPreference<PaxGenderValues>> paxGenderPreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_pax_pets_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "pax_pets_preference_id"))
	private List<EntityPreference<PaxPetsValues>> paxPetsPreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_pax_smoker_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "pax_smoker_preference_id"))
	private List<EntityPreference<PaxSmokerValues>> paxSmokerPreferences;

	@ManyToAny(metaDef = "PreferenceMetaDef",
		metaColumn = @Column(name = "preference_type"),
		fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "preferences_string_preference_membership",
		joinColumns = @JoinColumn(name = "preferences_id"),
		inverseJoinColumns = @JoinColumn(name = "string_preference_id"))
	private List<EntityPreference<String>> stringPreferences;
}
