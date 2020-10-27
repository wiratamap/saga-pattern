package com.personal.librarykafkaconsumer.configuration;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaConsumerConfig {

    private final Environment environment;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put("bootstrap.servers", this.environment.getProperty("spring.kafka.bootstrap-servers"));
        configProps.put("group.id", this.environment.getProperty("spring.kafka.consumer.group-id"));
        configProps.put("client.id", this.environment.getProperty("spring.kafka.consumer.client-id"));
        configProps.put("enable.auto.commit", this.environment.getProperty("spring.kafka.consumer.enable-auto-commit"));
        configProps.put("key.deserializer", StringDeserializer.class);
        configProps.put("value.deserializer", StringDeserializer.class);
        configProps.put("auto.offset.reset", this.environment.getProperty("spring.kafka.consumer.auto-offset-reset"));

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(this.consumerFactory());
        factory.setAutoStartup(Boolean.parseBoolean(this.environment.getProperty("kafka.listener.auto-startup")));
        return factory;
    }
}
