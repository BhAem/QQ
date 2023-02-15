package Model;

import Common.ChatViewSet;
import Common.ListSet;
import Common.Message;
import View.Chat;
import View.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.*;

public class ClientThread extends Thread {
    private Socket s;

    public ClientThread(Socket s) {
        this.s = s;
    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public void run() {
        while (true) {
            // 循环读取从服务器发来的消息
            try {
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Message m = (Message) ois.readObject();
                //处理文字信息结果
                if (m.getCmd().equals("message")) {
                    if(!m.isFlag()) {
                        JOptionPane.showMessageDialog(null, m.getResult());
                    } else {
                        // 从服务器获得的消息显示到相应的聊天界面
                        Chat qqChat = ChatViewSet.getChat(m.getReceiver() + " " + m.getSender());
                        if(qqChat == null){
                            qqChat = new Chat(m.getReceiver(), m.getSender());
                            ChatViewSet.addChat(m.getReceiver() + " " + m.getSender(), qqChat);
                        }
                        qqChat.showMessage(m);
                    }
                    //处理文件传输结果
                } else if ("filemessage".equals(m.getCmd())){
                    Chat qqChat = ChatViewSet.getChat(m.getReceiver() + " " + m.getSender());
                    if(qqChat == null){
                        qqChat = new Chat(m.getReceiver(), m.getSender());
                        // 把聊天界面加入到管理类
                        ChatViewSet.addChat(m.getReceiver() + " " + m.getSender(), qqChat);
                    }
                    File gettedfile = (File)m.getData();
                    File file = new File("src/Client/src/GettedFile/" + gettedfile.getName());
                    FileInputStream fis = new FileInputStream(gettedfile);
                    FileOutputStream fos = new FileOutputStream(file);
                    try{
                        byte[] buf = new byte[1024];
                        int n = 0;
                        while((n = fis.read(buf)) != -1){
                            fos.write(buf);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    m.setData(gettedfile.getName() + " -----接收成功");
                    qqChat.showMessage(m);
                    // 处理获取好友结果
                } else if (m.getCmd().equals("getFriends"))  {
                    String receiver = m.getReceiver();
                    List friendList = ListSet.getFriendList(receiver);
                    // 更新列表
                    if (friendList != null) {
                        friendList.initList(m);
                    }
                    // 处理获取好友数量结果
                } else if (m.getCmd().equals("getFriendsNum")) {
                    String receiver = m.getReceiver();
                    List friendList = ListSet.getFriendList(receiver);
                    // 更新在线好友
                    if (friendList != null) {
                        friendList.updateList(m);
                    }
                    // 处理删除好友结果
                } else if (m.getCmd().equals("deleteTheFriend")) {
                    String receiver = m.getReceiver();
                    List friendList = ListSet.getFriendList(receiver);
                    if (friendList != null) {
                        friendList.dealWithDeleteFriend(m);
                    }
                    // 处理添加好友结果
                } else if (m.getCmd().equals("addTheFriend")) {
                    String receiver = m.getReceiver();
                    List friendList = ListSet.getFriendList(receiver);
                    if (friendList != null) {
                        friendList.dealWithAddFriend(m);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}