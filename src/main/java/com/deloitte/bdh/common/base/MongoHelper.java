package com.deloitte.bdh.common.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB 操作工具类
 *
 * @author chenghzhang
 * @date 2020/10/10
 */
@Repository
public class MongoHelper<T> {

    /**
     * 注入template
     */
    private MongoTemplate mongoTemplate;

    /**
     * 功能描述: 创建一个集合
     * 同一个集合中可以存入多个不同类型的对象，我们为了方便维护和提升性能，
     * 后续将限制一个集合中存入的对象类型，即一个集合只能存放一个类型的数据
     *
     * @param name 集合名称，相当于传统数据库的表名
     * @return:void
     */
    public void createCollection(String name) {
        mongoTemplate.createCollection(name);
    }

    /**
     * 功能描述: 创建索引
     * 索引是顺序排列，且唯一的索引
     *
     * @param collectionName 集合名称，相当于关系型数据库中的表名
     * @param filedName      对象中的某个属性名
     * @return:java.lang.String
     */
    public String createIndex(String collectionName, String filedName) {
        //配置索引选项
        IndexOptions options = new IndexOptions();
        // 设置为唯一
        options.unique(true);
        //创建按filedName升序排的索引
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(filedName), options);
    }


    /**
     * 功能描述: 获取当前集合对应的所有索引的名称
     *
     * @param collectionName
     * @return:java.util.List<java.lang.String>
     */
    public List<String> getAllIndexes(String collectionName) {
        ListIndexesIterable<Document> list = mongoTemplate.getCollection(collectionName).listIndexes();
        //上面的list不能直接获取size，因此初始化arrayList就不设置初始化大小了
        List<String> indexes = new ArrayList<>();
        for (Document document : list) {
            document.entrySet().forEach((key) -> {
                //提取出索引的名称
                if (key.getKey().equals("name")) {
                    indexes.add(key.getValue().toString());
                }
            });
        }
        return indexes;
    }

    /**
     * 功能描述: 往对应的集合中插入一条数据
     *
     * @param info           存储对象
     * @param collectionName 集合名称
     * @return:void
     */
    public void insert(T info, String collectionName) {
        mongoTemplate.insert(info, collectionName);
    }

    /**
     * 功能描述: 往对应的集合中批量插入数据，注意批量的数据中不要包含重复的id
     *
     * @param infos 对象列表
     * @return:void
     */

    public void insertBatch(List<T> infos, String collectionName) {
        mongoTemplate.insert(infos, collectionName);
    }

    /**
     * 功能描述: 使用索引信息精确更改某条数据
     *
     * @param id             唯一键
     * @param collectionName 集合名称
     * @param info           待更新的内容
     * @return:void
     */
    public void updateById(String id, String collectionName, T info) {
        final String idName = "id";
        Query query = new Query(Criteria.where(idName).is(id));
        Update update = new Update();
        String str = JSON.toJSONString(info);
        JSONObject jQuery = JSON.parseObject(str);
        jQuery.forEach((key, value) -> {
            //因为id相当于传统数据库中的主键，这里使用时就不支持更新，所以需要剔除掉
            if (!idName.equals(key)) {
                update.set(key, value);
            }
        });
        mongoTemplate.updateMulti(query, update, info.getClass(), collectionName);
    }

    /**
     * 功能描述: 根据id删除集合中的内容
     *
     * @param id             序列id
     * @param collectionName 集合名称
     * @param clazz          集合中对象的类型
     * @return:void
     */

    public void deleteById(String id, Class<T> clazz, String collectionName) {
        // 设置查询条件，当id=#{id}
        Query query = new Query(Criteria.where("id").is(id));
        // mongodb在删除对象的时候会判断对象类型，如果你不传入对象类型，只传入了集合名称，它是找不到的
        // 上面我们为了方便管理和提升后续处理的性能，将一个集合限制了一个对象类型，所以需要自行管理一下对象类型
        // 在接口传入时需要同时传入对象类型
        mongoTemplate.remove(query, clazz, collectionName);
    }

    /**
     * 功能描述: 根据id查询信息
     *
     * @param id             注解
     * @param clazz          类型
     * @param collectionName 集合名称
     * @return:java.util.List<T>
     */

    public T selectById(String id, Class<T> clazz, String collectionName) {
        // 查询对象的时候，不仅需要传入id这个唯一键，还需要传入对象的类型，以及集合的名称
        return mongoTemplate.findById(id, clazz, collectionName);
    }

    /**
     * 功能描述: 查询列表信息
     * 将集合中符合对象类型的数据全部查询出来
     *
     * @param collectName 集合名称
     * @param clazz       类型
     * @return:java.util.List<T>
     */
    public List<T> selectList(String collectName, Class<T> clazz) {
        return selectListByPage(collectName, clazz, null, null);
    }

    /**
     * 功能描述: 根据条件查询列表信息
     *
     * @param collectName 集合名称
     * @param conditions  查询条件，目前查询条件处理的比较简单，仅仅做了相等匹配，没有做模糊查询等复杂匹配
     * @param clazz       对象类型
     * @return:java.util.List<T>
     */

    public List<T> selectList(String collectName, Map<String, String> conditions, Class<T> clazz) {
        if (ObjectUtils.isEmpty(conditions)) {
            return selectList(collectName, clazz);
        } else {
            // 设置分页参数，mongo是从第0条开始计算
            Query query = new Query();
            // 往query中注入查询条件
            conditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
            return mongoTemplate.find(query, clazz, collectName);
        }
    }

    /**
     * 功能描述: 分页查询列表信息
     *
     * @param collectName 集合名称
     * @param clazz       对象类型
     * @param page        当前页码
     * @param size        每页条数
     * @return:java.util.List<T>
     */
    public List<T> selectListByPage(String collectName, Class<T> clazz, Integer page, Integer size) {
        // 设置分页参数
        Query query = new Query();
        // 设置分页信息，mongo是从第0条开始计算
        if (!ObjectUtils.isEmpty(page) && ObjectUtils.isEmpty(size)) {
            query.skip(size * (page - 1));
            query.limit(size);
        }
        return mongoTemplate.find(query, clazz, collectName);
    }


    /**
     * 功能描述: 根据条件分页查询列表信息
     *
     * @param collectName 集合名称
     * @param conditions  查询条件，目前查询条件处理的比较简单，仅仅做了相等匹配，没有做模糊查询等复杂匹配
     * @param clazz       对象类型
     * @param page        当前页码
     * @param size        每页条数
     * @return:java.util.List<T>
     */

    public List<T> selectListByPage(String collectName, Map<String, String> conditions, Class<T> clazz, Integer page, Integer size) {
        if (ObjectUtils.isEmpty(conditions)) {
            return selectListByPage(collectName, clazz, page, size);
        } else {
            // 设置分页参数，mongo是从第0条开始计算
            Query query = new Query();
            query.skip(size * (page - 1));
            query.limit(size);
            // 往query中注入查询条件
            conditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
            return mongoTemplate.find(query, clazz, collectName);
        }
    }

    /**
     * 功能描述: 根据条件（已经定义好的Query对象）查询集合
     *
     * @param collectName 集合名称
     * @param query       查询条件
     * @param clazz       对象类型
     * @return:java.util.List<T>
     */
    public List<T> selectList(String collectName, Query query, Class<T> clazz) {
        if (query == null) {
            query = new Query();
        }
        List<T> result = mongoTemplate.find(query, clazz, collectName);
        return result;
    }

    /**
     * 根据条件查询库中符合条件的记录数量
     *
     * @param collectName 集合名称
     * @return 记录数量
     */
    public long count(String collectName) {
        Query query = new Query();
        return this.mongoTemplate.count(query, collectName);
    }

    /**
     * 根据条件查询库中符合条件的记录数量
     *
     * @param collectName 集合名称
     * @param conditions  查询条件
     * @return 记录数量
     */
    public long count(String collectName, Map<String, String> conditions) {
        Query query = new Query();
        // 往query中注入查询条件
        conditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        return this.mongoTemplate.count(query, collectName);
    }

    /**
     * 功能描述: 根据条件（已经定义好的Query对象）查询集合记录数量
     *
     * @param collectName 集合名称
     * @param query       查询条件
     * @return 记录数量
     */
    public long count(String collectName, Query query) {
        if (query == null) {
            query = new Query();
        }
        return this.mongoTemplate.count(query, collectName);
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
