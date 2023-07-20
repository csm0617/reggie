package com.csm.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id，作用域仅限于当前线程
 * */
public class BaseContext {
   private static ThreadLocal<Long> threadLocal=  new ThreadLocal<>();


    /**
     * 设置threadLocal的值
     * @param id
     */
   public static void  setCurrentId(Long id){
       threadLocal.set(id);
   }


    /**
     * 取出threadLocal的值
     * @return
     */
   //
   public static Long  getCurrentId(){
       return threadLocal.get();
   }

}
