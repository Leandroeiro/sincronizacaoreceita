/*
Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do Sicredi recebe e organiza as informações de 
contas para enviar ao Banco Central. Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o Sicredi 
já possiu mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, 
antes as 10:00 da manhã na abertura das agências.

Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma 
nova coluna.


Formato CSV:
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
...

*/
package sincronizacaoreceita;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SincronizacaoReceita {
	
    public static void main(String[] args) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    	SpringApplication.run(SincronizacaoReceita.class, args);
    	log.info("Download csv in springboot application started successfully");
    	if(args != null) {
	    	for (String arquivo : args) {
	    		String extensao = arquivo.substring(arquivo.length() - 3);
	    		verificarExtensaoArquivo(extensao);
	    		File file = new File (arquivo);
	    		rodarArquivo(file);			
			}  
    	}
    }

	private static void rodarArquivo(File file) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		InputStream targetStream;
		Reader reader = null;
		List<Conta> contas = new ArrayList<Conta>();			
		CsvUploadService csvUploadService = new CsvUploadService();
		try {
			targetStream = new FileInputStream(file);
			reader = new InputStreamReader(targetStream, StandardCharsets.UTF_8);
		} catch (FileNotFoundException e) {
			log.info("ERRO NA LEITURA DO CSV");
		}
		
		BufferedReader br = new BufferedReader(reader);
		
		String linha = "";
		String csvDivisor = ";";
		try {
			while ((linha = br.readLine()) != null) {

				if (linha.startsWith(";") || linha.equals("") || linha.startsWith("Agencia")) {
					continue;
				}
					
				String[] cliente = linha.split(csvDivisor);
				contas.add(criarConta(cliente));

			}
			enviarRequisicao(contas);
			csvUploadService.load(contas, file.getName());
			finalizarArquivo(br);
			log.info("Fim do Processo!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void enviarRequisicao(List<Conta> contas) {
		ReceitaService receitaService = new ReceitaService();
		for (Conta conta : contas) {
			log.info("Enviando requisicao da conta : " + conta.getConta());
			boolean contaAprovada;
			try {
				contaAprovada = receitaService.atualizarConta(conta.getAgencia(), conta.getConta(), conta.getSaldo(), conta.getStatus());
				conta.setResultado(contaAprovada);
			} catch (RuntimeException | InterruptedException e) {
				log.info("Ocorreu um Erro");
			}
		}
	}

	private static void finalizarArquivo(BufferedReader br) throws IOException {
		br.close();
	}
	
	private static Conta criarConta(String[] cliente) {
		Conta conta = new Conta();
		conta.setAgencia(cliente[0]);
		conta.setConta(cliente[1].replace("-", ""));
		conta.setSaldo(Double.valueOf(cliente[2].replace(",", ".")));
		conta.setStatus(cliente[3]);
		return conta;
	}

	private static void verificarExtensaoArquivo(String extensao) throws IOException {
		if(!extensao.equalsIgnoreCase("csv")) {
			throw new IOException("extensao_invalida");
		}
	}
	
}
