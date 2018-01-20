package com.poker.games.impl;

import com.poker.games.Table;
public class GTable extends Table {

	public GTable(int tableId, int table_max_user) {
		super(tableId, table_max_user);
	}

	@Override
	protected int dispatchTableMessage(int cmd, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		// TODO Auto-generated method stub
		return 0;
	}



}
