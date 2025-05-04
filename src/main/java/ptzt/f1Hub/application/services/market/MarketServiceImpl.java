package ptzt.f1Hub.application.services.market;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.instraestructure.repository.MarketRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService{

    private final MarketRepository marketRepository;

    @Transactional
    @Override
    public Market create(Market market) {

        return marketRepository.save(market);

    }

    @Transactional
    @Override
    public Market update(Market market) {

        return marketRepository.save(market);

    }

    @Override
    public Optional<Market> getById(Long id) {

        return marketRepository.findById(id);

    }

}
