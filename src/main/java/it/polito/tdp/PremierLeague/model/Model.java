package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private PremierLeagueDAO dao;
	private Player best;
	
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
		best = null;
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
		Collections.sort(opponent, (a,b) -> (int)b.getPesoAsMinutiGiocati()-(int)a.getPesoAsMinutiGiocati());
		return opponent;
	}
	
	public SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
	public Player getTopPlayer(){
		return this.best;
	}
	
}
