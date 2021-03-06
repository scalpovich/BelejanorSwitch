package com.belejanor.switcher.struct.servipagos;

import com.belejanor.switcher.utils.SerializationObject;

public class Tester {

	public static void main(String[] args) {
		
		
		TrxRequerimientoAfectacion req = new TrxRequerimientoAfectacion();
		TransaccionAfectacion trx = new TransaccionAfectacion();
		DetalleTransaccion detTrx = null;
		Cheques chq = new Cheques();
		
		trx.setTra_codigo_transaccion("126781");
		trx.setTra_serial_transaccion("126781");
		trx.setTra_fecha_proceso("2013-08-22");
		trx.setTra_hora("15:34:16");
		trx.setAge_codigo_agencia("192");
		trx.setUsu_codigo_usuario("2852");
		trx.setTra_fecha_contable("2013-08-22");
		trx.setTra_tipo_transaccion("0001");
		trx.setTra_cat_motivo_reverso("0000");
		trx.setTra_monto_total("125.00");
		trx.setCad_codigo_canal("1");
		trx.setTra_pin_trama("0000");
		trx.setIns_fi_number_origen("0914");
		trx.setTra_message_type_code("TR");
		
		
		CabeceraDetalle cabDet = new CabeceraDetalle();
		cabDet.setDet_codigo_detalle("1");
		cabDet.setIns_fi_number("1361");
		cabDet.setTrb_codigo_traban("1700");
		cabDet.setMot_codigo_motivo("0000");
		cabDet.setMon_codigo_moneda("001");
		cabDet.setDet_cuenta("1234567");
		cabDet.setDet_referencia("00000002165712");
		cabDet.setDet_monto("125.00");
		cabDet.setDet_monto_efe("100.00");
		cabDet.setDet_monto_doc("25.00");
		
		RegistroMetadata regMeta = new RegistroMetadata("Cabecera");
		regMeta.addCampoDef("1", "Numero Cuenta");
		regMeta.addCampoDef("2", "Depositante");
		regMeta.addCampoDef("3", "Tipo Identificacion");
		regMeta.addCampoDef("4", "Numero Identificacion");
		
		Registro reg = new Registro("Cabecera");
		reg.addCampo("1", "1234567");
		reg.addCampo("2", "Soraya Serna");
		reg.addCampo("3", "C");
		reg.addCampo("4", "1720386935");
		
		
		detTrx = new DetalleTransaccion();
		detTrx.addRegistro(reg);
		detTrx.addRegistroMetadata(regMeta);
		
		
		ContenedorCheques conteCheques = new ContenedorCheques();
		
		detTrx.setCabeceraDetalle(cabDet);
		
		chq.setDescripcion("Cheques Locales");
		chq.setNumero_documentos("1");
		chq.setValor("25.00");
		
		conteCheques.setCheques(chq);
	
		req.setTransaccion(trx);
		req.setDetalleTransaccion(detTrx);
		req.setCheques(conteCheques);
		
		String XML = SerializationObject.ObjectToString(req, TrxRequerimientoAfectacion.class);
		System.out.println(XML);
	}

}
