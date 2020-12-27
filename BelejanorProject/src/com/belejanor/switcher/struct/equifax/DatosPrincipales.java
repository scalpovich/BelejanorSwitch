package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"personaId", "tipoDocumento","numeroDocummento","ingresosUnidadFamiliar",
		"gastosFinancierosInternos", "fechaNacimientoPrincipal","fechaNacimientoConyugue", "gastosHogarUnidadFamiliar",
		"tipoDocumentoConyuge","numeroDocumentoConyuge"})
public class DatosPrincipales implements Serializable{

	
	public DatosPrincipales() {
		
		this.personaId = StringUtils.Empty();
		this.tipoDocumento = StringUtils.Empty();
		this.numeroDocummento = StringUtils.Empty();
		this.ingresosUnidadFamiliar = 0;
		this.gastosFinancierosInternos = 0;
		this.gastosHogarUnidadFamiliar = 0;
		this.tipoDocumentoConyuge = null;
		this.numeroDocumentoConyuge = null;
		this.fechaNacimientoConyugue = null;
		this.fechaNacimientoPrincipal = null;
	}
	
	private String personaId;
	private String tipoDocumento;
	private String numeroDocummento;
	private double ingresosUnidadFamiliar;
	private double gastosHogarUnidadFamiliar;
	private double gastosFinancierosInternos;
	private String tipoDocumentoConyuge;
	private String numeroDocumentoConyuge;
	private String fechaNacimientoPrincipal;
	private String fechaNacimientoConyugue;
	
	@XmlElement(name="PersonaId")
	public String getPersonaId() {
		return personaId;
	}
	public void setPersonaId(String personaId) {
		this.personaId = personaId;
	}
	@XmlElement(name="TipoDocumento")
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	@XmlElement(name="NumeroDocummento")
	public String getNumeroDocummento() {
		return numeroDocummento;
	}
	public void setNumeroDocummento(String numeroDocummento) {
		this.numeroDocummento = numeroDocummento;
	}
	@XmlElement(name="IngresosUnidadFamiliar")
	public double getIngresosUnidadFamiliar() {
		return ingresosUnidadFamiliar;
	}
	public void setIngresosUnidadFamiliar(double ingresosUnidadFamiliar) {
		this.ingresosUnidadFamiliar = ingresosUnidadFamiliar;
	}
	@XmlElement(name="GastosHogarUnidadFamiliar")
	public double getGastosHogarUnidadFamiliar() {
		return gastosHogarUnidadFamiliar;
	}
	public void setGastosHogarUnidadFamiliar(double gastosHogarUnidadFamiliar) {
		this.gastosHogarUnidadFamiliar = gastosHogarUnidadFamiliar;
	}
	@XmlElement(name="GastosFinancierosInternos")
	public double getGastosFinancierosInternos() {
		return gastosFinancierosInternos;
	}
	public void setGastosFinancierosInternos(double gastosFinancierosInternos) {
		this.gastosFinancierosInternos = gastosFinancierosInternos;
	}
	@XmlElement(name="TipoDocumentoConyuge")
	public String getTipoDocumentoConyuge() {
		return tipoDocumentoConyuge;
	}
	public void setTipoDocumentoConyuge(String tipoDocumentoConyuge) {
		this.tipoDocumentoConyuge = tipoDocumentoConyuge;
	}
	@XmlElement(name="NumeroDocumentoConyuge")
	public String getNumeroDocumentoConyuge() {
		return numeroDocumentoConyuge;
	}
	public void setNumeroDocumentoConyuge(String numeroDocumentoConyuge) {
		this.numeroDocumentoConyuge = numeroDocumentoConyuge;
	}
	@XmlElement(name="FechaNacimientoTitular")
	public String getFechaNacimientoPrincipal() {
		return fechaNacimientoPrincipal;
	}
	public void setFechaNacimientoPrincipal(String fechaNacimientoPrincipal) {
		this.fechaNacimientoPrincipal = fechaNacimientoPrincipal;
	}
	@XmlElement(name="FechaNacimientoConyuge")
	public String getFechaNacimientoConyugue() {
		return fechaNacimientoConyugue;
	}
	public void setFechaNacimientoConyugue(String fechaNacimientoConyugue) {
		this.fechaNacimientoConyugue = fechaNacimientoConyugue;
	}
	
}
