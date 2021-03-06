package com.belejanor.switcher.sqlservices;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.scheduler.TypeBatch;
import com.belejanor.switcher.snp.spi.SnpSPIOrdenante;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

import oracle.jdbc.OracleTypes;

public class IsoSqlMaintenance/* implements Callable<Integer>*/{
	
	private int codeSqlError;
	private String descriptionSqlError;
	private int[] masiveMantenaince = null;
	private Logger log;
		
	public IsoSqlMaintenance(){
		log = new Logger();
	}
	
	public int getCodeSqlError() {
		return codeSqlError;
	}

	public void setCodeSqlError(int codeSqlError) {
		this.codeSqlError = codeSqlError;
	}

	public String getDescriptionSqlError() {
		return descriptionSqlError;
	}

	public void setDescriptionSqlError(String descriptionSqlError) {
		this.descriptionSqlError = descriptionSqlError;
	}

	public int[] getMasiveMantenaince() {
		return masiveMantenaince;
	}

	public void setMasiveMantenaince(int[] masiveMantenaince) {
		this.masiveMantenaince = masiveMantenaince;
	}
	
	public int InsertIso8583(wIso8583 iso){
		
		CallableStatement cs = null;
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_INSERTISO8583(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setString("@wISO_000_Message_Type", iso.getISO_000_Message_Type());
			cs.setString("@wISO_BITMAP", iso.getISO_BitMap());
			cs.setString("@wISO_002_PAN", iso.getISO_002_PAN());
			cs.setString("@wISO_003_ProcessingCode", iso.getISO_003_ProcessingCode());
			cs.setDouble("@wISO_004_AmountTransaction", iso.getISO_004_AmountTransaction());
			cs.setDouble("@wISO_006_BillAmount", iso.getISO_006_BillAmount());
			cs.setTimestamp("@wISO_007_TransDatetime", GeneralUtils.DateToTimestamp(iso.getISO_007_TransDatetime()));
			cs.setDouble("@wISO_008_BillFeeAmount", iso.getISO_008_BillFeeAmount());
			cs.setString("@wISO_011_SysAuditNumber", iso.getISO_011_SysAuditNumber());
			cs.setTimestamp("@wISO_012_LocalDatetime", GeneralUtils.DateToTimestamp(iso.getISO_012_LocalDatetime()));
			cs.setTimestamp("@wISO_013_LocalDate", GeneralUtils.DateToTimestamp(iso.getISO_013_LocalDate()));
			cs.setTimestamp("@wISO_015_SettlementDatel", GeneralUtils.DateToTimestamp(iso.getISO_015_SettlementDatel()));
			cs.setString("@wISO_018_MerchantType", iso.getISO_018_MerchantType());
			cs.setString("@wISO_019_AcqCountryCode", iso.getISO_019_AcqCountryCode());
			cs.setString("@wISO_022_PosEntryCode", iso.getISO_022_PosEntryMode());
			cs.setString("@wISO_023_CardSeq", iso.getISO_023_CardSeq());
			cs.setString("@wISO_024_NetworkId", iso.getISO_024_NetworkId());
			cs.setDouble("@wISO_028_TranFeeAmount", iso.getISO_028_TranFeeAmount());
			cs.setDouble("@wISO_029_SettlementFee", iso.getISO_029_SettlementFee());
			cs.setDouble("@wISO_030_ProcFee", iso.getISO_030_ProcFee());
			cs.setString("@wISO_032_ACQInsID", iso.getISO_032_ACQInsID());
			cs.setString("@wISO_033_FWDInsID", iso.getISO_033_FWDInsID());
			cs.setString("@wISO_034_PANExt", iso.getISO_034_PANExt());
			cs.setString("@wISO_035_Track2", iso.getISO_035_Track2());
			cs.setString("@wISO_036_Track3", iso.getISO_036_Track3());
			cs.setString("@wISO_037_RetrievalReferenceNro", iso.getISO_037_RetrievalReferenceNumber());
			cs.setString("@wISO_038_AutorizationNumber", iso.getISO_038_AutorizationNumber());
			cs.setString("@wISO_039_ResponseCode", iso.getISO_039_ResponseCode());
			cs.setString("@wISO_039p_ResponseDetail", iso.getISO_039p_ResponseDetail());
			cs.setString("@wISO_041_CardAcceptorID", iso.getISO_041_CardAcceptorID());
			cs.setString("@wISO_042_Card_Acc_ID_Code", iso.getISO_042_Card_Acc_ID_Code());
			cs.setString("@wISO_043_CardAcceptorLoc", iso.getISO_043_CardAcceptorLoc());
			cs.setString("@wISO_044_AddRespData",iso.getISO_044_AddRespData());
			cs.setDouble("@wISO_049_TranCurrCode", iso.getISO_049_TranCurrCode());
			cs.setDouble("@wISO_051_CardCurrCode", iso.getISO_051_CardCurrCode());
			cs.setString("@wISO_052_PinBlock", iso.getISO_052_PinBlock());
			cs.setString("@wISO_054_AditionalAmounts", iso.getISO_054_AditionalAmounts());
			cs.setString("@wISO_055_EMV", iso.getISO_055_EMV());
			cs.setString("@wISO_090_OriginalData", iso.getISO_090_OriginalData());
			cs.setString("@wISO_102_AccountID_1", iso.getISO_102_AccountID_1());
			cs.setString("@wISO_103_AccountID_2", iso.getISO_103_AccountID_2());
			cs.setString("@wISO_104_TranDescription", iso.getISO_104_TranDescription());		
			cs.setString("@wISO_114_ExtendedData", iso.getISO_114_ExtendedData());
			cs.setString("@wISO_115_ExtendedData", iso.getISO_115_ExtendedData());
		    cs.setString("@wISO_120_ExtendedData", iso.getISO_120_ExtendedData());
		    cs.setString("@wISO_121_ExtendedData", iso.getISO_121_ExtendedData());
		    cs.setString("@wISO_122_ExtendedData", iso.getISO_122_ExtendedData());
		    cs.setString("@wISO_123_ExtendedData", iso.getISO_123_ExtendedData());
		    cs.setString("@wISO_124_ExtendedData", iso.getISO_124_ExtendedData());
		    cs.setDouble("@wsISO_LogStatus", iso.getWsIso_LogStatus());
		    cs.setTimestamp("@wsISO_TranDatetime", GeneralUtils.DateToTimestamp(iso.getWsISO_TranDatetime()));
		    cs.setTimestamp("@wsISO_TranDatetimeResponse", GeneralUtils.DateToTimestamp(iso.getWsISO_TranDatetimeResponse()));
		    cs.setDouble("@wsISO_SFRetryCounts", iso.getWsISO_SFRetryCounts());
		    cs.setString("@wsISO_FlagStoreFprward", String.valueOf(iso.getWsISO_FlagStoreForward()));
		    cs.setDouble("@wISO_012_LocalDatetime_decimal", iso.getwISO_012_LocalDatetime_decimal());
		    cs.setDouble("@wISO_TempoTrx_Value", iso.getWsTempTrx());
		    cs.setDouble("@wISO_TempoBDD_Value", iso.getWsTempBDD());
		    cs.setDouble("@wISO_TempoAut_Value", iso.getWsTempAut());
		    cs.setString("@wISO_IP", iso.getWs_IP());
		    
		    iso.getTickBdd().reset();
		    iso.getTickBdd().start();
		    	cs.execute();
		    iso.getTickBdd().stop();
			return 0;
		    
		} catch (SQLException ex) {
				
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			iso.setWsTempBDD((iso.getTickBdd().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
	}
	
    public int InsertIso8583(wIso8583 iso, typeBDD type){
		
    	PreparedStatement cs = null;
    	Connection conn = null;
		try {			
			
			conn = DBCPDataSource.getConnection();
			String query = "begin tran \r\n" + 
					"        \r\n" + 
					"        INSERT INTO ISO8583\r\n" + 
					"        VALUES\r\n" + 
					"        (\r\n" + 
					"            '"+ iso.getISO_000_Message_Type() +"', \r\n" + 
					"            '"+ iso.getISO_BitMap() +"', \r\n" + 
					"            '"+ iso.getISO_002_PAN() +"', \r\n" + 
					"            '"+ iso.getISO_003_ProcessingCode() +"', \r\n" + 
					"            " + iso.getISO_004_AmountTransaction() +", \r\n" + 
					"            " + iso.getISO_006_BillAmount() +",     \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_007_TransDatetime(),"yyyy-dd-MM HH:mm:ss") +"',103), \r\n" + 
					"            " + iso.getISO_008_BillFeeAmount() +"    ,     \r\n" + 
					"            '"+ iso.getISO_011_SysAuditNumber() +"'   ,     \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_012_LocalDatetime(),"yyyy-dd-MM HH:mm:ss") +"',103), \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_013_LocalDate(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_015_SettlementDatel(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"            '"+ iso.getISO_018_MerchantType() +"', \r\n" + 
					"            '"+ iso.getISO_019_AcqCountryCode() +"', \r\n" + 
					"            '"+ iso.getISO_022_PosEntryMode() +"',  \r\n" + 
					"            '"+ iso.getISO_023_CardSeq() +"', \r\n" + 
					"            '"+ iso.getISO_024_NetworkId() +"', \r\n" + 
					"            " + iso.getISO_028_TranFeeAmount() + " ,\r\n" + 
					"            " + iso.getISO_029_SettlementFee() + " , \r\n" + 
					"            " + iso.getISO_030_ProcFee() + " , \r\n" + 
					"            '"+ iso.getISO_032_ACQInsID() +"', \r\n" + 
					"            '"+ iso.getISO_033_FWDInsID() +"', \r\n" + 
					"            '"+ iso.getISO_034_PANExt() +"', \r\n" + 
					"            '"+ iso.getISO_035_Track2() +"', \r\n" + 
					"            '"+ iso.getISO_036_Track3() +"', \r\n" + 
					"            '"+ iso.getISO_037_RetrievalReferenceNumber() +"', \r\n" + 
					"            '"+ iso.getISO_038_AutorizationNumber() + "', \r\n" + 
					"            '"+ iso.getISO_039_ResponseCode() + "', \r\n" + 
					"            '"+ iso.getISO_039p_ResponseDetail() + "', \r\n" + 
					"            '"+ iso.getISO_041_CardAcceptorID() + "', \r\n" + 
					"            '"+ iso.getISO_042_Card_Acc_ID_Code() + "', \r\n" + 
					"            '"+ iso.getISO_043_CardAcceptorLoc() + "', \r\n" + 
					"            '"+ iso.getISO_044_AddRespData() + "', \r\n" + 
					"            " + iso.getISO_049_TranCurrCode() + ", \r\n" + 
					"            " + iso.getISO_051_CardCurrCode() + ", \r\n" + 
					"            '"+ iso.getISO_052_PinBlock() + "', \r\n" + 
					"            '"+ iso.getISO_054_AditionalAmounts() + "', \r\n" + 
					"            '"+ iso.getISO_055_EMV() + "', \r\n" + 
					"            '"+ iso.getISO_090_OriginalData() + "',  \r\n" + 
					"            '"+ iso.getISO_102_AccountID_1() + "', \r\n" + 
					"            '"+ iso.getISO_103_AccountID_2() + "', \r\n" + 
					"            '"+ iso.getISO_104_TranDescription() + "', \r\n" + 
					"            '"+ iso.getISO_114_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_115_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_120_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_121_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_122_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_123_ExtendedData() + "', \r\n" + 
					"            '"+ iso.getISO_124_ExtendedData() + "', \r\n" + 
					"            " + iso.getWsIso_LogStatus() + ", \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getWsISO_TranDatetime(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"            CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getWsISO_TranDatetimeResponse(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"            " + iso.getWsISO_SFRetryCounts() + ", \r\n" + 
					"            '"+ iso.getWsISO_FlagStoreForward() + "', \r\n" + 
					"            " + (long)iso.getwISO_012_LocalDatetime_decimal() + ", \r\n" + 
					"            " + iso.getWsTempTrx() + ", \r\n" + 
					"            " + iso.getWsTempBDD() + ", \r\n" + 
					"            " + iso.getWsTempAut( )+ ", \r\n" + 
					"            '"+ iso.getWs_IP() + "' \r\n" + 
					"\r\n" + 
					"        )\r\n" + 
					"\r\n" + 
					"       commit tran";
			       cs = conn.prepareStatement(query);
		    int result = 0;
		    iso.getTickBdd().reset();
		    iso.getTickBdd().start();
		    	result = cs.executeUpdate();
		    if(iso.getTickBdd().isStarted())	
		    	iso.getTickBdd().stop();
		    if(result == 1)
				return 0;
		    else
		    	return -1;
		    
		} catch (SQLException ex) {
				
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertIso8583(BDDType), [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				  cs.close();
				if(conn!=null) 
				  conn.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			iso.setWsTempBDD((iso.getTickBdd().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
	}
	
    public int UpdateIso8583(wIso8583 iso){
		
    	CallableStatement cs = null;
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_UPDATEISO8583(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setString("@iwISO_000_Message_Type", iso.getISO_000_Message_Type());
			cs.setString("@iwISO_011_SysAuditNumber", iso.getISO_011_SysAuditNumber());
			cs.setTimestamp("@iwISO_012_LocalDatetime", new Timestamp(iso.getISO_012_LocalDatetime().getTime()));
			cs.setString("@iwISO_002_PAN", iso.getISO_002_PAN());
			cs.setString("@iwISO_BITMAP", iso.getISO_BitMap());
			cs.setString("@iwISO_003_ProcessingCode", iso.getISO_003_ProcessingCode());
			cs.setDouble("@iwISO_004_AmountTransaction", iso.getISO_004_AmountTransaction());
			cs.setDouble("@iwISO_006_BillAmount", iso.getISO_006_BillAmount());
			cs.setTimestamp("@iwISO_007_TransDatetime", new Timestamp(iso.getISO_007_TransDatetime().getTime()));
			cs.setDate("@iwISO_007_TransDatetime", FormatUtils.convertUtilToSql(iso.getISO_007_TransDatetime()));
			cs.setDouble("@iwISO_008_BillFeeAmount", iso.getISO_008_BillFeeAmount());					
			cs.setTimestamp("@iwISO_013_LocalDate", new Timestamp(iso.getISO_013_LocalDate().getTime()));
			cs.setDate("@iwISO_015_SettlementDatel", FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel()));
			cs.setString("@iwISO_018_MerchantType", iso.getISO_018_MerchantType());
			cs.setString("@iwISO_019_AcqCountryCode", iso.getISO_019_AcqCountryCode());
			cs.setString("@iwISO_022_PosEntryCode", iso.getISO_022_PosEntryMode());           
			cs.setString("@iwISO_023_CardSeq", iso.getISO_023_CardSeq());
			cs.setString("@iwISO_024_NetworkId", iso.getISO_024_NetworkId());
			cs.setDouble("@iwISO_028_TranFeeAmount", iso.getISO_028_TranFeeAmount());
			cs.setDouble("@iwISO_029_SettlementFee", iso.getISO_029_SettlementFee());
			cs.setDouble("@iwISO_030_ProcFee", iso.getISO_030_ProcFee());
			cs.setString("@iwISO_032_ACQInsID", iso.getISO_032_ACQInsID());
			cs.setString("@iwISO_033_FWDInsID", iso.getISO_033_FWDInsID());
			cs.setString("@iwISO_034_PANExt", iso.getISO_034_PANExt());
			cs.setString("@iwISO_035_Track2", iso.getISO_035_Track2());
			cs.setString("@iwISO_036_Track3", iso.getISO_036_Track3());
			cs.setString("@iwISO_037_RetrievalReferenceNro", iso.getISO_037_RetrievalReferenceNumber());
			cs.setString("@iwISO_038_AutorizationNumber", iso.getISO_038_AutorizationNumber());
			cs.setString("@iwISO_039_ResponseCode", iso.getISO_039_ResponseCode());
			if(StringUtils.IsNullOrEmpty(iso.getISO_039p_ResponseDetail()))
				iso.setISO_039p_ResponseDetail("VALOR NULO");
			if(iso.getISO_039p_ResponseDetail().length() > 4000)
				cs.setString("@iwISO_039p_ResponseDetail", iso.getISO_039p_ResponseDetail().substring(0,4000));
			else
				cs.setString("@iwISO_039p_ResponseDetail", iso.getISO_039p_ResponseDetail());
			cs.setString("@iwISO_041_CardAcceptorID", iso.getISO_041_CardAcceptorID());
			cs.setString("@iwISO_042_Card_Acc_ID_Code", iso.getISO_042_Card_Acc_ID_Code());
			cs.setString("@iwISO_043_CardAcceptorLoc", iso.getISO_043_CardAcceptorLoc());
			cs.setString("@iwISO_044_AddRespData", iso.getISO_044_AddRespData());
			cs.setDouble("@iwISO_049_TranCurrCode", iso.getISO_049_TranCurrCode());
			cs.setDouble("@iwISO_051_CardCurrCode", iso.getISO_051_CardCurrCode());
			cs.setString("@iwISO_052_PinBlock", iso.getISO_052_PinBlock());
			cs.setString("@iwISO_054_AditionalAmounts", iso.getISO_054_AditionalAmounts());
			cs.setString("@iwISO_055_EMV", iso.getISO_055_EMV());
			cs.setString("@iwISO_090_OriginalData", iso.getISO_090_OriginalData());
			cs.setString("@iwISO_102_AccountID_1", iso.getISO_102_AccountID_1());
			cs.setString("@iwISO_103_AccountID_2", iso.getISO_103_AccountID_2());
			cs.setString("@iwISO_104_TranDescription", iso.getISO_104_TranDescription());		
			cs.setString("@iwISO_114_ExtendedData", iso.getISO_114_ExtendedData());
			cs.setString("@iwISO_115_ExtendedData", iso.getISO_115_ExtendedData());
		    cs.setString("@iwISO_120_ExtendedData", iso.getISO_120_ExtendedData());
		    cs.setString("@iwISO_121_ExtendedData", iso.getISO_121_ExtendedData());
		    cs.setString("@iwISO_122_ExtendedData", iso.getISO_122_ExtendedData());
		    cs.setString("@iwISO_123_ExtendedData", iso.getISO_123_ExtendedData());
		    cs.setString("@iwISO_124_ExtendedData", iso.getISO_124_ExtendedData());
		    cs.setDouble("@iwsISO_LogStatus", iso.getWsIso_LogStatus());
		    cs.setTimestamp("@iwsISO_TranDatetime", new Timestamp(iso.getWsISO_TranDatetime().getTime()));
		    cs.setTimestamp("@iwsISO_TranDatetimeResponse", new Timestamp(iso.getWsISO_TranDatetimeResponse().getTime()));
		    cs.setDouble("@iwsISO_SFRetryCounts", iso.getWsISO_SFRetryCounts());
		    cs.setString("@iwsISO_FlagStoreFprward", String.valueOf(iso.getWsISO_FlagStoreForward()));
		    cs.setDouble("@iwISO_012_LocalDatetime_decimal", iso.getwISO_012_LocalDatetime_decimal());
		    cs.setDouble("@iwISO_TempoTrx_Value", iso.getWsTempTrx());
		    cs.setDouble("@iwISO_TempoBDD_Value", iso.getWsTempBDD());
		    cs.setDouble("@iwISO_TempoAut_Value", iso.getWsTempAut());
		    cs.setString("@iwISO_IP", iso.getWs_IP());		    
		    
		    this.codeSqlError = cs.executeUpdate();
		    if(this.codeSqlError == 1)
		    	this.descriptionSqlError = "UPDATE SUCCESSFULL";
		    else if (this.codeSqlError == 0) {
		    	this.descriptionSqlError = "ERROR UPDATE!!! (NO SE ACTUALIZO NINGUN REGISTRO) SQLERROR("+ this.codeSqlError +")";
			}else {
				this.descriptionSqlError = "ERROR UPDATE!!! (SE ACTUALIZO MAS DE UN REGISTRO) SQLERROR("+ this.codeSqlError +")";
			}
		    	
			return this.codeSqlError;
		    
		} catch (SQLException ex) {
				
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = GeneralUtils.ExceptionToString("SQL ERROR UPDATE ", ex, false);
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {			
			this.codeSqlError = -11;
			this.descriptionSqlError = GeneralUtils.ExceptionToString("SQL ERROR UPDATE (GENERAL) ", e, false);
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		
	}
    
    public int UpdateIso8583(wIso8583 iso, typeBDD type){
		
    	PreparedStatement cs = null;
    	Connection conn = null;
    	
		try {			
			
			conn = DBCPDataSource.getConnection();
			String detalleError = StringUtils.Empty();
			if(StringUtils.IsNullOrEmpty(iso.getISO_039p_ResponseDetail()))
				detalleError = "VALOR NULO";
			if(iso.getISO_039p_ResponseDetail().length() > 2000)
				detalleError = iso.getISO_039p_ResponseDetail().substring(0,4000);
			else
				detalleError = iso.getISO_039p_ResponseDetail();
			
			String query = "begin\r\n" + 
					"       begin tran\r\n" + 
					"        \r\n" + 
					"        UPDATE  ISO8583\r\n" + 
					"        SET\r\n" + 
					"				          \r\n" + 
					"        WISO_BITMAP                 =         '"+ iso.getISO_BitMap() + "', \r\n" + 
					"        WISO_002_PAN                =         '"+ iso.getISO_002_PAN() + "', \r\n" + 
					"        WISO_003_PROCESSINGCODE     =         '"+ iso.getISO_003_ProcessingCode() +"',  \r\n" + 
					"        WISO_004_AMOUNTTRANSACTION	=    	   " + iso.getISO_004_AmountTransaction() +", \r\n" + 
					"        WISO_006_BILLAMOUNT       	=    	   " + iso.getISO_006_BillAmount() + ", \r\n" + 
					"        WISO_007_TRANSDATETIME    	=    	   CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_007_TransDatetime(),"yyyy-dd-MM HH:mm:ss") +"',103), \r\n" + 
					"        WISO_008_BILLFEEAMOUNT    	=    	   " + iso.getISO_008_BillFeeAmount() + ", \r\n" + 
					"        WISO_013_LOCALDATE        	=    	   CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_013_LocalDate(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"        WISO_015_SETTLEMENTDATEL  	=    	   CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_015_SettlementDatel(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"        WISO_018_MERCHANTTYPE     	=    	   '"+ iso.getISO_018_MerchantType() +"', \r\n" + 
					"        WISO_019_ACQCOUNTRYCODE   	=    	   '"+ iso.getISO_019_AcqCountryCode() +"', \r\n" + 
					"        WISO_022_POSENTRYMODE     	=    	   '"+ iso.getISO_022_PosEntryMode() + "', \r\n" + 
					"        WISO_023_CARDSEQ          	=    	   '"+ iso.getISO_023_CardSeq() + "', \r\n" + 
					"        WISO_024_NETWORKID        	=    	   '"+ iso.getISO_024_NetworkId() + "', \r\n" + 
					"        WISO_028_TRANFEEAMOUNT    	=    	   " + iso.getISO_028_TranFeeAmount() + " , \r\n" + 
					"        WISO_029_SETTLEMENTFEE    	=    	   " + iso.getISO_029_SettlementFee() + " , \r\n" + 
					"        WISO_030_PROCFEE          	=    	   " + iso.getISO_030_ProcFee() + " , \r\n" + 
					"        WISO_032_ACQINSID         	=    	   '"+ iso.getISO_032_ACQInsID() + "', \r\n" + 
					"        WISO_033_FWDINSID         	=    	   '"+ iso.getISO_033_FWDInsID() + "', \r\n" + 
					"        WISO_034_PANEXT           	=    	   '"+ iso.getISO_034_PANExt() + "', \r\n" + 
					"        WISO_035_TRACK2           	=    	   '"+ iso.getISO_035_Track2() + "', \r\n" + 
					"        WISO_036_TRACK3           	=    	   '"+ iso.getISO_036_Track3() + "', \r\n" + 
					"        WISO_037_RETRIEVALREFERENCENRO =	   '"+ iso.getISO_037_RetrievalReferenceNumber() +"', \r\n" + 
					"        WISO_038_AUTORIZATIONNUMBER    =      '"+ iso.getISO_038_AutorizationNumber() + "',  \r\n" + 
					"        WISO_039_RESPONSECODE     =    	   '"+ iso.getISO_039_ResponseCode() + "', \r\n" + 
					"        WISO_039P_RESPONSEDETAIL  =    	   '"+ detalleError + "', \r\n" + 
					"        WISO_041_CARDACCEPTORID   =    	   '"+ iso.getISO_041_CardAcceptorID() + "', \r\n" + 
					"        WISO_042_CARD_ACC_ID_CODE =    	   '"+ iso.getISO_042_Card_Acc_ID_Code() + "', \r\n" + 
					"        WISO_043_CARDACCEPTORLOC  =           '"+ iso.getISO_043_CardAcceptorLoc() + "', \r\n" + 
					"        WISO_044_ADDRESPDATA      =    	   '"+ iso.getISO_044_AddRespData() + "', \r\n" + 
					"        WISO_049_TRANCURRCODE     =    	   " + iso.getISO_049_TranCurrCode() + ", \r\n" + 
					"        WISO_051_CARDCURRCODE     =    	   " + iso.getISO_051_CardCurrCode() + ", \r\n" + 
					"        WISO_052_PINBLOCK         =    	   '"+ iso.getISO_052_PinBlock() + "', \r\n" + 
					"        WISO_054_ADITIONALAMOUNTS =    	   '"+ iso.getISO_054_AditionalAmounts() + "', \r\n" + 
					"        WISO_055_EMV              =    	   '"+ iso.getISO_055_EMV() + "', \r\n" + 
					"        WISO_090_ORIGINALDATA     =    	   '"+ iso.getISO_090_OriginalData() + "', \r\n" + 
					"        WISO_102_ACCOUNTID_1      =    	   '"+ iso.getISO_102_AccountID_1() + "', \r\n" + 
					"        WISO_103_ACCOUNTID_2      =    	   '"+ iso.getISO_103_AccountID_2() + "', \r\n" + 
					"        WISO_104_TRANDESCRIPTION  =           '"+ iso.getISO_104_TranDescription() + "', \r\n" + 
					"        WISO_114_EXTENDEDDATA     =    	   '"+ iso.getISO_114_ExtendedData() + "', \r\n" + 
					"        WISO_115_EXTENDEDDATA     =    	   '"+ iso.getISO_115_ExtendedData() + "', \r\n" + 
					"        WISO_120_EXTENDEDDATA     =    	   '"+ iso.getISO_120_ExtendedData() + "',  \r\n" + 
					"        WISO_121_EXTENDEDDATA     =    	   '"+ iso.getISO_121_ExtendedData() + "',  \r\n" + 
					"        WISO_122_EXTENDEDDATA     =    	   '"+ iso.getISO_122_ExtendedData() + "',  \r\n" + 
					"        WISO_123_EXTENDEDDATA     =    	   '"+ iso.getISO_123_ExtendedData() + "',  \r\n" + 
					"        WISO_124_EXTENDEDDATA     =    	   '"+ iso.getISO_124_ExtendedData() + "',  \r\n" + 
					"        WSISO_LOGSTATUS           =    	   " + iso.getWsIso_LogStatus() + ",  \r\n" + 
					"        WSISO_TRANDATETIME        =    	   CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getWsISO_TranDatetime(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"        WSISO_TRANDATETIMERESPONSE  =         CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getWsISO_TranDatetimeResponse(),"yyyy-dd-MM HH:mm:ss") + "',103), \r\n" + 
					"        WSISO_SFRETRYCOUNTS       =    	   " + iso.getWsISO_SFRetryCounts() + ", \r\n" + 
					"        WSISO_FLAGSTOREFPRWARD     =   	   '"+ iso.getWsISO_FlagStoreForward() + "',  \r\n" + 
					"        WISO_012_LOCALDATETIME_DECIMAL =	   " + (long)iso.getwISO_012_LocalDatetime_decimal() + ", \r\n" + 
					"        WISO_TEMPOTRX_VALUE        =   	   " + NumbersUtils.truncateDecimal(iso.getWsTempTrx(),6) + ", \r\n" + 
					"        WISO_TEMPOBDD_VALUE        =   	   " + NumbersUtils.truncateDecimal(iso.getWsTempBDD(),6) + ", \r\n" + 
					"        WISO_TEMPOAUT_VALUE        =   	   " + NumbersUtils.truncateDecimal(iso.getWsTempAut(),6)+ ", \r\n" + 
					"        WISO_IP       =                 	   '"+ iso.getWs_IP() + "'  \r\n" + 
					"        WHERE         \r\n" + 
					"        WISO_000_MESSAGE_TYPE = '"+ iso.getISO_000_Message_Type() +"' and\r\n" + 
					"        WISO_011_SYSAUDITNUMBER =  '"+ iso.getISO_011_SysAuditNumber() +"' and\r\n" + 
					"        WISO_012_LOCALDATETIME = CONVERT(DATETIME,'"+ FormatUtils.DateToString(iso.getISO_012_LocalDatetime(),"yyyy-dd-MM HH:mm:ss") +"',103) \r\n" + 
					"\r\n" + 
					"       \r\n" + 
					"       if exists(select PROCCODE from TRANSACTION_CONFIGURATION\r\n" + 
					"                 WHERE PROCCODE = '"+ iso.getISO_003_ProcessingCode() +"' AND\r\n" + 
					"                 CANAL_COD =  '"+ iso.getISO_018_MerchantType() +"'  AND \r\n" + 
					"                 NET_ID = convert (numeric,'"+ iso.getISO_024_NetworkId() +"') AND\r\n" + 
					"                 TRXNROPERMISSION <> -1 AND\r\n" + 
					"                 TRXCUPOMAX <> -1)\r\n" + 
					"       BEGIN\r\n" + 
					"           begin\r\n" + 
					"                declare @string varchar(4000)\r\n" + 
					"                declare @delimiter char(1), @pattern varchar(500)\r\n" + 
					"                declare @sql varchar(4000)\r\n" + 
					"                declare @acum varchar(4000)\r\n" + 
					"                declare @flag int\r\n" + 
					"                \r\n" + 
					"                declare @contA numeric(8)\r\n" + 
					"                declare @nroTran numeric(8)\r\n" + 
					"                declare @LSecuencias varchar(850)                  \r\n" + 
					"                declare @LSecuencias2 varchar(850)\r\n" + 
					"                declare @lenSec numeric(10)\r\n" + 
					"                declare @lenSec2 numeric(10)\r\n" + 
					"                declare @err_code varchar(850)\r\n" + 
					"                declare @err_msg  varchar(850)\r\n" + 
					"                declare @xDocument varchar(850)\r\n" + 
					"                declare @xSecuencial varchar(850)\r\n" + 
					"                declare @pos int\r\n" + 
					"                declare @piece varchar(4000)\r\n" + 
					"\r\n" + 
					"                set @delimiter = '{' \r\n" + 
					"                set @contA = 0\r\n" + 
					"                set @lenSec2 = 0\r\n" + 
					"                set @nroTran = 0\r\n" + 
					"                set @acum = ''\r\n" + 
					"                set @flag = 0\r\n" + 
					"\r\n" + 
					"                if ('"+ iso.getISO_018_MerchantType() +"' = '0000')\r\n" + 
					"                   begin\r\n" + 
					"                     set @xDocument =  '"+ iso.getISO_002_PAN() +"' \r\n" + 
					"                     set @xSecuencial ='"+ iso.getISO_011_SysAuditNumber() +"' \r\n" + 
					"                   end\r\n" + 
					"                else\r\n" + 
					"                    begin\r\n" + 
					"                      set @xDocument =   '"+ iso.getISO_102_AccountID_1() + "' \r\n" + 
					"                      set @xSecuencial = '"+ iso.getISO_037_RetrievalReferenceNumber() + "' \r\n" + 
					"                     end\r\n" + 
					"                \r\n" + 
					"                if(substring('"+ iso.getISO_000_Message_Type() +"',1,2) = '12')\r\n" + 
					"                  begin\r\n" + 
					"                    if('"+ iso.getISO_039_ResponseCode() + "' <> '000')\r\n" + 
					"                    begin\r\n" + 
					"                        if('"+ iso.getISO_039_ResponseCode() + "' <> '120' and '"+ iso.getISO_039_ResponseCode() + "' <> '123')\r\n" + 
					"                          begin\r\n" + 
					"\r\n" + 
					"                                insert into BITACORA values('ENTRO ACA 1.0') " +        
					"                                select  @nroTran = LIM_NUMERO, @LSecuencias = LIM_SECUENCIAS \r\n" + 
					"                                from VALID_LIMITESTRX \r\n" + 
					"                                where LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() +"' and\r\n" + 
					"                                LIM_RED = '"+ iso.getISO_024_NetworkId() +"' and LIM_DOCUMENTO = @xDocument \r\n" + 
					"                                and LIM_CANAL = '"+ iso.getISO_018_MerchantType() +"' \r\n" + 
					"\r\n" + 
					"                                if(@nroTran > 0)\r\n" + 
					"                                  begin\r\n" + 
					"                                     if(@nroTran = 1)\r\n" + 
					"                                       begin\r\n" + 
					"\r\n" + 
					"\r\n" +
					"                                 insert into BITACORA values('ENTRO ACA 2.0') " +                                                             
					"                                            DELETE VALID_LIMITESTRX\r\n" + 
					"                                            WHERE  LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() +"' and\r\n" + 
					"                                            LIM_RED = '"+ iso.getISO_024_NetworkId() + "' and \r\n" + 
					"                                            LIM_DOCUMENTO = @xDocument and \r\n" + 
					"                                            LIM_CANAL = '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"\r\n" + 
					"\r\n" + 
					"                                       end\r\n" + 
					"                                       else\r\n" + 
					"                                         begin\r\n" + 
					"                                 insert into BITACORA values('ENTRO ACA 3.0') " +       
					"                                              set @lenSec = CHAR_LENGTH(@LSecuencias)\r\n" + 
					"\r\n" + 
					"                                              set @string = @LSecuencias\r\n" + 
					"\r\n" + 
					"                                              if right(rtrim(@string),1) <> @delimiter\r\n" +  
					"                                                set @string = @string  + @delimiter\r\n" + 
					"\r\n" + 
					"                                               set @pattern = '%' + @delimiter + '%'\r\n" + 
					"                                               set @pos =  patindex(@pattern , @string)\r\n" + 
					"\r\n" + 
					"                                               while @pos <> 0\r\n" + 
					"                                                begin\r\n" + 
					"                                                     set @piece = left(@string, @pos - 1)\r\n" + 
					"                                                     set @string = stuff(@string, 1, @pos, '')\r\n" + 
					"                                                     set @pos =  patindex(@pattern , @string)\r\n" + 
					"\r\n" + 
					"                                                     if(CHARINDEX(@xSecuencial, @piece) <> 0)\r\n" + 
					"                                                        set @flag = 1\r\n" + 
					"                                                     else\r\n" + 
					"                                                        set @flag = 0\r\n" + 
					"\r\n" + 
					"                                                    if(@flag = 1)\r\n" + 
					"                                                        set @acum = str_replace(@acum,@piece,'')\r\n" + 
					"                                                    else\r\n" + 
					"                                                        set @acum = @acum + @piece + '{'\r\n" + 
					"\r\n" + 
					"                                                end \r\n" + 
					"                                                insert into BITACORA values(@acum)\r\n" + 
					"                                                set @LSecuencias2 = @acum\r\n" + 
					"                                                set @lenSec2 = CHAR_LENGTH(@LSecuencias2)\r\n" + 
					"\r\n" + 
					"                                                if(@lenSec > @lenSec2)\r\n" + 
					"                                                  begin\r\n" + 
					"\r\n" + 
					"                                 insert into BITACORA values('ENTRO ACA 4.0') " +       
					"                                                        UPDATE  VALID_LIMITESTRX\r\n" + 
					"                                                        SET LIM_NUMERO = LIM_NUMERO - 1,\r\n" + 
					"                                                        LIM_MONTO = LIM_MONTO - " + iso.getISO_004_AmountTransaction() + ",\r\n" + 
					"                                                        LIM_SECUENCIAS = @LSecuencias2\r\n" + 
					"                                                        WHERE  LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and\r\n" + 
					"                                                        LIM_RED = '"+ iso.getISO_024_NetworkId() + "' and \r\n" + 
					"                                                        LIM_DOCUMENTO = @xDocument and \r\n" + 
					"                                                        LIM_CANAL = '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"\r\n" + 
					"										if exists(select ALERT_TRX from TRANSACTION_CONFIGURATION  \r\n" +
					"										        where PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and CANAL_COD = '" + iso.getISO_018_MerchantType() + "' \r\n" +  
					"										        and NET_ID = "+ iso.getISO_024_NetworkId() + " and ALERT_TRX <> '0')" + "\r\n" +  
					"										begin\r\n" + 
					"\r\n" +										
					"										update VALID_LIMIT_CHRONOLOGIC \r\n" +
					"										set LIM_NUMERO = LIM_NUMERO - 1,\r\n" + 
					"										   LIM_MONTO = LIM_MONTO - " + iso.getISO_004_AmountTransaction() + ",\r\n" +
					"										   LIM_DATE = GETDATE() \r\n" + 
					"										where LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and LIM_CANAL = '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"										     and LIM_RED = '"+ iso.getISO_024_NetworkId() + "' \r\n" + 
					"										     and LIM_DOCUMENTO = @xDocument \r\n" + 
                    "                                        end \r\n" + 
					"\r\n" + 
					"                                                  end\r\n" +  
					"\r\n" + 
					"                                         end\r\n" + 
					"                                  end\r\n" + 
					"                          end\r\n" + 
					"                      end \r\n" + 
					"                end\r\n" + 
					"                else\r\n" + 
					"                begin\r\n" + 
					"                        \r\n" + 
					"                        if('"+ iso.getISO_039_ResponseCode() + "' = '000')\r\n" + 
					"                          begin\r\n" + 
					"\r\n" + 
					"                            select  @nroTran = LIM_NUMERO, @LSecuencias=LIM_SECUENCIAS\r\n" + 
					"                            from VALID_LIMITESTRX \r\n" + 
					"                            where LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and\r\n" + 
					"                            LIM_RED = '"+ iso.getISO_024_NetworkId() +"' and LIM_DOCUMENTO = @xDocument \r\n" + 
					"                            and LIM_CANAL = '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"\r\n" + 
					"                            if(@nroTran > 0)\r\n" + 
					"                              begin\r\n" + 
					"                                set @lenSec = CHAR_LENGTH(@LSecuencias)\r\n" + 
					"                                set @string = @LSecuencias\r\n" + 
					"                                \r\n" + 
					"                                if right(rtrim(@string),1) <> @delimiter\r\n" + 
					"                                    set @string = @string  + @delimiter\r\n" + 
					"\r\n" + 
					"                                set @pattern = '%' + @delimiter + '%'\r\n" + 
					"                                set @pos =  patindex(@pattern , @string)\r\n" + 
					"\r\n" + 
					"                               while @pos <> 0\r\n" + 
					"                                begin\r\n" + 
					"                                     set @piece = left(@string, @pos - 1)\r\n" + 
					"                                     set @string = stuff(@string, 1, @pos, '')\r\n" + 
					"                                     set @pos =  patindex(@pattern , @string)\r\n" + 
					"                                     \r\n" + 
					"                                     if(CHARINDEX(@xSecuencial, @piece) <> 0)\r\n" + 
					"                                        set @flag = 1\r\n" + 
					"                                     else\r\n" + 
					"                                        set @flag = 0\r\n" + 
					"\r\n" + 
					"                                    if(@flag = 1)\r\n" + 
					"                                        set @acum = str_replace(@acum,@piece,'')\r\n" + 
					"                                    else\r\n" + 
					"                                        set @acum = @acum + @piece + '{'\r\n" + 
					"                                end \r\n" + 
					"                                set @LSecuencias2 = @acum\r\n" + 
					"                                set @lenSec2 = CHAR_LENGTH(@LSecuencias2)\r\n" + 
					"\r\n" + 
					"                                if(@lenSec > @lenSec2)\r\n" + 
					"                                  begin\r\n" + 
					"                                    \r\n" + 
					"                                         UPDATE  VALID_LIMITESTRX\r\n" + 
					"                                         SET LIM_NUMERO = LIM_NUMERO - 1,\r\n" + 
					"                                         LIM_MONTO = LIM_MONTO - " + iso.getISO_004_AmountTransaction() + ",\r\n" + 
					"                                         LIM_SECUENCIAS = @LSecuencias2\r\n" + 
					"                                        WHERE  LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and\r\n" + 
					"                                        LIM_RED = '"+ iso.getISO_024_NetworkId() + "' and \r\n" + 
					"                                        LIM_DOCUMENTO = @xDocument and \r\n" + 
					"                                        LIM_CANAL =  '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"\r\n" + 
					"										if exists(select ALERT_TRX from TRANSACTION_CONFIGURATION  \r\n" +
					"										        where PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and CANAL_COD = '" + iso.getISO_018_MerchantType() + "' \r\n" +  
					"										        and NET_ID = "+ iso.getISO_024_NetworkId() + " and ALERT_TRX <> '0')" + "\r\n" +  
					"										begin\r\n" + 
					"\r\n" +										
					"										update VALID_LIMIT_CHRONOLOGIC \r\n" +
					"										set LIM_NUMERO = LIM_NUMERO - 1,\r\n" + 
					"										   LIM_MONTO = LIM_MONTO - " + iso.getISO_004_AmountTransaction() + ",\r\n" +
					"										   LIM_DATE = GETDATE() \r\n" + 
					"										where LIM_PROCCODE = '"+ iso.getISO_003_ProcessingCode() + "' and LIM_CANAL = '"+ iso.getISO_018_MerchantType() + "' \r\n" + 
					"										     and LIM_RED = '"+ iso.getISO_024_NetworkId() + "' \r\n" + 
					"										     and LIM_DOCUMENTO = @xDocument \r\n" + 
                    "                                        end \r\n" + 
					"\r\n" + 
					"                                    \r\n" + 
					"                                  end\r\n" + 
					"                              end\r\n" + 
					"                          end\r\n" + 
					"                end\r\n" + 
					"\r\n" + 
					"           end \r\n" + 
					"       END\r\n" + 
					"     commit tran\r\n" + 
					"end";
			
			cs = conn.prepareStatement(query);
			
			
		    this.codeSqlError = cs.executeUpdate();
		    if(this.codeSqlError == 1)
		    	this.descriptionSqlError = "UPDATE SUCCESSFULL";
		    else if (this.codeSqlError == 0) {
		    	this.descriptionSqlError = "ERROR UPDATE!!! (NO SE ACTUALIZO NINGUN REGISTRO) SQLERROR("+ this.codeSqlError +")";
			}else {
				this.descriptionSqlError = "ERROR UPDATE!!! (SE ACTUALIZO MAS DE UN REGISTRO) SQLERROR("+ this.codeSqlError +")";
			}
		    	
			return this.codeSqlError;
		    
		} catch (SQLException ex) {
				
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = GeneralUtils.ExceptionToString("SQL ERROR UPDATE ", ex, false);
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {			
			this.codeSqlError = -11;
			this.descriptionSqlError = GeneralUtils.ExceptionToString("SQL ERROR UPDATE (GENERAL) ", e, false);
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateIso8583, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			try {
				if(cs!= null)
				 cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		
	}
    
    public int InsertSPIOrdenante(wIso8583 iso){
		
		CallableStatement cs = null;
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_INSERT_SPI_ORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setTimestamp("wFECHA", GeneralUtils.DateToTimestamp(new Date()));
			cs.setString("wMSGID", iso.getISO_121_ExtendedData());
			cs.setTimestamp("wCREDTTM", GeneralUtils.DateToTimestamp(iso.getISO_012_LocalDatetime()));
			cs.setString("wINSTRID", StringUtils.Empty());
			cs.setString("wENDTOENDID", iso.getISO_002_PAN() + iso.getISO_011_SysAuditNumber());
			cs.setString("wTXID", iso.getISO_102_AccountID_1() + iso.getISO_011_SysAuditNumber());
			cs.setDouble("wAMMOUNT", iso.getISO_004_AmountTransaction());
			cs.setTimestamp("wFECCONT", GeneralUtils.DateToTimestamp(iso.getISO_015_SettlementDatel()));
			cs.setString("wORD_NM", iso.getISO_034_PANExt());
			cs.setString("wORD_ID", iso.getISO_002_PAN().toUpperCase().trim());
			cs.setString("wORD_TXID", iso.getISO_022_PosEntryMode().equalsIgnoreCase("CED") ? "NIDN": 
				                      iso.getISO_022_PosEntryMode().equalsIgnoreCase("PAS")?"CCPT":"TXID");
			cs.setString("wORD_ACCOUNT", iso.getISO_102_AccountID_1());
			cs.setString("wORD_ACCOUNT_TYPE", iso.getISO_003_ProcessingCode().substring(2,3).equals("1")?"02":"01");
			cs.setString("wORD_ACCOUNT_TYPE_MONEY", iso.getISO_051_CardCurrCode() == 840 ? "USD":"XXX");
			cs.setString("wINST_ACC_BCE", MemoryGlobal.AccountConciliationVc); //ojo confirmar (Cuenta Coop en el BCE)
			cs.setString("wINST_ACC_BCE_TYPE", "01");
			cs.setString("wINST_ACC_BCE_TYPE_MONEY", "USD");
			cs.setString("wINST_ORD_CODE", MemoryGlobal.BCE_Efi_VC);
			cs.setString("wINST_ORD_AGE", iso.getISO_041_CardAcceptorID() + " - " + iso.getISO_042_Card_Acc_ID_Code());
			cs.setString("wINST_RCP_CODE", iso.getISO_033_FWDInsID().toUpperCase().trim());
			cs.setString("wRCP_NM", iso.getISO_114_ExtendedData());
			cs.setString("wRCP_ID", iso.getISO_115_ExtendedData() );
			cs.setString("wRCP_TXID", iso.getISO_035_Track2().equalsIgnoreCase("CED") ? "NIDN": 
                iso.getISO_035_Track2().equalsIgnoreCase("PAS")?"CCPT":"TXID");
			cs.setString("wRCP_ACCOUNT", iso.getISO_103_AccountID_2());
			if(iso.getISO_003_ProcessingCode().substring(4,6).equals("10"))
				cs.setString("wRCP_ACCOUNT_TYPE", "02");
			else
				cs.setString("wRCP_ACCOUNT_TYPE", "01");
			cs.setString("wRCP_ACCOUNT_TYPE_MONEY", iso.getISO_051_CardCurrCode() == 840 ? "USD":"XXX");
			cs.setString("wPURP_CODE", "17");
			cs.setString("wCOD_ERR0R_AUTH", iso.getISO_039_ResponseCode());
			cs.setString("wDES_ERROR_AUT", iso.getISO_039p_ResponseDetail());
			cs.setDouble("wLOGSTATUS", iso.getWsIso_LogStatus());
			cs.setDouble("wFITSWITCH_TIME", 0);
			cs.setDouble("wBDD_TIME", 0);
			cs.setDouble("wAUTH_TIME", 0);
			cs.setString("wCOD_RETURN_CORE", iso.getISO_044_AddRespData());
			cs.setString("wREVER_FLAG", StringUtils.Empty());
			cs.setString("wREVER_RETURN_CORE_CODE", StringUtils.Empty());
			cs.setTimestamp("wDATE_LAST_BCE", GeneralUtils.DateToTimestamp(FormatUtils.StringToDate(iso.getISO_122_ExtendedData(), "YYYY-MM-DD HH:mm:ss")));
			cs.setString("wMSGID_LAST_BE", iso.getISO_123_ExtendedData());
			cs.setString("wSTATUS_BCE", iso.getISO_104_TranDescription());
			String IsoXml = SerializationObject.ObjectToString(new Iso8583(iso), Iso8583.class);
			//log.WriteLogMonitor("VALOR ISO A INSERTAR ++++++++++++++ " + IsoXml, TypeMonitor.monitor, null);
			cs.setString("wISOMESSAGE", IsoXml);
			cs.setString("wSTATUS_REASON_BCE", "N/D");
			cs.setString("wISMAYORISTA", iso.getISO_BitMap());
			
			log.WriteLogMonitor("Insert>>>>>>>> " + iso.getISO_102_AccountID_1() + "   " + iso.getISO_103_AccountID_2(), TypeMonitor.monitor, null);
			
			
		    iso.getTickBdd().reset();
		    iso.getTickBdd().start();
		    	cs.execute();
		    iso.getTickBdd().stop();
			return 0;
		    
		} catch (SQLException ex) {
				
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertSPIOrdenante, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertSPIOrdenante, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			iso.setWsTempBDD((iso.getTickBdd().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
	}
    
    public int UpdateSPIOrdenante(SnpSPIOrdenante snp){
		
		CallableStatement cs = null;
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_UPDATE_SSPI_ORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setTimestamp("wFECHA", GeneralUtils.DateToTimestamp(snp.getFecha()));
			cs.setString("wMSGID", snp.getMsgid());
			cs.setTimestamp("wCREDTTM", GeneralUtils.DateToTimestamp(snp.getCredttm()));
			cs.setString("wINSTRID", snp.getInstrid());
			cs.setString("wENDTOENDID", snp.getEndtoendid());
			cs.setString("wTXID", snp.getTxid());
			cs.setDouble("wAMMOUNT", snp.getAmmount());
			cs.setTimestamp("wFECCONT", GeneralUtils.DateToTimestamp(snp.getFeccont()));
			cs.setString("wORD_NM", snp.getOrd_nm());
			cs.setString("wORD_ID", snp.getOrd_id());
			cs.setString("wORD_TXID", snp.getOrd_txid());
			cs.setString("wORD_ACCOUNT", snp.getOrd_account());
			cs.setString("wORD_ACCOUNT_TYPE", snp.getOrd_account_type());
			cs.setString("wORD_ACCOUNT_TYPE_MONEY", snp.getOrd_account_type_money());
			cs.setString("wINST_ACC_BCE", snp.getInst_acc_bce()); 
			cs.setString("wINST_ACC_BCE_TYPE", snp.getInst_acc_bce_type());
			cs.setString("wINST_ACC_BCE_TYPE_MONEY", "USD");
			cs.setString("wINST_ORD_CODE", snp.getInst_ord_code());
			cs.setString("wINST_ORD_AGE", snp.getInst_ord_age());
			cs.setString("wINST_RCP_CODE", snp.getInst_rcp_code());
			cs.setString("wRCP_NM", snp.getRcp_nm());
			cs.setString("wRCP_ID",  snp.getRcp_id());
			cs.setString("wRCP_TXID", snp.getTxid());
			cs.setString("wRCP_ACCOUNT", snp.getRcp_account());
			cs.setString("wRCP_ACCOUNT_TYPE", snp.getRcp_account_type());
			cs.setString("wRCP_ACCOUNT_TYPE_MONEY", snp.getRcp_account_type_money());
			cs.setString("wPURP_CODE", snp.getPurp_code());
			cs.setString("wCOD_ERR0R_AUTH", snp.getCod_error_auth());
			cs.setString("wDES_ERROR_AUT", snp.getDes_error_auth());
			cs.setDouble("wLOGSTATUS", snp.getLogstatus());
			cs.setDouble("wFITSWITCH_TIME", 0);
			cs.setDouble("wBDD_TIME", 0);
			cs.setDouble("wAUTH_TIME", 0);
			cs.setString("wCOD_RETURN_CORE", snp.getCod_return_core());
			cs.setString("wREVER_FLAG", snp.getRever_flag());
			cs.setString("wREVER_RETURN_CORE_CODE", snp.getRever_return_core_code());
			cs.setTimestamp("wDATE_LAST_BCE", GeneralUtils.DateToTimestamp(snp.getDate_last_bce()));
			cs.setString("wMSGID_LAST_BE", snp.getMsgid_last_be());
			cs.setString("wSTATUS_BCE", snp.getStatus_bce());
			cs.setString("wISOMESSAGE", snp.getIso_message());
			cs.setString("wSTATUS_REASON_BCE", snp.getStatus_reason_bce());
			cs.setString("wISMAYORISTA", snp.getIsMayorista());
			cs.setString("wISORDENANTE", String.valueOf(snp.isOrdenante()).toUpperCase());
			
			
			
			cs.execute();
			return 0;
		    
		} catch (SQLException ex) {
				
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateSPIOrdenante, [ISO11:] " + snp.getMsgid(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {
		
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::UpdateSPIOrdenante, [ISO11:] " + snp.getMsgid(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			
		}
		
	}
    
    public void RegisterRCVD_LotesSPI(wIso8583 isoRequest) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_REGISTER_RCVD_BATCH_EFI_SPI(?,?,?,?) }");
  			cs.setString("i_MessageId", isoRequest.getISO_121_ExtendedData());
  			cs.setString("i_ReferenceMessageBCE", isoRequest.getISO_123_ExtendedData());
  			cs.setTimestamp("i_DateLastMessageResponseBCE", FormatUtils.convertStringToTimestamp(isoRequest.getISO_122_ExtendedData(), 
  					       "yyyy-MM-dd HH:mm:ss"));
  			cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
  			cs.execute();
  			ResultSet rs = (ResultSet)cs.getObject("p_iso_resulset");
  			int count = 0;
  			
  			if(rs != null){
  				
  				while (rs.next()) {
  					count++;
  					if(count > 1)
  						break;
  					
  					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
  					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
  					
  					isoRequest.setWsIso_LogStatus(2);
  					isoRequest.setWsISO_TranDatetimeResponse(new Date());
  					
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO TUVO EXITO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RegisterRCVD_LotesSPI ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::RegisterRCVD_LotesSPI (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::RegisterRCVD_LotesSPI ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			log.WriteLogMonitor("============ ACTUALIZANDO RCVD LOTES, RESPONSE: " + isoRequest.getISO_039_ResponseCode() 
  			                    + " -- " + isoRequest.getISO_039p_ResponseDetail() , TypeMonitor.monitor, null);
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  	}
    
    public int InsertDataEvaluationEquifax(wIso8583 iso, RegisterData data){
		
		CallableStatement cs = null;
		
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_INSERT_INFO_EQUIFAX(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setString("iNumeroDocumento", data.getNumeroDocumento());
			cs.setString("iTipoConsulta", data.getTipoConsuta());
			cs.setString("iTipoCreditoSolicitado", data.getTipoCreditoSolicitado());
			cs.setDouble("iMontoSolicitado", data.getMontoSolicitado());
			cs.setInt("iPlazoSolicitado", data.getPlazoSolicitado());
			cs.setDouble("iGastosFinancieros", data.getGastosFinancieros());
			cs.setString("iTipoCliente", data.getTipoCliente());
			cs.setDouble("iCapacidadPago", data.getCapacidadPago());
			cs.setInt("iScoreTitular", data.getScoreTitular());
			cs.setInt("iScoreTitSobreEndeu", data.getScoreTitularSobreendeudamiento());
			cs.setString("iNumeroDocumentoConyuge", data.getNumeroDocumentoConyuge());
			cs.setInt("iScoreConyuge", data.getScoreConyuge());
			cs.setString("iDecision", data.getDesicion());
			cs.setString("iSegmento", data.getSegmento());
			cs.setString("iModelo", data.getModelo());
			cs.setDouble("iMontoSugerido", data.getMontoSugerido());
			cs.setInt("iPlazoSugerido", data.getPlazoSugerido());
			cs.setTimestamp("iFechaConsulta", GeneralUtils.DateToTimestamp(data.getFechaConsulta()));
			cs.setString("iUsuarioConsulta", data.getUsuarioConsulta());
			cs.setDouble("iIdConsultaEquifax", data.getIdConsultaEquifax());
		    
		    iso.getTickBdd().reset();
		    iso.getTickBdd().start();
		    	cs.execute();
		    iso.getTickBdd().stop();
			return 0;
		    
		} catch (SQLException ex) {
				
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertDataEvaluationEquifax, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {
			if(iso.getTickBdd().isStarted())
				iso.getTickBdd().stop();
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertDataEvaluationEquifax, [ISO11:] " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			iso.setWsTempBDD((iso.getTickBdd().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
	}
    
    public int InsertMassiveTransfer(Iso8583 data){
		
		CallableStatement cs = null;
		
		try {			
			
			cs = MemoryGlobal.conn.prepareCall("{call SW_INSERT_TRANSFER_FINANCOOP(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");		
			cs.setInt("iTRANSACTIONID", Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(1)));
			cs.setInt("iTRANSACTIONITEMID", Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(2)));
			cs.setInt("iSUBTRANSACTIONTYPEID", Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(0)));
			cs.setInt("iTRANSACTIONFEATUREID", Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(1)));
			cs.setString("iCURRENCYID", String.valueOf(data.getISO_049_TranCurrCode()).replace(".0", StringUtils.Empty()));
			cs.setTimestamp("iVALUEDATE", GeneralUtils.DateToTimestamp(data.getISO_012_LocalDatetime()));
			cs.setInt("iTRANSACTIONTYPEID", Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(2)));
			cs.setInt("iTRANSACTIONSTATUSID", Integer.parseInt(data.getISO_104_TranDescription()));
			cs.setString("iCLIENTBANKID", data.getISO_023_CardSeq());
			cs.setString("iDEBITPRODUCTBANKID", data.getISO_102_AccountID_1());
			cs.setInt("iDEBITPRODUCTTYPEID", Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(0)));
			cs.setString("iDEBITCURRENCYID", "840");
			cs.setString("iCREDITPRODUCTBANKID", data.getISO_103_AccountID_2());
			cs.setInt("iCREDITPRODUCTTYPEID", Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(1)));
			cs.setString("iCREDITCURRENCYID", "840");
			cs.setDouble("iAMOUNT", data.getISO_004_AmountTransaction());
			cs.setString("iNOTIFYTO", Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(0));
			cs.setString("iNOTIFICATIONCHANNELID", Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(1));
			cs.setString("iDESTINATIONDOCUMENTID", Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(0));
			cs.setString("iDESTINATIONNAME", Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(1));
			cs.setString("iDESTINATIONBANK", Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(2));
			cs.setString("iDESCRIPTION", data.getISO_034_PANExt());
			cs.setString("iBANKROUTINGNUMBER", data.getISO_033_FWDInsID());
			cs.setString("iSOURCENAME", Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(1));
			cs.setString("iSOURCEBANK", Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(2));
			cs.setString("iSOURCEDOCUMENTID", Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(0));
			cs.setString("iREGULATIONAMOUNTEXCEEDED", data.getISO_035_Track2());
			cs.setString("iSOURCEFUNDS", StringUtils.IsNullOrEmpty(data.getISO_036_Track3())?
										 StringUtils.Empty():Arrays.asList(data.getISO_036_Track3().split("\\|")).get(0));
			cs.setString("iDESTINATIONFUNDS", StringUtils.IsNullOrEmpty(data.getISO_036_Track3())?
										StringUtils.Empty():Arrays.asList(data.getISO_036_Track3().split("\\|")).get(1));
			cs.setString("iUSERDOCUMENTID", data.getISO_002_PAN());
			cs.setDouble("iTRANSACTIONCOST", data.getISO_006_BillAmount());
			cs.setString("iTRANSACTIONCOSTCURRENCYID", String.valueOf(data.getISO_051_CardCurrCode()).replace(".00", StringUtils.Empty()));
			cs.setDouble("iEXCHANGERATE", data.getISO_008_BillFeeAmount());
			cs.setString("iISVALID", data.getISO_123_ExtendedData());
			cs.setString("iVALIDATIONMESSAGE", data.getISO_124_ExtendedData());
			
		    
		    cs.execute();
			return 0;
		    
		} catch (SQLException ex) {
				
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertMassiveTransferFinancoop, [ISO11:] " + data.getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			return ex.getErrorCode();
			
		} catch (Exception e) {

			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertMassiveTransferFinancoop, [ISO11:] " + data.getISO_011_SysAuditNumber(), TypeMonitor.error, e);
			return -11;
		}finally {
			
			try {
				if(cs!= null)
				 cs.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		
	}
    
    public void InsertMassiveTransferFinancoopII(List<Iso8583> iso){
		
	    Statement stmt = null;
	    StringBuilder query = new StringBuilder();
	    String c36_0 = StringUtils.Empty();
	    String c36_1 = StringUtils.Empty();
	    for (Iso8583 data : iso) {
			
	    	 		query.append(" insert into trans_masivas_financoop "); 
	    	 		query.append(" values(" + Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(1)) + ",");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(2)) + ",");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(0)) + ",");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(1)) + ",");
	    	 		query.append("'" + String.valueOf(data.getISO_049_TranCurrCode()).replace(".0", StringUtils.Empty()) + "',");
	    	 		query.append("TO_TIMESTAMP('");
	    	 		query.append(GeneralUtils.DateToTimestamp(data.getISO_012_LocalDatetime()) + "','YYYY-MM-DD HH24:MI:SS.FF9'),");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(2)) + ",");
	    	 		query.append(Integer.parseInt(data.getISO_104_TranDescription()) + ",");
	    	 		query.append("'" + data.getISO_023_CardSeq() + "',");
	    	 		query.append("'" + data.getISO_102_AccountID_1() + "',");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(0)) + ",");
	    	 		query.append("'840'" + ",");
	    	 		query.append("'" +data.getISO_103_AccountID_2() + "',");
	    	 		query.append(Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(1)) + ",");
	    	 		query.append("'840'" + ",");
	    	 		query.append(data.getISO_004_AmountTransaction() + ",");
	    	 		query.append("'" + Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(0) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(1) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(0) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(1) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(2) + "',");
	    	 		query.append("'" + data.getISO_034_PANExt() + "',");
	    	 		query.append("'" + data.getISO_033_FWDInsID() + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(1) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(2) + "',");
	    	 		query.append("'" + Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(0) + "',");
	    	 		query.append("'" + data.getISO_035_Track2() + "',");
	    	 		if(!StringUtils.IsNullOrEmpty(data.getISO_036_Track3()))
	    	 		 c36_0 = Arrays.asList(data.getISO_036_Track3().split("\\|")).get(0);
	    	 		query.append("'" + c36_0 + "',");				
	    	 		if(!StringUtils.IsNullOrEmpty(data.getISO_036_Track3()))
		    	 	  c36_1 = Arrays.asList(data.getISO_036_Track3().split("\\|")).get(1);
	    	 		query.append("'" + c36_1 + "',");
	    	 		query.append("'" + data.getISO_002_PAN()+ "',");
	    	 		query.append(data.getISO_006_BillAmount() + ",");
	    	 		query.append("'" +String.valueOf(data.getISO_051_CardCurrCode()).replace(".00", StringUtils.Empty()) + "',");
	 	    		query.append(data.getISO_008_BillFeeAmount() + ",");
	 	    		query.append("'" + data.getISO_123_ExtendedData() + "',");
	 	    		query.append("'" + data.getISO_124_ExtendedData() + "')\r\n");
	 	    		                
		      }
	   
	    int response = 0;	
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(1200);
	        response  = stmt.executeUpdate(query.toString());
	        System.out.println(response);
	        
	    } catch (SQLException e ) {
	       
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    }
    
    public void insertMassiveFinacoopIII(List<Iso8583> iso) {
        final int batchSize = iso.size(); //Batch size is important.
        this.masiveMantenaince = new int[iso.size()];
        PreparedStatement cs = null;
        try {
            String sql = "INSERT INTO trans_masivas_financoop VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cs = MemoryGlobal.conn.prepareStatement(sql); 

            int insertCount=0;
            for (Iso8583 data : iso) {
            	
            	cs.setInt(1, Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(1)));
    			cs.setInt(2, Integer.parseInt(Arrays.asList(data.getISO_011_SysAuditNumber().split("\\_")).get(2)));
    			cs.setInt(3, Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(0)));
    			cs.setInt(4, Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(1)));
    			cs.setString(5, String.valueOf(data.getISO_049_TranCurrCode()).replace(".0", StringUtils.Empty()));
    			cs.setTimestamp(6, GeneralUtils.DateToTimestamp(data.getISO_012_LocalDatetime()));
    			cs.setInt(7, Integer.parseInt(Arrays.asList(data.getISO_090_OriginalData().split("\\|")).get(2)));
    			cs.setInt(8, Integer.parseInt(data.getISO_104_TranDescription()));
    			cs.setString(9, data.getISO_023_CardSeq());
    			cs.setString(10, data.getISO_102_AccountID_1());
    			cs.setInt(11, Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(0)));
    			cs.setString(12, "840");
    			cs.setString(13, data.getISO_103_AccountID_2());
    			cs.setInt(14, Integer.parseInt(Arrays.asList(data.getISO_022_PosEntryMode().split("\\|")).get(1)));
    			cs.setString(15, "840");
    			cs.setDouble(16, data.getISO_004_AmountTransaction());
    			cs.setString(17, Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(0));
    			cs.setString(18, Arrays.asList(data.getISO_120_ExtendedData().split("\\|")).get(1));
    			cs.setString(19, Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(0));
    			cs.setString(20, Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(1));
    			cs.setString(21, Arrays.asList(data.getISO_121_ExtendedData().split("\\|")).get(2));
    			cs.setString(22, data.getISO_034_PANExt());
    			cs.setString(23, data.getISO_033_FWDInsID());
    			cs.setString(24, Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(1));
    			cs.setString(25, Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(2));
    			cs.setString(26, Arrays.asList(data.getISO_122_ExtendedData().split("\\|")).get(0));
    			cs.setString(27, data.getISO_035_Track2());
    			cs.setString(28, StringUtils.IsNullOrEmpty(data.getISO_036_Track3())?
    										 StringUtils.Empty():Arrays.asList(data.getISO_036_Track3().split("\\|")).get(0));
    			cs.setString(29, StringUtils.IsNullOrEmpty(data.getISO_036_Track3())?
    										StringUtils.Empty():Arrays.asList(data.getISO_036_Track3().split("\\|")).get(1));
    			cs.setString(30, data.getISO_002_PAN());
    			cs.setDouble(31, data.getISO_006_BillAmount());
    			cs.setString(32, String.valueOf(data.getISO_051_CardCurrCode()).replace(".00", StringUtils.Empty()));
    			cs.setDouble(33, data.getISO_008_BillFeeAmount());
    			cs.setString(34, data.getISO_123_ExtendedData());
    			cs.setString(35, data.getISO_124_ExtendedData());
    			
                cs.addBatch();
                
                if (++insertCount % batchSize == 0) {
                	this.masiveMantenaince = cs.executeBatch();
                }
            }
            this.masiveMantenaince = cs.executeBatch();
            MemoryGlobal.conn.commit();

        } catch (SQLException ex) {
			
        	try {
				MemoryGlobal.conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.codeSqlError = ex.getErrorCode();
			this.descriptionSqlError = ex.getMessage().toString() + " " + ex.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertMassiveTransferFinancoop, [ISO11:] " + iso.get(0).getISO_011_SysAuditNumber(), TypeMonitor.error, ex);
			
		} catch (Exception e) {

			try {
				MemoryGlobal.conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			this.codeSqlError = -11;
			this.descriptionSqlError = e.getMessage().toString() + " " + e.getStackTrace();
			log.WriteLogMonitor("Error modulo IsoSqlMaintenance::InsertMassiveTransferFinancoop, [ISO11:] " + iso.get(0).getISO_011_SysAuditNumber(), TypeMonitor.error, e);
		}
       finally {
        try {
        	if(cs!= null)
            	cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  } 
    
  public wIso8583 creditDebitCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1);
			
			log.WriteLog("RQ>>>> " + (procname.contains("debito")?"Debito":"Credito") + " Cobis:  Sec=>" + iso.getISO_011_SysAuditNumber() 
			        + " Cuenta=> " + iso.getISO_102_AccountID_1() + " Valor: " + 
					String.valueOf(iso.getISO_004_AmountTransaction()), TypeLog.debug, TypeWriteLog.file);
			
	        cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ? )}");
	        
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime()));
			cs.setDate(3, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel()));
			cs.setString(4, iso.getISO_018_MerchantType());
			cs.setString(5, iso.getISO_037_RetrievalReferenceNumber());
			cs.setString(6,  procname.contains("debito")?"NOTA DE DEBITO BIMO":"NOTA DE CREDITO BIMO");
			cs.setObject(7, iso.getISO_102_AccountID_1());
			cs.setDouble(8, iso.getISO_004_AmountTransaction());
			cs.setString(9, procname.contains("debito")?"NOTA DE DEBITO BIMO":"NOTA DE CREDITO BIMO");
			cs.setString(10, Arrays.asList(iso.getWsTransactionConfig().getProccodeParams().split("\\-")).get(0));
			cs.setString(11, iso.getISO_032_ACQInsID());
			//==============================
			cs.setInt(12,0);
			cs.setString(13, null);
			cs.setString(14, null);
			cs.setShort(15, (short) 0);
			cs.setString(16, null);
			cs.setDate(17, null);
			
			cs.registerOutParameter(18, Types.VARCHAR);
			cs.registerOutParameter(19, Types.DATE);
			cs.registerOutParameter(20, Types.DATE);
			cs.registerOutParameter(21, Types.VARCHAR);
			cs.setString(22, null);
			cs.registerOutParameter(23, Types.VARCHAR);
			cs.registerOutParameter(24, Types.VARCHAR);
			cs.registerOutParameter(25, Types.DECIMAL);
			cs.registerOutParameter(26, Types.VARCHAR);
			cs.registerOutParameter(27, Types.CHAR);
			cs.registerOutParameter(28, Types.VARCHAR);
			cs.registerOutParameter(29, Types.VARCHAR);
			cs.registerOutParameter(30, Types.VARCHAR);
			cs.registerOutParameter(31, Types.VARCHAR);
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			iso.setISO_039_ResponseCode(null);
			String error = cs.getString(29);
			String desc = cs.getString(30);
			
			if(error.equals("000")) {
				
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_090_OriginalData(cs.getString(18));
				iso.setISO_019_AcqCountryCode(cs.getString(31));
				
			}else {
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
				iso.setISO_090_OriginalData(cs.getString(18).toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RS>>>>" + (procname.contains("debito")?"Debito":"Credito") + " Cobis:  Sec=>" + iso.getISO_011_SysAuditNumber() 
			        + " Cuenta=>" + iso.getISO_102_AccountID_1() + " Valor: " + 
					String.valueOf(iso.getISO_004_AmountTransaction()) + " ===> " + error + "-" + desc, TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			
			log.WriteLog("RS>>>> Respuesta Debit/Credit: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::creditDebitCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			log.WriteLog("RS>>>> Respuesta Debit/Credit: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage() 
		                                 , TypeLog.debug, TypeWriteLog.file);
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("DEBIT/CREDIT ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::creditDebitCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
    
  public wIso8583 ReverCreditDebitCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2);
			log.WriteLog("RQ>>>>Reverso " + (procname.contains("debito")?"debito":"credito")  + " Cobis: Sec=>" + iso.getISO_011_SysAuditNumber() 
			        + " Cuenta=> " + iso.getISO_102_AccountID_1() + " Valor: " + 
					String.valueOf(iso.getISO_004_AmountTransaction()), TypeLog.debug, TypeWriteLog.file);
			
			
			cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}");
	        
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime()));
			cs.setDate(3, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel()));
			cs.setString(4, iso.getISO_018_MerchantType());
			cs.setString(5, iso.getISO_090_OriginalData()); //Sec. Original
			cs.setString(6,  iso.getISO_019_AcqCountryCode()); //Sec. Cobis
			cs.setObject(7, iso.getISO_102_AccountID_1());
			cs.setDouble(8, iso.getISO_004_AmountTransaction());
			cs.setString(9, procname.contains("debito")?"NOTA DE DEBITO BIMO":"NOTA DE CREDITO BIMO");
			cs.setString(10, iso.getISO_032_ACQInsID());
			//==============================
			cs.setInt(11,0);
			cs.setString(12, null);
			cs.setString(13, null);
			cs.setShort(14, (short) 0);
			cs.setString(15, null);
			cs.setDate(16, null);
			
			cs.registerOutParameter(17, Types.VARCHAR);
			cs.registerOutParameter(18, Types.DATE);
			cs.registerOutParameter(19, Types.DATE);
			cs.registerOutParameter(20, Types.VARCHAR);
			cs.setString(21, null);
			cs.registerOutParameter(22, Types.VARCHAR);
			cs.registerOutParameter(23, Types.VARCHAR);
			cs.registerOutParameter(24, Types.DECIMAL);
			cs.registerOutParameter(25, Types.VARCHAR);
			cs.registerOutParameter(26, Types.VARCHAR);
			cs.registerOutParameter(27, Types.VARCHAR);
			cs.registerOutParameter(28, Types.VARCHAR);
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			iso.setISO_039_ResponseCode(null);
			String error = cs.getString(27);
			String desc = cs.getString(28);
			
			if(error.equals("000")) {
				
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_090_OriginalData(cs.getString(22));
				
			}else {
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RQ>>>>Reverso "+ (procname.contains("debito")?"debito":"credito") +" Cobis: " + "Sec=>"  + iso.getISO_011_SysAuditNumber() 
			        + " Cuenta=>" + iso.getISO_102_AccountID_1() + " Valor: " + 
					String.valueOf(iso.getISO_004_AmountTransaction()) + "====> Respuesta: " + error + "-" 
					+ desc, TypeLog.debug, TypeWriteLog.file);
			
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			
			log.WriteLog("RS>>>> Respuesta Reverso: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::ReverCreditDebitCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			log.WriteLog("RS>>>> Respuesta Reverso: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("DEBIT/CREDIT ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::ReverCreditDebitCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
    
  public wIso8583 balanceCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			
			log.WriteLog("RQ>>>> Cuenta Consulta: Sec=>" + iso.getISO_011_SysAuditNumber() + " Cuenta=> " 
			             + iso.getISO_102_AccountID_1(), TypeLog.debug, TypeWriteLog.file);
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1);
			
			cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}");
	        
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime()));
			cs.setDate(3, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel()));
			cs.setString(4, iso.getISO_018_MerchantType());
			cs.setString(5, iso.getISO_102_AccountID_1());
			cs.setInt(6,  Integer.parseInt(Arrays.asList(iso.getWsTransactionConfig().getProccodeParams().split("\\-")).get(0)));
			cs.setString(7, Arrays.asList(iso.getWsTransactionConfig().getProccodeParams().split("\\-")).get(1));
			//==============================
			cs.setInt(8,0);
			cs.setString(9, null);
			cs.setString(10, null);
			cs.setShort(11, (short) 0);
			cs.setString(12, null);
			cs.setDate(13, null);
			
			cs.registerOutParameter(14, Types.VARCHAR);
			cs.registerOutParameter(15, Types.DATE);
			cs.registerOutParameter(16, Types.DATE);
			
			cs.registerOutParameter(17, Types.VARCHAR);
			cs.setString(18, null);
			cs.registerOutParameter(19, Types.VARCHAR);
			
			cs.registerOutParameter(20, Types.VARCHAR);
			
			cs.registerOutParameter(21, Types.DECIMAL);
			cs.registerOutParameter(22, Types.DECIMAL);
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			String error = cs.getString(19);
			String desc = cs.getString(20);
			
			BigDecimal saldo1 = cs.getBigDecimal(21);
			BigDecimal saldo2 = cs.getBigDecimal(22);
			BigDecimal zero = new BigDecimal(0);
			
			iso.setISO_039_ResponseCode(null);
			
			if(error.equals("000")) {
				
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_054_AditionalAmounts(String.valueOf(saldo2));
				iso.setISO_104_TranDescription(saldo2.compareTo(zero) >= 0 ? "C":"D");
				iso.setISO_019_AcqCountryCode(String.valueOf(saldo2) + "|" + String.valueOf(saldo1));
			}else {
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RS>>>> Respuesta Consulta: Sec=>" + iso.getISO_011_SysAuditNumber() + 
					     " Respuesta: " + error + "-" + desc + " Valores Saldo: " + iso.getISO_019_AcqCountryCode()
			                                 , TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			log.WriteLog("RS>>>> Respuesta Consulta: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::balanceCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {	
			
			log.WriteLog("RS>>>> Respuesta Consulta: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);

			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("BALANCE ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::balanceCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
  
  public wIso8583 createFastPersonCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		int codPersona = -1;
		
		try {
			
			iso.setISO_124_ExtendedData(String.valueOf(codPersona));
			log.WriteLog("RQ>>>> createFastPersonCobisBimo: Sec=>" 
			            + iso.getISO_011_SysAuditNumber() + "\n"
			            + " Apellidos Nombres: " + iso.getISO_034_PANExt().trim() + "\n"
			            + " Tipo de Documento: " + iso.getISO_022_PosEntryMode().trim() + "\n"
			            + " Nro de Documento: " + iso.getISO_002_PAN() + "\n"
			            + " Cod. Pais: " + iso.getISO_019_AcqCountryCode() + "\n"
			            + " Nro. Celular: " + iso.getISO_023_CardSeq() + "\n"
			            + " Correo: " + Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(0).trim() + "\n"
			            + " Estado Civil: " + Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(3).trim() + "\n"
			            + " Fecha Nac: " + iso.getISO_114_ExtendedData() + "\n"
			            + " Genero: " + Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(1).trim() + "\n"
			            
			             ,TypeLog.debug, TypeWriteLog.file);
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1);
			
			cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ? )}");
	        			
			
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime()));
			cs.setObject(3, iso.getISO_022_PosEntryMode()); //Tipo Documento
			cs.setString(4, iso.getISO_002_PAN().trim()); //Documento
			cs.setObject(5, iso.getISO_019_AcqCountryCode().trim()); //Cod.Pais 239 Ecuador
			cs.setString(6, iso.getISO_023_CardSeq().trim()); //Numero de Celular a Enrolar
			cs.setString(7, Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(0).trim()); //Correo
			cs.setDate(8, FormatUtils.convertUtilToSql((FormatUtils.StringToDate(iso.getISO_114_ExtendedData(), "yyyy-MM-dd"))));//Fecha de Nacimiento
			cs.setObject(9, Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(1).trim()); //Genero
			cs.setObject(10, Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(3).trim()); //Estado Civil
			cs.setObject(11, "A02"); //Profesion se quema por lo pronto
			cs.setObject(12, "30"); // Nivel Educacion quemado por lo pronto
			cs.setDate(13, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel())); //Fecha Contable
			cs.setString(14, iso.getISO_034_PANExt().trim().toUpperCase()); //Apellidos, nombres
			//==============================
			
			cs.registerOutParameter(15, Types.VARCHAR);
			cs.registerOutParameter(16, Types.DATE);
			cs.registerOutParameter(17, Types.VARCHAR);
			cs.registerOutParameter(18, Types.VARCHAR);
			cs.registerOutParameter(19, Types.VARCHAR);
			cs.registerOutParameter(20, Types.VARCHAR);
			cs.registerOutParameter(21, Types.VARCHAR);
			cs.registerOutParameter(22, Types.DATE);
			cs.registerOutParameter(23, Types.VARCHAR);
			cs.registerOutParameter(24, Types.VARCHAR);
			cs.registerOutParameter(25, Types.VARCHAR);
			cs.registerOutParameter(26, Types.VARCHAR);
			cs.registerOutParameter(27, Types.DATE);
			cs.registerOutParameter(28, Types.VARCHAR);
			cs.registerOutParameter(29, Types.VARCHAR);
			cs.registerOutParameter(30, Types.VARCHAR);
			cs.registerOutParameter(31, Types.INTEGER);
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			String error = cs.getString(29);
			String desc = cs.getString(30);
			
			iso.setISO_039_ResponseCode(null);
			
			if(error.equals("000")) {
				
				codPersona = cs.getInt(31); 
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_124_ExtendedData(String.valueOf(codPersona)); //Recupero CodPersona
				
			}else {
				
				if(error.equals("001")) {
					codPersona = cs.getInt(31); 
					iso.setISO_124_ExtendedData(String.valueOf(codPersona)); //Recupero CodPersona
				}
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RS>>>> Respuesta CreateFastPerson: Sec=>" + iso.getISO_011_SysAuditNumber() + 
					     " Respuesta: " + error + "-" + desc + " Codigo Persona: " + String.valueOf(codPersona)
			                                 , TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			log.WriteLog("RS>>>> Respuesta createFastPersonCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::createFastPersonCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {	
			
			log.WriteLog("RS>>>> Respuesta createFastPersonCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);

			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("createFastPersonCobisBimo ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::createFastPersonCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
    
  public wIso8583 getDataEnrollCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		String cuentaEnrolada = StringUtils.Empty();
		String IdCliente = StringUtils.Empty();
		
		try {
			
			log.WriteLog("RQ>>>> getDataEnrollCobisBimo: Sec=>" 
			            + iso.getISO_011_SysAuditNumber() + "\n"
			            + " Apellidos Nombres: " + iso.getISO_034_PANExt().trim() + "\n"
			            + " Nro de Documento: " + iso.getISO_002_PAN()            
			             ,TypeLog.debug, TypeWriteLog.file);
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3);
			
			cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}");
	        			
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime())); //fecha
			cs.setDate(3, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel())); //fecha contable
			cs.setString(4, iso.getISO_002_PAN()); //Identificacion
			//==============================
			
			cs.setInt(5, 602);
			cs.setString(6, "C");
			cs.setObject(7,"");
			cs.setInt(8, 0);
			cs.setString(9, null);
			cs.setObject(10, "");
			cs.setInt(11, 0);
			cs.setString(12, null);
			cs.setObject(13, "");
			cs.setString(14, null);
			cs.setDate(15, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel())); 

			
			cs.registerOutParameter(16, Types.VARCHAR);
			cs.registerOutParameter(17, Types.DATE);
			cs.registerOutParameter(18, Types.DATE);
			cs.registerOutParameter(19, Types.VARCHAR);
			cs.registerOutParameter(20, Types.INTEGER);
			cs.registerOutParameter(21, Types.VARCHAR);
			cs.registerOutParameter(22, Types.VARCHAR);
			cs.registerOutParameter(23, Types.VARCHAR);
			
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			String error = cs.getString(21);
			String desc = cs.getString(22);
			
			
			iso.setISO_039_ResponseCode(null);
			
			if(error.equals("000")) {
				
				IdCliente =  String.valueOf(cs.getInt(20));
				cuentaEnrolada = cs.getString(23);
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_103_AccountID_2(cuentaEnrolada); //Recupero Cuenta
				iso.setISO_124_ExtendedData(IdCliente);
				
			}else {
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RS>>>> Respuesta getDataEnrollCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
					     " Respuesta: " + error + "-" + desc + " Cuenta: " + cuentaEnrolada + " Id Cliente: " + IdCliente
			                                 , TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			log.WriteLog("RS>>>> Respuesta getDataEnrollCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataEnrollCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {	
			
			log.WriteLog("RS>>>> Respuesta getDataEnrollCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);

			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("getDataEnrollCobisBimo ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataEnrollCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
    
  public wIso8583 createAccountCobisBimo(wIso8583 iso, typeBDD type){
		
		Connection conn = null;
		CallableStatement cs = null;
		String Cuenta = StringUtils.Empty();
		
		try {
			
			log.WriteLog("RQ>>>> createAccountCobisBimo: Sec=>" 
			            + iso.getISO_011_SysAuditNumber() + "\n"
			            + " Apellidos Nombres: " + iso.getISO_034_PANExt().trim() + "\n"
			            + " Tipo de Documento: " + iso.getISO_022_PosEntryMode().trim() + "\n"
			            + " Nro de Documento: " + iso.getISO_002_PAN() + "\n"
			            + " Cod Persona: " + (StringUtils.IsNullOrEmpty(iso.getISO_124_ExtendedData())?"XXX":iso.getISO_124_ExtendedData()) + "\n"
			            + " Nro. Celular: " + iso.getISO_023_CardSeq() + "\n"	            
			             ,TypeLog.debug, TypeWriteLog.file);
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2);
			
			cs = conn.prepareCall("{call "+ procname +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}");
	        			
			
			cs.setString(1, iso.getISO_011_SysAuditNumber());
			cs.setDate(2, FormatUtils.convertUtilToSql(iso.getISO_012_LocalDatetime()));
			cs.setObject(3, iso.getISO_022_PosEntryMode()); //Tipo Documento
			cs.setInt(4, Integer.parseInt(iso.getISO_124_ExtendedData())); //Codigo Persona
			cs.setString(5, iso.getISO_023_CardSeq().trim()); //Numero de Celular a Enrolar
			cs.setString(6, iso.getISO_002_PAN()); //Documento
			cs.setDate(7, FormatUtils.convertUtilToSql(iso.getISO_015_SettlementDatel()));//Fecha Contable
			//==============================
			
			cs.registerOutParameter(8, Types.VARCHAR);
			cs.registerOutParameter(9, Types.DATE);
			cs.registerOutParameter(10, Types.VARCHAR);
			cs.registerOutParameter(11, Types.INTEGER);
			cs.registerOutParameter(12, Types.VARCHAR);
			cs.registerOutParameter(13, Types.VARCHAR);
			cs.registerOutParameter(14, Types.DATE);
			cs.registerOutParameter(15, Types.VARCHAR);
			cs.registerOutParameter(16, Types.VARCHAR);
			cs.registerOutParameter(17, Types.VARCHAR);
			
			
			cs.setQueryTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());	
			cs.execute();
			
			String error = cs.getString(15);
			String desc = cs.getString(16);
			
			iso.setISO_039_ResponseCode(null);
			
			if(error.equals("000")) {
				
				Cuenta = cs.getString(17); 
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_103_AccountID_2(Cuenta); //Recupero Cuenta
				
			}else {
				
				iso.setISO_039p_ResponseDetail(StringUtils.IsNullOrEmpty(desc)?"TRANSACCION NO PUDO SER PROCESADA COBIS_COD:" + 
				                                                                error :desc.toUpperCase());
			}
			iso.setISO_039_ResponseCode(error);
			iso.setWsIso_LogStatus(2);
			log.WriteLog("RS>>>> Respuesta CreateFastPerson: Sec=>" + iso.getISO_011_SysAuditNumber() + 
					     " Respuesta: " + error + "-" + desc + " Cuenta: " + Cuenta
			                                 , TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			log.WriteLog("RS>>>> Respuesta createAccountCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::createAccountCobisBimo (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {	
			
			log.WriteLog("RS>>>> Respuesta createAccountCobisBimo: Sec=>" + iso.getISO_011_SysAuditNumber() + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);

			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("createAccountCobisBimo ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::createAccountCobisBimo ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return iso;
	}
  
    public String executeBatchCobis(TypeBatch type, String procName){
		
		Connection conn = null;
		PreparedStatement cs = null;
		String retorno = StringUtils.Empty();
		
		try {
			
			log.WriteLog("RQ>>>> executeBatchCobis: Param=>"  + type.toString().substring(0,1)			                        
			             ,TypeLog.debug, TypeWriteLog.file);
			
			conn = DBCPDataSource.getConnection();
			String procname =procName;

			String stmtString = "exec " + procname + " @iType=?";

			cs = conn.prepareStatement(stmtString);
			
			cs.setString(1, type.toString().substring(0,1)	);
			
			ResultSet rs = cs.executeQuery();
			
			int count = 0;
			
			if(rs != null){
			
				while (rs.next()) {
				count++;
				if(count == 1)
					retorno = rs.getString(1) + "-" + rs.getString(2);
	
				if (count == 0) 
				    retorno = "908ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO";
				}
				
			}else {
			
				retorno = "909ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO (NULL)";
			}
			log.WriteLog("RS>>>> executeBatchCobis: =>>" + retorno
			                                 , TypeLog.debug, TypeWriteLog.file);
			
		} catch (SQLException ex) {
			
			retorno = ex.getErrorCode() + ex.getMessage();
			log.WriteLog("RS>>>> Respuesta executeBatchCobis: " +
				     " Respuesta: SQLException!!! " + ex.getErrorCode() + "-" + ex.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeBatchCobis (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {	
			
			log.WriteLog("RS>>>> Respuesta executeBatchCobis: " + 
				     " Respuesta: Exception!!! " + e.getMessage()
		                                 , TypeLog.debug, TypeWriteLog.file);

			retorno = "908" + GeneralUtils.ExceptionToString("executeBatchCobis ERROR ", e, false);
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeBatchCobis ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return retorno;
	}
    
    public Runnable RunnableUpdateIso8583(wIso8583 iso){
    	
    	Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				codeSqlError = UpdateIso8583(iso);
				log.WriteLogMonitor("<< UPDATE Trx: Iso011: " + iso.getISO_011_SysAuditNumber() + " " 
								    +  descriptionSqlError + " >>\n", TypeMonitor.monitor, null);
			}
		};
		return runnable;
    }
    
   public Runnable RunnableUpdateIso8583(wIso8583 iso, typeBDD type){
    	
    	Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				codeSqlError = UpdateIso8583(iso, type);
				log.WriteLogMonitor("<< UPDATE Transaction Trx: Iso011: " + iso.getISO_011_SysAuditNumber() + " " 
								    +  descriptionSqlError + " >>\n", TypeMonitor.monitor, null);
			}
		};
		return runnable;
    }
    
    public Runnable RunnableInsertIso8583(wIso8583 iso){
    	
    	Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				iso.setISO_000_Message_Type(iso.getISO_000_Message_Type().contains("00")? 
						iso.getISO_000_Message_Type().replace("00", "10"):iso.getISO_000_Message_Type());
				codeSqlError = InsertIso8583(iso);
				log.WriteLogMonitor("<< INSERT Response Trx: Iso011: " + iso.getISO_011_SysAuditNumber() + " " 
								    +  descriptionSqlError + " >>\n", TypeMonitor.monitor, null);
			}
		};
		return runnable;
    }
    
    public Callable<Integer> callInsertMassiveTransferFinancoop(Iso8583 iso){
    	
    	Callable<Integer> callable = new Callable<Integer>() {
			
			@Override
			public Integer call() throws Exception {
				
				return InsertMassiveTransfer(iso);
			}
		};
		return callable;
    }

    public Callable<Integer> callTest(Iso8583 iso){
    	
    	Callable<Integer> callable = new Callable<Integer>() {
			
			@Override
			public Integer call() throws Exception {
				
				try {
					
					Thread.sleep(1500);
					return 0;
					
				} catch (Exception e) {
					
					return -1;
				}
			}
		};
		return callable;
    }
    
    public int callTestSinHilos(Iso8583 iso) {
    	
    	try {
			
			Thread.sleep(1500);
			return 0;
			
		} catch (Exception e) {
			
			return -1;
		}
    }
}

