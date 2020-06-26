package org.sharesquare.hub.service;


import org.sharesquare.model.Connector;
import org.sharesquare.model.Offer;
import org.sharesquare.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OfferService {

    @Autowired
    private IRepository<Offer> offerRepository;

    @Autowired
    private ConnectorService connectorService;

    /**
     * Persist offer locally, then trigger connectors to forward to other portals.
     * @param offer
     * @return the created offer with an ID set.
     */
    public Optional<Offer> create(Offer offer){
        //any uuid will do.
        if(offer.getId()==null){
            offer.setId(UUID.randomUUID());
        }
        final Optional<Offer> result = offerRepository.create(offer);
        if(result.isPresent()) {
            connectorService.updateOffer(result.get());
        }
        return result;
    }
    //TODO: implement CRUD operations
    //TODO: implement findMany (call through repository)

	public Offer getOffer(UUID id) {
		String idString = id.toString();
		Optional<Offer> offer = offerRepository.findById(idString);
		if (offer.isPresent()) {
			return offer.get();
		}
		return null;
	}

    public boolean updateOffer(UUID id, Offer offer) {
		offer.setId(id);
		Optional<Offer> previousOffer = offerRepository.update(offer);
		if (previousOffer.isPresent()) {
			return true;
		}
		return false;
	}

    public boolean deleteOffer(UUID id) {
    	String idString = id.toString();
    	if (offerRepository.findById(idString).isPresent()) {
    		offerRepository.deleteById(idString);
    		return true;
    	}
    	return false;
    }
}
