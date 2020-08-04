package org.sharesquare.hub.model.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "connector")
public class EntityConnector extends BaseEntity {

	@Column(name = "offer_update_webhook")
	private String offerUpdateWebhook;

	@Column(name = "alive_check_webhook")
	private String aliveCheckWebhook;

	private String apikey;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "connector_client_membership",
	           joinColumns = @JoinColumn(name = "connector_id"),
	           inverseJoinColumns = @JoinColumn(name = "client_id"))
	private List<EntityClient> clients;
}
