package Common;

import Model.ClientThread;

import java.util.HashMap;

public class ThreadSet {
    private static HashMap<String, ClientThread> hashMap = new HashMap<String, ClientThread>();
    public static void addThread(String account, ClientThread st){
        hashMap.put(account, st);
    }
    public static ClientThread getThread(String name){
        return hashMap.get(name);
    }
}