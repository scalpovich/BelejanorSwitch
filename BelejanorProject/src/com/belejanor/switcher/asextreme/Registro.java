package com.belejanor.switcher.asextreme;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Registro implements Serializable{

	private static final long serialVersionUID = 6904021180838690147L;
	/*Atributos Cliente*/
	private String BranchId;
	private String ClientId;
	private String Name;
	private String ClientType;
	private String DocumentId;
	private String DocumentType;
	private String FirstName;
	private String LastName;
	private String Mail;
	private String Phone;
	private String CellPhone;
	/*Atributos Cliente*/
	
	/*Atributos Producto*/
	private String ProductBankId;
	private String ProdNumber;
	private String Status;
	private String ProductTypeId;
	private String ProductAlias;
	private String CanTransact;
	private String CurrencyId;
	/*Atributos Producto*/
	
	/*Atributos Producto Seleccionado*/
	//algunos campos ya estan definidos
	private String Saldo;
	private String CurrencyIdInt;
	private String SaldoInt;
	private String Rate;
	private String ExpirationDate;
	private String CuotasPag;
	private String CuotasTotal;
	private String NextFeeDueDate;
	private String ClientName;
	private String BranchName;
	private String OfficeName;
	private String IdTransaccion;
	/*Atributos Producto Seleccionado*/
	
	/*Atributos 5 Movimientos */
	private String MovId;
	private String AccountId1;
	private String MovDate;
	private String Description;
	private String Amount;
	private String isDebit;
	private String Balance;
	private String MovTypeId;
	private String TypeDesc;
	private String CheckId;
	/*Atributos 5 Movimientos */
	
	/*Atributos Cuotas prestamos */
	private String ValueTrn;
	private String FeeNumber;
	private String PrincipalAmount;
	private String DueDate;
	private String InterestAmount;
	private String OverdueAmount;
	private String FeeStatusId;
	private String TotalAmount;
	/*Atributos Cuotas prestamos */
	

	/*Atributos Pago Cuotas prestamos */
	private String NormalInterestAmount;
	private String OthersAmount;
	private String PaymentDate;
	private String MovType;
	private String OverdueIntAmount;
	/*Atributos Cuotas prestamos */
	
	
	/*Atributos Transferencias Masivas*/
	
	private String TransactionItemId;
	private String SubTransactionTypeId;
	private String TransactionFeatureId;
	private String ValueDate;
	private String TransactionTypeId;
	private String TransactionStatusId;
	private String ClientBankId;
	private String DebitProductBankId;
	private String DebitProductTypeId;
	private String DebitCurrencyId;
	private String CreditProductBankId;
	private String CreditProductTypeId;
	private String CreditCurrencyId;
	private String NotifyTo;
	private String NotificationChannelId;
	private String DestinationDocumentId;
	private String DestinationName;
	private String DestinationBank;
	private String BankRoutingNumber;
	private String SourceName;
	private String SourceBank;
	private String SourceDocumentId;
	private String RegulationAmountExceeded;
	private String SourceFunds;
	private String DestinationFunds;
	private String UserDocumentId;
	private String TransactionCost;
	private String TransactionCostCurrencyId;
	private String ExchangeRate;
	private String IsValid;
	private String ValidationMessage;
	/*Atributos Transferencias Masivas*/
	
	public Registro(){
		
		super();
		
		/*------------Cliente----------------*/
		this.BranchId = null;
		this.ClientId = null;
		this.Name = null;
		this.ClientType = null;
		this.DocumentId = null;
		this.DocumentType = null;
		
		/*---------------Productos-------------*/
		
		this.ProductBankId= null;
		this.ProdNumber = null;
		this.Status = null;
		this.ProductTypeId = null;
		this.ProductAlias = null;
		this.CanTransact = null;
		this.CurrencyId = null;
		
		/*---------------Productos Seleccionados-------------*/
		this.Saldo= null;
		this.CurrencyIdInt= null;
		this.SaldoInt= null;
		this.Rate= null;
		this.ExpirationDate= null;
		this.CuotasPag= null;
		this.CuotasTotal= null;
		this.NextFeeDueDate= null;
		this.ClientName= null;
		this.BranchName= null;
		this.OfficeName= null;
		this.IdTransaccion= null;
		
		/*---------------5 Mov. una Cuenta -------------*/
		
		this.MovId= null;
		this.AccountId1= null;
		this.MovDate= null;
		this.Description= null;
		this.Amount= null;
		this.isDebit= null;
		this.Balance= null;
		this.MovTypeId= null;
		this.TypeDesc= null;
		this.CheckId= null;
		
		/*---------------Cuotas de un prestamo -------------*/
		
		this.ValueTrn= null;
		this.FeeNumber= null;
		this.PrincipalAmount= null;
		this.DueDate= null;
		this.InterestAmount= null;
		this.OverdueAmount= null;
		this.FeeStatusId= null;
		this.TotalAmount = null;
		
		/*---------------Pagos de un prestamo -------------*/
		
		this.NormalInterestAmount= null;
		this.OthersAmount= null;
		this.PaymentDate= null;
		this.MovType = null;
		this.OverdueIntAmount = null;
		
		/*Transferencias Masivas*/
		this.TransactionItemId= null;
		this.SubTransactionTypeId= null;
		this.TransactionFeatureId= null;
		this.ValueDate= null;
		this.TransactionTypeId= null;
		this.TransactionStatusId= null;
		this.ClientBankId= null;
		this.DebitProductBankId= null;
		this.DebitProductTypeId= null;
		this.DebitCurrencyId= null;
		this.CreditProductBankId= null;
		this.CreditProductTypeId= null;
		this.CreditCurrencyId= null;
		this.NotifyTo= null;
		this.NotificationChannelId= null;
		this.DestinationDocumentId= null;
		this.DestinationName= null;
		this.DestinationBank= null;
		this.BankRoutingNumber= null;
		this.SourceName= null;
		this.SourceBank= null;
		this.SourceDocumentId= null;
		this.RegulationAmountExceeded= null;
		this.SourceFunds= null;
		this.DestinationFunds= null;
		this.UserDocumentId= null;
		this.TransactionCost= null;
		this.TransactionCostCurrencyId= null;
		this.ExchangeRate= null;
		this.IsValid= null;
		this.ValidationMessage= null;
	}
	
	public Registro(String ProductBankId, String ProdNumber, String Status
					,String ProductTypeId, String ProductAlias, 
					 String CanTransact, String CurrencyId){
		
		this();
		this.ProductBankId= ProductBankId;
		this.ProdNumber = ProdNumber;
		this.Status = Status;
		this.ProductTypeId = ProductTypeId;
		this.ProductAlias = ProductAlias;
		this.CanTransact = CanTransact;
		this.CurrencyId = CurrencyId;
	}
	
	public Registro(String BranchId, String ClientId, String Name
			, String ClientType, String DocumentId, String DocumentType, 
			 String FirstName, String LastName, String Mail, String Phone, String CellPhone){

		this();
		this.BranchId = BranchId;
		this.ClientId = ClientId;
		this.Name = Name;
		this.ClientType = ClientType;
		this.DocumentId = DocumentId;
		this.DocumentType = DocumentType;
		this.FirstName = FirstName;
		this.LastName = LastName;
		this.Mail = Mail;
		this.Phone = Phone;
		this.CellPhone = CellPhone;
   }

   public Registro(String ClientId, String ProductBankId, String ProductTypeId
				  ,String ProductAlias, String ProdNumber, String CurrencyId,
				  String CurrencyIdInt, String SaldoInt, String ExpirationDate
				  ,String CuotasPag, String CuotasTotal, String NextFeeDueDate
				  ,String ClientName, String BranchName, String CanTransact
				  ,String OfficeName, String IdTransaction, String Saldo, String Rate){
	   
	   this();
	   this.ClientId = ClientId;
	   this.ProductBankId = ProductBankId;
	   this.ProductTypeId = ProductTypeId;
	   this.ProductAlias = ProductAlias;
	   this.ProdNumber = ProdNumber;
	   this.CurrencyId = CurrencyId;
	   this.CurrencyIdInt = CurrencyIdInt;
	   this.SaldoInt = SaldoInt;
	   this.ExpirationDate = ExpirationDate;
	   this.CuotasPag = CuotasPag;
	   this.CuotasTotal = CuotasTotal;
	   this.NextFeeDueDate = NextFeeDueDate;
	   this.ClientName = ClientName;
	   this.BranchName = BranchName;
	   this.CanTransact = CanTransact;
	   this.OfficeName = OfficeName;
	   this.IdTransaccion = IdTransaction;
	   this.Saldo = Saldo;
	   this.Rate = Rate;
   }
   
   public Registro(String MovId, String AccountId1, String MovDate
			  ,String Description, String Amount, String isDebit,
			  String Balance, String MovTypeId, String TypeDesc
			  ,String CheckId){

		this();
		this.MovId = MovId;
		this.AccountId1 = AccountId1;
		this.MovDate = MovDate;
		this.Description = Description;
		this.Amount = Amount;
		this.isDebit = isDebit;
		this.Balance = Balance;
		this.MovTypeId = MovTypeId;
		this.TypeDesc = TypeDesc;
		this.CheckId = CheckId;
		
   }
   
   public Registro(String TransactionItemId
		   ,String SubTransactionTypeId
		   ,String TransactionFeatureId
		   ,String CurrencyId
		   ,String ValueDate
		   ,String TransactionTypeId
		   ,String TransactionStatusId
		   ,String ClientBankId
		   ,String DebitProductBankId
		   ,String DebitProductTypeId
		   ,String DebitCurrencyId
		   ,String CreditProductBankId
		   ,String CreditProductTypeId
		   ,String CreditCurrencyId
		   ,String Amount
		   ,String NotifyTo
		   ,String NotificationChannelId
		   ,String DestDocumentNumber
		   ,String DestinationName
		   ,String DestinationBank
		   ,String Description
		   ,String BankRoutingNumber
		   ,String SourceName
		   ,String SourceBank
		   ,String SourceDocumentId
		   ,String RegulationAmountExceeded
		   ,String SourceFunds
		   ,String DestinationFunds
		   ,String UserDocumentId
		   ,String TransactionCost
		   ,String TransactionCostCurrencyId
		   ,String ExchangeRate
		   ,String IsValid
		   ,String ValidationMessage) {
	   
	   this();
	   
	   this.TransactionItemId = TransactionItemId;     
	   this.SubTransactionTypeId = SubTransactionTypeId;
	   this.TransactionFeatureId = TransactionFeatureId;
	   this.CurrencyId = CurrencyId;
	   this.ValueDate = ValueDate;
	   this.TransactionTypeId = TransactionTypeId;
	   this.TransactionStatusId = TransactionStatusId;
	   this.ClientBankId = ClientBankId;
	   this.DebitProductBankId = DebitProductBankId;
	   this.DebitProductTypeId = DebitProductTypeId;
	   this.DebitCurrencyId = DebitCurrencyId;
	   this.CreditProductBankId = CreditProductBankId;
	   this.CreditProductTypeId = CreditProductTypeId;
	   this.CreditCurrencyId = CreditCurrencyId;
	   this.Amount = Amount;
	   this.NotifyTo = NotifyTo;
	   this.NotificationChannelId = NotificationChannelId;
	   this.DestinationDocumentId = DestDocumentNumber;
	   this.DestinationName = DestinationName;
	   this.DestinationBank = DestinationBank;
	   this.Description = Description;
	   this.BankRoutingNumber = BankRoutingNumber;
	   this.SourceName = SourceName;
	   this.SourceBank = SourceBank;
	   this.SourceDocumentId = SourceDocumentId;
	   this.RegulationAmountExceeded = RegulationAmountExceeded;
	   this.SourceFunds = SourceFunds;
	   this.DestinationFunds = DestinationFunds;
	   this.UserDocumentId = UserDocumentId;
	   this.TransactionCost = TransactionCost;
	   this.TransactionCostCurrencyId = TransactionCostCurrencyId;
	   this.ExchangeRate = ExchangeRate;
	   this.IsValid = IsValid;
	   this.ValidationMessage = ValidationMessage;
   }
   
   public Registro(String ValueTrn, String FeeNumber, String PrincipalAmount
			,String DueDate, String InterestAmount, 
			 String OverdueAmount, String FeeStatusId, String OthersAmount, String TotalAmount){

		this();
		this.ValueTrn= ValueTrn;
		this.FeeNumber = FeeNumber;
		this.PrincipalAmount = PrincipalAmount;
		this.DueDate = DueDate;
		this.InterestAmount = InterestAmount;
		this.OverdueAmount = OverdueAmount;
		this.FeeStatusId = FeeStatusId;
		this.OthersAmount = OthersAmount;
		this.TotalAmount = TotalAmount;
   }
   
   public Registro(String ValueTrn, String FeeNumber, String MovType, String NormalInterestAmount
			,String OthersAmount, String OverdueIntAmount, 
			 String PaymentDate, String PrincipalAmount){

		this();
		this.ValueTrn= ValueTrn;
		this.FeeNumber = FeeNumber;
		this.MovType = MovType;
		this.NormalInterestAmount = NormalInterestAmount;
		this.OthersAmount = OthersAmount;
		this.OverdueIntAmount = OverdueIntAmount;
		this.PaymentDate = PaymentDate;
		this.PrincipalAmount = PrincipalAmount;
  }
  
   public Registro(int pivote, String ValueTrn, String FeeNumber, String MovType, String NormalInterestAmount
			,String OthersAmount, String OverdueIntAmount, 
			 String PaymentDate, String PrincipalAmount, String TotalAmount){

		this();
		this.ValueTrn= ValueTrn;
		this.FeeNumber = FeeNumber;
		this.MovType = MovType;
		this.NormalInterestAmount = NormalInterestAmount;
		this.OthersAmount = OthersAmount;
		this.OverdueIntAmount = OverdueIntAmount;
		this.PaymentDate = PaymentDate;
		this.PrincipalAmount = PrincipalAmount;
		this.TotalAmount = TotalAmount;
   }
   
	/*-------------------------- Cliente ----------------------------*/
	 @XmlAttribute(name="BranchId")
	public String getBranchId() {
		return BranchId;
	}

	public void setBranchId(String branchId) {
		BranchId = branchId;
	}
	 @XmlAttribute(name="ClientId")
	public String getClientId() {
		return ClientId;
	}

	public void setClientId(String clientId) {
		ClientId = clientId;
	}
	 @XmlAttribute(name="Name")
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	 @XmlAttribute(name="ClientType")
	public String getClientType() {
		return ClientType;
	}

	public void setClientType(String clientType) {
		ClientType = clientType;
	}
	 @XmlAttribute(name="DocumentId")
	public String getDocumentId() {
		return DocumentId;
	}

	public void setDocumentId(String documentId) {
		DocumentId = documentId;
	}
	 @XmlAttribute(name="DocumentType")
	public String getDocumentType() {
		return DocumentType;
	}

	public void setDocumentType(String documentType) {
		DocumentType = documentType;
	}
	@XmlAttribute(name="FirstName")
	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	@XmlAttribute(name="LastName")
	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}
	@XmlAttribute(name="Mail")
	public String getMail() {
		return Mail;
	}

	public void setMail(String mail) {
		Mail = mail;
	}
	@XmlAttribute(name="Phone")
	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}
	@XmlAttribute(name="CellPhone")
	public String getCellPhone() {
		return CellPhone;
	}

	public void setCellPhone(String cellPhone) {
		CellPhone = cellPhone;
	}

	/*-------------------------- Productos ----------------------------*/	
	@XmlAttribute(name="ProductBankId")
	public String getProductBankId() {
		return ProductBankId;
	}

	public void setProductBankId(String productBankId) {
		ProductBankId = productBankId;
	}
	@XmlAttribute(name="ProdNumber")
	public String getProdNumber() {
		return ProdNumber;
	}

	public void setProdNumber(String prodNumber) {
		ProdNumber = prodNumber;
	}
	@XmlAttribute(name="StatusId")
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
	@XmlAttribute(name="ProductTypeId")
	public String getProductTypeId() {
		return ProductTypeId;
	}
	
	public void setProductTypeId(String productTypeId) {
		ProductTypeId = productTypeId;
	}
	@XmlAttribute(name="ProductAlias")
	public String getProductAlias() {
		return ProductAlias;
	}

	public void setProductAlias(String productAlias) {
		ProductAlias = productAlias;
	}
	@XmlAttribute(name="CanTransact")
	public String getCanTransact() {
		return CanTransact;
	}

	public void setCanTransact(String canTransact) {
		CanTransact = canTransact;
	}
	@XmlAttribute(name="CurrencyId")
	public String getCurrencyId() {
		return CurrencyId;
	}

	public void setCurrencyId(String currencyId) {
		CurrencyId = currencyId;
	}

	
	/*-------------------------- Productos Seleccionados ----------------------------*/
	
	@XmlAttribute(name="Saldo")
	public String getSaldo() {
		return Saldo;
	}

	public void setSaldo(String saldo) {
		Saldo = saldo;
	}
	@XmlAttribute(name="CurrencyIdInt")
	public String getCurrencyIdInt() {
		return CurrencyIdInt;
	}

	public void setCurrencyIdInt(String currencyIdInt) {
		CurrencyIdInt = currencyIdInt;
	}
	@XmlAttribute(name="SaldoInt")
	public String getSaldoInt() {
		return SaldoInt;
	}

	public void setSaldoInt(String saldoInt) {
		SaldoInt = saldoInt;
	}
	@XmlAttribute(name="Rate")
	public String getRate() {
		return Rate;
	}

	public void setRate(String rate) {
		Rate = rate;
	}
	@XmlAttribute(name="ExpirationDate")
	public String getExpirationDate() {
		return ExpirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		ExpirationDate = expirationDate;
	}
	@XmlAttribute(name="CuotasPag")
	public String getCuotasPag() {
		return CuotasPag;
	}

	public void setCuotasPag(String cuotasPag) {
		CuotasPag = cuotasPag;
	}
	@XmlAttribute(name="CuotasTotal")
	public String getCuotasTotal() {
		return CuotasTotal;
	}

	public void setCuotasTotal(String cuotasTotal) {
		CuotasTotal = cuotasTotal;
	}
	@XmlAttribute(name="NextFeeDueDate")
	public String getNextFeeDueDate() {
		return NextFeeDueDate;
	}

	public void setNextFeeDueDate(String nextFeeDueDate) {
		NextFeeDueDate = nextFeeDueDate;
	}
	@XmlAttribute(name="ClientName")
	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}
	@XmlAttribute(name="BranchName")
	public String getBranchName() {
		return BranchName;
	}

	public void setBranchName(String branchName) {
		BranchName = branchName;
	}
	@XmlAttribute(name="OfficeName")
	public String getOfficeName() {
		return OfficeName;
	}

	public void setOfficeName(String officeName) {
		OfficeName = officeName;
	}
	@XmlAttribute(name="IdTransaccion")
	public String getIdTransaccion() {
		return IdTransaccion;
	}

	public void setIdTransaccion(String idTransaccion) {
		IdTransaccion = idTransaccion;
	}

	/*---------------- 5 Movimientos ------------------------*/
	@XmlAttribute(name="MovId")
	public String getMovId() {
		return MovId;
	}
	
	public void setMovId(String movId) {
		MovId = movId;
	}
	@XmlAttribute(name="AccountId1")
	public String getAccountId1() {
		return AccountId1;
	}

	public void setAccountId1(String accountId1) {
		AccountId1 = accountId1;
	}
	@XmlAttribute(name="MovDate")
	public String getMovDate() {
		return MovDate;
	}

	public void setMovDate(String movDate) {
		MovDate = movDate;
	}
	@XmlAttribute(name="Description")
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
	@XmlAttribute(name="Amount")
	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}
	@XmlAttribute(name="isDebit")
	public String getIsDebit() {
		return isDebit;
	}

	public void setIsDebit(String isDebit) {
		this.isDebit = isDebit;
	}
	@XmlAttribute(name="Balance")
	public String getBalance() {
		return Balance;
	}

	public void setBalance(String balance) {
		Balance = balance;
	}
	@XmlAttribute(name="MovTypeId")
	public String getMovTypeId() {
		return MovTypeId;
	}

	public void setMovTypeId(String movTypeId) {
		MovTypeId = movTypeId;
	}
	@XmlAttribute(name="TypeDesc")
	public String getTypeDesc() {
		return TypeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		TypeDesc = typeDesc;
	}
	@XmlAttribute(name="CheckId")
	public String getCheckId() {
		return CheckId;
	}

	public void setCheckId(String checkId) {
		CheckId = checkId;
	}

	/*--------------------Cuotas Presamos ------------------------------------*/
	@XmlAttribute(name="ValueTrn")
	public String getValueTrn() {
		return ValueTrn;
	}

	public void setValueTrn(String valueTrn) {
		ValueTrn = valueTrn;
	}
	@XmlAttribute(name="FeeNumber")
	public String getFeeNumber() {
		return FeeNumber;
	}

	public void setFeeNumber(String feeNumber) {
		FeeNumber = feeNumber;
	}
	@XmlAttribute(name="PrincipalAmount")
	public String getPrincipalAmount() {
		return PrincipalAmount;
	}

	public void setPrincipalAmount(String principalAmount) {
		PrincipalAmount = principalAmount;
	}
	@XmlAttribute(name="DueDate")
	public String getDueDate() {
		return DueDate;
	}

	public void setDueDate(String dueDate) {
		DueDate = dueDate;
	}
	@XmlAttribute(name="InterestAmount")
	public String getInterestAmount() {
		return InterestAmount;
	}

	public void setInterestAmount(String interestAmount) {
		InterestAmount = interestAmount;
	}
	@XmlAttribute(name="OverdueAmount")
	public String getOverdueAmount() {
		return OverdueAmount;
	}

	public void setOverdueAmount(String overdueAmount) {
		OverdueAmount = overdueAmount;
	}
	@XmlAttribute(name="FeeStatusId")
	public String getFeeStatusId() {
		return FeeStatusId;
	}

	public void setFeeStatusId(String feeStatusId) {
		FeeStatusId = feeStatusId;
	}

	
	/*--------------------Pagos de Presamos ------------------------------------*/
	@XmlAttribute(name="NormalInterestAmount")
	public String getNormalInterestAmount() {
		return NormalInterestAmount;
	}

	public void setNormalInterestAmount(String normalInterestAmount) {
		NormalInterestAmount = normalInterestAmount;
	}
	@XmlAttribute(name="OthersAmount")
	public String getOthersAmount() {
		return OthersAmount;
	}

	public void setOthersAmount(String othersAmount) {
		OthersAmount = othersAmount;
	}
	@XmlAttribute(name="PaymentDate")
	public String getPaymentDate() {
		return PaymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		PaymentDate = paymentDate;
	}
	@XmlAttribute(name="MovType")
	public String getMovType() {
		return MovType;
	}

	public void setMovType(String movType) {
		MovType = movType;
	}
	@XmlAttribute(name="OverdueIntAmount")
	public String getOverdueIntAmount() {
		return OverdueIntAmount;
	}

	public void setOverdueIntAmount(String overdueIntAmount) {
		OverdueIntAmount = overdueIntAmount;
	}
	@XmlAttribute(name="TotalAmount")
	public String getTotalAmount() {
		return TotalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		TotalAmount = totalAmount;
	}
	
	/*Tranferencias Masivas*/
	@XmlAttribute(name="TransactionItemId")
	public String getTransactionItemId() {
		return TransactionItemId;
	}
	public void setTransactionItemId(String transactionItemId) {
		TransactionItemId = transactionItemId;
	}
	@XmlAttribute(name="SubTransactionTypeId")
	public String getSubTransactionTypeId() {
		return SubTransactionTypeId;
	}
	public void setSubTransactionTypeId(String subTransactionTypeId) {
		SubTransactionTypeId = subTransactionTypeId;
	}
	@XmlAttribute(name="TransactionFeatureId")
	public String getTransactionFeatureId() {
		return TransactionFeatureId;
	}
	public void setTransactionFeatureId(String transactionFeatureId) {
		TransactionFeatureId = transactionFeatureId;
	}
	
	@XmlAttribute(name="ValueDate")
	public String getValueDate() {
		return ValueDate;
	}
	public void setValueDate(String valueDate) {
		ValueDate = valueDate;
	}
	@XmlAttribute(name="TransactionTypeId")
	public String getTransactionTypeId() {
		return TransactionTypeId;
	}
	public void setTransactionTypeId(String transactionTypeId) {
		TransactionTypeId = transactionTypeId;
	}
	@XmlAttribute(name="TransactionStatusId")
	public String getTransactionStatusId() {
		return TransactionStatusId;
	}
	public void setTransactionStatusId(String transactionStatusId) {
		TransactionStatusId = transactionStatusId;
	}
	@XmlAttribute(name="ClientBankId")
	public String getClientBankId() {
		return ClientBankId;
	}
	public void setClientBankId(String clientBankId) {
		ClientBankId = clientBankId;
	}
	@XmlAttribute(name="DebitProductBankId")
	public String getDebitProductBankId() {
		return DebitProductBankId;
	}
	public void setDebitProductBankId(String debitProductBankId) {
		DebitProductBankId = debitProductBankId;
	}
	@XmlAttribute(name="DebitProductTypeId")
	public String getDebitProductTypeId() {
		return DebitProductTypeId;
	}
	public void setDebitProductTypeId(String debitProductTypeId) {
		DebitProductTypeId = debitProductTypeId;
	}
	@XmlAttribute(name="DebitCurrencyId")
	public String getDebitCurrencyId() {
		return DebitCurrencyId;
	}
	public void setDebitCurrencyId(String debitCurrencyId) {
		DebitCurrencyId = debitCurrencyId;
	}
	@XmlAttribute(name="CreditProductBankId")
	public String getCreditProductBankId() {
		return CreditProductBankId;
	}
	public void setCreditProductBankId(String creditProductBankId) {
		CreditProductBankId = creditProductBankId;
	}
	@XmlAttribute(name="CreditProductTypeId")
	public String getCreditProductTypeId() {
		return CreditProductTypeId;
	}
	public void setCreditProductTypeId(String creditProductTypeId) {
		CreditProductTypeId = creditProductTypeId;
	}
	@XmlAttribute(name="CreditCurrencyId")
	public String getCreditCurrencyId() {
		return CreditCurrencyId;
	}
	public void setCreditCurrencyId(String creditCurrencyId) {
		CreditCurrencyId = creditCurrencyId;
	}
	
	@XmlAttribute(name="NotifyTo")
	public String getNotifyTo() {
		return NotifyTo;
	}
	public void setNotifyTo(String notifyTo) {
		NotifyTo = notifyTo;
	}
	@XmlAttribute(name="NotificationChannelId")
	public String getNotificationChannelId() {
		return NotificationChannelId;
	}
	public void setNotificationChannelId(String notificationChannelId) {
		NotificationChannelId = notificationChannelId;
	}
	@XmlAttribute(name="DestDocumentNumber")
	public String getDestinationDocumentId() {
		return DestinationDocumentId;
	}
	public void setDestinationDocumentId(String destinationDocumentId) {
		DestinationDocumentId = destinationDocumentId;
	}
	@XmlAttribute(name="DestinationName")
	public String getDestinationName() {
		return DestinationName;
	}
	public void setDestinationName(String destinationName) {
		DestinationName = destinationName;
	}
	@XmlAttribute(name="DestinationBank")
	public String getDestinationBank() {
		return DestinationBank;
	}
	public void setDestinationBank(String destinationBank) {
		DestinationBank = destinationBank;
	}
	
	@XmlAttribute(name="BankRoutingNumber")
	public String getBankRoutingNumber() {
		return BankRoutingNumber;
	}
	public void setBankRoutingNumber(String bankRoutingNumber) {
		BankRoutingNumber = bankRoutingNumber;
	}
	@XmlAttribute(name="SourceName")
	public String getSourceName() {
		return SourceName;
	}
	public void setSourceName(String sourceName) {
		SourceName = sourceName;
	}
	@XmlAttribute(name="SourceBank")
	public String getSourceBank() {
		return SourceBank;
	}
	public void setSourceBank(String sourceBank) {
		SourceBank = sourceBank;
	}
	@XmlAttribute(name="SourceDocumentId")
	public String getSourceDocumentId() {
		return SourceDocumentId;
	}
	public void setSourceDocumentId(String sourceDocumentId) {
		SourceDocumentId = sourceDocumentId;
	}
	@XmlAttribute(name="RegulationAmountExceeded")
	public String getRegulationAmountExceeded() {
		return RegulationAmountExceeded;
	}
	public void setRegulationAmountExceeded(String regulationAmountExceeded) {
		RegulationAmountExceeded = regulationAmountExceeded;
	}
	@XmlAttribute(name="SourceFunds")
	public String getSourceFunds() {
		return SourceFunds;
	}
	public void setSourceFunds(String sourceFunds) {
		SourceFunds = sourceFunds;
	}
	@XmlAttribute(name="DestinationFunds")
	public String getDestinationFunds() {
		return DestinationFunds;
	}
	public void setDestinationFunds(String destinationFunds) {
		DestinationFunds = destinationFunds;
	}
	@XmlAttribute(name="UserDocumentId")
	public String getUserDocumentId() {
		return UserDocumentId;
	}
	public void setUserDocumentId(String userDocumentId) {
		UserDocumentId = userDocumentId;
	}
	@XmlAttribute(name="TransactionCost")
	public String getTransactionCost() {
		return TransactionCost;
	}
	public void setTransactionCost(String transactionCost) {
		TransactionCost = transactionCost;
	}
	@XmlAttribute(name="TransactionCostCurrencyId")
	public String getTransactionCostCurrencyId() {
		return TransactionCostCurrencyId;
	}
	public void setTransactionCostCurrencyId(String transactionCostCurrencyId) {
		TransactionCostCurrencyId = transactionCostCurrencyId;
	}
	@XmlAttribute(name="ExchangeRate")
	public String getExchangeRate() {
		return ExchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		ExchangeRate = exchangeRate;
	}
	@XmlAttribute(name="IsValid")
	public String getIsValid() {
		return IsValid;
	}
	public void setIsValid(String isValid) {
		IsValid = isValid;
	}
	@XmlAttribute(name="ValidationMessage")
	public String getValidationMessage() {
		return ValidationMessage;
	}
	public void setValidationMessage(String validationMessage) {
		ValidationMessage = validationMessage;
	}
	
	
}
