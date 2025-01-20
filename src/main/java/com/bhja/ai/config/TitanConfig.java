package com.bhja.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.autoconfigure.bedrock.BedrockAwsConnectionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
@ConfigurationProperties(prefix = "ai.titan")
@Getter
@Setter
public class TitanConfig {
    private String modelId;
    private int maxToken;
    private Float temperature;
    private Float permutations;

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(BedrockAwsConnectionProperties awsConnectionProperties) {
        return
                BedrockRuntimeClient.builder().credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return awsConnectionProperties.getAccessKey();
                    }

                    @Override
                    public String secretAccessKey() {
                        return awsConnectionProperties.getSecretKey();
                    }
                })).build();
    }
}
