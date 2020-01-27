package com.sequenceiq.it.cloudbreak.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.SdxClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.action.flow.EnvironmentFlowLogsListAction;
import com.sequenceiq.it.cloudbreak.action.flow.SdxFlowLogsListAction;
import com.sequenceiq.it.cloudbreak.action.flow.StackFlowLogsListAction;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.dto.sdx.SdxTestDto;
import com.sequenceiq.it.cloudbreak.dto.stack.StackTestDto;

@Service
public class FlowTestClient {

    public Action<EnvironmentTestDto, EnvironmentClient> getFlowLogsByEnvironmentNameAndChainId() {
        return new EnvironmentFlowLogsListAction();
    }

    public Action<SdxTestDto, SdxClient> getFlowLogsBySdxNameAndChainId() {
        return new SdxFlowLogsListAction();
    }

    public Action<StackTestDto, CloudbreakClient> getFlowLogsByDistroxNameAndChainId() {
        return new StackFlowLogsListAction();
    }
}
