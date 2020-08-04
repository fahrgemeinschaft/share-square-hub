package org.sharesquare.hub.model.data;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@IdClass(EntityOfferTargetStatusKey.class)
@Table(name = "offer_target_status")
public class EntityOfferTargetStatus {

	@Id
	@Column(name = "offer_id")
	private UUID offerId;

	@Id
	@Column(name = "target_system_id")
	private UUID targetSystemId;

	public enum Status {
		PROCESSING,
		FAILED,
		SUCCESS
	}

	@Enumerated(EnumType.STRING)
	private Status status = Status.PROCESSING;
}
