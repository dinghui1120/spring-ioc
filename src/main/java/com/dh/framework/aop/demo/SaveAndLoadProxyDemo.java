package com.dh.framework.aop.demo;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 保存代理类
 */
public class SaveAndLoadProxyDemo {

    public interface HelloService {
        void sayHello(String name);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SaveAndLoadProxyDemo.class.getClassLoader().getResource(""));
        HelloService target = new HelloService() {
            @Override
            public void sayHello(String name) {
                System.out.println("Hello, " + name);
            }
        };

        // 创建代理对象
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("[代理前置逻辑]");
                Object result = method.invoke(target, args);
                System.out.println("[代理后置逻辑]");
                return result;
            }
        };

        // 保存并加载代理类
        HelloService proxy = (HelloService) createProxyAndSave(
                "MyProxyClass", // 代理类的全限定名
                "E:/company/test/workspace/spring/spring-ioc/target/SaveProxy",        // 保存目录 (注意根据你的项目结构改一下)
                handler,
                HelloService.class
        );

        proxy.sayHello("cat");
    }

    // 生成、保存、加载、实例化 代理类
    public static Object createProxyAndSave(String simpleClassName, String saveDir,
                                            InvocationHandler handler, Class<?>... interfaces) throws Exception {
        // 生成字节码
        byte[] classFile = ProxyGenerator.generateProxyClass(simpleClassName, interfaces);

        // 保存到指定目录（不要加包结构）
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File proxyClassFile = new File(dir, simpleClassName + ".class");
        try (FileOutputStream out = new FileOutputStream(proxyClassFile)) {
            out.write(classFile);
            System.out.println("代理类已保存到：" + proxyClassFile.getAbsolutePath());
        }

        // 加载
        URL[] urls = { new File(saveDir).toURI().toURL() };
        URLClassLoader classLoader = new URLClassLoader(urls);
        Class<?> proxyClass = classLoader.loadClass(simpleClassName); // 👈 注意这里也是简单名字
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(handler);
    }

}
