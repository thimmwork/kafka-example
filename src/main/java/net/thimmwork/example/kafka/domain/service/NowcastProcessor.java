package net.thimmwork.example.kafka.domain.service;

import net.thimmwork.example.kafka.adapter.json.in.NowcastWarning;
import net.thimmwork.example.kafka.adapter.json.in.NowcastWarningsUpdatedMessage;
import net.thimmwork.example.kafka.domain.model.WarningEntity;
import net.thimmwork.example.kafka.port.out.WarningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@Transactional
public class NowcastProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowcastProcessor.class);

    private final WarningRepository warningRepository;

    public NowcastProcessor(WarningRepository warningRepository) {
        this.warningRepository = warningRepository;
    }

    public Void onWarningUpdated(NowcastWarningsUpdatedMessage warningsUpdated) {
        for (NowcastWarning warning : warningsUpdated.getWarnings()) {
            WarningEntity persistedWarning = warningRepository.findById(warning.getWarnId())
                    .orElseGet(() -> new WarningEntity(warning));
            warningRepository.save(persistedWarning);
        }
        return null;
    }
}
