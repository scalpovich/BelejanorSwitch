package com.belejanor.switcher.detail;

import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;




public class PruebasObjetcDto {

	public static void main(String[] args) throws Exception{
		
		
		
		Detail grq = new Detail();
		grq.setArea("01");
		grq.setIpaddress("127.0.0.1");
		
		Table table = new Table("FINANCIERO", "FINANCIERO");
		
		
		Criterion cri = new Criterion("Tblad");
		cri.setAlias("Tblas");
		cri.setCondition("=");
		cri.setName("ccuenta");
		cri.setValue("87272772");
		
		
		
		Record reg = new Record();
		reg.setNumber(1);
		
		Field fld = new Field("FINANCIERO","CODIGO","15");
		//fld.setAlias("FINANCIERO");
		//fld.setName("CODIGO");
		fld.setPk("1");
		
		reg.addField(fld);
		
		fld = new Field("cuenta2");
		fld.setAlias("FINANCIERO2");
		fld.setName("CODIGO2");
		fld.setPk("2");
		
		reg.addField(fld);
		
		table.addRecord(reg);
		
		
		
		
		Field ctl = new Field("","CONTROL_MID","21472732D328DBAAF11FCFD19C64685D51A622F3");
		ctl.setPk("");
		
		grq.addField(ctl);
		grq.addTable(table);
		
		
		
		String xml = grq.toXml();
		
		
		System.out.println(xml);
		
		//UCIClient.send(arg0, host, port, timeout)
		
		
		
	}
}
