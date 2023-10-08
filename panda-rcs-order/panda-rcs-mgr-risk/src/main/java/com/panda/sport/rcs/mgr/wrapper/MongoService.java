package com.panda.sport.rcs.mgr.wrapper;

import java.util.List;
import java.util.Map;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-10-29 18:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MongoService<T> {

    void createCollection(String name);

    /**
     * 功能描述: 创建索引
     * 索引是顺序排列，且唯一的索引
     *
     * @param collectionName 集合名称，相当于关系型数据库中的表名
     * @param filedName      对象中的某个属性名
     * @return:java.lang.String
     */
    String createIndex(String collectionName, String filedName);

    /**
     * 功能描述: 获取当前集合对应的所有索引的名称
     *
     * @param collectionName
     * @return:java.util.List<java.lang.String>
     */
    List<String> getAllIndexes(String collectionName);

    void insert(T info, String collectionName);

    /**
     * 功能描述: 往对应的集合中批量插入数据，注意批量的数据中不要包含重复的id
     *
     * @param infos 对象列表
     * @return:void
     */
    void insertMulti(List<T> infos, String collectionName);

    void updateById(String id, String collectionName, T info);

    void update(Map<String, Object> map, String collectionName, T info);

    void save(Map<String, Object> map, String collectionName, T info);

    boolean exists(Map<String, Object> map, String collectionName, T info);

    void upsert(Map<String, Object> map, String collectionName, T info);

    void deleteById(String id, Class<T> clazz, String collectionName);

    T selectById(String id, Class<T> clazz, String collectionName);

    /**
     * 功能描述: 查询列表信息
     * 将集合中符合对象类型的数据全部查询出来
     *
     * @param collectName 集合名称
     * @param clazz       类型
     * @return:java.util.List<T>
     */
    List<T> selectList(String collectName, Class<T> clazz);

    /**
     * 功能描述: 分页查询列表信息
     *
     * @param collectName 集合名称
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @return:java.util.List<T>
     */
    List<T> selectList(String collectName, Class<T> clazz, Integer currentPage, Integer pageSize);

    /**
     * 功能描述: 根据条件查询集合
     *
     * @param collectName 集合名称
     * @param conditions  查询条件，目前查询条件处理的比较简单，仅仅做了相等匹配，没有做模糊查询等复杂匹配
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @return:java.util.List<T>
     */
    List<T> selectByCondition(String collectName, Map<String, String> conditions, Class<T> clazz, Integer currentPage, Integer pageSize);

}
