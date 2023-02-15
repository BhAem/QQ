package View;

import Common.ChatViewSet;
import Common.Message;
import Common.ThreadSet;
import Common.User;
import Model.LinkServer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;

public class List extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JPanel friendPanel;
    private JScrollPane jsp;
    private JTextField jtf;
    private JButton addFriend, deleteFriend;
    private String you;
    private JLabel friends[];
    private String[] friendList;
    private int len;   //当前好友数量

    public void initList(Message message) {

        if (message != null) {
            if (message.isFlag()) {
                //获取好友账号数组
                Object[] temp = message.getArrayData();
                friendList = Arrays.copyOf(temp, temp.length, String[].class);
                len = friendList.length;
                for (int i = 0; i < len; i++) {
                    Random random = new Random();
                    int j = random.nextInt(3) + 1;
                    ImageIcon icon = new ImageIcon("src/Client/src/Images/" + j + ".jpeg");
                    icon.setImage(icon.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
                    friends[i] = new JLabel(friendList[i] + "", icon, JLabel.LEFT); //①
                    friends[i].addMouseListener(new MyMouseListener());
                    String t = "删除" + friendList[i];
                    deleteFriend = new JButton(t);
                    deleteFriend.setContentAreaFilled(false); // 透明化处理
                    deleteFriend.setSize(10,10);

                    Box hBox01 = Box.createHorizontalBox();
                    hBox01.add(friends[i]);
                    hBox01.add(Box.createHorizontalGlue());
                    hBox01.add(deleteFriend);
                    friendPanel.add(hBox01);

                    String[] obj = new String[]{you, friendList[i]};
                    ActionListener listener = new ChangeListener(friendPanel, hBox01, t, obj, you);
                    deleteFriend.addActionListener(listener);
                }
            }
        }

        jsp = new JScrollPane(friendPanel);
        jsp.setBounds(0, 55, 287, 445);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(jsp);
        add(jtf);
        add(addFriend);

        setLayout(null);
        setTitle(you + "'s FriendList");
        setSize(300, 500);
        setLocation(1100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void updateList(Message msg) {
        //从数据库中获取当前好友数量
        len = 0;
        if(msg.isFlag()) {
            len = Integer.parseInt(msg.getData().toString());
        }

        if(len == 50) { //超过50个则提示达到好友上限
            JOptionPane.showMessageDialog(this, "超过50个好友上限");
        } else {
            String account = jtf.getText().trim();
            //处理一些细节
            if (account.equals(you)) {
                JOptionPane.showMessageDialog(this, "不能添加自己为好友");
                return;
            }
            String[] obj = new String[]{you, account};
            Message message = new Message();
            message.setCmd("addTheFriend");
            message.setArrayData(obj);
            message.setReceiver(you);
            message.setSender(you);

            try{
                ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
                oos.writeObject(message);
            } catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }

    public void dealWithAddFriend (Message message){
        String account = (String)message.getArrayData()[1];
        if (message != null) {
            if (message.isFlag()) {
                JOptionPane.showMessageDialog(null, message.getResult());
                len++;  //好友数量+1
                Random random = new Random();
                int j = random.nextInt(3) + 1;
                ImageIcon icon = new ImageIcon("src/Client/src/Images/" + j + ".jpeg");
                icon.setImage(icon.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
                friends[len - 1] = new JLabel(account + "", icon, JLabel.LEFT);
                friends[len - 1].addMouseListener(new MyMouseListener());
                String t = "删除" + account;
                deleteFriend = new JButton(t);
                deleteFriend.setContentAreaFilled(false); // 透明化处理
                deleteFriend.setSize(10, 10);

                Box hBox01 = Box.createHorizontalBox();
                hBox01.add(friends[len - 1]);
                hBox01.add(Box.createHorizontalGlue());
                hBox01.add(deleteFriend);
                friendPanel.add(hBox01);

                String[] obj2 = new String[]{you, account};
                ActionListener listener = new ChangeListener(friendPanel, hBox01, t, obj2, you);
                deleteFriend.addActionListener(listener);

                //添加好友后实时更新好友列表
                friendPanel.updateUI();
                friendPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, message.getResult());
            }
        }
    }

    public void dealWithDeleteFriend(Message msg) {
        if (msg != null) {
            if (msg.isFlag()) {
                JOptionPane.showMessageDialog(null, msg.getResult());
            } else {
                JOptionPane.showMessageDialog(null, msg.getResult());
            }
        }
    }

    public List() {

    }

    public void init(String you) {
        this.you = you;
        jtf = new JTextField();
        jtf.setText("输入QQ号");
        addFriend = new JButton("添加好友");
        addFriend.addActionListener(this);
        jtf.setBounds(10, 10, 190, 30);
        addFriend.setBounds(195, 10, 90, 30);
        friendPanel = new JPanel();
        friendPanel.setLayout(new GridLayout(50, 2, 4, 4));
        friends = new JLabel[50];   //限制50个好友

        // 关闭好友列表即表示退出登录，此时发送退出登录的信息给服务器（标记不在线）
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Message m =new Message();
                m.setCmd("exit");
                m.setSender(you);
                try{
                    ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
                    oos.writeObject(m);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });

        User u = new User(you);
        Message message = new Message();
        message.setCmd("getFriends");
        message.setData(u);
        message.setReceiver(you);
        message.setSender(you);

        try{
            ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e1){
            e1.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        new List("222", null);
//    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addFriend) {
            User u = new User(you);
            Message msg = new Message();
            msg.setCmd("getFriendsNum");
            msg.setData(u);
            msg.setReceiver(you);
            msg.setSender(you);

            try{
                ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
                oos.writeObject(msg);
            } catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }

    //鼠标事件监听
    class MyMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            // 双击头像或变灰处两次即弹出对话框
            if (e.getClickCount() == 2) {
                JLabel label = (JLabel) e.getSource();
                Chat chat = new Chat(you, label.getText());
                ChatViewSet.addChat(you + " " + label.getText(), chat);
            }
        }

        // 鼠标进入好友label，label的背景色变灰
        @Override
        public void mouseEntered(MouseEvent e) {
            JLabel label = (JLabel) e.getSource();
            label.setOpaque(true);
            label.setBackground(Color.GRAY);
        }

        // 鼠标离开好友label，label的背景色还原
        @Override
        public void mouseExited(MouseEvent e) {
            JLabel label = (JLabel) e.getSource();
            label.setOpaque(false);
            label.setBackground(Color.WHITE);
        }
    }

//    public static void main(String[] args) {
//        new List("aaa", null);
//    }
}

//删除好友的事件监听
class ChangeListener implements ActionListener
{
    JPanel panel;
    Box box;
    String str;
    String[] obj;
    String you;

    public ChangeListener(JPanel panel, Box hBox01, String str, String[] obj, String you) {
        super();
        this.panel = panel;
        this.box = hBox01;
        this.str = str;
        this.obj = obj;
        this.you = you;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //删除friendPanel中的指定对象
        if(str.equals(e.getActionCommand()))  {
            panel.remove(this.box);
            panel.updateUI();
            panel.repaint();
        }

        //删除数据库的数据
        Message msg = new Message();
        msg.setCmd("deleteTheFriend");
        msg.setArrayData(this.obj);
        msg.setReceiver(obj[0]);
        msg.setSender(obj[0]);

        try{
            ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e1){
            e1.printStackTrace();
        }

    }
}