package co.edu.uniquindio.utils.communication.transfer.integration.starter;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.web.restful.MessageProcessorWrapper;
import co.edu.uniquindio.utils.communication.web.restful.RestfulWebCommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.web.restful.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass({RestfulWebCommunicationManagerFactory.class})
@EnableConfigurationProperties(IntegrationCommunicationProperties.class)
public class IntegrationCommunicationAutoConfiguration {
    private final IntegrationCommunicationProperties integrationCommunicationProperties;
    private final IntegrationFlowContext flowContext;
    private final int webPort;

    public IntegrationCommunicationAutoConfiguration(IntegrationCommunicationProperties integrationCommunicationProperties, IntegrationFlowContext flowContext, @Value("${server.port}") int webPort) {
        this.integrationCommunicationProperties = integrationCommunicationProperties;
        this.flowContext = flowContext;
        this.webPort = webPort;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory(RestTemplate restTemplate, MessageProcessorWrapper messageProcessorWrapper) {
        return new RestfulWebCommunicationManagerFactory(restTemplate, integrationCommunicationProperties.getBaseURL(), integrationCommunicationProperties.getRequestPath(), webPort, new Observable<>(), integrationCommunicationProperties.getInstances(), flowContext, messageProcessorWrapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(Jackson2ObjectMapperBuilder builder) {
        RestTemplate restTemplate = new RestTemplate();

        builder.mixIn(Message.class, MessageMixIn.class)
                .mixIn(MessageType.class, MessageTypeMixIn.class)
                .mixIn(Address.class, AddressMixIn.class)
                .mixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .mixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .mixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());

        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(){
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        builder.mixIn(Message.class, MessageMixIn.class)
                .mixIn(MessageType.class, MessageTypeMixIn.class)
                .mixIn(Address.class, AddressMixIn.class)
                .mixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .mixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .mixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);

        return builder;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageMixIn.class)
                .addMixIn(MessageType.class, MessageTypeMixIn.class)
                .addMixIn(Address.class, AddressMixIn.class)
                .addMixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .addMixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .addMixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);

        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcessorWrapper messageProcessorWrapper() {
        return new MessageProcessorWrapper();
    }

}