package com.sequenceiq.cloudbreak.altus;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AltusDatabusConfiguration {

    private final String altusDatabusEndpoint;

    private final boolean useSharedAltusCredential;

    private final boolean useSharedAltusCredentialDefaultValue;

    private final String sharedAccessKey;

    private final char[] sharedSecretKey;

    public AltusDatabusConfiguration(
            @Value("${altus.databus.endpoint:}") String altusDatabusEndpoint,
            @Value("${altus.databus.shared.credential.enabled:false}") boolean useSharedAltusCredential,
            @Value("${altus.databus.shared.credential.default:false}") boolean useSharedAltusCredentialDefaultValue,
            @Value("${altus.databus.shared.accessKey:}") String sharedAccessKey,
            @Value("${altus.databus.shared.secretKey:}") String sharedSecretKey) {
        this.altusDatabusEndpoint = altusDatabusEndpoint;
        this.useSharedAltusCredential = useSharedAltusCredential;
        this.useSharedAltusCredentialDefaultValue = useSharedAltusCredentialDefaultValue;
        this.sharedAccessKey = sharedAccessKey;
        this.sharedSecretKey = StringUtils.isBlank(sharedSecretKey) ? null : sharedSecretKey.toCharArray();
    }

    public String getAltusDatabusEndpoint() {
        return altusDatabusEndpoint;
    }

    public boolean isUseSharedAltusCredential() {
        return useSharedAltusCredential;
    }

    public boolean getUseSharedAltusCredentialDefaultValue() {
        return useSharedAltusCredentialDefaultValue;
    }

    public String getSharedAccessKey() {
        return sharedAccessKey;
    }

    public char[] getSharedSecretKey() {
        return sharedSecretKey;
    }
}
