package com.belejanor.switcher.asextreme;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="ExtremeMsg")
@XmlType(propOrder={"header", "data","tables"})
public class ExtremeRequest implements Serializable {

	private static final long serialVersionUID = -2519575748369657736L;
	private Header header;
	private Data data;
	private FxTables tables;
	
	public ExtremeRequest(){
		
		this.header = null;
		this.data = null;
		this.tables = null;
	}
	@XmlElement(name="Header")
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}
	@XmlElement(name="Data")
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	@XmlElement(name="FxTables")
	public FxTables getTables() {
		return tables;
	}
	public void setTables(FxTables tables) {
		this.tables = tables;
	}
	
	
}

