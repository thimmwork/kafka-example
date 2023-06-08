package net.thimmwork.example.kafka.domain.service;

import net.thimmwork.example.kafka.domain.model.OvenTemperatureMessage;
import net.thimmwork.example.kafka.port.out.OvenControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@Transactional
public class OvenTemperatureChangedProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OvenTemperatureChangedProcessor.class);

    private final OvenControl ovenControl;
    private final int desiredTemperatureInCelsius;
    private final long maxAgeInSeconds;

    public OvenTemperatureChangedProcessor(OvenControl ovenControl,
                                           @Value("${net.thimmwork.oven.desired-temperature-in-celsius:180}")
                                           int desiredTemperatureInCelsius,
                                           @Value("${net.thimmwork.oven.meter.max-meter-age-in-seconds:600}")
                                           long maxAgeInMinutes) {
        this.ovenControl = ovenControl;
        this.desiredTemperatureInCelsius = desiredTemperatureInCelsius;
        this.maxAgeInSeconds = maxAgeInMinutes;
    }

    public Void onTemperatureChange(OvenTemperatureMessage ovenTemperatureMessage) {
        if (ovenTemperatureMessage.getTimestamp().isBefore(Instant.now().minusSeconds(maxAgeInSeconds))) {
            LOGGER.debug("Skipped old temperature message for ID {} with timestamp {}",
                    ovenTemperatureMessage.getUuid(), ovenTemperatureMessage.getTimestamp());
        }
        if (desiredTemperatureInCelsius > ovenTemperatureMessage.getTemperatureInCelsius()) {
            ovenControl.heatUp();
        }
        if (desiredTemperatureInCelsius < ovenTemperatureMessage.getTemperatureInCelsius()) {
            ovenControl.stopHeating();
        }
        return null;
    }
}
