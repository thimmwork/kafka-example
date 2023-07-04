package net.thimmwork.example.kafka.adapter.json.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class NowcastWarning {
    private String warnId;
    private int type;
    private int level;
    private Long start;
    private Long end;
    private List<Integer> states = new ArrayList<>();
    private List<Region> regions = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private Boolean bn;
    @JsonProperty("isVorabinfo")
    private Boolean vorabinfo;
    private String instruction;
    private String description;
    private String descriptionText;
    private String event;
    private String headLine;

    public NowcastWarning() {
    }

    public NowcastWarning(String warnId,
                          int type,
                          int level,
                          Long start,
                          Long end,
                          List<Integer> states,
                          List<Region> regions,
                          List<String> urls,
                          Boolean bn,
                          Boolean isVorabinfo,
                          String instruction,
                          String description,
                          String descriptionText,
                          String event,
                          String headLine) {
        this.warnId = warnId;
        this.type = type;
        this.level = level;
        this.start = start;
        this.end = end;
        this.states = states;
        this.regions = regions;
        this.urls = urls;
        this.bn = bn;
        this.vorabinfo = isVorabinfo;
        this.instruction = instruction;
        this.description = description;
        this.descriptionText = descriptionText;
        this.event = event;
        this.headLine = headLine;
    }

    public NowcastWarning(String warnId, Long start, Long end, List<Region> regions) {
        this.warnId = warnId;
        this.start = start;
        this.end = end;
        this.regions = regions;
    }

    public String getWarnId() {
        return warnId;
    }

    public int getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public List<Integer> getStates() {
        return states;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public List<String> getUrls() {
        return urls;
    }

    public Boolean getBn() {
        return bn;
    }

    public Boolean getVorabinfo() {
        return vorabinfo;
    }

    public String getInstruction() {
        return instruction;
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
