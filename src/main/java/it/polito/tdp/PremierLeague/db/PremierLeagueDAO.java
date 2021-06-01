package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadAllPlayers(Map<Integer, Player> idMap) {
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("PlayerID"))){
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(player.getPlayerID(), player);					
				}				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Player> getPlayersXGoals(Map<Integer, Player> idMap, double x) {
		String sql = "SELECT p.PlayerID, p.Name "
				+ "FROM players p, actions a "
				+ "WHERE p.PlayerID=a.PlayerID "
				+ "GROUP BY p.PlayerID, p.Name "
				+ "HAVING AVG(a.Goals)>? ";
		Connection conn = DBConnect.getConnection();
		List<Player> result= new ArrayList<>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(idMap.containsKey(res.getInt("PlayerID"))){
					result.add(idMap.get(res.getInt("PlayerID")));				
				}				
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Arco> getArchi(Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID AS p1, a2.PlayerID AS p2, (SUM(a1.TimePlayed)-SUM(a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID>a2.PlayerID AND a1.TeamID!=a2.TeamID AND a1.`Starts`=a2.`Starts` AND a1.`Starts`=1 "
				+ "		AND a1.MatchID=a2.MatchID "
				+ "GROUP BY a1.PlayerID, a2.PlayerID";
		Connection conn = DBConnect.getConnection();
		List<Arco> result= new ArrayList<>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(idMap.containsKey(res.getInt("p1")) && idMap.containsKey(res.getInt("p2"))) {
					Arco arco= new Arco(idMap.get(res.getInt("p1")),idMap.get(res.getInt("p2")),res.getDouble("peso"));
					result.add(arco);
				}				
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
