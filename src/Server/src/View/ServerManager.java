package View;

import Model.LinkClient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerManager extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JButton startServer;
    private JButton endServer;

    public ServerManager() {
        setLayout(new FlowLayout());
        startServer = new JButton("开启服务器");
        endServer = new JButton("关闭服务器");
        startServer.setBounds(50, 20, 200, 40);
        endServer.setBounds(50, 80, 200, 40);
        startServer.addActionListener(this);
        endServer.addActionListener(this);
        add(startServer);
        add(endServer);
        setLayout(null);
        setTitle("服务器");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ServerManager();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startServer) {
            JOptionPane.showMessageDialog(null, "服务器成功启动");
            new LinkClient();
        }
        else if (e.getSource() == endServer) {
            System.exit(0);
        }
    }
}
