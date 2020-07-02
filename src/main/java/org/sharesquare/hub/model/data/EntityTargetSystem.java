package org.sharesquare.hub.model.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "target_system")
public class EntityTargetSystem extends BaseEntity {
}
