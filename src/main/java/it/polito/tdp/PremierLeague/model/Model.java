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
		double titolarita=0;
		cerca(parziale, k, titolarita);
		return this.dreamTeam;
	}
	
	private void cerca(List<Player> parziale, int k, double maxTitolarita) {
		//caso terminale
		if(parziale.size()==k) { //ho riempito la squadra
			this.dreamTeam= new ArrayList<>(parziale);
			return; 
		}
		for(Player p: this.grafo.vertexSet()) {
			if((calcolaTitolarita(p)+maxTitolarita)>maxTitolarita) {
				if(!parziale.contains(p) && aggiuntaLecita(p, parziale)) {
					parziale.add(p);
					maxTitolarita+=calcolaTitolarita(p);
					cerca(parziale,k,maxTitolarita);
					parziale.remove(parziale.size()-1);
				}
				
			}
		}
	}
	
	private double calcolaTitolarita(Player p) {
		double titolarita;
		Set<DefaultWeightedEdge> archiUscenti=this.grafo.outgoingEdgesOf(p);
		Set<DefaultWeightedEdge> archiEntranti=this.grafo.incomingEdgesOf(p);
		double pesiUscenti=0;
		double pesiEntranti=0;
		for(DefaultWeightedEdge edge: archiUscenti) {
			pesiUscenti+=this.grafo.getEdgeWeight(edge);
		}
		for(DefaultWeightedEdge edge: archiEntranti) {
			pesiEntranti+=this.grafo.getEdgeWeight(edge);
		}
		titolarita=pesiUscenti-pesiEntranti;
		return titolarita;
	}
	
	private boolean aggiuntaLecita(Player player, List<Player> parziale) {
		for(Player p: parziale) {
			if(!Graphs.successorListOf(this.grafo, p).contains(player))
				return true;
		}
		return true;
		
	}
}
