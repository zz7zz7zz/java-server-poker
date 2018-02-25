package com.open.net.base;

public class ITimeObj {
	
	public long timestamp;
	public int timeOutId;//唯一id,标识什么事件
	public int duration;//时间
	public ITimer timer;//回调
	public boolean isInvalid = false;
	
	public ITimeObj(int timeOutId, int duration, ITimer timer) {
		this.timestamp = System.currentTimeMillis();
		this.timeOutId = timeOutId;
		this.duration = duration;
		this.timer = timer;
		this.isInvalid = false;
	}
}
