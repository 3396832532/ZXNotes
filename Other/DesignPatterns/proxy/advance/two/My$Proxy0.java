package proxy.advance.two;
import java.lang.reflect.Method;
public class My$Proxy0 implements proxy.advance.two.Flyable{
    proxy.advance.two.MyInvocationHandler h;
    public My$Proxy0(MyInvocationHandler h) {
        this.h = h;
    }
    @Override
    public void fly(){
       try {
           Method md = proxy.advance.two.Flyable.class.getMethod("fly");
           h.invoke(this, md);
       }catch(Exception e) {
           e.printStackTrace();
       }
   }
}