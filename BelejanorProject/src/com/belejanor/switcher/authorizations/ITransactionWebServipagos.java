package com.belejanor.switcher.authorizations;

import com.belejanor.switcher.struct.servipagos.Error;

public interface ITransactionWebServipagos {

	abstract String ProcesaTransacciones(String xml) throws Error;
}
