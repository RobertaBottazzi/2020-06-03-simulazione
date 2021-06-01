package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
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
	
	public List<Player> getTopPlayer(){
		return null;
	}
	
	public SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
}
