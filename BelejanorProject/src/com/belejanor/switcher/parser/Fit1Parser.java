package com.belejanor.switcher.parser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.fit1struct.Bloques;
import com.belejanor.switcher.fit1struct.Cabecera;
import com.belejanor.switcher.fit1struct.Campos;
import com.belejanor.switcher.fit1struct.Criterios;
import com.belejanor.switcher.fit1struct.Dependencias;
import com.belejanor.switcher.fit1struct.DetailFit1;
import com.belejanor.switcher.fit1struct.Detalle;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.ChannelsFit1;
import com.belejanor.switcher.memcached.DispositivosFit1;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.User_Channel;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class Fit1Parser {
	
	private Logger log;
	
	public Fit1Parser(){
		log = new Logger();
	}

	public DataReturnParser parseDepositoReciclador(wIso8583 iso){
		
		DataReturnParser data = null;
		final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		
		ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
				cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			}
			
			
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.get(0).setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			
			executor = Executors.newWorkStealingPool();
			
			List<Callable<wIso8583>> callables = Arrays.asList(
			        () -> sql.getFechaContableFit1(isoAux.get(0)),
			        () -> sql.getDataPersonFit1(isoAux.get(1))
			);
			
			isoResAux = (List<wIso8583>) executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
			if(isoResAux != null){
				
				wIso8583 res  = isoResAux.stream()
				          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
				          .findFirst().orElseGet(()-> null);
				
				if(res != null){
					
					data.setCodError(res.getISO_039_ResponseCode());
					data.setDesError(res.getISO_039p_ResponseDetail());
					return data;
				}
				
			}else{
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (LAMBDA NULL EXCEPTION)");
				return data;
			}
			
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoResAux.get(0).getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			cab.setNumero("60397");
			cab.setCampoSEC("4");
			cab.setCampoTDC("1");
			cab.setCampoESP("1");
			cab.setCampoLEG("30000");
			
			Detalle det = new Detalle();
			Bloques blq1 = new Bloques("0","1","1");
			List<Criterios> listCriterios = new ArrayList<>();			
			List<Campos> listCampos = new ArrayList<>();
			
			listCampos.add(new Campos("CEDULA", null, isoResAux.get(1).getISO_121_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("NOMLEGAL", null, isoResAux.get(1).getISO_122_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("FCN", null,  isoResAux.get(0).getISO_120_ExtendedData() + " 00:00:00.0", "1", "false"));
			listCampos.add(new Campos("CUENTACREDITO", null, iso.getISO_102_AccountID_1().trim(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTACREDITO", null, "USD", "1", "false"));
			
			blq1.setCampos(listCampos);
			
			listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), null, null));//Cpersona
			listCriterios.add(new Criterios("192030", null, null, null));
			listCriterios.add(new Criterios("518009", isoResAux.get(1).getISO_122_ExtendedData(), null, null));
	
			blq1.setCriterios(listCriterios);
			
			
			Bloques blq2 = new Bloques("1", "1", "1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			listCamposBlq2.add(new Campos("DESCRIPCION", null, iso.getISO_123_ExtendedData(), "1", "false"));
			blq2.setCampos(listCamposBlq2);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Bloques blq3 = new Bloques("2", "1", "1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			listCamposBlq3.add(new Campos("CTIPOCOMPONENTE", null, Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(0), "1", "false"));
			listCamposBlq3.add(new Campos("COMPONENTE", null, Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(1), "1", "false"));
			listCamposBlq3.add(new Campos("MONEDA", null, isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHAVALOR", null, null, "1", "false"));
			listCamposBlq3.add(new Campos("FECHADISPONIBLE", null, null, "1", "false"));
			listCamposBlq3.add(new Campos("VALOR", null, df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
			blq3.setCampos(listCamposBlq3);
			
			List<Bloques> listBlq = new ArrayList<>();
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
		
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::DataReturnParser ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
	
	public DataReturnParser parseCreaPersonaRapida(wIso8583 iso){
		
		DataReturnParser data = null;
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		wIso8583 isoFc = null;
		try {
			
			List<Bloques> listBlq = new ArrayList<>();
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
			cab.setPassword(GeneralUtils.GetSecuencial(32));
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0, 3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setMessageId(iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
			cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			//Fecha Contable
			isoFc = (wIso8583) iso.clone();
			isoFc.setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			isoFc = sql.getFechaContableFit1(isoFc);
			if(!isoFc.getISO_039_ResponseCode().equals("000")) {
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, SUCURSAL NRO. " + isoFc.getISO_042_Card_Acc_ID_Code());
				return data;
			}else {
				
				cab.setFechaContable(isoFc.getISO_120_ExtendedData());
				cab.setNivelSeguridad("10");
				cab.setRol(iso.getWsTransactionConfig().getCommonRol());
				cab.setCaducarTrx("0");
				cab.setEsHistorico("0");
				cab.setEsPDF("0");
				cab.setTodoAnterior("0");
				cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
				cab.setCampoSBT(null);
				cab.setCampoLEG("1");
			}
			
			Detalle det = new Detalle();
			Bloques blq0 = new Bloques("0","1","0");
			List<Criterios> listCriterios = new ArrayList<>();			
			listCriterios.add(new Criterios("192006", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("318002", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("192015", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("192016", iso.getISO_034_PANExt(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("334002", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("164016", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("164014", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("40006", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("192023", "1", StringUtils.Empty(), StringUtils.Empty()));
			
			blq0.setCriterios(listCriterios);
			
			Bloques blq1 = new Bloques("1","1","1");
			List<Campos> listCamposBlq1 = new ArrayList<>();
			List<Dependencias> listDepBlq1 = new ArrayList<>();
			List<Dependencias> listDep1Blq1 = new ArrayList<>();
			List<Dependencias> listDep2Blq1 = new ArrayList<>();
			List<Dependencias> listDep3Blq1 = new ArrayList<>();
			List<Dependencias> listDep4Blq1 = new ArrayList<>();
			List<Dependencias> listDep5Blq1 = new ArrayList<>();
			List<Dependencias> listDep6Blq1 = new ArrayList<>();
			List<Dependencias> listDep7Blq1 = new ArrayList<>();
			List<Dependencias> listDep8Blq1 = new ArrayList<>();
			
			listDepBlq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDep1Blq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDep2Blq1.add(new Dependencias("192009", StringUtils.Empty(), iso.getISO_022_PosEntryMode()/*.equalsIgnoreCase("CC")?"CED":"CED"*/, "1"));
			listDep3Blq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDep4Blq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDep5Blq1.add(new Dependencias("192010", StringUtils.Empty(), iso.getISO_115_ExtendedData()/*.equalsIgnoreCase("PN")?"NAT":"JUR"*/, "1"));
			listDep6Blq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDep7Blq1.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDep8Blq1.add(new Dependencias("192005", StringUtils.Empty(), "EC", "1"));
			
			listCamposBlq1.add(new Campos("192006", StringUtils.Empty(), StringUtils.Empty(), "1", "false",listDepBlq1));
			listCamposBlq1.add(new Campos("192009", StringUtils.Empty(), iso.getISO_022_PosEntryMode()/*.equalsIgnoreCase("CC")?"CED":"CED"*/, "1", "false",listDep1Blq1));
			listCamposBlq1.add(new Campos("318003", StringUtils.Empty(), "CEDULA DE IDENTIDAD", "0", "false",listDep2Blq1));
			listCamposBlq1.add(new Campos("192015", StringUtils.Empty(), iso.getISO_002_PAN(), "1", "false",listDep3Blq1));
			listCamposBlq1.add(new Campos("192010", StringUtils.Empty(), iso.getISO_115_ExtendedData()/*.equalsIgnoreCase("PN")?"NAT":"JUR"*/, "1", "false",listDep4Blq1));
			listCamposBlq1.add(new Campos("334003", StringUtils.Empty(), iso.getISO_115_ExtendedData().equalsIgnoreCase("NAT")?"NATURAL":"JURIDICA", "0", "false",listDep5Blq1));
			listCamposBlq1.add(new Campos("192016", StringUtils.Empty(), iso.getISO_034_PANExt(), "1", "false",listDep6Blq1));
			listCamposBlq1.add(new Campos("192004", StringUtils.Empty(), "ES", "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("192005", StringUtils.Empty(), "EC", "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("188007", StringUtils.Empty(), "ECUATORIANA", "0", "false",listDep8Blq1));
			listCamposBlq1.add(new Campos("192014", StringUtils.Empty(), StringUtils.Empty(), "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("192023", StringUtils.Empty(), "1", "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("192020", StringUtils.Empty(), "1", "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("192021", StringUtils.Empty(), "2", "1", "false",listDep7Blq1));
			listCamposBlq1.add(new Campos("192030", StringUtils.Empty(), "1", "1", "false",listDep7Blq1));
			
			blq1.setCampos(listCamposBlq1);
			
			Bloques blq2 = new Bloques("2","1","1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			List<Dependencias> listDepBlq2 = new ArrayList<>();
			List<Dependencias> listDep1Blq2 = new ArrayList<>();
			List<Dependencias> listDep2Blq2 = new ArrayList<>();
			
			listDepBlq2.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDep1Blq2.add(new Dependencias("165005", StringUtils.Empty(), StringUtils.Empty(), "1"));
			
			listCamposBlq2.add(new Campos("165005", StringUtils.Empty(), StringUtils.Empty(), "1", "false", listDepBlq2));
			listCamposBlq2.add(new Campos("165003", StringUtils.Empty(), Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(0)
					, "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165002", StringUtils.Empty(), Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(1)
					, "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165010", StringUtils.Empty(), Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(2)
					, "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165011", StringUtils.Empty(), Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(2)
					, "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165008", StringUtils.Empty(), iso.getISO_114_ExtendedData() + " 00-00-00"
					, "1", "false", listDep1Blq2));
			String genero = Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(1);
			listCamposBlq2.add(new Campos("165009", StringUtils.Empty(),  genero
					, "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165004", StringUtils.Empty(), "1"
					, "1", "false", listDep1Blq2));
			String estadoCivil = StringUtils.Empty();
			estadoCivil = Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(3);
			listDep2Blq2.add(new Dependencias("165004", StringUtils.Empty(), estadoCivil, "1"));
			switch (estadoCivil) {
			case "1":
					if(genero.equals("M"))
						estadoCivil = "SOLTERO";
					else
						estadoCivil = "SOLTERA";
				break;
			case "2":
				if(genero.equals("M"))
					estadoCivil = "CASADO";
				else
					estadoCivil = "CASADA";
			break;
			case "3":
				if(genero.equals("M"))
					estadoCivil = "DIVORCIADO";
				else
					estadoCivil = "DIVORCIADA";
			break;
			case "4":
				if(genero.equals("M"))
					estadoCivil = "VIUDO";
				else
					estadoCivil = "VIUDA";
			break;
			case "5":
					estadoCivil = "EN UNION DE HECHO";
			break;
					
			default:
					estadoCivil = "NO REGISTRA";
				break;
			}
			listCamposBlq2.add(new Campos("99003", StringUtils.Empty(), estadoCivil, "0", "false", listDep2Blq2));
			listCamposBlq2.add(new Campos("165012", StringUtils.Empty(), "P", "1", "false", listDep1Blq2));
			listCamposBlq2.add(new Campos("165015", StringUtils.Empty(), "0", "1", "false", listDep1Blq2));
			
			blq2.setCampos(listCamposBlq2);
			
			
			Bloques blq3 = new Bloques("3","1","1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			List<Dependencias> listDepBlq3 = new ArrayList<>();
			List<Dependencias> listDep1Blq3 = new ArrayList<>();
			List<Dependencias> listDep2Blq3 = new ArrayList<>();
			List<Dependencias> listDepDobleBlq3 = new ArrayList<>();
			List<Dependencias> listDep4Blq3 = new ArrayList<>();
			List<Dependencias> listDepTripleBlq3 = new ArrayList<>();
			List<Dependencias> listDepCuatBlq3 = new ArrayList<>();
			List<Dependencias> listDepCuat1Blq3 = new ArrayList<>();
			List<Dependencias> listDepCinBlq3 = new ArrayList<>();
			List<Dependencias> listDepCin1Blq3 = new ArrayList<>();
			
			listDepBlq3.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDep1Blq3.add(new Dependencias("197005", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDep2Blq3.add(new Dependencias("197015", StringUtils.Empty(), StringUtils.Empty(), "1"));
			
			listCamposBlq3.add(new Campos("197005", StringUtils.Empty(), StringUtils.Empty(), "1", "false", listDepBlq3));
			listCamposBlq3.add(new Campos("197015", StringUtils.Empty(), "1", "1", "false", listDep1Blq3));
			listCamposBlq3.add(new Campos("197010", StringUtils.Empty(), "1", "1", "false", listDep2Blq3));
			
			
			/*Si existe correo electronico*/
			if(!StringUtils.IsNullOrEmpty(Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(0).trim())) {
				
				listCamposBlq3.add(new Campos("197008", StringUtils.Empty(), "MA", "1", "false", listDep2Blq3));
				List<Dependencias> listDep3Blq3 = new ArrayList<>();
				listDep3Blq3.add(new Dependencias("197008", StringUtils.Empty(), "MA", "1"));
				listCamposBlq3.add(new Campos("302003", StringUtils.Empty(), "EMAIL", "0", "false", listDep3Blq3));
				listCamposBlq3.add(new Campos("197016", StringUtils.Empty(), 
						Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(0), "1", "false", listDep2Blq3));
				listCamposBlq3.add(new Campos("197028", StringUtils.Empty(), 
						FormatUtils.DateToString(new Date(), "yyyy-MM-dd") + " 00-00-00", "1", "false", listDep2Blq3));
				listCamposBlq3.add(new Campos("197031", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq3));
				
			}
			
			listCamposBlq3.add(new Campos("197004", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq3));
			
			listDepDobleBlq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepDobleBlq3.add(new Dependencias("197015", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq3.add(new Campos("197006", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepDobleBlq3));
			
			listDep4Blq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq3.add(new Campos("227006", StringUtils.Empty(), StringUtils.Empty() , "0", "false", listDep4Blq3));
			
			listDepTripleBlq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepTripleBlq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepTripleBlq3.add(new Dependencias("197015", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq3.add(new Campos("197003", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepTripleBlq3));
			
			listDepCuatBlq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuatBlq3.add(new Dependencias("197003", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuatBlq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuatBlq3.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listCamposBlq3.add(new Campos("33006", StringUtils.Empty(), StringUtils.Empty() , "0", "false", listDepCuatBlq3));
			
			listDepCuat1Blq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuat1Blq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuat1Blq3.add(new Dependencias("197015", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCuat1Blq3.add(new Dependencias("197003", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq3.add(new Campos("197002", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepCuat1Blq3));
			
			
			listDepCinBlq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCinBlq3.add(new Dependencias("197003", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCinBlq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCinBlq3.add(new Dependencias("197002", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCinBlq3.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listCamposBlq3.add(new Campos("7007", StringUtils.Empty(), StringUtils.Empty() , "0", "false", listDepCinBlq3));
			
			listDepCin1Blq3.add(new Dependencias("197002", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCin1Blq3.add(new Dependencias("197006", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCin1Blq3.add(new Dependencias("197004", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCin1Blq3.add(new Dependencias("197015", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listDepCin1Blq3.add(new Dependencias("197003", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq3.add(new Campos("197033", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepCin1Blq3));
			
			listCamposBlq3.add(new Campos("197030", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq3));
			listCamposBlq3.add(new Campos("197007", StringUtils.Empty(), "1" , "1", "false", listDep2Blq3));
			listCamposBlq3.add(new Campos("197038", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq3));
			listCamposBlq3.add(new Campos("197037", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq3));
			
			blq3.setCampos(listCamposBlq3);
			
			Bloques blq4 = new Bloques("4","1","1");
			List<Campos> listCamposBlq4 = new ArrayList<>();
			List<Dependencias> listDepBlq4 = new ArrayList<>();
			List<Dependencias> listDep1Blq4 = new ArrayList<>();
			List<Dependencias> listDep2Blq4 = new ArrayList<>();
			List<Dependencias> listDep3Blq4 = new ArrayList<>();
			List<Dependencias> listDep4Blq4 = new ArrayList<>();
			List<Dependencias> listDep5Blq4 = new ArrayList<>();
			
			
			listDepBlq4.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listCamposBlq4.add(new Campos("206002", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepBlq4));
			
			listDep1Blq4.add(new Dependencias("206002", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq4.add(new Campos("206010", StringUtils.Empty(), "1" , "1", "false", listDep1Blq4));
			
			listDep2Blq4.add(new Dependencias("206010", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq4.add(new Campos("206003", StringUtils.Empty(), "CEL" , "1", "false", listDep2Blq4));
			
			listDep3Blq4.add(new Dependencias("206003", StringUtils.Empty(), "CEL", "1"));
			listCamposBlq4.add(new Campos("350003", StringUtils.Empty(), "CELULAR" , "0", "false", listDep3Blq4));	
			
			listCamposBlq4.add(new Campos("206001", StringUtils.Empty(), "593" , "1", "false", listDep2Blq4));
			
			listCamposBlq4.add(new Campos("206009", StringUtils.Empty(), iso.getISO_023_CardSeq() , "1", "false", listDep2Blq4));
			
			listCamposBlq4.add(new Campos("206005", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq4));
			
			listCamposBlq4.add(new Campos("206004", StringUtils.Empty(), "001" , "1", "false", listDep2Blq4));
			
			listDep4Blq4.add(new Dependencias("206004", StringUtils.Empty(), "001", "1"));
			listCamposBlq4.add(new Campos("352003", StringUtils.Empty(), "PROPIO" , "0", "false", listDep4Blq4));
			
			listCamposBlq4.add(new Campos("206018", StringUtils.Empty(), "Claro" , "0", "false", listDep1Blq4));
			
			listCamposBlq4.add(new Campos("206008", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDep2Blq4));
			
			listDep5Blq4.add(new Dependencias("206008", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq4.add(new Campos("197016", StringUtils.Empty(), StringUtils.Empty() , "0", "false", listDep5Blq4));
			
			listCamposBlq4.add(new Campos("197004", StringUtils.Empty(), StringUtils.Empty() , "0", "false", listDep1Blq4));
			
			List<Criterios> listCriterioBlq4 = new ArrayList<>();
			
			
			listCriterioBlq4.add(new Criterios("197008",StringUtils.Empty(),StringUtils.Empty(),StringUtils.Empty()));
			listCriterioBlq4.add(new Criterios("197008",StringUtils.Empty(),StringUtils.Empty(),StringUtils.Empty()));
			listCriterioBlq4.add(new Criterios("197008",StringUtils.Empty(),StringUtils.Empty(),StringUtils.Empty()));
			listCriterioBlq4.add(new Criterios("197008",StringUtils.Empty(),StringUtils.Empty(),StringUtils.Empty()));
			listCriterioBlq4.add(new Criterios("197008",StringUtils.Empty(),StringUtils.Empty(),StringUtils.Empty()));
			
			blq4.setCriterios(listCriterioBlq4);
			blq4.setCampos(listCamposBlq4);
			
			Bloques blq5 = new Bloques("5","1","1");
			List<Campos> listCamposBlq5 = new ArrayList<>();
			List<Dependencias> listDepBlq5 = new ArrayList<>();
			List<Dependencias> listDep1Blq5 = new ArrayList<>();
			
			listDepBlq5.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listCamposBlq5.add(new Campos("164007", StringUtils.Empty(), StringUtils.Empty() , "1", "false", listDepBlq5));
			
			listDep1Blq5.add(new Dependencias("164007", StringUtils.Empty(), StringUtils.Empty(), "1"));
			listCamposBlq5.add(new Campos("164022", StringUtils.Empty(), "2" , "1", "false", listDep1Blq5));
			
			listCamposBlq5.add(new Campos("164042", StringUtils.Empty(), "1" , "1", "false", listDep1Blq5));
			
			listCamposBlq5.add(new Campos("164033", StringUtils.Empty(), "1" , "1", "false", listDep1Blq5));
			
			listCamposBlq5.add(new Campos("164006", StringUtils.Empty(), "EC" , "1", "false", listDep1Blq5));
			
			
			blq5.setCampos(listCamposBlq5);
			
			listBlq.add(blq0);
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
			listBlq.add(blq4);
			listBlq.add(blq5);
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			msg.setRespuesta(null);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo Fit1Parser::parseCreaPersonaRapida ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
	
    public DataReturnParser parseCreaCuentaBasica(wIso8583 iso){
		
		DataReturnParser data = null;
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		wIso8583 isoFc = null;
		try {
			
			List<Bloques> listBlq = new ArrayList<>();
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
			cab.setPassword(GeneralUtils.GetSecuencial(32));
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setSubsistema(Arrays.asList(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
									.get(1).split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
									.get(1).split("\\-")).get(1));
			cab.setVersion(Arrays.asList(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
									.get(1).split("\\-")).get(2));
			cab.setTipoTrx(Arrays.asList(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
									.get(1).split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setMessageId(iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
			cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			//Fecha Contable
			isoFc = (wIso8583) iso.clone();
			isoFc.setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			isoFc = sql.getFechaContableFit1(isoFc);
			if(!isoFc.getISO_039_ResponseCode().equals("000")) {
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, SUCURSAL NRO. " + isoFc.getISO_042_Card_Acc_ID_Code());
				return data;
			}else {
				
				cab.setFechaContable(isoFc.getISO_120_ExtendedData());
				cab.setNivelSeguridad("10");
				cab.setRol(iso.getWsTransactionConfig().getCommonRol());
				cab.setCaducarTrx("0");
				cab.setEsHistorico("0");
				cab.setEsPDF("0");
				cab.setTodoAnterior("0");
				cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
				cab.setCampoSBT(null);
				cab.setCampoLEG("1");
			}
			
			
	          
			Detalle det = new Detalle();
			Bloques blq0 = new Bloques("0","1","1");
			List<Campos> listCamposBlq0 = new ArrayList<>();
			List<Criterios> listCriterios = new ArrayList<>();	
			List<Dependencias> listDepBlq0 = new ArrayList<>();
			List<Dependencias> listDep1Blq0 = new ArrayList<>();
			List<Dependencias> listDep2Blq0 = new ArrayList<>();
			List<Dependencias> listDep3Blq0 = new ArrayList<>();
			List<Dependencias> listDep4Blq0 = new ArrayList<>();
			List<Dependencias> listDep5Blq0 = new ArrayList<>();
			List<Dependencias> listDep6Blq0 = new ArrayList<>();
			List<Dependencias> listDep7Blq0 = new ArrayList<>();
			
			//String comodinCuenta = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
	        //  .get(7);
			
			String comodinCuenta = StringUtils.Empty();
			
			listCriterios.add(new Criterios("255002", Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
					          .get(2), null, null));
			listCriterios.add(new Criterios("78012", null, null, null));
			listCriterios.add(new Criterios("78002", null, null, null));
			listCriterios.add(new Criterios("957012", "ACT", null, null));
			
			
			listDepBlq0.add(new Dependencias("255002", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listDep1Blq0.add(new Dependencias("78002", null, comodinCuenta, "1"));
			
			
			listCamposBlq0.add(new Campos("78002", null, comodinCuenta, "1", "false",listDepBlq0));
			
			listCamposBlq0.add(new Campos("78003", null, "002", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78001", null, "NOR", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78017", null, "1", "1", "false",listDep1Blq0));
			
			/*Mandatorio*/
			listCamposBlq0.add(new Campos("135001", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0", "false",listDep1Blq0));
			
			
			listDep2Blq0.add(new Dependencias("135001", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listDep2Blq0.add(new Dependencias("255002", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listCamposBlq0.add(new Campos("135005", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(6), "0", "false",listDep2Blq0));
			
			/*TALVES ES 400*/
			listCamposBlq0.add(new Campos("215004", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(3), "0", "false",listDep1Blq0));
			
			
			listDep3Blq0.add(new Dependencias("215004", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(3), "0"));
			listDep3Blq0.add(new Dependencias("255002", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listDep3Blq0.add(new Dependencias("135001", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listCamposBlq0.add(new Campos("215006", null, "AHORROS VISTA", "0", "false",listDep3Blq0));
			
		
			listCamposBlq0.add(new Campos("283002", null, "1", "0", "false",listDep1Blq0));
			
			listDep4Blq0.add(new Dependencias("283002", null, "1", "0"));
			listCamposBlq0.add(new Campos("283003", null, "COMERCIAL", "0", "false",listDep4Blq0));
			
			listCamposBlq0.add(new Campos("161002", null, "USD", "0", "false",listDep1Blq0));
			
			listDep5Blq0.add(new Dependencias("369007", null, null, "0"));
			listCamposBlq0.add(new Campos("369005", null, null, "0", "false",listDep5Blq0));
			
			listCamposBlq0.add(new Campos("78008", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(4), "1", "false",listDep1Blq0));
			
			listDep6Blq0.add(new Dependencias("369005", null, null, "0"));
			listCamposBlq0.add(new Campos("192016", null, iso.getISO_034_PANExt(), "0", "false",listDep6Blq0));
			
			listCamposBlq0.add(new Campos("78016", null, iso.getISO_034_PANExt(), "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78011", null, "NAT", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78023", null, null, "1", "false",listDep1Blq0));
			
			listDep7Blq0.add(new Dependencias("78002", null, comodinCuenta, "1"));
			listDep7Blq0.add(new Dependencias("255002", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0"));
			listCamposBlq0.add(new Campos("64003", null, null, "1", "false",listDep7Blq0));
			
			listCamposBlq0.add(new Campos("78021", null, "1", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78019", null, "1", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78020", null, "1", "1", "false",listDep1Blq0));
			
			listCamposBlq0.add(new Campos("78033", null, "1", "0", "false",listDep1Blq0));
			
			
			blq0.setCriterios(listCriterios);
			blq0.setCampos(listCamposBlq0);
			
			Bloques blq1 = new Bloques("1","1","1");
			List<Campos> listCamposBlq1 = new ArrayList<>();
			List<Dependencias> listDepBlq1 = new ArrayList<>();
			List<Dependencias> listDep1Blq1 = new ArrayList<>();
			List<Dependencias> listDep2Blq1 = new ArrayList<>();
			List<Dependencias> listDep3Blq1 = new ArrayList<>();
			List<Dependencias> listDep4Blq1 = new ArrayList<>();
			List<Dependencias> listDep7Blq1 = new ArrayList<>();
			

			listDepBlq1.add(new Dependencias("84001", null, ".", "1"));
			
			listCamposBlq1.add(new Campos("84001", null, ".", "1", "false"));
			
			listCamposBlq1.add(new Campos("84024", null, "0", "1", "false",listDepBlq1));
			
			listCamposBlq1.add(new Campos("262003", null, null, "0", "false",listDepBlq1));
			
			listDep1Blq1.add(new Dependencias("262003", null, null, "0"));
			
			listCamposBlq1.add(new Campos("262006", null, null, "0", "false",listDep1Blq1));
			
			listCamposBlq1.add(new Campos("264002", null, "1", "0", "false",listDepBlq1));
			
			listDep2Blq1.add(new Dependencias("264002", null, "1", "0"));
			listDep2Blq1.add(new Dependencias("262003", null, null, "0"));
			listCamposBlq1.add(new Campos("264007", null, null, "0", "false",listDep2Blq1));
			
			listDep3Blq1.add(new Dependencias("69003", null, null, "1"));
			listCamposBlq1.add(new Campos("58001", null, "1", "0", "false",listDep3Blq1));
			
			listDep7Blq1.add(new Dependencias("58001", null, "1", "0"));
			listCamposBlq1.add(new Campos("58003", null, "INDIVIDUAL", "0", "false",listDep7Blq1));
			
			listDep4Blq1.add(new Dependencias("78002", null, comodinCuenta, "0"));
			listCamposBlq1.add(new Campos("69003", null, null, "1", "false",listDep4Blq1));
			
			listCamposBlq1.add(new Campos("84037", null, null, "1", "false",listDepBlq1));
			
			blq1.setCampos(listCamposBlq1);
			
			Bloques blq2 = new Bloques("2","1","1");
			
			List<Criterios> listCriteriosBlq2 = new ArrayList<>();
			List<Campos> listCamposBlq2 = new ArrayList<>();
			List<Dependencias> listDepBlq2 = new ArrayList<>();
			List<Dependencias> listDep2Blq2 = new ArrayList<>();
			List<Dependencias> listDep4Blq2 = new ArrayList<>();
			List<Dependencias> listDep5Blq2 = new ArrayList<>();
			List<Dependencias> listDep6Blq2 = new ArrayList<>();
			
			listDepBlq2.add(new Dependencias("81003", null, null, "1"));
			listDep2Blq2.add(new Dependencias("78002", null, comodinCuenta, "0"));
			listDep4Blq2.add(new Dependencias("192006", null, iso.getISO_124_ExtendedData(), "0"));
			
			listCamposBlq2.add(new Campos("232002",null, Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|"))
			          .get(2), "0", "false"));
			
			/*Cpersona de la persona a crear la cuenta basica P124*/
			listCriteriosBlq2.add(new Criterios("192006", iso.getISO_124_ExtendedData(), null, null));
			/*Cedula*/
			listCriteriosBlq2.add(new Criterios("192015", iso.getISO_002_PAN(), null, null));
			/*Nombres*/
			listCriteriosBlq2.add(new Criterios("192016", iso.getISO_034_PANExt().trim(), null, null));
			/*Tipo de Persona, Natural o Juridica*/
			listCriteriosBlq2.add(new Criterios("334002", iso.getISO_115_ExtendedData(), null, null));
			listCriteriosBlq2.add(new Criterios("638022", null, null, null));
			
			listCamposBlq2.add(new Campos("81009", null, "AUX", "0", "false"));
			
			
			listCamposBlq2.add(new Campos("81002", null, iso.getISO_124_ExtendedData(), "1", "false", listDepBlq2));
			
			
			listCamposBlq2.add(new Campos("81003", null, null, "1", "false", listDep2Blq2));
			
			listCamposBlq2.add(new Campos("192009", null, null, "0", "false", listDep4Blq2));
			
			listCamposBlq2.add(new Campos("192009", null, "CED", "0", "false", listDep4Blq2));
			
			listCamposBlq2.add(new Campos("192034", null, iso.getISO_124_ExtendedData(), "0", "false", listDep4Blq2));
			
			listCamposBlq2.add(new Campos("231002", null, "PRI", "0", "false", listDepBlq2));
			
			listDep5Blq2.add(new Dependencias("231002", null, "PRI", "0"));
			listCamposBlq2.add(new Campos("231003", null, "TITULAR", "0", "false", listDep5Blq2));
			
			listCamposBlq2.add(new Campos("197015", null, "1", "0", "false", listDepBlq2));
			
			listDep6Blq2.add(new Dependencias("197015", null, "1", "0"));
			listCamposBlq2.add(new Campos("197019", null, "DIRECCION CUENTA CREADA POR BIMO", "0", "false", listDep6Blq2));
			
			listCamposBlq2.add(new Campos("81009", null, "1", "1", "false", listDepBlq2));
			
			listCamposBlq2.add(new Campos("81008", null, "1", "1", "false", listDepBlq2));
			
			listCamposBlq2.add(new Campos("192021", null, "1", "0", "false", listDep4Blq2));
			
			blq2.setCriterios(listCriteriosBlq2);
			blq2.setCampos(listCamposBlq2);
			
			
			Bloques blq3 = new Bloques("3","1","1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			List<Dependencias> listDepBlq3 = new ArrayList<>();
			List<Dependencias> listDep1Blq3 = new ArrayList<>();
			List<Dependencias> listDep2Blq3 = new ArrayList<>();
			
			listDepBlq3.add(new Dependencias("78002", null, comodinCuenta, "0"));
			listDep1Blq3.add(new Dependencias("72005", null, null, "1"));
			listDep2Blq3.add(new Dependencias("192006", null, iso.getISO_124_ExtendedData(), "0"));
			
			listCamposBlq3.add(new Campos("72005", null, null, "1", "false", listDepBlq3));
			
			listCamposBlq3.add(new Campos("192006", null, iso.getISO_124_ExtendedData(), "0", "false", listDep1Blq3));
			
			listCamposBlq3.add(new Campos("192009", null, "CED", "0", "false", listDep2Blq3));
			
			listCamposBlq3.add(new Campos("192015", null, iso.getISO_002_PAN(), "0", "false", listDep2Blq3));
			
			listCamposBlq3.add(new Campos("192016", null, iso.getISO_034_PANExt(), "0", "false", listDep2Blq3));
			
			listCamposBlq3.add(new Campos("72010", null, null, "1", "false", listDep1Blq3));

			listCamposBlq3.add(new Campos("72001", null, null, "1", "false", listDep1Blq3));
			
			listCamposBlq3.add(new Campos("72009", null, null, "1", "false", listDep1Blq3));
			
			blq3.setCampos(listCamposBlq3);
			
			Bloques blq4 = new Bloques("4","1","1");
			List<Campos> listCamposBlq4 = new ArrayList<>();
			List<Dependencias> listDepBlq4 = new ArrayList<>();
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			
			listCamposBlq4.add(new Campos("65016", null, null , "1", "false"));
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			listCamposBlq4.add(new Campos("65001", null, null , "1", "false", listDepBlq4));
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			listCamposBlq4.add(new Campos("65008", null, "1" , "1", "false", listDepBlq4));
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			listCamposBlq4.add(new Campos("65015", null, "1" , "1", "false", listDepBlq4));
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			listCamposBlq4.add(new Campos("65017", null, "999000" , "1", "false", listDepBlq4));
			
			listDepBlq4.add(new Dependencias("65016", null, null, "1"));
			listCamposBlq4.add(new Campos("65002", null, "ENT" , "1", "false", listDepBlq4));
			
			blq4.setCampos(listCamposBlq4);
			
			
			Bloques blq5 = new Bloques("5","1","1");
			List<Campos> listCamposBlq5 = new ArrayList<>();
			List<Dependencias> listDepBlq5 = new ArrayList<>();
			List<Criterios> listCriteriosBlq5 = new ArrayList<>();
			
			listDepBlq5.add(new Dependencias("74001", null, null, "0"));
			
			listCamposBlq5.add(new Campos("74001", null, null, "0", "false"));
			
			listDepBlq5.add(new Dependencias("74001", null, null, "0"));
			listCamposBlq5.add(new Campos("74011", null, "1" , "0", "false", listDepBlq5));
			
			listCriteriosBlq5.add(new Criterios("CID", "2", null, null));
			listCriteriosBlq5.add(new Criterios("IDM", "ES", null, null));
			
			blq5.setCriterios(listCriteriosBlq5);
			
			listBlq.add(blq0);
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
			listBlq.add(blq4);
			listBlq.add(blq5);
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			msg.setRespuesta(null);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo Fit1Parser::parseCreaCuentaBasica ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
	
    public DataReturnParser parseTransferenciaSPI_ORD(wIso8583 iso){
		
		DataReturnParser data = null;
		final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		
		ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getISO_041_CardAcceptorID());
				cab.setCodOficina(iso.getISO_042_Card_Acc_ID_Code());
			}
			
			
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.get(0).setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			
			executor = Executors.newWorkStealingPool();
			
			List<Callable<wIso8583>> callables = Arrays.asList(
			        () -> sql.getFechaContableFit1(isoAux.get(0)),
			        () -> sql.getDataPersonFit1(isoAux.get(1))
			);
			
			isoResAux = (List<wIso8583>) executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
			if(isoResAux != null){
				
				wIso8583 res  = isoResAux.stream()
				          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
				          .findFirst().orElseGet(()-> null);
				
				if(res != null){
					
					data.setCodError(res.getISO_039_ResponseCode());
					data.setDesError(res.getISO_039p_ResponseDetail());
					return data;
				}
				
			}else{
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (LAMBDA NULL EXCEPTION)");
				return data;
			}
			
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoResAux.get(0).getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCampoLEG(null);
			cab.setCtipoPersona(isoResAux.get(1).getISO_034_PANExt());
			cab.setCampoSRC(StringUtils.Empty());
		
			Detalle det = new Detalle();
			Bloques blq1 = new Bloques("0","1","1");
			List<Criterios> listCriterios = new ArrayList<>();			
			List<Campos> listCampos = new ArrayList<>();
			
			listCampos.add(new Campos("CEDULA", StringUtils.Empty(), isoResAux.get(1).getISO_121_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("NOMLEGAL", StringUtils.Empty(), isoResAux.get(1).getISO_122_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("FECHACONTABLE", StringUtils.Empty(),  isoResAux.get(0).getISO_120_ExtendedData() + " 00-00-00", "1", "false"));
			listCampos.add(new Campos("CUENTACREDITO", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTACREDITO", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("CUENTADEBITO", StringUtils.Empty(), iso.getISO_102_AccountID_1().trim(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTADEBITO", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			blq1.setCampos(listCampos);
			
			listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona
			listCriterios.add(new Criterios("192030", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("518009", isoResAux.get(1).getISO_122_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));
		
			blq1.setCriterios(listCriterios);
			
			Bloques blq2 = new Bloques("1", "1", "1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			listCamposBlq2.add(new Campos("DESCRIPCION", StringUtils.Empty(), iso.getISO_121_ExtendedData(), "1", "false"));
			blq2.setCampos(listCamposBlq2);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			
			
			List<Criterios> listCriteriosBlq3 = new ArrayList<>();		
			Bloques blq3 = new Bloques("2", "1", "1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			listCamposBlq3.add(new Campos("COMPONENTE", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(0), "1", "false"));
			listCamposBlq3.add(new Campos("CCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
				      .getProccodeParams().split("\\-")).get(1), "1", "false"));
			listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), "TRANSFERENCIAS SPI ENVIADAS", "1", "false"));
			listCamposBlq3.add(new Campos("MONEDA", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHAVALOR", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHADISPONIBLE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("VALOR", StringUtils.Empty(), df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
			listCriteriosBlq3.add(new Criterios("FILTROCONCEPTO", Arrays.asList(iso.getWsTransactionConfig()
				      .getProccodeParams().split("\\-")).get(1), StringUtils.Empty(), StringUtils.Empty()));
			blq3.setCampos(listCamposBlq3);
			blq3.setCriterios(listCriteriosBlq3);
			
			
			Bloques blq4 = new Bloques("3", "1", "0");
			
			Bloques blq5 = new Bloques("4", "1", "1");
			List<Campos> listCamposBlq5 = new ArrayList<>();
			listCamposBlq5.add(new Campos("998004", StringUtils.Empty(), iso.getWsTransactionConfig().getCommonCompania(), "1", "false"));
			listCamposBlq5.add(new Campos("998006", StringUtils.Empty(), iso.getWsTransactionConfig().getUser_Fit(), "1", "false"));
			listCamposBlq5.add(new Campos("998001", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
				                                         .getProccodeParams().split("\\-")).get(1), "1", "false"));
			listCamposBlq5.add(new Campos("998015", StringUtils.Empty(), GeneralUtils.GetSecuencial(27), "1", "false"));
			listCamposBlq5.add(new Campos("998002", StringUtils.Empty(), iso.getISO_051_CardCurrCode() == 840?"USD":StringUtils.Empty()
					                               , "1", "false"));
			listCamposBlq5.add(new Campos("998005", StringUtils.Empty(), iso.getISO_041_CardAcceptorID(), "1", "false"));
			listCamposBlq5.add(new Campos("998003", StringUtils.Empty(), iso.getISO_042_Card_Acc_ID_Code(), "1", "false"));
			listCamposBlq5.add(new Campos("998008", StringUtils.Empty(), isoResAux.get(0).getISO_120_ExtendedData() 
					                     + " 00-00-00", "1", "false"));
			listCamposBlq5.add(new Campos("998017", StringUtils.Empty(), isoResAux.get(1).getISO_120_ExtendedData(), "1", "false"));
			listCamposBlq5.add(new Campos("998014", StringUtils.Empty(), iso.getISO_115_ExtendedData(), "1", "false"));
			listCamposBlq5.add(new Campos("998023", StringUtils.Empty(), iso.getISO_102_AccountID_1(), "1", "false"));
			listCamposBlq5.add(new Campos("998022", StringUtils.Empty(), iso.getISO_103_AccountID_2(), "1", "false"));
			
			blq5.setCampos(listCamposBlq5);
			
			
			List<Bloques> listBlq = new ArrayList<>();
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
			listBlq.add(blq4);
			listBlq.add(blq5);
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::parseTransferenciaSPI_ORD ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
    
    public DataReturnParser parseNotaCreditoDebito(wIso8583 iso){
		
		DataReturnParser data = null;
		final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		
		ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
				cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			}
			
			
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.get(0).setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			
			executor = Executors.newWorkStealingPool();
			
			List<Callable<wIso8583>> callables = Arrays.asList(
			        () -> sql.getFechaContableFit1(isoAux.get(0)),
			        () -> sql.getDataPersonFit1(isoAux.get(1))
			);
			
			isoResAux = (List<wIso8583>) executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
			if(isoResAux != null){
				
				wIso8583 res  = isoResAux.stream()
				          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
				          .findFirst().orElseGet(()-> null);
				
				if(res != null){
					
					data.setCodError(res.getISO_039_ResponseCode());
					data.setDesError(res.getISO_039p_ResponseDetail());
					return data;
				}else {
					
					data.setTipoIdentificacion(isoAux.get(1).getISO_023_CardSeq());
					data.setTipoPersona(isoAux.get(1).getISO_034_PANExt());
					data.setcPersona(isoAux.get(1).getISO_120_ExtendedData());
					data.setIdentificador(isoAux.get(1).getISO_121_ExtendedData());
					data.setNombresCuenta(isoAux.get(1).getISO_122_ExtendedData());
					data.setNumCuenta(isoAux.get(1).getISO_123_ExtendedData());
				}
				
			}else{
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (LAMBDA NULL EXCEPTION)");
				return data;
			}
			
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoResAux.get(0).getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCampoLEG(null);
			cab.setCtipoPersona(isoResAux.get(1).getISO_034_PANExt());
			cab.setCampoSRC(StringUtils.Empty());
		
			Detalle det = new Detalle();
			Bloques blq1 = new Bloques("0","1","1");
			List<Criterios> listCriterios = new ArrayList<>();			
			List<Campos> listCampos = new ArrayList<>();
			
			listCampos.add(new Campos("CEDULA", StringUtils.Empty(), isoResAux.get(1).getISO_121_ExtendedData(), "1", "false"));
			data.setIdentificador(isoResAux.get(1).getISO_121_ExtendedData());
			listCampos.add(new Campos("NOMLEGAL", StringUtils.Empty(), isoResAux.get(1).getISO_122_ExtendedData(), "1", "false"));
			data.setNombresCuenta(isoResAux.get(1).getISO_122_ExtendedData());
			listCampos.add(new Campos("FCN", StringUtils.Empty(),  isoResAux.get(0).getISO_120_ExtendedData() + " 00-00-00", "1", "false"));
			listCampos.add(new Campos("CUENTACREDITO", StringUtils.Empty(), iso.getWsTransactionConfig().getProccodeTransactionFit()
					            .startsWith("04-0162-00")?iso.getISO_102_AccountID_1().trim():StringUtils.Empty(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTACREDITO", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCampos.add(new Campos("CUENTADEBITO", StringUtils.Empty(), iso.getWsTransactionConfig().getProccodeTransactionFit()
								.startsWith("04-0163-00")?iso.getISO_102_AccountID_1().trim():StringUtils.Empty(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTADEBITO", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			
			blq1.setCampos(listCampos);
			
			listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona
			listCriterios.add(new Criterios("192030", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("518009", isoResAux.get(1).getISO_122_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));
			
			if(iso.getWsTransactionConfig().getProccodeTransactionFit()
		            .equals("04-0162-00")){
				
				cab.setMaximo("0");
				cab.setCampoSEC("4");
				cab.setCampoTDC("1");
				cab.setCampoESP("1");
				listCriterios.add(new Criterios("518002", iso.getISO_102_AccountID_1().trim(), null, null));
				
			}else{
				
				cab.setCampoSBT(StringUtils.Empty());
				cab.setMaximo("0");
				cab.setCampoSEC("0");
				cab.setCampoTDC("1");
				cab.setCampoESP("1");
				cab.setNumero(StringUtils.Empty()/*"2825"/*GeneralUtils.GetSecuencial(6)*/);
				cab.setCampoRES("100");
				cab.setCampoTOPx("0");
				cab.setCampoTOPy("0");
				cab.setCampoWID("3.2");
				cab.setCampoHEI("2.5");
				cab.setCampoUNID("1");
				cab.setCampoGUI("1");
				cab.setCampoDOC("D");
			}
			
			blq1.setCriterios(listCriterios);
			
			Bloques blq2 = new Bloques("1", "1", "1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			listCamposBlq2.add(new Campos("DESCRIPCION", StringUtils.Empty(), iso.getISO_123_ExtendedData(), "1", "false"));
			blq2.setCampos(listCamposBlq2);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Bloques blq3 = new Bloques("2", "1", "1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			listCamposBlq3.add(new Campos("COMPONENTE", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(0), "1", "false"));
			listCamposBlq3.add(new Campos("CCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
				      .getProccodeParams().split("\\-")).get(1), "1", "false"));
			listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), iso.getISO_123_ExtendedData(), "1", "false"));
			listCamposBlq3.add(new Campos("MONEDA", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHAVALOR", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHADISPONIBLE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("VALOR", StringUtils.Empty(), df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
			blq3.setCampos(listCamposBlq3);
			
			List<Bloques> listBlq = new ArrayList<>();
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
		
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::DataReturnParser ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
	
    public DataReturnParser parseTransferenciasInternas(wIso8583 iso){
		
		DataReturnParser data = null;
		final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		
		ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
				cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			}
			
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.get(0).setISO_042_Card_Acc_ID_Code(iso.getWsTransactionConfig().getCommonCodSursal());
			isoAux.get(1).setISO_042_Card_Acc_ID_Code(iso.getWsTransactionConfig().getCommonCodSursal());
			isoAux.get(2).setISO_042_Card_Acc_ID_Code(iso.getWsTransactionConfig().getCommonCodSursal());
			
			executor = Executors.newWorkStealingPool();
			
			List<Callable<wIso8583>> callables = Arrays.asList(
			        () -> sql.getFechaContableFit1(isoAux.get(0)),
			        () -> sql.getDataPersonFit1(isoAux.get(1)),
			        () -> sql.getDataPersonFit1_103Destino(isoAux.get(2))
			);
			
			isoResAux = (List<wIso8583>) executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
			if(isoResAux != null){
				
				wIso8583 res  = isoResAux.stream()
				          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
				          .findFirst().orElseGet(()-> null);
				
				if(res != null){
					
					data.setCodError(res.getISO_039_ResponseCode());
					data.setDesError(res.getISO_039p_ResponseDetail());
					return data;
				}
				
			}else{
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (ORIGEN Y DESTINO) (LAMBDA NULL EXCEPTION)");
				return data;
			}
			
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setMessageId(iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoResAux.get(0).getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			//cab.setCtipoPersona(isoResAux.get(1).getISO_034_PANExt());
			cab.setCampoSBT(StringUtils.Empty());
		
			Detalle det = new Detalle();
			Bloques blq1 = new Bloques("0","1","1");
			List<Criterios> listCriterios = new ArrayList<>();			
			List<Campos> listCampos = new ArrayList<>();
			
			listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona Origen
			listCampos.add(new Campos("CUENTADEBITO", StringUtils.Empty(), iso.getISO_102_AccountID_1(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTADEBITO", StringUtils.Empty(), iso.getISO_049_TranCurrCode() == 840?"USD":"XXX", "1", "false"));
			listCampos.add(new Campos("NOMBRECUENTADEBITO", StringUtils.Empty(), Arrays.asList(iso.getISO_122_ExtendedData().split("\\|")).get(1)
					       , "1", "false"));
			listCriterios.add(new Criterios("518005", isoResAux.get(2).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona Destino
			blq1.setCampos(listCampos);
			listCampos.add(new Campos("CUENTACREDITO", StringUtils.Empty(), iso.getISO_103_AccountID_2(), "1", "false"));
			listCampos.add(new Campos("MONEDACUENTACREDITO", StringUtils.Empty(), iso.getISO_049_TranCurrCode() == 840?"USD":"XXX", "1", "false"));
			listCampos.add(new Campos("NOMBRECUENTACREDITO", StringUtils.Empty(), Arrays.asList(iso.getISO_121_ExtendedData().split("\\|")).get(1)
				       , "1", "false"));
			blq1.setCriterios(listCriterios);
			
			
			Bloques blq2 = new Bloques("1", "1", "1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			listCamposBlq2.add(new Campos("NUMDOCUMENTO", StringUtils.Empty(), Arrays.asList(iso.getISO_122_ExtendedData().split("\\|"))
					          .get(0).substring(1), "1", "false"));
			listCamposBlq2.add(new Campos("DESCRIPCION", StringUtils.Empty(), iso.getISO_034_PANExt(), "1", "false"));
			blq2.setCampos(listCamposBlq2);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			
			
			Bloques blq3 = new Bloques("2", "1", "1");
			List<Campos> listCamposBlq3 = new ArrayList<>();
			listCamposBlq3.add(new Campos("COMPONENTE", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(0), "1", "false"));
			//listCamposBlq3.add(new Campos("CCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
			//	      .getProccodeParams().split("\\-")).get(1), "1", "false"));
			listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), "TRANSFERENCIAS MASIVAS FINANCOOP", "1", "false"));
			listCamposBlq3.add(new Campos("MONEDA", StringUtils.Empty(),iso.getISO_049_TranCurrCode() == 840?"USD":"XXX", "1", "false"));
			listCamposBlq3.add(new Campos("FECHAVALOR", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHADISPONIBLE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("VALOR", StringUtils.Empty(), df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
			listCamposBlq3.add(new Campos("MENSAJE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("CID", StringUtils.Empty(), iso.getWsTransactionConfig().getCommonCompania(), "1", "false"));
			listCamposBlq3.add(new Campos("IDM", StringUtils.Empty(), iso.getWsTransactionConfig().getCommonIdioma(), "1", "false"));
			listCamposBlq3.add(new Campos("SUBSISTEMA", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					           .getProccodeTransactionFit().split("\\-")).get(0), "1", "false"));
			listCamposBlq3.add(new Campos("VERSION", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					           .getProccodeTransactionFit().split("\\-")).get(2), "1", "false"));
			listCamposBlq3.add(new Campos("TRANSACCION", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					            .getProccodeTransactionFit().split("\\-")).get(1), "1", "false"));
			blq3.setCampos(listCamposBlq3);
			
			
			Bloques blq6 = new Bloques("6", "1", "1");
			List<Campos> listCamposBlq6 = new ArrayList<>();
			List<Criterios> listCriterios6 = new ArrayList<>();	
			List<Dependencias> listDepBlq6_1 = new ArrayList<>();
			List<Dependencias> listDepBlq6_2 = new ArrayList<>();
			List<Dependencias> listDepBlq6_3 = new ArrayList<>();
			List<Dependencias> listDepBlq6_4 = new ArrayList<>();
			List<Dependencias> listDepBlq6_5 = new ArrayList<>();
			
			listDepBlq6_1.add(new Dependencias("78002", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDepBlq6_2.add(new Dependencias("69002", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDepBlq6_3.add(new Dependencias("69001", StringUtils.Empty(), "1", "0"));
			listDepBlq6_4.add(new Dependencias("72002", StringUtils.Empty(), StringUtils.Empty(), "0"));
			listDepBlq6_5.add(new Dependencias("192006", StringUtils.Empty(), StringUtils.Empty(), "0"));
			
			
			listCamposBlq6.add(new Campos("69002", StringUtils.Empty(), StringUtils.Empty(), "0", "false",listDepBlq6_1));
			listCamposBlq6.add(new Campos("69001", StringUtils.Empty(), StringUtils.Empty(), "0", "false",listDepBlq6_2));
			listCamposBlq6.add(new Campos("69004", StringUtils.Empty(), "2", "0", "false",listDepBlq6_2));
			listCamposBlq6.add(new Campos("58003", StringUtils.Empty(), "INDIVIDUAL", "0", "false",listDepBlq6_3));
			
			listCriterios6.add(new Criterios("72002", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCamposBlq6.add(new Campos("192006", StringUtils.Empty(), StringUtils.Empty(), "1", "false",listDepBlq6_4));
			listCamposBlq6.add(new Campos("192015", StringUtils.Empty(), StringUtils.Empty(), "0", "false",listDepBlq6_5));
			listCamposBlq6.add(new Campos("192009", StringUtils.Empty(), StringUtils.Empty(), "0", "false",listDepBlq6_5));
			listCamposBlq6.add(new Campos("192016", StringUtils.Empty(), StringUtils.Empty(), "0", "false",listDepBlq6_5));
			
			
			blq6.setCampos(listCamposBlq6);
			blq6.setCriterios(listCriterios6);
			
	
			List<Bloques> listBlq = new ArrayList<>();
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
			listBlq.add(blq6);
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::parseTransferenciasInternas ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
    
    public DataReturnParser parseNotaCreditoDebitoBIMO(wIso8583 iso){
		
		DataReturnParser data = null;
		final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		
		ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			DetailFit1 msg = new DetailFit1();
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
				cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			}
			
			
			isoAux.add((wIso8583) iso.clone());
			isoAux.add((wIso8583) iso.clone());
			isoAux.get(0).setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			
			executor = Executors.newWorkStealingPool();
			
			List<Callable<wIso8583>> callables = Arrays.asList(
			        () -> sql.getFechaContableFit1(isoAux.get(0)),
			        () -> sql.getDataPersonFit1(isoAux.get(1))
			);
			
			isoResAux = (List<wIso8583>) executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
			if(isoResAux != null){
				
				wIso8583 res  = isoResAux.stream()
				          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
				          .findFirst().orElseGet(()-> null);
				
				if(res != null){
					
					data.setCodError(res.getISO_039_ResponseCode());
					data.setDesError(res.getISO_039p_ResponseDetail());
					return data;
				}else {
					
					data.setTipoIdentificacion(isoAux.get(1).getISO_023_CardSeq());
					data.setTipoPersona(isoAux.get(1).getISO_034_PANExt());
					data.setcPersona(isoAux.get(1).getISO_120_ExtendedData());
					data.setIdentificador(isoAux.get(1).getISO_121_ExtendedData());
					data.setNombresCuenta(isoAux.get(1).getISO_122_ExtendedData());
					data.setNumCuenta(isoAux.get(1).getISO_123_ExtendedData());
				}
				
			}else{
				
				data.setCodError("909");
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (LAMBDA NULL EXCEPTION)");
				return data;
			}
			
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			cab.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoResAux.get(0).getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCampoLEG(null);
			cab.setCtipoPersona(isoResAux.get(1).getISO_034_PANExt());
			cab.setCampoSRC(StringUtils.Empty());
		
			Detalle det = new Detalle();
			Bloques blq1 = new Bloques("0","1","1");
			List<Criterios> listCriterios = new ArrayList<>();			
			List<Campos> listCampos = new ArrayList<>();
			
			String NroCuentaDebito = StringUtils.Empty();
			String NroCuentaCredito = StringUtils.Empty();
			if(iso.getWsTransactionConfig().getProccodeTransactionFit()
		            .startsWith("04-0843")) {
				/*NroCuentaDebito = */NroCuentaCredito = iso.getISO_102_AccountID_1();
			}else
				NroCuentaDebito = iso.getISO_102_AccountID_1();
			
			listCampos.add(new Campos("CEDULA", StringUtils.Empty(), isoResAux.get(1).getISO_121_ExtendedData(), "1", "false"));
			data.setIdentificador(isoResAux.get(1).getISO_121_ExtendedData());
			listCampos.add(new Campos("NOMLEGAL", StringUtils.Empty(), isoResAux.get(1).getISO_122_ExtendedData(), "1", "false"));
			data.setNombresCuenta(isoResAux.get(1).getISO_122_ExtendedData());
			listCampos.add(new Campos("FCN", StringUtils.Empty(),  isoResAux.get(0).getISO_120_ExtendedData() + " 00-00-00", "1", "false"));
			
			listCampos.add(new Campos("CUENTACREDITO", StringUtils.Empty(), NroCuentaCredito , "1", "false"));
			listCampos.add(new Campos("MONEDACUENTACREDITO", StringUtils.Empty(), "USD", "1", "false"));
			listCampos.add(new Campos("CUENTADEBITO", StringUtils.Empty(), NroCuentaDebito, "1", "false"));
			listCampos.add(new Campos("MONEDACUENTADEBITO", StringUtils.Empty(), "USD", "1", "false"));
			
			blq1.setCampos(listCampos);
			
			listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona
			listCriterios.add(new Criterios("192030", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
			listCriterios.add(new Criterios("518009", isoResAux.get(1).getISO_122_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));
			
			if(iso.getWsTransactionConfig().getProccodeTransactionFit()
		            .startsWith("04-0843")){
				
				cab.setMaximo("0");
				cab.setCampoSEC("4");
				cab.setCampoTDC("1");
				cab.setCampoESP("1");
				listCriterios.add(new Criterios("518002", iso.getISO_102_AccountID_1().trim(), null, null));
				
			}else{
				
				cab.setCampoSBT(StringUtils.Empty());
				cab.setMaximo("0");
				cab.setCampoSEC("0");
				cab.setCampoTDC("1");
				cab.setCampoESP("1");
				cab.setNumero(StringUtils.Empty()/*"2825"/*GeneralUtils.GetSecuencial(6)*/);
				cab.setCampoRES("100");
				cab.setCampoTOPx("0");
				cab.setCampoTOPy("0");
				cab.setCampoWID("3.2");
				cab.setCampoHEI("2.5");
				cab.setCampoUNID("1");
				cab.setCampoGUI("1");
				cab.setCampoDOC("D");
			}
			
			blq1.setCriterios(listCriterios);
			
			Bloques blq2 = new Bloques("1", "1", "1");
			List<Campos> listCamposBlq2 = new ArrayList<>();
			listCamposBlq2.add(new Campos("DESCRIPCION", StringUtils.Empty(), iso.getISO_123_ExtendedData(), "1", "false"));
			blq2.setCampos(listCamposBlq2);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Bloques blq3 = null;
			blq3 = new Bloques("2", "1", "1");
			
			List<Campos> listCamposBlq3 = new ArrayList<>();
			
			listCamposBlq3.add(new Campos("COMPONENTE", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
				      .getProccodeParams().split("\\-")).get(0), "1", "false"));
		    listCamposBlq3.add(new Campos("CCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
			      .getProccodeParams().split("\\-")).get(1), "1", "false"));
			
		    if(iso.getWsTransactionConfig().getProccodeTransactionFit()
				            .startsWith("04-0844")) {
		    	listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), iso.getISO_123_ExtendedData(), "1", "false"));
		    	
		    }else {
		    	
		    	listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
					      .getProccodeParams().split("\\-")).get(2), "1", "false"));
		    }
			
			listCamposBlq3.add(new Campos("MONEDA", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHAVALOR", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("FECHADISPONIBLE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
			listCamposBlq3.add(new Campos("VALOR", StringUtils.Empty(), df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
			
			blq3.setCampos(listCamposBlq3);
			
			List<Bloques> listBlq = new ArrayList<>();
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
		
			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::parseNotaCreditoDebitoBIMO ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
    
    private boolean analizeFeeTransaction(wIso8583 iso, Ref<DataReturnParser> dataR){
    
    	boolean responseCode = false;
    	DataReturnParser retorno = dataR.get();
    	try {
			
    		String [] dataAdditional = iso.getISO_120_ExtendedData().trim().split("\\*");
    		String data = Arrays.stream(dataAdditional)
    				      .filter(p -> p.startsWith("S2C"))
    				      .findFirst().orElseGet(() -> null);
    		
    		if(data != null){
    			
    			data = data.replace("S2C", StringUtils.Empty());
    			int value = Integer.parseInt(data);
    			if(value > 0)

    				responseCode = true;
    			else
    				responseCode = false;	
    		}else
    			responseCode = false;
    			
    		retorno.setCodError("000");
    		
		} catch (Exception e) {
			
			
			retorno.setCodError("909");
			retorno.setDesError("ERROR AL IDENTIFICAR COMISION DE LA TRX. " + 
			     GeneralUtils.ExceptionToString(null, e, false));
		}
    	dataR.set(retorno);
    	return responseCode;
    }
    
    public DataReturnParser parseMantenimientoCanalesFit1(wIso8583 iso){
		
		DataReturnParser data = null;
		//final List<wIso8583> isoAux = new ArrayList<>();
		//List<wIso8583> isoResAux = new ArrayList<>();
		//IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
		ConfigChannelsDispFit1 configTrx = null;
		
		//ExecutorService executor = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			data = new DataReturnParser();
			
			/*Anasis de la comision de la Transaccion */
			Ref<DataReturnParser> dataReturnFee = new Ref<Fit1Parser.DataReturnParser>(data);
			boolean fee = analizeFeeTransaction(iso, dataReturnFee);
			data = dataReturnFee.get();
			
			if(data.getCodError().equals("000")){
			
				/*Si tiene comision en la trama y si es debito*/
				if(fee && Arrays.asList(iso.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\-")).get(2).equalsIgnoreCase("D")){
					
					configTrx = new ConfigChannelsDispFit1(Arrays.asList(iso.getWsTransactionConfig()
							.getProccodeParams().split("\\-")).get(0), 
							Arrays.asList(iso.getWsTransactionConfig()
							.getProccodeParams().split("\\-")).get(1), iso.getISO_041_CardAcceptorID());
					
				}else{
					
					/*Si es una consulta o debito sin consulta*/
					configTrx = new ConfigChannelsDispFit1(Arrays.asList(iso.getWsTransactionConfig()
							.getProccodeTransactionFit().split("\\-")).get(0), 
							Arrays.asList(iso.getWsTransactionConfig()
						    .getProccodeTransactionFit().split("\\-")).get(1), iso.getISO_041_CardAcceptorID());
				}
				
			}else{
				
				return data;
			}
				
			if(configTrx.getError().startsWith("000")){
			
				DetailFit1 msg = new DetailFit1();
				
				Cabecera cab = new Cabecera();
				
				/*isoAux.add((wIso8583) iso.clone());
				isoAux.add((wIso8583) iso.clone());
				isoAux.get(0).setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
				
				executor = Executors.newWorkStealingPool();
				
				List<Callable<wIso8583>> callables = Arrays.asList(
				        () -> sql.getFechaContableFit1(isoAux.get(0)),
				        () -> sql.getDataPersonFit1(isoAux.get(1))
				);
				
				isoResAux = (List<wIso8583>) executor.invokeAll(callables)
					    .stream()
					    .map(future -> {
					        try {
					            return future.get();
					        }
					        catch (Exception e) {
					            throw new IllegalStateException(e);
					        }
					    })
					    .peek(Objects::requireNonNull)
					    .collect(Collectors.toList());
				
				if(isoResAux != null){
					
					wIso8583 res  = isoResAux.stream()
					          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
					          .findFirst().orElseGet(()-> null);
					
					if(res != null){
						
						data.setCodError(res.getISO_039_ResponseCode());
						data.setDesError(res.getISO_039p_ResponseDetail());
						return data;
					}
					
				}else{
					
					data.setCodError("909");
					data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, DATOS PERSONA (LAMBDA NULL EXCEPTION)");
					return data;
				}
				*/
				
				/*Obtencion de varios parametros de la cabecera del mensaje, dependiendo del canal, y dispositivo*/
				Ref<Cabecera> cabRef = new Ref<Cabecera>(cab);
				Ref<ConfigChannelsDispFit1> chanDis = new Ref<ConfigChannelsDispFit1>(configTrx);
				Ref<DataReturnParser> dataReturn = new Ref<Fit1Parser.DataReturnParser>(data);
				cab = getDataChannelsFit1(cabRef, iso, chanDis, dataReturn);
				
				if(!dataReturn.get().codError.equals("000")){
					
					return data;
				}
				
				cab.setTipoTrx(iso.getISO_004_AmountTransaction() > 0 ? "MAN":"CON");
				cab.setSessionId(MemoryGlobal.sessionSys);
				cab.setFechaTrx(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
				cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
				cab.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
				cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
				cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
				cab.setNivelSeguridad("10");
				cab.setRol(iso.getWsTransactionConfig().getCommonRol());
				cab.setCaducarTrx("0");
				cab.setEsHistorico("0");
				cab.setEsPDF("0");
				cab.setTodoAnterior("0");
				cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
				cab.setCampoLEG(null);
				//cab.setCtipoPersona(isoResAux.get(1).getISO_034_PANExt());
				cab.setCampoSRC(StringUtils.Empty());
				cab.setMaximo("0");
				cab.setCampoSEC("4");
				cab.setCampoTDC("1");
				cab.setCampoESP("1");
			
				Detalle det = new Detalle();
				Bloques blq1 = new Bloques("0","1","1");
				List<Criterios> listCriterios = new ArrayList<>();			
				List<Campos> listCampos = new ArrayList<>();
				
				//listCampos.add(new Campos("CEDULA", StringUtils.Empty(), isoResAux.get(1).getISO_121_ExtendedData(), "1", "false"));
				//listCampos.add(new Campos("NOMLEGAL", StringUtils.Empty(), isoResAux.get(1).getISO_122_ExtendedData(), "1", "false"));
				listCampos.add(new Campos("FCN", StringUtils.Empty(),  cab.getFechaContable() + " 00-00-00", "1", "false"));
				//listCampos.add(new Campos("CUENTACREDITO", StringUtils.Empty(), iso.getWsTransactionConfig().getProccodeTransactionFit()
				//		            .startsWith("04-0162-00")?iso.getISO_102_AccountID_1():StringUtils.Empty(), "1", "false"));
				//listCampos.add(new Campos("MONEDACUENTACREDITO", StringUtils.Empty(), isoResAux.get(1).getISO_124_ExtendedData(), "1", "false"));
				listCampos.add(new Campos("CUENTADEBITO", StringUtils.Empty(), iso.getISO_102_AccountID_1().trim(), "1", "false"));
				listCampos.add(new Campos("MONEDACUENTADEBITO", StringUtils.Empty(), iso.getISO_049_TranCurrCode() == 840 ? "USD":"USD", "1", "false"));
				blq1.setCampos(listCampos);
				
				//listCriterios.add(new Criterios("518005", isoResAux.get(1).getISO_120_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));//Cpersona
				listCriterios.add(new Criterios("192030", StringUtils.Empty(), StringUtils.Empty(), StringUtils.Empty()));
				//listCriterios.add(new Criterios("518009", isoResAux.get(1).getISO_122_ExtendedData(), StringUtils.Empty(), StringUtils.Empty()));
				listCriterios.add(new Criterios("518002", iso.getISO_102_AccountID_1().trim(), null, null));
					
				
				blq1.setCriterios(listCriterios);
				
				Bloques blq2 = new Bloques("1", "1", "1");
				List<Campos> listCamposBlq2 = new ArrayList<>();
				listCamposBlq2.add(new Campos("DESCRIPCION", StringUtils.Empty(), configTrx.getChannelsFit1().getDes2(), "1", "false"));
				blq2.setCampos(listCamposBlq2);
				
				DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
			    simbolo.setDecimalSeparator('.');
				DecimalFormat df = new DecimalFormat("0.00",simbolo);
				
				Bloques blq3 = new Bloques("2", "1", "1");
				List<Campos> listCamposBlq3 = new ArrayList<>();
				listCamposBlq3.add(new Campos("COMPONENTE", StringUtils.Empty(), configTrx.getChannelsFit1().getsComponente(), "1", "false"));
				//listCamposBlq3.add(new Campos("CCONCEPTO", StringUtils.Empty(), Arrays.asList(iso.getWsTransactionConfig()
				//	      .getProccodeParams().split("\\-")).get(1), "1", "false"));
				//listCamposBlq3.add(new Campos("DCONCEPTO", StringUtils.Empty(), iso.getISO_123_ExtendedData(), "1", "false"));
				listCamposBlq3.add(new Campos("MONEDA", StringUtils.Empty(), iso.getISO_049_TranCurrCode() == 840 ? "USD":"USD", "1", "false"));
				listCamposBlq3.add(new Campos("FECHAVALOR", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
				listCamposBlq3.add(new Campos("FECHADISPONIBLE", StringUtils.Empty(), StringUtils.Empty(), "1", "false"));
				listCamposBlq3.add(new Campos("VALOR", StringUtils.Empty(), df.format(iso.getISO_004_AmountTransaction()), "1", "false"));
				blq3.setCampos(listCamposBlq3);
				
				List<Bloques> listBlq = new ArrayList<>();
				listBlq.add(blq1);
				listBlq.add(blq2);
				listBlq.add(blq3);
			
				
				det.setBloques(listBlq);
				
				msg.setCabecera(cab);
				msg.setDetalle(det);
				
				data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
				data.setCodError("111");
				
			}else {
				
				data.setCodError(configTrx.getError().substring(0,3));
				data.setDesError(configTrx.getError().substring(3));
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::parseRetiroAtmCoonecta ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 " + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return data;
	}
    
	public DataReturnParser parseReversoFit1(wIso8583 iso){
		
		DataReturnParser data = null;
		try {
			
			DetailFit1 msg = new DetailFit1();
			data = new DataReturnParser();	
			wIso8583 isoRetrieve = new wIso8583();
			IsoRetrievalTransaction retrieve = new IsoRetrievalTransaction();
			isoRetrieve = retrieve.RetrieveTransactionIso(iso, 
					      iso.getWsTransactionConfig().getProccodeReverFlag());
				System.out.println("Campo 44  Fit1(Reverso)----- >  " + isoRetrieve.getISO_044_AddRespData());
				
			if(iso.getWsISO_SF_Count() > 0){
				if(!StringUtils.isNullOrEmpty(isoRetrieve.getISO_044_AddRespData()))
					isoRetrieve.setISO_039_ResponseCode("000");
			}
			if(isoRetrieve.getISO_039_ResponseCode().equals("000")){
	
				iso.getWsTransactionConfig().setProccodeReverseFitOriginal(iso.getWsTransactionConfig().getProccodeTransactionFit());
				iso.getWsTransactionConfig().setProccodeTransactionFit(MemoryGlobal.messageReverseUCIFit1);
				iso.getWsTransactionConfig().setRevRetrievalIso011fromBdd(isoRetrieve.getISO_044_AddRespData());
				iso.setISO_102_AccountID_1(isoRetrieve.getISO_102_AccountID_1().trim());
			}
			else{
				
				data.setCodError(isoRetrieve.getISO_039_ResponseCode());
				data.setDesError(isoRetrieve.getISO_039p_ResponseDetail());
				return data;
			}
			
			Cabecera cab = new Cabecera();
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
				
				Ref<Cabecera> cabValidations = new Ref<Cabecera>(cab);
				Ref<DataReturnParser> resVal = new Ref<DataReturnParser>(data);
				cab = ValidateUserTerminal(cabValidations, iso, resVal);
				if(!resVal.get().getCodError().equals("000")){
					
					return data;
				}
				
			}else{
				
				cab.setUser(iso.getWsTransactionConfig().getUser_Fit());
				cab.setPassword(GeneralUtils.GetSecuencial(32));
				cab.setCodSucursal(iso.getWsTransactionConfig().getCommonCodSursal());
				cab.setCodOficina(iso.getWsTransactionConfig().getCommonCodOficina());
			}
			
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			wIso8583 isoFecCon = new wIso8583();
			isoFecCon.setISO_042_Card_Acc_ID_Code(cab.getCodSucursal());
			isoFecCon = sql.getFechaContableFit1(isoFecCon);
			
			if(!isoFecCon.getISO_039_ResponseCode().equals("000")){
				
				data.setCodError(isoFecCon.getISO_039_ResponseCode());
				data.setDesError("ERROR AL RECUPERAR FECHA CONTABLE, " + isoFecCon.getISO_039p_ResponseDetail());
				return data;
			}
				
			cab.setSessionId(MemoryGlobal.sessionSys);
			cab.setTipoTrx(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(3).substring(0,3));
			cab.setFechaTrx(FormatUtils.DateToString(isoRetrieve.getISO_012_LocalDatetime(), "YYYY-MM-dd HH:mm:ss"));
			cab.setIpAutporizada(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(2));
			cab.setSubsistema(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(0));
			cab.setTransaccion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(1));
			cab.setVersion(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\-")).get(2));
			String msgSufijo = "_R";
			System.out.println("Reverso momento poner (FIT1).... " + iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd());
			if(MemoryGlobal.flagSecuenceInReverse)
				cab.setMessageId(GeneralUtils.GetSecuencial(16) + msgSufijo);
			else
				cab.setMessageId(iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd() + msgSufijo);
			cab.setIdioma(iso.getWsTransactionConfig().getCommonIdioma());
			cab.setTerminal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(0));
			cab.setCanal(Arrays.asList(iso.getWsTransactionConfig().getTerm_Name().split("\\-")).get(1));
			cab.setCodCompania(iso.getWsTransactionConfig().getCommonCompania());
			cab.setFechaContable(isoFecCon.getISO_120_ExtendedData());
			cab.setNivelSeguridad("10");
			cab.setRol(iso.getWsTransactionConfig().getCommonRol());
			cab.setCaducarTrx("0");
			cab.setEsHistorico("0");
			cab.setEsPDF("0");
			cab.setTodoAnterior("0");
			cab.setCompaniaID(iso.getWsTransactionConfig().getCommonCompania());
			cab.setCampoLEG(null);
			cab.setCampoESP(null);
			
			Detalle det = new Detalle();
			List<Bloques> listBlq = new ArrayList<>();
			
			
			Bloques blq1 = new Bloques("0","1","0");
			List<Criterios> listCriteriosBlq1 = new ArrayList<>();			
			listCriteriosBlq1.add(new Criterios("369007", cab.getUser(), null, null));// Usuario
			listCriteriosBlq1.add(new Criterios("369006", "ECG", null, null));// Tipo Usuario
			listCriteriosBlq1.add(new Criterios("369006", "1", null, null));// CTIPOUSUARIO quemado 1
			if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y"))
				listCriteriosBlq1.add(new Criterios("369001", iso.getISO_041_CardAcceptorID(), null, null));// ALIAS CAJERO
			else
				listCriteriosBlq1.add(new Criterios("369001", iso.getWsTransactionConfig().getUser_Fit(), null, null));// ALIAS NORMAL
			blq1.setCriterios(listCriteriosBlq1);
			
			
			Bloques blq2 = new Bloques("1","1","0");
			
			Bloques blq3 = new Bloques("2","1","0");
			List<Criterios> listCriteriosBlq3 = new ArrayList<>();
			listCriteriosBlq3.add(new Criterios("659008", null, null, null));// VCAJATRANSACCIONESDIA	CSUBSISTEMA_TRANSACCION
			listCriteriosBlq3.add(new Criterios("659011", null, null, null));// VCAJATRANSACCIONESDIA	CTRANSACCION
			listCriteriosBlq3.add(new Criterios("659010", null, null, null));// VCAJATRANSACCIONESDIA	CTIPOCOMPONENTE
			listCriteriosBlq3.add(new Criterios("659001", null, null, null));// VCAJATRANSACCIONESDIA	CCOMPONENTECODIGO
			listCriteriosBlq3.add(new Criterios("659013", null, null, null));// VCAJATRANSACCIONESDIA	DEBITOCREDITO
			listCriteriosBlq3.add(new Criterios("659002", null, null, null));// VCAJATRANSACCIONESDIA	CCUENTA
			
			
			
			Bloques blq4 = new Bloques("3","1","1");
			
			List<Campos> listCamposBlq4 = new ArrayList<>();
			
			
			List<Dependencias> listDepCamp1 = new ArrayList<>();
			listDepCamp1.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), null));
			listCamposBlq4.add(new Campos("659017",iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd(),
					iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd(),"1","false",listDepCamp1 ));
			
			
			List<Dependencias> listDepCamp2Blq4 = new ArrayList<>();
			listDepCamp2Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), null));
			listCamposBlq4.add(new Campos("659008",Arrays.asList(iso.getWsTransactionConfig().getProccodeReverseFitOriginal()
					.split("\\-")).get(0),Arrays.asList(iso.getWsTransactionConfig().getProccodeReverseFitOriginal()
					.split("\\-")).get(0),"0","false",listDepCamp2Blq4));
		

			List<Dependencias> listDepCamp3Blq4 = new ArrayList<>();
			listDepCamp3Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), null));
			listCamposBlq4.add(new Campos("659011",Arrays.asList(iso.getWsTransactionConfig().getProccodeReverseFitOriginal()
					.split("\\-")).get(1),Arrays.asList(iso.getWsTransactionConfig().getProccodeReverseFitOriginal()
					.split("\\-")).get(1),"0","false",listDepCamp3Blq4));
			
			List<Dependencias> listDep1Camp4Blq4 = new ArrayList<>();
			listDep1Camp4Blq4.add(new Dependencias("659011", null, Arrays.asList(iso.getWsTransactionConfig()
					.getProccodeReverseFitOriginal()
					.split("\\-")).get(1), "0"));
			listDep1Camp4Blq4.add(new Dependencias("659008", null, Arrays.asList(iso.getWsTransactionConfig()
					.getProccodeReverseFitOriginal()
					.split("\\-")).get(0), "0"));
			listCamposBlq4.add(new Campos("260004",isoRetrieve.getISO_123_ExtendedData(),
					          isoRetrieve.getISO_123_ExtendedData(),"0","false",listDep1Camp4Blq4));
			
		
			List<Dependencias> listDepCamp5Blq4 = new ArrayList<>();
			listDepCamp5Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			listCamposBlq4.add(new Campos("659010",Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(0),Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(0),"0","false",listDepCamp5Blq4));
			
			
			List<Dependencias> listDepCamp6Blq4 = new ArrayList<>();
			listDepCamp6Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			listCamposBlq4.add(new Campos("659001",Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(1),Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(1),"0","false",listDepCamp6Blq4));
			

			List<Dependencias> listDepCamp7Blq4 = new ArrayList<>();
			listDepCamp7Blq4.add(new Dependencias("659001", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(1), "0"));
			listDepCamp7Blq4.add(new Dependencias("659010", null, Arrays.asList(iso.getWsTransactionConfig().getProccodeParams()
					.split("\\-")).get(0), "0"));
			listCamposBlq4.add(new Campos("50004","PRINCIPAL","PRINCIPAL","0","false",listDepCamp7Blq4));
			
			
			List<Dependencias> listDepCamp8Blq4 = new ArrayList<>();
			listDepCamp8Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			listCamposBlq4.add(new Campos("659013", iso.getISO_003_ProcessingCode().startsWith("01")?"D":"C" ,
					  iso.getISO_003_ProcessingCode().startsWith("01")?"D":"C","0","false",listDepCamp8Blq4));
			

			listCamposBlq4.add(new Campos("659002", iso.getISO_102_AccountID_1(), iso.getISO_102_AccountID_1(),"0","false"));
			
			
			List<Dependencias> listDepCamp10Blq4 = new ArrayList<>();
			listDepCamp10Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			//listCamposBlq4.add(new Campos("659015", FormatUtils.DateToString(isoRetrieve.getISO_012_LocalDatetime(), "YYYY-MM-dd HH-mm-ss") ,
			//		FormatUtils.DateToString(isoRetrieve.getISO_012_LocalDatetime(), "YYYY-MM-dd HH-mm-ss"),"0","false",listDepCamp10Blq4));
			
			//listCamposBlq4.add(new Campos("659015", FormatUtils.DateToStringThreadSafe(new Date(), "YYYY-MM-dd HH-mm-ss.SSS") ,
			//		FormatUtils.DateToStringThreadSafe(new Date(), "YYYY-MM-dd HH-mm-ss.SSS"),"0","false",listDepCamp10Blq4));
			
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			List<Dependencias> listDepCamp11Blq4 = new ArrayList<>();
			listDepCamp11Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			listCamposBlq4.add(new Campos("659021", "1", df.format(iso.getISO_004_AmountTransaction()),"0","false",listDepCamp11Blq4));
			
			
			List<Dependencias> listDepCamp12Blq4 = new ArrayList<>();
			listDepCamp12Blq4.add(new Dependencias("659002", null, iso.getISO_102_AccountID_1().trim(), "0"));
			listCamposBlq4.add(new Campos("659018", "0", "1","0","false",listDepCamp12Blq4));

			listCamposBlq4.add(new Campos("1681019", null, null,"1","false"));
			
			blq4.setCampos(listCamposBlq4);
			
			
			blq1.setCriterios(listCriteriosBlq1);
			blq3.setCriterios(listCriteriosBlq3);
			
			listBlq.add(blq1);
			listBlq.add(blq2);
			listBlq.add(blq3);
			listBlq.add(blq4);

			
			det.setBloques(listBlq);
			
			msg.setCabecera(cab);
			msg.setDetalle(det);
			
			data.setData(SerializationObject.ObjectToString(msg, DetailFit1.class));
			data.setCodError("111");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::parseReversoFit1 ", TypeMonitor.error, e);
			data.setCodError("909");
			data.setDesError("ERROR AL PARSEAR DETAIL PARA FIT1 (REVERSO)" + 
			GeneralUtils.ExceptionToString(null, e, false));
		}
		return data;
	}
	
	private Cabecera getDataChannelsFit1(Ref<Cabecera> cab, wIso8583 iso, 
			Ref<ConfigChannelsDispFit1> config, Ref<DataReturnParser> data){
		
		Cabecera _cab = null;
		DataReturnParser _data = data.get();
		IsoRetrievalTransaction sql = null;
		wIso8583 isoAux = null;
		try {
			
			sql = new IsoRetrievalTransaction();
			ChannelsFit1  infoChannel = config.get().getChannelsFit1();
			DispositivosFit1 infoDispo = config.get().getDispoFit1();
			isoAux = (wIso8583) iso.clone();
			isoAux.setISO_042_Card_Acc_ID_Code(infoDispo.getcTerminal());
			isoAux = sql.getFechaContableFit1_Channels(isoAux);
			if(isoAux.getISO_039_ResponseCode().equals("000")){
			
				_cab = cab.get();
				_cab.setUser(infoDispo.getcUsuario());
				//_cab.setCanal(infoDispo.getcCanal());
				_cab.setCanal(isoAux.getISO_123_ExtendedData());
				_cab.setTerminal(infoDispo.getcTerminal());
				_cab.setSubsistema(infoChannel.getcSubsistema_FIT());
				_cab.setTransaccion(infoChannel.getcTransaccion_FIT());
				_cab.setVersion(infoChannel.getVersionTransaccion());
				_cab.setCodSucursal(isoAux.getISO_121_ExtendedData());
				_cab.setCodOficina(isoAux.getISO_122_ExtendedData());
				_cab.setFechaContable(isoAux.getISO_120_ExtendedData());
			
			}else{
				
				_data.setCodError(isoAux.getISO_039_ResponseCode());
				_data.setCodError(isoAux.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::getDataChannelsFit1 ", TypeMonitor.error, e);
		}
		 data.set(_data);
			cab.set(_cab);
		return cab.get();
	}
	
	private Cabecera ValidateUserTerminal(Ref<Cabecera> cab, wIso8583 iso, Ref<DataReturnParser> data){
		
		Cabecera _cab = null;
		DataReturnParser _data = data.get();
		try {
			
			_cab = cab.get();
			User_Channel usrch = new User_Channel();
			usrch = usrch.getUserChannel(iso.getISO_018_MerchantType(), 
										 iso.getISO_041_CardAcceptorID());
			
			  if(usrch != null){
				
				_cab.setUser(usrch.getUser_fit());
				_cab.setCodSucursal(usrch.getUch_sucursal());
				_cab.setCodOficina(usrch.getUch_oficina());
				_cab.setPassword(usrch.getUser_pass());
				
			}else{
				
				_data.setCodError("101");
				_data.setDesError("TERMINAL " + iso.getISO_041_CardAcceptorID() + 
						", CANAL: " + iso.getISO_018_MerchantType() + " NO REGISTRADOS");
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo Fit1Parser::ValidateUserTerminal ", TypeMonitor.error, e);
		}
			    data.set(_data);
				cab.set(_cab);
		return cab.get();
	}
	
	public class DataReturnParser{
		
		private String identificador;
		private String cPersona;
		private String numCuenta;
		private String tipoIdentificacion;
		private String tipoPersona;
		private String nombresCuenta;
		private String data;
		private String codError;
		private String desError;
		
		public DataReturnParser() {
			
			this.codError = "000";
			this.data = null;
			this.desError = "PARSEO EXITOSO";
		}
		
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getCodError() {
			return codError;
		}
		public void setCodError(String codError) {
			this.codError = codError;
		}
		public String getDesError() {
			return desError;
		}
		public void setDesError(String desError) {
			this.desError = desError;
		}

		public String getIdentificador() {
			return identificador;
		}

		public void setIdentificador(String identificador) {
			this.identificador = identificador;
		}

		public String getcPersona() {
			return cPersona;
		}

		public void setcPersona(String cPersona) {
			this.cPersona = cPersona;
		}

		public String getNumCuenta() {
			return numCuenta;
		}

		public void setNumCuenta(String numCuenta) {
			this.numCuenta = numCuenta;
		}

		public String getTipoIdentificacion() {
			return tipoIdentificacion;
		}

		public void setTipoIdentificacion(String tipoIdentificacion) {
			this.tipoIdentificacion = tipoIdentificacion;
		}

		public String getNombresCuenta() {
			return nombresCuenta;
		}

		public void setNombresCuenta(String nombresCuenta) {
			this.nombresCuenta = nombresCuenta;
		}

		public String getTipoPersona() {
			return tipoPersona;
		}

		public void setTipoPersona(String tipoPersona) {
			this.tipoPersona = tipoPersona;
		}
	
	}

}
class ConfigChannelsDispFit1{
	
	private Logger log;
	private String codCanal;
	private String cTransaccionATM;
	private String codTerminal;
	private ChannelsFit1 channelsFit1;
	private DispositivosFit1 dispoFit1;
	private String error;
	
	
	public ConfigChannelsDispFit1() {
		
		log = new Logger();
		this.channelsFit1 = null;
		this.dispoFit1 = null;
		this.error = "000TRANSACCION EXITIOSA";
	}
	
	public ConfigChannelsDispFit1(String codCanal, String cTransaccionATM, String codTerminal) {
		
		this();
		this.codCanal = codCanal;
		this.cTransaccionATM = cTransaccionATM;
		this.codTerminal = codTerminal;
		
		getDataCanalFit1();
		getDataDispositivoFit1();
	}
	
	
	public String getError() {
		return error;
	}

	public ChannelsFit1 getChannelsFit1() {
		return channelsFit1;
	}
	public void setChannelsFit1(ChannelsFit1 channelsFit1) {
		this.channelsFit1 = channelsFit1;
	}
	public DispositivosFit1 getDispoFit1() {
		return dispoFit1;
	}
	public void setDispoFit1(DispositivosFit1 dispoFit1) {
		this.dispoFit1 = dispoFit1;
	}
	
	private void  getDataCanalFit1(){
		
		ChannelsFit1 channFit1 = null;
		try {
			
			channFit1 = new ChannelsFit1();
			channFit1 = channFit1.getChannelsFit1_Object(this.codCanal, this.cTransaccionATM);
			if(channFit1 != null){
				
				this.channelsFit1 = channFit1;
				
			}else{
				
				this.error = "100ERROR NO SE PUDO RECUPERAR INFORMACION DE CANAL PARA LA TRANSACCION";
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ConfigChannelsDispFit1::getDataCanalFit1 ", TypeMonitor.error, e);
		}
		
	}
	
	private void getDataDispositivoFit1(){
			
			DispositivosFit1 cdisFit1 = null;
			String terminal = null;
			try {
				
				cdisFit1 = new DispositivosFit1();
			
				switch (this.codCanal) {
				case "JTM":
					
						terminal = this.codTerminal.startsWith("ATM" + MemoryGlobal.codCoonectaCoop) 
						           ? this.codTerminal:"JTMIN";
						cdisFit1 = cdisFit1.getDispositivosFit1_Object(this.codCanal, terminal);
						
					break;

				default:
					break;
				}
				
				if(cdisFit1 != null){
					
					this.dispoFit1 = cdisFit1;
					
				}else{
					
					this.error = "100ERROR NO SE PUDO RECUPERAR TDISPOSITIVO PARA LA TRANSACCION";
				}
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo ConfigChannelsDispFit1::getDataCanalFit1 ", TypeMonitor.error, e);
			}
			
		}
}
