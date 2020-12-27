package com.belejanor.switcher.struct.servipagos;

import com.belejanor.switcher.utils.SerializationObject;

public class Tester2 {

	public static void main(String[] args) {
		
		String tramaXml = "<RequerimientoAfectacion>\r\n" + 
				"	<Transaccion>\r\n" + 
				"		<tra_codigo_transaccion>126781</tra_codigo_transaccion>\r\n" + 
				"		<tra_serial_transaccion>126781</tra_serial_transaccion>\r\n" + 
				"		<tra_fecha_proceso>2013-08-22</tra_fecha_proceso>\r\n" + 
				"		<tra_hora>15:34:16</tra_hora>\r\n" + 
				"		<age_codigo_agencia>192</age_codigo_agencia>\r\n" + 
				"		<usu_codigo_usuario>2852</usu_codigo_usuario>\r\n" + 
				"		<tra_fecha_contable>2013-08-22</tra_fecha_contable>\r\n" + 
				"		<tra_tipo_transaccion>0001</tra_tipo_transaccion>\r\n" + 
				"		<tra_cat_motivo_reverso>0000</tra_cat_motivo_reverso>\r\n" + 
				"		<tra_monto_total>125.00</tra_monto_total>\r\n" + 
				"		<cad_codigo_canal>1</cad_codigo_canal>\r\n" + 
				"		<tra_pin_trama>0000</tra_pin_trama>\r\n" + 
				"		<ins_fi_number_origen>0914</ins_fi_number_origen>\r\n" + 
				"		<tra_message_type_code>TR</tra_message_type_code>\r\n" + 
				"	</Transaccion>\r\n" + 
				"	<DetalleTransaccion>\r\n" + 
				"		<cabeceraDetalle>\r\n" + 
				"			<det_codigo_detalle>1</det_codigo_detalle>\r\n" + 
				"			<ins_fi_number>1361</ins_fi_number>\r\n" + 
				"			<trb_codigo_traban>1700</trb_codigo_traban>  \r\n" + 
				"			<mot_codigo_motivo>0000</mot_codigo_motivo>\r\n" + 
				"			<mon_codigo_moneda>001</mon_codigo_moneda>\r\n" + 
				"			<det_cuenta>1234567</det_cuenta>\r\n" + 
				"			<det_referencia>00000002165712</det_referencia>\r\n" + 
				"			<det_monto>125.00</det_monto>\r\n" + 
				"			<det_monto_efe>100.00</det_monto_efe>\r\n" + 
				"			<det_monto_doc>25.00</det_monto_doc>\r\n" + 
				"		</cabeceraDetalle>\r\n" + 
				"		<registroMetadata tipo=\"Cabecera\">\r\n" + 
				"			<campoDef id=\"1\" name=\"Numero Cuenta\"/>\r\n" + 
				"			<campoDef id=\"2\" name=\"Depositante\"/>\r\n" + 
				"			<campoDef id=\"3\" name=\"Tipo Identificacion\"/>\r\n" + 
				"			<campoDef id=\"4\" name=\"Numero Identificacion\"/>\r\n" + 
				"		</registroMetadata>\r\n" + 
				"		<registro tipo=\"Cabecera\">\r\n" + 
				"			<campo id=\"1\" valor=\"1234567\"/>\r\n" + 
				"			<campo id=\"2\" valor=\" Soraya Serna\"/>\r\n" + 
				"			<campo id=\"3\" valor=\"C\"/>\r\n" + 
				"			<campo id=\"4\" valor=\"1720386935\"/>\r\n" + 
				"		</registro>\r\n" + 
				"	</DetalleTransaccion>\r\n" + 
				"	<Cheques>\r\n" + 
				"		<cheques Valor=\"25.00\" numero_documentos=\"1\" descripcion=\"Cheques Locales\"/>\r\n" + 
				"	</Cheques>\r\n" + 
				"</RequerimientoAfectacion>";
		
		        TrxRequerimientoAfectacion afec = (TrxRequerimientoAfectacion) SerializationObject.StringToObject(tramaXml, TrxRequerimientoAfectacion.class);
				System.out.println(afec.getCheques().getCheques().getNumero_documentos());

	}

}
