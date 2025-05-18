package com.faspix.cryptorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.math.BigDecimal;

@Configuration
public class RedisTemplateConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, BigDecimal> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, BigDecimal> context = RedisSerializationContext
                .<String, BigDecimal>newSerializationContext(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(BigDecimal.class))
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
