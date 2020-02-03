package com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.database;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatabaseRequest {

    @NotNull
    private DatabaseAvailabilityType availabilityType;

    public DatabaseAvailabilityType getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(DatabaseAvailabilityType availabilityType) {
        this.availabilityType = availabilityType;
    }
}
