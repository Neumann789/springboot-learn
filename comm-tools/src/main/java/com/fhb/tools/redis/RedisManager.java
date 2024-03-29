package com.fhb.tools.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisManager {

    protected static Logger logger = LoggerFactory.getLogger(RedisManager.class);

    private String host = "127.0.0.1";

    private int port = 6379;

    private String password = "";

    private int expire = 0;

    private JedisPool jedisPool = null;

    public RedisManager() {

    }

    public RedisManager(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        if (jedisPool == null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port, 0, this.password);
        }
    }

    /**
     * 初始化方法
     */
    public void init() {
        if (jedisPool == null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
        }
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    public long setnx(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.setnx(key, value);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 获得Redis连接
     *
     * @return
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        byte[] value = null;
        Jedis jedis = jedisPool.getResource();
        try {
            value = jedis.get(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String value = null;
        Jedis jedis = jedisPool.getResource();
        try {
            value = jedis.get(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @return
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            if (this.expire != 0) {
                jedis.expire(key, this.expire);
            }
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public byte[] set(byte[] key, byte[] value, int expire) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * del
     *
     * @param key
     */
    public void del(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * keys
     *
     * @return
     */
    public Set<byte[]> keys(String pattern) {
        Set<byte[]> keys = null;
        Jedis jedis = jedisPool.getResource();
        try {
            keys = jedis.hkeys(pattern.getBytes());
        } finally {
            jedisPool.returnResource(jedis);
        }
        return keys;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key     key值
     * @param seconds 多少秒后过期
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间
     */
    public long expire(String key, int seconds) {
        if (key == null || key.equals("")) {
            return 0;
        }

        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.expire(key, seconds);
        } catch (Exception ex) {
            logger.error("EXPIRE error[key=" + key + " seconds=" + seconds + "]" + ex.getMessage(), ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0;
    }

    /**
     * 设置一个key在某个时间点过期
     *
     * @param key           key值
     * @param unixTimestamp unix时间戳，从1970-01-01 00:00:00开始到现在的秒数
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间
     */
    public long expireAt(String key, int unixTimestamp) {
        if (key == null || key.equals("")) {
            return 0;
        }

        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.expireAt(key, unixTimestamp);
        } catch (Exception ex) {
            logger.error("EXPIRE error[key=" + key + " unixTimestamp=" + unixTimestamp + "]" + ex.getMessage(), ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0;
    }

    /**
     * 截断一个List
     *
     * @param key   列表key
     * @param start 开始位置 从0开始
     * @param end   结束位置
     * @return 状态码
     */
    public String trimList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return "-";
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.ltrim(key, start, end);
        } catch (Exception ex) {
            logger.error("LTRIM 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage(), ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return "-";
    }

    /**
     * 检查Set长度
     *
     * @param key
     * @return
     */
    public long countSet(String key) {
        if (key == null) {
            return 0;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.scard(key);
        } catch (Exception ex) {
            logger.error("countSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0;
    }

    /**
     * 添加到Set中（同时设置过期时间）
     *
     * @param key     key值
     * @param seconds 过期时间 单位s
     * @param value
     * @return
     */
    public boolean addSet(String key, int seconds, String... value) {
        boolean result = addSet(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到Set中
     *
     * @param key
     * @param value
     * @return
     */
    public boolean addSet(String key, String... value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.sadd(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }


    /**
     * @param key
     * @param value
     * @return 判断值是否包含在set中
     */
    public boolean containsInSet(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.sismember(key, value);
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 获取Set
     *
     * @param key
     * @return
     */
    public Set<String> getSet(String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 从set中删除value
     *
     * @param key
     * @return
     */
    public boolean removeSetValue(String key, String... value) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.srem(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }


    /**
     * 从list中删除value 默认count 1
     *
     * @param key
     * @param values 值list
     * @return
     */
    public int removeListValue(String key, List<String> values) {
        return removeListValue(key, 1, values);
    }

    /**
     * 从list中删除value
     *
     * @param key
     * @param count
     * @param values 值list
     * @return
     */
    public int removeListValue(String key, long count, List<String> values) {
        int result = 0;
        if (values != null && values.size() > 0) {
            for (String value : values) {
                if (removeListValue(key, count, value)) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * 从list中删除value
     *
     * @param key
     * @param count 要删除个数
     * @param value
     * @return
     */
    public boolean removeListValue(String key, long count, String value) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.lrem(key, count, value);
            return true;
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 截取List
     *
     * @param key
     * @param start 起始位置
     * @param end   结束位置
     * @return
     */
    public List<String> rangeList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return null;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.lrange(key, start, end);
        } catch (Exception ex) {
            logger.error("rangeList 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage(), ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 检查List长度
     *
     * @param key
     * @return
     */
    public long countList(String key) {
        if (key == null) {
            return 0;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.llen(key);
        } catch (Exception ex) {
            logger.error("countList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0;
    }

    /**
     * 添加到List中（同时设置过期时间）
     *
     * @param key     key值
     * @param seconds 过期时间 单位s
     * @param value
     * @return
     */
    public boolean addList(String key, int seconds, String... value) {
        boolean result = addList(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到List
     *
     * @param key
     * @param value
     * @return
     */
    public boolean addList(String key, String... value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.lpush(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 添加到List(只新增)
     *
     * @param key
     * @return
     */
    public boolean addList(String key, List<String> list) {
        if (key == null || list == null || list.size() == 0) {
            return false;
        }
        for (String value : list) {
            addList(key, value);
        }
        return true;
    }

    /**
     * 获取List
     *
     * @param key
     * @return
     */
    public List<String> getList(String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 设置HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @param value  Json String or String value
     * @return
     */
    public boolean setHSet(String domain, String key, String value) {
        if (value == null) return false;
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.hset(domain, key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 获得HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return Json String or String value
     */
    public String getHSet(String domain, String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.hget(domain, key);
        } catch (Exception ex) {
            logger.error("getHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public long delHSet(String domain, String key) {
        Jedis Jedis = null;
        long count = 0;
        try {
            Jedis = jedisPool.getResource();
            count = Jedis.hdel(domain, key);
        } catch (Exception ex) {
            logger.error("delHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return count;
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public long delHSet(String domain, String... key) {
        Jedis Jedis = null;
        long count = 0;
        try {
            Jedis = jedisPool.getResource();
            count = Jedis.hdel(domain, key);
        } catch (Exception ex) {
            logger.error("delHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return count;
    }

    /**
     * 判断key是否存在
     *
     * @param domain 域名
     * @param key    键值
     * @return
     */
    public boolean existsHSet(String domain, String key) {
        Jedis Jedis = null;
        boolean isExist = false;
        try {
            Jedis = jedisPool.getResource();
            isExist = Jedis.hexists(domain, key);
        } catch (Exception ex) {
            logger.error("existsHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return isExist;
    }

    /**
     * 全局扫描hset
     *
     * @param match field匹配模式
     * @return
     */
    public List<Map.Entry<String, String>> scanHSet(String domain, String match) {
        Jedis Jedis = null;
        try {
            int cursor = 0;
            Jedis jedis = jedisPool.getResource();
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            ScanResult<Map.Entry<String, String>> scanResult;
            List<Map.Entry<String, String>> list = new ArrayList<>();
            do {
                scanResult = jedis.hscan(domain, String.valueOf(cursor), scanParams);
                list.addAll(scanResult.getResult());
                cursor = Integer.parseInt(scanResult.getStringCursor());
            } while (cursor > 0);
            return list;
        } catch (Exception ex) {
            logger.error("scanHSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }


    /**
     * 返回 domain 指定的哈希集中所有字段的value值
     *
     * @param domain
     * @return
     */

    public List<String> hvals(String domain) {
        Jedis Jedis = null;
        List<String> retList = null;
        try {
            Jedis = jedisPool.getResource();
            retList = Jedis.hvals(domain);
        } catch (Exception ex) {
            logger.error("hvals error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return retList;
    }

    /**
     * 返回 domain 指定的哈希集中所有字段的key值
     *
     * @param domain
     * @return
     */

    public Set<String> hkeys(String domain) {
        Jedis Jedis = null;
        Set<String> retList = null;
        try {
            Jedis = jedisPool.getResource();
            retList = Jedis.hkeys(domain);
        } catch (Exception ex) {
            logger.error("hkeys error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return retList;
    }

    /**
     * 返回 domain 指定的哈希key值总数
     *
     * @param domain
     * @return
     */
    public long lenHset(String domain) {
        Jedis Jedis = null;
        long retList = 0;
        try {
            Jedis = jedisPool.getResource();
            retList = Jedis.hlen(domain);
        } catch (Exception ex) {
            logger.error("hkeys error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return retList;
    }

    /**
     * 设置排序集合
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public boolean setSortedSet(String key, long score, String value) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.zadd(key, score, value);
            return true;
        } catch (Exception ex) {
            logger.error("setSortedSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 获得排序集合
     *
     * @param key
     * @param startScore
     * @param endScore
     * @param orderByDesc
     * @return
     */
    public Set<String> getSoredSet(String key, long startScore, long endScore, boolean orderByDesc) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            if (orderByDesc) {
                return Jedis.zrevrangeByScore(key, endScore, startScore);
            } else {
                return Jedis.zrangeByScore(key, startScore, endScore);
            }
        } catch (Exception ex) {
            logger.error("getSoredSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 计算排序长度
     *
     * @param key
     * @param startScore
     * @param endScore
     * @return
     */
    public long countSoredSet(String key, long startScore, long endScore) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Long count = Jedis.zcount(key, startScore, endScore);
            return count == null ? 0L : count;
        } catch (Exception ex) {
            logger.error("countSoredSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0L;
    }

    /**
     * 删除排序集合
     *
     * @param key
     * @param value
     * @return
     */
    public boolean delSortedSet(String key, String value) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            long count = Jedis.zrem(key, value);
            return count > 0;
        } catch (Exception ex) {
            logger.error("delSortedSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    /**
     * 获得排序集合
     *
     * @param key
     * @param startRange
     * @param endRange
     * @param orderByDesc
     * @return
     */
    public Set<String> getSoredSetByRange(String key, int startRange, int endRange, boolean orderByDesc) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            if (orderByDesc) {
                return Jedis.zrevrange(key, startRange, endRange);
            } else {
                return Jedis.zrange(key, startRange, endRange);
            }
        } catch (Exception ex) {
            logger.error("getSoredSetByRange error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    /**
     * 获得排序打分
     *
     * @param key
     * @return
     */
    public Double getScore(String key, String member) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.zscore(key, member);
        } catch (Exception ex) {
            logger.error("getSoredSet error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    public boolean set(String key, String value, int second) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.setex(key, second, value);
            return true;
        } catch (Exception ex) {
            logger.error("set error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    public boolean set(String key, String value) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.set(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("set error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    public String get(String key, String defaultValue) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.get(key) == null ? defaultValue : Jedis.get(key);
        } catch (Exception ex) {
            logger.error("get error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return defaultValue;
    }

    public boolean del(String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            Jedis.del(key);
            return true;
        } catch (Exception ex) {
            logger.error("del error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return false;
    }

    public Long incr(String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.incr(key);
        } catch (Exception ex) {
            logger.error("incr error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return null;
    }

    public long decr(String key) {
        Jedis Jedis = null;
        try {
            Jedis = jedisPool.getResource();
            return Jedis.decr(key);
        } catch (Exception ex) {
            logger.error("incr error.", ex);
            returnBrokenResource(Jedis);
        } finally {
            returnResource(Jedis);
        }
        return 0;
    }


    public void returnBrokenResource(Jedis Jedis) {
        try {
            jedisPool.returnBrokenResource(Jedis);
        } catch (Exception e) {
            logger.error("returnBrokenResource error.", e);
        }
    }

    public void returnResource(Jedis Jedis) {
        try {
            jedisPool.returnResource(Jedis);
        } catch (Exception e) {
            logger.error("returnResource error.", e);
        }
    }

    public JedisPool getjedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
