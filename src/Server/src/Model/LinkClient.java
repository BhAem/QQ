package Model;

import Common.Message;
import Common.ThreadSet;
import Common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LinkClient{
    private ServerSocket ss = null;

    public LinkClient() {
        try {
            this.ss = new ServerSocket(8081);
            while (true){
                Socket s = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Message msg = (Message) ois.readObject();
                User user = (User) msg.getData();

                Message message = new Message();
                message.setReceiver(msg.getReceiver());
                message.setSender(msg.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                if (msg.getCmd().equals("login")) {
                    ProcessMsg processMsg = new ProcessMsg();
                    message.setFlag(processMsg.checkUser(user));

                    if (message.isFlag() && ThreadSet.getThread(message.getSender()) == null) {
                        ServerThread st = new ServerThread(s);
                        ThreadSet.addThread(message.getSender(), st);
                        st.start();
                        message.setResult("登陆成功");
                    } else {
                        message.setResult("账号密码输入错误！");
                        s.close();
                    }
                    oos.writeObject(message);

                } else if (msg.getCmd().equals("checkregist")) {

                    ProcessMsg processMsg = new ProcessMsg();
                    message.setFlag(processMsg.checkregistUser(user));
                    if(message.isFlag()) {
                        message.setResult("该账号已存在,系统将为您重现编号");
                    }
                    oos.writeObject(message);

                } else if (msg.getCmd().equals("regist")) {
                    ProcessMsg processMsg = new ProcessMsg();
                    boolean flag = processMsg.registUser(user);
                    if (flag) {
                        message.setFlag(true);
                        message.setResult("注册成功");
                    } else {
                        message.setFlag(false);
                        message.setResult("注册出错");
                        s.close();
                    }
                    oos.writeObject(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}