package com.deloitte.bdh.common.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 *
 * @author pengdh
 * @date 2018/04/19
 */
@RefreshScope
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
	@Value("${spring.redis.cluster.host}")
	private String host;
	@Value("${spring.redis.cluster.port}")
	private int port;
	@Value("${spring.redis.cluster.password}")
	private String password;
	@Value("${spring.redis.cluster.nodes}")
	private String nodes;
	@Value("${spring.redis.cluster.timeout}")
	private int timeout;
	@Value("${spring.redis.cluster.maxIdle}")
	private int maxIdle;
	@Value("${spring.redis.cluster.maxWait}")
	private long maxWaitMillis;
	@Value("${spring.redis.cluster.maxRedirects}")
	private int maxRedirects;
	@Value("${spring.redis.cluster.maxAttempts}")
	private int maxAttempts;

	/**
	 * Jedis连接池
	 */
	@Bean
	public JedisPool redisPoolFactory() {
		logger.info("JedisPool注入成功！！");
		logger.info("redis地址：" + host + ":" + port);
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setMaxTotal(500);
		return new JedisPool(jedisPoolConfig, host, port,
				timeout, password);
	}

	/**
	 * JedisCluster
	 */
	@Bean
	public JedisCluster JedisClusterFactory() {
		logger.info("JedisCluster创建！！");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		if (StringUtils.isNotBlank(nodes)) {
			// 以“,”分割成"ip:port"
			String[] serverArray = nodes.split(",");
			// 以“:”分割成 “ip”,“port”
			if (serverArray != null) {
				for (String ipAndPort : serverArray) {
					//ipAndPort 值：(ip：端口)
					String[] ipAndPortArray = ipAndPort.split(":");
					logger.info("redis地址：" + ipAndPortArray[0].trim() + ":" + ipAndPortArray[1].trim());
					jedisClusterNodes.add(
							new HostAndPort(ipAndPortArray[0].trim(), Integer.valueOf(ipAndPortArray[1].trim())));
				}
			}
		}
		return new JedisCluster(jedisClusterNodes, timeout, timeout, maxAttempts, password,
				jedisPoolConfig);
	}

	/**
	 * 连接池设置
	 */
	@Bean
	public RedisConnectionFactory connectionFactory() {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("spring.redis.cluster.nodes", nodes);
		source.put("spring.redis.cluster.timeout", timeout);
		source.put("spring.redis.cluster.max-redirects", maxRedirects);
		RedisClusterConfiguration configuration = new RedisClusterConfiguration(
				new MapPropertySource("RedisClusterConfiguration", source));
		configuration.setPassword(RedisPassword.of(password));
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
		connectionFactory.afterPropertiesSet();
		return connectionFactory;
	}

	@Bean
	public KeyGenerator wiselyKeyGenerator() {
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

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

//		return RedisCacheManager.create(redisConnectionFactory);

		// 2.x写法
		// bdh 信息缓存配置
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration
				.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(30)).disableCachingNullValues().prefixKeysWith("bi:");
		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
		redisCacheConfigurationMap.put("bi:", cacheConfiguration);
		logger.info("bdh 缓存 启动");
		// 初始化一个RedisCacheWriter
		RedisCacheWriter redisCacheWriter = RedisCacheWriter
				.nonLockingRedisCacheWriter(redisConnectionFactory);
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
		// 设置默认超过期时间是3600秒
		defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
		// 初始化RedisCacheManager
		RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig,
				redisCacheConfigurationMap);
		return cacheManager;
	}

}
