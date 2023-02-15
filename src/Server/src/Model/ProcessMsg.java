package Model;

import Common.User;

import java.sql.*;

public class ProcessMsg {
    private static Connection con;
    private Connection getconection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return con;
    }

    //登录时查询是否存在该账号
    public boolean checkUser(User user) {
        PreparedStatement sql = null;
        con = getconection();
        ResultSet res = null;
        try {
            sql = con.prepareStatement("select * from user where account=? and password=?");
            sql.setString(1, user.getAccount());
            sql.setString(2, user.getPassword());
            res = sql.executeQuery();
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (res != null)
                    res.close();
                if (sql != null)
                    sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //注册时查询是否已经注册该账号
    public boolean checkregistUser(User user) {
        PreparedStatement sql = null;
        con = getconection();
        ResultSet res = null;
        try {
            sql = con.prepareStatement("select * from user where account=?");
            sql.setString(1, user.getAccount());
            res = sql.executeQuery();
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (res != null)
                    res.close();
                if (sql != null)
                    sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //用户注册
    public boolean registUser(User user) {
        System.out.println("怎么说");
        PreparedStatement sql = null;
        Statement stat = null;
        con = getconection();
        boolean flag1 = false;
        boolean flag2 = false;
        //在test数据库中的user表中添加账户信息
        try {
            sql = con.prepareStatement("insert into user(account,password,name) values(?,?,?)");
            sql.setString(1, user.getAccount());
            sql.setString(2, user.getPassword());
            sql.setString(3, user.getName());
            sql.executeUpdate();;
            flag1 = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //在friend数据库中为新注册的账户建立属于自己的好友列表
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/friend", "root", "123456");
            stat = con.createStatement();
            //创建新的表单
            String temp = "create table t"+user.getAccount()+"(id int, account VARCHAR(20), name varchar(20))";
            stat.executeUpdate(temp);
            flag2 = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (sql != null)
                    sql.close();
                if (stat != null)
                    stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag1 && flag2 ? true : false;
    }

    //获取用户
    public String[] getFriends(User user) {
        PreparedStatement sql = null;
        con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/friend", "root", "123456");;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet res = null;
        String[] friends = new String[0];
        //在friend数据库中为账户查找好友
        try {
            String temp = "select * from t" + user.getAccount();
            sql = con.prepareStatement(temp);
            res = sql.executeQuery();
            res.last();
            int n = res.getRow();
            System.out.println(n);
            friends = new String[n];
            int i = 0;
            res.beforeFirst();
            while (res.next()) {
                String account =  res.getString("account");
                friends[i++] = account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (res != null)
                    res.close();
                if (sql != null)
                    sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    //添加用户
    public boolean addFriends(String[] info) {
        PreparedStatement sql = null;
        con = getconection();
        ResultSet res = null;
        boolean flag = false;
        String name1 = "";
        String name2 = "";
        try {
            sql = con.prepareStatement("select * from user where account=?");
            System.out.println(info[1]);
            sql.setString(1, info[1]);
            res = sql.executeQuery();
            while (res.next()) {
                flag = true;
                name1 = res.getString("name");
                System.out.println("167");
                System.out.println(name1);
            }
            sql = con.prepareStatement("select * from user where account=?");
            System.out.println(info[1]);
            sql.setString(1, info[0]);
            res = sql.executeQuery();
            while (res.next()) {
                name2 = res.getString("name");
                System.out.println("177");
                System.out.println(name2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //在两者的friend数据库中新增好友
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/friend", "root", "123456");
            sql = con.prepareStatement("insert into t" + info[0] + "(account, name)" + " values(?,?)");
            sql.setString(1, info[1]);
            sql.setString(2, name1);
            sql.executeUpdate();
            sql = con.prepareStatement("insert into t" + info[1] + "(account, name)" + " values(?,?)");
            sql.setString(1, info[0]);
            sql.setString(2, name2);
            sql.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (res != null)
                    res.close();
                if (sql != null)
                    sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    //删除用户
    public boolean deleteFriends(String[] info) {
        Statement stmt = null;
        ResultSet res = null;
        boolean flag = false;
        //在两者的friend数据库中新增好友
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/friend", "root", "123456");
            stmt = con.createStatement();
            stmt.executeUpdate("delete from t"+info[0]+" where account = " +info[1]);
            stmt.executeUpdate("delete from t"+info[1]+" where account = " +info[0]);
            flag = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (res != null)
                    res.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}