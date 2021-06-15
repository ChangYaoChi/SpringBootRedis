package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@EnableCaching
public class UserService {

    @Autowired
    private UserRepository userDao;

    /**
     * cacheNames = value，若設置了 cacheNames 的值，就不用設置 value
     * 若沒有設置 cacheNames 及 value，default 為方法所在的類別名稱
     * <p>
     * 使用 keyGenerator ，注意是否在 config 中定義好。
     */
    //    @Cacheable(keyGenerator = "keyGenerator")
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 執行時,將清除value = getAllUsers cache
     * 【cacheNames = "userService"】
     * 也可指定清除的key 【@CacheEvict(value="abc")】
     */
    @CacheEvict(value = "getAllUsers", allEntries = true)
    public void clearAllUserCache() {

    }

    /**
     * key 的值可以SpEL表示式編寫，或者按照傳遞參數名稱編寫，與 keyGenerator 不能共存
     * "#p0": 表示以第一個"傳遞參數"作為key
     * "#id": 表示以傳遞參數 id 作為key
     * default: 以 SimpleKey[所有傳遞參數依序排列並以逗號隔開]
     * <p>
     * condition 快取的條件，符合條件才會進行快取
     * unless 與 condition 相反，符合條件會拒絕快取
     */
    @Cacheable(value = "user", key = "#p0", unless = "#result == null")
    public User findById(Integer id) {
        Optional<User> user = userDao.findById(id);
        return Optional.of(user).get().orElse(null);
    }

    @Cacheable(value = "userCache", key = "#username", unless = "#result == null")
    public User findByIdAndUsername(Integer id, String username) {
        return Optional.of(userDao.findByIdAndAndUserName(id, username)).get().orElse(null);
    }

    @CachePut(value = "user", key = "#p0")
    public User updateById(Integer id, User userUpdate) {
        User user = Optional.of(userDao.findById(id)).get().orElse(null);
        user.setUserName(userUpdate.getUserName());
        return userDao.save(user);
    }

    /**
     * 刪除 db 內的真實資料時，快取的資料也要一併刪除，否則程式會繼續從快取拿取資料
     * <p>
     * allEntries：設置為 true 的話，會將同一個 cacheNames 底下的所有快取全部刪除
     * beforeInvocation : 設置為 true，則在方法還沒有執行的時候就清空快取，預設情況下，如果方法執行丟擲異常，則不會清空快取
     */
    @CacheEvict(value = "user")
    public void clear(Integer id) {
        userDao.deleteById(id);
    }
}
