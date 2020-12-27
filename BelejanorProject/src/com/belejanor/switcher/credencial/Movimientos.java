package com.belejanor.switcher.credencial;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Movimientos {
		
		private List<Movements> Movimiento;
		@XmlElement(name = "Movimiento")
		public List<Movements> getMovimiento() {
			return Movimiento;
		}
		public void setMovimiento(List<Movements> movimiento) {
			Movimiento = movimiento;
		}
		
}
