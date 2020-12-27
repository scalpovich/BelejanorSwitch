package com.belejanor.switcher.scheduler;

public enum TypeBatch {

	DIARIO("D"),
    SEMANAL ("S"),
    MENSUAL("M"),
	ANUAL("A");

    @SuppressWarnings("unused")
	private final String name;

    TypeBatch(String s) {
        name = s;
    }
}
