package com.dh.framework.aop.aspect.joinpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接点上下文
 * 使用ThreadLocal实现通知间的属性共享
 */
public class JoinPointContext {

    /**
     * 连接点属性
     */
    private static final ThreadLocal<Map<String, Object>> ATTRIBUTES_HOLDER = 
            ThreadLocal.withInitial(HashMap::new);

    /**
     * 创建资源回收监听器
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ATTRIBUTES_HOLDER.remove();
            } catch (Exception e) {
                // 忽略关闭时的异常
            }
        }));
    }
    
    /**
     * 设置属性值
     */
    public static void setAttribute(String key, Object value) {
        ATTRIBUTES_HOLDER.get().put(key, value);
    }
    
    /**
     * 获取属性值
     */
    public static Object getAttribute(String key) {
        return ATTRIBUTES_HOLDER.get().get(key);
    }
    
    /**
     * 获取所有属性
     */
    public static Map<String, Object> getAttributes() {
        return ATTRIBUTES_HOLDER.get();
    }
    
    /**
     * 清理当前线程的所有属性
     */
    public static void clear() {
        ATTRIBUTES_HOLDER.get().clear();
    }

} 