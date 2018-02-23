package com.open.net.client.object;

import java.util.LinkedList;

import com.open.net.client.message.Message;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   数据回调
 */

public abstract class AbstractClientMessageProcessor {

    //----------------------------------发数据------------------------------------------------
    public final void send(AbstractClient mClient,byte[] src){
        this.send(mClient,src,0,src.length);
    }

    public void send(AbstractClient mClient,byte[] src , int offset , int length){
        mClient.onSendMessage(src,offset,length);
    }

    //----------------------------------收数据------------------------------------------------
    public final void onReceiveData(AbstractClient mClient, byte[] src , int offset , int length) {
        mClient.onReceiveData(src,offset,length);
    }

    public final void onReceiveDataCompleted(AbstractClient mClient){
        if(mClient.mReadMessageQueen.mReadQueen.size()>0){
            onReceiveMessages(mClient,mClient.mReadMessageQueen.mReadQueen);
            mClient.onReceiveMessageClear();
        }
    }

    //请不要去操作这个表的数据，只能读，不能增删改
    public abstract void onReceiveMessages(AbstractClient mClient, LinkedList<Message> mQueen);

}
