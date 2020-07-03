@AnyMetaDef(name= "PreferenceMetaDef",
            metaType = "string",
            idType = "java.util.UUID",
            metaValues = {
            		@MetaValue(value = "boolean", targetEntity = EntityBooleanPreference.class),
            		@MetaValue(value = "double", targetEntity = EntityDoublePreference.class),
            		@MetaValue(value = "integer", targetEntity = EntityIntegerPreference.class),
            		@MetaValue(value = "pax_gender", targetEntity = EntityPaxGenderPreference.class),
            		@MetaValue(value = "pax_pets", targetEntity = EntityPaxPetsPreference.class),
            		@MetaValue(value = "pax_smoker", targetEntity = EntityPaxSmokerPreference.class),
            		@MetaValue(value = "string", targetEntity = EntityStringPreference.class),
            }
)
package org.sharesquare.hub.model.data;

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import org.sharesquare.hub.model.data.preferences.*;
