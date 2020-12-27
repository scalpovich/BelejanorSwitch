package com.belejanor.switcher.fit1struct;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;

@XmlType(propOrder={"user", "password", "sessionId","tipoTrx","fechaTrx",
		"ipAutporizada","mac","subsistema","transaccion","version","messageId",
		"idioma","terminal","canal","codCompania","codSucursal","codOficina",
		"fechaContable","nivelSeguridad","rol","campoCIN","caducarTrx","esHistorico",
		"esPDF","todoAnterior","companiaID","campoSBT","numero","campoSRC","maximo",
		"campoSEC","campoTDC","ctipoPersona","campoTPR","campoESP","campoLEG","campoRES",
		"campoTOPx","campoTOPy","campoWID","campoHEI","campoUNID","campoGUI","campoDOC"})
public class Cabecera {

	private String user;
	private String password;
	private String sessionId;
	private String tipoTrx;
	private String fechaTrx;
	private String ipAutporizada;
	private String mac;
	private String subsistema;
	private String transaccion;
	private String version;
	private String messageId;
	private String idioma;
	private String terminal;
	private String canal;
	private String codCompania;
	private String codSucursal;
	private String codOficina;
	private String fechaContable;
	private String nivelSeguridad;
	private String rol;
	private String campoCIN;
	private String caducarTrx;
	private String esHistorico;
	private String esPDF;
	private String todoAnterior;
	private String companiaID;
	private String campoSBT;
	private String numero;
	private String campoSRC;
	private String maximo;
	private String campoSEC;
	private String campoTDC;
	private String ctipoPersona;
	private String campoTPR;
	private String campoESP;
	private String campoLEG;
	private String campoRES;
	private String campoTOPx;
	private String campoTOPy;
	private String campoWID;
	private String campoHEI;
	private String campoUNID;
	private String campoGUI;
	private String campoDOC;
	
	
	public  Cabecera() {
		
		this.user = null;
		this.password = null;
		this.sessionId = null;
		this.tipoTrx = null;
		this.fechaTrx = FormatUtils.DateToString(new Date(), "YYYY-MM-dd HH:mm:ss");
		this.ipAutporizada = "127.0.0.1";
		this.mac = null;
		this.subsistema = null;
		this.transaccion = null;
		this.version = null;
		this.messageId = null;
		this.idioma = null;
		this.terminal = null;
		this.canal = null;
		this.codCompania = "2";
		this.codSucursal = "1";
		this.codOficina = "1";
		this.fechaContable = FormatUtils.DateToString(new Date(), "YYYY-MM-dd");
		this.nivelSeguridad = "10";
		this.rol = "1";
		this.campoCIN = null;
		this.caducarTrx = null;
		this.esHistorico = null;
		this.esPDF = null;
		this.todoAnterior = null;
		this.companiaID = "2";
		this.campoSBT = null;
		this.numero = null;
		this.campoSRC = null;
		this.maximo = null;
		this.campoSEC = null;
		this.campoTDC = null;
		this.campoTPR = null;
		this.campoESP = "1";
		this.campoLEG = "30000";
		
	}
	
	@XmlElement(name="USR")
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@XmlElement(name="PWD")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@XmlElement(name="SID")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	@XmlElement(name="TIP")
	public String getTipoTrx() {
		return tipoTrx;
	}
	public void setTipoTrx(String tipoTrx) {
		this.tipoTrx = tipoTrx;
	}
	@XmlElement(name="FTR")
	public String getFechaTrx() {
		return fechaTrx;
	}
	public void setFechaTrx(String fechaTrx) {
		this.fechaTrx = fechaTrx;
	}
	@XmlElement(name="IPA")
	public String getIpAutporizada() {
		return ipAutporizada;
	}
	public void setIpAutporizada(String ipAutporizada) {
		this.ipAutporizada = ipAutporizada;
	}
	@XmlElement(name="MAC")
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	@XmlElement(name="SUB")
	public String getSubsistema() {
		return subsistema;
	}
	public void setSubsistema(String subsistema) {
		this.subsistema = subsistema;
	}
	@XmlElement(name="TRN")
	public String getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}
	@XmlElement(name="VER")
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@XmlElement(name="MSG")
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	@XmlElement(name="IDM")
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	@XmlElement(name="TER")
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	@XmlElement(name="CAN")
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	@XmlElement(name="CIO")
	public String getCodCompania() {
		return codCompania;
	}
	public void setCodCompania(String codCompania) {
		this.codCompania = codCompania;
	}
	@XmlElement(name="SUC")
	public String getCodSucursal() {
		return codSucursal;
	}
	public void setCodSucursal(String codSucursal) {
		this.codSucursal = codSucursal;
	}
	@XmlElement(name="OFC")
	public String getCodOficina() {
		return codOficina;
	}
	public void setCodOficina(String codOficina) {
		this.codOficina = codOficina;
	}
	@XmlElement(name="FCN")
	public String getFechaContable() {
		return fechaContable;
	}
	public void setFechaContable(String fechaContable) {
		this.fechaContable = fechaContable;
	}
	@XmlElement(name="NVS")
	public String getNivelSeguridad() {
		return nivelSeguridad;
	}
	public void setNivelSeguridad(String nivelSeguridad) {
		this.nivelSeguridad = nivelSeguridad;
	}
	@XmlElement(name="ROL")
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	@XmlElement(name="CIN")
	public String getCampoCIN() {
		return campoCIN;
	}
	public void setCampoCIN(String campoCIN) {
		this.campoCIN = campoCIN;
	}
	@XmlElement(name="CAD")
	public String getCaducarTrx() {
		return caducarTrx;
	}
	public void setCaducarTrx(String caducarTrx) {
		this.caducarTrx = caducarTrx;
	}
	@XmlElement(name="HIS")
	public String getEsHistorico() {
		return esHistorico;
	}
	public void setEsHistorico(String esHistorico) {
		this.esHistorico = esHistorico;
	}
	@XmlElement(name="PDF")
	public String getEsPDF() {
		return esPDF;
	}
	public void setEsPDF(String esPDF) {
		this.esPDF = esPDF;
	}
	@XmlElement(name="ALL")
	public String getTodoAnterior() {
		return todoAnterior;
	}
	public void setTodoAnterior(String todoAnterior) {
		this.todoAnterior = todoAnterior;
	}
	@XmlElement(name="CID")
	public String getCompaniaID() {
		return companiaID;
	}
	public void setCompaniaID(String companiaID) {
		this.companiaID = companiaID;
	}
	@XmlElement(name="SBT")
	public String getCampoSBT() {
		return campoSBT;
	}
	public void setCampoSBT(String campoSBT) {
		this.campoSBT = campoSBT;
	}
	@XmlElement(name="NUM")
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	@XmlElement(name="SRC")
	public String getCampoSRC() {
		return campoSRC;
	}
	public void setCampoSRC(String campoSRC) {
		this.campoSRC = campoSRC;
	}
	@XmlElement(name="MAX")
	public String getMaximo() {
		return maximo;
	}
	public void setMaximo(String maximo) {
		this.maximo = maximo;
	}
	@XmlElement(name="SEC")
	public String getCampoSEC() {
		return campoSEC;
	}
	public void setCampoSEC(String campoSEC) {
		this.campoSEC = campoSEC;
	}
	@XmlElement(name="TDC")
	public String getCampoTDC() {
		return campoTDC;
	}
	public void setCampoTDC(String campoTDC) {
		this.campoTDC = campoTDC;
	}
	@XmlElement(name="TPR")
	public String getCtipoPersona() {
		return ctipoPersona;
	}

	public void setCtipoPersona(String ctipoPersona) {
		this.ctipoPersona = ctipoPersona;
	}

	@XmlElement(name="TPR")
	public String getCampoTPR() {
		return campoTPR;
	}
	public void setCampoTPR(String campoTPR) {
		this.campoTPR = campoTPR;
	}
	@XmlElement(name="ESP")
	public String getCampoESP() {
		return campoESP;
	}
	public void setCampoESP(String campoESP) {
		this.campoESP = campoESP;
	}
	@XmlElement(name="LEG")
	public String getCampoLEG() {
		return campoLEG;
	}
	public void setCampoLEG(String campoLEG) {
		this.campoLEG = campoLEG;
	}
	@XmlElement(name="RES")
	public String getCampoRES() {
		return campoRES;
	}

	public void setCampoRES(String campoRES) {
		this.campoRES = campoRES;
	}
	@XmlElement(name="TOPX")
	public String getCampoTOPx() {
		return campoTOPx;
	}

	public void setCampoTOPx(String campoTOPx) {
		this.campoTOPx = campoTOPx;
	}
	@XmlElement(name="TOPY")
	public String getCampoTOPy() {
		return campoTOPy;
	}

	public void setCampoTOPy(String campoTOPy) {
		this.campoTOPy = campoTOPy;
	}
	@XmlElement(name="WID")
	public String getCampoWID() {
		return campoWID;
	}

	public void setCampoWID(String campoWID) {
		this.campoWID = campoWID;
	}
	@XmlElement(name="HEI")
	public String getCampoHEI() {
		return campoHEI;
	}

	public void setCampoHEI(String campoHEI) {
		this.campoHEI = campoHEI;
	}
	@XmlElement(name="UNID")
	public String getCampoUNID() {
		return campoUNID;
	}

	public void setCampoUNID(String campoUNID) {
		this.campoUNID = campoUNID;
	}
	@XmlElement(name="GUI")
	public String getCampoGUI() {
		return campoGUI;
	}

	public void setCampoGUI(String campoGUI) {
		this.campoGUI = campoGUI;
	}
	@XmlElement(name="DOC")
	public String getCampoDOC() {
		return campoDOC;
	}

	public void setCampoDOC(String campoDOC) {
		this.campoDOC = campoDOC;
	}
	

}

