package br.ufes.inf.nemo.datawriter;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

	private static String driverClassPostgreSQL = "org.postgresql.Driver";
	private static String database = "intersep";
	private static String driverURLPostgreSQL = "jdbc:postgresql://localhost:5433/" + database;
	private static String userPostgreSQL = "intersep";
	private static String passwordPostgreSQL = "intersep";
	
	public static Connection con;
	
	// Ao executar, execute apenas o preenchimento das duas tabelas restantes
	public static void main(String[] args) {
		try {
			Class.forName(driverClassPostgreSQL);
			con = DriverManager.getConnection(driverURLPostgreSQL, userPostgreSQL, passwordPostgreSQL);
				/* FillPPE_DB.fillTipificacaoFato();
				FillPPE_DB.fillTipoPessoa();
				FillPPE_DB.fillPessoa();
				FillPPE_DB.fillPessoaFisica();
				FillPPE_DB.fillCPF();
				FillPPE_DB.fillProcedimento();
				FillPPE_DB.fillFeito();
				FillPPE_DB.fillFeitoOrigem();
				FillPPE_DB.fillExecucao();
				FillPPE_DB.fillPenaAplicada();
				FillPPE_DB.fillPessoaFeito();
				FillPPE_DB.fillPeca();
				FillPPE_DB.fillEnvolvido(); */
				FillPPE_DB.fillEnvolvimento();
			con.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
