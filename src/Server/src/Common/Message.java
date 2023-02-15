package Common;

import java.io.Serializable;

public class Message implements Serializable {  //序列化
    private static final long serialVersionUID = 1L;
    private String sender = null;
    private String receiver = null;
    private Object data = null;     // 传递的数据
    private boolean flag = false;   // 指令的处理结果
    private String cmd = null;      // 指定服务器指令
    private String result = null;   // 处理结果
    private Object[] arrayData = new Object[0];

    public  Message() {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Object[] getArrayData() {return this.arrayData;}

    public void setArrayData(Object[] arrayData) {
        int len = arrayData.length;
        this.arrayData = new Object[len];
        System.arraycopy(arrayData, 0, this.arrayData, 0, len);
    }
}