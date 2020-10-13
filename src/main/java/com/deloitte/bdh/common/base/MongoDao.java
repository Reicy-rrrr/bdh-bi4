package com.deloitte.bdh.common.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 操作MongoDB的DAO基类
 *
 * @author chenghzhang
 * @date 2020/10/11
 */
@Slf4j
public abstract class MongoDao<T> {
    /**
     * 反射获取泛型类型
     *
     * @return
     */
    protected abstract Class<T> getEntityClass();

    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     * 保存一个对象
     * @param t
     */
    public void save(T t) {
        this.mongoTemplate.save(t);
    }

    /**
     * 插入一条记录到集合中
     *
     * @param t
     */
    public void insert(T t) {
        this.mongoTemplate.insert(t);
    }

    /**
     * 批量插入记录到集合中
     *
     * @param infos
     */
    public void insertBatch(List<T> infos) {
        this.mongoTemplate.insert(infos);
    }

    /***
     * 根据id从集合中查询对象
     * @param id
     * @return
     */
    public T selectById(Integer id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 根据条件查询集合
     *
     * @param object
     * @return
     */
    public List<T> selectList(T object) {
        Query query = buildQuery(object);
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 根据条件查询只返回一个文档
     *
     * @param object
     * @return
     */
    public T selectOne(T object) {
        Query query = buildQuery(object);
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /***
     * 根据条件分页查询
     * @param object
     * @param page 当前页码
     * @param size 每页记录数
     * @return
     */
    public List<T> selectListByPage(T object, int page, int size) {
        Query query = buildQuery(object);
        // 设置分页参数，mongo是从第0条开始计算
        query.skip(size * (page - 1));
        query.limit(size);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    /***
     * 根据条件查询库中符合条件的记录数量
     * @param object
     * @return
     */
    public long count(T object) {
        Query query = buildQuery(object);
        return this.mongoTemplate.count(query, this.getEntityClass());
    }

    /***
     * 删除对象
     * @param t
     * @return
     */
    public int delete(T t) {
        return (int) this.mongoTemplate.remove(t).getDeletedCount();
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    public void deleteById(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        if (null != criteria) {
            Query query = new Query(criteria);
            T obj = this.mongoTemplate.findOne(query, this.getEntityClass());
            if (obj != null) {
                this.delete(obj);
            }
        }
    }

    /*MongoDB中更新操作分为三种
     * 1：updateFirst     修改第一条
     * 2：updateMulti     修改所有匹配的记录
     * 3：upsert  修改时如果不存在则进行添加操作
     * */

    /**
     * 修改匹配到的第一条记录
     *
     * @param srcObj
     * @param targetObj
     */
    public void updateFirst(T srcObj, T targetObj) {
        Query query = buildQuery(srcObj);
        Update update = buildUpdate(targetObj);
        this.mongoTemplate.updateFirst(query, update, this.getEntityClass());
    }

    /***
     * 修改匹配到的所有记录
     * @param srcObj
     * @param targetObj
     */
    public void updateBatch(T srcObj, T targetObj) {
        Query query = buildQuery(srcObj);
        Update update = buildUpdate(targetObj);
        this.mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    /***
     * 修改匹配到的记录，若不存在该记录则进行添加
     * @param srcObj
     * @param targetObj
     */
    public void updateInsert(T srcObj, T targetObj) {
        Query query = buildQuery(srcObj);
        Update update = buildUpdate(targetObj);
        this.mongoTemplate.upsert(query, update, this.getEntityClass());
    }

    /**
     * 将查询条件对象转换为Query对象
     *
     * @param object
     * @return
     * @author Jason
     */
    private Query buildQuery(T object) {
        Query query = new Query();
        String[] fields = getFieldName(object);
        Criteria criteria = new Criteria();
        for (int i = 0; i < fields.length; i++) {
            String filedName = (String) fields[i];
            Object filedValue = getFieldValueByName(filedName, object);
            if (filedValue != null) {
                criteria.and(filedName).is(filedValue);
            }
        }
        query.addCriteria(criteria);
        return query;
    }

    /**
     * 将更新条件对象转换为Update对象
     *
     * @param object
     * @return
     * @author Jason
     */
    private Update buildUpdate(T object) {
        Update update = new Update();
        String[] fields = getFieldName(object);
        for (int i = 0; i < fields.length; i++) {
            String filedName = (String) fields[i];
            Object filedValue = getFieldValueByName(filedName, object);
            if (filedValue != null) {
                update.set(filedName, filedValue);
            }
        }
        return update;
    }

    /***
     * 获取对象属性返回字符串数组
     * @param o
     * @return
     */
    private static String[] getFieldName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; ++i) {
            fieldNames[i] = fields[i].getName();
        }

        return fieldNames;
    }

    /***
     * 根据属性获取对象属性值
     * @param fieldName
     * @param o
     * @return
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String e = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + e + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[0]);
            return method.invoke(o, new Object[0]);
        } catch (Exception var6) {
            return null;
        }
    }
}
