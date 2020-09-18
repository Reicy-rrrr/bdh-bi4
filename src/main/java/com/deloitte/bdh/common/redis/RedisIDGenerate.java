package com.deloitte.bdh.common.redis;

import com.deloitte.bdh.common.config.RedisConfig;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

/**
 * 基于 redis 分布式自增id生成器
 *
 * @author dahpeng
 * @date 2020/03/11
 */

@Component
public class RedisIDGenerate {

	@Autowired
	private RedisConfig redisConfig;

	/**
	 * 获取今日最后的时间
	 *
	 * @return Date
	 */
	private static Date getTodayEndTime() {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	/**
	 * 今天的日期格式
	 *
	 * @return String
	 */
	private static String getTodayFormatyyyyMMdd() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	/**
	 * 今天的日期格式
	 *
	 * @return String
	 */
	private static String getTodayFormatyyMMdd() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		return sdf.format(new Date());
	}

	/**
	 * 缓存设置
	 *
	 * @param key 缓存key
	 * @param value 值
	 * @param expireTime 过期时间
	 */
	public void set(String key, int value, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		counter.set(value);
		counter.expireAt(expireTime);
	}

	/**
	 * 缓存设置
	 *
	 * @param key 缓存key
	 * @param value 值
	 * @param unit 过期时间
	 */
	public void set(String key, int value, long timeout, TimeUnit unit) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		counter.set(value);
		counter.expire(timeout, unit);
	}

	/**
	 * 生成自增id(无过期时间)
	 *
	 * @param key 缓存key
	 * @return long
	 */
	public long generate(String key) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		return counter.incrementAndGet();
	}

	/**
	 * 生成自增id(按指定值自增)
	 *
	 * @param key 缓存key
	 * @param increment 自增数
	 * @return long
	 */
	public long generate(String key, int increment) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		return counter.addAndGet(increment);
	}

	/**
	 * 生成自增id(按1自增)
	 *
	 * @param key 缓存key
	 * @param expireTime 过期时间点,将在指点时间点过期
	 * @return long
	 */
	public long generate(String key, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		counter.expireAt(expireTime);
		return counter.incrementAndGet();
	}

	/**
	 * 根据业务key, 长度, 获取id
	 *
	 * @param key 缓存key
	 * @param increment 自增数
	 * @param expireTime 过期时间
	 * @return long
	 */
	public long generate(String key, int increment, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		counter.expireAt(expireTime);
		return counter.addAndGet(increment);
	}

	/**
	 * 根据业务key, 长度, 按天获取id(格式：yyyyMMdd+length位自增id)
	 *
	 * @param key 缓存key
	 * @param length 长度
	 * @return String
	 */
	public String generateIdByTodayFormatyyyyMMdd(String key, Integer length) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		long num = counter.incrementAndGet();
		counter.expireAt(getTodayEndTime());
		String id = getTodayFormatyyyyMMdd() + String.format("%0" + length + "d", num);
		return id;
	}

	/**
	 * 根据业务key, 长度, 按天获取id(格式：yyMMdd+length位自增id)
	 *
	 * @param key 缓存key
	 * @param length 长度
	 * @return String
	 */
	public String generateIdByTodayFormatyyMMdd(String key, Integer length) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		long num = counter.incrementAndGet();
		counter.expireAt(getTodayEndTime());
		String id = getTodayFormatyyMMdd() + String.format("%0" + length + "d", num);
		return id;
	}

	/**
	 * 根据业务key, 长度, 按天获取id(格式：bizCode+yyyyMMdd+length位自增id)
	 *
	 * @param key 缓存key
	 * @param bizCode 业务编码
	 * @param length 长度
	 * @return String
	 */
	public String generateIdByTodayFormatyyyyMMdd(String key, String bizCode, Integer length) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		long num = counter.incrementAndGet();
		counter.expireAt(getTodayEndTime());
		String id = bizCode + getTodayFormatyyyyMMdd() + String.format("%0" + length + "d", num);
		return id;
	}

	/**
	 * 根据业务key, 长度, 按天获取id(格式：bizCode+yyMMdd+length位自增id)
	 *
	 * @param key 缓存key
	 * @param bizCode 业务编码
	 * @param length 长度
	 * @return String
	 */
	public String generateIdByTodayFormatyyMMdd(String key, String bizCode, Integer length) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisConfig.connectionFactory());
		long num = counter.incrementAndGet();
		counter.expireAt(getTodayEndTime());
		String id = bizCode + getTodayFormatyyMMdd() + String.format("%0" + length + "d", num);
		return id;
	}

}
