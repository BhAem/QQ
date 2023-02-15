package View;

import Common.ChatViewSet;
import Common.Message;
import Common.ThreadSet;
import Model.ClientThread;
import Model.LinkServer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class Chat extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JTextArea jTextArea;
    private JTextField jTextField;
    private JButton sendButton, fileButton;
    private JPanel jPanel;
    private String you;
    private String friend;
    private JFileChooser fc;

    public Chat(String you, String friend) {
        this.you = you;
        this.friend = friend;

        setLayout(new BorderLayout());
        fc = new JFileChooser();
        jPanel = new JPanel();
        jTextField = new JTextField(22);
        sendButton = new JButton("发送");
        fileButton = new JButton("文件");
        jPanel.add(fileButton);
        jPanel.add(jTextField);
        jPanel.add(sendButton);
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        JScrollPane scrollpane=new JScrollPane();//创建滚动条面板
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setViewportView(jTextArea);

        // 监听聊天窗口的关闭
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ChatViewSet.removeQQChat(you+" "+friend);
            }
        });

        add(scrollpane, BorderLayout.CENTER);
        add(jPanel, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
        fileButton.addActionListener(this);
        setTitle(friend);
        setSize(440, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void showMessage(Message message){
        SimpleDateFormat sdf = new SimpleDateFormat("HH点mm分钟ss秒");
        String msg = message.getSender() + "\t" +sdf.format(new Date()) + "\n" + (String) message.getData() + "\n";
        // 将接收的消息显示在聊天框中
        jTextArea.append(msg);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH点mm分钟ss秒");
            String msg = "你("+you+")"+ "\t" + sdf.format(new Date())  + "\n" + jTextField.getText() + "\n";
            // 在自己的文本框中写上发送的信息，包装信息发送给服务器
            jTextArea.append(msg);
            Message message = new Message();
            message.setCmd("message");
            message.setSender(you);
            message.setReceiver(friend);
            message.setData(jTextField.getText());

            try{
                ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
                oos.writeObject(message);
            } catch (IOException e1){
                e1.printStackTrace();
            }
            jTextField.setText(null);

        } else if (e.getSource() == fileButton) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH点mm分钟ss秒");
            String msg = "你("+you+")"+ "\t" + sdf.format(new Date())  + "\n" + jTextField.getText() + "\n";
            int select = fc.showOpenDialog(this);
            if(select == JFileChooser.APPROVE_OPTION){
                File file = fc.getSelectedFile();
                Message m = new Message();
                m.setSender(you);
                m.setReceiver(friend);
                m.setCmd("filemessage");
                m.setData(file);
                String info = "你("+you+")传输文件："+ "\t" + sdf.format(new Date())  + "\n" + file.getName() +"\n";
                jTextArea.append(info);
                try{
                    ObjectOutputStream oos = new ObjectOutputStream(ThreadSet.getThread(you).getS().getOutputStream());
                    oos.writeObject(m);
                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args) {
//        new Chat("1", "2");
//    }

}