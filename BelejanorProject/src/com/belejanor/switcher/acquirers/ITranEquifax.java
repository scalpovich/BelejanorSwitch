package com.belejanor.switcher.acquirers;

import com.belejanor.switcher.struct.equifax.ConsultaDatosPersona;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersonaRespuesta;
import com.belejanor.switcher.struct.equifax.Error;

public interface ITranEquifax {

	abstract ConsultaDatosPersonaRespuesta consultarDatosPersona(ConsultaDatosPersona dataResponse) throws Error;
}
