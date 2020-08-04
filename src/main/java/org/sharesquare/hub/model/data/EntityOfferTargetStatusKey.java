package org.sharesquare.hub.model.data;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class EntityOfferTargetStatusKey implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID offerId;

	private UUID targetSystemId;
}
