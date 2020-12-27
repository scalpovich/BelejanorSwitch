package com.belejanor.switcher.acquirers;

import com.belejanor.switcher.struct.equifax.ErrorRegister;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.struct.equifax.ResponseRegisterDataEquifax;

public interface IRegisterEquifax {

	abstract ResponseRegisterDataEquifax insertarEvaluacion(RegisterData dataIn) throws ErrorRegister;
}
