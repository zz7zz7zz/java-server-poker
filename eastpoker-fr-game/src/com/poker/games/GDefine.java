package com.poker.games;

public class GDefine {

	public enum TableStatus{
		TABLE_STATUS_PLAY(1),
		TABLE_STATUS_STOP(2);
		
		int code;
        private TableStatus(int code) {
            this.code = code;
        }
	}
	
	enum LoginRet{
		LOGIN_SUCCESS(1),
		LOGIN_FAILED_ALREADY_EXIST(2),
		LOGIN_FAILED_FULL(3);
		
		int code;
        private LoginRet(int code) {
            this.code = code;
        }
	}
	
	enum LogoutRet{
		LOGOUT_SUCCESS(1),
		LOGOUT_FAILED(2);
		
		int code;
        private LogoutRet(int code) {
            this.code = code;
        }
	}
	
}
