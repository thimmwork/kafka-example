package net.thimmwork.example.kafka.adapter.json.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NowcastWarningsUpdatedMessage {
    private Long time;
    private List<NowcastWarning> warnings;
    private String binnenSee;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public NowcastWarningsUpdatedMessage(@JsonProperty("time") Long time,
                                         @JsonProperty("warnings") List<NowcastWarning> warnings,
                                         @JsonProperty("binnenSee") String binnenSee) {
        this.time = time;
        this.warnings = warnings;
        this.binnenSee = binnenSee;
    }

    public Long getTime() {
        return time;
    }

    public List<NowcastWarning> getWarnings() {
        return warnings;
    }

    public String getBinnenSee() {
        return binnenSee;
    }
}
