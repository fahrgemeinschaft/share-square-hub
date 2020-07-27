package org.sharesquare.hub.model.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "connector")
public class EntityConnector extends BaseEntity {

	@Column(name = "offer_update_webhook")
	private URL offerUpdateWebhook;

	@Column(name = "alive_check_webhook")
	private URL aliveCheckWebhook;
}
