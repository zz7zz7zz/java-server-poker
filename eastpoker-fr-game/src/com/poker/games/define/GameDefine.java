package com.poker.games.define;

public class GameDefine {

	public enum TableStatus{
		
		TABLE_STATUS_PLAY(1),
		TABLE_STATUS_STOP(2),
		TABLE_STATUS_WAIT(3);
		
		private final int value;
		
        private TableStatus(int value) {
            this.value = value;
        }
        
		public int getValue() {
			return value;
		}
	}
	
	public enum LoginResult{
		
		LOGIN_SUCCESS(1),
		LOGIN_SUCCESS_ALREADY_EXIST(2),
		LOGIN_FAILED_FULL(3);
		
		private final int value;
		
        private LoginResult(int value) {
            this.value = value;
        }
        
		public int getValue() {
			return value;
		}
	}
	
	public enum LogoutResult{
		
		LOGOUT_SUCCESS(1),
		LOGOUT_FAILED(2);
		
		private final int value;
		
        private LogoutResult(int value) {
            this.value = value;
        }
        
		public int getValue() {
			return value;
		}
	}
	
}
