package net.thimmwork.example.kafka.domain.model;

import net.thimmwork.example.kafka.adapter.json.in.NowcastWarning;
import org.springframework.data.annotation.PersistenceCreator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity(name = "warning")
public class WarningEntity {
    @Id
    private String warnId;

    private String description;
    private String descriptionText;
    private String event;
    private String headLine;

    private Instant endTime;

    @PersistenceCreator
    public WarningEntity() {
    }

    public WarningEntity(NowcastWarning warning) {
        this.warnId = warning.getWarnId();
        this.description = warning.getDescription();
        this.descriptionText = warning.getDescriptionText();
        this.event = warning.getEvent();
        this.headLine = warning.getHeadLine();
        this.endTime = Instant.ofEpochMilli(warning.getEnd());
    }

    public String getWarnId() {
        return warnId;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public String getEvent() {
        return event;
    }

    public String getHeadLine() {
        return headLine;
    }
}
