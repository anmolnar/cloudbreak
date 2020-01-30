package com.sequenceiq.cloudbreak.controller.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Controller;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.StackType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.dto.NameOrCrn;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.StackV4Endpoint;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.ClusterRepairV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.MaintenanceModeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackImageChangeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackScaleV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.UpdateClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.UserNamePasswordV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.GeneratedBlueprintV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.RetryableFlowResponse;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.RetryableFlowResponse.Builder;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackStatusV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackViewV4Responses;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.UpgradeOptionV4Response;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.workspace.controller.WorkspaceEntityType;
import com.sequenceiq.distrox.v1.distrox.StackOperations;
import com.sequenceiq.flow.api.model.FlowStartResponse;

@Controller
@WorkspaceEntityType(Stack.class)
public class StackV4Controller extends NotificationController implements StackV4Endpoint {

    @Inject
    private StackOperations stackOperations;

    @Override
    public StackViewV4Responses list(Long workspaceId, String environmentCrn, boolean onlyDatalakes) {
        List<StackType> types = new ArrayList<>();
        if (onlyDatalakes) {
            types.add(StackType.DATALAKE);
        } else {
            types.add(StackType.DATALAKE);
            types.add(StackType.WORKLOAD);
        }
        return stackOperations.listByEnvironmentCrn(workspaceId, environmentCrn, types);
    }

    @Override
    public StackV4Response post(Long workspaceId, @Valid StackV4Request request) {
        return stackOperations.post(workspaceId, request);
    }

    @Override
    public StackV4Response get(Long workspaceId, String name, Set<String> entries) {
        return stackOperations.get(NameOrCrn.ofName(name), workspaceId, entries, null);
    }

    @Override
    public FlowStartResponse delete(Long workspaceId, String name, boolean forced) {
        return stackOperations.delete(NameOrCrn.ofName(name), workspaceId, forced);
    }

    @Override
    public void sync(Long workspaceId, String name) {
        stackOperations.sync(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public FlowStartResponse retry(Long workspaceId, String name) {
        return stackOperations.retry(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public List<RetryableFlowResponse> listRetryableFlows(Long workspaceId, String name) {
        return stackOperations.getRetryableFlows(name, workspaceId)
                .stream().map(retryable -> Builder.builder().setName(retryable.getName()).setFailDate(retryable.getFailDate()).build())
                .collect(Collectors.toList());
    }

    @Override
    public FlowStartResponse putStop(Long workspaceId, String name) {
        return stackOperations.putStop(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public FlowStartResponse putStart(Long workspaceId, String name) {
        return stackOperations.putStart(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public FlowStartResponse putScaling(Long workspaceId, String name, @Valid StackScaleV4Request updateRequest) {
        return stackOperations.putScaling(NameOrCrn.ofName(name), workspaceId, updateRequest);
    }

    @Override
    public FlowStartResponse repairCluster(Long workspaceId, String name, @Valid ClusterRepairV4Request clusterRepairRequest) {
        return stackOperations.repairCluster(NameOrCrn.ofName(name), workspaceId, clusterRepairRequest);
    }

    @Override
    public FlowStartResponse upgradeCluster(Long workspaceId, String name) {
        return stackOperations.upgradeCluster(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public UpgradeOptionV4Response checkForUpgrade(Long workspaceId, String name) {
        return stackOperations.checkForUpgrade(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public GeneratedBlueprintV4Response postStackForBlueprint(Long workspaceId, String name, @Valid StackV4Request stackRequest) {
        return stackOperations.postStackForBlueprint(NameOrCrn.ofName(name), workspaceId, stackRequest);
    }

    @Override
    public FlowStartResponse changeImage(Long workspaceId, String name, @Valid StackImageChangeV4Request stackImageChangeRequest) {
        return stackOperations.changeImage(NameOrCrn.ofName(name), workspaceId, stackImageChangeRequest);
    }

    @Override
    public FlowStartResponse deleteWithKerberos(Long workspaceId, String name, boolean forced) {
        return stackOperations.delete(NameOrCrn.ofName(name), workspaceId, forced);
    }

    @Override
    public StackV4Request getRequestfromName(Long workspaceId, String name) {
        return stackOperations.getRequest(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public StackStatusV4Response getStatusByName(Long workspaceId, String name) {
        return stackOperations.getStatus(NameOrCrn.ofName(name), workspaceId);
    }

    @Override
    public FlowStartResponse deleteInstance(Long workspaceId, String name, boolean forced, String instanceId) {
        return stackOperations.deleteInstance(NameOrCrn.ofName(name), workspaceId, forced, instanceId);
    }

    @Override
    public FlowStartResponse deleteMultipleInstances(Long workspaceId, String name, List<String> instanceIds, boolean forced) {
        return stackOperations.deleteInstances(NameOrCrn.ofName(name), workspaceId, instanceIds, forced);
    }

    @Override
    public FlowStartResponse putPassword(Long workspaceId, String name, @Valid UserNamePasswordV4Request userNamePasswordJson) {
        return stackOperations.putPassword(NameOrCrn.ofName(name), workspaceId, userNamePasswordJson);
    }

    @Override
    public FlowStartResponse setClusterMaintenanceMode(Long workspaceId, String name, @NotNull MaintenanceModeV4Request maintenanceMode) {
        return stackOperations.setClusterMaintenanceMode(NameOrCrn.ofName(name), workspaceId, maintenanceMode);
    }

    @Override
    public FlowStartResponse putCluster(Long workspaceId, String name, @Valid UpdateClusterV4Request updateJson) {
        return stackOperations.putCluster(NameOrCrn.ofName(name), workspaceId, updateJson);
    }

    @Override
    public String getClusterHostsInventory(Long workspaceId, String name) {
        return stackOperations.getClusterHostsInventory(workspaceId, name);
    }
}
