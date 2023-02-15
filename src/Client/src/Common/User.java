package Common;

import java.io.Serializable;

public class User implements Serializable { //序列化
    private static final long serialVersionUID = 1L;
    private String name;    //自定义昵称
    private String account; //QQ账号
    private String password;//QQ密码

    public User(String account) {
        this.account = account;
    }

    public User(String username, String password) {
        this.account = username;
        this.password = password;
    }

    public User(String name, String account, String password) {
        this.name = name;
        this.account = account;
        this.password = password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}