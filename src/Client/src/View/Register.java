package View;

import Common.Message;
import Common.User;
import Model.LinkServer;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Register extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    private JTextField nameField, accountField;
    private JPasswordField pwdField1, pwdField2;
    private JButton finish;
    private JLabel hint, la, lb, lc, ld;

    public Register(){
        //一些swing布局
        hint = new JLabel("<html><font size=32>欢迎注册QQ</font></html>",SwingConstants.CENTER);
        hint.setBounds(100, 20, 240, 50);

        la = new JLabel("昵称");
        lb = new JLabel("默认账号");
        lc = new JLabel("输入密码");
        ld = new JLabel("再次输入");

        nameField = new JTextField();
        nameField.setBounds(70, 90,300, 30);
        la.setBounds(10, 90, 40, 30);
        accountField = new JTextField();
        accountField.setBounds(70, 130,300, 30);
        lb.setBounds(10, 130, 60, 30);

        //随机生成的范围到100000~188888的账号，后期会与数据库中已存在的账号进行核对并重新生成
        Random random = new Random();
        int temp = (int) random.nextInt(88888) + 100000;
        String str = String.valueOf(temp);
        accountField.setText(str);

        pwdField1 = new JPasswordField();
        pwdField2 = new JPasswordField();
        pwdField1.setBounds(70, 170, 300, 30);
        lc.setBounds(10, 170, 60, 30);
        pwdField2.setBounds(70, 210,300, 30);
        ld.setBounds(10, 210, 60, 30);

        finish = new JButton("完成注册");
        finish.setBounds(170, 250, 100, 30);

        add(la);
        add(lb);
        add(lc);
        add(ld);
        add(hint);
        add(nameField);
        add(accountField);
        add(pwdField1);
        add(pwdField2);
        add(finish);

        finish.addActionListener(this);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();   //获取屏幕大小
        int width = dimension.width;
        int height = dimension.height;
        int x = (width - 440) / 2;
        int y = (height - 340) / 2;
        setLayout(null);
        setBounds(x, y, 440, 340);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == finish){

            String name = nameField.getText().trim();
            String account = accountField.getText().trim();
            String pwd1 = new String(pwdField1.getPassword()).trim();
            String pwd2 = new String(pwdField2.getPassword()).trim();
            //错误处理
            if ("".equals(name)) {
                JOptionPane.showMessageDialog(null, "请输入昵称");
            }
            else if ("".equals(pwd1)) {
                JOptionPane.showMessageDialog(this, "请输入密码","系统提示",JOptionPane.WARNING_MESSAGE);
            }
            else if ("".equals(pwd2)) {
                JOptionPane.showMessageDialog(this, "请再次输入密码","系统提示",JOptionPane.WARNING_MESSAGE);
            }
            else if(pwd1.equals(pwd2)) {
                //进入注册流程
                User user = new User(name, account, pwd1);
                Message message = new Message();
                //检查数据库中是否存在该账号
                message.setCmd("checkregist");
                message.setFlag(true);
                message.setData(user);
                message.setSender(account);
                message.setReceiver(account);
                LinkServer linkServer = new LinkServer();
                message = linkServer.sendRegisterInfo2Server(message);
                if (message != null) {
                    if (!message.isFlag()) { //该账号未被注册
                        //进行注册
                        message.setCmd("regist");
                        message.setData(user);
                        message = linkServer.sendRegisterInfo2Server(message);
                        if (message.isFlag()) {  //注册成功
                            this.dispose();
                            JOptionPane.showMessageDialog(null, message.getResult());
                        } else {    //注册出错
                            JOptionPane.showMessageDialog(null, message.getResult());
                        }
                    } else {    //账号已被注册
                        JOptionPane.showMessageDialog(null, message.getResult());
                        setVisible(false);
                        new Register(); //重新随机生成账号
                    }

                }
            } else {    //处理错误
                JOptionPane.showMessageDialog(this,"两次输入密码不同，请检查输入");
                pwdField1.setText("");
                pwdField2.setText("");
                setVisible(false);
            }
        }
    }
//    public static void main(String[] args) {
//        new Register();
//    }
}