package it.polito.tdp.PremierLeague.model;

public class Opponents{
	
	private Player player;
	private double pesoAsMinutiGiocati;
	private Player topPlayer;
	
	public Opponents(Player player, double pesoAsMinutiGiocati) {
		this.player = player;
		this.pesoAsMinutiGiocati = pesoAsMinutiGiocati;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public double getPesoAsMinutiGiocati() {
		return pesoAsMinutiGiocati;
	}

	public void setPesoAsMinutiGiocati(double pesoAsMinutiGiocati) {
		this.pesoAsMinutiGiocati = pesoAsMinutiGiocati;
	}
		
	public Player getTopPlayer() {
		return topPlayer;
	}

	public void setTopPlayer(Player topPlayer) {
		this.topPlayer = topPlayer;
	}

	@Override
	public String toString() {
		return player+" "+"|"+" "+pesoAsMinutiGiocati;
	}


	
	

}
