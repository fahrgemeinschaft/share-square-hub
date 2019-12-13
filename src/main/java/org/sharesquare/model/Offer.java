package org.sharesquare.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sharesquare.ShareSquareObject;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class Offer  extends ShareSquareObject {

    private String userId;

    private Location origin;
    private Location destination;

    private List<ContactOption>  contactOptions;

    private List<String> targetPlatforms;

    private List<Preference> preferences;

    private String additionalInfo;




}
