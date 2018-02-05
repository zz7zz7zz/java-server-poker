package com.poker.games.define;

public class GameDefine {

	public enum TableStatus{
		TABLE_STATUS_PLAY(1),
		TABLE_STATUS_STOP(2),
		TABLE_STATUS_WAIT(3);
		
		int code;
        private TableStatus(int code) {
            this.code = code;
        }
	}
	
	public enum LoginResult{
		LOGIN_SUCCESS(1),
		LOGIN_SUCCESS_ALREADY_EXIST(2),
		LOGIN_FAILED_FULL(3);
		
		int code;
        private LoginResult(int code) {
            this.code = code;
        }
	}
	
	public enum LogoutResult{
		LOGOUT_SUCCESS(1),
		LOGOUT_FAILED(2);
		
		int code;
        private LogoutResult(int code) {
            this.code = code;
        }
	}
	
}
