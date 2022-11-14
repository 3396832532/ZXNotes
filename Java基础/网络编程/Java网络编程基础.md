# Java网络编程基础

* [一、InetAddress使用](#一inetaddress使用)
* [二、使用UDP协议发送数据](#二使用udp协议发送数据)
* [三、使用TCP协议发送数据](三#使用tcp协议发送数据)
* [四、使用TCP协议发送端发送小写，接收端返回大写给发送端](#四使用tcp协议发送端发送小写接收端返回大写给发送端)

## 一、InetAddress使用

```java
/**
 * InetAddress的使用
 可以通过ip获取到，也可以通过主机名获取InetAddress对象
 InetAddress的常用方法：getLocalHost()
                      getHostName()
 */
public class InetAddressDemo {

    public static void main(String[] args) throws UnknownHostException {
        // ip地址是唯一的
        InetAddress inet = InetAddress.getByName("172.22.20.161");

        System.out.println(InetAddress.getLocalHost());

        System.out.println(inet.getHostAddress());
        System.out.println(inet.getHostName());

    }
}
```

## 二、使用UDP协议发送数据

发送端代码:

```java
/**
 * 如果是主机名错误会抛出异常，但是如果是端口号错误就不会抛出异常
 * 但是一个端口号不能重复的开启
 */
public class Sender {

    public static void main(String[] args) throws IOException {
        //创建发送数据的 Socket对象
        DatagramSocket ds = new DatagramSocket();

        // 创建包对象
        byte[] bys = "hello, upd, im coming!".getBytes();
        // 发送数据包的对象， 1、发送数据的字节数组，2、长度,3、接收端的ip、4、接收端的端口
        DatagramPacket dp = new DatagramPacket(bys, bys.length,
                InetAddress.getByName("zxzxin"), 9999);

        // 发送数据
        ds.send(dp);

        // 释放资源
        ds.close();
    }
}
```

接收端代码：

```java
/**
 * 使用UDP协议发送数据
 */
public class Receiver {

    public static void main(String[] args) throws IOException {
        // 创建接收端Socket对象, 并指定接收的端口
        DatagramSocket ds = new DatagramSocket(9999);

        // 创建包对象，bys可以用来接收数据
        byte[] bys = new byte[1024];
        DatagramPacket dp = new DatagramPacket(bys, bys.length);

        //接收数据
        ds.receive(dp); //这里会阻塞

        //解析数据
        //获取发送端IP对象
        InetAddress inet = dp.getAddress();

        // 获取数据
        byte[] data = dp.getData();

        // 获取数据的长度
        int len = dp.getLength();

        System.out.println("sender --> " + inet.getHostName());

        System.out.println(new String(data, 0, len));
    }
}
```

## 三、使用TCP协议发送数据

发送端:

```java
/**
 * 使用TCP协议发送数据
 *     1、创建发送端Socket对象(创建连接)
 *     2、获取输出流对象
 *     3、发送数据
 *     4、释放连接
 *
 * 但是要注意的是: 没有接收端，自己运行发送端的话，会抛出异常java.net.ConnectException: Connect refused
 */
public class Sender {

    public static void main(String[] args) throws IOException {
        //1、创建发送端Socket对象(创建连接)
        Socket socket = new Socket(InetAddress.getByName("zxzxin"), 10086);

        //2、获取输出流对象
        OutputStream os = socket.getOutputStream();

        //3、发送数据

        String str = "hello, tcp, im coming!";
        os.write(str.getBytes());

        //4、释放连接
//        os.close(); //socket会帮我们关闭
        socket.close();
    }
}
```

接收端:

```java

/**
 * 使用TCP协议接收数据
 *    1)、创建接收端Socket对象 (ServerSocket)
 *    2)、监听，阻塞
 *    3)、获取输入流对象
 *    4)、获取数据
 *    5)、输出数据
 *    6)、释放资源
 *
 * 使用ServerSocket类来接收.
 */
public class Receiver {


    public static void main(String[] args) throws IOException {

        // 1)、创建接收端Socket对象
        ServerSocket serverSocket = new ServerSocket(10086);

        // 2)、侦听并接收到此套接字的连接 accept()方法
        Socket socket = serverSocket.accept();

        // 3)、获取输入流对象
        InputStream is = socket.getInputStream();

        // 4)、获取数据
        InetAddress inet = socket.getInetAddress();
        System.out.println("sender --> " + inet.getHostName());

        byte[] bys = new byte[1024];
        int len = is.read(bys);
        System.out.println(new String(bys, 0, len));

        socket.close();
//        serverSocket.close(); // 注意服务端不要关
    }
}
```

## 四、使用TCP协议发送端发送小写，接收端返回大写给发送端

Client:

```java
/**
 案例: 客户端发送数据，服务端接收到数据，转换成大写，并返回给客户端
 */
public class Client {

    public static void main(String[] args) throws IOException {
        //1、创建发送端Socket对象(创建连接)
        Socket socket = new Socket(InetAddress.getByName("zxzxin"), 10086);

        //2、获取输出流对象
        OutputStream os = socket.getOutputStream();

        //3、发送数据

        String str = "hello, tcp, im coming!";
        os.write(str.getBytes());

        // 接收客户端返回的大写的字符串
        InputStream is = socket.getInputStream();
        byte[] bys = new byte[1024];
        int len = is.read(bys);
        System.out.println(new String(bys, 0, len));

        //4、释放连接
//        os.close(); //socket会帮我们关闭
        socket.close();
    }
}

```

Server:

```java
/**
    接收端: 接收客户端的字符串，转换成大写，然后返回给客户端
 */
public class Server {

    public static void main(String[] args) throws IOException {

        // 1)、创建接收端Socket对象
        ServerSocket serverSocket = new ServerSocket(10086);

        // 2)、侦听并接收到此套接字的连接 accept()方法
        Socket socket = serverSocket.accept();

        // 3)、获取输入流对象
        InputStream is = socket.getInputStream();

        // 4)、获取数据
        InetAddress inet = socket.getInetAddress();
        System.out.println("sender --> " + inet.getHostName());

        byte[] bys = new byte[1024];
        int len = is.read(bys);

        String str = new String(bys, 0, len);
        System.out.println(str);

        String upperStr = str.toUpperCase();

        OutputStream os = socket.getOutputStream();

        //返回数据给客户端
        os.write(upperStr.getBytes());

        socket.close();
//        serverSocket.close(); // 注意服务端不要关

    }
}
```

