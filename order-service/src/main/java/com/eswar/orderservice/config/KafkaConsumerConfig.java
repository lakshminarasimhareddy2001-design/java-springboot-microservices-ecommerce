package com.eswar.orderservice.config;

import com.eswar.orderservice.kafka.event.StockRejectedEvent;
import com.eswar.orderservice.kafka.event.StockReservedEvent;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, StockReservedEvent> stockReservedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");





        return new DefaultKafkaConsumerFactory<>
                (props, new StringDeserializer(),
                 new ErrorHandlingDeserializer<>(new JacksonJsonDeserializer<>(StockReservedEvent.class, false)
                 ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockReservedEvent> stockReservedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockReservedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockReservedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, StockRejectedEvent> stockRejectedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),  new ErrorHandlingDeserializer<>(new JacksonJsonDeserializer<>(StockRejectedEvent.class, false)
        ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockRejectedEvent> stockRejectedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockRejectedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockRejectedConsumerFactory());
        return factory;
    }
}
