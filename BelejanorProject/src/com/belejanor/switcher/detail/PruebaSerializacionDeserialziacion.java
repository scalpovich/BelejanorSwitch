package com.belejanor.switcher.detail;

import java.util.Arrays;

import com.belejanor.switcher.utils.SerializationObject;

public class PruebaSerializacionDeserialziacion {

	public static void main(String[] args) {
		
		FitBank fit = new FitBank();
		
		GeneralRequestClass grq = new GeneralRequestClass();
		grq.setTpp("Legacy");
		grq.setUsr("ADMIN");
		grq.setIdm("ES");
		grq.setTer("TER_MID");
		grq.setSid("71A6E841C3921E42B477502F9DFED527");
		grq.setRol("1");
		grq.setNvs("10");
		grq.setIpa("10.1.1.9");
		grq.setTip("CON");
		grq.setSub("05");
		grq.setTrn("4015");
		grq.setVer("01");
		grq.setAre("00");
		grq.setCio("2");
		grq.setSuc("100");
		grq.setOfc("100");
		grq.setMsg("20160330175306_0414609");
		grq.setRev("0");
		grq.setCan("MID");
		grq.setFcn("2016-03-30");
							
		
		Criterions[] cri = new Criterions[1];
		cri[0] = new Criterions();
		cri[0].setAlias("tcuenta1");
		cri[0].setCond("=");
		cri[0].setName("CCUENTA");
		cri[0].setVal("1000365021");
		
		
		Fields[] cam = new Fields[4];
		cam[0] = new Fields();
		cam[0].setAlias("tcuenta1");
		cam[0].setName("CCUENTA");
		cam[0].setPk("0");		
		
		cam[1] = new Fields();
		cam[1].setAlias("tcuenta1");
		cam[1].setName("FAPERTURA");
		cam[1].setPk("0");
		
		cam[2] = new Fields();
		cam[2].setAlias("tcuenta1");
		cam[2].setName("CCONDICIONOPERATIVA");
		cam[2].setPk("0");
		
		cam[3] = new Fields();
		cam[3].setTipo("INNER_SELECT");
		cam[3].setAlias("tcuenta1");
		cam[3].setName("TPRODUCTO+SIGLAS");
		cam[3].setPk("0");
		
		Records[] reg = new Records[1];
		reg[0] = new Records();
		reg[0].setNumero("0");
		reg[0].setField(Arrays.asList(cam));
				
		
		Fields[] camCtl = new Fields[3];
		camCtl[0] = new Fields();		
		camCtl[0].setName("FECHA");
		camCtl[0].setPk("0");
		camCtl[0].setVal("2016-03-30");
		
		camCtl[1] = new Fields();		
		camCtl[1].setName("COMPANIA");
		camCtl[1].setPk("0");
		camCtl[1].setVal("2");
		
		camCtl[2] = new Fields();		
		camCtl[2].setName("SSUBCUENTA");
		camCtl[2].setPk("0");
		camCtl[2].setVal("0");
		
		ControlField[] ctl = new ControlField[1];
		ctl[0] = new ControlField();
		ctl[0].setFields(Arrays.asList(camCtl));
		
		Tables[] table = new Tables[1];
		table[0] = new Tables();
		table[0].setAlias("tcuenta1");
		table[0].setBlq("0");
		table[0].setMpg("0");
		table[0].setName("TCUENTA");
		table[0].setRact("0");
		table[0].setNpg("1");
		table[0].setNrg("1");
		table[0].setCriterions(Arrays.asList(cri));
		table[0].setRegs(Arrays.asList(reg));
		
		Details det = new Details();
		det.setTables(Arrays.asList(table));
		det.setControlFields(Arrays.asList(ctl));
		
		fit.setDet(det);
		fit.setGrq(grq);
		
		String XML = SerializationObject.ObjectToString(fit);
		System.out.println(XML);															
		
	}

}
