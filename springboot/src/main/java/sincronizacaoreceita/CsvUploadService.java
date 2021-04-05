package sincronizacaoreceita;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.extern.slf4j.Slf4j;
 
//lombok annotation
@Slf4j
//spring annotation
@Service
public class CsvUploadService {
 
    private static final String[] HEADERS = {"agencia","conta","saldo","status", "resultado"};
    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader(HEADERS);
 
    public void load(final List<Conta> contas, String nomeArquivo) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
        montarArquivoCsv(contas, nomeArquivo);
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void montarArquivoCsv(final List<Conta> contas, String nomeArquivo) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        log.info("Montando o arquivo CSV.");

        Writer writer = Files.newBufferedWriter(Paths.get(nomeArquivo));
        StatefulBeanToCsv<Conta> beanToCsv = new StatefulBeanToCsvBuilder(writer).build();

        beanToCsv.write(contas);

        writer.flush();
        writer.close();
    }
    
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";")) + "\n";
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
