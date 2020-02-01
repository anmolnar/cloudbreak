package com.sequenceiq.freeipa.api.v1.freeipa.stack.model.health;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.doc.FreeIpaModelDescriptions;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.common.Status;
import com.sequenceiq.service.api.doc.ModelDescriptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("HealthDetailsFreeIpaV1Response")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthDetailsFreeIpaResponse {
    @NotNull
    @ApiModelProperty(value = ModelDescriptions.ENVIRONMENT_CRN, required = true)
    private String environmentCrn;

    @NotNull
    @ApiModelProperty(value = FreeIpaModelDescriptions.FREEIPA_NAME, required = true)
    private String name;

    @NotNull
    private String crn;

    @NotNull
    private List<NodeHealthDetails> nodeHealthDetailsFreeIpaRespons;

    private Status status;

    public String getEnvironmentCrn() {
        return environmentCrn;
    }

    public void setEnvironmentCrn(String environmentCrn) {
        this.environmentCrn = environmentCrn;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NodeHealthDetails> getNodeHealthDetailsFreeIpaRespons() {
        if (nodeHealthDetailsFreeIpaRespons == null) {
            nodeHealthDetailsFreeIpaRespons = new ArrayList<>();
        }
        return nodeHealthDetailsFreeIpaRespons;
    }

    public void setNodeHealthDetailsFreeIpaRespons(List<NodeHealthDetails> nodeHealthDetailsFreeIpaRespons) {
        this.nodeHealthDetailsFreeIpaRespons = nodeHealthDetailsFreeIpaRespons;
    }

    public void addNodeHealthDetailsFreeIpaResponses(NodeHealthDetails nodeHealthDetails) {
        if (nodeHealthDetailsFreeIpaRespons == null) {
            nodeHealthDetailsFreeIpaRespons = new ArrayList<>();
        }
        this.nodeHealthDetailsFreeIpaRespons.add(nodeHealthDetails);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
