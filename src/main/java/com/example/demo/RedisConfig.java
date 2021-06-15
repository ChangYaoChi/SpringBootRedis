package com.example.demo;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.lang.reflect.Method;

@Configuration
//@EnableCaching
public class RedisConfig {

    /**
     * 定義Redis資料來源連線，有三種模式，如下
     * 1. RedisStandaloneConfiguration
     * 2. RedisSentinelConfiguration
     * 3. RedisClusterConfiguration
     * <p>
     * 當 Maven 引入 spring-boot-starter-data-redis 後，SpringBoot 的 CacheManager 就會自動使用 RedisCache。
     * 因此除非有需要設置不同的連線模式、redis server IP 或是 port，不然其實是可以不用特地配置下面 JedisConnectionFactory
     * <p>
     * JedisConnection 需要引用下列 maven
     * <dependency>
     * <groupId>redis.clients</groupId>
     * <artifactId>jedis</artifactId>
     * </dependency>
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 6379);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * 自定義 key 產生器
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * RedisTemplate 是 Spring Data Redis 提供給使用者的高階抽象介面，使用可以透過 RedisTemplate 操作各種不同類型的資料
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    /**
     * CacheManager 快取管理器，管理各種快取元件
     * 當 Maven 引入 spring-boot-starter-data-redis 後，SpringBoot 的 CacheManager 就會自動使用 RedisCache。
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
            .fromSerializer(new GenericJackson2JsonRedisSerializer());
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig(); // 過期時間

        return RedisCacheManager.builder(factory).cacheDefaults(defaultCacheConfig).build();

    }
}
