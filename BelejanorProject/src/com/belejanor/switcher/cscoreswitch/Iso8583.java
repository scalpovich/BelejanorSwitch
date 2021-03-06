package com.belejanor.switcher.cscoreswitch;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.XML;
import org.json.JSONObject;
import com.belejanor.switcher.bimo.pacs.camt_998_211.RoRCod;
import com.belejanor.switcher.electroniccash.DTORequestCredit;
import com.belejanor.switcher.electroniccash.DTORequestDebit;
import com.belejanor.switcher.electroniccash.DTORequestIsValidAccount;
import com.belejanor.switcher.electroniccash.DTORequestRevert;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Iso8583_Retrieve;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.cscoreswitch.Iso8583;

@XmlRootElement
public class Iso8583 implements Serializable, Cloneable {

	private static final long serialVersionUID = 1436076052703387344L;
	
	private String ISO_000_Message_Type;
	private String ISO_BitMap;
	private String ISO_002_PAN;
	private String ISO_003_ProcessingCode;
	private double ISO_004_AmountTransaction;
	private double ISO_006_BillAmount;
	private Date ISO_007_TransDatetime;
	private double ISO_008_BillFeeAmount;
	private String ISO_011_SysAuditNumber;	
	private Date ISO_012_LocalDatetime;
	private Date ISO_013_LocalDate;
	private Date ISO_015_SettlementDatel;
	private String ISO_018_MerchantType;
	private String ISO_019_AcqCountryCode;
	private String ISO_022_PosEntryMode;
	private String ISO_023_CardSeq;
	private String ISO_024_NetworkId;
	private double ISO_028_TranFeeAmount;
	private double ISO_029_SettlementFee;
	private double ISO_030_ProcFee;
	private String ISO_032_ACQInsID;
	private String ISO_033_FWDInsID;
	private String ISO_034_PANExt;
	private String ISO_035_Track2;
	private String ISO_036_Track3;
	private String ISO_037_RetrievalReferenceNumber;
	private String ISO_038_AutorizationNumber;
	private String ISO_039_ResponseCode;
	private String ISO_039p_ResponseDetail;
	private String ISO_041_CardAcceptorID;
	private String ISO_042_Card_Acc_ID_Code;
	private String ISO_043_CardAcceptorLoc;
	private String ISO_044_AddRespData;
	private double ISO_049_TranCurrCode;
	private double ISO_051_CardCurrCode;
	private String ISO_052_PinBlock;
	private String ISO_054_AditionalAmounts;
	private String ISO_055_EMV;
	private String ISO_090_OriginalData;
	private String ISO_102_AccountID_1;
	private String ISO_103_AccountID_2;
	private String ISO_104_TranDescription;
	private String ISO_114_ExtendedData;
	private String ISO_115_ExtendedData;
	private String ISO_120_ExtendedData;
	private String ISO_121_ExtendedData;
	private String ISO_122_ExtendedData;
	private String ISO_123_ExtendedData;
	private String ISO_124_ExtendedData;	
	
	
	public Iso8583(String messagetype, String procCode, String canalCode, String networkId){
		
		this();
		this.ISO_000_Message_Type = messagetype;
		this.ISO_003_ProcessingCode = procCode;
		this.ISO_018_MerchantType = canalCode;
		this.ISO_024_NetworkId = networkId;
		this.ISO_011_SysAuditNumber = GeneralUtils.GetSecuencial(8);
		this.ISO_041_CardAcceptorID = "USERMIDD";
		
	}
	
	public String getISO_000_Message_Type() {
		return ISO_000_Message_Type;
	}

	public void setISO_000_Message_Type(String iSO_000_Message_Type) {
		ISO_000_Message_Type = iSO_000_Message_Type;
	}

	public String getISO_BitMap() {
		return ISO_BitMap;
	}

	public void setISO_BitMap(String iSO_BitMap) {
		ISO_BitMap = iSO_BitMap;
	}

	public String getISO_002_PAN() {
		return ISO_002_PAN;
	}

	public void setISO_002_PAN(String iSO_002_PAN) {
		ISO_002_PAN = iSO_002_PAN;
	}

	public String getISO_003_ProcessingCode() {
		return ISO_003_ProcessingCode;
	}

	public void setISO_003_ProcessingCode(String iSO_003_ProcessingCode) {
		ISO_003_ProcessingCode = iSO_003_ProcessingCode;
	}

	public double getISO_004_AmountTransaction() {
		return ISO_004_AmountTransaction;
	}

	public void setISO_004_AmountTransaction(double iSO_004_AmountTransaction) {
		ISO_004_AmountTransaction = iSO_004_AmountTransaction;
	}

	public double getISO_006_BillAmount() {
		return ISO_006_BillAmount;
	}

	public void setISO_006_BillAmount(double iSO_006_BillAmount) {
		ISO_006_BillAmount = iSO_006_BillAmount;
	}

	public Date getISO_007_TransDatetime() {
		return ISO_007_TransDatetime;
	}

	public void setISO_007_TransDatetime(Date iSO_007_TransDatetime) {
		ISO_007_TransDatetime = iSO_007_TransDatetime;
	}

	public double getISO_008_BillFeeAmount() {
		return ISO_008_BillFeeAmount;
	}

	public void setISO_008_BillFeeAmount(double iSO_008_BillFeeAmount) {
		ISO_008_BillFeeAmount = iSO_008_BillFeeAmount;
	}

	public String getISO_011_SysAuditNumber() {
		return ISO_011_SysAuditNumber;
	}

	public void setISO_011_SysAuditNumber(String iSO_011_SysAuditNumber) {
		ISO_011_SysAuditNumber = iSO_011_SysAuditNumber;
	}

	public Date getISO_012_LocalDatetime() {
		return ISO_012_LocalDatetime;
	}

	public void setISO_012_LocalDatetime(Date iSO_012_LocalDatetime) {
		ISO_012_LocalDatetime = iSO_012_LocalDatetime;
	}

	public Date getISO_013_LocalDate() {
		return ISO_013_LocalDate;
	}

	public void setISO_013_LocalDate(Date iSO_013_LocalDate) {
		ISO_013_LocalDate = iSO_013_LocalDate;
	}

	public Date getISO_015_SettlementDatel() {
		return ISO_015_SettlementDatel;
	}

	public void setISO_015_SettlementDatel(Date iSO_015_SettlementDatel) {
		ISO_015_SettlementDatel = iSO_015_SettlementDatel;
	}

	public String getISO_018_MerchantType() {
		return ISO_018_MerchantType;
	}

	public void setISO_018_MerchantType(String iSO_018_MerchantType) {
		ISO_018_MerchantType = iSO_018_MerchantType;
	}

	public String getISO_019_AcqCountryCode() {
		return ISO_019_AcqCountryCode;
	}

	public void setISO_019_AcqCountryCode(String iSO_019_AcqCountryCode) {
		ISO_019_AcqCountryCode = iSO_019_AcqCountryCode;
	}

	public String getISO_022_PosEntryMode() {
		return ISO_022_PosEntryMode;
	}

	public void setISO_022_PosEntryMode(String iSO_022_PosEntryMode) {
		ISO_022_PosEntryMode = iSO_022_PosEntryMode;
	}

	public String getISO_023_CardSeq() {
		return ISO_023_CardSeq;
	}

	public void setISO_023_CardSeq(String iSO_023_CardSeq) {
		ISO_023_CardSeq = iSO_023_CardSeq;
	}

	public String getISO_024_NetworkId() {
		return ISO_024_NetworkId;
	}

	public void setISO_024_NetworkId(String iSO_024_NetworkId) {
		ISO_024_NetworkId = iSO_024_NetworkId;
	}

	public double getISO_028_TranFeeAmount() {
		return ISO_028_TranFeeAmount;
	}

	public void setISO_028_TranFeeAmount(double iSO_028_TranFeeAmount) {
		ISO_028_TranFeeAmount = iSO_028_TranFeeAmount;
	}

	public double getISO_029_SettlementFee() {
		return ISO_029_SettlementFee;
	}

	public void setISO_029_SettlementFee(double iSO_029_SettlementFee) {
		ISO_029_SettlementFee = iSO_029_SettlementFee;
	}

	public double getISO_030_ProcFee() {
		return ISO_030_ProcFee;
	}

	public void setISO_030_ProcFee(double iSO_030_ProcFee) {
		ISO_030_ProcFee = iSO_030_ProcFee;
	}

	public String getISO_032_ACQInsID() {
		return ISO_032_ACQInsID;
	}

	public void setISO_032_ACQInsID(String iSO_032_ACQInsID) {
		ISO_032_ACQInsID = iSO_032_ACQInsID;
	}

	public String getISO_033_FWDInsID() {
		return ISO_033_FWDInsID;
	}

	public void setISO_033_FWDInsID(String iSO_033_FWDInsID) {
		ISO_033_FWDInsID = iSO_033_FWDInsID;
	}

	public String getISO_034_PANExt() {
		return ISO_034_PANExt;
	}

	public void setISO_034_PANExt(String iSO_034_PANExt) {
		ISO_034_PANExt = iSO_034_PANExt;
	}

	public String getISO_035_Track2() {
		return ISO_035_Track2;
	}

	public void setISO_035_Track2(String iSO_035_Track2) {
		ISO_035_Track2 = iSO_035_Track2;
	}

	public String getISO_036_Track3() {
		return ISO_036_Track3;
	}

	public void setISO_036_Track3(String iSO_036_Track3) {
		ISO_036_Track3 = iSO_036_Track3;
	}

	public String getISO_037_RetrievalReferenceNumber() {
		return ISO_037_RetrievalReferenceNumber;
	}

	public void setISO_037_RetrievalReferenceNumber(
			String iSO_037_RetrievalReferenceNumber) {
		ISO_037_RetrievalReferenceNumber = iSO_037_RetrievalReferenceNumber;
	}

	public String getISO_038_AutorizationNumber() {
		return ISO_038_AutorizationNumber;
	}

	public void setISO_038_AutorizationNumber(String iSO_038_AutorizationNumber) {
		ISO_038_AutorizationNumber = iSO_038_AutorizationNumber;
	}

	public String getISO_039_ResponseCode() {
		return ISO_039_ResponseCode;
	}

	public void setISO_039_ResponseCode(String iSO_039_ResponseCode) {
		ISO_039_ResponseCode = iSO_039_ResponseCode;
	}

	public String getISO_039p_ResponseDetail() {
		return ISO_039p_ResponseDetail;
	}

	public void setISO_039p_ResponseDetail(String iSO_039p_ResponseDetail) {
		ISO_039p_ResponseDetail = iSO_039p_ResponseDetail;
	}

	public String getISO_041_CardAcceptorID() {
		return ISO_041_CardAcceptorID;
	}

	public void setISO_041_CardAcceptorID(String iSO_041_CardAcceptorID) {
		ISO_041_CardAcceptorID = iSO_041_CardAcceptorID;
	}

	public String getISO_042_Card_Acc_ID_Code() {
		return ISO_042_Card_Acc_ID_Code;
	}

	public void setISO_042_Card_Acc_ID_Code(String iSO_042_Card_Acc_ID_Code) {
		ISO_042_Card_Acc_ID_Code = iSO_042_Card_Acc_ID_Code;
	}

	public String getISO_043_CardAcceptorLoc() {
		return ISO_043_CardAcceptorLoc;
	}

	public void setISO_043_CardAcceptorLoc(String iSO_043_CardAcceptorLoc) {
		ISO_043_CardAcceptorLoc = iSO_043_CardAcceptorLoc;
	}

	public String getISO_044_AddRespData() {
		return ISO_044_AddRespData;
	}

	public void setISO_044_AddRespData(String iSO_044_AddRespData) {
		ISO_044_AddRespData = iSO_044_AddRespData;
	}

	public double getISO_049_TranCurrCode() {
		return ISO_049_TranCurrCode;
	}
	
	
	public void setISO_049_TranCurrCode(double iSO_049_TranCurrCode) {
		ISO_049_TranCurrCode = iSO_049_TranCurrCode;
	}

	public double getISO_051_CardCurrCode() {
		return ISO_051_CardCurrCode;
	}

	public void setISO_051_CardCurrCode(double iSO_051_CardCurrCode) {
		ISO_051_CardCurrCode = iSO_051_CardCurrCode;
	}

	public String getISO_052_PinBlock() {
		return ISO_052_PinBlock;
	}

	public void setISO_052_PinBlock(String iSO_052_PinBlock) {
		ISO_052_PinBlock = iSO_052_PinBlock;
	}

	public String getISO_054_AditionalAmounts() {
		return ISO_054_AditionalAmounts;
	}

	public void setISO_054_AditionalAmounts(String iSO_054_AditionalAmounts) {
		ISO_054_AditionalAmounts = iSO_054_AditionalAmounts;
	}

	public String getISO_055_EMV() {
		return ISO_055_EMV;
	}

	public void setISO_055_EMV(String iSO_055_EMV) {
		ISO_055_EMV = iSO_055_EMV;
	}

	public String getISO_090_OriginalData() {
		return ISO_090_OriginalData;
	}

	public void setISO_090_OriginalData(String iSO_090_OriginalData) {
		ISO_090_OriginalData = iSO_090_OriginalData;
	}

	public String getISO_102_AccountID_1() {
		return ISO_102_AccountID_1;
	}

	public void setISO_102_AccountID_1(String iSO_102_AccountID_1) {
		ISO_102_AccountID_1 = iSO_102_AccountID_1;
	}

	public String getISO_103_AccountID_2() {
		return ISO_103_AccountID_2;
	}

	public void setISO_103_AccountID_2(String iSO_103_AccountID_2) {
		ISO_103_AccountID_2 = iSO_103_AccountID_2;
	}

	public String getISO_104_TranDescription() {
		return ISO_104_TranDescription;
	}

	public void setISO_104_TranDescription(String iSO_104_TranDescription) {
		ISO_104_TranDescription = iSO_104_TranDescription;
	}

	public String getISO_114_ExtendedData() {
		return ISO_114_ExtendedData;
	}

	public void setISO_114_ExtendedData(String iSO_114_ExtendedData) {
		ISO_114_ExtendedData = iSO_114_ExtendedData;
	}

	public String getISO_115_ExtendedData() {
		return ISO_115_ExtendedData;
	}

	public void setISO_115_ExtendedData(String iSO_115_ExtendedData) {
		ISO_115_ExtendedData = iSO_115_ExtendedData;
	}

	public String getISO_120_ExtendedData() {
		return ISO_120_ExtendedData;
	}

	public void setISO_120_ExtendedData(String iSO_120_ExtendedData) {
		ISO_120_ExtendedData = iSO_120_ExtendedData;
	}

	public String getISO_121_ExtendedData() {
		return ISO_121_ExtendedData;
	}

	public void setISO_121_ExtendedData(String iSO_121_ExtendedData) {
		ISO_121_ExtendedData = iSO_121_ExtendedData;
	}

	public String getISO_122_ExtendedData() {
		return ISO_122_ExtendedData;
	}

	public void setISO_122_ExtendedData(String iSO_122_ExtendedData) {
		ISO_122_ExtendedData = iSO_122_ExtendedData;
	}

	public String getISO_123_ExtendedData() {
		return ISO_123_ExtendedData;
	}

	public void setISO_123_ExtendedData(String iSO_123_ExtendedData) {
		ISO_123_ExtendedData = iSO_123_ExtendedData;
	}

	public String getISO_124_ExtendedData() {
		return ISO_124_ExtendedData;
	}

	public void setISO_124_ExtendedData(String iSO_124_ExtendedData) {
		ISO_124_ExtendedData = iSO_124_ExtendedData;
	}
	
	
	
	public Iso8583(){
		
		this.ISO_000_Message_Type = StringUtils.Empty();
		this.ISO_002_PAN = StringUtils.Empty();
		this.ISO_003_ProcessingCode = StringUtils.Empty();
		this.ISO_004_AmountTransaction = 0;
		this.ISO_006_BillAmount = 0;
		this.ISO_007_TransDatetime = new Date();
		this.ISO_008_BillFeeAmount = 0;
		this.ISO_011_SysAuditNumber = StringUtils.Empty();
		this.ISO_012_LocalDatetime = new Date();
		this.ISO_013_LocalDate = new Date();
		this.ISO_015_SettlementDatel = new Date();
		this.ISO_018_MerchantType = StringUtils.Empty();
		this.ISO_019_AcqCountryCode = StringUtils.Empty();
		this.ISO_022_PosEntryMode = StringUtils.Empty();
		this.ISO_023_CardSeq = StringUtils.Empty();
		this.ISO_024_NetworkId = StringUtils.Empty();
		this.ISO_028_TranFeeAmount = 0;
		this.ISO_029_SettlementFee = 0;
		this.ISO_030_ProcFee = 0;
		this.ISO_032_ACQInsID = StringUtils.Empty();
		this.ISO_033_FWDInsID = StringUtils.Empty();
		this.ISO_034_PANExt = StringUtils.Empty();
		this.ISO_035_Track2 = StringUtils.Empty();
		this.ISO_036_Track3 = StringUtils.Empty();
		this.ISO_037_RetrievalReferenceNumber = StringUtils.Empty();
		this.ISO_038_AutorizationNumber = StringUtils.Empty();
		this.ISO_039_ResponseCode = "909";
		this.ISO_039p_ResponseDetail = "TRANSACCION INICIALIZADA";
		this.ISO_041_CardAcceptorID = StringUtils.Empty();
		this.ISO_042_Card_Acc_ID_Code = StringUtils.Empty();
		this.ISO_043_CardAcceptorLoc = StringUtils.Empty();
		this.ISO_044_AddRespData = StringUtils.Empty();
		this.ISO_049_TranCurrCode = 0;
		this.ISO_051_CardCurrCode = 0;
		this.ISO_052_PinBlock = StringUtils.Empty();
		this.ISO_054_AditionalAmounts = StringUtils.Empty();
		this.ISO_055_EMV = StringUtils.Empty();
		this.ISO_090_OriginalData = StringUtils.Empty();
		this.ISO_102_AccountID_1 = StringUtils.Empty();
		this.ISO_103_AccountID_2 = StringUtils.Empty();
		this.ISO_104_TranDescription = StringUtils.Empty();
		this.ISO_114_ExtendedData = StringUtils.Empty();
		this.ISO_115_ExtendedData = StringUtils.Empty();
		this.ISO_120_ExtendedData = StringUtils.Empty();
		this.ISO_121_ExtendedData = StringUtils.Empty();
		this.ISO_122_ExtendedData = StringUtils.Empty();
		this.ISO_123_ExtendedData = StringUtils.Empty();
		this.ISO_124_ExtendedData = StringUtils.Empty();
		this.ISO_BitMap = "FFFFFFFFFFFFFFFF";
	}
	
	public Iso8583(Iso8583_Retrieve iso){
		
		Logger log = null;
		try {
			
			this.ISO_000_Message_Type = iso.getISO_000_Message_Type();
			this.ISO_002_PAN = iso.getISO_002_PAN();
			this.ISO_003_ProcessingCode = iso.getISO_003_ProcessingCode();
			this.ISO_004_AmountTransaction = iso.getISO_004_AmountTransaction();
			this.ISO_006_BillAmount = iso.getISO_006_BillAmount();
			this.ISO_007_TransDatetime = FormatUtils.StringToDate(iso.getISO_007_TransDatetime().substring(0, 19),"yyyy-MM-dd HH:mm:ss");
			this.ISO_008_BillFeeAmount = iso.getISO_008_BillFeeAmount();
			this.ISO_011_SysAuditNumber = iso.getISO_011_SysAuditNumber();
			this.ISO_012_LocalDatetime = FormatUtils.StringToDate(iso.getISO_012_LocalDatetime().substring(0, 19),"yyyy-MM-dd HH:mm:ss");
			this.ISO_013_LocalDate = FormatUtils.StringToDate(iso.getISO_013_LocalDate().substring(0, 19),"yyyy-MM-dd HH:mm:ss");
			this.ISO_015_SettlementDatel = FormatUtils.StringToDate(iso.getISO_015_SettlementDatel().substring(0, 19),"yyyy-MM-dd HH:mm:ss");
			this.ISO_018_MerchantType = iso.getISO_018_MerchantType();
			this.ISO_019_AcqCountryCode = iso.getISO_019_AcqCountryCode();
			this.ISO_022_PosEntryMode = iso.getISO_022_PosEntryMode();
			this.ISO_023_CardSeq = iso.getISO_023_CardSeq();
			this.ISO_024_NetworkId = iso.getISO_024_NetworkId();
			this.ISO_028_TranFeeAmount = iso.getISO_028_TranFeeAmount();
			this.ISO_029_SettlementFee = iso.getISO_029_SettlementFee();
			this.ISO_030_ProcFee = iso.getISO_030_ProcFee();
			this.ISO_032_ACQInsID = iso.getISO_032_ACQInsID();
			this.ISO_033_FWDInsID = iso.getISO_033_FWDInsID();
			this.ISO_034_PANExt = iso.getISO_034_PANExt();
			this.ISO_035_Track2 = iso.getISO_035_Track2();
			this.ISO_036_Track3 = iso.getISO_036_Track3();
			this.ISO_037_RetrievalReferenceNumber = iso.getISO_037_RetrievalReference();
			this.ISO_038_AutorizationNumber = iso.getISO_038_AutorizationNumber();
			this.ISO_039_ResponseCode = iso.getISO_039_ResponseCode();
			this.ISO_039p_ResponseDetail = iso.getISO_039p_ResponseDetail();
			this.ISO_041_CardAcceptorID = iso.getISO_041_CardAcceptorID();
			this.ISO_042_Card_Acc_ID_Code = iso.getISO_042_Card_Acc_ID_Code();
			this.ISO_043_CardAcceptorLoc = iso.getISO_043_CardAcceptorLoc();
			this.ISO_044_AddRespData = iso.getISO_044_AddRespData();
			this.ISO_049_TranCurrCode = iso.getISO_049_TranCurrCode();
			this.ISO_051_CardCurrCode = iso.getISO_051_CardCurrCode();
			this.ISO_052_PinBlock = iso.getISO_052_PinBlock();
			this.ISO_054_AditionalAmounts = iso.getISO_054_AditionalAmounts();
			this.ISO_055_EMV = iso.getISO_055_EMV();
			this.ISO_090_OriginalData = iso.getISO_090_OriginalData();
			this.ISO_102_AccountID_1 = iso.getISO_102_AccountID_1();
			this.ISO_103_AccountID_2 = iso.getISO_103_AccountID_2();
			this.ISO_104_TranDescription = iso.getISO_104_TranDescription();
			this.ISO_114_ExtendedData = iso.getISO_114_ExtendedData();
			this.ISO_115_ExtendedData = iso.getISO_115_ExtendedData();
			this.ISO_120_ExtendedData = iso.getISO_120_ExtendedData();
			this.ISO_121_ExtendedData = iso.getISO_121_ExtendedData();
			this.ISO_122_ExtendedData = iso.getISO_122_ExtendedData();
			this.ISO_123_ExtendedData = iso.getISO_123_ExtendedData();
			this.ISO_124_ExtendedData = iso.getISO_124_ExtendedData();		
			this.ISO_BitMap = iso.getISO_BitMap();
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR Iso8583(wIso8583 iso)", TypeMonitor.error, e);
		}
	}
	
    public Iso8583(wIso8583 iso){
		
		Logger log = null;
		try {
			
			this.ISO_000_Message_Type = iso.getISO_000_Message_Type();
			this.ISO_002_PAN = iso.getISO_002_PAN();
			this.ISO_003_ProcessingCode = iso.getISO_003_ProcessingCode();
			this.ISO_004_AmountTransaction = iso.getISO_004_AmountTransaction();
			this.ISO_006_BillAmount = iso.getISO_006_BillAmount();
			this.ISO_007_TransDatetime = iso.getISO_007_TransDatetime();
			this.ISO_008_BillFeeAmount = iso.getISO_008_BillFeeAmount();
			this.ISO_011_SysAuditNumber = iso.getISO_011_SysAuditNumber();
			this.ISO_012_LocalDatetime = iso.getISO_012_LocalDatetime();
			this.ISO_013_LocalDate = iso.getISO_013_LocalDate();
			this.ISO_015_SettlementDatel = iso.getISO_015_SettlementDatel();
			this.ISO_018_MerchantType = iso.getISO_018_MerchantType();
			this.ISO_019_AcqCountryCode = iso.getISO_019_AcqCountryCode();
			this.ISO_022_PosEntryMode = iso.getISO_022_PosEntryMode();
			this.ISO_023_CardSeq = iso.getISO_023_CardSeq();
			this.ISO_024_NetworkId = iso.getISO_024_NetworkId();
			this.ISO_028_TranFeeAmount = iso.getISO_028_TranFeeAmount();
			this.ISO_029_SettlementFee = iso.getISO_029_SettlementFee();
			this.ISO_030_ProcFee = iso.getISO_030_ProcFee();
			this.ISO_032_ACQInsID = iso.getISO_032_ACQInsID();
			this.ISO_033_FWDInsID = iso.getISO_033_FWDInsID();
			this.ISO_034_PANExt = iso.getISO_034_PANExt();
			this.ISO_035_Track2 = iso.getISO_035_Track2();
			this.ISO_036_Track3 = iso.getISO_036_Track3();
			this.ISO_037_RetrievalReferenceNumber = iso.getISO_037_RetrievalReferenceNumber();
			this.ISO_038_AutorizationNumber = iso.getISO_038_AutorizationNumber();
			this.ISO_039_ResponseCode = iso.getISO_039_ResponseCode();
			this.ISO_039p_ResponseDetail = iso.getISO_039p_ResponseDetail();
			this.ISO_041_CardAcceptorID = iso.getISO_041_CardAcceptorID();
			this.ISO_042_Card_Acc_ID_Code = iso.getISO_042_Card_Acc_ID_Code();
			this.ISO_043_CardAcceptorLoc = iso.getISO_043_CardAcceptorLoc();
			this.ISO_044_AddRespData = iso.getISO_044_AddRespData();
			this.ISO_049_TranCurrCode = iso.getISO_049_TranCurrCode();
			this.ISO_051_CardCurrCode = iso.getISO_051_CardCurrCode();
			this.ISO_052_PinBlock = iso.getISO_052_PinBlock();
			this.ISO_054_AditionalAmounts = iso.getISO_054_AditionalAmounts();
			this.ISO_055_EMV = iso.getISO_055_EMV();
			this.ISO_090_OriginalData = iso.getISO_090_OriginalData();
			this.ISO_102_AccountID_1 = iso.getISO_102_AccountID_1();
			this.ISO_103_AccountID_2 = iso.getISO_103_AccountID_2();
			this.ISO_104_TranDescription = iso.getISO_104_TranDescription();
			this.ISO_114_ExtendedData = iso.getISO_114_ExtendedData();
			this.ISO_115_ExtendedData = iso.getISO_115_ExtendedData();
			this.ISO_120_ExtendedData = iso.getISO_120_ExtendedData();
			this.ISO_121_ExtendedData = iso.getISO_121_ExtendedData();
			this.ISO_122_ExtendedData = iso.getISO_122_ExtendedData();
			this.ISO_123_ExtendedData = iso.getISO_123_ExtendedData();
			this.ISO_124_ExtendedData = iso.getISO_124_ExtendedData();		
			this.ISO_BitMap = iso.getISO_BitMap();
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR Iso8583(wIso8583 iso)", TypeMonitor.error, e);
		}
	}
	
	public Iso8583(Iso8583Binary isoBin){
		
		this();
		Logger log = null;
		try {
			
			String mti = isoBin.getMti();
			switch (mti) {
			case "0220":
			case "0200":
				this.ISO_000_Message_Type = "1200";
				break;
			case "0800":
				this.ISO_000_Message_Type = "1800";
				break;
			case "0420":
			case "0400":
				this.ISO_000_Message_Type = "1400";
				break;
			case "0300":
				this.ISO_000_Message_Type = "1300";
				break;
			default:
				break;
			}
			byte[] prBitmap = isoBin.getPrimaryBitmap();
			
			//int lowNibble = prBitmap[0] & 0x0f; // segundo nibble
			int hiNibble = (prBitmap[0] >> 4) & 0x0f; // primer nibble
			if(hiNibble >= 8){
				this.ISO_BitMap = DatatypeConverter.printHexBinary(isoBin.getPrimaryBitmap());
				this.ISO_BitMap = this.ISO_BitMap + DatatypeConverter.printHexBinary(isoBin.getDe1_SecondaryBitmap());
			}else
				this.ISO_BitMap = DatatypeConverter.printHexBinary(prBitmap);
			this.ISO_002_PAN = isoBin.getDe2_PAN();
			
			if(this.ISO_000_Message_Type.equals("1800")){
				this.ISO_003_ProcessingCode = StringUtils.IsNullOrEmpty(isoBin.getDe3_ProcCode().trim())? "000000"
	                  				    :isoBin.getDe3_ProcCode(); //Mensaje de Control
			}else{
				
				this.ISO_003_ProcessingCode = isoBin.getDe3_ProcCode();
			}
			
			
			this.ISO_004_AmountTransaction = (Double.parseDouble(String.valueOf(Integer
					                        .parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe4_AmtTxn())?"0"
					                        :isoBin.getDe4_AmtTxn())))/100);
			this.ISO_006_BillAmount = (Double.parseDouble(String.valueOf(Integer
                    					.parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe6_AmtCardhBill()) ? "0"
                    							: isoBin.getDe6_AmtCardhBill())))/100);
			this.ISO_007_TransDatetime = FormatUtils.StringToDateIso(FormatUtils.DateToString(new Date(), "yyyy") + 
					                   isoBin.getDe7_TransDttm(), "yyyyMMddHHmmss");
			this.ISO_008_BillFeeAmount = (Double.parseDouble(String.valueOf(Integer
                    .parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe8_AmtCardhBillFee())?"0"
                    :isoBin.getDe8_AmtCardhBillFee())))/100);
			this.ISO_011_SysAuditNumber = isoBin.getDe11_STAN();
			this.ISO_012_LocalDatetime =  FormatUtils.StringToDateIso(FormatUtils.DateToString(new Date(), "yyyy")  + 
	                   isoBin.getDe13_DateLocal() + isoBin.getDe12_TimeLocal(), "yyyyMMddHHmmss");
			this.ISO_013_LocalDate = FormatUtils.StringToDateIso(FormatUtils.DateToString(new Date(), "yyyy") + 
	                   			isoBin.getDe13_DateLocal(), "yyyyMMdd");
			this.ISO_015_SettlementDatel = FormatUtils.StringToDateIso(FormatUtils.DateToString(new Date(), "yyyy") + 
           			isoBin.getDe15_DateSetl(), "yyyyMMdd");
			
			if(this.ISO_000_Message_Type.equals("1800")){
				this.ISO_018_MerchantType = StringUtils.IsNullOrEmpty(isoBin.getDe18_MerchType()
						                .replace("0000", ""))? "0001"
	                  				    :isoBin.getDe18_MerchType(); //Mensaje de Control
			}else{
				
				this.ISO_018_MerchantType = isoBin.getDe18_MerchType();
			}
			
			this.ISO_019_AcqCountryCode = isoBin.getDe19_AcqInstCtryCode();
			this.ISO_022_PosEntryMode = isoBin.getDe20_PriAccNumExtCtryCode();
			this.ISO_023_CardSeq = isoBin.getDe23_CardSeqNo();
			
			if(this.ISO_000_Message_Type.equals("1800")){
				this.ISO_024_NetworkId = StringUtils.IsNullOrEmpty(isoBin.getDe24_NetIntlId()
						.replace("000", ""))? "551"
					    :isoBin.getDe24_NetIntlId(); //Mensaje de Control
			}else{
				
				this.ISO_024_NetworkId = isoBin.getDe24_NetIntlId();
			}
			this.ISO_028_TranFeeAmount = (Double.parseDouble(String.valueOf(Integer
                    .parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe28_AmtTxnFee())?"0"
                    :isoBin.getDe28_AmtTxnFee())))/100);
			this.ISO_029_SettlementFee = (Double.parseDouble(String.valueOf(Integer
                    .parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe29_AmtSettleFee())?"0"
                    :isoBin.getDe29_AmtSettleFee())))/100);
			this.ISO_030_ProcFee = (Double.parseDouble(String.valueOf(Integer
                    .parseInt(StringUtils.IsNullOrEmpty(isoBin.getDe30_AmtTxnProcFee())?"0"
                    :isoBin.getDe30_AmtTxnProcFee())))/100);
			this.ISO_032_ACQInsID = isoBin.getDe32_AcqInstIdCode();
			this.ISO_033_FWDInsID = isoBin.getDe33_FwdInstIdCode();
			this.ISO_034_PANExt = isoBin.getDe34_PanExt();
			this.ISO_035_Track2 = GeneralUtils.isEmptyByteArray(isoBin.getDe35_Track2()) 
         		   				 ? StringUtils.Empty() :new String(isoBin.getDe35_Track2());
			this.ISO_036_Track3 =  GeneralUtils.isEmptyByteArray(isoBin.getDe36_Track3()) 
	   				             ? StringUtils.Empty() :new String(isoBin.getDe36_Track3());
			this.ISO_037_RetrievalReferenceNumber = isoBin.getDe37_RetRefNo();
			this.ISO_038_AutorizationNumber = isoBin.getDe38_AuthIdentResp();
			this.ISO_039_ResponseCode = StringUtils.padRight(isoBin.getDe39_RespCode(),3,"0");
			this.ISO_041_CardAcceptorID = isoBin.getDe41_CardAcptTermId();
			this.ISO_042_Card_Acc_ID_Code = isoBin.getDe42_CardAcptIdCode();
			this.ISO_043_CardAcceptorLoc = isoBin.getDe43_CardAcptNameLoc();
			this.ISO_044_AddRespData = GeneralUtils.isEmptyByteArray(isoBin.getDe44_AddtRespData()) 
	   				                  ? StringUtils.Empty() : new String(isoBin.getDe44_AddtRespData());
			this.ISO_049_TranCurrCode = Double.parseDouble(isoBin.getDe49_CurCodeTxn());
			this.ISO_052_PinBlock = DatatypeConverter.printHexBinary(isoBin.getDe52_PinData());
			this.ISO_054_AditionalAmounts = isoBin.getDe54_AddtlAmts();
			this.ISO_055_EMV = GeneralUtils.isEmptyByteArray(isoBin.getDe55_ResIso()) 
                    		   ? StringUtils.Empty() : new String(isoBin.getDe55_ResIso());
			this.ISO_090_OriginalData = isoBin.getDe90_OrigDataElem();
			this.ISO_102_AccountID_1 = GeneralUtils.isEmptyByteArray(isoBin.getDe102_AcctId1()) 
         		                       ? StringUtils.Empty() : new String(isoBin.getDe102_AcctId1());
			this.ISO_103_AccountID_2 = GeneralUtils.isEmptyByteArray(isoBin.getDe103_AcctId2()) 
                                       ? StringUtils.Empty() : new String(isoBin.getDe103_AcctId2());
			this.ISO_104_TranDescription = GeneralUtils.isEmptyByteArray(isoBin.getDe104_TxnDesc()) 
					                      ? StringUtils.Empty() : new String(isoBin.getDe104_TxnDesc());
			this.ISO_114_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe114_ResvNat()) 
					                      ? StringUtils.Empty() : new String(isoBin.getDe114_ResvNat());
			this.ISO_115_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe115_ResvNat()) 
					                      ? StringUtils.Empty() : new String(isoBin.getDe115_ResvNat());
			this.ISO_120_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe120_ResvPriv()) 
					                      ? StringUtils.Empty(): new String(isoBin.getDe120_ResvPriv());
			this.ISO_121_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe121_ResvPriv()) 
					                      ? StringUtils.Empty(): new String(isoBin.getDe121_ResvPriv());
			this.ISO_122_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe122_ResvPriv()) 
					                      ? StringUtils.Empty(): new String(isoBin.getDe122_ResvPriv());
			this.ISO_123_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe123_ResvPriv()) 
					                      ? StringUtils.Empty(): new String(isoBin.getDe123_ResvPriv());
			this.ISO_124_ExtendedData = GeneralUtils.isEmptyByteArray(isoBin.getDe124_ResvPriv()) 
					                      ? StringUtils.Empty(): new String(isoBin.getDe124_ResvPriv());
	        if(this.ISO_000_Message_Type.equals("1800")){
	        	
	        	this.ISO_115_ExtendedData = isoBin.getDe70_NetMgtInfoCode();
	        }
	        	
			
			
		} catch (Exception e) {
			
			this.ISO_039_ResponseCode = "999";
			this.ISO_039p_ResponseDetail = GeneralUtils.ExceptionToString("EROR CONSTRUCTOR ISO ", e, false);
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR Iso8583(Iso8583Binary isoBin)", TypeMonitor.error, e);
			
		}
	}
	
	public <T> Iso8583(T obj){
		
		Logger log = null;
		DTORequestDebit debit = null;
		DTORequestCredit credit = null;
		DTORequestRevert rever = null;
		DTORequestIsValidAccount valid = null;
		
		this.ISO_039_ResponseCode = "000";
		this.ISO_000_Message_Type = "1200";
		this.ISO_007_TransDatetime = new Date();
		this.ISO_012_LocalDatetime = new Date();
		this.ISO_013_LocalDate = new Date();
		this.ISO_015_SettlementDatel = new Date();
		this.ISO_018_MerchantType = "0001";
		this.ISO_024_NetworkId = "555557";
		this.ISO_041_CardAcceptorID = "WEB-BCE";
		try {
			
			switch (obj.getClass().getName()) {
			case "com.fitbank.middleware.electroniccash.DTORequestDebit":
				debit = (DTORequestDebit)obj;
				String validated = debit.ValidateMethod(debit);
				if(validated.startsWith("OK")){
					if(debit.getAccountType().equalsIgnoreCase("CA"))
						this.ISO_003_ProcessingCode = "011000";
					else if (debit.getAccountType().equalsIgnoreCase("CC"))
						this.ISO_003_ProcessingCode = "012000";
					else this.ISO_003_ProcessingCode = "-1";
					
					this.ISO_004_AmountTransaction = debit.getAmount();
					this.ISO_011_SysAuditNumber = String.valueOf(debit.getTransactionSequenceId());
					this.ISO_002_PAN = debit.getMsisdnSource();
					this.ISO_102_AccountID_1 = debit.getAccountId();
					this.ISO_114_ExtendedData = debit.getExtraInfo();
					this.ISO_124_ExtendedData = debit.getAccountType();
					
				}else{
						
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = validated;
				}
				
				break;
			case "com.fitbank.middleware.electroniccash.DTORequestCredit":
				
				credit = (DTORequestCredit) obj;
				String validate = credit.ValidateMethod(credit);
				if(validate.startsWith("OK")){
					if(credit.getAccountType().equalsIgnoreCase("CA"))
						this.ISO_003_ProcessingCode = "201000";
					else if (credit.getAccountType().equalsIgnoreCase("CC")) 
						this.ISO_003_ProcessingCode = "202000";
					else this.ISO_003_ProcessingCode = "-1";
					
					this.ISO_004_AmountTransaction = credit.getAmount();
					this.ISO_011_SysAuditNumber = String.valueOf(credit.getTransactionSequenceId());
					this.ISO_002_PAN = credit.getMsisdnSource();
					this.ISO_102_AccountID_1 = credit.getAccountId();
					this.ISO_114_ExtendedData = credit.getExtraInfo();
					this.ISO_124_ExtendedData = credit.getAccountType();
				}
				else{
					
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = validate;
				}
				break;
			case "com.fitbank.middleware.electroniccash.DTORequestIsValidAccount":
				
				valid = (DTORequestIsValidAccount) obj;
				String validatea = valid.ValidateMethod(valid);
				if(validatea.startsWith("OK")){
					if(valid.getAccountType().equalsIgnoreCase("CA"))
						this.ISO_003_ProcessingCode = "600001";
					else if (valid.getAccountType().equalsIgnoreCase("CC")) 
						this.ISO_003_ProcessingCode = "600001";
					else 
						this.ISO_003_ProcessingCode = "-1"; 
					
					this.ISO_011_SysAuditNumber = GeneralUtils.GetSecuencial(8);
					this.ISO_102_AccountID_1 = valid.getAccountId();
					this.ISO_124_ExtendedData = valid.getAccountType();
				}
				else {
					
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = validatea;
				}
				break;
			case "com.fitbank.middleware.electroniccash.DTORequestRevert":
				
				System.out.println("Entro en switch DTORequestRevert...1");
				rever = (DTORequestRevert)obj;
				System.out.println("Entro en switch DTORequestRevert...2");
				String validater = rever.ValidateMethod(rever);
				if(validater.startsWith("OK")){
					System.out.println("Entro en switch DTORequestRevert...3.1");
					this.ISO_011_SysAuditNumber = String.valueOf(rever.getTransactionSequenceId());
					this.ISO_000_Message_Type = "1400";
					
				}else {
					System.out.println("Entro en switch DTORequestRevert...3.2");
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = validater;
				}
				break;
				
			default:
				break;
			}
				
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR <T> Iso8583(T obj)", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "909";
			this.ISO_039p_ResponseDetail = GeneralUtils.ExceptionToString("ERROR CONSTRUCTOR ISO<T> ", e, true);
		} 
	}
	/*Enrolamiento*/
	public Iso8583(com.belejanor.switcher.bimo.pacs.camt_998_211.Document document){
		
		this();
		Date date = null;
		Logger log = null;
		try {
			
			this.ISO_000_Message_Type = "1200";
			this.ISO_003_ProcessingCode = "781000";
			this.ISO_024_NetworkId = "555522";
			if(document != null){
				
				if(document.getHeader()  != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											.getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
												.getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
												.getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
													.getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
													.getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
														.getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													this.ISO_032_ACQInsID = document.getHeader().getSender();
													
													if(!StringUtils.IsNullOrEmpty(document.getHeader()
															             .getReceiver())){
														
													 if(document.getHeader().getReceiver().equalsIgnoreCase(MemoryGlobal.IdBIMOEfi)){
														 
															this.ISO_033_FWDInsID = document.getHeader().getReceiver();
														
															if(document.getHeader().getMge() != null){
																
																if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getType())){
																	
																	this.ISO_BitMap = document.getHeader()
																              .getMge().getType();
																	
																	if(document.getHeader()
																              .getMge().getRoR() != null){
																		if(document.getHeader()
																	              .getMge().getRoR() == RoRCod.REQ){
																			
																			this.ISO_036_Track3 = document.getHeader()
																		              .getMge().getRoR().toString();
																			
																			if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getIdMge())){
																				this.ISO_037_RetrievalReferenceNumber = document.getHeader()
																			              .getMge().getIdMge();
																				
																				if(document.getHeader()
																			              .getMge().getOpeDate() != null){
																			            	  
																					date = document.getHeader()
																				              .getMge().getOpeDate().toGregorianCalendar().getTime();
																					this.ISO_007_TransDatetime = date;
																					
																					boolean isPss = document.getHeader().isPssblDplct();
																					this.ISO_122_ExtendedData = String.valueOf(isPss) + "|";
																					
																					if(document.getHeader().getPrty() != null){
																						
																						switch (document.getHeader().getPrty()) {
																						
																						case URGT:
																						case HIGH:
																						case LOWW:	
																						case NORM:	
																							
																							this.ISO_122_ExtendedData += String.valueOf
																							           (document.getHeader().getPrty());
																							
																							break;	
																						default:
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																															  .toUpperCase();
																							break;
																						}
																						
																						if(document.getPrtryMsg() != null){
																							
																							if(document.getPrtryMsg().getGrpHdr() != null){
																								
																								if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																										            .getGrpHdr().getMsgId())){
																									
																									this.ISO_011_SysAuditNumber = document.getPrtryMsg()
																								            .getGrpHdr().getMsgId(); 
																									
																									if(document.getPrtryMsg()
																								            .getGrpHdr().getCreDtTm() != null){
																										
																									    date = document.getHeader()
																									              .getMge().getOpeDate().toGregorianCalendar().getTime();
																										this.ISO_012_LocalDatetime = date;
																										
																										if(document.getPrtryMsg()
																									            .getGrpHdr().getNbOfTxs() != null){
																											
																											this.ISO_006_BillAmount = document.getPrtryMsg()
																										            .getGrpHdr().getNbOfTxs().doubleValue();
																											if(this.ISO_006_BillAmount >= 1){
																												
																												if(document.getPrtryMsg().getGrpHdr().getSttInf() != null){
																													
																													switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
																													
																													case CLRG:
																													case COVE:
																													case INGA:
																													case INDA:
																														
																															this.ISO_035_Track2 = String.valueOf(document.getPrtryMsg().getGrpHdr()
																																	           .getSttInf().getSttlmMtd());
																														
																														break;
	
																													default:
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																						  .toUpperCase();
																														
																														break;
																													}
																													
																													if(document.getPrtryMsg().getGrpHdr().getInstdAgt() != null){
																														
																														if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId() != null){
																															
																															if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																															          .getFinInstnId().getOthr() != null){
																																
																																if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId().getOthr().getId())){
																																	/*Validacion de codigo Cooperativa BIC*/
																																	this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																	          .getFinInstnId().getOthr().getId() + "|";
																																	
																																	if(document.getPrtryMsg().getGrpHdr().getInstgAgt() != null){
																																		
																																		if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId() != null){
																																			
																																			if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId().getOthr() != null){
																																				
																																				if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr()
																																						.getInstgAgt()
																																				      .getFinInstnId().getOthr().getId())){
																																					/*Validacion de codigo Entidad BANRED*/
																																					
																																					this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																					          .getFinInstnId().getOthr().getId();
																																					
																																					if(document.getPrtryMsg().getAcctEnroll()!= null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																								.getAcctEnroll().getPartyNm())){
																																							
																																							this.ISO_034_PANExt = document.getPrtryMsg()
																																									.getAcctEnroll().getPartyNm();
																																							if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																									.getAcctEnroll().getPartyId())){
																																								
																																								this.ISO_002_PAN = document.getPrtryMsg()
																																										.getAcctEnroll().getPartyId();
																																								if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																										.getAcctEnroll().getPartyDocType())){
																																									this.ISO_022_PosEntryMode = document.getPrtryMsg()
																																											.getAcctEnroll().getPartyDocType();

																																									if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																											.getAcctEnroll().getPINBimo())){
																																										
																																										this.ISO_052_PinBlock = document.getPrtryMsg()
																																												.getAcctEnroll().getPINBimo();
																																										
																																										if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																												.getAcctEnroll().getPartyPerfil())){
																																											this.ISO_115_ExtendedData = document.getPrtryMsg()
																																													.getAcctEnroll().getPartyPerfil();
																																											
																																											if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																													.getAcctEnroll().getPartyDocCtry())){
																																												 
																																												 this.ISO_019_AcqCountryCode = document.getPrtryMsg()
																																															.getAcctEnroll().getPartyDocCtry();
																																											 }
																																												 
																																												 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																															.getAcctEnroll().getPartyPrdType())){
																																													 
																																													 this.ISO_043_CardAcceptorLoc = document.getPrtryMsg()
																																																.getAcctEnroll().getPartyPrdType();
																																													 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																.getAcctEnroll().getPartyPhone())){
																																														 
																																														 this.ISO_023_CardSeq = document.getPrtryMsg()
																																																	.getAcctEnroll().getPartyPhone();
																																														 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																	.getAcctEnroll().getPartyEmail())){
																																															 this.ISO_123_ExtendedData = document.getPrtryMsg()
																																																		.getAcctEnroll().getPartyEmail() + "|";
																																														 }else{
																																															 
																																															 /*En Fit1 si no mando el mail da error mando generico*/
																																															 this.ISO_123_ExtendedData = "enrolamientos@gmail.com|";
																																														 }
																																															 if(document.getPrtryMsg()
																																																		.getAcctEnroll().getPartyBrdDt() != null){
																																																 
																																																 this.ISO_114_ExtendedData = FormatUtils.DateToString(document.getPrtryMsg()
																																																			.getAcctEnroll().getPartyBrdDt().toGregorianCalendar()
																																																			.getTime(),"YYYY-MM-dd");
																																																 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																			.getAcctEnroll().getAcctInfo())){
																																																	 
																																																	 this.ISO_102_AccountID_1 = document.getPrtryMsg()
																																																				.getAcctEnroll().getAcctInfo();
																																																	 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																				.getAcctEnroll().getGender())){
																																																		 
																																																		 this.ISO_123_ExtendedData += document.getPrtryMsg()
																																																					.getAcctEnroll().getGender() + "|";
																																																	 }else{
																																																		 
																																																		 this.ISO_123_ExtendedData += "HOMBRE|";
																																																	 }
																																																		 
																																																		 if(document.getPrtryMsg()
																																																					.getAcctEnroll().getPartyIssueDate() != null){
																																																			 
																																																			 this.ISO_123_ExtendedData += FormatUtils.DateToString(document.getPrtryMsg()
																																																					.getAcctEnroll().getPartyIssueDate()
																																																					.toGregorianCalendar().getTime(), "YYYY-MM-dd") + "|";
																																																		 }else{
																																																			 
																																																			 this.ISO_123_ExtendedData += FormatUtils.DateToString(new Date(), "YYYY-MM-dd") + "|";
																																																		 }
																																																			 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																						.getAcctEnroll().getMaritalSts())){
																																																				 
																																																				 this.ISO_123_ExtendedData += document.getPrtryMsg()
																																																							.getAcctEnroll().getMaritalSts() + "|";
																																																			  }else{
																																																				  
																																																				  this.ISO_123_ExtendedData += "SOLTERO|";
																																																			  }
																																																				 
																																																				 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																							.getAcctEnroll().getOccupation())){
																																																					 
																																																					 this.ISO_123_ExtendedData += document.getPrtryMsg()
																																																								.getAcctEnroll().getOccupation() + "|";
																																																				 }else{
																																																					 
																																																					 this.ISO_123_ExtendedData += "EMPLEADO PRIVADO|";
																																																				 }
																																																					 
																																																					 if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																																								.getAcctEnroll().getInstruction())){
																																																						 
																																																						 this.ISO_123_ExtendedData += document.getPrtryMsg()
																																																									.getAcctEnroll().getInstruction();
																																																					 }else{
																																																						 
																																																						 this.ISO_123_ExtendedData += "SECUNDARIA";
																																																					 }
																																																						 
																																																						 if(document.getPrtryMsg()
																																																									.getAcctEnroll().getSttlmDt() != null){
																																																							 
																																																							 this.ISO_015_SettlementDatel = document.getPrtryMsg()
																																																										.getAcctEnroll().getSttlmDt().toGregorianCalendar()
																																																										.getTime();
																																																							 
																																																							 this.ISO_039_ResponseCode = "004";
																																																							 
																																																							 /*Exit*/
																																																							 
																																																						 }else{
																																																							 
																																																							 this.ISO_039_ResponseCode = "999";
																																																							 this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																																							    		+ "AcctEnroll/SttlmDt ES NULO O VACIO"
																																																							    		 .toUpperCase(); 
																																																						 }
																																																						 
																																																	 
																																																 }else{
																																																	 
																																																	 this.ISO_039_ResponseCode = "999";
																																																	 this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																																	    		+ "AcctEnroll/AcctInfo ES NULO O VACIO"
																																																	    		 .toUpperCase(); 
																																																 }
																																																 
																																															 }else{
																																																 
																																																 this.ISO_039_ResponseCode = "999";
																																																 this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																																    		+ "AcctEnroll/PartyBrdDt ES NULO O VACIO"
																																																    		 .toUpperCase();
																																															 }
																																															 
																																													
																																													 }else{
																																														 
																																														 this.ISO_039_ResponseCode = "999";
																																														 this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																														    		+ "AcctEnroll/PartyPhone ES NULO O VACIO"
																																														    		 .toUpperCase();
																																													 }
																																													 
																																												 }else{
																																													 
																																													 this.ISO_039_ResponseCode = "999";
																																													 this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																													    		+ "AcctEnroll/PartyPrdType ES NULO O VACIO"
																																													    		 .toUpperCase();
																																												 }
																																												
																																										}else{
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																										    		+ "AcctEnroll/PartyPerfil ES NULO O VACIO"
																																										    		 .toUpperCase();
																																										}
																																										
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																									    		+ "AcctEnroll/PINBimo ES NULO O VACIO"
																																									    		 .toUpperCase();
																																									}
																																									
																																								}else{
																																									
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																								    		+ "AcctEnroll/PartyDocType ES NULA O VACIO"
																																								    		 .toUpperCase();
																																								}
																																								
																																							}else{
																																								
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																							    		+ "AcctEnroll/PartyId ES NULA O VACIO"
																																							    		 .toUpperCase();
																																							}
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																						    		+ "AcctEnroll/PartyNm ES NULA O VACIO"
																																						    		 .toUpperCase();
																																						}
																																						
																																					}else{
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																					    		+ "AcctEnroll ES NULA"
																																					    		 .toUpperCase();
																																					}
																																					
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULO O VACIO"
																																				    		 .toUpperCase();
																																				}
																																				
																																			}else{
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																			    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																			    		 .toUpperCase();
																																			}
																																			
																																		}else{
																																			
																																			this.ISO_039_ResponseCode = "999";
																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																		    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																		    		 .toUpperCase();
																																		}
																																		
																																	}else{
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																	    		+ "GrpHdr/InstgAgt ES NULA"
																																	    		 .toUpperCase();
																																	}
																																	
																																}else{
																																
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																    		+ "GrpHdr/FinInstnId/Othr/Id ES NULO O VACIO"
																																									  .toUpperCase();
																																}
																																
																															}else{
																																
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																															    		+ "GrpHdr/FinInstnId/Othr ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																														    		+ "GrpHdr/FinInstnId ES NULA"
																																							  .toUpperCase();
																														}
																														
																													}else{
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/InstdAgt ES NULA"
																																						  .toUpperCase();
																													}
																													
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																												    		+ "GrpHdr/SttInf ES NULA"
																																					  .toUpperCase();
																												}
																																																					
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																											    		+ "GrpHdr/NbOfTxs ES INCORRECTO"
																																				  .toUpperCase();
																											}
																											
																										}else{
																											
																											this.ISO_039_ResponseCode = "999";
																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																										    		+ "GrpHdr/NbOfTxs ES NULO"
																																			  .toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																									    		+ "GrpHdr/CreDtTm ES INCORRECTO"
																																		  .toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																								    		+ "GrpHdr/MsgId ES INCORRECTO"
																																	  .toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg/GrpHdr ES NULA"
																																  .toUpperCase();
																							}
																							
																						}else{
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg ES NULA"
																															  .toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Prty ES NULO"
																														  .toUpperCase();
																					}
																					
																			     }else {
																					
																			    	 this.ISO_039_ResponseCode = "067";
																				     this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/OpeDate ES NULO"
																													  .toUpperCase();
																				 }
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/IdMge NO ES CORRECTO"
																											  .toUpperCase();
																			}
																			
																		}else{
																			
																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR NO ES CORRECTO"
																										  .toUpperCase();
																		}
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR ES NULA O VACIO"
																									  .toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/Type ES NULA O VACIO"
																								  .toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Mge ES NULA"
																							  .toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "209";
															this.ISO_039p_ResponseDetail = "	TORA NO COINCIDE CON "
																	+ "LA ASIGNADA POR BIMO";
															
													    }
													 
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver"
																+ " ES NULO o VACIO".toUpperCase();
													}
													///aqui....validacion fin receiver MemoryGlobal.IdBIMOEfi
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender"
															+ " ES NULO o VACIO".toUpperCase();
												}
													
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId"
														+ " ES NULO o VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ"
													+ " ES NULO o VACIO".toUpperCase();
										}
																				
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ"
												+ " ES NULO o VACIO".toUpperCase();
									}
									
								}else{
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Service ES NULA"
																  .toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App"
										+ " ES NULO o VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel"
									+ " ES NULO o VACIO".toUpperCase();
						}
						
					}else{
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
													  .toUpperCase();
					}
					
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA";
				}
				
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA camt.998.211 ES NULA";
			}
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR ENROLAMIENTO BIMO ", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
	}
	/*Des-Enrolamiento*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.camt_998_221.Document document){
		
		this();
		Date date = null;
		Logger log = null;
		try {
			
			this.ISO_000_Message_Type = "1200";
			this.ISO_003_ProcessingCode = "791000";
			this.ISO_024_NetworkId = "555522";
			if(document != null){
				
				if(document.getHeader()  != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											.getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
												.getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
												.getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
													.getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
													.getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
														.getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													this.ISO_032_ACQInsID = document.getHeader().getSender();
													
													if(!StringUtils.IsNullOrEmpty(document.getHeader()
															             .getReceiver())){
														
													 if(document.getHeader().getReceiver().equalsIgnoreCase(MemoryGlobal.IdBIMOEfi)){
														 
															this.ISO_033_FWDInsID = document.getHeader().getReceiver();
														
															if(document.getHeader().getMge() != null){
																
																if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getType())){
																	
																	this.ISO_BitMap = document.getHeader()
																              .getMge().getType();
																	
																	if(document.getHeader()
																              .getMge().getRoR() != null){
																		if(document.getHeader()
																	              .getMge().getRoR() == com.belejanor.switcher.bimo.pacs.camt_998_221.RoRCod.REQ){
																			
																			this.ISO_036_Track3 = document.getHeader()
																		              .getMge().getRoR().toString();
																			
																			if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getIdMge())){
																				this.ISO_037_RetrievalReferenceNumber = document.getHeader()
																			              .getMge().getIdMge();
																				
																				if(document.getHeader()
																			              .getMge().getOpeDate() != null){
																			            	  
																					date = document.getHeader()
																				              .getMge().getOpeDate().toGregorianCalendar().getTime();
																					this.ISO_007_TransDatetime = date;
																					
																					boolean isPss = document.getHeader().isPssblDplct();
																					this.ISO_122_ExtendedData = String.valueOf(isPss) + "|";
																					
																					if(document.getHeader().getPrty() != null){
																						
																						switch (document.getHeader().getPrty()) {
																						
																						case URGT:
																						case HIGH:
																						case LOWW:	
																						case NORM:	
																							
																							this.ISO_122_ExtendedData += String.valueOf
																							           (document.getHeader().getPrty());
																							
																							break;	
																						default:
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																															  .toUpperCase();
																							break;
																						}
																						
																						if(document.getPrtryMsg() != null){
																							
																							if(document.getPrtryMsg().getGrpHdr() != null){
																								
																								if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																										            .getGrpHdr().getMsgId())){
																									
																									this.ISO_011_SysAuditNumber = document.getPrtryMsg()
																								            .getGrpHdr().getMsgId(); 
																									
																									if(document.getPrtryMsg()
																								            .getGrpHdr().getCreDtTm() != null){
																										
																									    date = document.getHeader()
																									              .getMge().getOpeDate().toGregorianCalendar().getTime();
																										this.ISO_012_LocalDatetime = date;
																										
																										if(document.getPrtryMsg()
																									            .getGrpHdr().getNbOfTxs() != null){
																											
																											this.ISO_006_BillAmount = document.getPrtryMsg()
																										            .getGrpHdr().getNbOfTxs().doubleValue();
																											if(this.ISO_006_BillAmount >= 1){
																												
																												if(document.getPrtryMsg().getGrpHdr().getSttInf() != null){
																													
																													switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
																													
																													case CLRG:
																													case COVE:
																													case INGA:
																													case INDA:
																														
																															this.ISO_035_Track2 = String.valueOf(document.getPrtryMsg().getGrpHdr()
																																	           .getSttInf().getSttlmMtd());
																														
																														break;
	
																													default:
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																						  .toUpperCase();
																														
																														break;
																													}
																													
																													if(document.getPrtryMsg().getGrpHdr().getInstdAgt() != null){
																														
																														if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId() != null){
																															
																															if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																															          .getFinInstnId().getOthr() != null){
																																
																																if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId().getOthr().getId())){
																																	/*Validacion de codigo Cooperativa BIC*/
																																	this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																	          .getFinInstnId().getOthr().getId() + "|";
																																	
																																	if(document.getPrtryMsg().getGrpHdr().getInstgAgt() != null){
																																		
																																		if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId() != null){
																																			
																																			if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId().getOthr() != null){
																																				
																																				if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr()
																																						.getInstgAgt()
																																				      .getFinInstnId().getOthr().getId())){
																																					/*Validacion de codigo Entidad BANRED*/
																																					this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																					          .getFinInstnId().getOthr().getId();
																																					
																																					/*Estructura de Desenrolamiento*/
																																					if(document.getPrtryMsg().getAcctUnEnroll() != null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																								.getAcctUnEnroll().getPartyPrdType())){
																																							
																																							this.ISO_043_CardAcceptorLoc = document.getPrtryMsg()
																																									.getAcctUnEnroll().getPartyPrdType();
																																							
																																							if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																									.getAcctUnEnroll().getPartyPhone())){
																																								this.ISO_023_CardSeq = document.getPrtryMsg()
																																										.getAcctUnEnroll().getPartyPhone();
																																								
																																								if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																																										.getAcctUnEnroll().getAccInfo())){
																																									this.ISO_124_ExtendedData = document.getPrtryMsg()
																																											.getAcctUnEnroll().getAccInfo();
																																									
																																									/*Desglose de la Cuenta en Campo 120*/
																																									this.ISO_102_AccountID_1 = Arrays.asList(this.ISO_124_ExtendedData
																																											.split("\\|")).get(3);
																																									
																																									if(document.getPrtryMsg()
																																											.getAcctUnEnroll().getSttlmDt() != null){
																																										
																																										 this.ISO_015_SettlementDatel = document.getPrtryMsg()
																																											.getAcctUnEnroll().getSttlmDt().toGregorianCalendar()
																																											.getTime();
																																										 
																																										 this.ISO_039_ResponseCode = "004";
																																										 /*END*/
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																									    		+ "AcctUnEnroll/SttlmDt ES NULO"
																																									    		 .toUpperCase();
																																									}
																																								}
																																								
																																							}else{
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																							    		+ "AcctUnEnroll/PartyPhone ES NULO"
																																							    		 .toUpperCase();
																																							}
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																						    		+ "AcctUnEnroll/PartyPrdType ES NULO"
																																						    		 .toUpperCase();
																																						}
																																						
																																					}else{
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																					    		+ "AcctUnEnroll ES NULA"
																																					    		 .toUpperCase();
																																					}
																																					/*Fin Estructura de Desenrolamiento*/
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULO O VACIO"
																																				    		 .toUpperCase();
																																				}
																																				
																																			}else{
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																			    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																			    		 .toUpperCase();
																																			}
																																			
																																		}else{
																																			
																																			this.ISO_039_ResponseCode = "999";
																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																		    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																		    		 .toUpperCase();
																																		}
																																		
																																	}else{
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																	    		+ "GrpHdr/InstgAgt ES NULA"
																																	    		 .toUpperCase();
																																	}
																																	
																																}else{
																																
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																    		+ "GrpHdr/FinInstnId/Othr/Id ES NULO O VACIO"
																																									  .toUpperCase();
																																}
																																
																															}else{
																																
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																															    		+ "GrpHdr/FinInstnId/Othr ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																														    		+ "GrpHdr/FinInstnId ES NULA"
																																							  .toUpperCase();
																														}
																														
																													}else{
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/InstdAgt ES NULA"
																																						  .toUpperCase();
																													}
																													
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																												    		+ "GrpHdr/SttInf ES NULA"
																																					  .toUpperCase();
																												}
																																																					
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																											    		+ "GrpHdr/NbOfTxs ES INCORRECTO"
																																				  .toUpperCase();
																											}
																											
																										}else{
																											
																											this.ISO_039_ResponseCode = "999";
																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																										    		+ "GrpHdr/NbOfTxs ES NULO"
																																			  .toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																									    		+ "GrpHdr/CreDtTm ES INCORRECTO"
																																		  .toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																								    		+ "GrpHdr/MsgId ES INCORRECTO"
																																	  .toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg/GrpHdr ES NULA"
																																  .toUpperCase();
																							}
																							
																						}else{
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg ES NULA"
																															  .toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Prty ES NULO"
																														  .toUpperCase();
																					}
																					
																			     }else {
																					
																			    	 this.ISO_039_ResponseCode = "067";
																				     this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/OpeDate ES NULO"
																													  .toUpperCase();
																				 }
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/IdMge NO ES CORRECTO"
																											  .toUpperCase();
																			}
																			
																		}else{
																			
																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR NO ES CORRECTO"
																										  .toUpperCase();
																		}
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR ES NULA O VACIO"
																									  .toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/Type ES NULA O VACIO"
																								  .toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Mge ES NULA"
																							  .toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "209";
															this.ISO_039p_ResponseDetail = "CODIGO EFI RECEPTORA NO COINCIDE CON "
																	+ "LA ASIGNADA POR BIMO";
															
													    }
													 
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver"
																+ " ES NULO o VACIO".toUpperCase();
													}
													///aqui....validacion fin receiver
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender"
															+ " ES NULO o VACIO".toUpperCase();
												}
													
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId"
														+ " ES NULO o VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ"
													+ " ES NULO o VACIO".toUpperCase();
										}
																				
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ"
												+ " ES NULO o VACIO".toUpperCase();
									}
									
								}else{
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Service ES NULA"
																  .toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App"
										+ " ES NULO o VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel"
									+ " ES NULO o VACIO".toUpperCase();
						}
						
					}else{
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
													  .toUpperCase();
					}
					
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA";
				}
				
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA camt.998.211 ES NULA";
			}
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR DES-ENROLAMIENTO BIMO ", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
	}
    /*Consulta de Saldo*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.camt_998_201.Document document){
		
		this();
		Date date = null;
		Logger log = null;
		try {
			
			this.ISO_000_Message_Type = "1200";
			this.ISO_003_ProcessingCode = "311000";
			this.ISO_024_NetworkId = "555522";
			if(document != null){
				
				if(document.getHeader()  != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											.getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
												.getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
												.getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
													.getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
													.getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
														.getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													this.ISO_032_ACQInsID = document.getHeader().getSender();
													
													if(!StringUtils.IsNullOrEmpty(document.getHeader()
															             .getReceiver())){
														
													 if(document.getHeader().getReceiver().equalsIgnoreCase(MemoryGlobal.IdBIMOEfi)){
														 
															this.ISO_033_FWDInsID = document.getHeader().getReceiver();
														
															if(document.getHeader().getMge() != null){
																
																if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getType())){
																	
																	this.ISO_BitMap = document.getHeader()
																              .getMge().getType();
																	
																	if(document.getHeader()
																              .getMge().getRoR() != null){
																		if(document.getHeader()
																	              .getMge().getRoR() == com.belejanor.switcher.bimo.pacs.camt_998_201.RoRCod.REQ){
																			
																			this.ISO_036_Track3 = document.getHeader()
																		              .getMge().getRoR().toString();
																			
																			if(!StringUtils.IsNullOrEmpty(document.getHeader()
																		              .getMge().getIdMge())){
																				this.ISO_037_RetrievalReferenceNumber = document.getHeader()
																			              .getMge().getIdMge();
																				
																				if(document.getHeader()
																			              .getMge().getOpeDate() != null){
																			            	  
																					date = document.getHeader()
																				              .getMge().getOpeDate().toGregorianCalendar().getTime();
																					this.ISO_007_TransDatetime = date;
																					
																					boolean isPss = document.getHeader().isPssblDplct();
																					this.ISO_122_ExtendedData = String.valueOf(isPss) + "|";
																					
																					if(document.getHeader().getPrty() != null){
																						
																						switch (document.getHeader().getPrty()) {
																						
																						case URGT:
																						case HIGH:
																						case LOWW:	
																						case NORM:	
																							
																							this.ISO_122_ExtendedData += String.valueOf
																							           (document.getHeader().getPrty());
																							
																							break;	
																						default:
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																															  .toUpperCase();
																							break;
																						}
																						
																						if(document.getPrtryMsg() != null){
																							
																							if(document.getPrtryMsg().getGrpHdr() != null){
																								
																								if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg()
																										            .getGrpHdr().getMsgId())){
																									
																									this.ISO_011_SysAuditNumber = document.getPrtryMsg()
																								            .getGrpHdr().getMsgId(); 
																									
																									if(document.getPrtryMsg()
																								            .getGrpHdr().getCreDtTm() != null){
																										
																									    date = document.getHeader()
																									              .getMge().getOpeDate().toGregorianCalendar().getTime();
																										this.ISO_012_LocalDatetime = date;
																										
																										if(document.getPrtryMsg()
																									            .getGrpHdr().getNbOfTxs() != null){
																											
																											this.ISO_006_BillAmount = document.getPrtryMsg()
																										            .getGrpHdr().getNbOfTxs().doubleValue();
																											if(this.ISO_006_BillAmount >= 1){
																												
																												if(document.getPrtryMsg().getGrpHdr().getSttInf() != null){
																													
																													switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
																													
																													case CLRG:
																													case COVE:
																													case INGA:
																													case INDA:
																														
																															this.ISO_035_Track2 = String.valueOf(document.getPrtryMsg().getGrpHdr()
																																	           .getSttInf().getSttlmMtd());
																														
																														break;
	
																													default:
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																						  .toUpperCase();
																														
																														break;
																													}
																													
																													if(document.getPrtryMsg().getGrpHdr().getInstdAgt() != null){
																														
																														if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId() != null){
																															
																															if(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																															          .getFinInstnId().getOthr() != null){
																																
																																if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																          .getFinInstnId().getOthr().getId())){
																																	/*Validacion de codigo Cooperativa BIC*/
																																	this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstdAgt()
																																	          .getFinInstnId().getOthr().getId() + "|";
																																	
																																	if(document.getPrtryMsg().getGrpHdr().getInstgAgt() != null){
																																		
																																		if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId() != null){
																																			
																																			if(document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																				      .getFinInstnId().getOthr() != null){
																																				
																																				if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGrpHdr()
																																						.getInstgAgt()
																																				      .getFinInstnId().getOthr().getId())){
																																					/*Validacion de codigo Entidad BANRED*/
																																					this.ISO_121_ExtendedData += document.getPrtryMsg().getGrpHdr().getInstgAgt()
																																					          .getFinInstnId().getOthr().getId();
																																					
																																					/*Estructura de GetAccount*/
																																					if(document.getPrtryMsg().getGetAcct() != null){
																																						
																																						if(document.getPrtryMsg().getGetAcct().getAcctQryDef() != null){
																																							
																																							if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																									                      .getAcctCrit() != null){
																																								if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																					                      .getAcctCrit().getNewCrit() != null){
																																									
																																									if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																						                      .getAcctCrit().getNewCrit().getSchCrit() != null){
																																										
																																										if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																							                .getAcctCrit().getNewCrit().getSchCrit().getAcctId() != null){
																																											
																																											if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																									           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																									           .getEQ() != null){
																																												
																																												if(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																												           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																												           .getEQ().getOthr() != null){
																																													
																																													if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																													           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																													           .getEQ().getOthr().getId())){
																																														
																																														this.ISO_124_ExtendedData = document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																														           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																														           .getEQ().getOthr().getId();
																																														/*Desglose de la cuenta en P102*/
																																														
																																														this.ISO_102_AccountID_1 = Arrays.asList(this.ISO_124_ExtendedData.split("\\|")).get(3);
																																														
																																														if(!StringUtils.IsNullOrEmpty(document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																														           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																														           .getEQ().getOthr().getPhone())){
																																															
																																															this.ISO_023_CardSeq = document.getPrtryMsg().getGetAcct().getAcctQryDef()
																																															           .getAcctCrit().getNewCrit().getSchCrit().getAcctId().get(0)
																																															           .getEQ().getOthr().getPhone();
																																															/*END*/
																																															
																																															this.ISO_039_ResponseCode = "004";
																																															
																																														}else{
																																														
																																															this.ISO_039_ResponseCode = "999";
																																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																														    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit"
																																														    		+ "/AcctId/EQ/Othr/Phone ES NULO O VACIO"
																																														    		 .toUpperCase();
																																														}
																																														
																																													}else{
																																														
																																														this.ISO_039_ResponseCode = "999";
																																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																													    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit"
																																													    		+ "/AcctId/EQ/Othr/Id ES NULO O VACIO"
																																													    		 .toUpperCase();
																																													}
																																													
																																												}else{
																																													
																																													this.ISO_039_ResponseCode = "999";
																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																												    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit/AcctId/EQ/Othr ES NULA"
																																												    		 .toUpperCase();
																																												}
																																												
																																											}else{
																																												
																																												this.ISO_039_ResponseCode = "999";
																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																											    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit/AcctId/EQ ES NULA"
																																											    		 .toUpperCase();
																																											}
																																											
																																										}else{
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																										    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit/AcctId ES NULA"
																																										    		 .toUpperCase();
																																										}
																																										
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																									    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit/SchCrit ES NULA"
																																									    		 .toUpperCase();
																																									}
																																									
																																								}else{
																																									
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																								    		+ "GetAcct/AcctQryDef/AcctCrit/NewCrit ES NULA"
																																								    		 .toUpperCase();
																																								}
																																								
																																							}else{
																																								
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																							    		+ "GetAcct/AcctQryDef/AcctCrit ES NULA"
																																							    		 .toUpperCase();
																																							}
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																						    		+ "GetAcct/AcctQryDef ES NULA"
																																						    		 .toUpperCase();
																																						}
																																						
																																					}else{
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																					    		+ "GetAcct ES NULA"
																																					    		 .toUpperCase();
																																					}
																																					/*Fin Estructura de GetAccount*/
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULO O VACIO"
																																				    		 .toUpperCase();
																																				}
																																				
																																			}else{
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																			    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																			    		 .toUpperCase();
																																			}
																																			
																																		}else{
																																			
																																			this.ISO_039_ResponseCode = "999";
																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																		    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																		    		 .toUpperCase();
																																		}
																																		
																																	}else{
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																																	    		+ "GrpHdr/InstgAgt ES NULA"
																																	    		 .toUpperCase();
																																	}
																																	
																																}else{
																																
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																																    		+ "GrpHdr/FinInstnId/Othr/Id ES NULO O VACIO"
																																									  .toUpperCase();
																																}
																																
																															}else{
																																
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																															    		+ "GrpHdr/FinInstnId/Othr ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																														    		+ "GrpHdr/FinInstnId ES NULA"
																																							  .toUpperCase();
																														}
																														
																													}else{
																														
																														this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																													    		+ "GrpHdr/InstdAgt ES NULA"
																																						  .toUpperCase();
																													}
																													
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/PrtryMsg/"
																												    		+ "GrpHdr/SttInf ES NULA"
																																					  .toUpperCase();
																												}
																																																					
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																											    		+ "GrpHdr/NbOfTxs ES INCORRECTO"
																																				  .toUpperCase();
																											}
																											
																										}else{
																											
																											this.ISO_039_ResponseCode = "999";
																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																										    		+ "GrpHdr/NbOfTxs ES NULO"
																																			  .toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																									    		+ "GrpHdr/CreDtTm ES INCORRECTO"
																																		  .toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/PrtryMsg/"
																								    		+ "GrpHdr/MsgId ES INCORRECTO"
																																	  .toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg/GrpHdr ES NULA"
																																  .toUpperCase();
																							}
																							
																						}else{
																							
																							this.ISO_039_ResponseCode = "999";
																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/PrtryMsg ES NULA"
																															  .toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Prty ES NULO"
																														  .toUpperCase();
																					}
																					
																			     }else {
																					
																			    	 this.ISO_039_ResponseCode = "067";
																				     this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/OpeDate ES NULO"
																													  .toUpperCase();
																				 }
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/IdMge NO ES CORRECTO"
																											  .toUpperCase();
																			}
																			
																		}else{
																			
																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR NO ES CORRECTO"
																										  .toUpperCase();
																		}
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/RoR ES NULA O VACIO"
																									  .toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Mge/Type ES NULA O VACIO"
																								  .toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Mge ES NULA"
																							  .toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "209";
															this.ISO_039p_ResponseDetail = "CODIGO EFI RECEPTORA NO COINCIDE CON "
																	+ "LA ASIGNADA POR BIMO";
															
													    }
													 
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver"
																+ " ES NULO o VACIO".toUpperCase();
													}
													///aqui....validacion fin receiver
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender"
															+ " ES NULO o VACIO".toUpperCase();
												}
													
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId"
														+ " ES NULO o VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ"
													+ " ES NULO o VACIO".toUpperCase();
										}
																				
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ"
												+ " ES NULO o VACIO".toUpperCase();
									}
									
								}else{
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/Service ES NULA"
																  .toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App"
										+ " ES NULO o VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel"
									+ " ES NULO o VACIO".toUpperCase();
						}
						
					}else{
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
													  .toUpperCase();
					}
					
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA";
				}
				
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA camt.998.211 ES NULA";
			}
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR CONSULTA SALDO ", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
	}

    /*Credito*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.pacs_008_021.Document document){
    	
    	this();
    	Logger log = null;
		Date date = null;
    	try {
			
    		this.ISO_000_Message_Type = "1200";
			this.ISO_003_ProcessingCode = "201000";
			this.ISO_024_NetworkId = "555522";
    		
			if(document != null){
				
				/*HEADER*/
				if(document.getHeader() != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											                .getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
								                .getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
								                .getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
									                .getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
									                .getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
										                .getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													if(document.getHeader().getSender().equals(MemoryGlobal.IdBIMOBanred)){
														
														this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOBanred + "|";
														if(!StringUtils.IsNullOrEmpty(document.getHeader().getReceiver())){
															
															if(document.getHeader().getReceiver().equals(MemoryGlobal.IdBIMOEfi)){
																
																this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOEfi + "|";
																
																if(document.getHeader().getMge() != null){
																	
																	if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getType())){
																		
																		this.ISO_BitMap = document.getHeader().getMge().getType();
																		
																		if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getIdMge())){
																			
																			this.ISO_036_Track3 = document.getHeader().getMge().getIdMge();
																		
																			if(document.getHeader().getMge().getRoR() !=null){
																				
																				if(document.getHeader().getMge().getRoR() == 
																						com.belejanor.switcher.bimo.pacs.pacs_008_021.RoRCod.REQ){
																					
																					this.ISO_121_ExtendedData += document.getHeader().getMge().getRoR() + "|";
																					if(document.getHeader().getMge().getOpeDate() != null){
																						
																						date = document.getHeader()
																					              .getMge().getOpeDate().toGregorianCalendar().getTime();
																						this.ISO_007_TransDatetime = date;
																						
																						boolean isPss = document.getHeader().isPssblDplct();
																						this.ISO_121_ExtendedData = String.valueOf(isPss) + "|";
																						
																						if(document.getHeader().getPrty() != null){
																							
																							switch (document.getHeader().getPrty()) {
																							
																							case URGT:
																							case HIGH:
																							case LOWW:	
																							case NORM:	
																								
																								this.ISO_121_ExtendedData += String.valueOf
																								           (document.getHeader().getPrty());
																								
																								break;	
																							default:
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																																  .toUpperCase();
																								break;
																							}
																							
																							/*******************SECTOR FIToFICstmrCdtTrf*************************************/
																							if(document.getFIToFICstmrCdtTrf() != null){
																								
																								if(document.getFIToFICstmrCdtTrf().getGrpHdr() != null){
																									
																									if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																												.getGrpHdr().getMsgId())){
																										
																										this.ISO_011_SysAuditNumber = document.getFIToFICstmrCdtTrf()
																												.getGrpHdr().getMsgId();
																										if(document.getFIToFICstmrCdtTrf()
																															.getGrpHdr().getCreDtTm() != null){
																											
																											date = document.getFIToFICstmrCdtTrf()
																													.getGrpHdr().getCreDtTm().toGregorianCalendar().getTime();
																											this.ISO_012_LocalDatetime = date;
																											
																											if(document.getFIToFICstmrCdtTrf()
																													.getGrpHdr().getNbOfTxs() != null){
																												
																												this.ISO_006_BillAmount = document.getFIToFICstmrCdtTrf()
																											            .getGrpHdr().getNbOfTxs().doubleValue();
																												
																												if(ISO_006_BillAmount == 1){
																													
																													if(document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf() != null){
																														
																														switch (document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf().getSttlmMtd()) {
																														
																														case CLRG:
																														case COVE:
																														case INGA:
																														case INDA:
																															
																																this.ISO_035_Track2 = String.valueOf(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																		           .getSttInf().getSttlmMtd());
																															
																															break;
		
																														default:
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																							  .toUpperCase();
																															
																															break;
																														}
																														if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()!= null){
																															
																															if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
																																									.getFinInstnId() != null){
																																if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
																																		.getFinInstnId().getOthr() != null){
																																	
																																	if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																																					.getGrpHdr().getInstdAgt()
																																					.getFinInstnId().getOthr().getId())){
																																		
																																		/*OJO InstdAgt --> quien recibe el credito (en este caso nosotros) */
																																		this.ISO_033_FWDInsID = document.getFIToFICstmrCdtTrf()
																																				.getGrpHdr().getInstdAgt()
																																				.getFinInstnId().getOthr().getId();
																																		
																																		if(this.ISO_033_FWDInsID.equals(MemoryGlobal.IdBIMOEfi)){
																																		
																																			if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()!= null){
																																				
																																				if(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId() != null){
																																					
																																					if(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId().getOthr() != null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																							       .getInstgAgt().getFinInstnId().getOthr().getId())){
																																							
																																							/*OJO InstgAgt --> es el Instructor (quien realiza el debito, en nuestro caso la EFI acq) */
																																							this.ISO_032_ACQInsID = document.getFIToFICstmrCdtTrf().getGrpHdr()
																																								       .getInstgAgt().getFinInstnId().getOthr().getId();
																																							
																																							/*--------------------------------------------ESTRUCTURA DE CREDITO CdtTrfTxInf--------------------------------*/
																																							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf() != null){
																																							
																																								if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										                         .get(0).getPmtId() !=  null){
																																									
																																									if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										                         .get(0).getPmtId().getEndToEndId())){
																																										
																																										this.ISO_037_RetrievalReferenceNumber = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																						                         .get(0).getPmtId().getEndToEndId();

																																										if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											                         .get(0).getPmtId().getTxId())){
																																											
																																											this.ISO_022_PosEntryMode = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																							                         .get(0).getPmtId().getTxId();
																																										    if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																							                         .get(0).getPmtTpInf() != null){
																																										    	
																																										    	if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																								                         .get(0).getPmtTpInf().getCtgyPurp() != null){
																																										    		
																																										    		if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																									                         .get(0).getPmtTpInf().getCtgyPurp().getPrtry())){
																																										    			this.ISO_090_OriginalData = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										                         .get(0).getPmtTpInf().getCtgyPurp().getPrtry() + "|";
																																										    				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											                         .get(0).getIntrBkSttlmAmt() != null){
																																										    					
																																										    					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																												                         .get(0).getIntrBkSttlmAmt().getCcy())){
																																										    						
																																										    						this.ISO_090_OriginalData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                         .get(0).getIntrBkSttlmAmt().getCcy();
																																										    						this.ISO_049_TranCurrCode = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                         .get(0).getIntrBkSttlmAmt().getCcy().equals("USD")?840:-1;
																																										    						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                         .get(0).getIntrBkSttlmAmt().getValue() != null){
																																										    							
																																										    							this.ISO_004_AmountTransaction = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                         .get(0).getIntrBkSttlmAmt().getValue().doubleValue();
																																										    							
																																										    							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                         .get(0).getChrgBr() != null){
																																										    								
																																										    								if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                                      .get(0).getChrgBr() == 
																																													                                      com.belejanor.switcher.bimo.pacs.pacs_008_021.ChargeBearerType1Code.DEBT){
																																										    									
																																										    									this.ISO_090_OriginalData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                                      .get(0).getChrgBr().toString();
																																										    									
																																										    									/*-------------------------- DEBTOR ---------------------------*/
																																										    									if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										    											               .get(0).getDbtr() != null){
																																										    										
																																										    										if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																								    											               .get(0).getDbtr().getNm())){
																																										    										
																																										    											this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																									    											               .get(0).getDbtr().getNm() + "|";
																																										    										}
																																										    										
																																										    										if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																								    											               .get(0).getDbtr().getCtctDtls()!= null){
																																										    											
																																										    											if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																																										    													.getCdtTrfTxInf()
																																									    											               .get(0).getDbtr().getCtctDtls().getPhneNb())){
																																										    												
																																										    												this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf()
																																											    													.getCdtTrfTxInf()
																																										    											               .get(0).getDbtr().getCtctDtls().getPhneNb() + "^";
																																										    												/*-----------------------------Dbtr Acct---------------------------*/
																																										    												
																																										    												if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										    											               .get(0).getDbtrAcct() != null){
																																										    													
																																										    													if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											    											               .get(0).getDbtrAcct().getId() != null){
																																										    														
																																										    														if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																												    											               .get(0).getDbtrAcct().getId().getOthr() != null){
																																										    															
																																										    															if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													    											               .get(0).getDbtrAcct().getId().getOthr().getId())){
																																										    																
																																										    																this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														    											               .get(0).getDbtrAcct().getId().getOthr().getId();
																																										    																
																																										    																/*-----------------------------DbtrAgt Acctor---------------------------*/
																																										    																if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														    											               .get(0).getDbtrAgt() != null){
																																										    																	
																																										    																	if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																															    											               .get(0).getDbtrAgt().getFinInstnId() != null){
																																										    																		
																																										    																		if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																    											               .get(0).getDbtrAgt().getFinInstnId().getOthr() != null){
																																										    																			if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																	    											               .get(0).getDbtrAgt().getFinInstnId().getOthr().getId())){
																																										    																				
																																										    																				/*--------------------------CdtrAgt (Instirucion Receptora)------------------------------*/
																																										    																				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																		    											               .get(0).getCdtrAgt() != null){
																																										    																					
																																										    																					if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																			    											               .get(0).getCdtrAgt().getFinInstnId() != null){
																																										    																						
																																										    																						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																				    											               .get(0).getCdtrAgt().getFinInstnId() != null){
																																										    																							
																																										    																							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																					    											               .get(0).getCdtrAgt().getFinInstnId().getOthr() != null){
																																										    																								
																																										    																								if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																						    											               .get(0).getCdtrAgt().getFinInstnId().getOthr().getId())){
																																										    																									
																																										    																									if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																							    											               .get(0).getCdtrAgt().getFinInstnId().getOthr()
																																																							    											               .getId().equals(MemoryGlobal.IdBIMOEfi)){
																																										    																										
																																										    																										/*-------------------------Cdtr ------------------------*/
																																										    																										
																																										    																										if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																								    											               .get(0).getCdtr() != null){
																																										    																											
																																										    																											if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																								    											               .get(0).getCdtr().getNm())){
																																										    																											
																																										    																												this.ISO_034_PANExt = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               .get(0).getCdtr().getNm();
																																										    																											}
																																										    																											if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																									    											               .get(0).getCdtr().getCtctDtls() != null){
																																										    																											
																																										    																												if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               .get(0).getCdtr().getCtctDtls().getPhneNb())){
																																										    																													
																																										    																													this.ISO_023_CardSeq = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               .get(0).getCdtr().getCtctDtls().getPhneNb();
																																										    																													
																																										    																													if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               .get(0).getCdtr().getCtctDtls().getEmailAdr())){
																																																											    											            
																																										    																														this.ISO_055_EMV = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																												    											               .get(0).getCdtr().getCtctDtls().getEmailAdr();
																																																											    											    }
																																										    																													
																																										    																													/*---------------------------------------CdtrAcct Cuenta de la persona---------------*/
																																										    																													if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               				.get(0).getCdtrAcct() != null){
																																										    																														if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																								    											               				.get(0).getCdtrAcct().getId() != null){
																																										    																															
																																										    																															if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																									    											               				.get(0).getCdtrAcct().getId().getOthr() != null){
																																										    																																
																																										    																																if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               				.get(0).getCdtrAcct().getId().getOthr().getId())){
																																										    																																	
																																										    																																	this.ISO_124_ExtendedData = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               				.get(0).getCdtrAcct().getId().getOthr().getId(); 
																																										    																																	if(Arrays.asList(this.ISO_124_ExtendedData.split("\\|")).size() == 5){
																																										    																																		/*Cuenta a ser acreditada en la institucion FitBank*/
																																										    																																		this.ISO_102_AccountID_1 = Arrays
																																										    																																					.asList(this.ISO_124_ExtendedData.split("\\|")).get(3);
																																										    																																		/*--------------------------------ESTRUCTURA SplmtryData---------------------------------*/
																																										    																																		
																																										    																																		if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																												    											               				.get(0).getSplmtryData() != null){
																																										    																																			
																																										    																																			if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																													    											               				.get(0).getSplmtryData().getEnvlp() != null){
																																										    																																				
																																										    																																				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																														    											               				.get(0).getSplmtryData().getEnvlp().getCnts() != null){
																																										    																																					
																																										    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																														    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getCustRef())){
																																										    																																						
																																										    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getCustRef() + "|";
																																										    																																					}
																																										    																																					
																																										    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																															    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getChanRef())){
																																											    																																						
																																											    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getChanRef() + "|";
																																											    																																				}
																																										    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																															    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getAgtRef())){
																																											    																																						
																																											    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getAgtRef() + "|";
																																											    																																				}
																																										    																																					if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																															    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getDbtCred() != null){
																																										    																																						
																																										    																																						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getDbtCred() ==
																																																																    											               						com.belejanor.switcher.bimo.pacs.pacs_008_021.DebitCredit.CREDIT){
																																										    																																							
																																										    																																							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getSttlmDt() != null){
																																										    																																								
																																										    																																								this.ISO_015_SettlementDatel = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																		    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getSttlmDt()
																																																																		    											               				.toGregorianCalendar().getTime();
																																										    																																								
																																										    																																								this.ISO_039_ResponseCode = "004";
																																										    																																								/*END*/
																																										    																																								
																																										    																																							}else{
																																										    																																								
																																										    																																								this.ISO_039_ResponseCode = "999";
																																																																																		    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																																		    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/SttlmDt ES NULO"
																																																																																		    		.toUpperCase();
																																										    																																							}
																																										    																																							
																																										    																																						}else{
																																										    																																							
																																										    																																							this.ISO_039_ResponseCode = "999";
																																																																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																																	    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/DbtCred ES INCORRECTO"
																																																																																	    		.toUpperCase();
																																										    																																						}
																																										    																																						
																																										    																																					}else{
																																										    																																						
																																										    																																						this.ISO_039_ResponseCode = "999";
																																																																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																																    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/DbtCred ES NULA"
																																																																																    		.toUpperCase();
																																										    																																					}
																																										    																																					
																																										    																																				}else{
																																										    																																					
																																										    																																					this.ISO_039_ResponseCode = "999";
																																																																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																															    		+ "CdtrAgt/SplmtryData/Envlp/Cnts ES NULA"
																																																																															    		.toUpperCase();
																																										    																																				}
																																										    																																				
																																										    																																			}else{
																																										    																																				
																																										    																																				this.ISO_039_ResponseCode = "999";
																																																																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																														    		+ "CdtrAgt/SplmtryData/Envlp ES NULA"
																																																																														    		.toUpperCase();
																																										    																																			}
																																										    																																			
																																										    																																		}else {
																																										    																																			
																																										    																																			this.ISO_039_ResponseCode = "999";
																																																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																													    		+ "CdtrAgt/SplmtryData ES NULA"
																																																																													    		.toUpperCase();
																																																																													}
																																										    																																		/*--------------------------------End ESTRUCTURA SplmtryData---------------------------------*/
																																										    																																		
																																										    																																	}else{
																																										    																																		
																																										    																																		this.ISO_039_ResponseCode = "999";
																																																																												    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																												    		+ "CdtrAgt/CdtrAcct/Id/Othr/Id ES INCORRECTO"
																																																																												    		.toUpperCase();
																																										    																																	}
																																										    																																	
																																										    																																}else{
																																										    																																	
																																										    																																	this.ISO_039_ResponseCode = "999";
																																																																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																											    		+ "CdtrAgt/CdtrAcct/Id/Othr/Id ES NULA"
																																																																											    		.toUpperCase();
																																										    																																}
																																										    																																
																																										    																															}else{
																																										    																																
																																										    																																this.ISO_039_ResponseCode = "999";
																																																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																										    		+ "CdtrAgt/CdtrAcct/Id/Othr ES NULA"
																																																																										    		.toUpperCase();
																																										    																															}
																																										    																															
																																										    																														}else{
																																										    																															
																																										    																															this.ISO_039_ResponseCode = "999";
																																																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																									    		+ "CdtrAgt/CdtrAcct/Id ES NULA"
																																																																									    		.toUpperCase();
																																										    																														}
																																										    																														
																																										    																													}else{
																																										    																														
																																										    																														this.ISO_039_ResponseCode = "999";
																																																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																								    		+ "CdtrAgt/CdtrAcct ES NULA"
																																																																								    		.toUpperCase();
																																										    																													}
																																										    																													/*---------------------------------------CdtrAcct Cuenta de la persona---------------*/
																																										    																													
																																										    																												}else{
																																										    																													
																																										    																													this.ISO_039_ResponseCode = "999";
																																																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																							    		+ "CdtrAgt/Cdtr/CtctDtls/PhneNb ES NULA O VACIO"
																																																																							    		.toUpperCase();
																																										    																												}
																																										    																												
																																										    																											}else{
																																										    																												
																																										    																												this.ISO_039_ResponseCode = "999";
																																																																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																						    		+ "CdtrAgt/Cdtr/CtctDtls ES NULA"
																																																																						    		.toUpperCase();
																																										    																											}
																																										    																											
																																										    																										}else{
																																										    																											
																																										    																											this.ISO_039_ResponseCode = "999";
																																																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																					    		+ "CdtrAgt/Cdtr NO PERTENECE A NUESTRA INSTITUCION"
																																																																					    		.toUpperCase();
																																										    																										}
																																										    																										/*-------------------------End Cdtr ------------------------*/
																																										    																										
																																										    																									}else{
																																										    																										
																																										    																										this.ISO_039_ResponseCode = "999";
																																																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																				    		+ "CdtrAgt/FinInstnId/Othr/Id NO PERTENECE A NUESTRA INSTITUCION"
																																																																				    		.toUpperCase();
																																										    																									}
																																										    																									
																																										    																								}else{
																																										    																									
																																										    																									this.ISO_039_ResponseCode = "999";
																																																																			    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																			    		+ "CdtrAgt/FinInstnId/Othr/Id ES NULO O VACIO"
																																																																			    		.toUpperCase();
																																										    																								}
																																											    																								
																																										    																							}else{
																																										    																								

																																											    																							this.ISO_039_ResponseCode = "999";
																																																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																		    		+ "CdtrAgt/FinInstnId/Othr ES NULA"
																																																																		    		.toUpperCase();
																																										    																							}
																																										    																							
																																										    																						}else{
																																										    																							
																																										    																							this.ISO_039_ResponseCode = "999";
																																																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																	    		+ "CdtrAgt/FinInstnId ES NULA"
																																																																	    		.toUpperCase();
																																										    																						}
																																										    																						
																																										    																					}else{
																																										    																						
																																										    																						this.ISO_039_ResponseCode = "999";
																																																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																    		+ "CdtrAgt ES NULA"
																																																																    		.toUpperCase();
																																										    																					}
																																										    																					
																																										    																				}else{
																																										    																					
																																										    																					this.ISO_039_ResponseCode = "999";
																																																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																															    		+ "CdtrAgt ES NULA"
																																																															    		.toUpperCase();	
																																										    																				}
																																										    																				/*--------------------------CdtrAgt (End Instirucion Receptora)------------------------------*/
																																										    																			}else{
																																										    																				
																																										    																				this.ISO_039_ResponseCode = "999";
																																																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																														    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId/Othr/Id ES NULA"
																																																														    		.toUpperCase();	
																																										    																			}
																																										    																			
																																										    																		}else{
																																										    																			
																																										    																			this.ISO_039_ResponseCode = "999";
																																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																													    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId/Othr ES NULA"
																																																													    		.toUpperCase();	
																																										    																		}
																																										    																		
																																										    																	}else{
																																										    																		
																																										    																		this.ISO_039_ResponseCode = "999";
																																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																												    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId ES NULA"
																																																												    		.toUpperCase();	
																																										    																	}
																																										    																	
																																										    																}else{
																																										    																	
																																										    																	this.ISO_039_ResponseCode = "999";
																																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																											    		+ "CdtTrfTxInf/DbtrAgt ES NULA"
																																																											    		.toUpperCase();	
																																										    																}
																																										    																/*-----------------------------End DbtrAgt Acctor---------------------------*/
																																										    																
																																										    															}else{
																																										    																
																																										    																this.ISO_039_ResponseCode = "999";
																																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																										    		+ "CdtTrfTxInf/DbtrAcct/Id/Othr/Id ES NULA O VACIA"
																																																										    		.toUpperCase();	
																																										    															}
																																										    															
																																										    														}else{
																																										    															
																																										    															this.ISO_039_ResponseCode = "999";
																																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																									    		+ "CdtTrfTxInf/DbtrAcct/Id/Othr ES NULA"
																																																									    		.toUpperCase();	
																																										    														}
																																										    														
																																										    													}else{
																																										    														
																																										    														this.ISO_039_ResponseCode = "999";
																																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																								    		+ "CdtTrfTxInf/DbtrAcct/Id ES NULA"
																																																								    		.toUpperCase();	
																																										    													}
																																										    													
																																										    												}else{
																																										    													
																																										    													this.ISO_039_ResponseCode = "999";
																																																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																							    		+ "CdtTrfTxInf/DbtrAcct ES NULA"
																																																							    		.toUpperCase();	
																																										    												}
																																										    												
																																										    												/*-----------------------------End Dbtr Acct---------------------------*/
																																										    												
																																										    											}else{
																																										    												
																																										    												this.ISO_039_ResponseCode = "999";
																																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																						    		+ "CdtTrfTxInf/Dbtr/CtctDtls/PhneNb ES NULA"
																																																						    		.toUpperCase();	
																																										    											}
																																										    											
																																										    										}else{
																																										    											
																																										    											this.ISO_039_ResponseCode = "999";
																																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																					    		+ "CdtTrfTxInf/Dbtr/CtctDtls ES NULA"
																																																					    		.toUpperCase();	
																																										    										}
																																										    										
																																										    									}else{
																																										    										
																																										    										this.ISO_039_ResponseCode = "999";
																																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																				    		+ "CdtTrfTxInf/Dbtr ES INCORRECTO"
																																																				    		.toUpperCase();	
																																										    									}
																																										    									/*------------------------ FIN DEBTOR -------------------------*/
																																										    									
																																										    								}else{
																																										    									
																																										    									this.ISO_039_ResponseCode = "999";
																																																			    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																			    		+ "CdtTrfTxInf/ChrgBr ES INCORRECTO"
																																																			    		.toUpperCase();	
																																										    								}
																																										    								
																																										    							}else{
																																										    								
																																										    								this.ISO_039_ResponseCode = "999";
																																																		    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																		    		+ "CdtTrfTxInf/ChrgBr ES NULO"
																																																		    		.toUpperCase();	
																																										    							}
																																										    							
																																										    						}else{
																																										    							
																																										    							this.ISO_039_ResponseCode = "999";
																																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																	    		+ "CdtTrfTxInf/IntrBkSttlmAmt/Value ES NULO"
																																																	    		.toUpperCase();	
																																										    						}
																																										    						
																																										    					}else{
																																										    						
																																										    						this.ISO_039_ResponseCode = "999";
																																																    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																    		+ "CdtTrfTxInf/IntrBkSttlmAmt/Ccy ES NULO O VACIO"
																																																    		.toUpperCase();	
																																										    					}
																																										    					
																																										    				}else{
																																										    					
																																										    					this.ISO_039_ResponseCode = "999";
																																															    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																															    		+ "CdtTrfTxInf/IntrBkSttlmAmt ES NULO O VACIO"
																																															    		.toUpperCase();	
																																										    				}
																																										    			
																																										    		}else{
																																										    			
																																										    			this.ISO_039_ResponseCode = "999";
																																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																													    		+ "CdtTrfTxInf/PmtTpInf/CtgyPurp/Prtry ES NULO O VACIO"
																																													    		.toUpperCase();
																																										    		}
																																										    		
																																										    	}else{
																																										    	
																																										    		this.ISO_039_ResponseCode = "999";
																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																												    		+ "CdtTrfTxInf/PmtTpInf/CtgyPurp ES NULA"
																																												    		.toUpperCase();
																																										    	}
																																										    	
																																										    }else{
																																										    	
																																										    	this.ISO_039_ResponseCode = "999";
																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																											    		+ "CdtTrfTxInf/PmtTpInf ES NULA"
																																											    		.toUpperCase();
																																										    }
																																											
																																										}else {
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																										    		+ "CdtTrfTxInf/PmtId/TxId ES NULA"
																																										    		.toUpperCase();
																																										}
																																										
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																									    		+ "CdtTrfTxInf/PmtId/EndToEndId ES NULA"
																																									    		.toUpperCase();
																																									}
																																									
																																								}else {
																																								
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																								    		+ "CdtTrfTxInf/PmtId ES NULA"
																																								    		.toUpperCase();
																																								}
																																								
																																							}else{
																																								
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																							    		+ "CdtTrfTxInf ES NULA"
																																							    		.toUpperCase();
																																							}
																																							
																																							/*--------------------------------------------FIN ESTRUCTURA DE CREDITO CdtTrfTxInf--------------------------------*/
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																						    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULA"
																																															  .toUpperCase();
																																						}
																																						
																																					}else {
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																					    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																														  .toUpperCase();
																																					}
																																					
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																													  .toUpperCase();
																																				}
																																				
																																			}else {
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																			    		+ "GrpHdr/InstgAgt ES NULA"
																																												  .toUpperCase();
																																			}
																																			
																																		}else {
																																			
																																			this.ISO_039_ResponseCode = "999";
																																		    this.ISO_039p_ResponseDetail = "El campo Document/FIToFICstmrCdtTrf/"
																																		    		+ "GrpHdr/InstdAgt no corresponde a nuestra institucion"
																																											  .toUpperCase();
																																		}
																																		
																																	}else {
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																	    		+ "GrpHdr/InstdAgt/FinInstnId/Othr/Id ES NULA"
																																										  .toUpperCase();
																																	}
																																	
																																}else{
																																	
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																    		+ "GrpHdr/InstdAgt/FinInstnId/Othr ES NULA"
																																									  .toUpperCase();
																																}
																																
																															}else{
																															
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																															    		+ "GrpHdr/InstdAgt/FinInstnId ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/InstdAgt ES NULA"
																																							  .toUpperCase();
																														}
																														
																												   }else{
																													  
																													   this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES NULO"
																																						  .toUpperCase();
																												   }
		
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																													this.ISO_039p_ResponseDetail = "EL SISTEMA SOLAMENTE SOPORTA UNA "
																															+ "TRANSACCION DE CREDITO".toUpperCase();
																												}
																												
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																												this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																														+ "/GrpHdr/NbOfTxs ES NULO".toUpperCase();
																											}
																											
																										}else {
																											
																											this.ISO_039_ResponseCode = "999";
																											this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																													+ "/GrpHdr/getCreDtTm ES NULO".toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																										this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																												+ "/GrpHdr/MsgId ES NULO O VACIO".toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf"
																											+ "/GrpHdr ES NULA".toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																								this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf "
																										+ "ES NULA".toUpperCase();
																							}
																							
																							/*******************FIN SECTOR FIToFICstmrCdtTrf**********************************/
																							
																						}else {
																							
																							this.ISO_039_ResponseCode = "999";
																							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																									+ "Prty ES NULO".toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																						this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																								+ "Mge/OpeDate ES NULO".toUpperCase();
																					}																				
																				}else{
																					
																					this.ISO_039_ResponseCode = "999";
																					this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																							+ "Mge/RoR ES INCORRECTO".toUpperCase();
																				}
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																						+ "Mge/RoR ES NULO O VACIO".toUpperCase();
																			}
																		
																		}else {
																			

																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																					+ "Mge/IdMge ES NULO O VACIO".toUpperCase();
																		}
																		
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																				+ "Mge/Type ES NULO O VACIO".toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/"
																			+ "Mge ES NULA".toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																		+ "NO CORRESPONDE A ESTA INSTITUCION".toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "999";
															this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																	+ "ES NULO O VACIO".toUpperCase();
														}
														
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
																+ "NO CORRESPONDE A BANRED".toUpperCase();
													}
													
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
															+ "ES NULO O VACIO".toUpperCase();
												}
												
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId "
														+ "ES NULO O VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ "
													+ "ES NULO O VACIO".toUpperCase();
										}
										
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ "
												+ "ES NULO O VACIO".toUpperCase();
									}
									
								}else {
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header"
											+ "/OrigId/Service ES NULA".toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App "
										+ "ES NULO O VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel "
									+ "ES NULO O VACIO".toUpperCase();
						}
						
					}else {
					
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
								.toUpperCase();
					}
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA"
							.toUpperCase();
				}
				/*FIN HEADER*/
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA pacs_008_021 ES NULA"
						.toUpperCase();
			}
    		
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR PREFIX ISO ", TypeMonitor.error, e);
			
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
    }
    
    /*Reverso Credito*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.pacs_007_041.Document document){
    	
    	this();
    	Logger log = null;
		Date date = null;
    	try {
			
    		this.ISO_000_Message_Type = "1400";
			this.ISO_003_ProcessingCode = "201000";
			this.ISO_024_NetworkId = "555522";
    		
			if(document != null){
				
				/*HEADER*/
				if(document.getHeader() != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											                .getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
								                .getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
								                .getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
									                .getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
									                .getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
										                .getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													if(document.getHeader().getSender().equals(MemoryGlobal.IdBIMOBanred)){
														
														this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOBanred + "|";
														if(!StringUtils.IsNullOrEmpty(document.getHeader().getReceiver())){
															
															if(document.getHeader().getReceiver().equals(MemoryGlobal.IdBIMOEfi)){
																
																this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOEfi + "|";
																
																if(document.getHeader().getMge() != null){
																	
																	if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getType())){
																		
																		this.ISO_BitMap = document.getHeader().getMge().getType();
																		
																		if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getIdMge())){
																			
																			this.ISO_036_Track3 = document.getHeader().getMge().getIdMge();
																		
																			if(document.getHeader().getMge().getRoR() !=null){
																				
																				if(document.getHeader().getMge().getRoR() == 
																						com.belejanor.switcher.bimo.pacs.pacs_007_041.RoRCod.REQ){
																					
																					this.ISO_121_ExtendedData += document.getHeader().getMge().getRoR() + "|";
																					if(document.getHeader().getMge().getOpeDate() != null){
																						
																						date = document.getHeader()
																					              .getMge().getOpeDate().toGregorianCalendar().getTime();
																						this.ISO_007_TransDatetime = date;
																						
																						boolean isPss = document.getHeader().isPssblDplct();
																						this.ISO_121_ExtendedData = String.valueOf(isPss) + "|";
																						
																						if(document.getHeader().getPrty() != null){
																							
																							switch (document.getHeader().getPrty()) {
																							
																							case URGT:
																							case HIGH:
																							case LOWW:	
																							case NORM:	
																								
																								this.ISO_121_ExtendedData += String.valueOf
																								           (document.getHeader().getPrty());
																								
																								break;	
																							default:
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																																  .toUpperCase();
																								break;
																							}
																							
																							/*******************SECTOR FIToFICstmrCdtTrf*************************************/
																							if(document.getFIToFIPmtRvsl() != null){
																								
																								if(document.getFIToFIPmtRvsl().getGrpHdr() != null){
																									
																									if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																												.getGrpHdr().getMsgId())){
																										
																										this.ISO_011_SysAuditNumber = document.getFIToFIPmtRvsl()
																												.getGrpHdr().getMsgId();
																										if(document.getFIToFIPmtRvsl()
																															.getGrpHdr().getCreDtTm() != null){
																											
																											date = document.getFIToFIPmtRvsl()
																													.getGrpHdr().getCreDtTm().toGregorianCalendar().getTime();
																											this.ISO_012_LocalDatetime = date;
																											
																											if(document.getFIToFIPmtRvsl()
																													.getGrpHdr().getNbOfTxs() != null){
																												
																												this.ISO_006_BillAmount = document.getFIToFIPmtRvsl()
																											            .getGrpHdr().getNbOfTxs().doubleValue();
																												
																												if(ISO_006_BillAmount == 1){
																													
																													if(document.getFIToFIPmtRvsl().getGrpHdr().getSttInf() != null){
																														
																														switch (document.getFIToFIPmtRvsl().getGrpHdr().getSttInf().getSttlmMtd()) {
																														
																														case CLRG:
																														case COVE:
																														case INGA:
																														case INDA:
																															
																																this.ISO_035_Track2 = String.valueOf(document.getFIToFIPmtRvsl().getGrpHdr()
																																		           .getSttInf().getSttlmMtd());
																															
																															break;
		
																														default:
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																							  .toUpperCase();
																															
																															break;
																														}
																														if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()!= null){
																															
																															if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()
																																									.getFinInstnId() != null){
																																if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()
																																		.getFinInstnId().getOthr() != null){
																																	
																																	if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																					.getGrpHdr().getInstdAgt()
																																					.getFinInstnId().getOthr().getId())){
																																		
																																		/*OJO InstdAgt --> quien recibe el credito (en este caso nosotros) */
																																		this.ISO_033_FWDInsID = document.getFIToFIPmtRvsl()
																																				.getGrpHdr().getInstdAgt()
																																				.getFinInstnId().getOthr().getId();
																																		
																																		if(this.ISO_033_FWDInsID.equals(MemoryGlobal.IdBIMOEfi)){
																																		
																																			if(document.getFIToFIPmtRvsl().getGrpHdr().getInstgAgt()!= null){
																																				
																																				if(document.getFIToFIPmtRvsl().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId() != null){
																																					
																																					if(document.getFIToFIPmtRvsl().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId().getOthr() != null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getGrpHdr()
																																							       .getInstgAgt().getFinInstnId().getOthr().getId())){
																																							
																																							/*OJO InstgAgt --> es el Instructor (quien realiza el debito, en nuestro caso la EFI acq) */
																																							this.ISO_032_ACQInsID = document.getFIToFIPmtRvsl().getGrpHdr()
																																								       .getInstgAgt().getFinInstnId().getOthr().getId();
																																							
																																							/*------------------------------ESTRUCTURA  TxInf de REVERSO----------------------------------*/
																																							
																																							if(document.getFIToFIPmtRvsl().getTxInf() != null){
																																								
																																								if(document.getFIToFIPmtRvsl().getTxInf().getOrgnlGrpInf() != null){
																																									
																																									if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf()
																																											                              .getOrgnlGrpInf().getOrgnlMsgId())){
																																										
																																										this.ISO_037_RetrievalReferenceNumber = document.getFIToFIPmtRvsl().getTxInf()
																																					                              .getOrgnlGrpInf().getOrgnlMsgId();
																																										
																																										if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf()
																																					                              .getOrgnlGrpInf().getOrgnlMsgNmId())){
																																											
																																											this.ISO_122_ExtendedData = document.getFIToFIPmtRvsl().getTxInf()
																																						                              .getOrgnlGrpInf().getOrgnlMsgNmId();
																																											
																																											if(document.getFIToFIPmtRvsl().getTxInf()
																																						                              .getOrgnlGrpInf().getOrgnlCreDtTm() != null){
																																												
																																												this.ISO_013_LocalDate = document.getFIToFIPmtRvsl().getTxInf()
																																							                              .getOrgnlGrpInf().getOrgnlCreDtTm().toGregorianCalendar()
																																							                              .getTime();	
																																											}
																																											
																																											if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																																				.getTxInf().getOrgnlEndToEndId())){
																																												
																																												this.ISO_090_OriginalData = document.getFIToFIPmtRvsl()
																																														.getTxInf().getOrgnlEndToEndId() + "|";
																																												
																																												this.ISO_022_PosEntryMode = document.getFIToFIPmtRvsl()
																																														.getTxInf().getOrgnlEndToEndId();
																																												
																																												if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																														.getTxInf().getOrgnlTxId())){
																																													
																																													this.ISO_090_OriginalData += document.getFIToFIPmtRvsl()
																																															.getTxInf().getOrgnlTxId();
																																												}else{
																																													
																																													StringUtils.trimEnd(this.ISO_090_OriginalData, "|");
																																												}
																																												
																																												if(document.getFIToFIPmtRvsl()
																																														.getTxInf().getRvsdIntrBkSttlmAmt() != null){
																																													
																																													if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																															.getTxInf().getRvsdIntrBkSttlmAmt().getCcy())){
																																														
																																														this.ISO_049_TranCurrCode = document.getFIToFIPmtRvsl()
																																																.getTxInf().getRvsdIntrBkSttlmAmt().getCcy().equalsIgnoreCase("USD")?840:-1;
																																														
																																														if(document.getFIToFIPmtRvsl()
																																																.getTxInf().getRvsdIntrBkSttlmAmt().getValue() != null){
																																															
																																															this.ISO_004_AmountTransaction = document.getFIToFIPmtRvsl()
																																																	.getTxInf().getRvsdIntrBkSttlmAmt().getValue().doubleValue();
																																															
																																															/*----------------------------SplmtryData----------------------------*/
																																															
																																																if(document.getFIToFIPmtRvsl().getSplmtryData() != null){
																																																	
																																																	if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																			                .getEnvlp() != null){
																																																		
																																																		if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																                .getEnvlp().getCnts() != null){
																																																			
																																																			if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																	                .getEnvlp().getCnts().getCdtrAcct())){
																																																				
																																																				this.ISO_115_ExtendedData = document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																		                .getEnvlp().getCnts().getCdtrAcct() + "^";
																																																				
																																																				if(Arrays.asList(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																	                .getEnvlp().getCnts().getCdtrAcct().split("\\|")).size() == 5){
																																																					
																																																					this.ISO_102_AccountID_1 = Arrays.asList(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																			                .getEnvlp().getCnts().getCdtrAcct().split("\\|")).get(3);
																																																					
																																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																			                .getEnvlp().getCnts().getPrtry())){
																																																						
																																																						if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																				                .getEnvlp().getCnts().getPrtry().equalsIgnoreCase("PAGO")
																																																				           || document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																			                .getEnvlp().getCnts().getPrtry().equalsIgnoreCase("COBROP2P")){
																																																							
																																																							if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																					                .getEnvlp().getCnts().getPhone())){
																																																								
																																																								this.ISO_023_CardSeq = document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																						            .getEnvlp().getCnts().getPhone();
																																																								
																																																								if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																						                .getEnvlp().getCnts().getReversalRsn())){
																																																									
																																																									this.ISO_115_ExtendedData += document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																							                .getEnvlp().getCnts().getReversalRsn() + "^";
																																																									
																																																									if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																							                .getEnvlp().getCnts().getOrgnSetlmtDt() != null){
																																																										
																																																										this.ISO_115_ExtendedData += FormatUtils.DateToString
																																																												 (document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																								                .getEnvlp().getCnts().getOrgnSetlmtDt()
																																																								                .toGregorianCalendar().getTime(),"yyyy-MM-dd");
																																																										
																																																										if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																								                .getEnvlp().getCnts().getSttlmDt() != null){
																																																											
																																																											this.ISO_015_SettlementDatel =  document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																									              .getEnvlp().getCnts().getSttlmDt().toGregorianCalendar().getTime();
																																																											
																																																											this.ISO_039_ResponseCode = "004";
																																																											//End Constructor
																																																											
																																																										}else{
																																																											
																																																											this.ISO_039_ResponseCode = "999";
																																																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																										    		+ "TxInf/SplmtryData/Envlp/Cnts/SttlmDt ES NULO O VACIO"
																																																										    		 .toUpperCase();
																																																										}
																																																										
																																																									}else{
																																																										
																																																										this.ISO_039_ResponseCode = "999";
																																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																									    		+ "TxInf/SplmtryData/Envlp/Cnts/OrgnSetlmtDt ES NULO O VACIO"
																																																									    		 .toUpperCase();
																																																									}
																																																									
																																																								}else{
																																																									
																																																									this.ISO_039_ResponseCode = "999";
																																																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																								    		+ "TxInf/SplmtryData/Envlp/Cnts/Phone ES NULO O VACIO"
																																																								    		 .toUpperCase();
																																																								}
																																																								
																																																							}else{
																																																								
																																																								this.ISO_039_ResponseCode = "999";
																																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																							    		+ "TxInf/SplmtryData/Envlp/Cnts/Phone ES INCORRECTO"
																																																							    		 .toUpperCase();
																																																							}
																																																							
																																																						}else{
																																																							
																																																							this.ISO_039_ResponseCode = "999";
																																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																						    		+ "TxInf/SplmtryData/Envlp/Cnts/Prtry ES INCORRECTO"
																																																						    		 .toUpperCase();
																																																						}
																																																						
																																																					}else{
																																																						
																																																						this.ISO_039_ResponseCode = "999";
																																																					    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																					    		+ "TxInf/SplmtryData/Envlp/Cnts/Prtry ES NULO O VACIO"
																																																														  .toUpperCase();
																																																					}
																																																					
																																																				}else{
																																																					
																																																					this.ISO_039_ResponseCode = "999";
																																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																				    		+ "TxInf/SplmtryData/Envlp/Cnts/CdtrAcct ES INCORRECTO"
																																																													  .toUpperCase();
																																																				}
																																																				
																																																			}else{
																																																				
																																																				this.ISO_039_ResponseCode = "999";
																																																			    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																			    		+ "TxInf/SplmtryData/Envlp/Cnts/CdtrAcct ES NULO O VACIO"
																																																												  .toUpperCase();
																																																			}
																																																			
																																																		}else{
																																																			
																																																			this.ISO_039_ResponseCode = "999";
																																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																		    		+ "TxInf/SplmtryData/Envlp/Cnts ES NULA"
																																																											  .toUpperCase();
																																																		}
																																																		
																																																	}else{
																																																		
																																																		this.ISO_039_ResponseCode = "999";
																																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																	    		+ "TxInf/SplmtryData/Envlp ES NULA"
																																																										  .toUpperCase();
																																																	}
																																																	
																																																}else{
																																																	
																																																	this.ISO_039_ResponseCode = "999";
																																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																    		+ "TxInf/SplmtryData ES NULA"
																																																									  .toUpperCase();
																																																}
																																															
																																															/*---------------------End-------SplmtryData-------------------------*/
																																														}else{
																																															
																																															this.ISO_039_ResponseCode = "999";
																																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																														    		+ "TxInf/RvsdIntrBkSttlmAmt/Value ES NULA "
																																																							  .toUpperCase();
																																														}
																																														
																																													}else {
																																														
																																														this.ISO_039_ResponseCode = "999";
																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																													    		+ "TxInf/RvsdIntrBkSttlmAmt/Ccy ES NULA "
																																																						  .toUpperCase();
																																													}
																																													
																																												}else{
																																													
																																													this.ISO_039_ResponseCode = "999";
																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																												    		+ "TxInf/RvsdIntrBkSttlmAmt ES NULA "
																																																					  .toUpperCase();
																																												}
																																												
																																											}else{
																																												
																																												this.ISO_039_ResponseCode = "999";
																																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																											    		+ "TxInf/OrgnlEndToEndId ES NULO O VACIO"
																																																				  .toUpperCase();
																																											}
																																											
																																										}else{
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																										    		+ "TxInf/OrgnlGrpInf/OrgnlMsgId ES NULO O VACIO"
																																																			  .toUpperCase();
																																										}
																																										
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																									    		+ "TxInf/OrgnlGrpInf/OrgnlMsgId ES NULO O VACIO"
																																																		  .toUpperCase();
																																										
																																									}
																																									
																																								}else{
																																									
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																								    		+ "TxInf/OrgnlGrpInf/ ES NULA"
																																																	  .toUpperCase();
																																								}
																																								
																																							}else{
																																								
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																							    		+ "TxInf ES NULA"
																																																  .toUpperCase();
																																							}
																																							/*------------------------------ FIN ESTRUCTURA  TxInf de REVERSO----------------------------------*/
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																						    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULA"
																																															  .toUpperCase();
																																						}
																																						
																																					}else {
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																					    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																														  .toUpperCase();
																																					}
																																					
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																													  .toUpperCase();
																																				}
																																				
																																			}else {
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																			    		+ "GrpHdr/InstgAgt ES NULA"
																																												  .toUpperCase();
																																			}
																																			
																																		}else {
																																			
																																			this.ISO_039_ResponseCode = "999";
																																		    this.ISO_039p_ResponseDetail = "El campo Document/FIToFIPmtRvsl/"
																																		    		+ "GrpHdr/InstdAgt no corresponde a nuestra institucion"
																																											  .toUpperCase();
																																		}
																																		
																																	}else {
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																	    		+ "GrpHdr/InstdAgt/FinInstnId/Othr/Id ES NULA"
																																										  .toUpperCase();
																																	}
																																	
																																}else{
																																	
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																    		+ "GrpHdr/InstdAgt/FinInstnId/Othr ES NULA"
																																									  .toUpperCase();
																																}
																																
																															}else{
																															
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																															    		+ "GrpHdr/InstdAgt/FinInstnId ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																														    		+ "GrpHdr/InstdAgt ES NULA"
																																							  .toUpperCase();
																														}
																														
																												   }else{
																													  
																													   this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES NULO"
																																						  .toUpperCase();
																												   }
		
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																													this.ISO_039p_ResponseDetail = "EL SISTEMA SOLAMENTE SOPORTA UNA "
																															+ "TRANSACCION DE CREDITO".toUpperCase();
																												}
																												
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																												this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																														+ "/GrpHdr/NbOfTxs ES NULO".toUpperCase();
																											}
																											
																										}else {
																											
																											this.ISO_039_ResponseCode = "999";
																											this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																													+ "/GrpHdr/getCreDtTm ES NULO".toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																										this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																												+ "/GrpHdr/MsgId ES NULO O VACIO".toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl"
																											+ "/GrpHdr ES NULA".toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																								this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl "
																										+ "ES NULA".toUpperCase();
																							}
																							
																							/*******************FIN SECTOR FIToFICstmrCdtTrf**********************************/
																							
																						}else {
																							
																							this.ISO_039_ResponseCode = "999";
																							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																									+ "Prty ES NULO".toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																						this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																								+ "Mge/OpeDate ES NULO".toUpperCase();
																					}																				
																				}else{
																					
																					this.ISO_039_ResponseCode = "999";
																					this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																							+ "Mge/RoR ES INCORRECTO".toUpperCase();
																				}
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																						+ "Mge/RoR ES NULO O VACIO".toUpperCase();
																			}
																		
																		}else {
																			

																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																					+ "Mge/IdMge ES NULO O VACIO".toUpperCase();
																		}
																		
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																				+ "Mge/Type ES NULO O VACIO".toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/"
																			+ "Mge ES NULA".toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																		+ "NO CORRESPONDE A ESTA INSTITUCION".toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "999";
															this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																	+ "ES NULO O VACIO".toUpperCase();
														}
														
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
																+ "NO CORRESPONDE A BANRED".toUpperCase();
													}
													
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
															+ "ES NULO O VACIO".toUpperCase();
												}
												
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId "
														+ "ES NULO O VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ "
													+ "ES NULO O VACIO".toUpperCase();
										}
										
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ "
												+ "ES NULO O VACIO".toUpperCase();
									}
									
								}else {
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header"
											+ "/OrigId/Service ES NULA".toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App "
										+ "ES NULO O VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel "
									+ "ES NULO O VACIO".toUpperCase();
						}
						
					}else {
					
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
								.toUpperCase();
					}
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA"
							.toUpperCase();
				}
				/*FIN HEADER*/
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA pacs_008_021 ES NULA"
						.toUpperCase();
			}
    		
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR REVERSO CREDITO ", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
    }
    
    /*Debito*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.pacs_008_071.Document document){
    	
    	this();
    	Logger log = null;
		Date date = null;
    	try {
			
    		this.ISO_000_Message_Type = "1200";
			this.ISO_003_ProcessingCode = "011000";
			this.ISO_024_NetworkId = "555522";
    		
			if(document != null){
				
				/*HEADER*/
				if(document.getHeader() != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											                .getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
								                .getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
								                .getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
									                .getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
									                .getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
										                .getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													if(document.getHeader().getSender().equals(MemoryGlobal.IdBIMOBanred)){
														
														this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOBanred + "|";
														if(!StringUtils.IsNullOrEmpty(document.getHeader().getReceiver())){
															
															if(document.getHeader().getReceiver().equals(MemoryGlobal.IdBIMOEfi)){
																
																this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOEfi + "|";
																
																if(document.getHeader().getMge() != null){
																	
																	if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getType())){
																		
																		this.ISO_BitMap = document.getHeader().getMge().getType();
																		
																		if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getIdMge())){
																			
																			this.ISO_036_Track3 = document.getHeader().getMge().getIdMge();
																		
																			if(document.getHeader().getMge().getRoR() !=null){
																				
																				if(document.getHeader().getMge().getRoR() == 
																						com.belejanor.switcher.bimo.pacs.pacs_008_071.RoRCod.REQ){
																					
																					this.ISO_121_ExtendedData += document.getHeader().getMge().getRoR() + "|";
																					if(document.getHeader().getMge().getOpeDate() != null){
																						
																						date = document.getHeader()
																					              .getMge().getOpeDate().toGregorianCalendar().getTime();
																						this.ISO_007_TransDatetime = date;
																						
																						boolean isPss = document.getHeader().isPssblDplct();
																						this.ISO_121_ExtendedData = String.valueOf(isPss) + "|";
																						
																						if(document.getHeader().getPrty() != null){
																							
																							switch (document.getHeader().getPrty()) {
																							
																							case URGT:
																							case HIGH:
																							case LOWW:	
																							case NORM:	
																								
																								this.ISO_121_ExtendedData += String.valueOf
																								           (document.getHeader().getPrty());
																								
																								break;	
																							default:
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																																  .toUpperCase();
																								break;
																							}
																							
																							/*******************SECTOR FIToFICstmrCdtTrf*************************************/
																							if(document.getFIToFICstmrCdtTrf() != null){
																								
																								if(document.getFIToFICstmrCdtTrf().getGrpHdr() != null){
																									
																									if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																												.getGrpHdr().getMsgId())){
																										
																										this.ISO_011_SysAuditNumber = document.getFIToFICstmrCdtTrf()
																												.getGrpHdr().getMsgId();
																										if(document.getFIToFICstmrCdtTrf()
																															.getGrpHdr().getCreDtTm() != null){
																											
																											date = document.getFIToFICstmrCdtTrf()
																													.getGrpHdr().getCreDtTm().toGregorianCalendar().getTime();
																											this.ISO_012_LocalDatetime = date;
																											
																											if(document.getFIToFICstmrCdtTrf()
																													.getGrpHdr().getNbOfTxs() != null){
																												
																												this.ISO_006_BillAmount = document.getFIToFICstmrCdtTrf()
																											            .getGrpHdr().getNbOfTxs().doubleValue();
																												
																												if(ISO_006_BillAmount == 1){
																													
																													if(document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf() != null){
																														
																														switch (document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf().getSttlmMtd()) {
																														
																														case CLRG:
																														case COVE:
																														case INGA:
																														case INDA:
																															
																																this.ISO_035_Track2 = String.valueOf(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																		           .getSttInf().getSttlmMtd());
																															
																															break;
		
																														default:
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																							  .toUpperCase();
																															
																															break;
																														}
																														if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()!= null){
																															
																															if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
																																									.getFinInstnId() != null){
																																if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
																																		.getFinInstnId().getOthr() != null){
																																	
																																	if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																																					.getGrpHdr().getInstdAgt()
																																					.getFinInstnId().getOthr().getId())){
																																		
																																		/*OJO InstdAgt --> Institucion ACREDITANTE (en este caso la Efi Adquiriente) */
																																		this.ISO_032_ACQInsID = document.getFIToFICstmrCdtTrf()
																																				.getGrpHdr().getInstdAgt()
																																				.getFinInstnId().getOthr().getId();
																																		
																																		
																																			if(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()!= null){
																																				
																																				if(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId() != null){
																																					
																																					if(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId().getOthr() != null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getGrpHdr()
																																							       .getInstgAgt().getFinInstnId().getOthr().getId())){
																																							
																																							/*OJO InstgAgt --> es el Instructor (quien realiza el debito, en nuestro caso la EFI acq) */
																																							this.ISO_033_FWDInsID = document.getFIToFICstmrCdtTrf().getGrpHdr()
																																								       .getInstgAgt().getFinInstnId().getOthr().getId();
																																							
																																							if(this.ISO_033_FWDInsID.equalsIgnoreCase(MemoryGlobal.IdBIMOEfi)){
																																								/*--------------------------------------------ESTRUCTURA DE CREDITO CdtTrfTxInf--------------------------------*/
																																								if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf() != null){
																																								
																																									if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											                         .get(0).getPmtId() !=  null){
																																										
																																										if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											                         .get(0).getPmtId().getEndToEndId())){
																																											
																																											this.ISO_037_RetrievalReferenceNumber = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																							                         .get(0).getPmtId().getEndToEndId();
	
																																											if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																												                         .get(0).getPmtId().getTxId())){
																																												
																																												this.ISO_022_PosEntryMode = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																								                         .get(0).getPmtId().getTxId();
																																											    if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																								                         .get(0).getPmtTpInf() != null){
																																											    	
																																											    	if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																									                         .get(0).getPmtTpInf().getCtgyPurp() != null){
																																											    		
																																											    		if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										                         .get(0).getPmtTpInf().getCtgyPurp().getPrtry())){
																																											    			this.ISO_090_OriginalData = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											                         .get(0).getPmtTpInf().getCtgyPurp().getPrtry() + "|";
																																											    				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																												                         .get(0).getIntrBkSttlmAmt() != null){
																																											    					
																																											    					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													                         .get(0).getIntrBkSttlmAmt().getCcy())){
																																											    						
																																											    						this.ISO_090_OriginalData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                         .get(0).getIntrBkSttlmAmt().getCcy();
																																											    						this.ISO_049_TranCurrCode = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                         .get(0).getIntrBkSttlmAmt().getCcy().equals("USD")?840:-1;
																																											    						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                         .get(0).getIntrBkSttlmAmt().getValue() != null){
																																											    							
																																											    							this.ISO_004_AmountTransaction = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																															                         .get(0).getIntrBkSttlmAmt().getValue().doubleValue();
																																											    							
																																											    							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																															                         .get(0).getChrgBr() != null){
																																											    								
																																											    								if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                                      .get(0).getChrgBr() == 
																																														                                      com.belejanor.switcher.bimo.pacs.pacs_008_071.ChargeBearerType1Code.DEBT){
																																											    									
																																											    									this.ISO_090_OriginalData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														                                      .get(0).getChrgBr().toString();
																																											    									
																																											    									/*-------------------------- DEBTOR ---------------------------*/
																																											    									if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											    											               .get(0).getDbtr() != null){
																																											    										
																																											    										if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																									    											               .get(0).getDbtr().getNm())){
																																											    										
																																											    											this.ISO_034_PANExt = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																										    											               .get(0).getDbtr().getNm();
																																											    										}
																																											    										
																																											    										if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																									    											               .get(0).getDbtr().getCtctDtls()!= null){
																																											    											
																																											    											if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf()
																																											    													.getCdtTrfTxInf()
																																										    											               .get(0).getDbtr().getCtctDtls().getPhneNb())){
																																											    												
																																											    												this.ISO_023_CardSeq = document.getFIToFICstmrCdtTrf()
																																												    													.getCdtTrfTxInf()
																																											    											               .get(0).getDbtr().getCtctDtls().getPhneNb();
																																											    												/*-----------------------------Dbtr Acct---------------------------*/
																																											    												
																																											    												if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																											    											               .get(0).getDbtrAcct() != null){
																																											    													
																																											    													if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																												    											               .get(0).getDbtrAcct().getId() != null){
																																											    														
																																											    														if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																													    											               .get(0).getDbtrAcct().getId().getOthr() != null){
																																											    															
																																											    															if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																														    											               .get(0).getDbtrAcct().getId().getOthr().getId())){
																																											    																
																																											    																this.ISO_124_ExtendedData = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																															    											               .get(0).getDbtrAcct().getId().getOthr().getId();
																																											    																
																																											    																if(Arrays.asList(this.ISO_124_ExtendedData.split("\\|")).size() == 5){
																																											    																	
																																											    																	this.ISO_102_AccountID_1 = Arrays.asList(this.ISO_124_ExtendedData.split("\\|")).get(3);
																																											    																	
																																											    																/*-----------------------------DbtrAgt Acctor---------------------------*/
																																												    																if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																    											               .get(0).getDbtrAgt() != null){
																																												    																	
																																												    																	if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																	    											               .get(0).getDbtrAgt().getFinInstnId() != null){
																																												    																		
																																												    																		if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																		    											               .get(0).getDbtrAgt().getFinInstnId().getOthr() != null){
																																												    																			if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																			    											               .get(0).getDbtrAgt().getFinInstnId().getOthr().getId())){
																																												    																				
																																												    																				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																				    											       .get(0).getDbtrAgt().getFinInstnId().getOthr().getId()
																																																				    											       .equals(MemoryGlobal.IdBIMOEfi)){
																																													    																				/*--------------------------CdtrAgt (Instirucion Receptora)------------------------------*/
																																													    																				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																					    											               .get(0).getCdtrAgt() != null){
																																													    																					
																																													    																					if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																						    											               .get(0).getCdtrAgt().getFinInstnId() != null){
																																													    																						
																																													    																						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																							    											               .get(0).getCdtrAgt().getFinInstnId() != null){
																																													    																							
																																													    																							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																								    											               .get(0).getCdtrAgt().getFinInstnId().getOthr() != null){
																																													    																								
																																													    																								if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																									    											               .get(0).getCdtrAgt().getFinInstnId().getOthr().getId())){
																																													    																									
																																												    																										/*-------------------------Cdtr ------------------------*/
																																												    																										if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               .get(0).getCdtr() != null){
																																												    																											
																																												    																											if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               .get(0).getCdtr().getNm())){
																																												    																											
																																												    																												this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																												    											               .get(0).getCdtr().getNm() + "|";
																																												    																											}
																																												    																											if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               .get(0).getCdtr().getCtctDtls() != null){
																																												    																											
																																												    																												if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																												    											               .get(0).getCdtr().getCtctDtls().getPhneNb())){
																																												    																													
																																												    																													this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																													    											               .get(0).getCdtr().getCtctDtls().getPhneNb() + "|";
																																												    																													
																																												    																													if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																													    											               .get(0).getCdtr().getCtctDtls().getEmailAdr())){
																																																													    											            
																																												    																														this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																														    											               .get(0).getCdtr().getCtctDtls().getEmailAdr() + "^";
																																																													    											    }
																																												    																													
																																												    																													/*---------------------------------------CdtrAcct -------------------------------*/
																																												    																													if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																													    											               				.get(0).getCdtrAcct() != null){
																																												    																														if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																										    											               				.get(0).getCdtrAcct().getId() != null){
																																												    																																
																																												    																															if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																											    											               				.get(0).getCdtrAcct().getId().getOthr() != null){
																																												    																																
																																												    																																if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																												    											               				.get(0).getCdtrAcct().getId().getOthr().getId())){
																																												    																																	
																																												    																																	this.ISO_114_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																													    											               				.get(0).getCdtrAcct().getId().getOthr().getId(); 
																																												    																																	
																																												    																																		/*--------------------------------ESTRUCTURA SplmtryData---------------------------------*/
																																												    																																		
																																												    																																		if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																														    											               				.get(0).getSplmtryData() != null){
																																												    																																			
																																												    																																			if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																															    											               				.get(0).getSplmtryData().getEnvlp() != null){
																																												    																																				
																																												    																																				if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																    											               				.get(0).getSplmtryData().getEnvlp().getCnts() != null){
																																												    																																					
																																												    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getCustRef())){
																																												    																																						
																																												    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																		    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getCustRef() + "|";
																																												    																																					}
																																												    																																					
																																												    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getChanRef())){
																																													    																																						
																																													    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																			    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getChanRef() + "|";
																																													    																																				}
																																												    																																					if(!StringUtils.IsNullOrEmpty(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getAgtRef())){
																																													    																																						
																																													    																																						this.ISO_115_ExtendedData += document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																			    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getAgtRef() + "|";
																																													    																																				}
																																												    																																					if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																	    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getDbtCred() != null){
																																												    																																						
																																												    																																						if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																		    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getDbtCred() ==
																																																																		    											               						com.belejanor.switcher.bimo.pacs.pacs_008_071.DebitCredit.DEBIT){
																																												    																																							
																																												    																																							if(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																			    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getSttlmDt() != null){
																																												    																																								
																																												    																																								this.ISO_015_SettlementDatel = document.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																																																																				    											               				.get(0).getSplmtryData().getEnvlp().getCnts().getSttlmDt()
																																																																				    											               				.toGregorianCalendar().getTime();
																																												    																																								
																																												    																																								this.ISO_039_ResponseCode = "004";
																																												    																																								/*END*/
																																												    																																								
																																												    																																							}else{
																																												    																																								
																																												    																																								this.ISO_039_ResponseCode = "999";
																																																																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																																				    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/SttlmDt ES NULO"
																																																																																				    		.toUpperCase();
																																												    																																							}
																																												    																																							
																																												    																																						}else{
																																												    																																							
																																												    																																							this.ISO_039_ResponseCode = "999";
																																																																																			    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																																			    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/DbtCred ES INCORRECTO"
																																																																																			    		.toUpperCase();
																																												    																																						}
																																												    																																						
																																												    																																					}else{
																																												    																																						
																																												    																																						this.ISO_039_ResponseCode = "999";
																																																																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																																		    		+ "CdtrAgt/SplmtryData/Envlp/Cnts/DbtCred ES NULA"
																																																																																		    		.toUpperCase();
																																												    																																					}
																																												    																																					
																																												    																																				}else{
																																												    																																					
																																												    																																					this.ISO_039_ResponseCode = "999";
																																																																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																																	    		+ "CdtrAgt/SplmtryData/Envlp/Cnts ES NULA"
																																																																																	    		.toUpperCase();
																																												    																																				}
																																												    																																				
																																												    																																			}else{
																																												    																																				
																																												    																																				this.ISO_039_ResponseCode = "999";
																																																																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																																    		+ "CdtrAgt/SplmtryData/Envlp ES NULA"
																																																																																    		.toUpperCase();
																																												    																																			}
																																												    																																			
																																												    																																		}else {
																																												    																																			
																																												    																																			this.ISO_039_ResponseCode = "999";
																																																																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																															    		+ "CdtrAgt/SplmtryData ES NULA"
																																																																															    		.toUpperCase();
																																																																															}
																																												    																																		/*--------------------------------End ESTRUCTURA SplmtryData---------------------------------*/
																																												    																																	
																																												    																																	
																																												    																																}else{
																																												    																																	
																																												    																																	this.ISO_039_ResponseCode = "999";
																																																																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																													    		+ "CdtrAgt/CdtrAcct/Id/Othr/Id ES NULA"
																																																																													    		.toUpperCase();
																																												    																																}
																																												    																																
																																												    																															}else{
																																												    																																
																																												    																																this.ISO_039_ResponseCode = "999";
																																																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																												    		+ "CdtrAgt/CdtrAcct/Id/Othr ES NULA"
																																																																												    		.toUpperCase();
																																												    																															}
																																												    																															
																																												    																														}else{
																																												    																															
																																												    																															this.ISO_039_ResponseCode = "999";
																																																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																											    		+ "CdtrAgt/CdtrAcct/Id ES NULA"
																																																																											    		.toUpperCase();
																																												    																														}
																																												    																														
																																												    																													}else{
																																												    																														
																																												    																														this.ISO_039_ResponseCode = "999";
																																																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																										    		+ "CdtrAgt/CdtrAcct ES NULA"
																																																																										    		.toUpperCase();
																																												    																													}
																																												    																													/*---------------------------------------CdtrAcct Cuenta de la persona---------------*/
																																												    																													
																																												    																												}else{
																																												    																													
																																												    																													this.ISO_039_ResponseCode = "999";
																																																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																									    		+ "CdtrAgt/Cdtr/CtctDtls/PhneNb ES NULA O VACIO"
																																																																									    		.toUpperCase();
																																												    																												}
																																												    																												
																																												    																											}else{
																																												    																												
																																												    																												this.ISO_039_ResponseCode = "999";
																																																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																								    		+ "CdtrAgt/Cdtr/CtctDtls ES NULA"
																																																																								    		.toUpperCase();
																																												    																											}
																																												    																											
																																												    																										}else{
																																												    																											
																																												    																											this.ISO_039_ResponseCode = "999";
																																																																							    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																							    		+ "CdtrAgt/Cdtr NO PERTENECE A NUESTRA INSTITUCION"
																																																																							    		.toUpperCase();
																																												    																										}
																																												    																										/*-------------------------End Cdtr ------------------------*/
																																													    																										
																																													    																								}else{
																																													    																									
																																													    																									this.ISO_039_ResponseCode = "999";
																																																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																						    		+ "CdtrAgt/FinInstnId/Othr/Id ES NULO O VACIO"
																																																																						    		.toUpperCase();
																																													    																								}
																																														    																								
																																													    																							}else{
																																													    																								
			
																																														    																							this.ISO_039_ResponseCode = "999";
																																																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																					    		+ "CdtrAgt/FinInstnId/Othr ES NULA"
																																																																					    		.toUpperCase();
																																													    																							}
																																													    																							
																																													    																						}else{
																																													    																							
																																													    																							this.ISO_039_ResponseCode = "999";
																																																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																				    		+ "CdtrAgt/FinInstnId ES NULA"
																																																																				    		.toUpperCase();
																																													    																						}
																																													    																						
																																													    																					}else{
																																													    																						
																																													    																						this.ISO_039_ResponseCode = "999";
																																																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																			    		+ "CdtrAgt ES NULA"
																																																																			    		.toUpperCase();
																																													    																					}
																																													    																					
																																													    																				}else{
																																													    																					
																																													    																					this.ISO_039_ResponseCode = "999";
																																																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																		    		+ "CdtrAgt ES NULA"
																																																																		    		.toUpperCase();	
																																													    																				}
																																													    																				/*--------------------------CdtrAgt (End Instirucion Receptora)------------------------------*/
																																												    																				}else{
																																												    																					
																																												    																					this.ISO_039_ResponseCode = "999";
																																																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																																	    		+ "DbtrAgt/FinInstnId/Othr/Id NO PERTENECE A NUESTRA INSTITUCION"
																																																																	    		.toUpperCase();
																																												    																				}
																																												    																				
																																												    																			}else{
																																												    																				
																																												    																				this.ISO_039_ResponseCode = "999";
																																																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																																    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId/Othr/Id ES NULA"
																																																																    		.toUpperCase();	
																																												    																			}
																																												    																			
																																												    																		}else{
																																												    																			
																																												    																			this.ISO_039_ResponseCode = "999";
																																																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																															    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId/Othr ES NULA"
																																																															    		.toUpperCase();	
																																												    																		}
																																												    																		
																																												    																	}else{
																																												    																		
																																												    																		this.ISO_039_ResponseCode = "999";
																																																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																														    		+ "CdtTrfTxInf/DbtrAgt/FinInstnId ES NULA"
																																																														    		.toUpperCase();	
																																												    																	}
																																												    																	
																																												    																}else{
																																												    																	
																																												    																	this.ISO_039_ResponseCode = "999";
																																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																													    		+ "CdtTrfTxInf/DbtrAgt ES NULA"
																																																													    		.toUpperCase();	
																																												    																}
																																												    																/*-----------------------------End DbtrAgt Acctor---------------------------*/
																																											    																}else{
																																											    																	
																																											    																	this.ISO_039_ResponseCode = "999";
																																																												    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																												    		+ "CdtTrfTxInf/DbtrAcct/Id/Othr/Id ES INCORRECTA"
																																																												    		.toUpperCase();	
																																											    																}
																																												    																
																																											    															}else{
																																											    																
																																											    																this.ISO_039_ResponseCode = "999";
																																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																											    		+ "CdtTrfTxInf/DbtrAcct/Id/Othr/Id ES NULA O VACIA"
																																																											    		.toUpperCase();	
																																											    															}
																																											    															
																																											    														}else{
																																											    															
																																											    															this.ISO_039_ResponseCode = "999";
																																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																										    		+ "CdtTrfTxInf/DbtrAcct/Id/Othr ES NULA"
																																																										    		.toUpperCase();	
																																											    														}
																																											    														
																																											    													}else{
																																											    														
																																											    														this.ISO_039_ResponseCode = "999";
																																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																									    		+ "CdtTrfTxInf/DbtrAcct/Id ES NULA"
																																																									    		.toUpperCase();	
																																											    													}
																																											    													
																																											    												}else{
																																											    													
																																											    													this.ISO_039_ResponseCode = "999";
																																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																								    		+ "CdtTrfTxInf/DbtrAcct ES NULA"
																																																								    		.toUpperCase();	
																																											    												}
																																											    												
																																											    												/*-----------------------------End Dbtr Acct---------------------------*/
																																											    												
																																											    											}else{
																																											    												
																																											    												this.ISO_039_ResponseCode = "999";
																																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																							    		+ "CdtTrfTxInf/Dbtr/CtctDtls/PhneNb ES NULA"
																																																							    		.toUpperCase();	
																																											    											}
																																											    											
																																											    										}else{
																																											    											
																																											    											this.ISO_039_ResponseCode = "999";
																																																						    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																						    		+ "CdtTrfTxInf/Dbtr/CtctDtls ES NULA"
																																																						    		.toUpperCase();	
																																											    										}
																																											    										
																																											    									}else{
																																											    										
																																											    										this.ISO_039_ResponseCode = "999";
																																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																																					    		+ "CdtTrfTxInf/Dbtr ES INCORRECTO"
																																																					    		.toUpperCase();	
																																											    									}
																																											    									/*------------------------ FIN DEBTOR -------------------------*/
																																											    									
																																											    								}else{
																																											    									
																																											    									this.ISO_039_ResponseCode = "999";
																																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																				    		+ "CdtTrfTxInf/ChrgBr ES INCORRECTO"
																																																				    		.toUpperCase();	
																																											    								}
																																											    								
																																											    							}else{
																																											    								
																																											    								this.ISO_039_ResponseCode = "999";
																																																			    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																			    		+ "CdtTrfTxInf/ChrgBr ES NULO"
																																																			    		.toUpperCase();	
																																											    							}
																																											    							
																																											    						}else{
																																											    							
																																											    							this.ISO_039_ResponseCode = "999";
																																																		    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																		    		+ "CdtTrfTxInf/IntrBkSttlmAmt/Value ES NULO"
																																																		    		.toUpperCase();	
																																											    						}
																																											    						
																																											    					}else{
																																											    						
																																											    						this.ISO_039_ResponseCode = "999";
																																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																	    		+ "CdtTrfTxInf/IntrBkSttlmAmt/Ccy ES NULO O VACIO"
																																																	    		.toUpperCase();	
																																											    					}
																																											    					
																																											    				}else{
																																											    					
																																											    					this.ISO_039_ResponseCode = "999";
																																																    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																																    		+ "CdtTrfTxInf/IntrBkSttlmAmt ES NULO O VACIO"
																																																    		.toUpperCase();	
																																											    				}
																																											    			
																																											    		}else{
																																											    			
																																											    			this.ISO_039_ResponseCode = "999";
																																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																														    		+ "CdtTrfTxInf/PmtTpInf/CtgyPurp/Prtry ES NULO O VACIO"
																																														    		.toUpperCase();
																																											    		}
																																											    		
																																											    	}else{
																																											    	
																																											    		this.ISO_039_ResponseCode = "999";
																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																													    		+ "CdtTrfTxInf/PmtTpInf/CtgyPurp ES NULA"
																																													    		.toUpperCase();
																																											    	}
																																											    	
																																											    }else{
																																											    	
																																											    	this.ISO_039_ResponseCode = "999";
																																												    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																												    		+ "CdtTrfTxInf/PmtTpInf ES NULA"
																																												    		.toUpperCase();
																																											    }
																																												
																																											}else {
																																												
																																												this.ISO_039_ResponseCode = "999";
																																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																											    		+ "CdtTrfTxInf/PmtId/TxId ES NULA"
																																											    		.toUpperCase();
																																											}
																																											
																																										}else{
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																										    		+ "CdtTrfTxInf/PmtId/EndToEndId ES NULA"
																																										    		.toUpperCase();
																																										}
																																										
																																									}else {
																																									
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																									    		+ "CdtTrfTxInf/PmtId ES NULA"
																																									    		.toUpperCase();
																																									}
																																									
																																								}else{
																																									
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																								    		+ "CdtTrfTxInf ES NULA"
																																								    		.toUpperCase();
																																								}
																																								
																																								/*--------------------------------------------FIN ESTRUCTURA DE CREDITO CdtTrfTxInf--------------------------------*/
																																						  }else{
																																							  
																																							  this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																							    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id NO CORRESPONDE A NUESTRA INSTITUCION"
																																																  .toUpperCase();
																																						  }
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																						    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULA"
																																															  .toUpperCase();
																																						}
																																						
																																					}else {
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																					    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																														  .toUpperCase();
																																					}
																																					
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																													  .toUpperCase();
																																				}
																																				
																																			}else {
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																			    		+ "GrpHdr/InstgAgt ES NULA"
																																												  .toUpperCase();
																																			}
																																			
																																	}else {
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																																	    		+ "GrpHdr/InstdAgt/FinInstnId/Othr/Id ES NULA"
																																										  .toUpperCase();
																																	}
																																	
																																}else{
																																	
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																																    		+ "GrpHdr/InstdAgt/FinInstnId/Othr ES NULA"
																																									  .toUpperCase();
																																}
																																
																															}else{
																															
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																															    		+ "GrpHdr/InstdAgt/FinInstnId ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/InstdAgt ES NULA"
																																							  .toUpperCase();
																														}
																														
																												   }else{
																													  
																													   this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES NULO"
																																						  .toUpperCase();
																												   }
		
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																													this.ISO_039p_ResponseDetail = "EL SISTEMA SOLAMENTE SOPORTA UNA "
																															+ "TRANSACCION DE CREDITO".toUpperCase();
																												}
																												
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																												this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																														+ "/GrpHdr/NbOfTxs ES NULO".toUpperCase();
																											}
																											
																										}else {
																											
																											this.ISO_039_ResponseCode = "999";
																											this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																													+ "/GrpHdr/getCreDtTm ES NULO".toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																										this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf"
																												+ "/GrpHdr/MsgId ES NULO O VACIO".toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf"
																											+ "/GrpHdr ES NULA".toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																								this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFICstmrCdtTrf "
																										+ "ES NULA".toUpperCase();
																							}
																							
																							/*******************FIN SECTOR FIToFICstmrCdtTrf**********************************/
																							
																						}else {
																							
																							this.ISO_039_ResponseCode = "999";
																							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																									+ "Prty ES NULO".toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																						this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																								+ "Mge/OpeDate ES NULO".toUpperCase();
																					}																				
																				}else{
																					
																					this.ISO_039_ResponseCode = "999";
																					this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																							+ "Mge/RoR ES INCORRECTO".toUpperCase();
																				}
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																						+ "Mge/RoR ES NULO O VACIO".toUpperCase();
																			}
																		
																		}else {
																			

																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																					+ "Mge/IdMge ES NULO O VACIO".toUpperCase();
																		}
																		
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																				+ "Mge/Type ES NULO O VACIO".toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/"
																			+ "Mge ES NULA".toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																		+ "NO CORRESPONDE A ESTA INSTITUCION".toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "999";
															this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																	+ "ES NULO O VACIO".toUpperCase();
														}
														
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
																+ "NO CORRESPONDE A BANRED".toUpperCase();
													}
													
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
															+ "ES NULO O VACIO".toUpperCase();
												}
												
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId "
														+ "ES NULO O VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ "
													+ "ES NULO O VACIO".toUpperCase();
										}
										
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ "
												+ "ES NULO O VACIO".toUpperCase();
									}
									
								}else {
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header"
											+ "/OrigId/Service ES NULA".toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App "
										+ "ES NULO O VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel "
									+ "ES NULO O VACIO".toUpperCase();
						}
						
					}else {
					
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
								.toUpperCase();
					}
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA"
							.toUpperCase();
				}
				/*FIN HEADER*/
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA pacs_008_021 ES NULA"
						.toUpperCase();
			}
    		
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR DEBITO ", TypeMonitor.error, e);
			
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
    }
    
    /*Reverso Debito*/
    public Iso8583(com.belejanor.switcher.bimo.pacs.pacs_007_051.Document document){
    	
    	this();
    	Logger log = null;
		Date date = null;
    	try {
			
    		this.ISO_000_Message_Type = "1400";
			this.ISO_003_ProcessingCode = "011000";
			this.ISO_024_NetworkId = "555522";
    		
			if(document != null){
				
				/*HEADER*/
				if(document.getHeader() != null){
					
					if(document.getHeader().getOrigId() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getChannel())){
							
							this.ISO_018_MerchantType = document.getHeader().getOrigId().getChannel();
							
							if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId().getApp())){
								
								this.ISO_041_CardAcceptorID = document.getHeader().getOrigId().getApp();
								
								if(document.getHeader().getOrigId().getService() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
											                .getService().getIdServ())){
										
										this.ISO_120_ExtendedData = document.getHeader().getOrigId()
								                .getService().getIdServ() + "|";
										
										if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
								                .getService().getVersServ())){
											
											this.ISO_120_ExtendedData += document.getHeader().getOrigId()
									                .getService().getVersServ() + "|";
											
											if(!StringUtils.IsNullOrEmpty(document.getHeader().getOrigId()
									                .getOtherId())){
												
												this.ISO_120_ExtendedData += document.getHeader().getOrigId()
										                .getOtherId();
												
												if(!StringUtils.IsNullOrEmpty(document.getHeader().getSender())){
													
													if(document.getHeader().getSender().equals(MemoryGlobal.IdBIMOBanred)){
														
														this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOBanred + "|";
														if(!StringUtils.IsNullOrEmpty(document.getHeader().getReceiver())){
															
															if(document.getHeader().getReceiver().equals(MemoryGlobal.IdBIMOEfi)){
																
																this.ISO_121_ExtendedData += MemoryGlobal.IdBIMOEfi + "|";
																
																if(document.getHeader().getMge() != null){
																	
																	if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getType())){
																		
																		this.ISO_BitMap = document.getHeader().getMge().getType();
																		
																		if(!StringUtils.IsNullOrEmpty(document.getHeader().getMge().getIdMge())){
																			
																			this.ISO_036_Track3 = document.getHeader().getMge().getIdMge();
																		
																			if(document.getHeader().getMge().getRoR() !=null){
																				
																				if(document.getHeader().getMge().getRoR() == 
																						com.belejanor.switcher.bimo.pacs.pacs_007_051.RoRCod.REQ){
																					
																					this.ISO_121_ExtendedData += document.getHeader().getMge().getRoR() + "|";
																					if(document.getHeader().getMge().getOpeDate() != null){
																						
																						date = document.getHeader()
																					              .getMge().getOpeDate().toGregorianCalendar().getTime();
																						this.ISO_007_TransDatetime = date;
																						
																						boolean isPss = document.getHeader().isPssblDplct();
																						this.ISO_121_ExtendedData = String.valueOf(isPss) + "|";
																						
																						if(document.getHeader().getPrty() != null){
																							
																							switch (document.getHeader().getPrty()) {
																							
																							case URGT:
																							case HIGH:
																							case LOWW:	
																							case NORM:	
																								
																								this.ISO_121_ExtendedData += String.valueOf
																								           (document.getHeader().getPrty());
																								
																								break;	
																							default:
																								
																								this.ISO_039_ResponseCode = "999";
																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Prty ES INCORRECTO"
																																  .toUpperCase();
																								break;
																							}
																							
																							/*******************SECTOR FIToFICstmrCdtTrf*************************************/
																							if(document.getFIToFIPmtRvsl() != null){
																								
																								if(document.getFIToFIPmtRvsl().getGrpHdr() != null){
																									
																									if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																												.getGrpHdr().getMsgId())){
																										
																										this.ISO_011_SysAuditNumber = document.getFIToFIPmtRvsl()
																												.getGrpHdr().getMsgId();
																										if(document.getFIToFIPmtRvsl()
																															.getGrpHdr().getCreDtTm() != null){
																											
																											date = document.getFIToFIPmtRvsl()
																													.getGrpHdr().getCreDtTm().toGregorianCalendar().getTime();
																											this.ISO_012_LocalDatetime = date;
																											
																											if(document.getFIToFIPmtRvsl()
																													.getGrpHdr().getNbOfTxs() != null){
																												
																												this.ISO_006_BillAmount = document.getFIToFIPmtRvsl()
																											            .getGrpHdr().getNbOfTxs().doubleValue();
																												
																												if(ISO_006_BillAmount == 1){
																													
																													if(document.getFIToFIPmtRvsl().getGrpHdr().getSttInf() != null){
																														
																														switch (document.getFIToFIPmtRvsl().getGrpHdr().getSttInf().getSttlmMtd()) {
																														
																														case CLRG:
																														case COVE:
																														case INGA:
																														case INDA:
																															
																																this.ISO_035_Track2 = String.valueOf(document.getFIToFIPmtRvsl().getGrpHdr()
																																		           .getSttInf().getSttlmMtd());
																															
																															break;
		
																														default:
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFICstmrCdtTrf/"
																														    		+ "GrpHdr/SttInf/SttlmMtd ES INCORRECTO"
																																							  .toUpperCase();
																															
																															break;
																														}
																														if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()!= null){
																															
																															if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()
																																									.getFinInstnId() != null){
																																if(document.getFIToFIPmtRvsl().getGrpHdr().getInstdAgt()
																																		.getFinInstnId().getOthr() != null){
																																	
																																	if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																					.getGrpHdr().getInstdAgt()
																																					.getFinInstnId().getOthr().getId())){
																																	
																																		 this.ISO_032_ACQInsID =  document.getFIToFIPmtRvsl()
																																					.getGrpHdr().getInstdAgt()
																																					.getFinInstnId().getOthr().getId();
																																		
																																			if(document.getFIToFIPmtRvsl().getGrpHdr().getInstgAgt()!= null){
																																				
																																				if(document.getFIToFIPmtRvsl().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId() != null){
																																					
																																					if(document.getFIToFIPmtRvsl().getGrpHdr()
																																						       .getInstgAgt().getFinInstnId().getOthr() != null){
																																						
																																						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getGrpHdr()
																																							       .getInstgAgt().getFinInstnId().getOthr().getId())){
																																							
																																							/*OJO InstgAgt --> es el Instructor (A quien se envia el DEBITO, en nuestro caso a Nosotros) */
																																							this.ISO_033_FWDInsID = document.getFIToFIPmtRvsl().getGrpHdr()
																																								       .getInstgAgt().getFinInstnId().getOthr().getId();
																																							
																																							if(this.ISO_033_FWDInsID.equals(MemoryGlobal.IdBIMOEfi)){
																																								/*------------------------------ESTRUCTURA  TxInf de REVERSO----------------------------------*/
																																								
																																								if(document.getFIToFIPmtRvsl().getTxInf() != null){
																																									
																																									if(document.getFIToFIPmtRvsl().getTxInf().getOrgnlGrpInf() != null){
																																										
																																										if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf()
																																												                              .getOrgnlGrpInf().getOrgnlMsgId())){
																																											
																																											this.ISO_037_RetrievalReferenceNumber = document.getFIToFIPmtRvsl().getTxInf()
																																						                              .getOrgnlGrpInf().getOrgnlMsgId();
																																											
																																											if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf()
																																						                              .getOrgnlGrpInf().getOrgnlMsgNmId())){
																																												
																																												this.ISO_122_ExtendedData = document.getFIToFIPmtRvsl().getTxInf()
																																							                              .getOrgnlGrpInf().getOrgnlMsgNmId();
																																												
																																												if(document.getFIToFIPmtRvsl().getTxInf()
																																							                              .getOrgnlGrpInf().getOrgnlCreDtTm() != null){
																																													
																																													this.ISO_013_LocalDate = document.getFIToFIPmtRvsl().getTxInf()
																																								                              .getOrgnlGrpInf().getOrgnlCreDtTm().toGregorianCalendar()
																																								                              .getTime();	
																																												}
																																												
																																												if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																																					.getTxInf().getOrgnlEndToEndId())){
																																													
																																													this.ISO_090_OriginalData = document.getFIToFIPmtRvsl()
																																															.getTxInf().getOrgnlEndToEndId() + "|";
																																													
																																													this.ISO_022_PosEntryMode = document.getFIToFIPmtRvsl()
																																															.getTxInf().getOrgnlEndToEndId();
																																													
																																													if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																															.getTxInf().getOrgnlTxId())){
																																														
																																														this.ISO_090_OriginalData += document.getFIToFIPmtRvsl()
																																																.getTxInf().getOrgnlTxId();
																																													}else{
																																														
																																														StringUtils.trimEnd(this.ISO_090_OriginalData, "|");
																																													}
																																													
																																													if(document.getFIToFIPmtRvsl()
																																															.getTxInf().getRvsdIntrBkSttlmAmt() != null){
																																														
																																														if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl()
																																																.getTxInf().getRvsdIntrBkSttlmAmt().getCcy())){
																																															
																																															this.ISO_049_TranCurrCode = document.getFIToFIPmtRvsl()
																																																	.getTxInf().getRvsdIntrBkSttlmAmt().getCcy().equalsIgnoreCase("USD")?840:-1;
																																															
																																															if(document.getFIToFIPmtRvsl()
																																																	.getTxInf().getRvsdIntrBkSttlmAmt().getValue() != null){
																																																
																																																this.ISO_004_AmountTransaction = document.getFIToFIPmtRvsl()
																																																		.getTxInf().getRvsdIntrBkSttlmAmt().getValue().doubleValue();
																																																
																																																/*----------------------------SplmtryData----------------------------*/
																																																
																																																	if(document.getFIToFIPmtRvsl().getSplmtryData() != null){
																																																		
																																																		if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																				                .getEnvlp() != null){
																																																			
																																																			if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																	                .getEnvlp().getCnts() != null){
																																																				
																																																				if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																		                .getEnvlp().getCnts().getCdtrAcct())){
																																																					
																																																					this.ISO_115_ExtendedData = document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																			                .getEnvlp().getCnts().getCdtrAcct() + "^";
																																																					
																																																					if(Arrays.asList(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																		                .getEnvlp().getCnts().getCdtrAcct().split("\\|")).size() == 5){
																																																						
																																																						this.ISO_102_AccountID_1 = Arrays.asList(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																				                .getEnvlp().getCnts().getCdtrAcct().split("\\|")).get(3);
																																																						
																																																						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																				                .getEnvlp().getCnts().getPrtry())){
																																																							//aqui 
																																																							if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																					                .getEnvlp().getCnts().getPrtry().equalsIgnoreCase("PAGO")
																																																					           || document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																				                .getEnvlp().getCnts().getPrtry().equalsIgnoreCase("COBROP2P")){
																																																								
																																																								if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																						                .getEnvlp().getCnts().getPhone())){
																																																									
																																																									this.ISO_023_CardSeq = document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																							            .getEnvlp().getCnts().getPhone();
																																																									
																																																									if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																							                .getEnvlp().getCnts().getReversalRsn())){
																																																										
																																																										this.ISO_115_ExtendedData += document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																								                .getEnvlp().getCnts().getReversalRsn() + "^";
																																																										
																																																										if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																								                .getEnvlp().getCnts().getOrgnSetlmtDt() != null){
																																																											
																																																											this.ISO_115_ExtendedData += FormatUtils.DateToString
																																																													 (document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																									                .getEnvlp().getCnts().getOrgnSetlmtDt()
																																																									                .toGregorianCalendar().getTime(),"yyyy-MM-dd");
																																																											
																																																											if(document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																									                .getEnvlp().getCnts().getSttlmDt() != null){
																																																												
																																																												this.ISO_015_SettlementDatel =  document.getFIToFIPmtRvsl().getTxInf().getSplmtryData()
																																																										              .getEnvlp().getCnts().getSttlmDt().toGregorianCalendar().getTime();
																																																												
																																																												this.ISO_039_ResponseCode = "004";
																																																												//End Constructor
																																																												
																																																											}else{
																																																												
																																																												this.ISO_039_ResponseCode = "999";
																																																											    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																											    		+ "TxInf/SplmtryData/Envlp/Cnts/SttlmDt ES NULO O VACIO"
																																																											    		 .toUpperCase();
																																																											}
																																																											
																																																										}else{
																																																											
																																																											this.ISO_039_ResponseCode = "999";
																																																										    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																										    		+ "TxInf/SplmtryData/Envlp/Cnts/OrgnSetlmtDt ES NULO O VACIO"
																																																										    		 .toUpperCase();
																																																										}
																																																										
																																																									}else{
																																																										
																																																										this.ISO_039_ResponseCode = "999";
																																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																									    		+ "TxInf/SplmtryData/Envlp/Cnts/Phone ES NULO O VACIO"
																																																									    		 .toUpperCase();
																																																									}
																																																									
																																																								}else{
																																																									
																																																									this.ISO_039_ResponseCode = "999";
																																																								    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																								    		+ "TxInf/SplmtryData/Envlp/Cnts/Phone ES INCORRECTO"
																																																								    		 .toUpperCase();
																																																								}
																																																								
																																																							}else{
																																																								
																																																								this.ISO_039_ResponseCode = "999";
																																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																							    		+ "TxInf/SplmtryData/Envlp/Cnts/Prtry ES INCORRECTO"
																																																							    		 .toUpperCase();
																																																							}
																																																							
																																																						}else{
																																																							
																																																							this.ISO_039_ResponseCode = "999";
																																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																						    		+ "TxInf/SplmtryData/Envlp/Cnts/Prtry ES NULO O VACIO"
																																																															  .toUpperCase();
																																																						}
																																																						
																																																					}else{
																																																						
																																																						this.ISO_039_ResponseCode = "999";
																																																					    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																					    		+ "TxInf/SplmtryData/Envlp/Cnts/CdtrAcct ES INCORRECTO"
																																																														  .toUpperCase();
																																																					}
																																																					
																																																				}else{
																																																					
																																																					this.ISO_039_ResponseCode = "999";
																																																				    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																																				    		+ "TxInf/SplmtryData/Envlp/Cnts/CdtrAcct ES NULO O VACIO"
																																																													  .toUpperCase();
																																																				}
																																																				
																																																			}else{
																																																				
																																																				this.ISO_039_ResponseCode = "999";
																																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																			    		+ "TxInf/SplmtryData/Envlp/Cnts ES NULA"
																																																												  .toUpperCase();
																																																			}
																																																			
																																																		}else{
																																																			
																																																			this.ISO_039_ResponseCode = "999";
																																																		    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																		    		+ "TxInf/SplmtryData/Envlp ES NULA"
																																																											  .toUpperCase();
																																																		}
																																																		
																																																	}else{
																																																		
																																																		this.ISO_039_ResponseCode = "999";
																																																	    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																																	    		+ "TxInf/SplmtryData ES NULA"
																																																										  .toUpperCase();
																																																	}
																																																
																																																/*---------------------End-------SplmtryData-------------------------*/
																																															}else{
																																																
																																																this.ISO_039_ResponseCode = "999";
																																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																															    		+ "TxInf/RvsdIntrBkSttlmAmt/Value ES NULA "
																																																								  .toUpperCase();
																																															}
																																															
																																														}else {
																																															
																																															this.ISO_039_ResponseCode = "999";
																																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																														    		+ "TxInf/RvsdIntrBkSttlmAmt/Ccy ES NULA "
																																																							  .toUpperCase();
																																														}
																																														
																																													}else{
																																														
																																														this.ISO_039_ResponseCode = "999";
																																													    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																													    		+ "TxInf/RvsdIntrBkSttlmAmt ES NULA "
																																																						  .toUpperCase();
																																													}
																																													
																																												}else{
																																													
																																													this.ISO_039_ResponseCode = "999";
																																												    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																												    		+ "TxInf/OrgnlEndToEndId ES NULO O VACIO"
																																																					  .toUpperCase();
																																												}
																																												
																																											}else{
																																												
																																												this.ISO_039_ResponseCode = "999";
																																											    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																											    		+ "TxInf/OrgnlGrpInf/OrgnlMsgId ES NULO O VACIO"
																																																				  .toUpperCase();
																																											}
																																											
																																										}else{
																																											
																																											this.ISO_039_ResponseCode = "999";
																																										    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																										    		+ "TxInf/OrgnlGrpInf/OrgnlMsgId ES NULO O VACIO"
																																																			  .toUpperCase();
																																											
																																										}
																																										
																																									}else{
																																										
																																										this.ISO_039_ResponseCode = "999";
																																									    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																									    		+ "TxInf/OrgnlGrpInf/ ES NULA"
																																																		  .toUpperCase();
																																									}
																																									
																																								}else{
																																									
																																									this.ISO_039_ResponseCode = "999";
																																								    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																								    		+ "TxInf ES NULA"
																																																	  .toUpperCase();
																																								}
																																								/*------------------------------ FIN ESTRUCTURA  TxInf de REVERSO----------------------------------*/
																																								
																																							}else{
																																								
																																								this.ISO_039_ResponseCode = "999";
																																							    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																							    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id NO PERTENECE A NUESTRA INSTITUCION"
																																																  .toUpperCase();
																																							}
																																							
																																						}else{
																																							
																																							this.ISO_039_ResponseCode = "999";
																																						    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																						    		+ "GrpHdr/InstgAgt/FinInstnId/Othr/Id ES NULA"
																																															  .toUpperCase();
																																						}
																																						
																																					}else {
																																						
																																						this.ISO_039_ResponseCode = "999";
																																					    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																					    		+ "GrpHdr/InstgAgt/FinInstnId/Othr ES NULA"
																																														  .toUpperCase();
																																					}
																																					
																																				}else {
																																					
																																					this.ISO_039_ResponseCode = "999";
																																				    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																				    		+ "GrpHdr/InstgAgt/FinInstnId ES NULA"
																																													  .toUpperCase();
																																				}
																																				
																																			}else {
																																				
																																				this.ISO_039_ResponseCode = "999";
																																			    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																			    		+ "GrpHdr/InstgAgt ES NULA"
																																												  .toUpperCase();
																																			}

																																	}else {
																																		
																																		this.ISO_039_ResponseCode = "999";
																																	    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																																	    		+ "GrpHdr/InstdAgt/FinInstnId/Othr/Id ES NULA"
																																										  .toUpperCase();
																																	}
																																	
																																}else{
																																	
																																	this.ISO_039_ResponseCode = "999";
																																    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																																    		+ "GrpHdr/InstdAgt/FinInstnId/Othr ES NULA"
																																									  .toUpperCase();
																																}
																																
																															}else{
																															
																																this.ISO_039_ResponseCode = "999";
																															    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																															    		+ "GrpHdr/InstdAgt/FinInstnId ES NULA"
																																								  .toUpperCase();
																															}
																															
																														}else{
																															
																															this.ISO_039_ResponseCode = "999";
																														    this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl/"
																														    		+ "GrpHdr/InstdAgt ES NULA"
																																							  .toUpperCase();
																														}
																														
																												   }else{
																													  
																													   this.ISO_039_ResponseCode = "999";
																													    this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl/"
																													    		+ "GrpHdr/SttInf/SttlmMtd ES NULO"
																																						  .toUpperCase();
																												   }
		
																												}else{
																													
																													this.ISO_039_ResponseCode = "999";
																													this.ISO_039p_ResponseDetail = "EL SISTEMA SOLAMENTE SOPORTA UNA "
																															+ "TRANSACCION DE CREDITO".toUpperCase();
																												}
																												
																											}else{
																												
																												this.ISO_039_ResponseCode = "999";
																												this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																														+ "/GrpHdr/NbOfTxs ES NULO".toUpperCase();
																											}
																											
																										}else {
																											
																											this.ISO_039_ResponseCode = "999";
																											this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																													+ "/GrpHdr/getCreDtTm ES NULO".toUpperCase();
																										}
																										
																									}else{
																										
																										this.ISO_039_ResponseCode = "999";
																										this.ISO_039p_ResponseDetail = "EL CAMPO Document/FIToFIPmtRvsl"
																												+ "/GrpHdr/MsgId ES NULO O VACIO".toUpperCase();
																									}
																									
																								}else{
																									
																									this.ISO_039_ResponseCode = "999";
																									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl"
																											+ "/GrpHdr ES NULA".toUpperCase();
																								}
																								
																							}else{
																								
																								this.ISO_039_ResponseCode = "999";
																								this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/FIToFIPmtRvsl "
																										+ "ES NULA".toUpperCase();
																							}
																							
																							/*******************FIN SECTOR FIToFICstmrCdtTrf**********************************/
																							
																						}else {
																							
																							this.ISO_039_ResponseCode = "999";
																							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																									+ "Prty ES NULO".toUpperCase();
																						}
																						
																					}else{
																						
																						this.ISO_039_ResponseCode = "999";
																						this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																								+ "Mge/OpeDate ES NULO".toUpperCase();
																					}																				
																				}else{
																					
																					this.ISO_039_ResponseCode = "999";
																					this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																							+ "Mge/RoR ES INCORRECTO".toUpperCase();
																				}
																				
																			}else{
																				
																				this.ISO_039_ResponseCode = "999";
																				this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																						+ "Mge/RoR ES NULO O VACIO".toUpperCase();
																			}
																		
																		}else {
																			

																			this.ISO_039_ResponseCode = "999";
																			this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																					+ "Mge/IdMge ES NULO O VACIO".toUpperCase();
																		}
																		
																	}else {
																		
																		this.ISO_039_ResponseCode = "999";
																		this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/"
																				+ "Mge/Type ES NULO O VACIO".toUpperCase();
																	}
																	
																}else{
																	
																	this.ISO_039_ResponseCode = "999";
																	this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/"
																			+ "Mge ES NULA".toUpperCase();
																}
																
															}else{
																
																this.ISO_039_ResponseCode = "999";
																this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																		+ "NO CORRESPONDE A ESTA INSTITUCION".toUpperCase();
															}
															
														}else{
															
															this.ISO_039_ResponseCode = "999";
															this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Receiver "
																	+ "ES NULO O VACIO".toUpperCase();
														}
														
													}else{
														
														this.ISO_039_ResponseCode = "999";
														this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
																+ "NO CORRESPONDE A BANRED".toUpperCase();
													}
													
												}else{
													
													this.ISO_039_ResponseCode = "999";
													this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/Sender "
															+ "ES NULO O VACIO".toUpperCase();
												}
												
											}else{
												
												this.ISO_039_ResponseCode = "999";
												this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/OtherId "
														+ "ES NULO O VACIO".toUpperCase();
											}
											
										}else{
											
											this.ISO_039_ResponseCode = "999";
											this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/VersServ "
													+ "ES NULO O VACIO".toUpperCase();
										}
										
									}else{
										
										this.ISO_039_ResponseCode = "999";
										this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Service/IdServ "
												+ "ES NULO O VACIO".toUpperCase();
									}
									
								}else {
									
									this.ISO_039_ResponseCode = "999";
									this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header"
											+ "/OrigId/Service ES NULA".toUpperCase();
								}
								
							}else{
								
								this.ISO_039_ResponseCode = "999";
								this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/App "
										+ "ES NULO O VACIO".toUpperCase();
							}
							
						}else{
							
							this.ISO_039_ResponseCode = "999";
							this.ISO_039p_ResponseDetail = "EL CAMPO Document/Header/OrigId/Channel "
									+ "ES NULO O VACIO".toUpperCase();
						}
						
					}else {
					
						this.ISO_039_ResponseCode = "999";
						this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header/OrigId ES NULA"
								.toUpperCase();
					}
				}else {
					
					this.ISO_039_ResponseCode = "999";
					this.ISO_039p_ResponseDetail = "LA ESTRUCTURA Document/Header ES NULA"
							.toUpperCase();
				}
				/*FIN HEADER*/
			}else{
				
				this.ISO_039_ResponseCode = "999";
				this.ISO_039p_ResponseDetail = "LA ESTRUCTURA pacs_008_021 ES NULA"
						.toUpperCase();
			}
    		
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("ERROR CONSTRUCTOR REVERSO DEBITO ", TypeMonitor.error, e);
			this.ISO_039_ResponseCode = "070";
			this.ISO_039p_ResponseDetail = "ERROR EN PROCESOS " + e.getMessage().toUpperCase();
		}
    }
    
	public Iso8583(String tramaIso, String prefix){
		
		this();
		Logger log = null;
		try {
			
			switch (prefix) {
			case "SPI": //Opcion SPI Ordentante desde Fit1 o Fit3
				
				this.ISO_000_Message_Type = tramaIso.substring(3, 7);
				this.ISO_002_PAN = tramaIso.substring(7, 26).trim();
				this.ISO_003_ProcessingCode = tramaIso.substring(26, 32);
				this.ISO_004_AmountTransaction = (Double.parseDouble(String.valueOf(Integer
                        .parseInt(StringUtils.IsNullOrEmpty(tramaIso.substring(32, 44))?"0"
                        :tramaIso.substring(32, 44))))/100);
				this.ISO_012_LocalDatetime = FormatUtils.StringToDateIso(tramaIso.substring(44, 58)
	                     , "yyyyMMddHHmmss");
				this.ISO_011_SysAuditNumber = tramaIso.substring(58, 64);
				this.ISO_015_SettlementDatel = FormatUtils.StringToDateIso(tramaIso.substring(64, 72)
	                     , "yyyyMMdd");
				this.ISO_018_MerchantType = tramaIso.substring(72, 76);
				this.ISO_022_PosEntryMode = tramaIso.substring(76, 79);
				this.ISO_023_CardSeq = String.valueOf(Integer.parseInt(tramaIso.substring(79, 87)));
				this.ISO_024_NetworkId = tramaIso.substring(87, 93);
				this.ISO_032_ACQInsID = MemoryGlobal.BCE_Efi_VC;
				this.ISO_033_FWDInsID = tramaIso.substring(93, 101);
				this.ISO_034_PANExt = tramaIso.substring(101, 201).trim();
				this.ISO_035_Track2 = tramaIso.substring(201, 204);
				this.ISO_036_Track3 = tramaIso.substring(204, 234).trim();
				this.ISO_041_CardAcceptorID = String.valueOf(Integer.parseInt(tramaIso.substring(234, 240)));
				this.ISO_042_Card_Acc_ID_Code = String.valueOf(Integer.parseInt(tramaIso.substring(240, 246)));
				this.ISO_043_CardAcceptorLoc = tramaIso.substring(246, 316).trim();
				this.ISO_051_CardCurrCode = tramaIso.substring(316, 319).equalsIgnoreCase("USD")?840:0;
				this.ISO_102_AccountID_1 = tramaIso.substring(319, 339).trim();
				this.ISO_103_AccountID_2 = tramaIso.substring(339, 359).trim();
				this.ISO_114_ExtendedData = tramaIso.substring(359, 459).trim();
				this.ISO_115_ExtendedData = tramaIso.substring(459, 478).trim();
				this.ISO_120_ExtendedData = tramaIso.substring(478, 488).trim();
				this.ISO_121_ExtendedData = tramaIso.substring(488, 538).trim();
				this.ISO_039_ResponseCode = "004";
				break;
				
			case "PAG":
				
				String campo = StringUtils.Empty();
				try {
					
					this.ISO_000_Message_Type = "1200";
					this.ISO_041_CardAcceptorID = "FIT1_WEB";
					campo = "PREFIJO DE LA TRANSACCION";
					this.ISO_BitMap = Arrays.asList(tramaIso.split("\\|")).get(0);
					campo = "NRO. SECUENCIAL UNICO DE LA TRANSACCION";
					this.ISO_011_SysAuditNumber = Arrays.asList(tramaIso.split("\\|")).get(1);
					if(StringUtils.IsNullOrEmpty(this.ISO_011_SysAuditNumber.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
						break;
					}
					this.ISO_018_MerchantType = "0007";
					this.ISO_024_NetworkId = "555550";
					campo = "CODIGO DEL COMERCIO";
					this.ISO_002_PAN = Arrays.asList(tramaIso.split("\\|")).get(2);
					if(StringUtils.IsNullOrEmpty(this.ISO_002_PAN.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
						break;
					}
					if(Arrays.asList(tramaIso.split("\\|")).get(0).equals("PAG_I") || 
							Arrays.asList(tramaIso.split("\\|")).get(0).equals("PAG_A")){
						if(Arrays.asList(tramaIso.split("\\|")).get(0).equals("PAG_I"))
							this.ISO_003_ProcessingCode = "908000";
						else
							this.ISO_003_ProcessingCode = "908001";
						campo = "RAZON SOCIAL O NOMBRE DEL COMERCIO";
						this.ISO_034_PANExt = Arrays.asList(tramaIso.split("\\|")).get(3);
						if(StringUtils.IsNullOrEmpty(ISO_034_PANExt.trim())){
						
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						
						campo = "DIRECCION FISICA DEL COMERCIO";
						this.ISO_120_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(4);
						if(StringUtils.IsNullOrEmpty(ISO_120_ExtendedData.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "NRO. DOCUMENTO DE LA PERSONA ASOCIADA AL COMERCIO";
						this.ISO_121_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(5); 
						if(StringUtils.IsNullOrEmpty(this.ISO_121_ExtendedData.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "NOMBRE DEL SOCIO ASOCIADO AL COMERCIO";
						this.ISO_122_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(6); 
						if(StringUtils.IsNullOrEmpty(this.ISO_122_ExtendedData.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "NUMERO DE LA CUENTA ASOCIADA AL COMERCIO";
						this.ISO_102_AccountID_1 = Arrays.asList(tramaIso.split("\\|")).get(7); 
						if(StringUtils.IsNullOrEmpty(this.ISO_102_AccountID_1.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "ACTIVIDAD ECONOMICA DESCRIPTIVA DEL COMERCIO";
						this.ISO_114_ExtendedData =  Arrays.asList(tramaIso.split("\\|")).get(8);
						if(StringUtils.IsNullOrEmpty(this.ISO_114_ExtendedData.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "CODIGO ESTADO DEL COMERCIO";
						this.ISO_123_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(9);
						if(StringUtils.IsNullOrEmpty(this.ISO_123_ExtendedData.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						campo = "CODIGO DE LA PERSONA (CPERSONA)";
						this.ISO_023_CardSeq = Arrays.asList(tramaIso.split("\\|")).get(10);
						if(StringUtils.IsNullOrEmpty(ISO_023_CardSeq.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
							break;
						}
						
					}else if(Arrays.asList(tramaIso.split("\\|")).get(0).startsWith("PAG_C")){
						this.ISO_003_ProcessingCode = "308000";
						
						if(Arrays.asList(tramaIso.split("\\|")).get(0).equalsIgnoreCase("PAG_C_D")){
							campo = "NRO. DOCUMENTO DEL COMERCIO";
							this.ISO_121_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(2);
							if(StringUtils.IsNullOrEmpty(this.ISO_121_ExtendedData.trim())){
								
								this.ISO_039_ResponseCode = "905";
								this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
								break;
							}
							
						}else if (Arrays.asList(tramaIso.split("\\|")).get(0).equalsIgnoreCase("PAG_C_CD")) {
							campo = "NRO. DOCUMENTO DEL COMERCIO";
							this.ISO_121_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(3);
							if(StringUtils.IsNullOrEmpty(this.ISO_121_ExtendedData.trim())){
								
								this.ISO_039_ResponseCode = "905";
								this.ISO_039p_ResponseDetail = campo + " INVALIDO O AUSENTE";
								break;
							}
						}
					}
					if(StringUtils.IsNullOrEmpty(this.ISO_003_ProcessingCode.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = "PREFIJO DE LA TRANSACCION INVALIDO O AUSENTE";
						break;
					}
					this.ISO_039_ResponseCode = "004";
					
				} catch (Exception e) {
					
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = "ERROR EN ENVIO DE LA TRAMA, ERROR AL RECUPERAR CAMPO: " 
					      + campo ;
					log = new Logger();
					log.WriteLogMonitor("ERROR CONSTRUCTOR PREFIX ISO ", TypeMonitor.error, e);
				}
				break;
			case "PDE":
				String alerta = StringUtils.Empty();
				try {
					
					alerta = "PREFIJO DE LA TRANSACCION";
					this.ISO_BitMap = Arrays.asList(tramaIso.split("\\|")).get(0);
					
					int longitud = 0;
					int ajuste = 0;
					longitud = tramaIso.split("\\|").length;
					if(this.ISO_BitMap.equalsIgnoreCase("PDE_A") || this.ISO_BitMap.equalsIgnoreCase("PDE_D")){
						if(longitud != 20){
							
							this.ISO_039_ResponseCode = "909";
							this.ISO_039p_ResponseDetail = "TRAMA INVALIDA (LONGITUD SEPARADORES)";
							break;
						}
						
					}else{
						if(longitud != 21){
							
							this.ISO_039_ResponseCode = "909";
							this.ISO_039p_ResponseDetail = "TRAMA INVALIDA (LONGITUD SEPARADORES)";
							break;
						}
					}
					
					alerta = "TIPO MENSAJE DE LA TRANSACCION";
					this.ISO_000_Message_Type = Arrays.asList(tramaIso.split("\\|")).get(1);
					if(StringUtils.IsNullOrEmpty(this.ISO_000_Message_Type.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "NUMERO DE DOCUMENTO DE LA PERSONA";
					this.ISO_002_PAN = Arrays.asList(tramaIso.split("\\|")).get(2);
					if(StringUtils.IsNullOrEmpty(this.ISO_002_PAN.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO DE LA TRANSACCION";
					this.ISO_003_ProcessingCode = Arrays.asList(tramaIso.split("\\|")).get(3);
					if(StringUtils.IsNullOrEmpty(this.ISO_003_ProcessingCode.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "FECHA DE TRANSMISION DE LA TRANSACCION";
					this.ISO_007_TransDatetime = FormatUtils.StringToDate(Arrays.asList(tramaIso.split("\\|"))
							                    .get(4), "yyyy-MM-dd HH:mm:ss") ;
					if(this.ISO_007_TransDatetime == null){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "NUMERO SECUENCIAL UNICO DE LA TRANSACCION";
					this.ISO_011_SysAuditNumber = Arrays.asList(tramaIso.split("\\|")).get(5);
					if(StringUtils.IsNullOrEmpty(this.ISO_011_SysAuditNumber.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "FECHA DE LA TRANSACCION";
					this.ISO_012_LocalDatetime = FormatUtils.StringToDate(Arrays.asList(tramaIso.split("\\|"))
							                    .get(6), "yyyy-MM-dd HH:mm:ss");
					if(this.ISO_012_LocalDatetime == null){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "FECHA CONTABLE DE LA TRANSACCION";
					this.ISO_015_SettlementDatel = FormatUtils.StringToDate(Arrays.asList(tramaIso.split("\\|"))
		                    					.get(7), "yyyy-MM-dd");
					if(this.ISO_015_SettlementDatel == null){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO CANAL DE LA TRANSACCION";
					this.ISO_018_MerchantType = Arrays.asList(tramaIso.split("\\|")).get(8);
					if(StringUtils.IsNullOrEmpty(this.ISO_018_MerchantType.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO DE LA RED DE LA TRANSACCION";
					this.ISO_024_NetworkId = Arrays.asList(tramaIso.split("\\|")).get(9);
					if(StringUtils.IsNullOrEmpty(this.ISO_024_NetworkId.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "APELLIDOS Y NOMBRES DE LA PERSONA";
					this.ISO_034_PANExt = Arrays.asList(tramaIso.split("\\|")).get(10);
					if(StringUtils.IsNullOrEmpty(this.ISO_034_PANExt.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO DE LA SUCURSAL";
					this.ISO_041_CardAcceptorID = Arrays.asList(tramaIso.split("\\|")).get(11);
					if(StringUtils.IsNullOrEmpty(this.ISO_041_CardAcceptorID.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO DE LA OFICINA";
					this.ISO_042_Card_Acc_ID_Code = Arrays.asList(tramaIso.split("\\|")).get(12);
					if(StringUtils.IsNullOrEmpty(this.ISO_042_Card_Acc_ID_Code.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "DESCRIPCION DE LA OFICINA";
					this.ISO_043_CardAcceptorLoc = Arrays.asList(tramaIso.split("\\|")).get(13);
					if(StringUtils.IsNullOrEmpty(this.ISO_043_CardAcceptorLoc.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					if(this.ISO_BitMap.equalsIgnoreCase("PDE_C")){
						alerta = "CODIGO OTP";
						this.ISO_052_PinBlock = Arrays.asList(tramaIso.split("\\|")).get(14);
						if(StringUtils.IsNullOrEmpty(this.ISO_052_PinBlock.trim())){
							
							this.ISO_039_ResponseCode = "905";
							this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
							break;
						}
						ajuste = 1;
					}
					alerta = "NUMERO DE CUENTA A ASOCIAR/DESASOCIAR";
					this.ISO_102_AccountID_1 = Arrays.asList(tramaIso.split("\\|")).get(14 + ajuste);
					if(StringUtils.IsNullOrEmpty(this.ISO_102_AccountID_1.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "NUMERO DE CELULAR A ASOCIAR/DESACOCIAR";
					this.ISO_114_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(15 + ajuste);
					if(StringUtils.IsNullOrEmpty(this.ISO_114_ExtendedData.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CODIGO DE OPERADORA";
					this.ISO_115_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(16 + ajuste);
					if(StringUtils.IsNullOrEmpty(this.ISO_115_ExtendedData.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "NOMBRE USUARIO INSTITUCION (BANK)";
					this.ISO_120_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(17 + ajuste);
					if(StringUtils.IsNullOrEmpty(this.ISO_120_ExtendedData.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "NUMERO DE CUENTA DE LA EFI EN EL BCE (BANKCODE)";
					this.ISO_121_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(18 + ajuste);
					if(StringUtils.IsNullOrEmpty(this.ISO_121_ExtendedData.trim())){
						
						this.ISO_039_ResponseCode = "905";
						this.ISO_039p_ResponseDetail = alerta + " INVALIDO O AUSENTE";
						break;
					}
					alerta = "CORREO ELECTRONICO DE LA PERSONA";
					this.ISO_122_ExtendedData = Arrays.asList(tramaIso.split("\\|")).get(19 + ajuste);
					
					this.ISO_039_ResponseCode = "004";
					
				} catch (Exception e) {
					this.ISO_039_ResponseCode = "909";
					this.ISO_039p_ResponseDetail = "ERROR EN ENVIO DE LA TRAMA, ERROR AL RECUPERAR CAMPO: " 
					      + alerta ;
					log = new Logger();
					log.WriteLogMonitor("ERROR CONSTRUCTOR PREFIX ISO ", TypeMonitor.error, e);
				}
				
				break;
				
			case "{\"I": //Json
				
				String xmlIso = XML.toString(new JSONObject(tramaIso));
				Iso8583 iso = (Iso8583) SerializationObject.XMLToObjectJson(xmlIso);
				
				this.ISO_000_Message_Type = iso.ISO_000_Message_Type;
				this.ISO_002_PAN = iso.ISO_002_PAN;
				this.ISO_003_ProcessingCode = iso.ISO_003_ProcessingCode;
				this.ISO_004_AmountTransaction = iso.ISO_004_AmountTransaction;
				this.ISO_006_BillAmount = iso.ISO_006_BillAmount;
				this.ISO_007_TransDatetime = iso.ISO_007_TransDatetime;
				this.ISO_008_BillFeeAmount = iso.ISO_008_BillFeeAmount;
				this.ISO_011_SysAuditNumber = iso.ISO_011_SysAuditNumber;
				this.ISO_012_LocalDatetime = iso.ISO_012_LocalDatetime;
				this.ISO_013_LocalDate = iso.ISO_013_LocalDate;
				this.ISO_015_SettlementDatel = iso.ISO_015_SettlementDatel;
				this.ISO_018_MerchantType = iso.ISO_018_MerchantType;
				this.ISO_019_AcqCountryCode = iso.ISO_019_AcqCountryCode;
				this.ISO_022_PosEntryMode = iso.ISO_022_PosEntryMode;
				this.ISO_023_CardSeq = iso.ISO_023_CardSeq;
				this.ISO_024_NetworkId = iso.ISO_024_NetworkId;
				this.ISO_028_TranFeeAmount = iso.ISO_028_TranFeeAmount;
				this.ISO_029_SettlementFee = iso.ISO_029_SettlementFee;
				this.ISO_030_ProcFee = iso.ISO_030_ProcFee;
				this.ISO_032_ACQInsID = iso.ISO_032_ACQInsID;
				this.ISO_033_FWDInsID = iso.ISO_033_FWDInsID;
				this.ISO_034_PANExt = iso.ISO_034_PANExt;
				this.ISO_035_Track2 = iso.ISO_035_Track2;
				this.ISO_036_Track3 = iso.ISO_036_Track3;
				this.ISO_037_RetrievalReferenceNumber = iso.ISO_037_RetrievalReferenceNumber;
				this.ISO_038_AutorizationNumber = iso.ISO_038_AutorizationNumber;
				this.ISO_039_ResponseCode = iso.ISO_039_ResponseCode;
				this.ISO_039p_ResponseDetail = iso.ISO_039p_ResponseDetail;
				this.ISO_041_CardAcceptorID = iso.ISO_041_CardAcceptorID;
				this.ISO_042_Card_Acc_ID_Code = iso.ISO_042_Card_Acc_ID_Code;
				this.ISO_043_CardAcceptorLoc = iso.ISO_043_CardAcceptorLoc;
				this.ISO_044_AddRespData = iso.ISO_044_AddRespData;
				this.ISO_049_TranCurrCode = iso.ISO_049_TranCurrCode;
				this.ISO_051_CardCurrCode = iso.ISO_051_CardCurrCode;
				this.ISO_052_PinBlock = iso.ISO_052_PinBlock;
				this.ISO_054_AditionalAmounts = iso.ISO_054_AditionalAmounts;
				this.ISO_055_EMV = iso.ISO_055_EMV;
				this.ISO_090_OriginalData = iso.ISO_090_OriginalData;
				this.ISO_102_AccountID_1 = iso.ISO_102_AccountID_1;
				this.ISO_103_AccountID_2 = iso.ISO_103_AccountID_2;
				this.ISO_104_TranDescription = iso.ISO_104_TranDescription;
				this.ISO_114_ExtendedData = iso.ISO_114_ExtendedData;
				this.ISO_115_ExtendedData = iso.ISO_115_ExtendedData;
				this.ISO_120_ExtendedData = iso.ISO_120_ExtendedData;
				this.ISO_121_ExtendedData = iso.ISO_121_ExtendedData;
				this.ISO_122_ExtendedData = iso.ISO_122_ExtendedData;
				this.ISO_123_ExtendedData = iso.ISO_123_ExtendedData;
				this.ISO_124_ExtendedData = iso.ISO_124_ExtendedData;
				this.ISO_BitMap = iso.ISO_BitMap;
				
				this.ISO_039_ResponseCode = "004" ;
				
				break;
				
			case "120":
			case "140":
				
					if(tramaIso.length() == 659){
						
						this.ISO_000_Message_Type = tramaIso.substring(0,4);
						this.ISO_002_PAN = tramaIso.substring(4,23);
						this.ISO_003_ProcessingCode = tramaIso.substring(23,29);
						this.ISO_004_AmountTransaction = (Double.parseDouble(String.valueOf(Integer
		                        .parseInt(StringUtils.IsNullOrEmpty(tramaIso.substring(29, 41))?"0"
		                                :tramaIso.substring(29, 41))))/100);
						this.ISO_007_TransDatetime = FormatUtils.StringToDate("20" + tramaIso.substring(41, 51), 
								                     "yyyyMMddHHmm");
						this.ISO_013_LocalDate = FormatUtils.StringToDate(FormatUtils.DateToString(new Date(), "yyyy")
								                + tramaIso.substring(51, 55), "yyyyMMdd");
						this.ISO_011_SysAuditNumber = tramaIso.substring(55, 61);
						this.ISO_012_LocalDatetime = FormatUtils.StringToDate("20" + tramaIso.substring(61, 73), 
			                     					 "yyyyMMddHHmmss");
						this.ISO_019_AcqCountryCode =  tramaIso.substring(73, 76);
						this.ISO_023_CardSeq = tramaIso.substring(76, 100); //24 espacios
						this.ISO_032_ACQInsID = tramaIso.substring(100,111);
						this.ISO_033_FWDInsID = tramaIso.substring(111,122);
						this.ISO_037_RetrievalReferenceNumber = tramaIso.substring(122,134);
						this.ISO_039_ResponseCode = tramaIso.substring(134,137);
						this.ISO_041_CardAcceptorID = tramaIso.substring(137,145);
						this.ISO_042_Card_Acc_ID_Code = tramaIso.substring(145,160);
						this.ISO_043_CardAcceptorLoc = tramaIso.substring(160,202);
						this.ISO_049_TranCurrCode = Double.parseDouble(tramaIso.substring(202,205));
						this.ISO_054_AditionalAmounts = tramaIso.substring(205,325);
						this.ISO_024_NetworkId = tramaIso.substring(325,331);
						this.ISO_018_MerchantType = "0020";
						this.ISO_015_SettlementDatel = FormatUtils.StringToDate(FormatUtils.DateToString(new Date(), "yyyy")
				                + tramaIso.substring(331, 335), "yyyyMMdd");
						this.ISO_120_ExtendedData = tramaIso.substring(335, 635);
						this.ISO_102_AccountID_1 = tramaIso.substring(635,647);
						this.ISO_103_AccountID_2 = tramaIso.substring(647,659);
						this.ISO_039_ResponseCode = "004";
						this.ISO_114_ExtendedData = tramaIso;
						
					}else{
						
						this.ISO_039_ResponseCode = "909";
						this.ISO_039p_ResponseDetail = "TRAMA LONGITUD INVALIDA";
					}
				
				break;
			default:
				
				 this.ISO_039_ResponseCode = "908";
				 this.ISO_039p_ResponseDetail = "PREFIX ISO NO RECONICIDA";
				
				break;
			}
			
		} catch (Exception e) {
			
			  this.ISO_039_ResponseCode = "909";
			  this.ISO_039p_ResponseDetail = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
		}
	}
	
	public String Iso8583ObjToIsoText(Iso8583 iso, String prefix, boolean isUpperVariable){
		
		String data = StringUtils.Empty();
		StringBuilder str = new StringBuilder();
		try {
			
			switch (prefix) {
			case "SPI":
				
				str.append(iso.getISO_000_Message_Type());
				str.append(StringUtils.padRight(iso.getISO_002_PAN(),19," "));
				str.append(iso.getISO_003_ProcessingCode());
				str.append(String.format("%013.2f", iso.getISO_004_AmountTransaction())
						   .replace(",", StringUtils.Empty()).replace(".", StringUtils.Empty()));
				str.append(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMddHHmmss"));
				str.append(iso.getISO_011_SysAuditNumber());
				str.append(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "yyyyMMdd"));
				str.append(iso.getISO_018_MerchantType());
				str.append(iso.getISO_022_PosEntryMode());
				str.append(StringUtils.padLeft(iso.getISO_023_CardSeq(), 8, "0"));
				str.append(iso.getISO_024_NetworkId());
				str.append(iso.getISO_033_FWDInsID());
				str.append(StringUtils.padRight(iso.getISO_034_PANExt().trim(), 100, " "));
				str.append(iso.getISO_035_Track2().trim());
				str.append(StringUtils.padRight(iso.getISO_036_Track3(),30," ").substring(0,30));
				str.append(iso.getISO_039_ResponseCode());
				str.append(StringUtils.padRight(iso.getISO_039p_ResponseDetail().trim(),200," ").substring(0, 200));
				str.append(StringUtils.padLeft(iso.getISO_041_CardAcceptorID().trim(),6,"0"));
				str.append(StringUtils.padLeft(iso.getISO_042_Card_Acc_ID_Code().trim(),6,"0"));
				str.append(StringUtils.padRight(iso.getISO_043_CardAcceptorLoc().trim(),70," "));
				str.append(iso.getISO_051_CardCurrCode() == 840 ? "USD":"XXX");
				str.append(StringUtils.padRight(iso.getISO_102_AccountID_1(), 20, " "));
				str.append(StringUtils.padRight(iso.getISO_103_AccountID_2(), 20, " "));
				str.append(StringUtils.padRight(iso.getISO_114_ExtendedData(), 100, " "));
				str.append(StringUtils.padRight(iso.getISO_115_ExtendedData(), 19, " "));
				str.append(StringUtils.padRight(iso.getISO_120_ExtendedData(), 10, " "));
				str.append(StringUtils.padRight(iso.getISO_121_ExtendedData(), 50, " "));
				
				break;
				
			case "PAG":
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData().replace(prefix, StringUtils.Empty()));
					iso.setISO_115_ExtendedData(StringUtils.trimEnd(iso.getISO_115_ExtendedData(),"|"));
					iso.setISO_115_ExtendedData(StringUtils.trimEnd(iso.getISO_115_ExtendedData(),"^"));
					str.append(iso.getISO_115_ExtendedData());
					
				}else{
					
					String res = iso.getISO_BitMap() + "_R|" + iso.getISO_011_SysAuditNumber() + "|" + 
					             iso.getISO_039_ResponseCode() + "|" + iso.getISO_039p_ResponseDetail();
					res = res.replace(prefix, StringUtils.Empty());
					str.append(res);
				}
				break;
				
			case "PDE":
				
				String resPde = iso.getISO_BitMap() + "|" + iso.getISO_000_Message_Type() + "|" 
							    + iso.getISO_003_ProcessingCode() + "|" + iso.getISO_011_SysAuditNumber() + "|" 
							    + iso.getISO_039_ResponseCode() + "|" + iso.getISO_039p_ResponseDetail();
				resPde = resPde.replace(prefix, StringUtils.Empty());
				           
				str.append(resPde);
				
				break;
			case "{\"I": //Json
				
				//Aqui para modificar 115 con CDATA
				iso.setISO_115_ExtendedData("<![CDATA[" + iso.getISO_115_ExtendedData() + "]]>");
				String xmlIso = SerializationObject.ObjectToString(iso, Iso8583.class); 
				JSONObject xmlJSONObj = XML.toJSONObject(xmlIso);
	            String jsonPrettyPrintString = xmlJSONObj.toString(4);
	            boolean flag = StringUtils.isUpper(jsonPrettyPrintString.substring(0,3));
	            
	            
	            if(!flag)
	            	jsonPrettyPrintString = jsonPrettyPrintString.replace("{\"i",prefix);
	            jsonPrettyPrintString = jsonPrettyPrintString.replace(prefix, StringUtils.Empty());
	            str.append(jsonPrettyPrintString);
	            
				break;
			case "120":
			case "140":
				
				str.append(iso.getISO_000_Message_Type());
				str.append(iso.getISO_002_PAN());
				str.append(iso.getISO_003_ProcessingCode());
				str.append(String.format("%013.2f", iso.getISO_004_AmountTransaction())
						   .replace(",", StringUtils.Empty()).replace(".", StringUtils.Empty()));
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
				str.append(String.valueOf(iso.getISO_049_TranCurrCode()).replace(".0", StringUtils.Empty()));
				str.append(StringUtils.padRight(iso.getISO_054_AditionalAmounts(),120," ").substring(0,120));
				str.append(iso.getISO_024_NetworkId());
				str.append(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "MMdd"));
				str.append(iso.getISO_120_ExtendedData());
				str.append(iso.getISO_102_AccountID_1());
				str.append(iso.getISO_103_AccountID_2());
				
				
				break;
			default:
				str.append(StringUtils.Empty());
				data = "SYSTEM_MAL_FUNCTION: PREFIX RESPONSE ISO NO CONFIGURADO";
				str.append(data);
				break;
			}
			
		} catch (Exception e) {
			str.append(StringUtils.Empty());
			data = "SYSTEM_MAL_FUNCTION: RESPONSE -> " + 
			       GeneralUtils.ExceptionToString("ERROR ", e, false);
			str.append(data);
		}
		data = str.toString();
		iso.setISO_115_ExtendedData(data);
		return data;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {		
		return super.clone();
	}
	
}

