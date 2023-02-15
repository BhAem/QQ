package Common;

import View.Chat;

import java.util.*;

public class ChatViewSet {
    private static HashMap<String, Chat> hm = new HashMap<String, Chat>();

    // 加入一个聊天界面
    public static void addChat(String yourIdAndFriendId, Chat chat) {
        hm.put(yourIdAndFriendId, chat);
    }

    // 获取一个聊天界面
    public static Chat getChat(String yourIdAndFriendId) {
        return (Chat) hm.get(yourIdAndFriendId);
    }

    public static void removeQQChat(String yourIdAndFriendId){
        hm.remove(yourIdAndFriendId);
    }
}
