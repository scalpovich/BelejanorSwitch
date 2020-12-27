package com.belejanor.switcher.snp.spi;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

@SuppressWarnings("serial")
public class SnpOrdLotes extends Thread implements Serializable{

	private String fecha;
	private String num_referencia;
	private String ord_identificacion;
	private String ord_tipo_identificacion;
	private String ord_nombres;
	private String ord_numero_cuenta;
	private String ord_tipo_cta;
	private double valor_transferencia;
	private String recep_identificacion;
	private String recep_tipo_identificacion;
	private String recep_nombres;
	private String recep_numero_cuenta;
	private String recep_tipo_cta;
	private String recep_institucion;
	private String estado;
	private String observacion;
	private String motivo_transferencia;
	private String referencia_bce;
	
	public SnpOrdLotes() {
		super();
	}

	public String getFecha() {
		return fecha;
	}





	public void setFecha(String fecha) {
		this.fecha = fecha;
	}





	public String getNum_referencia() {
		return num_referencia;
	}





	public void setNum_referencia(String num_referencia) {
		this.num_referencia = num_referencia;
	}





	public String getOrd_identificacion() {
		return ord_identificacion;
	}





	public void setOrd_identificacion(String ord_identificacion) {
		this.ord_identificacion = ord_identificacion;
	}





	public String getOrd_tipo_identificacion() {
		return ord_tipo_identificacion;
	}





	public void setOrd_tipo_identificacion(String ord_tipo_identificacion) {
		this.ord_tipo_identificacion = ord_tipo_identificacion;
	}





	public String getOrd_nombres() {
		return ord_nombres;
	}





	public void setOrd_nombres(String ord_nombres) {
		this.ord_nombres = ord_nombres;
	}





	public String getOrd_numero_cuenta() {
		return ord_numero_cuenta;
	}





	public void setOrd_numero_cuenta(String ord_numero_cuenta) {
		this.ord_numero_cuenta = ord_numero_cuenta;
	}





	public String getOrd_tipo_cta() {
		return ord_tipo_cta;
	}





	public void setOrd_tipo_cta(String ord_tipo_cta) {
		this.ord_tipo_cta = ord_tipo_cta;
	}





	public double getValor_transferencia() {
		return valor_transferencia;
	}





	public void setValor_transferencia(double valor_transferencia) {
		this.valor_transferencia = valor_transferencia;
	}





	public String getRecep_identificacion() {
		return recep_identificacion;
	}





	public void setRecep_identificacion(String recep_identificacion) {
		this.recep_identificacion = recep_identificacion;
	}





	public String getRecep_tipo_identificacion() {
		return recep_tipo_identificacion;
	}





	public void setRecep_tipo_identificacion(String recep_tipo_identificacion) {
		this.recep_tipo_identificacion = recep_tipo_identificacion;
	}





	public String getRecep_nombres() {
		return recep_nombres;
	}





	public void setRecep_nombres(String recep_nombres) {
		this.recep_nombres = recep_nombres;
	}





	public String getRecep_numero_cuenta() {
		return recep_numero_cuenta;
	}





	public void setRecep_numero_cuenta(String recep_numero_cuenta) {
		this.recep_numero_cuenta = recep_numero_cuenta;
	}





	public String getRecep_tipo_cta() {
		return recep_tipo_cta;
	}





	public void setRecep_tipo_cta(String recep_tipo_cta) {
		this.recep_tipo_cta = recep_tipo_cta;
	}





	public String getRecep_institucion() {
		return recep_institucion;
	}





	public void setRecep_institucion(String recep_institucion) {
		this.recep_institucion = recep_institucion;
	}





	public String getEstado() {
		return estado;
	}





	public void setEstado(String estado) {
		this.estado = estado;
	}





	public String getObservacion() {
		return observacion;
	}





	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}





	public String getMotivo_transferencia() {
		return motivo_transferencia;
	}





	public void setMotivo_transferencia(String motivo_transferencia) {
		this.motivo_transferencia = motivo_transferencia;
	}





	public String getReferencia_bce() {
		return referencia_bce;
	}





	public void setReferencia_bce(String referencia_bce) {
		this.referencia_bce = referencia_bce;
	}

	
	/*@Override
	public void run(){
		
		String query = "select * from spi_lotes where estado = 'PEND'";	
		DataSetMemoryLoader<SnpOrdLotes> loader = 
	    new DataSetMemoryLoader<SnpOrdLotes>
		(MemoryGlobal.conn, SnpOrdLotes.class, query);
		MemoryGlobal.ListLotes = loader.LoadDataClass();
	}*/
	
	public Callable<List<SnpOrdLotes>> retornaLotesDemmon(){
		
		final Callable<List<SnpOrdLotes>> callable = new Callable<List<SnpOrdLotes>>() {
			
			@Override
			public List<SnpOrdLotes> call() throws Exception {
				
				String query = "SELECT * FROM SPI_LOTES WHERE ESTADO = 'PEND'";	
				DataSetMemoryLoader<SnpOrdLotes> loader = 
			    new DataSetMemoryLoader<SnpOrdLotes>
				(MemoryGlobal.conn, SnpOrdLotes.class, query);
				return  loader.LoadDataClass();
			}
		};
		
		return callable;
	}
	
	public List<SnpOrdLotes> retornaLotesDeamonWithout(){
		
		List<SnpOrdLotes> listOrd = null;
		Logger log = new Logger();
		String query = "SELECT * FROM SPI_LOTES WHERE ESTADO = 'PEND'";	
		DataSetMemoryLoader<SnpOrdLotes> loader = 
	    new DataSetMemoryLoader<SnpOrdLotes>
		(MemoryGlobal.conn, SnpOrdLotes.class, query);
		listOrd =  loader.LoadDataClass();
		log.WriteLogMonitor("********** TOTAL TRANSACCIONES ORDENANTES A PROCESAR: " + listOrd.size() + " ********************", TypeMonitor.monitor, null);
		//if(listOrd.size() == 0)
		//	MemoryGlobal.semaphoreIniLotesSpi.countDown();
		return listOrd;
	}
		
}
