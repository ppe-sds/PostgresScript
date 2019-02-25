package br.ufes.inf.nemo.datareader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.ufes.inf.nemo.datawriter.Main;

public class Reader {
	public static int count(String table) {
		return count(table, "true");
	}
	
	public static int count(String table, String filter) {
		int result = 0;
		try {
			Statement stmt = Main.con.createStatement();
			String query = "SELECT COUNT(*) FROM " + table + " WHERE " + filter;
			ResultSet results = stmt.executeQuery(query);
			String tam;
			
			if (results.next()) {	
				tam = results.getString("COUNT");	
				result = Integer.parseInt(tam);
			}
		} catch(SQLException e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean consult(String table, String column, String value) {
		boolean result = false;
		try {
			Statement stmt = Main.con.createStatement();
			String query = "SELECT * FROM " + table + " WHERE " + column + "=" + value;
			ResultSet results = stmt.executeQuery(query);
			
			result = results.next();
		} catch(SQLException e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> getData(String table, String column) {
		return getData(table, column, "true");
	}
	
	public static List<String> getData(String table, String column, String filter) {
		List<String> data = new ArrayList<>();
		try {
			Statement stmt = Main.con.createStatement();
			String query = "SELECT " + column + " FROM " + table + " WHERE " + filter;
			ResultSet results = stmt.executeQuery(query);
			
			while(results.next()) {
				data.add("'" + results.getString(1) + "'");
			}
		} catch(SQLException e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return data;
	}
	
	public static String consultVal(String table, String column, String value, String respColumn) {
		String response = "";
		try {
			Statement stmt = Main.con.createStatement();
			String query = "SELECT * FROM " + table + " WHERE " + column + "=" + value;
			ResultSet results = stmt.executeQuery(query);
			
			if(results.next()) {
				response = "'" + results.getString(respColumn) + "'";
			}
		} catch(SQLException e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}
}
