package com.belejanor.switcher.acquirers;


public interface ITranBIMOAcq {

	abstract com.belejanor.switcher.bimo.pacs.camt_998_212.Document processEnrollPerson
	         (com.belejanor.switcher.bimo.pacs.camt_998_211.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.camt_998_222.Document processDesenrollPerson
	         (com.belejanor.switcher.bimo.pacs.camt_998_221.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.camt_998_202.Document processQueryBalance
	         (com.belejanor.switcher.bimo.pacs.camt_998_201.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.pacs_002_022.Document processCredit
    		 (com.belejanor.switcher.bimo.pacs.pacs_008_021.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.pacs_002_042.Document processReverCredit
	         (com.belejanor.switcher.bimo.pacs.pacs_007_041.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.pacs_002_072.Document processDebit
	         (com.belejanor.switcher.bimo.pacs.pacs_008_071.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.pacs_002_052.Document processReverDebit
    		 (com.belejanor.switcher.bimo.pacs.pacs_007_051.Document document, String IP);
	abstract com.belejanor.switcher.bimo.pacs.camt_998_232.Document processGenOtp
	         (com.belejanor.switcher.bimo.pacs.camt_998_231.Document document, String IP);
}
