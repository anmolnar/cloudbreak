package com.sequenceiq.flow.api.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FlowStartResponse implements Pollable {

    public static final FlowStartResponse IMMEDIATE = new FlowStartResponse("IMMEDIATE", "IMMEDIATE");

    private final String flowId;

    private final String flowChainId;

    @JsonCreator
    public FlowStartResponse(@JsonProperty("flowId") String flowId,
            @JsonProperty("flowChainId") String flowChainId) {
        this.flowId = flowId;
        this.flowChainId = flowChainId;
    }

    public static FlowStartResponse of(String flowId, String flowChainId) {
        return new FlowStartResponse(flowId, flowChainId);
    }

    public String getFlowId() {
        return flowId;
    }

    @Override
    public String getFlowChainId() {
        return flowChainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlowStartResponse that = (FlowStartResponse) o;
        return Objects.equals(flowId, that.flowId) &&
                Objects.equals(flowChainId, that.flowChainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowId, flowChainId);
    }
}
