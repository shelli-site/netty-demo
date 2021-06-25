package com.demo.netty;


import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.aspects.Aspect;
import cn.hutool.core.lang.Console;

import java.lang.reflect.Method;

/**
 * create by shen_xi on 2021/05/25
 */
public class ProxyTest {
    public static class Dog {
        public String eat(String food) {
            if ("meat".equals(food)) {
                return "happy";
            }
            return "sad";
        }
    }

    public static void main(String[] args) {
        Dog dog = ProxyUtil.proxy(new Dog(), new Aspect() {
            @Override
            public boolean before(Object target, Method method, Object[] args) {
                Console.log("{} {} {}", target, method, args);
                return true;
            }

            @Override
            public boolean after(Object target, Method method, Object[] args, Object returnVal) {
                return true;
            }

            @Override
            public boolean afterException(Object target, Method method, Object[] args, Throwable e) {
                return false;
            }
        });
        Console.log(dog.eat("shit"));
    }
}
