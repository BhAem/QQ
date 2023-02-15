package View;

import Common.ListSet;
import Common.Message;
import Common.User;
import Model.LinkServer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class Login extends JFrame implements ActionListener {

    private JLabel bgLabel;
    private JButton loginButton, registerButton;
    private JTextField adminField;
    private JPasswordField pwdField;
    private JCheckBox autoLoginBox, memPwdBox;
    private JPanel loginInterface, loginPanel, chooses;

    public Login() {
        //一些swing布局
        ImageIcon bgPic = new ImageIcon("src/Client/src/Images/bg.png");
        ImageIcon loginPic = new ImageIcon("src/Client/src/Images/loginlogo.png");
        bgPic.setImage(bgPic.getImage().getScaledInstance(440, 120, Image.SCALE_DEFAULT));     //调整一下比例
        loginPic.setImage(loginPic.getImage().getScaledInstance(200, 35, Image.SCALE_DEFAULT));
        bgLabel = new JLabel(bgPic);
        loginButton = new JButton();
        loginButton.setIcon(loginPic);
        loginButton.setBorderPainted(false);
        loginButton.setBorder(null);
        loginPanel = new JPanel();
        loginPanel.add(loginButton);
        loginInterface = new JPanel();
        loginInterface.setLayout(null);
        adminField = new JTextField();
        pwdField = new JPasswordField();
        adminField.setText("请输入QQ号码");
        adminField.setBounds(70, 10,300, 40);
        pwdField.setBounds(70, 60,300, 40);
        loginInterface.add(adminField);
        loginInterface.add(pwdField);
        chooses = new JPanel();
        chooses.setLayout(new GridLayout(1, 3));
        registerButton = new JButton("注册账号");
        registerButton.setContentAreaFilled(false); // 透明化处理
        autoLoginBox = new JCheckBox("自动登录");
        memPwdBox = new JCheckBox("记住密码");
        chooses.add(autoLoginBox);
        chooses.add(memPwdBox);
        chooses.add(registerButton);
        chooses.setBounds(70, 105,300, 30);
        loginInterface.add(chooses);
        add(bgLabel, BorderLayout.NORTH);
        add(loginInterface, BorderLayout.CENTER);
        add(loginPanel, BorderLayout.SOUTH);
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();   //获取屏幕大小
        int width = dimension.width;
        int height = dimension.height;
        int x = (width - 440) / 2;
        int y = (height - 340) / 2;
        setBounds(x, y, 440, 340);     //将窗体放在屏幕中间
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {
            String account = adminField.getText().trim();
            String password = new String(pwdField.getPassword()).trim();
            //错误处理
            if ("".equals(account)) {
                JOptionPane.showMessageDialog(null, "请输入帐号");
            }
            else if ("".equals(password)) {
                JOptionPane.showMessageDialog(null, "请输入密码");
            }
            else {
                //用户登录
                User u = new User(account, password);
                Message message = new Message();
                message.setCmd("login");
                message.setData(u);
                message.setReceiver(account);
                message.setSender(account);

                // LinkServer用来连接服务器，实例化一个该类的对象
                LinkServer linkServer = new LinkServer();
                message = linkServer.sendLoginInfo2Server(message);

                if (message != null) {
                    if (message.isFlag()) {
                        this.dispose();
                        JOptionPane.showMessageDialog(null, message.getResult());
                        //显示好友列表
                        List list = new List();
                        ListSet.addFriendList(u.getAccount(), list);
                        list.init(u.getAccount());
                    } else {
                        JOptionPane.showMessageDialog(this, message.getResult());
                    }
                }
            }
        } else if(e.getSource() == registerButton) {
            //注册用户
            new Register();
        }
    }

    public static void main(String[] args) {
        new Login();
    }

}
