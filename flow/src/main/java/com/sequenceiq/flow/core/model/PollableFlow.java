package com.sequenceiq.flow.core.model;

import java.util.Objects;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;

public class PollableFlow implements AcceptResult {

    private final String flowId;

    public PollableFlow(String flowId) {
        this.flowId = Objects.requireNonNull(flowId);
    }

    public String getFlowId() {
        return flowId;
    }
}
