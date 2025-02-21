package com.bhja.ai.service;

import com.bhja.ai.config.AnthropicConfig;
import com.bhja.ai.config.TitanConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class BedrockService
        implements IBedrockService {


    private final TitanConfig titanConfig;
    private final AnthropicConfig anthropicConfig;
    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;


    /**
     * Conver
     *
     * @param input any questions
     * @return string.
     */

    @Override
    public Map<String, Object> converseWithTitan(String input) {
        try {
            //The message that the user sends to the model.
            Message message =
                    Message.builder().content(ContentBlock.fromText(input))
                           .role(ConversationRole.USER).build();
            ConverseResponse response = bedrockRuntimeClient.converse(request -> request
                    .modelId(titanConfig.getModelId()) //model id
                    .messages(message)
                    .inferenceConfig(config -> config
                            .maxTokens(titanConfig.getMaxToken()) // maximum tokens of the input to consider
                            .temperature(titanConfig.getTemperature()) //temperature to be used. Low temperatures yield valid results
                            .topP(titanConfig.getPermutations()))); // top permutations to pick from the model response.
            // Retrieve the generated text from Bedrock's response object.
            var responseText = response.output().message().content().getFirst().text();
            log.info(responseText);
            return Map.of("response", responseText);
        } catch (SdkClientException e) {
            log.error("Could not process the request :{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Anthropic claude 2.1 is used here . This is just a sample. A lot more can be done in v3.x
     * Here is the documentation of the parameters
     * <a href=https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages
     * .html#model-parameters-anthropic-claude-messages-request-response>
     * Parameters</a>
     *
     * @param request input
     * @return response as string.
     */
    @Override
    public Map<String, Object> summarizationWithAnthropicViaClaude(String request, String summary) {
        try {

            var input = getAnthropicTemplate().replace("{{prompt}}", request).replace("{{summary}}", summary).replace(
                                                     "{{temperature}}",
                                                     String.valueOf(anthropicConfig.getTemperature())).replace(
                                                             "{{permutations}}",String.valueOf(anthropicConfig.getPermutations()))
                                             .replace("{{tokens}}", String.valueOf(anthropicConfig.getMaxToken()));
            InvokeModelRequest request0 =
                    InvokeModelRequest.builder().modelId(anthropicConfig.getModelId()).body(SdkBytes.fromUtf8String(input)).contentType("application/json").build();

            InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request0);
            //TODO - Needs error handling and further processing as per usecase
            var responseBody = objectMapper.readValue(response.body().asUtf8String(), Map.class);

            log.info("{}", response.body().asUtf8String());
            return Map.of("response", responseBody.get("completion"));
        } catch (SdkClientException | JsonProcessingException e) {
            log.error("Could not process the request due to {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * A very small template. with the required parameters for summary.
     * @return String
     */
    protected String getAnthropicTemplate(){
        return """
                    {
                         "anthropic_version": "bedrock-2023-05-31",
                         "max_tokens_to_sample": {{tokens}},
                         "prompt": "Human: {{summary}}<text>{{prompt}}</text>\\n\\nAssistant:",
                         "temperature": {{temperature}},
                         "top_k" : 250,
                         "top_p" : {{permutations}},
                         "stop_sequences": []
                     }
                    """;
    }
}
