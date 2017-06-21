package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	public List<Driver> getDriversForSeason(Season s){
		String sql= "SELECT DISTINCT d.* "+
					"FROM races as r, results as res, drivers as d "+
					"WHERE r.year= ? and r.raceId=res.raceId and res.position is not null "+
					"and res.driverId=d.driverId ";
	
	Connection conn=DBConnect.getConnection();
	try{
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, s.getYear().getValue());
		
		ResultSet rs= st.executeQuery();
		List<Driver> result = new ArrayList<>();
		
		while(rs.next()){
			Driver d = new Driver(rs.getInt("driverId"),
					rs.getString("driverref"),
					rs.getInt("number"),
					rs.getString("code"),
					rs.getString("forename"),
					rs.getString("surname"),
					rs.getDate("dob").toLocalDate(),
					rs.getString("nationality"),
					rs.getString("url"));
		
			result.add(d);
		}
		
		
		conn.close();
		return result;
		
	}catch (SQLException e){
		e.printStackTrace();	
	}
	return null;
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}
	
	public Integer contaVittorie(Driver d1, Driver d2, Season s){
		
		String sql 	= 	"SELECT count(r1.raceId) as cnt "+
						"FROM results as r1, results as r2, races "+
						"WHERE r1.raceId=r2.raceId "+
						"AND races.year = ? "+
						"AND races.raceId=r1.raceId "+
						"AND r1.position < r2.position "+ //il minore stretto evita che prendo anche lo stesso pilota
						"AND r1.driverId= ? "+
						"AND r2.driverId= ?";

		/*	oppure uso una query più complessa e mi evito il doppio loop per creare gli archi!
		 * mi faccio ritornare una lista di oggetti nuovi			
		 * 				SELECT count(r1.raceId) as cnt, r1.driverId as d1, r2.driverId as d2
						FROM results as r1, results as r2, races 
						WHERE r1.raceId=r2.raceId 
						AND races.year = 2005 
						AND races.raceId=r1.raceId 
						AND r1.position < r2.position 
						GROUP BY d1, d2
						
		 la group by fa in modo di raggruppare le races per driver in modo che ottengo
		 il numero di vittorie di d1 su d2
		 */
		
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear().getValue());
			st.setInt(2, d1.getDriverId());
			st.setInt(3, d2.getDriverId());
			
			ResultSet res = st.executeQuery();

			res.next();
			Integer result = res.getInt("cnt");

			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
