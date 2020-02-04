package com.sequenceiq.flow.controller;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;

import com.google.api.client.util.Lists;
import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.flow.api.FlowEndpoint;
import com.sequenceiq.flow.api.model.FlowCheckResponse;
import com.sequenceiq.flow.api.model.FlowLogResponse;
import com.sequenceiq.flow.domain.FlowChainLog;
import com.sequenceiq.flow.domain.FlowLog;
import com.sequenceiq.flow.service.flowlog.FlowChainLogService;
import com.sequenceiq.flow.service.flowlog.FlowLogDBService;

@Controller
public class FlowController implements FlowEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);

    @Inject
    private FlowLogDBService flowLogDBService;

    @Inject
    private FlowChainLogService flowChainLogService;

    @Inject
    @Named("conversionService")
    private ConversionService conversionService;

    @Override
    public FlowLogResponse getLastFlowById(String flowId) {
        Optional<FlowLog> lastFlowLog = flowLogDBService.getLastFlowLog(flowId);
        if (lastFlowLog.isPresent()) {
            return conversionService.convert(lastFlowLog.get(), FlowLogResponse.class);
        }
        throw new BadRequestException("Not found flow for this flow id!");
    }

    @Override
    public List<FlowLogResponse> getFlowLogsByFlowId(String flowId) {
        List<FlowLog> flowLogs = flowLogDBService.findAllByFlowIdOrderByCreatedDesc(flowId);
        return flowLogs.stream().map(flowLog -> conversionService.convert(flowLog, FlowLogResponse.class)).collect(Collectors.toList());
    }

    @Override
    public FlowLogResponse getLastFlowByResourceName(String resourceName) {
        checkState(!Crn.isCrn(resourceName));
        return conversionService.convert(flowLogDBService.getLastFlowLogByResourceCrnOrName(resourceName), FlowLogResponse.class);
    }

    @Override
    public FlowLogResponse getLastFlowByResourceCrn(String resourceCrn) {
        checkState(Crn.isCrn(resourceCrn));
        return conversionService.convert(flowLogDBService.getLastFlowLogByResourceCrnOrName(resourceCrn), FlowLogResponse.class);
    }

    @Override
    public List<FlowLogResponse> getFlowLogsByResourceCrn(String resourceCrn) {
        checkState(Crn.isCrn(resourceCrn));
        List<FlowLog> flowLogs = flowLogDBService.getFlowLogsByResourceCrnOrName(resourceCrn);
        return flowLogs.stream().map(flowLog -> conversionService.convert(flowLog, FlowLogResponse.class)).collect(Collectors.toList());
    }

    @Override
    public List<FlowLogResponse> getFlowLogsByResourceName(String resourceName) {
        checkState(!Crn.isCrn(resourceName));
        List<FlowLog> flowLogs = flowLogDBService.getFlowLogsByResourceCrnOrName(resourceName);
        return flowLogs.stream().map(flowLog -> conversionService.convert(flowLog, FlowLogResponse.class)).collect(Collectors.toList());
    }

    @Override
    public List<FlowLogResponse> getFlowLogsByResourceNameAndChainId(String resourceName, String chainId) {
        Optional<FlowChainLog> firstByFlowChainIdOrderByCreatedDesc = flowChainLogService.findFirstByFlowChainIdOrderByCreatedDesc(chainId);
        if (firstByFlowChainIdOrderByCreatedDesc.isPresent()) {
            List<FlowChainLog> relatedChains = flowChainLogService.collectRelatedFlowChains(Lists.newArrayList(), firstByFlowChainIdOrderByCreatedDesc.get());
            List<FlowLog> flowLogs = flowLogDBService.getFlowLogsByResourceAndChainId(resourceName,
                    relatedChains.stream().map(flowChainLog -> flowChainLog.getFlowChainId()).collect(Collectors.toList()));
            return flowLogs.stream().map(flowLog -> conversionService.convert(flowLog, FlowLogResponse.class)).collect(Collectors.toList());
        }
        throw new NotFoundException("FlowChain not found by this flowChainId!");
    }

    @Override
    public FlowCheckResponse hasFlowRunning(String resourceName, String chainId) {
        Optional<FlowChainLog> firstByFlowChainIdOrderByCreatedDesc = flowChainLogService.findFirstByFlowChainIdOrderByCreatedDesc(chainId);
        if (firstByFlowChainIdOrderByCreatedDesc.isPresent()) {
            List<FlowChainLog> relatedChains = flowChainLogService.collectRelatedFlowChains(Lists.newArrayList(), firstByFlowChainIdOrderByCreatedDesc.get());
            List<FlowLog> relatedFlowLogs = flowLogDBService.getFlowLogsByResourceAndChainId(resourceName,
                    relatedChains.stream().map(flowChainLog -> flowChainLog.getFlowChainId()).collect(Collectors.toList()));
            FlowCheckResponse flowCheckResponse = new FlowCheckResponse();
            flowCheckResponse.setFlowChainId(chainId);
            flowCheckResponse.setHasActiveFlow(flowChainLogService.checkIfAnyFlowChainHasEventInQueue(relatedChains) ||
                    flowLogDBService.hasPendingFlowEvent(relatedFlowLogs));
            return flowCheckResponse;
        }
        throw new NotFoundException("FlowChain not found by this flowChainId!");
    }
}
