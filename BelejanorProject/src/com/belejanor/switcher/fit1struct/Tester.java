package com.belejanor.switcher.fit1struct;

import java.util.ArrayList;
import java.util.List;

import com.belejanor.switcher.utils.SerializationObject;

public class Tester {

	public static void main(String[] args) {
		
		
		DetailFit1 msg = new DetailFit1();
		
		Cabecera cab = new Cabecera();
		cab.setUser("8100");
		cab.setPassword("C4F422D5BBA3FCD59D28A1E6A939C0C8");
		cab.setSessionId("0f0df57f711f4b70b8c906961eadeac6");
		cab.setTipoTrx("MAN");
		cab.setFechaTrx("2017-12-12 08:51:14");
		cab.setIpAutporizada("172.17.1.137");
		cab.setSubsistema("04");
		cab.setTransaccion("0684");
		cab.setVersion("01");
		cab.setMessageId("20171212124131_258754");
		cab.setIdioma("ES");
		cab.setTerminal("TER_MID");
		cab.setCanal("PC");
		cab.setCodCompania("2");
		cab.setCodSucursal("1");
		cab.setCodOficina("1");
		cab.setFechaContable("2016-10-14");
		cab.setNivelSeguridad("10");
		cab.setRol("1");
		cab.setCaducarTrx("0");
		cab.setEsHistorico("0");
		cab.setEsPDF("0");
		cab.setTodoAnterior("0");
		cab.setCompaniaID("2");
		cab.setNumero("60397");
		cab.setCampoSEC("4");
		cab.setCampoTDC("1");
		cab.setCampoESP("1");
		cab.setCampoLEG("30000");
		
		msg.setCabecera(cab);
		
		
		Detalle det = new Detalle();
		
		Bloques blq1 = new Bloques("0","1","1");
		
		
		Criterios cri1Bloq1 = new Criterios();
		cri1Bloq1.setCodigo("518005");
		cri1Bloq1.setValor("106846");
		
		
		List<Campos> listCampos = new ArrayList<>();
		listCampos.add(new Campos("CEDULA", null, "1804073268", "1", "false"));
		listCampos.add(new Campos("NOMLEGAL", null, "QUINATOA RIVERA TATIANA DEL PILAR", "1", "false"));
		listCampos.add(new Campos("NOMLEGAL", null, "QUINATOA RIVERA TATIANA DEL PILAR", "1", "false"));
	
		
		blq1.setCampos(listCampos);
		
		List<Criterios> listCriterios = new ArrayList<>();
		listCriterios.add(cri1Bloq1);
		listCriterios.add(new Criterios("192030", null, null, null));
		
		blq1.setCriterios(listCriterios);
		
		
		List<Bloques> listBlq = new ArrayList<>();
		listBlq.add(blq1);
		
		det.setBloques(listBlq);
		
		msg.setCabecera(cab);
		msg.setDetalle(det);
		
		long time_start, time_end;
		time_start = System.currentTimeMillis();
			//String trama = SerializationObject.ObjectToXML(msg);
			String trama = SerializationObject.ObjectToString(msg, DetailFit1.class);
		time_end = System.currentTimeMillis();
		System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");
		System.out.println(trama);
		
		
		
		String rr = 
"<FITBANK>													  " +
"	<CAB>                                                     " +   
"		<SUB>04</SUB>                                         " +
"		<TRN>0684</TRN>                                       " +
"		<VER>01</VER>                                         " +
"		<CDT/>                                                " +
"	</CAB>                                                    " +
"	<DET>                                                     " +
"		<COM>                                                 " +
"			<SEC>1</SEC>                                      " +
"			<CON/>                                            " +
"			<DES>DEPOSITO A CUENTA, CANAL RECICLADOR</DES>    " +
"			<IDP/>                                            " +
"			<NMP/>                                            " +
"			<VMO>12.5</VMO>                                   " +
"			<DBL>1</DBL>                                      " +
"			<LCR>1</LCR>                                      " +
"			<DLO/>                                            " +
"			<LOC/>                                            " +
"			<RUT/>                                            " +
"			<CTG/>                                            " +
"			<CHG/>                                            " +
"			<SUD/>                                            " +
"			<OFD/>                                            " +
"		</COM>                                                " +
"		<DEB>                                                 " +
"			<CTA>408100062246</CTA>                           " +
"			<MON>USD</MON>                                    " +
"			<VAL>12.5</VAL>                                   " +
"			<VLO>12.5</VLO>                                   " +
"			<FVL>2017-12-12</FVL>                             " +
"			<FDS/>                                            " +
"			<FVO/>                                            " +
"			<FDO/>                                            " +
"		</DEB>                                                " +
"		<CRE>                                                 " +
"			<CTA>408100062246</CTA>                           " +
"			<MON>USD</MON>                                    " +
"			<VAL>12.5</VAL>                                   " +
"			<VLO>12.5</VLO>                                   " +
"			<FVL>2017-12-12</FVL>                             " +
"			<FDS/>                                            " +
"			<FVO/>                                            " +
"			<FDO/>                                            " +
"		</CRE>                                                " +
"	</DET>                                                    " +
"	<AUT/>                                                    " +
"	<RES>                                                     " +
"		<FCN>2016-10-14</FCN>                                 " +
"		<MSG>2dcfb13b^16021ab2808^-71e</MSG>                  " +
"		<SDS>77878.35</SDS>                                   " +
"		<SAT>0</SAT>                                          " +
"		<SBL>0</SBL>                                          " +
"		<SRT>0</SRT>                                          " +
"		<NOM>QUINATOA RIVERA TATIANA DEL PILAR</NOM>          " +
"		<COD>0</COD>                                          " +
"		<DSC>TRANSACCION REALIZADA CORRECTAMENTE</DSC>        " +
"		<ODB>SALDOEFECTIVO</ODB>                              " +
"		<NRO>0</NRO>                                          " +
"		<ODB>SALDOCHEQUES </ODB>                              " +
"		<NRO>0</NRO>                                          " +
"		<ODB>SALDOEFECTIVO</ODB>                              " +
"		<NRO>0</NRO>                                          " +
"		<ODB>SALDOCHEQUES</ODB>                               " +
"		<NRO>0</NRO>                                          " +
"		<ODB>MENSAJE</ODB>                                    " +
"		<NRO>2dcfb13b|16021ab2808|-71e</NRO>                  " +
"	</RES>                                                    " +
"</FITBANK>                                                   ";
		
		
	DetailFit1 res = (DetailFit1) SerializationObject.StringToObject(rr, DetailFit1.class);	
	System.out.println("---------------->" + res.getCabecera().getSubsistema());	

	
		
	
	}

}
