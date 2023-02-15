package Common;

import Model.ServerThread;

import java.net.Socket;
import java.util.HashMap;

public class ThreadSet {
    private static HashMap<String, ServerThread> hashMap = new HashMap<String, ServerThread>();
    public static void addThread(String account, ServerThread st){
        hashMap.put(account, st);
    }
    public static ServerThread getThread(String name){
        return hashMap.get(name);
    }
    public static void removeThread(String uid){
        hashMap.remove(uid);
    }
}