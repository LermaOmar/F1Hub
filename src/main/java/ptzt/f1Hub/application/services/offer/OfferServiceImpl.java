package ptzt.f1Hub.application.services.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.repository.OfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService{

    private final OfferRepository offerRepository;

    @Override
    public List<Offer> getAll() {

        return offerRepository.findAll();

    }
}
