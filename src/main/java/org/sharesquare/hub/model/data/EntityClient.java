package org.sharesquare.hub.model.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "client")
public class EntityClient extends BaseEntity {

	private String name;

	private String authkey;
}
