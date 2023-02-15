package Model;

import Common.Message;
import Common.ThreadSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LinkServer{
    private int port = 8081;
    private String address = "127.0.0.1";
    private Socket socket;

    public LinkServer() {}

    // 向服务器发送登录请求信息
    public Message sendLoginInfo2Server(Message message) {
        Message ms = null;
        try {
            // 向服务器发送账号信息
            socket = new Socket("127.0.0.1", 8081);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);

            // 从服务器收到验证是否通过的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ms = (Message) ois.readObject();
            // 验证登录是否成功
            if (ms.isFlag()) {
                // 创建一个该用户和服务器保持通讯连接的线程并启动
                ClientThread ct = new ClientThread(socket);
                ct.start();
                ThreadSet.addThread(ms.getReceiver(), ct);
            } else {
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ms;
    }

    // 向服务器发送注册请求信息
    public Message sendRegisterInfo2Server(Message message) {
        Message ms = null;
        try {
            socket = new Socket("127.0.0.1", 8081);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            // 从服务器收到验证是否通过的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ms = (Message) ois.readObject();
            // 验证注册是否成功
            if (!ms.isFlag()) {
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ms;
    }
}