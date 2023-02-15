package Model;

import Common.Message;
import Common.ThreadSet;
import Common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectOutputStream oos;
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());;
                Message message = (Message) ois.readObject();
                // 处理客户端发送来的文本或文件信息
                if ("message".equals(message.getCmd()) || "filemessage".equals(message.getCmd())) {
                    if(ThreadSet.getThread(message.getReceiver()) != null) {
                        message.setFlag(true);
                        oos = new ObjectOutputStream(ThreadSet.getThread(message.getReceiver()).socket.getOutputStream());
                    } else {
                        message.setFlag(false);
                        message.setResult("当前用户不在线");
                        oos = new ObjectOutputStream(ThreadSet.getThread(message.getSender()).socket.getOutputStream());
                    }
                    oos.writeObject(message);
                }
                //处理获取好友请求
                else if ("getFriends".equals(message.getCmd())) {
                    ProcessMsg processMsg = new ProcessMsg();
                    User user = (User) message.getData();
                    String[] friends = processMsg.getFriends(user);
                    if (friends.length != 0) {
                        message.setFlag(true);
                        message.setArrayData(friends);
                    } else {
                        message.setFlag(false);
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                }
                //处理获取好友数量请求
                else if ("getFriendsNum".equals(message.getCmd())) {
                    ProcessMsg processMsg = new ProcessMsg();
                    User user = (User) message.getData();
                    String[] friends = processMsg.getFriends(user);
                    if (friends.length != 0) {
                        message.setFlag(true);
                        message.setData(friends.length);
                    } else {
                        message.setFlag(false);
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                }
                //添加好友功能
                else if ("addTheFriend".equals(message.getCmd())) {
                    ProcessMsg processMsg = new ProcessMsg();
                    Object[] objects = message.getArrayData();
                    String[] info = Arrays.copyOf(objects, objects.length, String[].class);
                    message.setFlag(processMsg.addFriends(info));
                    if (message.isFlag()) {
                        message.setResult("添加成功");
                    } else {
                        message.setResult("不存在该用户");
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                }
                //删除好友功能
                else if ("deleteTheFriend".equals(message.getCmd())) {
                    ProcessMsg processMsg = new ProcessMsg();
                    Object[] objects = message.getArrayData();
                    String[] info = Arrays.copyOf(objects, objects.length, String[].class);
                    message.setFlag(processMsg.deleteFriends(info));
                    if (message.isFlag()) {
                        message.setResult("删除成功");
                    } else {
                        message.setResult("删除失败");
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                }
                //处理退出登录信息
                else if ("exit".equals(message.getCmd())) {
                    ThreadSet.removeThread(message.getSender()); //从线程集合中移除该用户线程
                    break;
                }

            } catch (IOException e) {
                socket = null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}