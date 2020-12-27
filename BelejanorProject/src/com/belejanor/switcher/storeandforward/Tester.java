package com.belejanor.switcher.storeandforward;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;

public class Tester {

	public static void main(String[] args) {
		Iso8583 iso = new Iso8583();
		iso.setISO_000_Message_Type("1200");
		iso.setISO_003_ProcessingCode("011000");
		iso.setISO_004_AmountTransaction(15);
		iso.setISO_024_NetworkId("555551");
		iso.setISO_018_MerchantType("0000");
		wIso8583 wiso = new wIso8583();
		Thread t = new Thread(new AdminProcessStoreAndForward().EvaluateStoreAndForwardCodesThread(wiso, "CON001"));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
