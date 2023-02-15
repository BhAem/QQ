package Common;

import View.List;
import java.util.HashMap;

public class ListSet {
    private static HashMap<String, List> hm = new HashMap<String, List>();

    // 将列表添加到集合中
    public static void addFriendList(String uid, List qqFriendList) {
        hm.put(uid, qqFriendList);
    }

    // 从集合中获取列表
    public static List getFriendList(String uid) {
        return hm.get(uid);
    }
}
