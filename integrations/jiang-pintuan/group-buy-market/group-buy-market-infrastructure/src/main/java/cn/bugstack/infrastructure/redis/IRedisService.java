package cn.bugstack.infrastructure.redis;

import org.redisson.api.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Redis 鏈嶅姟
 *
 */
public interface IRedisService {

    /**
     * 璁剧疆鎸囧畾 key 鐨勫€?
     *
     * @param key   閿?
     * @param value 鍊?
     */
    <T> void setValue(String key, T value);

    /**
     * 璁剧疆鎸囧畾 key 鐨勫€?
     *
     * @param key     閿?
     * @param value   鍊?
     * @param expired 杩囨湡鏃堕棿
     */
    <T> void setValue(String key, T value, long expired);

    /**
     * 鑾峰彇鎸囧畾 key 鐨勫€?
     *
     * @param key 閿?
     * @return 鍊?
     */
    <T> T getValue(String key);

    /**
     * 鑾峰彇闃熷垪
     *
     * @param key 閿?
     * @param <T> 娉涘瀷
     * @return 闃熷垪
     */
    <T> RQueue<T> getQueue(String key);

    /**
     * 鍔犻攣闃熷垪
     *
     * @param key 閿?
     * @param <T> 娉涘瀷
     * @return 闃熷垪
     */
    <T> RBlockingQueue<T> getBlockingQueue(String key);

    /**
     * 寤惰繜闃熷垪
     *
     * @param rBlockingQueue 鍔犻攣闃熷垪
     * @param <T>            娉涘瀷
     * @return 闃熷垪
     */
    <T> RDelayedQueue<T> getDelayedQueue(RBlockingQueue<T> rBlockingQueue);

    /**
     * 璁剧疆鍊?
     *
     * @param key   key 閿?
     * @param value 鍊?
     */
    void setAtomicLong(String key, long value);

    /**
     * 鑾峰彇鍊?
     *
     * @param key key 閿?
     */
    Long getAtomicLong(String key);

    /**
     * 鑷 Key 鐨勫€硷紱1銆?銆?銆?
     *
     * @param key 閿?
     * @return 鑷鍚庣殑鍊?
     */
    long incr(String key);

    /**
     * 鎸囧畾鍊硷紝鑷 Key 鐨勫€硷紱1銆?銆?銆?
     *
     * @param key 閿?
     * @return 鑷鍚庣殑鍊?
     */
    long incrBy(String key, long delta);

    /**
     * 鑷噺 Key 鐨勫€硷紱1銆?銆?銆?
     *
     * @param key 閿?
     * @return 鑷鍚庣殑鍊?
     */
    long decr(String key);

    /**
     * 鎸囧畾鍊硷紝鑷 Key 鐨勫€硷紱1銆?銆?銆?
     *
     * @param key 閿?
     * @return 鑷鍚庣殑鍊?
     */
    long decrBy(String key, long delta);


    /**
     * 绉婚櫎鎸囧畾 key 鐨勫€?
     *
     * @param key 閿?
     */
    void remove(String key);

    /**
     * 鍒ゆ柇鎸囧畾 key 鐨勫€兼槸鍚﹀瓨鍦?
     *
     * @param key 閿?
     * @return true/false
     */
    boolean isExists(String key);

    /**
     * 灏嗘寚瀹氱殑鍊兼坊鍔犲埌闆嗗悎涓?
     *
     * @param key   閿?
     * @param value 鍊?
     */
    void addToSet(String key, String value);

    /**
     * 鍒ゆ柇鎸囧畾鐨勫€兼槸鍚︽槸闆嗗悎鐨勬垚鍛?
     *
     * @param key   閿?
     * @param value 鍊?
     * @return 濡傛灉鏄泦鍚堢殑鎴愬憳杩斿洖 true锛屽惁鍒欒繑鍥?false
     */
    boolean isSetMember(String key, String value);

    /**
     * 灏嗘寚瀹氱殑鍊兼坊鍔犲埌鍒楄〃涓?
     *
     * @param key   閿?
     * @param value 鍊?
     */
    void addToList(String key, String value);

    /**
     * 鑾峰彇鍒楄〃涓寚瀹氱储寮曠殑鍊?
     *
     * @param key   閿?
     * @param index 绱㈠紩
     * @return 鍊?
     */
    String getFromList(String key, int index);

    /**
     * 鑾峰彇Map
     *
     * @param key 閿?
     * @return 鍊?
     */
    <K, V> RMap<K, V> getMap(String key);

    /**
     * 灏嗘寚瀹氱殑閿€煎娣诲姞鍒板搱甯岃〃涓?
     *
     * @param key   閿?
     * @param field 瀛楁
     * @param value 鍊?
     */
    void addToMap(String key, String field, String value);

    /**
     * 鑾峰彇鍝堝笇琛ㄤ腑鎸囧畾瀛楁鐨勫€?
     *
     * @param key   閿?
     * @param field 瀛楁
     * @return 鍊?
     */
    String getFromMap(String key, String field);

    /**
     * 鑾峰彇鍝堝笇琛ㄤ腑鎸囧畾瀛楁鐨勫€?
     *
     * @param key   閿?
     * @param field 瀛楁
     * @return 鍊?
     */
    <K, V> V getFromMap(String key, K field);

    /**
     * 灏嗘寚瀹氱殑鍊兼坊鍔犲埌鏈夊簭闆嗗悎涓?
     *
     * @param key   閿?
     * @param value 鍊?
     */
    void addToSortedSet(String key, String value);

    /**
     * 鑾峰彇 Redis 閿侊紙鍙噸鍏ラ攣锛?
     *
     * @param key 閿?
     * @return Lock
     */
    RLock getLock(String key);

    /**
     * 鑾峰彇 Redis 閿侊紙鍏钩閿侊級
     *
     * @param key 閿?
     * @return Lock
     */
    RLock getFairLock(String key);

    /**
     * 鑾峰彇 Redis 閿侊紙璇诲啓閿侊級
     *
     * @param key 閿?
     * @return RReadWriteLock
     */
    RReadWriteLock getReadWriteLock(String key);

    /**
     * 鑾峰彇 Redis 淇″彿閲?
     *
     * @param key 閿?
     * @return RSemaphore
     */
    RSemaphore getSemaphore(String key);

    /**
     * 鑾峰彇 Redis 杩囨湡淇″彿閲?
     * <p>
     * 鍩轰簬Redis鐨凴edisson鐨勫垎甯冨紡淇″彿閲忥紙Semaphore锛塉ava瀵硅薄RSemaphore閲囩敤浜嗕笌java.util.concurrent.Semaphore鐩镐技鐨勬帴鍙ｅ拰鐢ㄦ硶銆?
     * 鍚屾椂杩樻彁渚涗簡寮傛锛圓sync锛夈€佸弽灏勫紡锛圧eactive锛夊拰RxJava2鏍囧噯鐨勬帴鍙ｃ€?
     *
     * @param key 閿?
     * @return RPermitExpirableSemaphore
     */
    RPermitExpirableSemaphore getPermitExpirableSemaphore(String key);

    /**
     * 闂攣
     *
     * @param key 閿?
     * @return RCountDownLatch
     */
    RCountDownLatch getCountDownLatch(String key);

    /**
     * 甯冮殕杩囨护鍣?
     *
     * @param key 閿?
     * @param <T> 瀛樻斁瀵硅薄
     * @return 杩斿洖缁撴灉
     */
    <T> RBloomFilter<T> getBloomFilter(String key);

    Boolean setNx(String key);

    Boolean setNx(String key, long expired, TimeUnit timeUnit);

    RBitSet getBitSet(String key);

    default int getIndexFromUserId(String userId) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(userId.getBytes(StandardCharsets.UTF_8));
            // 灏嗗搱甯屽瓧鑺傛暟缁勮浆鎹负姝ｆ暣鏁?
            BigInteger bigInt = new BigInteger(1, hashBytes);
            // 鍙栨ā浠ョ‘淇濈储寮曞湪鍚堢悊鑼冨洿鍐?
            return bigInt.mod(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

}
