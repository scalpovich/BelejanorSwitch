package com.belejanor.switcher.parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

public class CoonectaParser {
	
	private Logger log;
	
	public CoonectaParser(){
		log = new Logger();
	}

	public Iso8583 parseIsoTextToIsoObject(String isoText){
		
		Iso8583 iso = null;
		try {
			iso = new Iso8583();
			iso.setISO_000_Message_Type(isoText.substring(0,4));
			iso.setISO_002_PAN(isoText.substring(4,23));
			iso.setISO_003_ProcessingCode(isoText.substring(23,29));
			String ammount = isoText.substring(29, 41);
			iso.setISO_004_AmountTransaction(Double.parseDouble(ammount)/100);
			SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm");
			Date d = format.parse(isoText.substring(41, 51));
			iso.setISO_007_TransDatetime(d);
			Calendar cal= Calendar.getInstance(); 
			int year= cal.get(Calendar.YEAR); 
			format = new SimpleDateFormat("yyyyMMdd");
			d = format.parse(String.valueOf(year) + isoText.substring(51,55));
			iso.setISO_013_LocalDate(d);
			iso.setISO_011_SysAuditNumber(isoText.substring(55,61));
			format = new SimpleDateFormat("yyMMddHHmmss");
			d = format.parse(isoText.substring(61,73));
			iso.setISO_012_LocalDatetime(d);
			iso.setISO_019_AcqCountryCode(isoText.substring(73,76));
			iso.setISO_023_CardSeq(isoText.substring(76,100));//24 espacios
			iso.setISO_032_ACQInsID(isoText.substring(100,111));
			iso.setISO_033_FWDInsID(isoText.substring(111,122));
			iso.setISO_037_RetrievalReferenceNumber(isoText.substring(122,134));
			iso.setISO_039_ResponseCode(isoText.substring(134,137));
			iso.setISO_041_CardAcceptorID(isoText.substring(137,145));
			iso.setISO_042_Card_Acc_ID_Code(isoText.substring(145,160));
			iso.setISO_043_CardAcceptorLoc(isoText.substring(160,202));
			iso.setISO_049_TranCurrCode(Double.parseDouble(isoText.substring(202,205)));
			iso.setISO_054_AditionalAmounts(isoText.substring(205,325));
			iso.setISO_024_NetworkId(isoText.substring(325,331));
			format = new SimpleDateFormat("yyyyMMdd"); 
			d = format.parse(String.valueOf(year) + isoText.substring(331,335));
			iso.setISO_015_SettlementDatel(d);
			iso.setISO_120_ExtendedData(isoText.substring(335,635));
			iso.setISO_102_AccountID_1(isoText.substring(635,647));
			iso.setISO_103_AccountID_2(isoText.substring(647,659));
			iso.setISO_018_MerchantType("0000");
			
		} catch (Exception e) {
		
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("TRAMA INVALIDA");
			log.WriteLogMonitor("Error modulo CoonectaParser::parseIsoTextToIsoObject ", TypeMonitor.error, e);
		}
		return iso;
	}
	public String parseIsoObjectToIsoText(Iso8583 iso){
		
		String isoResponse = "";
		StringBuilder str = new StringBuilder();
		try {
			
			str.append(iso.getISO_000_Message_Type());
			str.append(iso.getISO_002_PAN());
			str.append(iso.getISO_003_ProcessingCode());
			str.append(String.format("%013.2f", iso.getISO_004_AmountTransaction())
					  .replace(",", "").replace(".", ""));
			str.append(FormatUtils.DateToString(iso.getISO_007_TransDatetime(), "yyMMddHHmm"));
			str.append(FormatUtils.DateToString(iso.getISO_013_LocalDate(), "MMdd"));
			str.append(iso.getISO_011_SysAuditNumber());
			str.append(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyMMddHHmmss"));
			str.append(iso.getISO_019_AcqCountryCode());
			str.append(iso.getISO_023_CardSeq());
			str.append(iso.getISO_032_ACQInsID());
			str.append(iso.getISO_033_FWDInsID());
			str.append(iso.getISO_037_RetrievalReferenceNumber());
			str.append(iso.getISO_039_ResponseCode());
			str.append(iso.getISO_041_CardAcceptorID());
			str.append(iso.getISO_042_Card_Acc_ID_Code());
			str.append(iso.getISO_043_CardAcceptorLoc());
			str.append((int)iso.getISO_049_TranCurrCode());
			str.append(StringUtils.padRight(iso.getISO_054_AditionalAmounts(),120," "));
			str.append(iso.getISO_024_NetworkId());
			str.append(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "MMdd"));
			str.append(iso.getISO_120_ExtendedData());
			str.append(iso.getISO_102_AccountID_1());
			str.append(iso.getISO_103_AccountID_2());
			isoResponse = str.toString();
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo CoonectaParser::parseIsoObjectToIsoText ", TypeMonitor.error, e);
		}
		return isoResponse;
	}
}
