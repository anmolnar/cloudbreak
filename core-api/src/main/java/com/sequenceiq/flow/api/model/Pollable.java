package com.sequenceiq.flow.api.model;

/**
 * Provides information to track a long running process running in flow.
 *
 * <strong>Tracking a flow chain</strong>
 *
 * If both the flowId and the flowChainId are present, than the running process is a flowChain and should be tracked based on the flowChainId.
 *
 * <strong>Tracking a flow</strong>
 *
 * If only the flowId is present and the flowChainId is null then the process is running in a simple flow and can be tracked based on the flowId.
 */
public interface Pollable {

    String getFlowId();

    String getFlowChainId();
}
