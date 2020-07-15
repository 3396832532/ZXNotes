package proxy.advance.two;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class MyProxy {

    public static Object newProxyInstance(Class inface, MyInvocationHandler h) throws Exception {
        String rt = "\n\r";
        String methodStr = "";
        Method[] methods = inface.getMethods(); //获取接口的所有方法 , 为所有这些方法都生成代理
        /*
        原来固定的思路 : 只能对时间代理
		for(Method m : methods) {
			methodStr += "@Override" + rt +
						 "public void " + m.getName() + "() {" + rt +
						 	"   long start = System.currentTimeMillis();" + rt +
							"   System.out.println(\"start time : \" + start);" + rt +
							"   t." + m.getName() + "();" + rt +
							"   long end = System.currentTimeMillis();" + rt +
							"   System.out.println("spend all time : " + (end - start)/1000 + "s.");" + rt +
						 "}";
		}
		*/
        for (Method m : methods) {
            methodStr += "    @Override" + rt +
                    "    public void " + m.getName() + "(){" + rt +
                    "       try {" + rt +
                    "           Method md = " + inface.getName() + ".class.getMethod(\"" + m.getName() + "\");" + rt +   //这个接口传入了 ,注意一定要写inface.getName
                    "           h.invoke(this, md);" + rt +
                    "       }catch(Exception e) {" + rt +
                    "           e.printStackTrace();" + rt +
                    "       }" + rt +
                    "   }";
        }

        String src =
                "package proxy.advance.two;" + rt +
                        "import java.lang.reflect.Method;" + rt +
                        "public class My$Proxy0 implements " + inface.getName() + "{" + rt +
                        "    proxy.advance.two.MyInvocationHandler h;" + rt + //定义成员变量 MyInvocationHandler对象
                        "    public My$Proxy0(MyInvocationHandler h) {" + rt +
                        "        this.h = h;" + rt +
                        "    }" + rt +
                        methodStr + rt +
                        "}";

        //把源码写到java文件里
        File file = new File("/home/zxzxin/Java_Maven/DesignPatterns/src/main/java/proxy/advance/two/My$Proxy0.java");
        FileWriter fw = new FileWriter(file);
        fw.write(src);
        fw.flush();
        fw.close();

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
        Class c = ul.loadClass("proxy.advance.two.My$Proxy0");
//        System.out.println("Class c : " + c);

        // 这是之前的
        //  生成实例return c.newInstance();   //c.newInstance()会调用无参数的Constructor，若类没有无参的Constructor时会出错
//        Constructor ctr = c.getConstructor(Movable.class);   // 可以得到带有参数的构造方法()
//        return ctr.newInstance(new Tank());
        Constructor ctr = c.getConstructor(MyInvocationHandler.class);  // 哪个处理器实现，就创建这个类的实例对象　
        Object m = ctr.newInstance(h);
        return m;
    }
}
