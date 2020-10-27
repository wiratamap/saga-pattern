package com.personal.librarykafkaproducer.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Environment environment;

    public KafkaProducerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        configProps.put(ProducerConfig.ACKS_CONFIG, environment.getProperty("spring.kafka.producer.acks"));
        configProps.put(ProducerConfig.RETRIES_CONFIG, environment.getProperty("spring.kafka.producer.retries"));
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, environment.getProperty("spring.kafka.producer.batch-size"));
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, environment.getProperty("spring.kafka.producer.linger-ms"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, environment.getProperty("spring.kafka.producer.client-id"));

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
