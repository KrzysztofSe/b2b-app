package b2b.service;

import b2b.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static b2b.model.BasketStatus.EXPIRED;

@Service
public class ExpirationService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpirationService.class);

    private final int thresholdMinutes;
    private final BasketRepository basketRepository;

    @Autowired
    public ExpirationService(@Value("${basket.expiration.older.than.minutes:30}") int thresholdMinutes,
                             BasketRepository basketRepository) {
        this.thresholdMinutes = thresholdMinutes;
        this.basketRepository = basketRepository;
    }

    @Scheduled(fixedRateString = "#{new Integer(${basket.expiration.rate.minutes:5})*60000}")
    public void expireAbandonedBaskets() {
        LOG.info("Looking for expiring baskets");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(thresholdMinutes);
        long count = basketRepository.setStatusForOlderThan(threshold, EXPIRED);
        LOG.info("Expired baskets count: {}", count);
    }

}
