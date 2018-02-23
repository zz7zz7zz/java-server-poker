package com.open.net.base;

import java.util.LinkedList;

public final class Looper {
	
	private static final LinkedList<IPoller> mPollerList = new LinkedList<>();
	
	private static long SLEEP_MILLIS = 100;
	
	public static final void set_sleep_millis(long sleep_millis){
		SLEEP_MILLIS = sleep_millis;
	}
	
	public static final void register(IPoller mPooer) {
		if(!mPollerList.contains(mPooer)) {
			mPollerList.add(mPooer);
		}
	}
	
	public static final void unRegister(IPoller mPooer) {
		mPollerList.remove(mPooer);
	}
	
	public static final void loop() {
		
		try {
			
			while(true) {
				
				for (IPoller iPoller : mPollerList) {
					iPoller.onPoll();
				}
				
				Thread.sleep(SLEEP_MILLIS);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
