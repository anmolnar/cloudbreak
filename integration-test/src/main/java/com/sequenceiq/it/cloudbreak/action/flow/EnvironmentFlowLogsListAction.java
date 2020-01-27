package com.sequenceiq.it.cloudbreak.action.flow;

import com.sequenceiq.it.cloudbreak.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;

public class EnvironmentFlowLogsListAction implements Action<EnvironmentTestDto, EnvironmentClient> {

    @Override
    public EnvironmentTestDto action(TestContext testContext, EnvironmentTestDto testDto, EnvironmentClient client) throws Exception {
        testDto.setLastKnownFlowLogs(client.getEnvironmentClient()
            .flowEndpoint()
            .getFlowLogsByResourceNameAndChainId(testDto.getName(), testDto.getLastKnownFlowChainId()));
        return testDto;
    }
}
