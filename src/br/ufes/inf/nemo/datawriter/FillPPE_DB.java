package br.ufes.inf.nemo.datawriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.ufes.inf.nemo.datareader.Reader;

/**
 * TODO list
 * @author jbatista
 * Alguns crimes de outro tipo se tornam homicídios na peça. Na realidade, crimes podem mudar de tipificação da peça para a pena aplicada.
 * Não há verificação explícita de que procedimento, feito origem e pena aplicada se relacionam corretamente. Implicitamente, essa relação é feita
 * pelo sequenciamento dos IDs.
 * Ainda resta implementar o preenchimento das tabelas envolvido e envolvimento.
 */
public class FillPPE_DB {

	private static int numPessoas = 10000;
	private static int numExecucoes = 0;
	private static int numProcedimentos = 0;
	
	private static FileWriter fw;
	private static File file = new File("ppe_db_content.sql");
	
	private static int raffleIdTipoPessoa() {
		return Utils.raffle(2) + 1;
	}
	
	private static int toHomicide(int crime) {
		// Apenas para simular uma chance de 1 em 30
		if(Utils.raffle(30) == 6) {
			return 1;
		} else {
			return crime;
		}
	}
	
	public static void fillTipoPessoa() {
		// Se a tabela de tipos de pessoa já foi preenchida, não faça nada.
		if(Reader.count("ppe_db.mini_siep_tipo_pessoa") != 0) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query =
			"INSERT INTO ppe_db.mini_siep_tipo_pessoa " +
			"(id_tipo_pessoa, ds_tipo_pessoa, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator() + 
			"(1, 'AdmSistema', 1, NOW())," + System.lineSeparator() +
			"(2, 'Advogado', 1, NOW())," + System.lineSeparator() + 
			"(3, 'Condenado', 1, NOW())," + System.lineSeparator() +  
			"(4, 'Vítima', 1, NOW());";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillPessoa() {
		// Se não há nenhuma pessoa a ser inserida, não faça nada
		if(numPessoas < 1) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = 
			"INSERT INTO ppe_db.mini_siep_pessoa " + 
			"(id_pessoa, id_tipo_pessoa, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		
		int num = Reader.count("ppe_db.mini_siep_pessoa");
		int id = num + 1, idTipoPessoa;
		int qtdPessoas = numPessoas;
		
		// Se não há pessoas no sistema, criar um administrador
		if(num == 0) {
			query += "(1, 1, 1, NOW())";
		} else {
			idTipoPessoa = raffleIdTipoPessoa();
			query += "(" + id + ", " + idTipoPessoa + ", 1, NOW())";
		}
		num++;
		qtdPessoas--;
		
		for(id = num + 1; id <= num + qtdPessoas; id++) {
			idTipoPessoa = raffleIdTipoPessoa();
			query += "," + System.lineSeparator(); 
			query += "(" + id + ", " + idTipoPessoa + ", 1, NOW())";
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillPessoaFisica() {
		// Se não há nenhuma pessoa física a ser inserida, não faça nada
		if(numPessoas < 1) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_pessoa_fisica " +
				"(id_pessoa_fisica, nm_pessoa, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		int num = Reader.count("ppe_db.mini_siep_pessoa_fisica");
		int id = num + 1;
		String fullName = Utils.raffleName();
		query += "(" + id + ", " + fullName + ", 1, NOW())";
		
		for(id = num + 2; id <= num + numPessoas; id++) {
			// O nome da pessoa não precisa ser único, apenas seu CPF
			fullName = Utils.raffleName();
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + fullName + ", 1, NOW())";
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillCPF() {
		// Se não há nenhum CPF a ser inserido, não faça nada
		if(numPessoas < 1) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_cpf " +
				"(id_cpf, id_pessoa_fisica, no_cpf, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		int num = Reader.count("ppe_db.mini_siep_cpf");
		int id = num + 1;
		String cpf;
		List<String> usedCPFs = new ArrayList<>();
		do {
			cpf = Utils.raffleDigits(11);
		} while(Reader.consult("ppe_db.mini_siep_cpf", "no_cpf", cpf));
		query += "(" + id + ", " + id + ", " + cpf + ", 1, NOW())";
		usedCPFs.add(cpf);
		
		for(id = num + 2; id <= num + numPessoas; id++) {
			do {
				cpf = Utils.raffleDigits(11);
			} while(Reader.consult("ppe_db.mini_siep_cpf", "no_cpf", cpf) || usedCPFs.contains(cpf));
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + id + ", " + cpf + ", 1, NOW())";
			usedCPFs.add(cpf);
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Supondo uma relação de um pra um entre condenados e feitos.
	public static void fillFeito() {
		// Se não há nenhum feito a ser inserido, não faça nada
		int num = Reader.count("ppe_db.mini_siep_feito");
		numExecucoes = Reader.count("ppe_db.mini_siep_pessoa", "id_tipo_pessoa=3");
		if(num >= numExecucoes) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_feito " +
				"(id_feito, no_feito, dm_tipo_feito, dm_situac_feito, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		int id = num + 1;
		List<String> usedNumsFeito = new ArrayList<>();
		String numFeito, tipoFeito, situacFeito;
		do {
			numFeito = Utils.raffleDigits(12);
		} while(Reader.consult("ppe_db.mini_siep_feito", "no_feito", numFeito));
		
		tipoFeito = Utils.raffleTipoFeito();
		situacFeito = Utils.raffleSituacFeito();
		query += "(" + id + ", " + numFeito + ", " + tipoFeito + ", " + situacFeito + ", 1, NOW())";
		usedNumsFeito.add(numFeito);
		
		for(id = num + 2; id <= numExecucoes; id++) {
			do {
				numFeito = Utils.raffleDigits(12);
			} while(Reader.consult("ppe_db.mini_siep_feito", "no_feito", numFeito) || usedNumsFeito.contains(numFeito));
			tipoFeito = Utils.raffleTipoFeito();
			situacFeito = Utils.raffleSituacFeito();
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + numFeito + ", " + tipoFeito + ", " + situacFeito + ", 1, NOW())";
			usedNumsFeito.add(numFeito);
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Supondo uma relação de um pra um entre feitos e feitos origem.
	public static void fillFeitoOrigem() {
		// Se não há nenhum feito origem a ser inserido, não faça nada.
		int num = Reader.count("ppe_db.mini_siep_feito_origem");
		if(numProcedimentos == 0) {
			numProcedimentos = Reader.count("ppe_db.deon_procedimento");
		}
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_feito_origem " +
				"(id_feito_origem, id_feito, no_processo_origem, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		int id = num + 1;
		String numProcessoOrigem;
		List<String> codsProcedimento = Reader.getData("ppe_db.deon_procedimento", "codigo");
		numProcessoOrigem = codsProcedimento.get(num);
		query += "(" + id + ", " + id + ", " + numProcessoOrigem + ", 1, NOW())";
		id++;
		
		for(int i = num + 1; i < numProcedimentos; i++) {
			numProcessoOrigem = codsProcedimento.get(i);
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + id + ", " + numProcessoOrigem + ", 1, NOW())";
			id++;
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillExecucao() {
		// Se não há nenhuma execução a ser inserida, não faça nada.
		int num = Reader.count("ppe_db.mini_siep_execucao");
		if(numExecucoes == 0) {
			numExecucoes = Reader.count("ppe_db.mini_siep_pessoa", "id_tipo_pessoa=3");
		}
		if(num >= numExecucoes) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_execucao " +
				"(id_execucao, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator(); 
		int id = num + 1;
		query += "("+ id + ", 1, NOW())";
		
		for(id = num + 2; id <= numExecucoes; id++) {
			query += "," + System.lineSeparator();
			query += "("+ id + ", 1, NOW())";
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillPenaAplicada() {
		// Se não há nenhuma pena aplicada a ser inserida, não faça nada.
		int num = Reader.count("ppe_db.mini_siep_pena_aplicada");
		if(numProcedimentos == 0) {
			numProcedimentos = Reader.count("ppe_db.deon_procedimento");
		}
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_pena_aplicada " +
				"(id_pena_aplicada, id_execucao, dt_inicio, dm_tipo_pena_aplicada, id_usuario_criacao, dt_usuario_criacao, dt_registro) VALUES" + System.lineSeparator();
		int id = num + 1;
		
		List<String> tiposPena = Reader.getData("ppe_db.deon_tipificacao_fato", "codigo");
		
		int index;
		String dtProcedimento, dtInicio, tipoPena;
		List<String> dtsProcedimento = Reader.getData("ppe_db.deon_procedimento", "data");
		dtProcedimento = dtsProcedimento.get(num);
		dtInicio = Utils.laterDatetime(dtProcedimento);
		index = Utils.raffle(tiposPena.size()) - 1;
		tipoPena = tiposPena.get(index);
		query += "(" + id + ", " + id + ", " + dtInicio + ", " + tipoPena + ", 1, NOW(), NOW())";
		id++;
		
		for(int i = num + 1; i < numProcedimentos; i++) {
			dtProcedimento = dtsProcedimento.get(i);
			dtInicio = Utils.laterDatetime(dtProcedimento);
			index = Utils.raffle(tiposPena.size()) - 1;
			tipoPena = tiposPena.get(index);
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + id + ", " + dtInicio + ", " + tipoPena + ", 1, NOW(), NOW())";
			id++;
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Supondo que cada feito foi feito por exatamente uma pessoa
	public static void fillPessoaFeito() {
		// Se todas as pessoas condenadas já foram relacionadas em pessoa feito, não faça nada.
		int num = Reader.count("ppe_db.mini_siep_pessoa_feito");
		if(numExecucoes == 0) {
			numExecucoes = Reader.count("ppe_db.mini_siep_pessoa", "id_tipo_pessoa=3");
		}
		if(num >= numExecucoes) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.mini_siep_pessoa_feito " +
				"(id_pessoa_feito, id_pessoa, id_feito, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator();
		int id = num + 1, idPessoa;
		List<String> idsCondenado = Reader.getData("ppe_db.mini_siep_pessoa", "id_pessoa", "id_tipo_pessoa=3");
		
		// Para cada pessoa condenada ainda não relacionada em pessoa feito.
		String idCondenado = idsCondenado.get(num);
		idCondenado = idCondenado.replaceAll("'", "");
		idPessoa = Integer.parseInt(idCondenado);
		query += "(" + id + ", " + idPessoa + ", " + id + ", 1, NOW())";
		id++;
		
		for(int i = num + 1; i < numExecucoes; i++) {
			idCondenado = idsCondenado.get(i);
			idCondenado = idCondenado.replaceAll("'", "");
			idPessoa = Integer.parseInt(idCondenado);
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + idPessoa + ", " + id + ", 1, NOW())";
			id++;
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillTipificacaoFato() {
		// Se a tabela de tipificação de fatos já foi preenchida, não faça nada.
		if(Reader.count("ppe_db.deon_tipificacao_fato") != 0) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query =
			"INSERT INTO ppe_db.deon_tipificacao_fato " +
			"(id_tipo_pessoa, ds_tipo_pessoa, id_usuario_criacao, dt_usuario_criacao) VALUES" + System.lineSeparator() + 
			"(1, '121', 'Homicídio')," + System.lineSeparator() +
			"(2, '157', 'Roubo')," + System.lineSeparator() + 
			"(3, '148', 'Sequestro')," + System.lineSeparator() +  
			"(4, '155', 'Furto');";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillProcedimento() {
		// Se não há nenhum procedimento a ser inserido, não faça nada.
		int num = Reader.count("ppe_db.deon_procedimento");
		numProcedimentos = Reader.count("ppe_db.mini_siep_pessoa", "id_tipo_pessoa=3");
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.deon_procedimento VALUES" + System.lineSeparator();
		int id = num + 1;
		String[] logradouros = {"Rua", "Avenida", "Alameda"};
		String[] estados = {"ES", "RJ", "MG", "SP"};
		List<String> usedCodigos = new ArrayList<>();
		String codigo, data, endereco, logradouro, estado;
		do {
			codigo = Utils.raffleDigits(8);
		} while(Reader.consult("ppe_db.deon_procedimento", "codigo", codigo));
		data = Utils.raffleDatetime();
		
		// Gerando endereço para o procedimento
		int index = Utils.raffle(logradouros.length) - 1;
		logradouro = logradouros[index];
		
		index = Utils.raffle(estados.length) - 1;
		estado = estados[index];
		
		endereco = "'" + logradouro + " " + Utils.raffle(100) + " - " +  Utils.raffle(1000);
		endereco += ", bairro " + Utils.raffleFirstName() + ", " + Utils.raffleLastName() + ", " + estado + "'";
		// ---------------------------------------------------------------------------------------------------------------
		
		query += "(" + id + ", " + codigo + ", 'Tramitado', 'IP', " + data + ", " + endereco + ")";
		usedCodigos.add(codigo);
		
		for(id = num + 2; id <= numProcedimentos; id++) {
			do {
				codigo = Utils.raffleDigits(8);
			} while(Reader.consult("ppe_db.deon_procedimento", "codigo", codigo) || usedCodigos.contains(codigo));
			data = Utils.raffleDatetime();
			
			// Gerando endereço para o procedimento
			index = Utils.raffle(logradouros.length) - 1;
			logradouro = logradouros[index];
			
			index = Utils.raffle(estados.length) - 1;
			estado = estados[index];
			
			endereco = "'" + logradouro + " " + Utils.raffle(100) + " - " +  Utils.raffle(1000);
			endereco += ", bairro " + Utils.raffleFirstName() + ", " + Utils.raffleLastName() + ", " + estado + "'";
			// ---------------------------------------------------------------------------------------------------------------
			
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + codigo + ", 'Tramitado', 'IP', " + data + ", " + endereco + ")";
			usedCodigos.add(codigo);
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillPeca() {
		// Se não há nenhuma peça a ser inserida, não faça nada.
		int num = Reader.count("ppe_db.deon_peca");
		if(numProcedimentos == 0) {
			numProcedimentos = Reader.count("ppe_db.deon_procedimento");
		}
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.deon_peca VALUES" + System.lineSeparator();
		int id = num + 1, idTipificacao;
		String codigo = Reader.consultVal("ppe_db.deon_procedimento", "id_procedimento", Integer.toString(id), "codigo");
		String idFeito = Reader.consultVal("ppe_db.mini_siep_feito_origem", "no_processo_origem", codigo, "id_feito");
		String tipoPena = Reader.consultVal("ppe_db.mini_siep_pena_aplicada", "id_execucao", idFeito, "dm_tipo_pena_aplicada");
		String info = Reader.consultVal("ppe_db.deon_tipificacao_fato", "codigo", tipoPena, "id_tipificacao_fato");
		info = info.replaceAll("'", "");
		idTipificacao = Integer.parseInt(info);
		idTipificacao = toHomicide(idTipificacao);
		query += "(" + id + ", " + id + ", " + idTipificacao + ")";
		for(id = num + 2; id <= numProcedimentos; id++) {
			codigo = Reader.consultVal("ppe_db.deon_procedimento", "id_procedimento", Integer.toString(id), "codigo");
			idFeito = Reader.consultVal("ppe_db.mini_siep_feito_origem", "no_processo_origem", codigo, "id_feito");
			tipoPena = Reader.consultVal("ppe_db.mini_siep_pena_aplicada", "id_execucao", idFeito, "dm_tipo_pena_aplicada");
			info = Reader.consultVal("ppe_db.deon_tipificacao_fato", "codigo", tipoPena, "id_tipificacao_fato");
			info = info.replaceAll("'", "");
			idTipificacao = Integer.parseInt(info);
			idTipificacao = toHomicide(idTipificacao);
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + id + ", " + idTipificacao + ")";
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillEnvolvido() {
		// Se não há nenhum envolvido a ser inserido, não faça nada.
		int num = Reader.count("ppe_db.deon_envolvido");
		if(numProcedimentos == 0) {
			numProcedimentos = Reader.count("ppe_db.deon_procedimento");
		}
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.deon_envolvido VALUES" + System.lineSeparator();
		int id = num + 1;
		List<String> idsCondenado = Reader.getData("ppe_db.mini_siep_pessoa", "id_pessoa", "id_tipo_pessoa=3");
		String idPessoa = idsCondenado.get(num);
		String cpf = Reader.consultVal("ppe_db.mini_siep_cpf", "id_pessoa_fisica", idPessoa, "no_cpf");
		String nome = Reader.consultVal("ppe_db.mini_siep_pessoa_fisica", "id_pessoa_fisica", idPessoa, "nm_pessoa");
		query += "(" + id + ", " + nome + ", " + cpf + ")";
		id++;
		
		for(int i = num + 1; i < numProcedimentos; i++) {
			idPessoa = idsCondenado.get(i);
			cpf = Reader.consultVal("ppe_db.mini_siep_cpf", "id_pessoa_fisica", idPessoa, "no_cpf");
			nome = Reader.consultVal("ppe_db.mini_siep_pessoa_fisica", "id_pessoa_fisica", idPessoa, "nm_pessoa");
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + nome + ", " + cpf + ")";
			id++;
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fillEnvolvimento() {
		// Se não há nenhum envolvimento a ser inserido, não faça nada.
		int num = Reader.count("ppe_db.deon_envolvimento");
		if(numProcedimentos == 0) {
			numProcedimentos = Reader.count("ppe_db.deon_procedimento");
		}
		if(num >= numProcedimentos) {
			return;
		}
		
		try {
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String query = "INSERT INTO ppe_db.deon_envolvimento VALUES" + System.lineSeparator();
		int id = num + 1;
		String idPeca = "'" + Integer.toString(id) + "'";
		String idProcedimento = Reader.consultVal("ppe_db.deon_peca", "id_peca", idPeca, "id_procedimento");
		String codigo = Reader.consultVal("ppe_db.deon_procedimento", "id_procedimento", idProcedimento, "codigo");
		String idFeito = Reader.consultVal("ppe_db.mini_siep_feito_origem", "no_processo_origem", codigo, "id_feito");
		String idPessoa = Reader.consultVal("ppe_db.mini_siep_pessoa_feito", "id_feito", idFeito, "id_pessoa");
		String cpf = Reader.consultVal("ppe_db.mini_siep_cpf", "id_pessoa_fisica", idPessoa, "no_cpf");
		String info = Reader.consultVal("ppe_db.deon_envolvido", "cpf", cpf, "id_envolvido");
		info = info.replaceAll("'", "");
		int idEnvolvido = Integer.parseInt(info);
		String tipo = "'SUSPEITO'";
		query += "(" + id + ", " + tipo + ", " + id + ", " + idEnvolvido + ")";
		
		// Para cada procedimento. O mais adequado seria para cada peça. Relação de um pra um entre peça e procedimento.
		for(id = num + 2; id <= numProcedimentos; id++) {
			idPeca = "'" + Integer.toString(id) + "'";
			idProcedimento = Reader.consultVal("ppe_db.deon_peca", "id_peca", idPeca, "id_procedimento");
			codigo = Reader.consultVal("ppe_db.deon_procedimento", "id_procedimento", idProcedimento, "codigo");
			idFeito = Reader.consultVal("ppe_db.mini_siep_feito_origem", "no_processo_origem", codigo, "id_feito");
			idPessoa = Reader.consultVal("ppe_db.mini_siep_pessoa_feito", "id_feito", idFeito, "id_pessoa");
			cpf = Reader.consultVal("ppe_db.mini_siep_cpf", "id_pessoa_fisica", idPessoa, "no_cpf");
			info = Reader.consultVal("ppe_db.deon_envolvido", "cpf", cpf, "id_envolvido");
			info = info.replaceAll("'", "");
			idEnvolvido = Integer.parseInt(info);
			query += "," + System.lineSeparator();
			query += "(" + id + ", " + tipo + ", " + id + ", " + idEnvolvido + ")";
		}
		query += ";";
		
		try {
			Statement stmt = Main.con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(query + System.lineSeparator() + System.lineSeparator());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
