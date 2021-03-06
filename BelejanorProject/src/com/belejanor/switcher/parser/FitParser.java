package com.belejanor.switcher.parser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.TrxCf_Cam;
import com.belejanor.switcher.memcached.TrxCf_Dep;
import com.belejanor.switcher.memcached.TrxCf_DepCam;
import com.belejanor.switcher.memcached.TrxCf_Dep_Table;
import com.belejanor.switcher.memcached.TrxCf_GRQ;
import com.belejanor.switcher.memcached.TrxCf_Join_Cri;
import com.belejanor.switcher.memcached.TrxCf_Reg_Ctl;
import com.belejanor.switcher.memcached.TrxCf_Table;
import com.belejanor.switcher.memcached.User_Channel;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.common.exception.FitbankException;
import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.CriterionType;
import com.fitbank.dto.management.Dependence;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.FieldType;
import com.fitbank.dto.management.Join;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.JoinType;

public class FitParser {

	private String errorParser;
	private boolean flagError;
	private String tabNameIni;
	private String tabAliasIni; 
	private String codErrorIso;
	private Logger log;
	
	public FitParser(){
		
		this.errorParser = "";
		this.flagError = false;
		this.tabAliasIni = "$n";
		this.tabNameIni = "$n";
		this.codErrorIso = "000";
		this.log = new Logger();
	}
	
	public String getErrorParser() {
		return errorParser;
	}

	public void setErrorParser(String errorParser) {
		this.errorParser = errorParser;
	}

	public boolean isFlagError() {
		return flagError;
	}

	public void setFlagError(boolean flagError) {
		this.flagError = flagError;
	}
	
	public String getCodErrorIso() {
		return codErrorIso;
	}

	public void setCodErrorIso(String codErrorIso) {
		this.codErrorIso = codErrorIso;
	}

	@SuppressWarnings("deprecation")
	public Detail parseFitbankTransaction(wIso8583 iso){
		
		boolean flagSoloCTL = false;
		Detail detail = new Detail();
		List<TrxCf_Table> lTables = null;
		List<TrxCf_Join_Cri> lCriteJoins = null;
		List<TrxCf_Reg_Ctl> lRegCtl = null;
		List<TrxCf_Cam> lCams = null;
		List<TrxCf_Dep> lDep = null;
		List<TrxCf_DepCam> lDepCam = null;
		List<TrxCf_Dep_Table> lDepTables = null;
 		TrxCf_Table objTables = null;
		TrxCf_Join_Cri objCriteJoins = null;
		TrxCf_Dep objDependences = null;
		TrxCf_Reg_Ctl objRegCtl = null;
		TrxCf_Cam objCam = null;
		TrxCf_DepCam objDependencesCam = null;
		TrxCf_Dep_Table objDependencesTables = null;
		
		try {
								
			List<String> trx = Arrays.asList(iso.getWsTransactionConfig()
					           .getProccodeTransactionFit().split("-"));
			if(trx.size() == 4){
			
				objTables = new TrxCf_Table();
				lTables = objTables.getTrxCf_TableListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
								   .stream()
								   .filter(p -> !(p.getTab_name().equalsIgnoreCase("$n")))
								   .collect(Collectors.toList());
				
				/********************************************* PROCESO TABLES **************************************************/
				if(lTables.size() > 0){
					
					flagSoloCTL = true;
					objCriteJoins = new TrxCf_Join_Cri();
					lCriteJoins = objCriteJoins.getTrxCf_JoinCriListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3));
					int counter = 0;
					
					for (TrxCf_Table iTable : lTables) {
								
						if(counter == 0){
							tabAliasIni = iTable.getTab_alias();
							tabNameIni  = iTable.getTab_name(); 
						}
							
						String tabName = iTable.getTab_name();
						String tabAlias = iTable.getTab_alias();
						Table dTable = new Table(tabName, tabAlias);
						
						if(iTable.getBlq() != null && iTable.getBlq() != "$n")
							dTable.setIBloque(Integer.parseInt(iTable.getBlq()));
						if(iTable.getMpg()!= null && iTable.getMpg() != "$n")
							dTable.setHasMorePages(iTable.getMpg());
						if(iTable.getRact()!= null && iTable.getRact() != "$n")
							dTable.setCurrentRecord(Integer.parseInt(iTable.getRact()));
						else
							dTable.setCurrentRecord(null);
						int aGetter = 0;
						if(iTable.getNpg()!= null && iTable.getNpg() != "$n"){
							
							if(iTable.getNpg().startsWith("com.")){
								String getter = (String) GeneralUtils.getValue(iTable.getNpg(),iso);
						    	aGetter = Integer.parseInt(getter);
						    	aGetter = aGetter == 0 ? 1 : ((aGetter) + Integer.parseInt((String) GeneralUtils.getValue(iTable.getNrg(),iso)))/(Integer.parseInt((String) GeneralUtils.getValue(iTable.getNrg(),iso))); 
						    	dTable.setPageNumber(aGetter);
							}else
								dTable.setPageNumber(Integer.parseInt(iTable.getNpg()));
							
					    }	
					    if(iTable.getNrg()!= null && iTable.getNrg() != "$n"){
					    	
					    	if(iTable.getNrg().startsWith("com.")){
								String getter = (String) GeneralUtils.getValue(iTable.getNrg(),iso);
								int  gte = Integer.parseInt(getter);
								dTable.setRequestedRecords(gte);
							}else
								dTable.setRequestedRecords(Integer.parseInt(iTable.getNrg()));
					    }
					    	
					    if(iTable.getDistinct() != null && iTable.getDistinct() != "$n")
					    	dTable.setDistinct(Boolean.parseBoolean(iTable.getDistinct()));	
					    if(iTable.getFinancial() != null && iTable.getFinancial() != "$n"){
					    	dTable.setFinancial(Boolean.parseBoolean(iTable.getFinancial()));
					    	dTable.setSpecial(true);
					    }
					    if(iTable.getReadonly() != null && iTable.getDistinct() != "$n"){
					    	if(iTable.getReadonly().equalsIgnoreCase("true"))
					    		dTable.setReadonly(true);
					    }
					    
						if(lCriteJoins.size() > 0){
							
							/********************************************* Criterions **************************************************/
							List<TrxCf_Join_Cri> lCriteriaFilters = lCriteJoins.stream().
																	filter(p -> p.getTab_name().equals(tabName)
																     && p.getTab_alias().equals(tabAlias)
																     && !p.getCond().equalsIgnoreCase("**"))
																	.collect(Collectors.toList());

							Criterion cri = null;				
							if(lCriteriaFilters.size() > 0){
								
								for (TrxCf_Join_Cri trxCf_Join_Cri : lCriteriaFilters) {
									//String valueC = null;
									String nameC, aliasC, valueC;
									nameC = aliasC = valueC = StringUtils.Empty();
									/*if(trxCf_Join_Cri.getVal() != null)
										valueC = trxCf_Join_Cri.getVal().trim();*/
									
									if(trxCf_Join_Cri.getVal().equals("**") || trxCf_Join_Cri.getVal().equals("$n"))
										valueC = null;
									else if (!trxCf_Join_Cri.getVal().equals("$e")) {
										valueC = trxCf_Join_Cri.getVal();
									}
									
									if(trxCf_Join_Cri.getJoin_cri_alias().equals("**") || trxCf_Join_Cri.getJoin_cri_alias().equals("$n"))
										aliasC = null;
									else if (!trxCf_Join_Cri.getJoin_cri_alias().equals("$e")) {
										aliasC = trxCf_Join_Cri.getJoin_cri_alias();
									}
									
									trxCf_Join_Cri.setJoin_cri_name(trxCf_Join_Cri.getJoin_cri_name().replace("||","^^"));
									if(trxCf_Join_Cri.getJoin_cri_name().equals("**") || trxCf_Join_Cri.getJoin_cri_name().equals("$n"))
										nameC = null;
									else if (!trxCf_Join_Cri.getJoin_cri_name().equals("$e")) {
										nameC = trxCf_Join_Cri.getJoin_cri_name().replaceAll("[$n|$n1|$n2|$n3|$n4]", StringUtils.Empty()); //Puse recien JCOL 2018-Octu-01
										nameC = nameC.replace("^^", "||");
									}
									cri = new Criterion(aliasC, nameC, GeneralUtils.getValue(valueC, iso));
									
									cri.setCondition(trxCf_Join_Cri.getCond());
									
									if(trxCf_Join_Cri.getTipo() != null){
										if(trxCf_Join_Cri.getTipo().equalsIgnoreCase("FILTER"))
											cri.setType(CriterionType.FILTER);
										if(trxCf_Join_Cri.getTipo().equalsIgnoreCase("JOIN"))
											cri.setType(CriterionType.JOIN);
										if(trxCf_Join_Cri.getTipo().equalsIgnoreCase("NORMAL"))
											cri.setType(CriterionType.NORMAL);
										if(trxCf_Join_Cri.getTipo().equalsIgnoreCase("ORDER"))
											cri.setType(CriterionType.ORDER);
									}
									if(trxCf_Join_Cri.getOrd() != null)
										cri.setOrder(Integer.parseInt(trxCf_Join_Cri.getOrd()));
									
									dTable.addCriterion(cri);
								}
							}
							
							/********************************************* Joins **************************************************/
							List<TrxCf_Join_Cri> lJoinsFilters = lCriteJoins.stream().
																 filter(p -> p.getTab_name().equals(tabName)
																 && p.getTab_alias().equals(tabAlias)
																 && p.getCond().equals("**"))
																 .collect(Collectors.toList());
							Join join = null;
							if(lJoinsFilters.size() > 0){
								
								for (TrxCf_Join_Cri trxCf_Join_Cri : lJoinsFilters) {
									
									String joinName = trxCf_Join_Cri.getJoin_cri_name();
									String joinAlias = trxCf_Join_Cri.getJoin_cri_alias();
									
									join = new Join(joinName, joinAlias);
									if(trxCf_Join_Cri.getTipo() != null){
										if(trxCf_Join_Cri.getTipo().equalsIgnoreCase("LEFT_OUTER_JOIN"))
											join.setType(JoinType.LEFT_OUTER_JOIN);
										else
											join.setType(JoinType.INNER_JOIN);
									}
									
									objDependences = new TrxCf_Dep();
									lDep = objDependences.getTrxCf_DepListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3));
									if(lDep.size() > 0){
										
										List<TrxCf_Dep> lDepsFilters = lDep.stream().
												 					   filter(p -> p.getTab_name().equals(tabName)
												                       && p.getTab_alias().equals(tabAlias)
												                       && p.getJoin_cri_name().equals(joinName)
												                       && p.getJoin_cri_alias().equals(joinAlias))
												 					   .collect(Collectors.toList());
										
											if(lDepsFilters.size() > 0){
												
												for (TrxCf_Dep trxCf_Dep : lDepsFilters) {
													
													String aliasHacia, aliasDesde, desde, hacia;
													aliasHacia = aliasDesde = desde = hacia = "";
													if(trxCf_Dep.getAlias_hacia().startsWith("$n"))
														aliasHacia = null;
													else if(!trxCf_Dep.getAlias_hacia().startsWith("$e"))
														aliasHacia = trxCf_Dep.getAlias_hacia().trim();
														
													if(trxCf_Dep.getAlias_desde().startsWith("$n"))
														aliasDesde = null;
													else if(!trxCf_Dep.getAlias_desde().startsWith("$e"))
														aliasDesde = trxCf_Dep.getAlias_desde().trim();
													
													if(trxCf_Dep.getDesde().startsWith("$n"))
														desde = null;
													else if(!trxCf_Dep.getDesde().startsWith("$e"))
														desde = trxCf_Dep.getDesde().trim();
													
													if(trxCf_Dep.getHacia().startsWith("$n"))
														hacia = null;
													else if(!trxCf_Dep.getHacia().startsWith("$e"))
														hacia = trxCf_Dep.getHacia().trim();
													
													Dependence dep = new Dependence(aliasDesde, desde, aliasHacia, hacia);
													if(trxCf_Dep.getVal() != null)
														dep.setValue(GeneralUtils.getValue(trxCf_Dep.getVal().trim(),iso));
														
													join.addDependence(dep);
												}
												
											}
											
									}/**** fin if dependencias ***/
									dTable.addJoin(join);
									
								}/**** Fin for Joins ***/
								
							}/**** Fin If Joins ****/
							
						}/***fin if joins y criterios***/
						
						/********************************************* Registros **************************************************/
						objRegCtl = new TrxCf_Reg_Ctl();
						lRegCtl = objRegCtl.getTrxCf_RegCtlListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
											.stream()
											.filter(p ->  p.getTab_Name().equals(tabName)
								                       && p.getTab_Alias().equals(tabAlias)
								                       && p.getReg_Nro() != -1)
								 					   .collect(Collectors.toList());
						
						Record reg = null;
						if(lRegCtl.size() > 0){
							
							objCam = new TrxCf_Cam();
							for (TrxCf_Reg_Ctl lt : lRegCtl) {
								
									int nroRegistro = lt.getReg_Nro();
								
									reg = new Record();
									reg.setNumber(nroRegistro);
									
						/********************************************* Campos Registros **************************************************/
									lCams = objCam.getTrxCf_CamListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
											      .stream()
											      .filter(p ->  p.getTab_name().equals(tabName)
									                       &&   p.getTab_alias().equals(tabAlias)
									                       &&   p.getReg_nro() == nroRegistro)
									 					   .collect(Collectors.toList());
									
									if(lCams.size() > 0){
										
										for (TrxCf_Cam itCam : lCams) {
											
											String nameCam = itCam.getCam_name().replaceAll("[$n|$n1|$n2|$n3]", StringUtils.Empty());
											String aliasCam = itCam.getCam_alias();
											String value, valueOld;
											value = valueOld = "";
											if(itCam.getVal() != null)
												value = (String)itCam.getVal();
											if(itCam.getOldval() != null)
												valueOld = (String)itCam.getOldval();
											
											Field cam = new Field(itCam.getCam_alias().replace("**", "").replace("$n", ""), 
																  itCam.getCam_name().replace("**", "").replace("$n", ""), 
													              GeneralUtils.getValue(value, iso));
											if(!StringUtils.IsNullOrEmpty(valueOld)) //Puse recien
												cam.setOldValue(GeneralUtils.getValue(valueOld, iso));
											
											if(itCam.getTipo() != null){
												if(itCam.getTipo().equalsIgnoreCase("INNER_SELECT")){
													cam.setType(FieldType.INNER_SELECT);
												}
												else if(itCam.getTipo().equalsIgnoreCase("AGGREGATE")){
													cam.setType(FieldType.AGGREGATE);
												}
												else if (itCam.getTipo().equalsIgnoreCase("NORMAL")) {
													cam.setType(FieldType.NORMAL);
												}
												else if (itCam.getTipo().equalsIgnoreCase("ID_INNER_SELECT")) {
													cam.setType(FieldType.ID_INNER_SELECT);
												}
											}
											if(!StringUtils.IsNullOrEmpty(itCam.getFunction_name()))
												cam.setFunctionName(itCam.getFunction_name());
											
											
							/*********************************************Dependencias Campos Registros ****************************/
											objDependencesCam = new TrxCf_DepCam();
											lDepCam = objDependencesCam.getTrxCf_DepCamListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
																	   .stream().
								 					                   filter(p -> p.getTab_name().equals(tabName)
								 					                   && p.getTab_alias().equals(tabAlias)
								                                       && p.getCam_alias().equals(aliasCam)
								                                       && p.getCam_name().equals(nameCam)
								                                       && p.getReg_nro() == nroRegistro)
								 					                  .collect(Collectors.toList());;
											
											if(lDepCam.size() > 0){
												
												for (TrxCf_DepCam trxCf_DepCam : lDepCam) {
													
													String aliasDesde, desde, aliasHacia, hacia;
													aliasDesde = desde = aliasHacia = hacia = "";
													
													if(trxCf_DepCam.getAlias_desde() != null){
														if(trxCf_DepCam.getAlias_desde().equals("**") || trxCf_DepCam.getAlias_desde().equals("$n"))
															aliasDesde = null;
														else if (!trxCf_DepCam.getAlias_desde().equals("$e")) 
															aliasDesde = trxCf_DepCam.getAlias_desde();
														else if (!trxCf_DepCam.getAlias_desde().startsWith("$es")) 
															aliasDesde = "";
													}else 
														aliasDesde = null;
													if(trxCf_DepCam.getDesde() != null){									
														if(trxCf_DepCam.getDesde().equals("**") || trxCf_DepCam.getDesde().equals("$n"))
															desde = null;
														else if (!trxCf_DepCam.getDesde().equals("$e")) 
															desde = trxCf_DepCam.getDesde();
													}else
														desde = null;
													if(trxCf_DepCam.getAlias_hacia() != null){	
														if(trxCf_DepCam.getAlias_hacia().equals("**") || trxCf_DepCam.getAlias_hacia().equals("$n"))
															aliasHacia = null;
														else if (!trxCf_DepCam.getAlias_hacia().equals("$e"))
															aliasHacia = trxCf_DepCam.getAlias_hacia();
													}else
														aliasHacia = null;
													if(trxCf_DepCam.getHacia() != null){
														if(trxCf_DepCam.getHacia().equals("**") || trxCf_DepCam.getHacia().equals("$n"))
															hacia = null;
														else if (!trxCf_DepCam.getHacia().equals("$e"))
															hacia = trxCf_DepCam.getHacia();
													}else
														hacia = null;
													//Dependence depCam = new Dependence(trxCf_DepCam.getAlias_desde(), 
													//		trxCf_DepCam.getDesde(), trxCf_DepCam.getAlias_hacia(), trxCf_DepCam.getHacia());
													
													Dependence depCam = new Dependence(aliasDesde, 
																	desde, aliasHacia, hacia);
													
													if(trxCf_DepCam.getVal() != null)
														depCam.setValue(trxCf_DepCam.getVal().trim());
														
													cam.addDependence(depCam);
												}
											}
																			
						    /********************************************* FIN Dependencias Campos Registros *************************/				
											cam.setPk(itCam.getPk());
											reg.addField(cam);
											
										}/*** Fin for Cams ***/
									}/*** Fin If CAMs ***/ 
									
									dTable.addRecord(reg); 
							}

						}/*** fin if Registros y CTls ***/
						
						/******************************************************PROCESO DEPENDENCIAS TABLAS ****************************/
						objDependencesTables = new TrxCf_Dep_Table();
						lDepTables = objDependencesTables.getTrxCf_Cam_Table_ListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
												   .stream().
			 					                   filter(p -> p.getTab_name().equals(tabName)
			 					                   && p.getTab_alias().equals(tabAlias))
			                                       .collect(Collectors.toList());
						if(lDepTables.size() > 0){
							
							for (TrxCf_Dep_Table trxCf_Dep_Table : lDepTables) {
								
								String aliasDesde, desde, aliasHacia, hacia;
								aliasDesde = desde = aliasHacia = hacia = "";
								
								if(trxCf_Dep_Table.getAlias_desde() != null){
									
									if(trxCf_Dep_Table.getAlias_desde().equals("**") || trxCf_Dep_Table.getAlias_desde().equals("$n")){										
										aliasDesde = null;
									}else if (!trxCf_Dep_Table.getAlias_desde().equals("$e")) {
										aliasDesde = trxCf_Dep_Table.getAlias_desde();
									}
								}else 
									aliasDesde = null;
								
								if(trxCf_Dep_Table.getDesde() != null){
									
									if(trxCf_Dep_Table.getDesde().equals("**") || trxCf_Dep_Table.getDesde().equals("$n")){										
										desde = null;
									}else if (!trxCf_Dep_Table.getDesde().equals("$e")) {
										desde = trxCf_Dep_Table.getDesde();
									}
								}else 
									desde = null;
								
								if(trxCf_Dep_Table.getAlias_hacia() != null){
									
									if(trxCf_Dep_Table.getAlias_hacia().equals("**") || trxCf_Dep_Table.getAlias_hacia().equals("$n")){										
										aliasHacia = null;
									}else if (!trxCf_Dep_Table.getAlias_hacia().equals("$e")) {
										aliasHacia = trxCf_Dep_Table.getAlias_hacia();
									}
								}else 
									aliasHacia = null;
								
								if(trxCf_Dep_Table.getHacia() != null){
									
									if(trxCf_Dep_Table.getHacia().equals("**") || trxCf_Dep_Table.getHacia().equals("$n")){										
										hacia = null;
									}else if (!trxCf_Dep_Table.getHacia().equals("$e")) {
										hacia = trxCf_Dep_Table.getHacia();
									}
								}else 
									hacia = null;
								
								
								Dependence depTable = new Dependence(aliasDesde, 
										desde, aliasHacia, hacia);
						
								if(trxCf_Dep_Table.getValue() != null)
									depTable.setValue(trxCf_Dep_Table.getValue().trim());
									
								dTable.addDependence(depTable);
								
							}
							
						}
						
						/******************************************************PROCESO DEPENDENCIAS TABLAS ****************************/
						
						counter++;
						detail.addTable(dTable);
					}/*** fin for tables ***/
					
				}/*** fin if tables ***/
				/********************************************* FIN PROCESO TABLES **************************************************/
				
				/********************************************* Proceso de CTLS **************************************************/
				List<TrxCf_Reg_Ctl> lCtls = null;
				if(!flagSoloCTL){
					
				    objRegCtl = new TrxCf_Reg_Ctl();
					lCtls = objRegCtl.getTrxCf_RegCtlListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
					.stream()
					.filter(p ->  p.getTab_Name().equals("$n")
		                       && p.getTab_Alias().equals("$n")
		                       && p.getReg_Nro() == -1)
		 					   .collect(Collectors.toList());
				}
				else{
					
					lCtls = objRegCtl.getTrxCf_RegCtlListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
							.stream()
							.filter(p ->  p.getTab_Name().equals(this.tabNameIni)
				                       && p.getTab_Alias().equals(this.tabAliasIni)
				                       && p.getReg_Nro() == -1)
				 					   .collect(Collectors.toList());
				}
				
				if(lCtls.size() > 0){
					
					if(objCam == null)
						objCam = new TrxCf_Cam();
					
					lCams = objCam.getTrxCf_CamListObject(trx.get(0), trx.get(1), trx.get(2), trx.get(3))
						      .stream()
						      .filter(p ->  p.getTab_name().equals(this.tabNameIni)
				                       &&   p.getTab_alias().equals(this.tabAliasIni)
				                       &&   p.getReg_nro() == -1)
				 					   .collect(Collectors.toList());
					
					if(lCams.size() > 0){
						
						for (TrxCf_Cam ite : lCams) {
							String value, valueAlias, valueName;
							value = valueAlias = valueName = "";
							if(ite.getVal() != null)
								value = ite.getVal();
							if(ite.getCam_alias().equals("**") || ite.getCam_alias().equals("$n"))
								valueAlias = null;
							else if (!ite.getCam_alias().equals("$e")) {
								valueAlias = ite.getCam_alias();
							}	
							if(ite.getCam_name().equals("**") || ite.getCam_name().equals("$n"))
								valueName = null;
							else if (!ite.getCam_name().equals("$e")) {
								valueName = ite.getCam_name();
							}
							
						    Field fieldCtl = new Field(valueAlias, valueName, GeneralUtils.getValue(value, iso));
						    detail.addField(fieldCtl);
						}
					}
				}
				/********************************************* FIN Proceso de CTLS**************************************************/
				
				/********************************************* Proceso de GRQ**************************************************/
				Ref<Detail> det = new Ref<Detail>(detail);
				detail = setGrq(det, trx, iso);
				/********************************************* Fin Proceso de GRQ**************************************************/
				//Proceso envio DOCUMENTO PARA FIT1
				Config cfg = new Config();
				cfg = cfg.getConfigSystem("FIT_VERSION");
				if(cfg != null){
					
					if(iso.getISO_003_ProcessingCode().startsWith("0") && cfg.getCfg_Valor().equals("1")){

						Field fieldCtl = new Field(null, "DOCUMENTO", detail.getMessageId());
					    detail.addField(fieldCtl);
					}

				}else{
					
					this.flagError = false;
					this.errorParser = "NO EXISTE VERSION DE FITBANK EN TABLA CONFIG";
					return null;
				}
			}
			else{
				
				this.flagError = false;
				this.errorParser = "NO EXISTE LA CONFIGURACION DE LA TRANSACCION(TrxCf) FIT: " + iso.getWsTransactionConfig().getProccodeTransactionFit();
			}
			
		}
		catch (FitbankException ef) {
			this.flagError = false;
			this.errorParser = GeneralUtils.ExceptionToString("ERROR FITBANKEXCEPTION: FitParser::parseFitbankTransaction ",ef, false);
			log.WriteLogMonitor("Error modulo FitParser::parseFitbankTransaction (FitException)", TypeMonitor.error, ef);
		}
		catch (Exception e) {
			this.flagError = false;
			this.errorParser = GeneralUtils.ExceptionToString("ERROR FitParser::parseFitbankTransaction ", e, false);
			log.WriteLogMonitor("Error modulo FitParser::parseFitbankTransaction (General)", TypeMonitor.error, e);
		}
		return detail;
	}
	
	private Detail setGrq(Ref<Detail> detail, List<String> trx, wIso8583 iso){
		
		try {
			
			TrxCf_GRQ grq = new TrxCf_GRQ();
			grq = grq.getDataTrxCf_GRQ(trx.get(0), trx.get(1), trx.get(2), trx.get(3)); 
			
			if(grq != null){
				
				Detail det = detail.get();
				if(grq.getAre() != null && !grq.equals("$n"))
					det.setArea((String)GeneralUtils.getValue(grq.getAre(),iso));
				if(grq.getIpa() != null && !grq.getIpa().equals("$n"))
					det.setIpaddress((String)GeneralUtils.getValue(grq.getIpa(),iso));
				if(grq.getSubsystem_pk() != null && !grq.getSubsystem_pk().equals("$n"))
					det.setSubsystem(grq.getSubsystem_pk());
				if(grq.getTransaction_pk() != null && !grq.getTransaction_pk().equals("$n"))
					det.setTransaction((String)GeneralUtils.getValue(grq.getTransaction_pk(),iso));
				if(grq.getVersion_pk() != null && !grq.getVersion_pk().equals("$n"))
					det.setVersion((String)GeneralUtils.getValue(grq.getVersion_pk(),iso));
				if(grq.getTip_pk() != null && !grq.getTip_pk().equals("$n")){
					if(grq.getTip_pk().startsWith("MANT_") || grq.getTip_pk().startsWith("MAN_")){
						det.setType("MAN");
					}else if (grq.getTip_pk().startsWith("STAND_") || grq.getTip_pk().startsWith("CON_")) {
						det.setType("CON");
					}else {
						det.setType((String)GeneralUtils.getValue(grq.getTip_pk(),iso));
					}
				}
				
				
				if(grq.getTpp() != null && !grq.getTpp().equals("$n"))
					det.setProcessType((String)GeneralUtils.getValue(grq.getTpp(),iso));
				//Validacion de usuarios Terminales /*ATMs, POS, etc*/
				if(iso.getWsTransactionConfig().getValidTerm().equalsIgnoreCase("Y")){
					
					Ref<TrxCf_GRQ> grqValidations = new Ref<TrxCf_GRQ>(grq);
					grq = ValidateUserTerminal(grqValidations, iso);
					if(!this.flagError){
						
						log.WriteLogMonitor("No se ha podido recuperar Terminal Validaciones Trx. Sec: " + iso.getISO_011_SysAuditNumber(), TypeMonitor.error, null);
						return null;
					}
				}	
				if(grq.getUsr() != null && !grq.getUsr().equals("$n"))	
					det.setUser((String)GeneralUtils.getValue(grq.getUsr(),iso));
				if(grq.getIdm() != null && !grq.getIdm().equals("$n"))
					det.setLanguage((String)GeneralUtils.getValue(grq.getIdm(),iso));
				if(grq.getTer() != null && !grq.getTer().equals("$n"))
					det.setTerminal((String)GeneralUtils.getValue(grq.getTer(),iso));
					det.setSessionid(MemoryGlobal.sessionSys);
				if(grq.getRol() != null && !grq.getRol().equals("$n"))
					det.setRole(Integer.parseInt((String)GeneralUtils.getValue(grq.getRol(),iso)));
				if(grq.getNvs() != null && !grq.getNvs().equals("$n"))
					det.setSecuritylevel(Integer.parseInt((String)GeneralUtils.getValue(grq.getNvs(),iso)));
				if(grq.getPwd() != null && !grq.getPwd().equals("$n"))
					det.setPassword((String)GeneralUtils.getValue(grq.getPwd(),iso));
				if(grq.getNpw() != null && !grq.getNpw().equals("$n"))
					det.setNewPassword((String)GeneralUtils.getValue(grq.getNpw(),iso));
				if(grq.getCio() != null && !grq.getCio().equals("$n"))
					det.setCompany(Integer.parseInt((String)GeneralUtils.getValue(grq.getCio(),iso)));
				if(grq.getSuc() != null && !grq.getSuc().equals("$n"))
					det.setOriginBranch(Integer.parseInt((String)GeneralUtils.getValue(grq.getSuc(),iso)));
				if(grq.getOfc() != null && !grq.getOfc().equals("$n"))
					det.setOriginOffice(Integer.parseInt((String)GeneralUtils.getValue(grq.getOfc(),iso)));
				String msgSufijo = "";
				if(iso.getISO_000_Message_Type().startsWith("14"))
					msgSufijo = "_R";
				if(grq.getMsg() != null && !grq.getMsg().equals("$n")){
					if(iso.getISO_000_Message_Type().startsWith("12")){
						
						String authPart = StringUtils.IsNullOrEmpty(iso.getISO_033_FWDInsID())? 
					    		StringUtils.Empty():iso.getISO_033_FWDInsID();
						if(authPart.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC) ||
								authPart.equals(MemoryGlobal.IdBIMOEfi)){
							if(authPart.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC))
								/*Aumento esta linea SPI Receptor*/
								if(iso.getISO_011_SysAuditNumber().length() >= 30) {
									det.setMessageId(iso.getISO_011_SysAuditNumber().substring(0,30));
								}
								else {
									det.setMessageId(iso.getISO_011_SysAuditNumber());
								}
							else
								det.setMessageId(iso.getISO_011_SysAuditNumber());
						}else{
							
							if(iso.getWsTransactionConfig().getProccodeReverFlag() == 0){
								
								if(iso.getISO_011_SysAuditNumber().length() > 11){
									det.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), 
											"yyMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
								}else {
									
									det.setMessageId(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), 
											"yyyyMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber());
								}
							}else {
								
								Calendar calendar = Calendar.getInstance();
								if(iso.getISO_011_SysAuditNumber().length() > 11)
									det.setMessageId(new SimpleDateFormat("yyMMddHHmmss").format(calendar.getTime()) + 
													"_" + (String)GeneralUtils.getValue(grq.getMsg(),iso) + msgSufijo);
								else

									det.setMessageId(new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime()) + 
													"_" + (String)GeneralUtils.getValue(grq.getMsg(),iso) + msgSufijo);
							}
						}
					}else {
						System.out.println("Reverso momento poner.... " + iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd());
						//det.setMessageId(iso.getISO_044_AddRespData() + msgSufijo); //asi estaba
						if(MemoryGlobal.flagSecuenceInReverse)
							det.setMessageId(GeneralUtils.GetSecuencial(16) + msgSufijo);
						else
							det.setMessageId(iso.getWsTransactionConfig().getRevRetrievalIso011fromBdd() + msgSufijo);
					}
				}
				if(grq.getRev() != null && !grq.getRev().equals("$n"))
					det.setReverse((String)GeneralUtils.getValue(grq.getRev(),iso));
				if(grq.getCan() != null && !grq.getCan().equals("$n"))
					det.setChannel((String)GeneralUtils.getValue(grq.getCan(),iso));
				if(grq.getMnr() != null && !grq.getMnr().equals("$n"))
					det.setMessageIdReverse((String)GeneralUtils.getValue(grq.getMnr(),iso));
				  
				/*if(grq.getFcn() != null && !grq.getFcn().equals("$n")){
					
					java.sql.Date sqlDate = java.sql.Date.valueOf( String.valueOf(new Date().getTime()) );
					det.setAccountingDate(sqlDate);
				}*/
			  this.flagError = true;
			  detail.set(det);
			}
			else
				this.setErrorParser("NO SE ENCUENTRA CONFIGURACION GRQ TRX: " + iso.getWsTransactionConfig().getProccodeTransactionFit());
			return detail.get();
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo FitParser::setGrq ", TypeMonitor.error, e);
			this.flagError = false;
			this.setErrorParser(GeneralUtils.ExceptionToString(null,e,false));
			return null;
		}
	}
	
	public Detail parseReversoFitBank(wIso8583 iso){
		
		Detail detail = new Detail();
		try {
			wIso8583 isoRetrieve = new wIso8583();
			IsoRetrievalTransaction retrieve = new IsoRetrievalTransaction();
			isoRetrieve = retrieve.RetrieveTransactionIso(iso, 
					      iso.getWsTransactionConfig().getProccodeReverFlag());
				System.out.println("Campo 44  ----- >  " + isoRetrieve.getISO_044_AddRespData());
				log = new Logger();
				log.WriteLogMonitor("CAMPO 44 ------> " + isoRetrieve.getISO_044_AddRespData() + " --- Respuesta SQL " + isoRetrieve.getISO_039_ResponseCode(), TypeMonitor.monitor, null);
			
			if(iso.getWsISO_SF_Count() > 0)
				isoRetrieve.setISO_039_ResponseCode("000");
			if(isoRetrieve.getISO_039_ResponseCode().equals("000")){
	
				
				
				iso.getWsTransactionConfig().setProccodeReverseFitOriginal(iso.getWsTransactionConfig().getProccodeTransactionFit());
				iso.getWsTransactionConfig().setProccodeTransactionFit(MemoryGlobal.messageReverseUCI);
				if(!iso.getISO_024_NetworkId().equals("555522"))
					iso.getWsTransactionConfig().setRevRetrievalIso011fromBdd(isoRetrieve.getISO_044_AddRespData());
				else{
					//BIMO
					
					if(StringUtils.IsNullOrEmpty(isoRetrieve.getISO_044_AddRespData())){
						log = new Logger();
						log.WriteLogMonitor("NO PUDO RECUERAR REVERSO ORIGINAL BIMO, SE PONE EL ORIGINAL 37", TypeMonitor.monitor, null);
						iso.getWsTransactionConfig().setRevRetrievalIso011fromBdd(iso.getISO_037_RetrievalReferenceNumber());
					}
					else{
						
						iso.getWsTransactionConfig().setRevRetrievalIso011fromBdd(isoRetrieve.getISO_044_AddRespData());
					}
					
				    log = new Logger();
					log.WriteLogMonitor("REVERSO BIMO: " + iso.getISO_037_RetrievalReferenceNumber() , TypeMonitor.monitor, null);
				}
					
				iso.setISO_102_AccountID_1(isoRetrieve.getISO_102_AccountID_1());
				detail = parseFitbankTransaction(iso);
			}
			else{
				this.flagError = false;
				this.codErrorIso = isoRetrieve.getISO_039_ResponseCode();
				this.errorParser = isoRetrieve.getISO_039p_ResponseDetail();
			}
				
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FitParser::parseReversoFitBank ", TypeMonitor.error, e);
			this.flagError = false;
			this.codErrorIso = "909";
			this.errorParser = GeneralUtils.ExceptionToString("ERROR PROCESAMIENTO REVERSO ", e, false);
		}
		return detail;
	}
	
	private TrxCf_GRQ ValidateUserTerminal(Ref<TrxCf_GRQ> grq, wIso8583 iso){
		
		TrxCf_GRQ grq_ = null;
		try {
			
			grq_ = grq.get();
			User_Channel usrch = new User_Channel();
			usrch = usrch.getUserChannel(iso.getISO_018_MerchantType(), 
										 iso.getISO_041_CardAcceptorID());
			if(usrch != null){
				
				grq_.setUsr(usrch.getUser_fit());
				grq_.setSuc(usrch.getUch_sucursal());
				grq_.setOfc(usrch.getUch_oficina());
				
				this.flagError = true;
				
			}else{
				
				this.flagError = false;
				this.codErrorIso = "101";
				this.errorParser = "TERMINAL " + iso.getISO_041_CardAcceptorID() + 
						", CANAL: " + iso.getISO_018_MerchantType() + " NO REGISTRADOS";
			}
			
		} catch (Exception e) {
			
			this.flagError = false;
			this.codErrorIso = "101";
			this.errorParser = GeneralUtils.ExceptionToString(null, e, false);
		}
		grq.set(grq_);
		return grq.get();
	}
	
}
