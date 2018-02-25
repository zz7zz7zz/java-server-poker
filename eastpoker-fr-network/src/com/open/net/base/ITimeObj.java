package com.open.net.base;

public class ITimeObj {
	
	public long timestamp;
	public int timeOutId;//唯一id,标识什么事件
	public int duration;//时间
	public ITimer timer;//回调
	
	public ITimeObj(int timeOutId, int duration, ITimer timer) {
		timestamp = System.currentTimeMillis();
		this.timeOutId = timeOutId;
		this.duration = duration;
		this.timer = timer;
	}
}
