package com.sequenceiq.it.cloudbreak.action.flow;

import com.sequenceiq.it.cloudbreak.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.stack.StackTestDto;

public class StackFlowLogsListAction implements Action<StackTestDto, CloudbreakClient> {
    @Override
    public StackTestDto action(TestContext testContext, StackTestDto testDto, CloudbreakClient client) throws Exception {
        testDto.setLastKnownFlowLogs(client.getCloudbreakClient()
                .flowEndpoint()
                .getFlowLogsByResourceNameAndChainId(testDto.getName(), testDto.getLastKnownFlowChainId()));
        return testDto;
    }
}
