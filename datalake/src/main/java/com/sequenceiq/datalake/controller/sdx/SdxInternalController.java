package com.sequenceiq.datalake.controller.sdx;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.service.sdx.SdxService;
import com.sequenceiq.flow.api.model.FlowStartResponse;
import com.sequenceiq.sdx.api.endpoint.SdxInternalEndpoint;
import com.sequenceiq.sdx.api.model.SdxClusterResponse;
import com.sequenceiq.sdx.api.model.SdxInternalClusterRequest;

@Controller
public class SdxInternalController implements SdxInternalEndpoint {

    @Inject
    private SdxService sdxService;

    @Inject
    private SdxClusterConverter sdxClusterConverter;

    @Override
    public SdxClusterResponse create(String name, @Valid SdxInternalClusterRequest createSdxClusterRequest) {
        String userCrn = ThreadBasedUserCrnProvider.getUserCrn();
        Pair<SdxCluster, FlowStartResponse> result = sdxService.createSdx(userCrn, name, createSdxClusterRequest, createSdxClusterRequest.getStackV4Request());
        SdxCluster sdxCluster = result.getLeft();
        SdxClusterResponse sdxClusterResponse = sdxClusterConverter.sdxClusterToResponse(sdxCluster);
        sdxClusterResponse.setName(sdxCluster.getClusterName());
        sdxClusterResponse.setFlowChainId(result.getRight().getFlowChainId());
        return sdxClusterResponse;
    }
}
