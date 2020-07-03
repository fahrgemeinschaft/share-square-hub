package org.sharesquare.hub.model.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.ManyToAny;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "offer")
public class EntityOffer extends BaseEntity {

	@Column(name = "user_id")
	private String userId;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "start_timezone")
	private ZoneId startTimezone;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "origin")
	private EntityLocation origin;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "destination")
	private EntityLocation destination;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "offer_contact_option_membership",
	           joinColumns = @JoinColumn(name = "offer_id"),
	           inverseJoinColumns = @JoinColumn(name = "contact_option_id"))
	private List<EntityContactOption> contactOptions;

	@ElementCollection
	@CollectionTable(name = "offer_target_system_membership",
	                 joinColumns = @JoinColumn(name = "offer_id"))
	@LazyCollection(LazyCollectionOption.FALSE)
	@Column(name = "target_system_id")
	private List<UUID> targetPlatforms;

	@ManyToAny(metaDef = "PreferenceMetaDef",
			   metaColumn = @Column(name = "preference_type"),
			   fetch = FetchType.EAGER)
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	@JoinTable(name = "offer_preference_membership",
	           joinColumns = @JoinColumn(name = "offer_id"),
	           inverseJoinColumns = @JoinColumn(name = "preference_id"))
	private List<EntityPreference<?>> preferences;

	@Column(name = "additional_info")
	private String additionalInfo;
}