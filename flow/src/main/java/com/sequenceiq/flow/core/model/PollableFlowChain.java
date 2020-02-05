package com.sequenceiq.flow.core.model;

import java.util.Objects;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;

public class PollableFlowChain implements AcceptResult {

    private final String flowChainId;

    public PollableFlowChain(String flowChainId) {
        this.flowChainId = Objects.requireNonNull(flowChainId);
    }

    public String getFlowChainId() {
        return flowChainId;
    }
}
