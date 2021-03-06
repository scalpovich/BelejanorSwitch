package com.belejanor.switcher.sqlservices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.otp.OTP;
import com.belejanor.switcher.snp.spi.SnpSPIOrdenante;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;
import oracle.jdbc.OracleTypes;

public class IsoRetrievalTransaction implements Callable<wIso8583>{
	
	private Logger log;
	private wIso8583 iso;
	
	public IsoRetrievalTransaction(){
		this.log = new Logger();
	}
	
	public IsoRetrievalTransaction(wIso8583 iso){
		this();
		this.iso = iso;
	}

	public wIso8583 RetrieveTransactionIso(wIso8583 isoRequest, int option) {
		
		wIso8583 iso = null;
		PreparedStatement cs = null ;
		String procedureName = StringUtils.Empty();
		Connection conn = null;
		
		try {			
			
			conn = DBCPDataSource.getConnection();
			switch (option) {
			
			case 0:
				
				//Calculo del mismo valor fecha + secuencial en ISO044
				if (isoRequest.getISO_011_SysAuditNumber().length() > 11) {
					
					isoRequest.setISO_044_AddRespData(FormatUtils.DateToString(isoRequest.getISO_012_LocalDatetime(), 
							   "yyMMddHHmmss") + "_" + isoRequest.getISO_011_SysAuditNumber());
				}else {
					
					isoRequest.setISO_044_AddRespData(FormatUtils.DateToString(isoRequest.getISO_012_LocalDatetime(), 
							   "yyyyMMddHHmmss") + "_" + isoRequest.getISO_011_SysAuditNumber());
				}
				return isoRequest;
				
			case 1:
				
				procedureName = "SW_RETRIEVETRANSACTION";
				String stmtString = "exec " + procedureName + " @i_wISO_000_Mess_Type=?, @i_wISO_011_SysAuditNro =?, @i_wISO_012_LocDt_dec =?, "
		        		+ "@i_wISO_003_ProcCode =?, @i_wISO_102_Acc_1=?, @i_wISO_024_NetId=?, @i_wISO_018_MerchType=?";
				cs = conn.prepareCall(stmtString);
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_011_SysAuditNumber());
				cs.setLong(3, Long.parseLong(FormatUtils.
						   DateToString(isoRequest.getISO_012_LocalDatetime(), "yyyyMMddHHmmss")));
				cs.setString(4, isoRequest.getISO_003_ProcessingCode());
				cs.setString(5, isoRequest.getISO_102_AccountID_1());
				cs.setString(6, isoRequest.getISO_024_NetworkId());
				cs.setString(7, isoRequest.getISO_018_MerchantType());
				
				break;
				
			case 2:
				
				cs = conn.prepareCall("{ CALL SW_RetrieveTrxElectronicCash(?,?,?,?,?)}");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_011_SysAuditNumber());
				cs.setString(3, isoRequest.getISO_024_NetworkId());
				cs.setString(4, isoRequest.getISO_018_MerchantType());
				
				break;
				
			case 3:
				
				cs = conn.prepareCall("{ CALL SW_RetrieveTrxAlexSoft(?,?,?,?,?,?,?,?)}");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_037_RetrievalReferenceNumber());
				cs.setString(3, isoRequest.getISO_024_NetworkId());
				cs.setString(4, isoRequest.getISO_018_MerchantType());
				cs.setDouble(5, isoRequest.getISO_004_AmountTransaction());
				cs.setString(6, isoRequest.getISO_102_AccountID_1());
				cs.setString(7, isoRequest.getISO_103_AccountID_2());
				
				break;
				
			case 4:
				
				cs = conn.prepareCall("{ CALL SW_RetrieveTrxBCE_VC(?,?,?,?,?,?)}");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_044_AddRespData());

				cs.setString(3, isoRequest.getISO_024_NetworkId());
				cs.setString(4, isoRequest.getISO_018_MerchantType());
				cs.setDouble(5, isoRequest.getISO_004_AmountTransaction());
				
				
				break;
			
			case 5:
				
				//Descomposicion del campo /090 Original Data Elements
				if(isoRequest.getISO_090_OriginalData().length() == 42){
					
					try {
						
						String mtiOriginal = isoRequest.getISO_090_OriginalData().substring(0,4);
						String mtiIso1 = StringUtils.Empty();
						
						switch (mtiOriginal) {
						case "0220":
						case "0200":
							mtiIso1 = "1200";
							break;	
						default:
							break;
						}
						
						String secuencial = String.valueOf(Integer.parseInt(isoRequest.
								getISO_090_OriginalData().substring(4,16)));
						String date13 = isoRequest.getISO_090_OriginalData().substring(16,20);
						String time12 = isoRequest.getISO_090_OriginalData().substring(20,28)
								        .substring(0,6);
						
						String fecha =  (FormatUtils.DateToString(new Date(), "yyyy")  + 
				                   		date13 + time12);
						
						cs = conn.prepareCall("{ CALL SW_RetrieveTrxCardExternals(?,?,?,?,?,?,?,?,?)}");
						cs.setString(1, mtiIso1);
						cs.setDouble(2, isoRequest.getISO_004_AmountTransaction());
						cs.setString(3, secuencial);
						cs.setString(4, fecha);
						cs.setString(5, isoRequest.getISO_003_ProcessingCode());
						cs.setString(6, isoRequest.getISO_102_AccountID_1());
						cs.setString(7, isoRequest.getISO_024_NetworkId());
						cs.setString(8, isoRequest.getISO_018_MerchantType());
						
					} catch (Exception e) {
						
						iso = new wIso8583();
						iso.setISO_039_ResponseCode("904");
						iso.setISO_039p_ResponseDetail("CAMPO ISO_090 INVALIDO");
						return iso;
					}	
					
				}else{
					
					iso = new wIso8583();
					iso.setISO_039_ResponseCode("905");
					iso.setISO_039p_ResponseDetail("CAMPO ISO_090 {ORIGINAL DATA ELEMENTS} TIENE LONGITUD ERRONEA");
					return iso;
				}
				break;
				
			case 6:
				
				cs = conn.prepareCall("{ CALL SW_RETRIEVETRXCREDENCIAL(?,?,?,?,?,?)}");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_121_ExtendedData());
				cs.setString(3, isoRequest.getISO_038_AutorizationNumber());
				cs.setString(4, isoRequest.getISO_024_NetworkId());
				cs.setString(5, isoRequest.getISO_018_MerchantType());
				
				break;
				
			case 7:
				
				String fecha = FormatUtils.DateToString(isoRequest.getISO_012_LocalDatetime(), "yyyyMMddHHmmss"); 
				cs = conn.prepareCall("{ CALL SW_RETRIEVETRANSACTIONATMS(?,?,?,?,?,?,?,?)	}");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_037_RetrievalReferenceNumber().trim());
				cs.setString(3, fecha);
				cs.setString(4, isoRequest.getISO_003_ProcessingCode());
				cs.setString(5, isoRequest.getISO_102_AccountID_1());
				cs.setString(6, isoRequest.getISO_024_NetworkId());
				cs.setString(7, isoRequest.getISO_018_MerchantType());
				
				break;

				//CODIGO EFI RECEP
			case 8:
				
				String fechaDec = FormatUtils.DateToString(isoRequest.getISO_012_LocalDatetime(), "yyyyMMddHHmmss"); 
				cs = conn.prepareCall("{ CALL SW_RETRIEVETRANSACTION_BIMO(?,?,?,?,?,?,?,?,?) }");
				cs.setString(1, isoRequest.getISO_000_Message_Type().replace("14", "12"));
				cs.setString(2, isoRequest.getISO_037_RetrievalReferenceNumber()); //MsgId
				cs.setString(3, fechaDec);
				cs.setString(4, isoRequest.getISO_003_ProcessingCode());
				cs.setString(5, isoRequest.getISO_102_AccountID_1());
				cs.setString(6, isoRequest.getISO_024_NetworkId());
				cs.setString(7, isoRequest.getISO_018_MerchantType());
				cs.setString(8, isoRequest.getISO_022_PosEntryMode()); //TrxEndToEnd
				cs.setString(9, isoRequest.getISO_023_CardSeq());// Celular
				
				break;
			case 9:
				
				isoRequest.setISO_044_AddRespData(isoRequest.getISO_044_AddRespData());
				return isoRequest;
				
			case 10:
				
				String fechaDeci = FormatUtils.DateToString(isoRequest.getISO_012_LocalDatetime(), "yyyyMMddHHmmss"); 
				cs = conn.prepareCall("{ CALL SW_RETRIEVE_SNP_SPI_ORD(?,?,?,?,?,?,?,?)	}");
				cs.setString(1, isoRequest.getISO_003_ProcessingCode());
				cs.setString(2, fechaDeci);
				cs.setString(3, isoRequest.getISO_011_SysAuditNumber());
				cs.setDouble(4, isoRequest.getISO_004_AmountTransaction());
				cs.setString(5, isoRequest.getISO_102_AccountID_1());
				cs.setString(6, isoRequest.getISO_024_NetworkId());
				cs.setString(7, isoRequest.getISO_018_MerchantType());
				
				break;

			default:
			
				break;
			}
		
			ResultSet rs = cs.executeQuery();
			int count = 0;
			
			if(rs != null){
			
				iso = new wIso8583();
				
				while (rs.next()) {
					count++;
					if(count > 1)
						break;
					iso.setISO_000_Message_Type(rs.getString("wISO_000_Message_Type"));
					iso.setISO_BitMap(rs.getString("wISO_BITMAP"));
					iso.setISO_002_PAN(rs.getString("wISO_002_PAN"));
					iso.setISO_003_ProcessingCode(rs.getString("wISO_003_ProcessingCode"));
					iso.setISO_004_AmountTransaction(rs.getDouble("wISO_004_AmountTransaction"));
					iso.setISO_006_BillAmount(rs.getDouble("wISO_006_BillAmount"));
					iso.setISO_007_TransDatetime(rs.getDate("wISO_007_TransDatetime"));
					iso.setISO_008_BillFeeAmount(rs.getDouble("wISO_008_BillFeeAmount"));
					iso.setISO_011_SysAuditNumber(rs.getString("wISO_011_SysAuditNumber"));
					iso.setISO_012_LocalDatetime(rs.getDate("wISO_012_LocalDatetime"));
					iso.setISO_013_LocalDate(rs.getDate("wISO_013_LocalDate"));
					iso.setISO_015_SettlementDatel(rs.getDate("wISO_015_SettlementDatel"));
					iso.setISO_018_MerchantType(rs.getString("wISO_018_MerchantType"));
					iso.setISO_019_AcqCountryCode(rs.getString("wISO_019_AcqCountryCode"));
					iso.setISO_022_PosEntryMode(rs.getString("wISO_022_PosEntryMode"));
					iso.setISO_023_CardSeq(rs.getString("wISO_023_CardSeq"));
					iso.setISO_024_NetworkId(rs.getString("wISO_024_NetworkId"));
					iso.setISO_028_TranFeeAmount(rs.getDouble("wISO_028_TranFeeAmount"));
					iso.setISO_029_SettlementFee(rs.getDouble("wISO_029_SettlementFee"));
					iso.setISO_030_ProcFee(rs.getDouble("wISO_030_ProcFee"));
					iso.setISO_032_ACQInsID(rs.getString("wISO_032_ACQInsID"));
					iso.setISO_033_FWDInsID(rs.getString("wISO_033_FWDInsID"));
					iso.setISO_034_PANExt(rs.getString("wISO_034_PANExt"));
					iso.setISO_035_Track2(rs.getString("wISO_035_Track2"));
					iso.setISO_036_Track3(rs.getString("wISO_036_Track3"));
					iso.setISO_037_RetrievalReferenceNumber(rs.getString("wISO_037_RetrievalReferenceNro"));
					iso.setISO_038_AutorizationNumber(rs.getString("wISO_038_AutorizationNumber"));
					iso.setISO_039_ResponseCode(rs.getString("wISO_039_ResponseCode"));
					iso.setISO_039p_ResponseDetail(rs.getString("wISO_039p_ResponseDetail"));
					iso.setISO_041_CardAcceptorID(rs.getString("wISO_041_CardAcceptorID"));
					iso.setISO_042_Card_Acc_ID_Code(rs.getString("wISO_042_Card_Acc_ID_Code"));
					iso.setISO_043_CardAcceptorLoc(rs.getString("wISO_043_CardAcceptorLoc"));
					iso.setISO_044_AddRespData(rs.getString("wISO_044_AddRespData"));
					iso.setISO_049_TranCurrCode(rs.getDouble("wISO_049_TranCurrCode"));
					iso.setISO_051_CardCurrCode(rs.getDouble("wISO_051_CardCurrCode"));
					iso.setISO_052_PinBlock(rs.getString("wISO_052_PinBlock"));
					iso.setISO_054_AditionalAmounts(rs.getString("wISO_054_AditionalAmounts"));
					iso.setISO_055_EMV(rs.getString("wISO_055_EMV"));
					iso.setISO_090_OriginalData(rs.getString("wISO_090_OriginalData"));
					iso.setISO_102_AccountID_1(rs.getString("wISO_102_AccountID_1"));
					iso.setISO_103_AccountID_2(rs.getString("wISO_103_AccountID_2"));
					iso.setISO_104_TranDescription(rs.getString("wISO_104_TranDescription"));
					iso.setISO_120_ExtendedData(rs.getString("wISO_120_ExtendedData"));
					iso.setISO_121_ExtendedData(rs.getString("wISO_121_ExtendedData"));
					iso.setISO_123_ExtendedData(rs.getString("wISO_123_ExtendedData"));
					iso.setISO_124_ExtendedData(rs.getString("wISO_124_ExtendedData"));
					iso.setWsIso_LogStatus(rs.getInt("wsISO_LogStatus"));
					iso.setWsISO_TranDatetime(rs.getDate("wsISO_TranDatetime"));
					iso.setWsISO_TranDatetimeResponse(rs.getDate("wsISO_TranDatetimeResponse"));
					iso.setWsISO_SFRetryCounts(rs.getInt("wsISO_SFRetryCounts"));
					iso.setWsISO_FlagStoreForward(Boolean.parseBoolean(rs.getString("wsISO_FlagStoreFprward")));
					iso.setwISO_012_LocalDatetime_decimal(rs.getDouble("wISO_012_LocalDatetime_decimal"));
					
					iso.setISO_039_ResponseCode("000");
					
					/*SOLO PARA FIT1 RECUPERAR EL MSGID*/
					Config cfg = new Config();
					cfg = cfg.getConfigSystem("FIT_VERSION");
					if(cfg != null){
						if(cfg.getCfg_Valor().equals("1")){
							if(!iso.getISO_044_AddRespData().contains("^"))
								iso = getInfoReversoFit1(iso);
						}
					}
					
				}
				if(count > 1){
					
					iso = new wIso8583();
					iso.setISO_039_ResponseCode("602");
					iso.setISO_039p_ResponseDetail("TRANSACCION ORIGINAL INCONSISTENTE ROWS: " + count);
				}else if (count == 0) {
					
					iso = new wIso8583();
					iso.setISO_039_ResponseCode("601");
					iso.setISO_039p_ResponseDetail("TRANSACCION A REVERSAR NO EXISTE");
				}	
				
			}else{
				
				iso = new wIso8583();
				iso.setISO_039_ResponseCode("601");
				iso.setISO_039p_ResponseDetail("TRANSACCION A REVERSAR NO EXISTE");
			}
		    
		} catch (SQLException ex) {
				
			iso = new wIso8583();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", ex, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveTransactionIso (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso = new wIso8583();
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveTransactionIso ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(cs!=null)
					cs.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {e.printStackTrace();}
		}
		
		
		return iso;
	}
	
     public wIso8583 MantenainceCommerceSQL(wIso8583 isoRequest) {
		
		CallableStatement cs = null ;
		StringBuilder str = new StringBuilder();
		str.append(isoRequest.getISO_BitMap() + "R|" + isoRequest.getISO_011_SysAuditNumber() + "|");
		try {			
			
			Connection cn = MemoryGlobal.conn;		
			cs = cn.prepareCall("{ CALL SW_SP_MANTENAINCE_COMMERCE(?,?,?,?,?,?,?,?,?,?,?)}");
			cs.setString("iPrefix", isoRequest.getISO_BitMap());
			cs.setString("iCodComercio", isoRequest.getISO_002_PAN());
			cs.setString("iNomComercio", isoRequest.getISO_034_PANExt());
			cs.setString("iDireccionComercio", isoRequest.getISO_120_ExtendedData());
			cs.setString("iNumDocumentoComercio", isoRequest.getISO_121_ExtendedData());
			cs.setString("iNomSocioComercio", isoRequest.getISO_122_ExtendedData());
			cs.setString("iNumCuentaComercio", isoRequest.getISO_102_AccountID_1());
			cs.setString("iObsComercio", isoRequest.getISO_114_ExtendedData());
			cs.setString("iEstadoComercio", isoRequest.getISO_123_ExtendedData());
			cs.setString("iCpersona", isoRequest.getISO_023_CardSeq());
			cs.registerOutParameter("p_recordsetInit", OracleTypes.CURSOR);
			cs.execute();
			ResultSet rs = (ResultSet)cs.getObject("p_recordsetInit");
			int count = 0;
			
			if(rs != null){
				
				while (rs.next()) {
					count++;
					if(count > 1)
						str.append("^");
					
					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
					if(count == 1)
						str.append(isoRequest.getISO_039_ResponseCode() + "|" + isoRequest.getISO_039p_ResponseDetail() + "|");
					isoRequest.setWsIso_LogStatus(2);
					isoRequest.setWsISO_TranDatetimeResponse(new Date());
					if(rs.getString("COD").equals("000") 
							&& isoRequest.getISO_BitMap().startsWith("PAG_C")){
						
						isoRequest.setISO_002_PAN(rs.getString("COD_COMERCIO"));
						isoRequest.setISO_034_PANExt(rs.getString("NOM_COMERCIO"));
						isoRequest.setISO_120_ExtendedData(rs.getString("DIRECCION_COMERCIO"));
						isoRequest.setISO_121_ExtendedData(rs.getString("NUMDOC_ASOC_COMERCIO"));
						isoRequest.setISO_122_ExtendedData(rs.getString("NOMSOCIO_ASOC_COMERCIO"));
						isoRequest.setISO_102_AccountID_1(rs.getString("NUMCTA_ASOC_COMERCIO"));
						isoRequest.setISO_114_ExtendedData(rs.getString("OBS_COMERCIO"));
						isoRequest.setISO_123_ExtendedData(rs.getString("ESTADO_COMERCIO"));
						isoRequest.setISO_023_CardSeq(rs.getString("CPERSONA"));
						
						str.append(isoRequest.getISO_002_PAN() + "|" + isoRequest.getISO_034_PANExt() + "|" +
								   isoRequest.getISO_120_ExtendedData() + "|" + isoRequest.getISO_121_ExtendedData() +  "|" +
								   isoRequest.getISO_122_ExtendedData() + "|" + isoRequest.getISO_102_AccountID_1() + "|" +
								   isoRequest.getISO_114_ExtendedData() + "|" + isoRequest.getISO_123_ExtendedData() + "|" +
								   isoRequest.getISO_023_CardSeq() + "|END");
					}
				}
				if (count == 0) {
					
					isoRequest = new wIso8583();
					isoRequest.setISO_039_ResponseCode("908");
					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
				}	
				
			}else{
				
				isoRequest = new wIso8583();
				isoRequest.setISO_039_ResponseCode("909");
				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
			}
		    
		} catch (SQLException ex) {
			
			isoRequest.setISO_039_ResponseCode("909");
			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", ex, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			isoRequest.setISO_039_ResponseCode("908");
			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL ", TypeMonitor.error, e);
			
		}finally {
			
			isoRequest.setISO_115_ExtendedData(str.toString());
			
			try {
				if(cs!=null)
					cs.close();
			} catch (SQLException e) {e.printStackTrace();}
		}
		
		return isoRequest;
	}
     
     public wIso8583 MantenainceBIMOWalletSQL(wIso8583 isoRequest) {
 		
 		CallableStatement cs = null ;
 		String build = StringUtils.Empty();
 		try {			
 			
 			Connection cn = MemoryGlobal.conn;		
 			cs = cn.prepareCall("{ CALL SW_SP_MANTENAINCE_BIMO(?,?,?,?,?,?,?,?,?)}");
 			cs.setString("iCommand", isoRequest.getISO_BitMap());
 			cs.setString("iDocumentId", isoRequest.getISO_002_PAN());
 			cs.setString("iType_Id", isoRequest.getISO_022_PosEntryMode());
 			cs.setString("iCpersona", isoRequest.getISO_019_AcqCountryCode());
 			cs.setString("iApellidosNombres", isoRequest.getISO_034_PANExt());
 			cs.setString("iMovilNumber", isoRequest.getISO_023_CardSeq());
 			cs.setString("iAccountNumber", isoRequest.getISO_102_AccountID_1());
 			cs.setInt("iStatus", Integer.parseInt(isoRequest.getISO_114_ExtendedData()));
 			cs.registerOutParameter("p_recordsetInit", OracleTypes.CURSOR);
 			cs.execute();
 			
 			ResultSet rs = (ResultSet)cs.getObject("p_recordsetInit");
 			int count = 0;
 			
 			if(rs != null){
 				
 				while (rs.next()) {
 					count++;
 					if(count == 1){
 					
	 					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
	 					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
	 					isoRequest.setWsIso_LogStatus(2);
	 					isoRequest.setWsISO_TranDatetimeResponse(new Date());
 					}
 					
 					if(rs.getString("COD").equals("000") 
 							&& isoRequest.getISO_BitMap().startsWith("C")){
 						
 						isoRequest.setISO_002_PAN(rs.getString("DOCUMENT_ID"));
 						isoRequest.setISO_022_PosEntryMode(rs.getString("TYPE_ID"));
 						isoRequest.setISO_023_CardSeq(rs.getString("MOVIL_NUMBER"));
 						isoRequest.setISO_034_PANExt(rs.getString("APELLIDOS_NOMBRES"));
 						isoRequest.setISO_035_Track2(rs.getString("CPERSONA"));
 						isoRequest.setISO_102_AccountID_1(rs.getString("ACCOUNT_NUMBER"));
 						isoRequest.setISO_120_ExtendedData(rs.getString("STATUS"));
 						isoRequest.setISO_121_ExtendedData(rs.getString("FDESDE"));
 						
 						build = rs.getString("DOCUMENT_ID") + "|" + rs.getString("TYPE_ID") + "|" +
 								rs.getString("MOVIL_NUMBER") + "|" + rs.getString("APELLIDOS_NOMBRES") + "|" +
 								rs.getString("CPERSONA") + "|" +  rs.getString("ACCOUNT_NUMBER") + "|" +
 								rs.getString("STATUS") + "|" + rs.getString("FDESDE");
 						
 						isoRequest.setISO_044_AddRespData(build);
 					}
 				}
 				if (count == 0) {
 					
 					isoRequest = new wIso8583();
 					isoRequest.setISO_039_ResponseCode("908");
 					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
 				}	
 				
 			}else{
 				
 				isoRequest = new wIso8583();
 				isoRequest.setISO_039_ResponseCode("909");
 				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
 			}
 		    
 		} catch (SQLException ex) {
 			
 			isoRequest.setISO_039_ResponseCode("909");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE BIMO_PERSON ERROR ", ex, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
 			
 		} catch (Exception e) {			
 			
 			isoRequest.setISO_039_ResponseCode("908");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE BIMO_PERSON ERROR ", e, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceBIMOWalletSQL ", TypeMonitor.error, e);
 			
 		}finally {
 			
 			try {
 				if(cs!=null)
 					cs.close();
 			} catch (SQLException e) {e.printStackTrace();}
 		}
 		
 		return isoRequest;
 	}
     
     public wIso8583 MantenainceBIMOWalletSQL(wIso8583 isoRequest, typeBDD type) {
  		
    	Connection conn = null;
 		PreparedStatement cs = null;
 		String build = StringUtils.Empty();
 		
  		try {			
  			
  			conn = DBCPDataSource.getConnection();	
  			String procname = Arrays.asList(isoRequest.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0);
			String stmtString = "exec " + procname + " @iCommand=?, @iDocumentId=?, @iType_Id=?, "
	        		                               + " @iCpersona=?, @iApellidosNombres=?, @iMovilNumber=?,"
	        		                               + " @iAccountNumber=?, @iStatus=?";
			
			cs = conn.prepareStatement(stmtString);
			
  			cs.setString(1, isoRequest.getISO_BitMap());
  			cs.setString(2, isoRequest.getISO_002_PAN());
  			cs.setString(3, isoRequest.getISO_022_PosEntryMode());
  			cs.setString(4, isoRequest.getISO_019_AcqCountryCode());
  			cs.setString(5, isoRequest.getISO_034_PANExt());
  			cs.setString(6, isoRequest.getISO_023_CardSeq());
  			cs.setString(7, isoRequest.getISO_102_AccountID_1());
  			cs.setInt(8, Integer.parseInt(isoRequest.getISO_114_ExtendedData()));
  			
  			cs.setQueryTimeout(isoRequest.getWsTransactionConfig().getProccodeTimeOutValue());
  			ResultSet rs = cs.executeQuery();
  			
  			isoRequest.setISO_039_ResponseCode(null);
  			int count = 0;
  			
  			if(rs != null){
  				
  				while (rs.next()) {
  					count++;
  					if(count == 1){
  					
 	 					isoRequest.setISO_039_ResponseCode(rs.getString(1));
 	 					isoRequest.setISO_039p_ResponseDetail(rs.getString(2));
 	 					isoRequest.setWsIso_LogStatus(2);
 	 					isoRequest.setWsISO_TranDatetimeResponse(new Date());
  					}
  					
  					if(rs.getString(1).equals("000") 
  							&& isoRequest.getISO_BitMap().startsWith("C")){
  						
  						isoRequest.setISO_002_PAN(rs.getString("DOCUMENT_ID"));
  						isoRequest.setISO_022_PosEntryMode(rs.getString("TYPE_ID"));
  						isoRequest.setISO_023_CardSeq(rs.getString("MOVIL_NUMBER"));
  						isoRequest.setISO_034_PANExt(rs.getString("APELLIDOS_NOMBRES"));
  						isoRequest.setISO_035_Track2(rs.getString("CPERSONA"));
  						isoRequest.setISO_102_AccountID_1(rs.getString("ACCOUNT_NUMBER"));
  						isoRequest.setISO_120_ExtendedData(rs.getString("STATUS"));
  						isoRequest.setISO_121_ExtendedData(rs.getString("FDESDE"));
  						
  						build = rs.getString("DOCUMENT_ID") + "|" + rs.getString("TYPE_ID") + "|" +
  								rs.getString("MOVIL_NUMBER") + "|" + rs.getString("APELLIDOS_NOMBRES") + "|" +
  								rs.getString("CPERSONA") + "|" +  rs.getString("ACCOUNT_NUMBER") + "|" +
  								rs.getString("STATUS") + "|" + rs.getString("FDESDE");
  						
  						isoRequest.setISO_044_AddRespData(build);
  					}
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE BIMO_PERSON ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE BIMO_PERSON ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceBIMOWalletSQL ", TypeMonitor.error, e);
  			
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
  		
  		return isoRequest;
  	} 
     
     public wIso8583 Query_Savings_Account_SQL(wIso8583 isoRequest) {
 		
 		CallableStatement cs = null ;
 		try {			
 			
 			Connection cn = MemoryGlobal.conn;		
 			cs = cn.prepareCall("{ CALL SW_QUERY_SAVINGS_ACCOUNT(?,?)}");
 			cs.setString("i_wISO_102_Acc_1", isoRequest.getISO_102_AccountID_1());
 			cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
 			cs.setQueryTimeout(10);
 			cs.execute();
 			ResultSet rs = (ResultSet)cs.getObject("p_iso_resulset");
 			int count = 0;
 			
 			if(rs != null){
 				
 				while (rs.next()) {
 					count++;
 					if(count >= 1){
 					
 						isoRequest.setISO_102_AccountID_1(rs.getString("NUMERO_CUENTA"));
 	 					isoRequest.setISO_120_ExtendedData(rs.getString("SALDO_CONTABLE"));
 	 					isoRequest.setISO_121_ExtendedData(rs.getString("SALDO_DISPONIBLE_CUENTA"));
 	 					isoRequest.setISO_122_ExtendedData(rs.getString("NOMBRE_OFICIAL_CUENTA"));
 	 					isoRequest.setWsIso_LogStatus(2);
 	 					isoRequest.setWsISO_TranDatetimeResponse(new Date());
 	 					isoRequest.setISO_039_ResponseCode("000");
 	 					isoRequest.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
 					}
 					
 				}
 				if (count == 0) {
 					
 					isoRequest = new wIso8583();
 					isoRequest.setISO_039_ResponseCode("214");
 					isoRequest.setISO_039p_ResponseDetail("LA CUENTA NO EXISTE, O NO SE ENCUENTRA ACTIVA");
 				}	
 				
 			}else{
 				
 				isoRequest = new wIso8583();
 				isoRequest.setISO_039_ResponseCode("909");
 				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
 			}
 		    
 		} catch (SQLException ex) {
 			
 			isoRequest.setISO_039_ResponseCode("909");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", ex, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
 			
 		} catch (Exception e) {			
 			
 			isoRequest.setISO_039_ResponseCode("908");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL ", TypeMonitor.error, e);
 			
 		}finally {
 			
 			try {
 				if(cs!=null)
 					cs.close();
 			} catch (SQLException e) {e.printStackTrace();}
 		}
 		
 		return isoRequest;
 	}
	
     public wIso8583 GetDataEquifax(wIso8583 isoRequest) {
 		
 		CallableStatement cs = null ;
 		
 		try {			
 			
 			Connection cn = MemoryGlobal.conn;		
 			cs = cn.prepareCall("{CALL SW_INFO_EQUIFAX(?,?)}");
 			cs.setString("i_Documento", isoRequest.getISO_002_PAN());
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
 					isoRequest.setISO_114_ExtendedData(StringUtils.Empty());
 					
 					if(rs.getString("COD").equals("000")){
 						
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("IDENTIFICACION_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("CPERSONA_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("TIPO_DOCUMENTO_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("IDENTIFICACION_CONYUGE") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("TIPO_IDENTIFICACION_CONYUGE") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("CPERSONA_CONYUGE") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("INGRESOS_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("INGRESOS_CONYUGE") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("EGRESOS_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("EGRESOS_CONYUGE") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("DIRECCION_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("TIPO_DIRECCION_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("CIUDAD_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("PARROQUIA_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("FECHA_MODIFICACION") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("TELEFONO_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("TIPO_TELEFONO") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("VALORES_INTERNOS") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("FECNAC_PRINCIPAL") + "|");
 						isoRequest.setISO_114_ExtendedData(isoRequest.getISO_114_ExtendedData() + rs.getString("FECNAN_CONYUGUE"));
 					}
 				}
 				if (count == 0) {
 					
 					isoRequest = new wIso8583();
 					isoRequest.setISO_039_ResponseCode("908");
 					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
 				}	
 				
 			}else{
 				
 				isoRequest = new wIso8583();
 				isoRequest.setISO_039_ResponseCode("909");
 				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
 			}
 		    
 		} catch (SQLException ex) {
 			
 			isoRequest.setISO_039_ResponseCode("909");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE DATA EQUIFAX ERROR ", ex, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::GetDataEquifax (SQLException) ", TypeMonitor.error, ex);
 			
 		} catch (Exception e) {			
 			
 			isoRequest.setISO_039_ResponseCode("908");
 			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE DATA EQUIFAX ERROR ", e, false));
 			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::GetDataEquifax ", TypeMonitor.error, e);
 			
 		}finally {
 			
 			
 			try {
 				if(cs!=null)
 					cs.close();
 			} catch (SQLException e) {e.printStackTrace();}
 		}
 		
 		return isoRequest;
 	}
     
     public wIso8583 RetrieveCommerceSQL(wIso8583 isoRequest) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_RETRIEVE_COMMERCE(?,?)}");
  			cs.setString("i_CommerceCod", isoRequest.getISO_120_ExtendedData());
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
  					if(rs.getString("COD").equals("000")){
  						
  						isoRequest.setISO_103_AccountID_2(rs.getString("NUMCTA_ASOC_COMERCIO"));
  						isoRequest.setISO_023_CardSeq(rs.getString("CPERSONA"));
  						isoRequest.setISO_022_PosEntryMode(isoRequest.getISO_022_PosEntryMode() + rs.getString("NOM_COMERCIO") + ")");
  					}
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveCommerceSQL ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  		return isoRequest;
  	}
     
     public wIso8583 RetrieveSavingsAccountFit1(wIso8583 isoRequest) {
   		
   		CallableStatement cs = null ;
   		
   		try {			
   			
   			Connection cn = MemoryGlobal.conn;		
   			cs = cn.prepareCall("{ CALL SW_RETRIEVE_SAVACCOUNT_FIT1(?,?)}");
   			cs.setString("i_cCuenta", isoRequest.getISO_102_AccountID_1());
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
   					if(rs.getString("COD").equals("000")){
   						
   						isoRequest.setISO_102_AccountID_1(rs.getString("NUMERO_CUENTA"));
   						isoRequest.setISO_120_ExtendedData(rs.getString("SALDO_CONTABLE"));
   						isoRequest.setISO_121_ExtendedData(rs.getString("SALDO_DISPONIBLE_CUENTA"));
   						isoRequest.setISO_122_ExtendedData(rs.getString("SALDO_BLOQUEADO"));
   					}
   				}
   				if (count == 0) {
   					
   					isoRequest = new wIso8583();
   					isoRequest.setISO_039_ResponseCode("100");
   					isoRequest.setISO_039p_ResponseDetail("EL NUMERO DE CUENTA " + isoRequest.getISO_102_AccountID_1() + " NO EXISTE");
   				}	
   				
   			}else{
   				
   				isoRequest = new wIso8583();
   				isoRequest.setISO_039_ResponseCode("909");
   				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
   			}
   		    
   		} catch (SQLException ex) {
   			
   			isoRequest.setISO_039_ResponseCode("909");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RetrieveSavingsAccountFit1 ".toUpperCase(), ex, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveSavingsAccountFit1 (SQLException) ", TypeMonitor.error, ex);
   			
   		} catch (Exception e) {			
   			
   			isoRequest.setISO_039_ResponseCode("908");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveSavingsAccountFit1 ", TypeMonitor.error, e);
   			
   		}finally {
   			
   			
   			try {
   				if(cs!=null)
   					cs.close();
   			} catch (SQLException e) {e.printStackTrace();}
   		}
   		
   		return isoRequest;
   	}
     
     public wIso8583 RetrieveAdminOTP(OTP otp, wIso8583 isoRequest) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_ADMIN_OTP(?,?,?,?,?,?,?,?)}");
  			cs.setString("i_ProcCode", isoRequest.getISO_003_ProcessingCode());
  			cs.setString("i_ParamOtp", otp.getParam());
  			cs.setTimestamp("i_FecGeneracion", new Timestamp(otp.getFechaGeneracion().getTime()));
  			cs.setTimestamp("i_FechaCaducidad", new Timestamp(otp.getFechaExpiracion().getTime()));
  			cs.setString("i_OtpCode", otp.getOtpCode());
  			cs.setString("i_CanalCode", isoRequest.getISO_018_MerchantType());
  			cs.setString("i_PinBlock", otp.getPinBlock());
  			
  			
  			cs.registerOutParameter("p_recordsetInit", OracleTypes.CURSOR);
  			cs.execute();
  			ResultSet rs = (ResultSet)cs.getObject("p_recordsetInit");
  			int count = 0;
  			
  			if(rs != null){
  				
  				while (rs.next()) {
  					count++;
  					if(count > 1)
  						break;
  					
  					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
  					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
  					
  					if(isoRequest.getISO_003_ProcessingCode().equals("900002")) {
  						
  						isoRequest.setISO_120_ExtendedData(rs.getString("OTPS"));
  						isoRequest.setISO_121_ExtendedData(rs.getString("FGEN"));
  						isoRequest.setISO_122_ExtendedData(rs.getString("FCAD"));
  						isoRequest.setISO_123_ExtendedData(rs.getString("PINBLOCK"));
  						isoRequest.setISO_023_CardSeq(rs.getString("REINTENTOS"));
  						isoRequest.setISO_035_Track2(rs.getString("PERMITIDOS"));
  					}
  					
  					isoRequest.setWsIso_LogStatus(2);
  					isoRequest.setWsISO_TranDatetimeResponse(new Date());
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS LA BDD NO HA RETORNADO NINGUN REGISTRO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD OTPs");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE OTP ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveAdminOTP (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE OTP ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveAdminOTP ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  		return isoRequest;
  	}
    
     public wIso8583 RetrieveAdminIVR(wIso8583 isoRequest) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			int notificacionesDesafio = -1;
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_ADMIN_CANAL_IVR(?,?,?,?,?,?,?,?)}");
  			cs.setString("i_ProcCode", isoRequest.getISO_003_ProcessingCode());
  			cs.setString("i_NroIdentificacion", isoRequest.getISO_002_PAN());
  			cs.setString("i_Nombres", isoRequest.getISO_034_PANExt());
  			cs.setString("i_Estado", isoRequest.getISO_036_Track3());
  			cs.setString("i_PinBlock", isoRequest.getISO_052_PinBlock());
  			cs.setString("i_PinBlockNew", isoRequest.getISO_090_OriginalData());
  			if(!StringUtils.IsNullOrEmpty(isoRequest.getISO_120_ExtendedData())) {
  				if(NumbersUtils.isNumeric(isoRequest.getISO_120_ExtendedData()))
  					notificacionesDesafio = Integer.parseInt(isoRequest.getISO_120_ExtendedData());
  			}
  			cs.setInt("i_NotificacionDesafio", notificacionesDesafio);
  			
  			cs.registerOutParameter("p_recordsetInit", OracleTypes.CURSOR);
  			cs.execute();
  			ResultSet rs = (ResultSet)cs.getObject("p_recordsetInit");
  			int count = 0;
  			
  			if(rs != null){
  				
  				while (rs.next()) {
  					count++;
  					if(count > 1)
  						break;
  					
  					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
  					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
  					
  					if(isoRequest.getISO_003_ProcessingCode().equals("540001")) {
  						
  						isoRequest.setISO_120_ExtendedData(rs.getString("ESTADO"));
  						isoRequest.setISO_121_ExtendedData(rs.getString("FLAG"));
  						
  					}
  					
  					isoRequest.setWsIso_LogStatus(2);
  					isoRequest.setWsISO_TranDatetimeResponse(new Date());
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS LA BDD NO HA RETORNADO NINGUN REGISTRO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD IVR_SWITCH");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE IVR_SWITCH ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveAdminIVR (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE IVR_SWITCH ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveAdminIVR ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  		return isoRequest;
  	}
     
     public wIso8583 RetrieveCuentasInversiones(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select tc.CCUENTA, tc.NOMBRECUENTA\r\n" + 
	    		"from tcuenta tc, tpersona tp, TCUENTASPERSONA tcp\r\n" + 
	    		"where \r\n" + 
	    		"tc.CCUENTA = tcp.CCUENTA and\r\n" + 
	    		"tcp.CPERSONA = tp.CPERSONA and\r\n" + 
	    		"tc.CSUBSISTEMA = '05' and\r\n" + 
	    		"tc.CESTATUSCUENTA = '002' and\r\n" + 
	    		"tc.FHASTA > sysdate and\r\n" + 
	    		"tp.FHASTA > sysdate and\r\n" + 
	    		"tcp.FHASTA > sysdate\r\n" + 
	    		"and tp.IDENTIFICACION = '"+  iso.getISO_002_PAN() +"'"; 
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        iso.setISO_102_AccountID_1(StringUtils.Empty());
	        while (rs.next()) {
	        	
	        	++count;
	            iso.setISO_102_AccountID_1(iso.getISO_102_AccountID_1() + "|" + rs.getString("CCUENTA"));
	            iso.setISO_034_PANExt(rs.getString("NOMBRECUENTA"));
	            
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("116");
	        	iso.setISO_039p_ResponseDetail("LA CEDULA " + iso.getISO_002_PAN() + " NO POSEE CUENTAS A PLAZO FIJO, O NO SE ENCUENTRAN ACTIVAS");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveCtasInversiones", TypeMonitor.error, e);
	    	
	    } finally {
	    	iso.setISO_102_AccountID_1(StringUtils.TrimStart(iso.getISO_102_AccountID_1(),"|"));
	    	iso.setISO_102_AccountID_1(StringUtils.trimEnd(iso.getISO_102_AccountID_1(),"|"));
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveCtasInversiones", TypeMonitor.error, e);
				} 
	        }
	    }
	    
	    return isoref;
    }
    
     public List<Iterables> RetrieveResultTransfersMasivasFinancoop(Ref<wIso8583> isoref){

	    int count = 0;
		wIso8583 iso = isoref.get();
		List<Iterables> iter = null;
	
	    Statement stmt = null;
	    String query =  "select * from trans_masivas_financoop\r\n" + 
	    		"where transactionid = '"+ iso.getISO_019_AcqCountryCode() +"'"
	    		+" and valuedate BETWEEN (SYSDATE-3) AND (SYSDATE + 1)"
	    		+ " order by transactionitemid"; 
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(20);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        iter = new ArrayList<Iterables>();
	        while (rs.next()) {
	        	
	        	
	        	++count;
	            Iterables it = new Iterables();
	            it.addMapper("TRANSACTIONID", rs.getString("TRANSACTIONID"));
	            it.addMapper("TRANSACTIONITEMID", rs.getString("TRANSACTIONITEMID"));
	            it.addMapper("SUBTRANSACTIONTYPEID", rs.getString("SUBTRANSACTIONTYPEID"));
	            it.addMapper("TRANSACTIONFEAUTUREID", rs.getString("TRANSACTIONFEATUREID"));
	            it.addMapper("VALUEDATE", rs.getString("VALUEDATE"));
	            it.addMapper("TRANSACTIONTYPEID", rs.getString("TRANSACTIONTYPEID"));
	            it.addMapper("TRANSACTIONSTATUSID", rs.getString("TRANSACTIONSTATUSID"));
	            it.addMapper("CLIENTBANKID", rs.getString("CLIENTBANKID"));
	            it.addMapper("DEBITPRODUCTBANKID", rs.getString("DEBITPRODUCTBANKID"));
	            it.addMapper("DEBITPRODUCTTYPEID", rs.getString("DEBITPRODUCTTYPEID"));
	            it.addMapper("DEBITCURRENCYID", rs.getString("DEBITCURRENCYID"));
	            it.addMapper("CREDITPRODUCTBANKID", rs.getString("CREDITPRODUCTBANKID"));
	            it.addMapper("CREDITPRODUCTTYPEID", rs.getString("CREDITPRODUCTTYPEID"));
	            it.addMapper("CREDITCURRENCYID", rs.getString("CREDITCURRENCYID"));
	            it.addMapper("AMOUNT", rs.getString("AMOUNT"));
	            it.addMapper("NOTIFYTO", rs.getString("NOTIFYTO"));
	            it.addMapper("NOTIFICATIONCHANNELID", rs.getString("NOTIFICATIONCHANNELID"));
	            it.addMapper("DESTINANTIONDOCUMENTID", rs.getString("DESTINATIONDOCUMENTID"));
	            it.addMapper("DESTINATIONNAME", rs.getString("DESTINATIONNAME"));
	            it.addMapper("DESTINATIONBANK", rs.getString("DESTINATIONBANK"));
	            it.addMapper("DESCRIPTION", rs.getString("DESCRIPTION"));
	            it.addMapper("BANKROUTINGNUMBER", rs.getString("BANKROUTINGNUMBER"));
	            it.addMapper("SOURCENAME", rs.getString("SOURCENAME"));
	            it.addMapper("SOURCEBANK", rs.getString("SOURCEBANK"));
	            it.addMapper("SOURCEDOCUMENTID", rs.getString("SOURCEDOCUMENTID"));
	            it.addMapper("REGULATIONAMOUNTEXCEEDED", rs.getString("REGULATIONAMOUNTEXCEEDED"));
	            it.addMapper("SOURCEFUNDS", rs.getString("SOURCEFUNDS"));
	            it.addMapper("DESTINATIONFUNDS", rs.getString("DESTINATIONFUNDS"));
	            it.addMapper("USERDOCUMENTID", rs.getString("USERDOCUMENTID"));
	            it.addMapper("TRANSACTIONCOST", rs.getString("TRANSACTIONCOST"));
	            it.addMapper("TRANSACTIONCOSTCURRENCYID", rs.getString("TRANSACTIONCOSTCURRENCYID"));
	            it.addMapper("EXCHANGERATE", rs.getString("EXCHANGERATE"));
	            it.addMapper("ISVALID", rs.getString("ISVALID"));
	            it.addMapper("VALIDATIONMESSAGE", rs.getString("VALIDATIONMESSAGE"));
	            iter.add(it);
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("116");
	        	iso.setISO_039p_ResponseDetail("NO SE ENCONTRARON TRANSACCIONES CON EL TRANSACTIONID " + iso.getISO_019_AcqCountryCode());
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveResultTransfersMasivasFinancoop", TypeMonitor.error, e);
	    	
	    } finally {

	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveResultTransfersMasivasFinancoop", TypeMonitor.error, e);
				} 
	        }
	    }
	    
	    return iter;
    }
    
     public wIso8583 RetrieveValidationSNPSPI(wIso8583 isoRequest) {
   		
   		CallableStatement cs = null ;
   		
   		try {			
   			
   			Connection cn = MemoryGlobal.conn;		
   			cs = cn.prepareCall("{ CALL SW_VALIDATE_DATA_SNP_SPI(?,?)}");
   			cs.setString("i_numDocumento", isoRequest.getISO_123_ExtendedData());
   			cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
   			cs.execute();
   			ResultSet rs = (ResultSet)cs.getObject("p_iso_resulset");
   			int count = 0;
   			
   			if(rs != null){
   				
   				while (rs.next()) {
   					count++;
   					//if(count > 1)
   						//break;
   					
   					isoRequest.setISO_039_ResponseCode(rs.getString("COD"));
   					isoRequest.setISO_039p_ResponseDetail(rs.getString("DETALLE"));
   					
   					isoRequest.setWsIso_LogStatus(2);
   					isoRequest.setWsISO_TranDatetimeResponse(new Date());
   					if(rs.getString("COD").equals("000")){
   						
   						isoRequest.setISO_123_ExtendedData(rs.getString("IDENTIFICACION"));
   						isoRequest.setISO_103_AccountID_2(isoRequest.getISO_103_AccountID_2() + rs.getString("CCUENTA") + "|");
   						isoRequest.setISO_114_ExtendedData(rs.getString("NOMBRECUENTA"));
   					}
   				}
   				if (count == 0) {
   					
   					isoRequest = new wIso8583();
   					isoRequest.setISO_039_ResponseCode("BE01");
   					isoRequest.setISO_039p_ResponseDetail("La identificacion del cliente final no corresponde con la asociada al numero de cuenta");
   				}	
   				
   			}else{
   				
   				isoRequest = new wIso8583();
   				isoRequest.setISO_039_ResponseCode("909");
   				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
   			}
   		    
   		} catch (SQLException ex) {
   			
   			isoRequest.setISO_039_ResponseCode("909");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE VALIDATION SPI ERROR ", ex, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveValidationSNPSPI (SQLException) ", TypeMonitor.error, ex);
   			
   		} catch (Exception e) {			
   			
   			isoRequest.setISO_039_ResponseCode("908");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveValidationSNPSPI ", TypeMonitor.error, e);
   			
   		}finally {
   			
   			isoRequest.setISO_103_AccountID_2(StringUtils.trimEnd(isoRequest.getISO_103_AccountID_2(), "|"));
   			try {
   				if(cs!=null)
   					cs.close();
   			} catch (SQLException e) {e.printStackTrace();}
   		}
   		
   		return isoRequest;
   	} 
    
     public SnpSPIOrdenante RetrieveTrxSPI_Ord(SnpSPIOrdenante snp) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_RETRIEVE_TRX_SPI_ORD(?,?,?,?,?,?)}");
  			
  			@SuppressWarnings("deprecation")
			java.sql.Date sqlDate = new java.sql.Date(snp.getCredttm().getDate()); 
  			
  			cs.setDate("i_fecha", sqlDate);
  			cs.setString("i_msgid", snp.getMsgid());
  			cs.setString("i_endtoendid", snp.getEndtoendid());
  			cs.setString("i_trxid", snp.getTxid());
  			cs.setString("i_flagIsOrdenante", String.valueOf(snp.isOrdenante()).toUpperCase());
  			
  			cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
  			cs.execute();
  			ResultSet rs = (ResultSet)cs.getObject("p_iso_resulset");
  			int count = 0;
  			
  			if(rs != null){
  				
  				while (rs.next()) {
  					count++;
  					if(count > 1)
  						break;
  					
  					snp.setError_code_prop(rs.getString("COD"));
  					snp.setDes_code_prop(rs.getString("DETALLE"));
  					snp.setLogstatus(2);
  					if(rs.getString("COD").equals("000")){
  						
  						snp.setFecha(FormatUtils.StringToDate(rs.getString("FECHA"),"yyyyy-MM-dd HH:mm:ss"));
  						snp.setMsgid(rs.getString("MSGID"));
  						snp.setCredttm(FormatUtils.StringToDate(rs.getString("CREDTTM"),"yyyyy-MM-dd HH:mm:ss"));
  						snp.setInstrid(rs.getString("INSTRID"));
  						snp.setEndtoendid(rs.getString("ENDTOENDID"));
  						snp.setTxid(rs.getString("TXID"));
  						snp.setAmmount(Double.parseDouble(rs.getString("AMMOUNT")));
  						snp.setFeccont(FormatUtils.StringToDate(rs.getString("FECCONT"),"yyyyy-MM-dd HH:mm:ss"));
  						snp.setOrd_nm(rs.getString("ORD_NM"));
  						snp.setOrd_id(rs.getString("ORD_ID"));
  						snp.setOrd_txid(rs.getString("ORD_TXID"));
  						snp.setOrd_account(rs.getString("ORD_ACCOUNT"));
  						snp.setOrd_account_type_money(rs.getString("ORD_ACCOUNT_TYPE"));
  						snp.setOrd_account_type_money(rs.getString("ORD_ACCOUNT_TYPE_MONEY"));
  						snp.setInst_acc_bce(rs.getString("INST_ACC_BCE"));
  						snp.setInst_acc_bce_type(rs.getString("INST_ACC_BCE_TYPE"));
  						snp.setInst_acc_bce_type_money(rs.getString("INST_ACC_BCE_TYPE_MONEY"));
  						snp.setInst_ord_code(rs.getString("INST_ORD_CODE"));
  						snp.setInst_ord_age(rs.getString("INST_ORD_AGE"));
  						snp.setInst_rcp_code(rs.getString("INST_RCP_CODE"));
  						snp.setRcp_nm(rs.getString("RCP_NM"));
  						snp.setRcp_id(rs.getString("RCP_ID"));
  						snp.setRcp_txid(rs.getString("RCP_TXID"));
  						snp.setRcp_account(rs.getString("RCP_ACCOUNT"));
  						snp.setRcp_account_type(rs.getString("RCP_ACCOUNT_TYPE"));
  						snp.setRcp_account_type_money(rs.getString("RCP_ACCOUNT_TYPE_MONEY"));
  						snp.setPurp_code(rs.getString("PURP_CODE"));
  						snp.setCod_error_auth(rs.getString("COD_ERROR_AUTH"));
  						snp.setDes_error_auth(rs.getString("DES_ERROR_AUT"));
  						snp.setLogstatus(Integer.parseInt(rs.getString("LOGSTATUS")));
  						snp.setFitswitch_time(Double.parseDouble(rs.getString("FITSWITCH_TIME")));
  						snp.setBdd_time(Double.parseDouble(rs.getString("BDD_TIME")));
  						snp.setAuth_time(Double.parseDouble(rs.getString("AUTH_TIME")));
  						snp.setCod_return_core(rs.getString("COD_RETURN_CORE"));
  						snp.setRever_flag(rs.getString("REVER_FLAG"));
  						snp.setRever_return_core_code(rs.getString("REVER_RETURN_CORE_CODE"));
  						snp.setDate_last_bce(FormatUtils.StringToDate(rs.getString("DATE_LAST_BCE"),"yyyyy-MM-dd HH:mm:ss"));
  						snp.setMsgid_last_be(rs.getString("MSGID_LAST_BE"));
  						snp.setStatus_bce(rs.getString("STATUS_BCE"));
  						snp.setIso_message(rs.getString("ISO_MESSAGE"));
  						snp.setStatus_reason_bce(rs.getString("STATUS_REASON_BCE"));
  						
  					}
  				}
  				if (count == 0) {
  					
  					snp.setError_code_prop("116");
  					snp.setDes_code_prop("EL PROCESO DE CONSULTA NO HA ARROJADO NINGUN RESULTADO");
  				}	
  				
  			}else{
  				
  				snp.setError_code_prop("909");
  				snp.setDes_code_prop("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			snp.setError_code_prop("909");
  			snp.setDes_code_prop(GeneralUtils.ExceptionToString("TRX. SPI ORD ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveTrxSPI_Ord (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			snp.setError_code_prop("908");
  			snp.setDes_code_prop(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveTrxSPI_Ord ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  		return snp;
  	}
     
     public wIso8583 RetrieveEFI_SPI_SQL(wIso8583 isoRequest) {
  		
  		CallableStatement cs = null ;
  		
  		try {			
  			
  			Connection cn = MemoryGlobal.conn;		
  			cs = cn.prepareCall("{ CALL SW_RETRIEVE_EFI_SPI(?,?)}");
  			log.WriteLogMonitor("LLego BANK... " + isoRequest.getISO_055_EMV(), TypeMonitor.monitor, null);
  			cs.setString("i_CodBank", isoRequest.getISO_055_EMV());
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
  					if(rs.getString("COD").equals("000")){
  						
  						isoRequest.setISO_055_EMV(rs.getString("COD_HOM"));
  						
  					}
  				}
  				if (count == 0) {
  					
  					isoRequest = new wIso8583();
  					isoRequest.setISO_039_ResponseCode("908");
  					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
  				}	
  				
  			}else{
  				
  				isoRequest = new wIso8583();
  				isoRequest.setISO_039_ResponseCode("909");
  				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
  			}
  		    
  		} catch (SQLException ex) {
  			
  			isoRequest.setISO_039_ResponseCode("909");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", ex, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
  			
  		} catch (Exception e) {			
  			
  			isoRequest.setISO_039_ResponseCode("908");
  			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COMMERCE ERROR ", e, false));
  			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveCommerceSQL ", TypeMonitor.error, e);
  			
  		}finally {
  			
  			
  			try {
  				if(cs!=null)
  					cs.close();
  			} catch (SQLException e) {e.printStackTrace();}
  		}
  		
  		return isoRequest;
  	} 
     
     public wIso8583 RetrieveCOST_FINANCOOP(wIso8583 isoRequest) {
   		
   		CallableStatement cs = null ;
   		
   		try {			
   			
   			Connection cn = MemoryGlobal.conn;		
   			
   			cs = cn.prepareCall("{ CALL SW_RETRIEVE_COST_FINANCOOP(?,?,?,?,?,?)}");
   			cs.setString("i_comodin", isoRequest.getISO_BitMap());//ONLY_FEATURE
   			cs.setString("i_feature", isoRequest.getISO_023_CardSeq());
   			cs.setString("i_param1",  isoRequest.getISO_120_ExtendedData());
   			cs.setString("i_param2",  isoRequest.getISO_121_ExtendedData());
   			cs.setString("i_param3",  isoRequest.getISO_122_ExtendedData());
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
   					if(rs.getString("COD").equals("000")){
   						
   						isoRequest.setISO_006_BillAmount(Double.parseDouble(rs.getString("COSTO")));
   					}
   				}
   				if (count == 0) {
   					
   					isoRequest = new wIso8583();
   					isoRequest.setISO_039_ResponseCode("908");
   					isoRequest.setISO_039p_ResponseDetail("ERROR, EL PROCESO NO HA ARROJADO NINGUN RESULTADO");
   				}	
   				
   			}else{
   				
   				isoRequest = new wIso8583();
   				isoRequest.setISO_039_ResponseCode("909");
   				isoRequest.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO INTERACTUAR CON LA BDD");
   			}
   		    
   		} catch (SQLException ex) {
   			
   			isoRequest.setISO_039_ResponseCode("909");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COST FINANCOOP ERROR ", ex, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::MantenainceCommerceSQL (SQLException) ", TypeMonitor.error, ex);
   			
   		} catch (Exception e) {			
   			
   			isoRequest.setISO_039_ResponseCode("908");
   			isoRequest.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE COST FINANCOOP ERROR ", e, false));
   			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::RetrieveCOST_FINANCOOP ", TypeMonitor.error, e);
   			
   		}finally {
   			
   			
   			try {
   				if(cs!=null)
   					cs.close();
   			} catch (SQLException e) {e.printStackTrace();}
   		}
   		
   		return isoRequest;
   	}  
    
	 public wIso8583 getControlBDDStatus(wIso8583 iso){
		
		CallableStatement cs = null ;
		try {
			
			Connection cn = MemoryGlobal.conn;
			cs = cn.prepareCall("{ CALL SW_CONTROL_BDD(?)}");
			cs.registerOutParameter("p_recordsetInit", OracleTypes.CURSOR);
			cs.execute();
			ResultSet rs = (ResultSet)cs.getObject("p_recordsetInit");
			
			if(rs != null){
				while (rs.next()) {
					iso.setISO_120_ExtendedData(rs.getString(3));
				}
			}
			if(StringUtils.IsNullOrEmpty(iso.getISO_120_ExtendedData())){
				
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("RETORNO RS=0");
			}
			else{
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			}
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", ex, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getControlBDDStatus (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getControlBDDStatus ", TypeMonitor.error, e);
			
		}finally {
			
			 try {
				 if(cs!=null)
				  cs.close();
			} catch (SQLException e) { e.printStackTrace(); }
		}
		
		return iso;
	}
	
     public wIso8583 getInfoSocialBankingRegister(wIso8583 iso){
		
		CallableStatement cs = null ;
		int count = 0;
		
		try {
			
			Connection cn = MemoryGlobal.conn;
			cs = cn.prepareCall("{ CALL SW_SOCIALBANKING_REGISTER(?,?)}");
			cs.setString("i_wISO_023_CardSeq", iso.getISO_023_CardSeq());
			cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
			cs.execute();
			ResultSet rs = (ResultSet)cs.getObject("p_iso_resulset");
			
			if(rs != null){
				
				while (rs.next()) {
					++count;
					iso.setISO_120_ExtendedData(rs.getString(1));
					iso.setISO_121_ExtendedData(rs.getString(2));
					iso.setISO_122_ExtendedData(rs.getString(3));
					iso.setISO_123_ExtendedData(rs.getString(4));
					iso.setISO_124_ExtendedData(rs.getString(5));
				}
				
				if(count > 0){
					
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
				}else{
					
					iso.setISO_039_ResponseCode("214");
					iso.setISO_039p_ResponseDetail("EL SOCIO/CLIENTE CON IDENTIFICACION: " + 
					iso.getISO_023_CardSeq() + " NO EXISTE EN LA INSTITUCION FINANCIERA");
				}
				
				iso.setWsIso_LogStatus(2);
				
			}else{
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS NO SE HA PODIDO RECUPERAR INFORMACION (NULL EXCEPTION)");
			}
			
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", ex, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getInfoSocialBankingRegister (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getInfoSocialBankingRegister ", TypeMonitor.error, e);
			
		}finally {
			
			 try {
				 if(cs!=null)
				  cs.close();
			} catch (SQLException e) { e.printStackTrace(); }
		}
		
		return iso;
	}
	
	 private wIso8583 getInfoReversoFit1(wIso8583 iso){
		
		
		try {
			log.WriteLogMonitor("Ingresa a ejecutar Consulta Transaccion Original....", TypeMonitor.monitor, null);
			Iso8583 ISO = new Iso8583();
			ISO.setISO_000_Message_Type("1200");
			ISO.setISO_003_ProcessingCode("333333");
			ISO.setISO_007_TransDatetime(new Date());
			ISO.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(8));
			ISO.setISO_012_LocalDatetime(new Date());
			ISO.setISO_015_SettlementDatel(new Date());
			ISO.setISO_018_MerchantType(iso.getISO_018_MerchantType());
			ISO.setISO_024_NetworkId(iso.getISO_024_NetworkId());
		    ISO.setISO_044_AddRespData(iso.getISO_044_AddRespData());
			ISO.setISO_115_ExtendedData("NUMEROMENSAJE");
			ISO.setISO_102_AccountID_1(iso.getISO_102_AccountID_1());
			
			csProcess proccesor = new csProcess();
			ISO = proccesor.ProcessTransactionMain(ISO, "127.0.0.1");
			if(ISO.getISO_039_ResponseCode().equals("000")){
				
				iso.setISO_044_AddRespData(ISO.getISO_114_ExtendedData());
				iso.setISO_039_ResponseCode("000");
				
			}else{
				
				iso.setISO_039_ResponseCode(ISO.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(ISO.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE PUDO RECUPERAR MSGID FIT1 " 
						+ GeneralUtils.ExceptionToString(null, e, true));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getInfoReversoFit1", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	 public wIso8583 getValCupoTrx(wIso8583 iso){
		
		Connection conn = null; 
		PreparedStatement cs = null ;
		try {
			
			conn = DBCPDataSource.getConnection();
			String procname = "SW_C_TRANSACTIONCUPO";
			String stmtString = "exec " + procname + " @iLimProcCode=?, @iLimRed =?, @iLimDocumento =?, "
	        		+ "@iLimCanal =?, @iLimNumero=?, @iLimMonto=?, @iLimSecuencial=?, @iLimAmmountDebit=?";
	        cs = conn.prepareStatement(stmtString);
			cs.setString(1, iso.getISO_003_ProcessingCode());
			cs.setString(2, iso.getISO_024_NetworkId());
			if(StringUtils.IsNullOrEmpty(iso.getISO_102_AccountID_1()))
				cs.setString(3, iso.getISO_002_PAN());
			else {
				
				/*Cambio por SPI Receptor, para que se registre el 103 cuenta de la persona en la Coop.*/
				if(iso.getISO_003_ProcessingCode().startsWith("71") && iso.getISO_024_NetworkId().equals("555777"))
					cs.setString(3, iso.getISO_103_AccountID_2());
				else
					cs.setString(3, iso.getISO_102_AccountID_1());
			}
			
			cs.setString(4, iso.getISO_018_MerchantType());
			cs.setInt(5, 1);
			cs.setDouble(6, iso.getISO_004_AmountTransaction());
			cs.setString(7, iso.getISO_011_SysAuditNumber());
			cs.setDouble(8, iso.getWsTransactionConfig().getAmmountDebit());
			
			cs.setQueryTimeout((iso.getWsTransactionConfig().getProccodeTimeOutValue()/1000) + 2);
			ResultSet rs = cs.executeQuery();
			
			iso.setISO_039_ResponseCode(null);
			
			if(rs != null){
				while (rs.next()) {
					iso.setISO_039_ResponseCode(rs.getString(1));	
					iso.setISO_039p_ResponseDetail(rs.getString(2));
				}
			}
			if(StringUtils.IsNullOrEmpty(iso.getISO_039_ResponseCode())){
				
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("RETORNO RS=0");
			}
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", ex, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getValCupoTrx (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getValCupoTrx ", TypeMonitor.error, e);
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
	
     public wIso8583 debitCobisAuth(wIso8583 iso){
		
		Connection conn = null;
		PreparedStatement cs = null ;
		try {
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1);
			String stmtString = "exec " + procname + " @numero_cuenta=?, @monto_transaccion=?, @causal=?, "
	        		+ "@concepto=?, @observacion=?";
	        cs = conn.prepareStatement(stmtString);
			cs.setString(1, iso.getISO_102_AccountID_1().trim());
			cs.setDouble(2, iso.getISO_004_AmountTransaction() + iso.getISO_008_BillFeeAmount());//Servicio + comision
			cs.setString(3, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cs.setString(4, StringUtils.padRight(iso.getWsTransactionConfig().getProccodeDescription(),30, " ").substring(0, 30).trim());
			cs.setString(5, "PAGO SERVICIOS, " + StringUtils.padRight((iso.getISO_120_ExtendedData() + " -- " + iso.getWsTransactionConfig()
			.getProccodeDescription()),100," ").substring(0, 100).trim());
			
			ResultSet rs = cs.executeQuery();
			
			iso.setISO_039_ResponseCode(null);
			
			if(rs != null){
				while (rs.next()) {
					
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					iso.setISO_090_OriginalData(rs.getString(1));
				}
			}
			if(StringUtils.IsNullOrEmpty(iso.getISO_039_ResponseCode())){
				
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("RETORNO RS=0");
				iso.setResponseBelejanor(true);
			}
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::debitCobisAuth (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::debitCobisAuth ", TypeMonitor.error, e);
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
    
    public wIso8583 savingAccountCobisAuth(wIso8583 iso){
		
		
		PreparedStatement cs = null ;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String procname = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0);
			String stmtString = "exec " + procname + " @i_cta_banco=?";
	        cs = conn.prepareStatement(stmtString);
			cs.setString(1, iso.getISO_102_AccountID_1().trim());
			
			ResultSet rs = cs.executeQuery();
			
			iso.setISO_039_ResponseCode(null);
			
			if(rs != null){
				while (rs.next()) {
					
					iso.setISO_028_TranFeeAmount(rs.getDouble(1));	
					
					if(iso.getISO_028_TranFeeAmount() > 0) {
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					}else if (iso.getISO_028_TranFeeAmount() <= 0 && iso.getISO_028_TranFeeAmount() != -1) {
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("FONDOS INSUFICIENTES");
						iso.setResponseBelejanor(true);
					}else {
						iso.setISO_039_ResponseCode("214");
						iso.setISO_039p_ResponseDetail("CUENTA NO EXISTE O LA MISMA NO ESTA ACTIVA");
						iso.setResponseBelejanor(true);
					}
				}
			}
			if(StringUtils.IsNullOrEmpty(iso.getISO_039_ResponseCode())){
				
				iso.setISO_039_ResponseCode("215");
				iso.setISO_039p_ResponseDetail("CUENTA NO EXISTE");
				iso.setResponseBelejanor(true);
			}
			
		} catch (SQLException ex) {
			
			iso.setISO_039_ResponseCode(String.valueOf(ex.getErrorCode()));
			iso.setISO_039p_ResponseDetail(ex.getMessage());
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::savingAccountCobisAuth (SQLException) ", TypeMonitor.error, ex);
			
		} catch (Exception e) {			
			
			iso.setISO_039_ResponseCode("908");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("RETRIEVE ISO ERROR ", e, false));
			log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::savingAccountCobisAuth ", TypeMonitor.error, e);
			
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
	
	public wIso8583 executeQueryValidAccount(Ref<wIso8583> isoref){

		    int count = 0;
			wIso8583 iso = isoref.get();
		
		    Statement stmt = null;
		    String query =  "select CCUENTA, NOMBRECUENTA, CCONDICIONOPERATIVA, " +
		    		        "CESTATUSCUENTA, CGRUPOPRODUCTO, CPRODUCTO " +
				    		"from tcuenta nolock " +
				    		"where CSUBSISTEMA = '04' " +
				    		"and FHASTA > SYSDATE " +
				    		"and CESTATUSCUENTA = '002' " +
				    		"and CPRODUCTO not in ('407','410', '991', '305', '990') " +
				    		"and CCUENTA = '"+  iso.getISO_102_AccountID_1() + "' " +
				    		"and CPERSONA_COMPANIA = 2 "; 
		    try {
		    	
		        stmt = MemoryGlobal.conn.createStatement();
		        stmt.setQueryTimeout(10);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	
		        	++count;
		            iso.setISO_120_ExtendedData(rs.getString("NOMBRECUENTA"));
		            iso.setISO_121_ExtendedData(rs.getString("CESTATUSCUENTA"));
		            iso.setISO_122_ExtendedData(rs.getString("CGRUPOPRODUCTO") + " - " + rs.getString("CPRODUCTO"));
		            iso.setISO_123_ExtendedData(rs.getString("CCONDICIONOPERATIVA"));
		            
		        }
		        
		        if(count <= 0){
		        	
		        	iso.setISO_039_ResponseCode("214");
		        	iso.setISO_039p_ResponseDetail("LA CUENTA NO EXISTE, O NO PERMITIDA PARA "
		        			+ "ESTE TIPO DE TRANSACCION");
		        	
		        }else{
		        	
		        	iso.setISO_039_ResponseCode("000");
		        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		        }
		        
		        iso.setWsIso_LogStatus(2);
		        iso.setWsISO_TranDatetimeResponse(new Date());
		        
		    } catch (SQLException e ) {
		       
		    	iso.setISO_039_ResponseCode("909");
		    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeQueryValidAccount", TypeMonitor.error, e);
		    	
		    } finally {
		    	
		        if (stmt != null) { 
		        	
			        try {
						stmt.close();
					} catch (SQLException e) {
						
						iso.setISO_039_ResponseCode("909");
				    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
				    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeQueryValidAccount", TypeMonitor.error, e);
					} 
		        }
		    }
		    
		    return isoref.get();
	}
	
	public wIso8583 getDataIvrCallCenter(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select cpersona, identificacion, nombrelegal\r\n" + 
	    		"from tpersona\r\n" + 
	    		"where identificacion = '"+ iso.getISO_002_PAN() +"'\r\n" + 
	    		"and fhasta > sysdate"; 
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	            iso.setISO_120_ExtendedData(rs.getString("CPERSONA"));
	            iso.setISO_121_ExtendedData(rs.getString("IDENTIFICACION"));
	            iso.setISO_122_ExtendedData(rs.getString("NOMBRELEGAL"));
	            
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("116");
	        	iso.setISO_039p_ResponseDetail("CLIENTE NO EXISTE, O NO PERMITIDA PARA "
	        			+ "ESTE TIPO DE TRANSACCION");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataIvrCallCenter", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataIvrCallCenter", TypeMonitor.error, e);
				} 
	        }
	    }
	    
	    return iso;
}
	
	public Iso8583 executeValidationTDDSaiBank(Iso8583 iso){

	    int count = 0;
	
	    Statement stmt = null;
	    
	    String query = "";
	    
	    if(iso.getISO_003_ProcessingCode().startsWith("01")) {
	    	
	    	/*Cuando es debitos*/
	    	query =  "select CESTATUSPLASTICO from TTARJETADEBITOSOLICITUD\r\n" + 
	    		         "where fhasta > sysdate and ctipotarjeta = 'DEB' " +
	    		         " and numerotarjeta='"+ iso.getISO_002_PAN() +"'";
	    }else {
	    
	    	/*Cuando son consultas*/
	    	query = "select CESTATUSPLASTICO from TTARJETADEBITOSOLICITUD\r\n" + 
		    		" where fhasta > sysdate and ctipotarjeta = 'DEB'\r\n" + 
		    		" and ccuenta = '"+ iso.getISO_102_AccountID_1() +"'";
	    }
	    
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	            iso.setISO_090_OriginalData(rs.getString("CESTATUSPLASTICO"));
	        }
	        
	        if(count <= 0){
	        	
	        	if(iso.getISO_003_ProcessingCode().startsWith("01")) {
	        		
		        	iso.setISO_039_ResponseCode("300");
		        	iso.setISO_039p_ResponseDetail("LA TARJETA NO EXISTE");
		        	
	        	}else {
	        		
	        		iso.setISO_039_ResponseCode("300");
		        	iso.setISO_039p_ResponseDetail("LA CUENTA NO EXISTE");
	        	}
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeValidationTDDSaiBank", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::executeValidationTDDSaiBank", TypeMonitor.error, e);
				} 
	        }
	    }
	    
	    return iso;
}
	
 public wIso8583 validateAccountPagoDirectoBanred(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select tc.CCUENTA, tc.NOMBRECUENTA, tp.identificacion, \r\n" + 
	    		"tc.cproducto, tc.cestatuscuenta\r\n" + 
	    		"from tcuenta tc, tpersona tp, TCUENTASPERSONA tcp\r\n" + 
	    		"where \r\n" + 
	    		"tc.CCUENTA = tcp.CCUENTA and\r\n" + 
	    		"tcp.CPERSONA = tp.CPERSONA and\r\n" + 
	    		"tc.CSUBSISTEMA = '04' and\r\n" + 
	    		"cproducto not in('000','305','991','992') and\r\n" + 
	    		"tc.FHASTA > sysdate and\r\n" + 
	    		"tp.FHASTA > sysdate and\r\n" + 
	    		"tcp.FHASTA > sysdate and\r\n" + 
	    		"tc.ccuenta = '"+ iso.getISO_102_AccountID_1() +"';";
	    	
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	            iso.setISO_120_ExtendedData(rs.getString("CCUENTA"));
	            iso.setISO_121_ExtendedData(rs.getString("NOMBRECUENTA"));
	            iso.setISO_122_ExtendedData(rs.getString("IDENTIFICACION"));
	            iso.setISO_123_ExtendedData(rs.getString("CPRODUCTO"));
	            iso.setISO_124_ExtendedData(rs.getString("CESTATUSCUENTA"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("214");
	        	iso.setISO_039p_ResponseDetail("CUENTA NO EXISTE");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDireccionTelefonoFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDireccionTelefonoFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	
public wIso8583 getDireccionTelefonoFit1(wIso8583 isoref){

    int count = 0;
	wIso8583 iso = isoref;

    Statement stmt = null;
    String query =  "select nvl((tpd.direccion || ', ' || nvl(tpd.observaciones,'S/N')),'N/D') as DIRECCION, " + 
    		"       case tpd.ctipodireccion " + 
    		"       when 'DO' then 'DOMICILIO' " + 
    		"       when 'OF' then 'OFICINA' end TIPO_DIRECCION, " + 
    		"       tci.nombre as CIUDAD, " + 
    		"       nvl(tpt.codigoarea || tpt.numerotelefono,'N/D') as TELEFONO " + 
    		"from tpersona tp, tpersonadirecciones tpd, tciudades tci, tprovincias tpi, tbarrios tbi, " + 
    		"     tpersonatelefonos tpt " + 
    		"where tp.cpersona = tpd.cpersona " + 
    		"and tp.cpersona = tpt.cpersona " + 
    		"and tpd.fhasta > sysdate " + 
    		"and tpd.numerodireccion = 1 " + 
    		"and tpd.ctipodireccion in('DO','OF') " + 
    		"and tpd.cciudad = tci.cciudad " + 
    		"and tpd.cprovincia = tpi.cprovincia " + 
    		"and tpd.cbarrio = tbi.cbarrio " + 
    		"and tci.cpais = tbi.cpais " + 
    		"and tci.cciudad = tbi.cciudad " + 
    		"and tci.cprovincia = tbi.cprovincia " + 
    		"and tpi.cprovincia = tci.cprovincia " + 
    		"and tpi.fhasta = tbi.fhasta " + 
    		"and tp.fhasta > sysdate " + 
    		"and tpt.fhasta > sysdate " + 
    		"and tpt.stelefono = 1 " + 
    		"and tp.identificacion = '"+ isoref.getISO_002_PAN() +"'";
    	
    try {
    	
        stmt = MemoryGlobal.conn.createStatement();
        stmt.setQueryTimeout(10);
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
        	
        	++count;
            iso.setISO_120_ExtendedData(rs.getString("DIRECCION"));
            iso.setISO_121_ExtendedData(rs.getString("TIPO_DIRECCION"));
            iso.setISO_122_ExtendedData(rs.getString("CIUDAD"));
            iso.setISO_123_ExtendedData(rs.getString("TELEFONO"));
        }
        
        if(count <= 0){
        	
        	iso.setISO_039_ResponseCode("214");
        	iso.setISO_039p_ResponseDetail("CLIENTE NO POSEE INFORMACION");
        	
        }else{
        	
        	iso.setISO_039_ResponseCode("000");
        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
        }
        
        iso.setWsIso_LogStatus(2);
        iso.setWsISO_TranDatetimeResponse(new Date());
        
    } catch (SQLException e ) {
       
    	iso.setISO_039_ResponseCode("909");
    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDireccionTelefonoFit1", TypeMonitor.error, e);
    	
    } finally {
    	
        if (stmt != null) { 
        	
	        try {
				stmt.close();
			} catch (SQLException e) {
				
				iso.setISO_039_ResponseCode("909");
		    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDireccionTelefonoFit1", TypeMonitor.error, e);
			} 
        }
      }
    
    	return iso;
    }

	public wIso8583 getDataPersonFit1(wIso8583 isoref){
		
	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select tper.CPERSONA, tper.IDENTIFICACION, tc.NOMBRECUENTA, tc.CCUENTA, tc.CMONEDA, tper.CTIPOPERSONA, tper.CTIPOIDENTIFICACION " +
			    		"from TCUENTA  tc ,  TCUENTASPERSONA tp, TPERSONA tper " +
			    		"where tc.CCUENTA = tp.CCUENTA " +
			    		"and   tc.FHASTA = tp.FHASTA " +
			    		"and   tper.CPERSONA = tp.CPERSONA " +
			    		"and   tper.FHASTA = tp.FHASTA " +
			    		"and   tp.FHASTA > SYSDATE " +
			    		"and   tc.FHASTA > SYSDATE " +
			    		"and   tper.FHASTA > SYSDATE " +
			    		"and   tc.CCUENTA = '" + iso.getISO_102_AccountID_1().trim() + "' ";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	iso.setISO_023_CardSeq(rs.getString("CTIPOIDENTIFICACION"));
	        	iso.setISO_034_PANExt(rs.getString("CTIPOPERSONA"));
	            iso.setISO_120_ExtendedData(rs.getString("CPERSONA"));
	            iso.setISO_121_ExtendedData(rs.getString("IDENTIFICACION"));
	            iso.setISO_122_ExtendedData(rs.getString("NOMBRECUENTA"));
	            iso.setISO_123_ExtendedData(rs.getString("CCUENTA"));
	            iso.setISO_124_ExtendedData(rs.getString("CMONEDA"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("214");
	        	iso.setISO_039p_ResponseDetail("LA ASOCIACION CUENTA PERSONA NO EXISTE");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	
     public wIso8583 getDataPersonFit1_103Destino(wIso8583 isoref){
		
	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select tper.CPERSONA, tper.IDENTIFICACION, tc.NOMBRECUENTA, tc.CCUENTA, tc.CMONEDA, tper.CTIPOPERSONA, tper.CTIPOIDENTIFICACION " +
			    		"from TCUENTA  tc ,  TCUENTASPERSONA tp, TPERSONA tper " +
			    		"where tc.CCUENTA = tp.CCUENTA " +
			    		"and   tc.FHASTA = tp.FHASTA " +
			    		"and   tper.CPERSONA = tp.CPERSONA " +
			    		"and   tper.FHASTA = tp.FHASTA " +
			    		"and   tp.FHASTA > SYSDATE " +
			    		"and   tc.FHASTA > SYSDATE " +
			    		"and   tper.FHASTA > SYSDATE " +
			    		"and   tc.CCUENTA = '" + iso.getISO_103_AccountID_2().trim() + "' ";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	iso.setISO_023_CardSeq(rs.getString("CTIPOIDENTIFICACION"));
	        	iso.setISO_034_PANExt(rs.getString("CTIPOPERSONA"));
	            iso.setISO_120_ExtendedData(rs.getString("CPERSONA"));
	            iso.setISO_121_ExtendedData(rs.getString("IDENTIFICACION"));
	            iso.setISO_122_ExtendedData(rs.getString("NOMBRECUENTA"));
	            iso.setISO_123_ExtendedData(rs.getString("CCUENTA"));
	            iso.setISO_124_ExtendedData(rs.getString("CMONEDA"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("214");
	        	iso.setISO_039p_ResponseDetail("LA ASOCIACION CUENTA PERSONA NO EXISTE");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getDataPersonFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	
	public wIso8583 getAccountsBasicBimoFit1(wIso8583 isoref){

		    int count = 0;
			wIso8583 iso = isoref;
		
		    Statement stmt = null;
		    String query =  "select tc.CCUENTA, tc.NOMBRECUENTA \r\n" + 
		    		"from tcuenta tc, tpersona tp, TCUENTASPERSONA tcp \r\n" + 
		    		"where \r\n" + 
		    		"tc.CCUENTA = tcp.CCUENTA and \r\n" + 
		    		"tcp.CPERSONA = tp.CPERSONA and \r\n" + 
		    		"tc.CSUBSISTEMA = '04' and \r\n" + 
		    		"tc.CGRUPOPRODUCTO = '"+ Arrays.asList(isoref.getWsTransactionConfig().getProccodeTransactionFit().split("\\|")).get(2) +"' and \r\n" + 
		    		"tc.CPRODUCTO = '"+ Arrays.asList(isoref.getWsTransactionConfig().getProccodeTransactionFit().split("\\|")).get(3) + "' and \r\n" + 
		    		"tc.CESTATUSCUENTA = '002' and \r\n" + 
		    		"tc.FHASTA > sysdate and \r\n" + 
		    		"tp.FHASTA > sysdate and \r\n" + 
		    		"tcp.FHASTA > sysdate \r\n" + 
		    		"and tp.IDENTIFICACION = '"+ isoref.getISO_002_PAN() +"'";
		    try {
		    	
		        stmt = MemoryGlobal.conn.createStatement();
		        stmt.setQueryTimeout(10);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	
		        	++count;
		        	iso.setISO_102_AccountID_1(rs.getString("CCUENTA"));
		        	iso.setISO_120_ExtendedData(rs.getString("NOMBRECUENTA"));
		        }
		        
		        if(count <= 0){
		        	
		        	iso.setISO_039_ResponseCode("100");
		        	iso.setISO_039p_ResponseDetail("SOCIO CLIENTE NO POSEE CUENTA DEL TIPO CONSULTADO");
		        	
		        }else{
		        	
		        	iso.setISO_039_ResponseCode("000");
		        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		        }
		        
		        iso.setWsIso_LogStatus(2);
		        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getAccountsBasicBimoFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getAccountsBasicBimoFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	   
	public wIso8583 getCpersonaFit1(wIso8583 isoref){

		    int count = 0;
			wIso8583 iso = isoref;
		
		    Statement stmt = null;
		    String query =  "select cpersona from tpersona \r\n" + 
		    				"where identificacion = '"+ isoref.getISO_002_PAN() +"' \r\n" + 
		    				"and fhasta > sysdate";
		    try {
		    	
		        stmt = MemoryGlobal.conn.createStatement();
		        stmt.setQueryTimeout(10);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	
		        	++count;
		        	iso.setISO_124_ExtendedData(rs.getString("CPERSONA"));
		        }
		        
		        if(count <= 0){
		        	
		        	iso.setISO_039_ResponseCode("909");
		        	iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR EL CPERSONA");
		        	
		        }else{
		        	
		        	iso.setISO_039_ResponseCode("000");
		        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		        }
		        
		        iso.setWsIso_LogStatus(2);
		        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCpersonaFit1", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCpersonaFit1", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	
	public wIso8583 getCpersonaFit3(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select cpersona from tpersona \r\n" + 
	    				"where identificacion = '"+ isoref.getISO_002_PAN() +"' \r\n" + 
	    				"and fhasta > sysdate and ROWNUM = 1";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	iso.setISO_124_ExtendedData(rs.getString("CPERSONA"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("909");
	        	iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR EL CPERSONA");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
        
    } catch (SQLException e ) {
       
    	iso.setISO_039_ResponseCode("909");
    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCpersonaFit3", TypeMonitor.error, e);
    	
    } finally {
    	
        if (stmt != null) { 
        	
	        try {
				stmt.close();
			} catch (SQLException e) {
				
				iso.setISO_039_ResponseCode("909");
		    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCpersonaFit3", TypeMonitor.error, e);
			} 
        }
      }
    
    	return iso;
    }
	
	public wIso8583 getCountCtasInversiones(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select count(*) as NRO from tcuenta\r\n" + 
	    		"where csubsistema = '05' \r\n" + 
	    		"and cestatuscuenta = '002' \r\n" + 
	    		"and cpersona_cliente = "+ isoref.getISO_124_ExtendedData() +" \r\n" + 
	    		"and fhasta > sysdate";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	iso.setISO_023_CardSeq(rs.getString("NRO"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("909");
	        	iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR INFORMACION");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
        
    } catch (SQLException e ) {
       
    	iso.setISO_039_ResponseCode("909");
    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCountCtasInversiones", TypeMonitor.error, e);
    	
    } finally {
    	
        if (stmt != null) { 
        	
	        try {
				stmt.close();
			} catch (SQLException e) {
				
				iso.setISO_039_ResponseCode("909");
		    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCountCtasInversiones", TypeMonitor.error, e);
			} 
        }
      }
    
    	return iso;
    }
	
	public wIso8583 getCountCtasPrestamos(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select count(*) as NRO from tcuenta\r\n" + 
	    		"where csubsistema = '06' \r\n" + 
	    		"and cestatuscuenta = '003' \r\n" + 
	    		"and cpersona_cliente = "+ isoref.getISO_124_ExtendedData() +" \r\n" + 
	    		"and fhasta > sysdate";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	iso.setISO_023_CardSeq(rs.getString("NRO"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("909");
	        	iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR INFORMACION");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCountCtasPrestamos", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getCountCtasPrestamos", TypeMonitor.error, e);
				} 
	        }
	      }
    
    	return iso;
    }
	
	public wIso8583 getValidateAccountFit1_PDE(wIso8583 isoref){

	    int count = 0;
		wIso8583 iso = isoref;
	
	    Statement stmt = null;
	    String query =  "select tc.CCUENTA as CUENTA, tgp.DESCRIPCION as TIPO, tec.CESTATUSCUENTA as CESTADO, tec.DESCRIPCION as DESESTADO " +
			    		"from TCUENTA tc, TGRUPOSPRODUCTO tgp, TESTATUSCUENTA tec " +
			    		"where tc.CSUBSISTEMA = (select CSUBSISTEMA from tcuenta where CCUENTA = '" + iso.getISO_102_AccountID_1() + "' and FHASTA > sysdate) " +
			    		"and tc.CGRUPOPRODUCTO = (select CGRUPOPRODUCTO from tcuenta where CCUENTA = '" + iso.getISO_102_AccountID_1() + "' and FHASTA > sysdate) " +
			    		"and tc.CCUENTA = '" + iso.getISO_102_AccountID_1() + "'" +
			    		"and tc.CPERSONA_COMPANIA = tgp.CPERSONA_COMPANIA " +
			    		"and tc.CSUBSISTEMA = tgp.CSUBSISTEMA " +
			    		"and tc.FHASTA = tgp.FHASTA " +
			    		"and tc.CGRUPOPRODUCTO = tgp.CGRUPOPRODUCTO " +
			    		"and tc.FHASTA > SYSDATE " +
			    		"and tgp.FHASTA > SYSDATE " +
			    		"and tc.CESTATUSCUENTA = tec.CESTATUSCUENTA " +
			    		"and tc.CSUBSISTEMA = tec.CSUBSISTEMA " +
			    		"and tc.FHASTA = tec.FHASTA " +
			    		"and tec.FHASTA > sysdate ";
	    try {
	    	
	        stmt = MemoryGlobal.conn.createStatement();
	        stmt.setQueryTimeout(10);
	        ResultSet rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	
	        	++count;
	        	
	        	iso.setISO_102_AccountID_1(rs.getString("CUENTA"));
	            iso.setISO_120_ExtendedData(rs.getString("TIPO"));
	            iso.setISO_121_ExtendedData(rs.getString("CESTADO"));
	            iso.setISO_122_ExtendedData(rs.getString("DESESTADO"));
	        }
	        
	        if(count <= 0){
	        	
	        	iso.setISO_039_ResponseCode("214");
	        	iso.setISO_039p_ResponseDetail("LA CUENTA " + iso.getISO_102_AccountID_1() + " NO EXISTE");
	        	
	        }else{
	        	
	        	iso.setISO_039_ResponseCode("000");
	        	iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        }
	        
	        iso.setWsIso_LogStatus(2);
	        iso.setWsISO_TranDatetimeResponse(new Date());
	        
	    } catch (SQLException e ) {
	       
	    	iso.setISO_039_ResponseCode("909");
	    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
	    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getValidateAccountFit1_PDE", TypeMonitor.error, e);
	    	
	    } finally {
	    	
	        if (stmt != null) { 
	        	
		        try {
					stmt.close();
				} catch (SQLException e) {
					
					iso.setISO_039_ResponseCode("909");
			    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
			    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getValidateAccountFit1_PDE", TypeMonitor.error, e);
				} 
	        }
	      }
	    
	    	return iso;
	    }
	
	public wIso8583 getFechaContableFit1(wIso8583 iso){

		    int count = 0;
			wIso8583 isoR = new wIso8583();
		    
		    Statement stmt = null;
		    String query =  "select CSUCURSAL, TO_CHAR(FCONTABLE,'yyyy-MM-dd') as FECHA " + 
		    				"from TSUCURSALFECHACONTABLE " +
		    				"where FHASTA > sysdate " +
		    				"and csucursal = '" + iso.getISO_042_Card_Acc_ID_Code() + "' ";
		    try {
		    	
		        stmt = MemoryGlobal.conn.createStatement();
		        stmt.setQueryTimeout(5);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	
		        	++count;
		        	isoR.setISO_120_ExtendedData(rs.getString("FECHA"));
		        }
		        
		        if(count <= 0){
		        	
		        	isoR.setISO_039_ResponseCode("214");
		        	isoR.setISO_039p_ResponseDetail("NO EXISTE FECHA CONTABLE CONFGURADA PARA LA SUCURSAL " + iso.getISO_042_Card_Acc_ID_Code());
		        	
		        }else{
		        	
		        	isoR.setISO_039_ResponseCode("000");
		        	isoR.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		        }
		        
		        isoR.setWsIso_LogStatus(2);
		        iso.setWsISO_TranDatetimeResponse(new Date());
		        
		    } catch (SQLException e ) {
		       
		    	isoR.setISO_039_ResponseCode("909");
		    	isoR.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getFechaContableFit1", TypeMonitor.error, e);
		    	
		    } finally {
		    	
		        if (stmt != null) { 
		        	
			        try {
						stmt.close();
					} catch (SQLException e) {
						
						iso.setISO_039_ResponseCode("909");
				    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
				    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getFechaContableFit1", TypeMonitor.error, e);
					} 
		        }
		    }
	    
	    return isoR;
    }

	public wIso8583 getFechaContableFit1_Channels(wIso8583 iso){
		
		    int count = 0;
			wIso8583 isoR = new wIso8583();
		    
		    Statement stmt = null;
		    String query =  "select TF.CSUCURSAL, (select TTER.COFICINA from TTERMINALES TTER " +
		    				"where TTER.CTERMINAL = '" + iso.getISO_042_Card_Acc_ID_Code() + "') as COFICINA, TO_CHAR(TF.FCONTABLE,'yyyy-MM-dd') as FECHA, " +
		    				"(select TTER.CCANAL from TTERMINALES TTER " +
                            "where TTER.CTERMINAL =  '" + iso.getISO_042_Card_Acc_ID_Code() + "'" +
                            "and TTER.FHASTA > sysdate) AS CANAL " +
							"from TSUCURSALFECHACONTABLE TF " +
							"where TF.CSUCURSAL = (select TTER.CSUCURSAL from TTERMINALES TTER " +
						    "where TTER.CTERMINAL = '"+ iso.getISO_042_Card_Acc_ID_Code() + "'" + 
						    "and TTER.FHASTA > sysdate) " +
							"and TF.FHASTA > sysdate ";
		    				
		    try {
		    	
		        stmt = MemoryGlobal.conn.createStatement();
		        stmt.setQueryTimeout(5);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	
		        	++count;
		        	isoR.setISO_120_ExtendedData(rs.getString("FECHA"));
		        	isoR.setISO_121_ExtendedData(rs.getString("CSUCURSAL"));
		        	isoR.setISO_122_ExtendedData(rs.getString("COFICINA"));
		        	isoR.setISO_123_ExtendedData(rs.getString("CANAL"));
		        }
		        
		        if(count <= 0){
		        	
		        	isoR.setISO_039_ResponseCode("214");
		        	isoR.setISO_039p_ResponseDetail("NO EXISTE FECHA CONTABLE CONFGURADA PARA LA SUCURSAL " + iso.getISO_042_Card_Acc_ID_Code());
		        	
		        }else{
		        	
		        	isoR.setISO_039_ResponseCode("000");
		        	isoR.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		        }
		        
		        isoR.setWsIso_LogStatus(2);
		        iso.setWsISO_TranDatetimeResponse(new Date());
		        
		    } catch (SQLException e ) {
		       
		    	isoR.setISO_039_ResponseCode("909");
		    	isoR.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR BDD ", e, false));
		    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getFechaContableFit1", TypeMonitor.error, e);
		    	
		    } finally {
		    	
		        if (stmt != null) { 
		        	
			        try {
						stmt.close();
					} catch (SQLException e) {
						
						iso.setISO_039_ResponseCode("909");
				    	iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CLOSE STATEMENT BDD ", e, false));
				    	log.WriteLogMonitor("Error modulo IsoRetrievalTransaction::getFechaContableFit1", TypeMonitor.error, e);
					} 
		        }
		    }
	    
	    return isoR;
    }
	    	    
	@Override
	public wIso8583 call() throws Exception {
		
		return getControlBDDStatus(this.iso);
	}
	
	
}
