package com.belejanor.switcher.authorizations;

import com.belejanor.switcher.cscoreswitch.wIso8583;

public interface ITranBIMOAut {

	abstract wIso8583 EnrolamientoBimoAut(wIso8583 iso);
	abstract wIso8583 AdminBimoAut(wIso8583 iso);
	abstract wIso8583 ConsultaMovilCtaBimoAut(wIso8583 iso);
	abstract wIso8583 VerificarUsuarioBimoAut(wIso8583 iso);
	abstract wIso8583 VerificarTelefonoCtaBimoAut(wIso8583 iso);
	abstract wIso8583 RegenerarPasswordBimoAut(wIso8583 iso);
	abstract wIso8583 AdminFinancialBimoAut(wIso8583 iso);
	abstract wIso8583 ListarPagosCobrosBimoAut(wIso8583 iso);
	abstract wIso8583 SolicitudCobroBimoAut(wIso8583 iso);
	abstract wIso8583 RechazoSolicitudCobro(wIso8583 iso);
	abstract wIso8583 ValidacionOTP(wIso8583 iso);
}
