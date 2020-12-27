package com.belejanor.switcher.memcached;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Iso8583_Retrieve implements Serializable{

	
	private String ISO_000_Message_Type;
	private String ISO_BitMap;
	private String ISO_002_PAN;
	private String ISO_003_ProcessingCode;
	private double ISO_004_AmountTransaction;
	private double ISO_006_BillAmount;
	private String ISO_007_TransDatetime;
	private double ISO_008_BillFeeAmount;
	private String ISO_011_SysAuditNumber;	
	private String ISO_012_LocalDatetime;
	private String ISO_013_LocalDate;
	private String ISO_015_SettlementDatel;
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
	private String ISO_037_RetrievalReference;
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
	
	public Iso8583_Retrieve() {
		
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

	public String getISO_007_TransDatetime() {
		return ISO_007_TransDatetime;
	}

	public void setISO_007_TransDatetime(String iSO_007_TransDatetime) {
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

	public String getISO_012_LocalDatetime() {
		return ISO_012_LocalDatetime;
	}

	public void setISO_012_LocalDatetime(String iSO_012_LocalDatetime) {
		ISO_012_LocalDatetime = iSO_012_LocalDatetime;
	}

	public String getISO_013_LocalDate() {
		return ISO_013_LocalDate;
	}

	public void setISO_013_LocalDate(String iSO_013_LocalDate) {
		ISO_013_LocalDate = iSO_013_LocalDate;
	}

	public String getISO_015_SettlementDatel() {
		return ISO_015_SettlementDatel;
	}

	public void setISO_015_SettlementDatel(String iSO_015_SettlementDatel) {
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

	public String getISO_037_RetrievalReference() {
		return ISO_037_RetrievalReference;
	}

	public void setISO_037_RetrievalReference(String iSO_037_RetrievalReference) {
		ISO_037_RetrievalReference = iSO_037_RetrievalReference;
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

	
	
	
}
