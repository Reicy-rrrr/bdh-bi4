package com.deloitte.bdh.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis单机操作工具类
 *
 * @author pengdh
 * @date 2018/04/19
 */
@Component
public class RedisSingleUtil {

	@Autowired
	private JedisPool jedisPool;

	public String get(String key) {
		Jedis resource = jedisPool.getResource();
		String s = resource.get(key);
		resource.close();
		return s;
	}

	public String set(String key, String value) {
		Jedis resource = jedisPool.getResource();
		String set = resource.set(key, value);
		resource.close();
		return set;
	}

	public String hget(String hkey, String field) {
		Jedis resource = jedisPool.getResource();
		String hget = resource.hget(hkey, field);
		resource.close();
		return hget;
	}

	public Long hset(String hkey, String field, String value) {
		Jedis resource = jedisPool.getResource();
		Long hset = resource.hset(hkey, field, value);
		resource.close();
		return hset;
	}

	public Long incr(String key) {
		Jedis resource = jedisPool.getResource();
		Long incr = resource.incr(key);
		resource.close();
		return incr;
	}

	public Long expire(String key, int sec) {
		Jedis resource = jedisPool.getResource();
		Long expire = resource.expire(key, sec);
		resource.close();
		return expire;
	}

	public Long ttl(String key) {
		Jedis resource = jedisPool.getResource();
		Long ttl = resource.ttl(key);
		resource.close();
		return ttl;
	}

	public Long del(String key) {
		Jedis resource = jedisPool.getResource();
		Long del = resource.del(key);
		resource.close();
		return del;
	}

	public Long hdel(String hkey, String field) {
		Jedis resource = jedisPool.getResource();
		Long hdel = resource.hdel(hkey, field);
		resource.close();
		return hdel;
	}
}
