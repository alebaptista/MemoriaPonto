package br.gov.pr.celepar.memoriaponto.model;

import java.util.Date;

public class Evento {
	public Date dataHora;
	public String descricao;
	public Evento(Date date, String string) {
		this.dataHora = date;
		this.descricao = string;
	}
	
}
