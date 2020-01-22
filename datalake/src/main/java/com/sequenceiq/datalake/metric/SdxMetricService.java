package com.sequenceiq.datalake.metric;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.common.metrics.AbstractMetricService;

@Service
public class SdxMetricService extends AbstractMetricService {

    private static final String METRIC_PREFIX = "sdx";

    @Override
    protected String getMetricPrefix() {
        return METRIC_PREFIX;
    }
}
