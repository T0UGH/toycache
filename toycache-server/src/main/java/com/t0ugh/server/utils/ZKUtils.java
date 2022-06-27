package com.t0ugh.server.utils;

import com.t0ugh.server.GlobalState;
import jdk.internal.dynalink.beans.StaticClass;

public class ZKUtils {
    public static String getMasterPath(long groupId){
        return String.format("/toycache/group%d/master", groupId);
    }

    public static String getFollowerPath(long groupId, long followerId){
        return String.format("/toycache/group%d/followers/follower%d", groupId, followerId);
    }

    public static String getFollowersPath(long groupId){
        return String.format("/toycache/group%d/followers", groupId);
    }

    public static long getFollowerId(String followerName){
        return Long.valueOf(followerName.substring(8));
    }
}
