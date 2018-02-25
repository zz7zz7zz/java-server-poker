package com.open.net.base;

import java.util.Iterator;
import java.util.LinkedList;

public final class Looper {
	
	private static long SLEEP_MILLIS = 5;
	private static final LinkedList<IPoller> mPollerList = new LinkedList<>();
	private static final LinkedList<ITimeObj>  mTimerList = new LinkedList<>();
	
	//----------------------------------------------------
	public static final void register(IPoller mPooer) {
		if(!mPollerList.contains(mPooer)) {
			mPollerList.add(mPooer);
		}
	}
	
	public static final void unRegister(IPoller mPooer) {
		mPollerList.remove(mPooer);
	}
	
	//----------------------------------------------------
	public static final void register(int timeOutId,int duration,ITimer timer) {
		mTimerList.add(new ITimeObj(timeOutId, duration, timer));
	}
	
	public static final void unRegister(int timeOutId,ITimer timer) {
		Iterator<ITimeObj> iterator = mTimerList.iterator();
		while (iterator.hasNext()) {
			ITimeObj mITimeObj = iterator.next();
			if(mITimeObj.timeOutId == timeOutId &&  mITimeObj.timer == timer){
				iterator.remove();
				break;
			}
		}
	}
	
	//----------------------------------------------------
	public static final void set_sleep_millis(long sleep_millis){
		SLEEP_MILLIS = sleep_millis;
	}
	
	public static final void loop() {
		
		try {
			
			while(true) {
				
				//遍历网络事件
				for (IPoller iPoller : mPollerList) {
					iPoller.onPoll();
				}
				
				//遍历定时器
				long now = System.currentTimeMillis();
				Iterator<ITimeObj> iterator = mTimerList.iterator();
				while (iterator.hasNext()) {
					ITimeObj mITimeObj = iterator.next();
					if(Math.abs(now - mITimeObj.timestamp) > 1000L){
						mITimeObj.duration --;
						if(mITimeObj.duration <=0){
							mITimeObj.timer.onTimeOut(mITimeObj.timeOutId);
							iterator.remove();
						}
					}
				}
					
				//休眠时间
				Thread.sleep(SLEEP_MILLIS);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
//	public static void main(String argc[]){
//		LinkedList<String>  mTimerList = new LinkedList<>();
//		mTimerList.add("100");
//		mTimerList.add("101");
//		mTimerList.add("102");
//		mTimerList.add("103");
//		
////		for (String iTimer : mTimerList) {
////			if(iTimer.equals("101")){
////				mTimerList.remove(iTimer);
////				continue;
////			}
////			System.out.println(iTimer);
////		}
//		
//		Iterator<String> iterator = mTimerList.iterator();
//		while (iterator.hasNext()) {
//			String iTimer = iterator.next();
//			if(iTimer.equals("101")){
//				iterator.remove();
//				continue;
//			}
//			System.out.println(iTimer);
//		}
//	}
}
