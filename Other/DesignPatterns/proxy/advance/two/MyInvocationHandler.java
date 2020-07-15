package proxy.advance.two;

import java.lang.reflect.Method;

/**
 * 能处理任何方法的	调用  只要给我一个Method就能对这个方法进行特殊的处理
 * 特殊处理的方式是由子类(实现类)决定
 */
public interface MyInvocationHandler {
    void invoke(Object o, Method m);
}
