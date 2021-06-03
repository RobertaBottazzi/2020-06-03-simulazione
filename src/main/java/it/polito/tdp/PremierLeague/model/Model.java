package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private PremierLeagueDAO dao;
	private List<Player> dreamTeam;
	private double maxTitolarita;
	
	public Model() {
		idMap=new HashMap<>();
		dao= new PremierLeagueDAO();
		dao.loadAllPlayers(idMap);
	}
	
	public void creaGrafo(double x) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, dao.getPlayersXGoals(idMap, x));
		for(Arco a: dao.getArchi(idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if(a.getPeso()>0) 
					Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				if(a.getPeso()<0)
					Graphs.addEdge(this.grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
			}
		}
	}
	
	public List<Opponents> getOpponentsTopPlayer(){
		int maxDelta=Integer.MIN_VALUE;
		Player best = null;
		List<Opponents> opponent= new ArrayList<>();
		for(Player p: this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p)>maxDelta) {
				maxDelta=this.grafo.degreeOf(p);
				best=p;
			}
		}
		for(DefaultWeightedEdge edge: this.grafo.outgoingEdgesOf(best)) {
			Opponents opponents =new Opponents(Graphs.getOppositeVertex(this.grafo, edge, best), this.grafo.getEdgeWeight(edge));
			opponents.setTopPlayer(best);
			opponent.add(opponents);
		}
		Collections.sort(opponent, (a,b) -> Double.compare(b.getPesoAsMinutiGiocati(), a.getPesoAsMinutiGiocati())/*(int)b.getPesoAsMinutiGiocati()-(int)a.getPesoAsMinutiGiocati()*/);
		return opponent;
	}
	
	public SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
	/*Il grado di titolarità di ogni singolo giocatore, in particolare, è dato dalla differenza del peso 
	 *dei suoi archi uscenti (i minuti che ha giocato in più dei suoi avversari) con il peso degli archi
	 *entranti (i minuti che ha giocato in meno).*/
	public List<Player> getDreamTeam(int k){
		List<Player> parziale= new ArrayList<>();
		this.dreamTeam=null;
		this.maxTitolarita=0;
		cerca(parziale, k);
		return this.dreamTeam;
	}
	
	private void cerca(List<Player> parziale, int k) {
		//caso terminale
		if(parziale.size()==k && calcolaTitolarita(parziale)>this.maxTitolarita) { //ho riempito la squadra
			this.dreamTeam= new ArrayList<>(parziale);
			this.maxTitolarita=calcolaTitolarita(dreamTeam);
			return; 
		}
		for(Player p: this.grafo.vertexSet()) {
			if(!parziale.contains(p) && aggiuntaLecita(p, parziale)) {
				parziale.add(p);
				cerca(parziale,k);
				parziale.remove(p);
			} 
		}
	}
	
	public double calcolaTitolarita(List<Player> parziale) {
		double titolarita;
		double pesiUscenti=0;
		double pesiEntranti=0;
		for(Player p: parziale) {
			Set<DefaultWeightedEdge> archiUscenti=this.grafo.outgoingEdgesOf(p);
			Set<DefaultWeightedEdge> archiEntranti=this.grafo.incomingEdgesOf(p);		
			for(DefaultWeightedEdge edge: archiUscenti) {
				pesiUscenti+=this.grafo.getEdgeWeight(edge);
			}
			for(DefaultWeightedEdge edge: archiEntranti) {
				pesiEntranti+=this.grafo.getEdgeWeight(edge);
			}
		}
		titolarita=pesiUscenti-pesiEntranti;
		return titolarita;
	}
	
	private boolean aggiuntaLecita(Player player, List<Player> parziale) {
		if(parziale.size()==0)
			return true;
		for(Player p: parziale) {
			if(!Graphs.successorListOf(this.grafo, p).contains(player))
				return true;
		}
		return false;
		
	}
}
