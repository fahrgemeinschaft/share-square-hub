package org.sharesquare.hub.model.data;

import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
@Entity
@Table(name = "target_system")
public class EntityTargetSystem extends BaseEntity {

	private String name;

	private String description;

	@Column(name = "vanity_url")
	private URL vanityUrl;

	@Column(name = "content_language")
	private String contentLanguage;

	@Column(name = "data_protection_regulations")
	private String dataProtectionRegulations;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "connector")
	private EntityConnector connector;

	private boolean active;
}
