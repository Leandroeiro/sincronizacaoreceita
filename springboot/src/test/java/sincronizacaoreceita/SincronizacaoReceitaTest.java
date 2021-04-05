package sincronizacaoreceita;

import java.io.File;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class SincronizacaoReceitaTest {
	
	private static final String PATH_ARQUIVO = System.getProperty("user.dir") + "/src/test/resources/contas".replace("/", File.separator);
	
	/*@Autowired
    private MockMvc mockMvc;

    @Autowired
    private SincronizacaoReceita sincronizacaoReceita;*/
    
   /* @BeforeAll
	public void setUp() throws IOException {
    	final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("contas.csv");
    	final MockMultipartFile file = new MockMultipartFile("contas", "contas", "src/test/resources/", inputStream);
    	//final String[] fileTest = file.getBytes();
    	
		//this.mockMvc = MockMvcBuilders.standaloneSetup(sincronizacaoReceita).build();
	}*/
	

	//@Test
	void case1() throws Exception {
		//sincronizacaoReceita.main(args);
		
	}

}
