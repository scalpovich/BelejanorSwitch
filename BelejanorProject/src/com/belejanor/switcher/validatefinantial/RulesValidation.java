package com.belejanor.switcher.validatefinantial;

import java.util.List;

import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;

public class RulesValidation {
	
	private Logger log;
	
	public RulesValidation(){
		this.log = new Logger();
	}

	public wIso8583 ValidateProcessor(wIso8583 iso){
		
		wIso8583 isoRes = null;
		try {
			
			ValidateAvailableTrx va = new ValidateAvailableTrx(iso);	
			ValidateOrigin vo = new ValidateOrigin(iso);
			EngineCallableProcessor<wIso8583> engine = new EngineCallableProcessor<>(3);
			engine.add(va);
			engine.add(vo.ValidateChannelNetworkTrx());
			//engine.add(new ValidateAvailableTrx(iso));
			engine.add(vo.ValidateIp());
			List<wIso8583> env = engine.goProcess();
			
			isoRes = env.stream().filter(p -> p.getISO_039_ResponseCode() != "000")
					             .findFirst().orElseGet(() -> null);
			
			if(isoRes == null){
				
				iso.setISO_039_ResponseCode("000");
				
			}else{
				
				iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
			}
		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo RulesValidation::ValidateProcessor, Iso11: " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR AL VALIDAR TRX. ", e, false));
		}
		return iso;
	}
}
