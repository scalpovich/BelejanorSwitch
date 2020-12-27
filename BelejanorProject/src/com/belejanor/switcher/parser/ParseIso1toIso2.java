package com.belejanor.switcher.parser;

import java.util.Arrays;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.Iso8583Binary;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.ErrorToIso;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class ParseIso1toIso2 {

	private Logger log;
	
	public ParseIso1toIso2() {
		log = new Logger();
	}
	
	public Iso8583Binary parseIso2ToIso1(Iso8583 iso, Iso8583Binary isoBin){
		
		try {
			String mti = isoBin.getMti();
			switch (mti) {
				case "0200":
					isoBin.setMti("0210");
					break;
				case "0220":
					isoBin.setMti("0230");
					break;
				case "0400":
					isoBin.setMti("0410");
					break;
				case "0420":
					isoBin.setMti("0430");
					break;
				case "0800":
					isoBin.setMti("0810");
					break;
				default:
					break;
			}
			Config conf = new Config();
			conf = conf.getConfigSystem("ISO_" + iso.getISO_003_ProcessingCode() + "_" +
			                            iso.getISO_018_MerchantType() + "_" + 
					 					iso.getISO_024_NetworkId());
			String BitMapResponse = conf.getCfg_Valor();
			if(BitMapResponse.length() == 32){
				
				String partI = BitMapResponse.substring(0, 16);
				String partII = BitMapResponse.substring(16);
				isoBin.setPrimaryBitmap(GeneralUtils.getHexBitMapToString(partI));
				isoBin.setDe1_SecondaryBitmap(GeneralUtils.getHexBitMapToString(partII));
			}else{
				
				isoBin.setPrimaryBitmap(GeneralUtils.getHexBitMapToString(BitMapResponse));
			}
			
			ErrorToIso error = new ErrorToIso();
			String errorReturn = error.getCodeErrorIso01(iso.getISO_039_ResponseCode());
			isoBin.setDe39_RespCode(Arrays.asList(errorReturn.split("\\|")).get(0));
			isoBin.setDe114_ResvNat(Arrays.asList(errorReturn.split("\\|")).get(1).getBytes());
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				if(StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber().trim())){
					String hash = String.valueOf(isoBin.hashCode());
					isoBin.setDe38_AuthIdentResp(StringUtils.padLeft(hash, 6, "1").substring(0,6));
				}else{
					
					isoBin.setDe38_AuthIdentResp(iso.getISO_038_AutorizationNumber());
				}
			}
			isoBin.setDe54_AddtlAmts(iso.getISO_054_AditionalAmounts());
			if(iso.getISO_000_Message_Type().startsWith("18")){
				
				isoBin.setDe70_NetMgtInfoCode(iso.getISO_115_ExtendedData());
			}
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR IsoBynary(Iso8583 iso)", TypeMonitor.error, e);
			isoBin = Iso8583Binary.GenericError();
			
		}
		return isoBin;
	}
}

