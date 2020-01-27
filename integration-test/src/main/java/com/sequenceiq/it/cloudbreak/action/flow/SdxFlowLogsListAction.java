package com.sequenceiq.it.cloudbreak.action.flow;

import com.sequenceiq.it.cloudbreak.SdxClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.sdx.SdxTestDto;

public class SdxFlowLogsListAction implements Action<SdxTestDto, SdxClient> {

    @Override
    public SdxTestDto action(TestContext testContext, SdxTestDto testDto, SdxClient client) throws Exception {
        testDto.setLastKnownFlowLogs(client.getSdxClient()
                .flowEndpoint()
                .getFlowLogsByResourceNameAndChainId(testDto.getName(), testDto.getLastKnownFlowChainId()));
        return testDto;
    }
}
