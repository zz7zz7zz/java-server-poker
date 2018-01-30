package com.poker.games;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.poker.games.impl.GUser;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   用户池
 */

public final class UserPool {

    public static ConcurrentLinkedQueue<User> mQueen = new ConcurrentLinkedQueue<User>();
    public static int GROWTH = 10;
    
    //初始化
    public static final void init(int user_init_size,int growth){
    	GROWTH = growth;
        for (int i = 0;i< user_init_size;i++){
            mQueen.add(new GUser());
        }
    }

    //取
    public static final User get(long uid){
        if(mQueen.isEmpty()){
            for(int i =0;i<GROWTH;i++){
            	 mQueen.add(new User());
            }
        }
        User ret= mQueen.poll();
        ret.reset();
        ret.uid = uid;
        return ret;
    }

    //回收
    public static final void release(User obj){
        if(null != obj){
            obj.reset();
            mQueen.add(obj);
        }
    }
}
