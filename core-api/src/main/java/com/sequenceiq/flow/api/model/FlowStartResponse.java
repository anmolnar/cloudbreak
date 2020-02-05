package com.sequenceiq.flow.api.model;

import java.util.Objects;

public class FlowStartResponse implements Pollable {

    private String flowId;

    private String flowChainId;

    public FlowStartResponse() {
    }

    public FlowStartResponse(String flowId, String flowChainId) {
        this.flowId = flowId;
        this.flowChainId = flowChainId;
    }

    public static FlowStartResponse of(String flowId, String flowChainId) {
        return new FlowStartResponse(flowId, flowChainId);
    }

    public static FlowStartResponse autoAccepted() {
        return new FlowStartResponse("AUTO_ACCEPTED", "AUTO_ACCEPTED");
    }

    @Override
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Override
    public String getFlowChainId() {
        return flowChainId;
    }

    public void setFlowChainId(String flowChainId) {
        this.flowChainId = flowChainId;
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
