package com.belejanor.switcher.banred.pagodirecto.struct;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.parser.PagoDirectoParser;
import com.belejanor.switcher.utils.SerializationObject;

public class ZTester {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
	
		//String a = "01099";
		//System.out.println(a.substring(3,5));
		DecimalFormat df = new DecimalFormat("#.##");

	    df.setRoundingMode(RoundingMode.FLOOR);

	    double result = new Double(df.format(3.545555555));
	    System.out.println(result);
		
		String a = "011020";
		System.out.println(a.substring(4,6));
		
	   ////////////////////////// CONSULTAS ORDENANTES //////////////////////////////////
	   
	   //TEST1
	   /*TrxConsultaPagoDirectoRequest trxConsultaRq = TestRequestConsulta();
	   System.out.println(trxConsultaRq.getMessage_sequence_number_9004());
	   PagoDirectoParser parser = new PagoDirectoParser();
	   byte[] tramaBytesConsultaIn = parser.TrxConsultaPagoDirectoRequestToByte(trxConsultaRq); 
	   System.out.println(new String(tramaBytesConsultaIn));
	   
	   SerializationObject serial = new SerializationObject();
	   @SuppressWarnings("static-access")
	   String xml = serial.ObjectToString(trxConsultaRq,TrxConsultaPagoDirectoRequest.class);
	   System.out.println(xml);*/
	   
	   //TEST2
	   /*MemoryGlobal.BanredPagoDirectoAbaOrdenante = "604811";
	   Iso8583 iso = new Iso8583();
	   iso.setISO_BitMap("072978");
	   iso.setISO_000_Message_Type("1200");
	   iso.setISO_002_PAN("1714891064");
	   iso.setISO_003_ProcessingCode("311020");
	   iso.setISO_004_AmountTransaction(5.89);
	   iso.setISO_011_SysAuditNumber("000001");
	   iso.setISO_018_MerchantType("0005");
	   iso.setISO_023_CardSeq("1717678922");
	   iso.setISO_024_NetworkId("555222");
	   iso.setISO_032_ACQInsID("0512");
	   iso.setISO_033_FWDInsID("704");
	   iso.setISO_034_PANExt("ORELLANA LOPEZ JUAN CARLOS");
	   iso.setISO_036_Track3("LESCANO VILLAFUERTE JANETH PAULINA DE LOS ANGELES");
	   iso.setISO_042_Card_Acc_ID_Code("1");
	   iso.setISO_043_CardAcceptorLoc("1");
	   iso.setISO_102_AccountID_1("4079299929001");
	   //iso.setISO_103_AccountID_2("5180309090000298");
	   iso.setISO_103_AccountID_2("2290020020");
	   iso.setISO_120_ExtendedData("POR DEUDAS EN EL DESTINO");
	   iso.setISO_121_ExtendedData("OTAVALO");
	   
	   TrxConsultaPagoDirectoRequest trx = new TrxConsultaPagoDirectoRequest();
	   trx = trx.Iso8583ToTrxConsultaPagoDirectoRequest(iso);
	   
	   SerializationObject serial = new SerializationObject();
	   String xml = serial.ObjectToString(trx,TrxConsultaPagoDirectoRequest.class);
	   System.out.println(xml);
	   
	   byte[] array = trx.TrxConsultaPagoDirectoRequestToByteArray(trx);
	   System.out.println(new String(array));
	   
	   /*RECEPTOR++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	   /*Prueba de bytes a estructura TrxConsultaPagoDirectoRequest*/
		
	   
	   /*String trama = "TR01853008052168016800163+160816100029000008050230001608162MEJIA YUMBLA MICHELLE ALEXANDRA0000003607310000123400000000000000000+00151608160400000109997168832PD   ;36073100001234=00000000000000000000000?02AULESTIA CANDO CESAR ANTONIO       CON TARJET0951412832   GUAYAGUIL 301500000000000000000916774110         ";
	   TrxConsultaPagoDirectoRequest trx1 =  new TrxConsultaPagoDirectoRequest();
	   trx1 = trx1.byteArraytoStruct(trama.getBytes());
	   SerializationObject serial = new SerializationObject();
	   String xml1 = serial.ObjectToString(trx1,TrxConsultaPagoDirectoRequest.class);
	   System.out.println(xml1);
	   
	   //Prueba Parseo de TrxConsultaPagoDirectoRequest a ISO8583
	   PagoDirectoParser parser1 = new PagoDirectoParser();
	   Iso8583 iso1 = parser1.convertTrxConsultaPagoDirectoRequestToIso8583(trx1);
	   System.out.println(iso1.getISO_114_ExtendedData());
	   
	   //PruebaRepuesta hacia Banred
	   parser1 =  new PagoDirectoParser();
	   TrxConsultaPagoDirectoResponse resp = new TrxConsultaPagoDirectoResponse("0000");
	   resp = parser1.Iso8583ToTrxConsultaPagoDirectoResponse(trx1, iso1);
	   byte[] respo = parser1.TrxConsultaPagoDirectoResponseToByte(resp);
	   
	   String hex = FormatUtils.bytesToHex(respo);
	   byte[] xx = FormatUtils.hexStringToByte(hex.replace("-", ""));
	   
	   if(respo[0] == 0x54 && respo[1] == 0x43) {
		   System.out.println("SI");
	   }
	   
	   System.out.println(new String(respo));*/
		
	   
	   
	   ////////////////////////////////////////////////////////////////////////////////////
		
		
	  /*PRUEBAS DE TRANSFERENCIAS*/
		/*ordenante*/
		/*MemoryGlobal.BanredPagoDirectoAbaOrdenante = "604811";
		   Iso8583 iso = new Iso8583();
		   iso.setISO_BitMap("072978");
		   iso.setISO_000_Message_Type("1200");
		   iso.setISO_002_PAN("1714891064");
		   iso.setISO_003_ProcessingCode("011010");
		   iso.setISO_004_AmountTransaction(5.89);
		   iso.setISO_011_SysAuditNumber("000001");
		   iso.setISO_018_MerchantType("0005");
		   iso.setISO_023_CardSeq("1717678922");
		   iso.setISO_024_NetworkId("555222");
		   iso.setISO_032_ACQInsID("0512");
		   iso.setISO_033_FWDInsID("704");
		   iso.setISO_034_PANExt("ORELLANA LOPEZ JUAN CARLOS");
		   iso.setISO_036_Track3("LESCANO VILLAFUERTE JANETH PAULINA DE LOS ANGELES");
		   iso.setISO_042_Card_Acc_ID_Code("1");
		   iso.setISO_043_CardAcceptorLoc("1");
		   iso.setISO_102_AccountID_1("4079299929001");
		   iso.setISO_103_AccountID_2("5180613008395011");
		   iso.setISO_120_ExtendedData("POR	 DEUDAS EN EL DESTINO");
		   iso.setISO_121_ExtendedData("OTAVALO");
		   
		   TrxTransferenciaPagoDirectoRequest trxRq = new TrxTransferenciaPagoDirectoRequest();
		   trxRq = trxRq.Iso8583ToTrxTransferenciaPagoDirectoRequest(iso);
		   
		   SerializationObject serial = new SerializationObject();
		   String xml = serial.ObjectToString(trxRq,TrxTransferenciaPagoDirectoRequest.class);
		   System.out.println(xml);
		   
		   byte[] array2 = trxRq.TrxTransferenciaPagoDirectoRequestToByteArray(trxRq);
		   System.out.println(new String(array2));*/
		   
		   /*RECEPTOR++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		   /*Prueba de bytes a estructura TrxConsultaPagoDirectoRequest*/
		
		   String trama = "TR01800000100002100210239+161116125135000012092100011611161Cesar-Aulestia AH-005----------0000003641704855004400000000000004000+00151611160400000109997168835PD   ;36417048550044=00000000000000000000000?020015020000000022000000060PRUEBAS DINERS TC REAL Ced asociadaPAGO DIREC0916774110   GUAYAQUIL-2362-0000696024908401704679263---      ";
		   TrxTransferenciaPagoDirectoRequest trx1 =  new TrxTransferenciaPagoDirectoRequest();
		   trx1 = trx1.byteArraytoStruct(trama.getBytes());
		   SerializationObject serial = new SerializationObject();
		   String xml1 = serial.ObjectToString(trx1,TrxTransferenciaPagoDirectoRequest.class);
		   System.out.println(xml1);
		   
		   //Prueba Parseo de TrxTransferenciaPagoDirectoRequest a ISO8583
		   PagoDirectoParser parser1 = new PagoDirectoParser();
		   Iso8583 iso1 = parser1.convertTrxTransferenciaPagoDirectoRequestToIso8583(trx1);
		   System.out.println(iso1.getISO_114_ExtendedData());
		   
		   //PruebaRepuesta hacia Banred
		   parser1 =  new PagoDirectoParser();
		   TrxTransferenciaPagoDirectoResponse resp = new TrxTransferenciaPagoDirectoResponse("0000");
		   resp = parser1.Iso8583ToTrxTransferenciaPagoDirectoResponse(trx1, iso1);
		   byte[] respo = parser1.TrxTransferenciaPagoDirectoResponseToByte(resp);
		   System.out.println(new String(respo));
		   
	}
	
	
	public static TrxConsultaPagoDirectoRequest TestRequestConsulta() {
		
		TrxConsultaPagoDirectoRequest trx = new TrxConsultaPagoDirectoRequest();
		
		trx.setMessage_type_code_X002(TypeMessageCode.TR);
		trx.setMessage_fi_number_9004("1234");
		trx.setMessage_sequence_number_9004("9012");
		trx.setMessage_terminal_number_9005("98762");
		trx.setSession_sequence_number_9006("000001");
		trx.setTransaction_code_S9004(TypeTransactionCode.CONSULTA_PAGO_DIRECTO);
		
		XactSourceInformation xact = new XactSourceInformation();
		xact.setSource_date_9006("190227");
		xact.setSource_time_9006("082359");
		xact.setSource_aba_number_9010("0000067890"); //aba asignado por la institucion
		xact.setSource_branch_number_9004("0000"); //se puede omitir
		xact.setSource_business_date_9006("190227");
		xact.setTrf_terminal_type_9001("1"); //1 es Ventanilla
		xact.setTrf_source_name_X031("ORELLANA LOPEZ JUAN CARLOS");
		
		
		DetailTransaction detail = new DetailTransaction();
		detail.setForce_post_indicator_X001("0"); //Siempre
		detail.setReversal_indicator_X001("0"); //0 no es reverso, 1 si lo es
		detail.setTrans_acct_nbr_9018("459040004"); //Cta RECEPTORA OJO
		detail.setTransaction_amount_S9_018("00000000000000000+"); //Se puede omitir
		
		
		TargetInstitutionGroup target = new TargetInstitutionGroup();
		target.setAuth_fi_nbr_9004("7777"); //codigo de la institucion duenia de la cuenta
		target.setHost_business_date_9006("190227"); //fecha contable del host receptor
		
		StandInGroup stand = new StandInGroup();
		stand.setStandin_auth_type_X001("0"); //0 siempre 0 en consultas OJO se puede omitir
		stand.setStandin_auth_metod_X001("4"); //Preguntar 1 primaria o 4 en el host ????????????????
		stand.setStandin_result_code_9004("0000"); //Preguntar si es asi se piuede omitir???????????
		
		CardRelatedGroup card = new CardRelatedGroup();
		card.setPin_verify_flag_X001("0"); //Enviar 0 no valida PIN
		card.setTrack_II_valid_flag_X001("0"); //Enviar 0 no hay data
		card.setRed_target_phone_9010("0000000000"); //se puede omitir (celular de la persona)
		card.setRed_appl_type_source_9001("5"); //Tipo de cuenta del ordenante (debito) siempre 5 en Coopes
		card.setRed_flg_red_pd_X002("PD"); //Valor fijo PD
		card.setRed_filler_X_003("   "); // 3 espacios en blanco siempre, es decir se puede omitir
		card.setTrack_2_data_X040("4938490000000000==000000000000000000000?"); //BIN + 0000000000==000000000000000000000?
		card.setCard_appl_code_X002("02");  //02 Tarjeta de Credito, 05 cuenta de ahorros //OJO ordenante
		
		UserRequestDefinedData user = new UserRequestDefinedData();
		user.setReqpdr_target_name_X035("LESCANO VILLAFUERTE JANETH PAULINA"); //Nombre del receptor
		user.setReqpdr_observations_X010("POR PAGO DE TARJETA");
		user.setReqpdr_source_id_X013("1714891064"); //Numero de cedula de la persona ORDENANTE
		user.setReqpdr_city_X010("          "); //La ciudad pero se manda 10 espacios en blaco, se puede omitir.
		user.setReqpdr_reference_X020("67272799900000000000"); //Enviar siempre 20 carazcteres
		user.setReqpdr_target_id_X013("1717678922"); //cedula o ruc del RECEPTOR
		user.setFiller_X006("      ");//filler 6 espacios en blanco u omitir
		
		
		
		StandardRequestFieldsConsulta stndRq = new StandardRequestFieldsConsulta(xact, detail, target, stand, card);
		
		trx.setStandard_request_fields(stndRq);
		trx.setUser_request_defined_data(user);
		
		
		
		
		return trx;
	}
}
