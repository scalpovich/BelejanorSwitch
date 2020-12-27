package com.belejanor.switcher.detail;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Tables {

	private List<Joins> join;
	private List<Criterions> criterions;
	private List<Records> regs;
	private String readonly;
    private String alias;
    private String distinct;
    private String blq;
    private String mpg;
    private String name;
    private String ract;
    private String npg;
    private String nrg;
    private String financial;
    
    public Tables(){
    	
    }
    @XmlElement(name="JOIN")
	public List<Joins> getJoin() {
		return join;
	}
	public void setJoin(List<Joins> join) {
		this.join = join;
	}
	@XmlElement(name="CRI")
	public List<Criterions> getCriterions() {
		return criterions;
	}
	public void setCriterions(List<Criterions> criterions) {
		this.criterions = criterions;
	}
	@XmlElement(name="REG")
	public List<Records> getRegs() {
		return regs;
	}
	public void setRegs(List<Records> regs) {
		this.regs = regs;
	}
	@XmlAttribute
	public String getReadonly() {
		return readonly;
	}
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	@XmlAttribute
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	@XmlAttribute
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}
	@XmlAttribute
	public String getBlq() {
		return blq;
	}
	public void setBlq(String blq) {
		this.blq = blq;
	}
	@XmlAttribute
	public String getMpg() {
		return mpg;
	}
	public void setMpg(String mpg) {
		this.mpg = mpg;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getRact() {
		return ract;
	}
	public void setRact(String ract) {
		this.ract = ract;
	}
	@XmlAttribute
	public String getNpg() {
		return npg;
	}
	public void setNpg(String npg) {
		this.npg = npg;
	}
	@XmlAttribute
	public String getNrg() {
		return nrg;
	}
	public void setNrg(String nrg) {
		this.nrg = nrg;
	}
	@XmlAttribute
	public String getFinancial() {
		return financial;
	}
	public void setFinancial(String financial) {
		this.financial = financial;
	}
        
}
