package proxy.advance.one;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class MyProxy {

    // 用来产生代理类
    public static Object newProxyInstance() throws Exception{
        String rt = "\n\r";

        //动态代理文件的源码 : 需要动态编译代码
        String src = "package proxy.advance.one;" + rt +
                "public class TankTimeProxy implements Movable {" + rt +
                "      private Movable tank;" + rt +
                "      public TankTimeProxy(Movable tank) {" + rt +
                "             this.tank = tank;" + rt +
                "      }" + rt +
                "     @Override" + rt +
                "     public void move() {" + rt +
                "          long start = System.currentTimeMillis();" + rt +
                "          System.out.println(\"start time : \" + start);" + rt +
                "          tank.move();" + rt +
                "          long end = System.currentTimeMillis();" + rt +
                "          System.out.println(\"end time : \" + end);" + rt +
                "          System.out.println(\"spend all time : \" + (end - start)/1000 + \"s.\");" + rt +
                "      }" + rt +
                "}";

        //把源码写到java文件里
        File file = new File("/home/zxzxin/Java_Maven/DesignPatterns/src/main/java/proxy/advance/one/TankTimeProxy.java");
        FileWriter fw = new FileWriter(file);
        fw.write(src);  fw.flush(); fw.close();

        //下面的代理，就是动态编译
        //编译源码，生成class,注意编译环境要换成jdk才有compiler,单纯的jre没有compiler，会空指针错误
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileMgr = jc.getStandardFileManager(null, null, null);//文件管事器
        Iterable units = fileMgr.getJavaFileObjects(file); //编译单元
        JavaCompiler.CompilationTask t = jc.getTask(null, fileMgr, null, null, null, units);//编译任务
        t.call();
        fileMgr.close();

        //把类load到内存里 并　生成新对象       !!!!!注意:下面的home前面不要加 /
        URL[] urls = new URL[]{new URL("file:/" + "home/zxzxin/Java_Maven/DesignPatterns/src/main/java/")};
        URLClassLoader ul = new URLClassLoader(urls);
        Class c = ul.loadClass("proxy.advance.one.TankTimeProxy");

        //生成实例return c.newInstance();   //c.newInstance()会调用无参数的Constructor，若类没有无参的Constructor时会出错
        Constructor ctr = c.getConstructor(Movable.class);   // 可以得到带有参数的构造方法()
        return ctr.newInstance(new Tank());
    }
}
