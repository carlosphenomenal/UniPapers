package com.unipapers.backend.Configurations.Cloudflare;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudflare.r2")
@Data
public class R2Properties {

    private String accountId;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String publicUrl;

}
