package com.csm.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MetaObjectHandler是mybatisPlus提供的填充公共数据的接口，这里我们将他实现方法
 */
@Component//交给Spring容器管理
@Slf4j//日志
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 在插入操作时，填充字段
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert时公共字段自动填充...");
        //这里可以在打断点或者日志中输出metaObject看看里面的内容
        log.info(metaObject.toString());
        //输出检查一下当前操作线程的Id看看是否和登录是同一个线程
        log.info("当前线程的id为 {}", Thread.currentThread().getId());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("creatUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 在更新操作时自动填充字段
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update时公共字段自动填充...");
        log.info(metaObject.toString());
        //输出检查一下当前操作线程的Id看看是否和登录是同一个线程
        log.info("当前线程的id为 {}", Thread.currentThread().getId());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
