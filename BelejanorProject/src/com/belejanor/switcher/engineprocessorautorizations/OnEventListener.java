package com.belejanor.switcher.engineprocessorautorizations;

import java.sql.ResultSet;

import com.belejanor.switcher.cscoreswitch.wIso8583;

public interface OnEventListener {

	abstract void onResultTransactionCobis(wIso8583 iso, ResultSet rs);
	abstract void onErrorResponseCobis(wIso8583 iso, Exception ex);
	abstract void onTimeotResponseCobis();
}
