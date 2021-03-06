package br.usjt.aepn2012.cardroiduino.ui;

import br.usjt.aepn2012.cardroiduino.R;
import br.usjt.aepn2012.cardroiduino.carcontrol.DatagramSocketClientGate;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Tela do M�dulo Controle Remoto do Sistema -
 * Tela que Ser� executada no Dispositivo que servir� de Controle Remoto do Carrinho
 * Possui:
 * 	- Conex�o Socket Client Para Recebimento e Envio de Dados ao Server (Dispositivo no Carrinho)
 * 	- Interface com o Aceler�metro para Captar os Movimentos do Dispositivo 
 * 	- Captura e exibi��o do dado de Video enviado pelo Server atrav�s da Conex�o UDP
 * </p>
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/01/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class CarControlActivity extends Activity implements SensorEventListener {
	
	/**
	 * Endere�o IP do Server - Dispositivo no Carrinho
	 */
	private String serverIPAddress;
	
	/**
	 * Porta disponibilizada pelo Server para recebimento dos dados de Comando
	 * Mesmo numero de porta disponibilizado pelo Client para recebimento dos Frames
	 */
	private String clientServerPort;
	
	/**
	 * Surperficie para exibi��o da imagem (Preview) da c�mera
	 */
	private FrameView surfaceView;
	
	/**
	 * Core do Sistema para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Gate que inicializa as Threads Trabalhadoras de Envio e Recebimento de Dados
	 */
	private DatagramSocketClientGate socketClientGate;
	
	/**
	 * Gerenciador de Sensores - Para registrar o Listener do 
	 * Sensor do Aceler�metro
	 */
	private SensorManager sensorManager;
	
	/**
	 * Objeto do Sensor Aceler�metro
	 */
	private Sensor accelerometer;
	
	/**
	 * Controle para Amostrar os dados do aceler�metro 
	 * A frequencia de leitura do aceler�metro � muito alta
	 * precisa-se controlar o numero de amostras ignorando 
	 * algumas
	 */
	//private int accelerAmostrasIgnoradas = 0;
	
	/**
	 * Marcha do Carrinho - Para Controlar a Marcha em que ele est�
	 */
	private int controleMarcha = 0;
	
	/**
	 * Flag para indicar que o modo Free Navigation (usando s�mente aceler�metros) est� ativado
	 */
	private boolean modoFreeNavigation = false;
	
	/**
	 * Flag que vai para true quando o m�do Free Navigation � ativado indicando
	 * que os limites devem ser calculados (Pontos obtidos no evento do aceler�metro)
	 */
	private boolean freeNavigationAdquirirLimites = false;
	
	/**
	 * Ponto de Inicio da R� do Automodelo
	 */
	private float freeNavigationPontoRe = 0;
	
	/**
	 * Ponto de Inicio do movimento de ir para Frente do Automodelo
	 */
	private float freeNavigationPontoFrente = 0;
	
	/**
	 * Ponto de Inicio da Marcha 2 e final da Marcha 1 do Automodelo
	 */
	//private float freeNavigationPontoM2 = 0;
	
	/**
	 * Ponto de Inicio da Marcha 3 e final da Marcha 2 do Automodelo
	 */
	//private float freeNavigationPontoM3 = 0;
	
	/**
	 * Valor do 1� acumulado (somado) lido do Eixo Y para calculo de controle direcional do Automodelo
	 */
	private float acumuladoLidoEixoY_1 = 0;
	
	/**
	 * N�mero de leituras acumuladas em acumuladoLidoEixoY_1
	 */
	private float contaLidoEixoY_1 = 0;
	
	/**
	 * Valor do 2� acumulado (somado) lido do Eixo Y para calculo de controle direcional do Automodelo
	 */
	private float acumuladoLidoEixoY_2 = 0;
	
	/**
	 * N�mero de leituras acumuladas em acumuladoLidoEixoY_2
	 */
	private float contaLidoEixoY_2 = 0;
	
	/**
	 * Valor do eixo Y calculado para o controle
	 */
	private float valorCalculadoEixoY = 0;
	
	/**
	 * Valor do 1� acumulado (somado) lido do Eixo Z para calculo de controle direcional do Automodelo
	 */
	private float acumuladoLidoEixoZ_1 = 0;
	
	/**
	 * N�mero de leituras acumuladas em acumuladoLidoEixoZ_1
	 */
	private float contaLidoEixoZ_1 = 0;
	
	/**
	 * Valor do 2� acumulado (somado) lido do Eixo Z para calculo de controle direcional do Automodelo
	 */
	private float acumuladoLidoEixoZ_2 = 0;
	
	/**
	 * N�mero de leituras acumuladas em acumuladoLidoEixoZ_2
	 */
	private float contaLidoEixoZ_2 = 0;
	
	/**
	 * Valor do eixo Z calculado para o controle
	 */
	private float valorCalculadoEixoZ = 0;
	
	/**
	 * Vari�vel do modo Free Navigation - Automodelo supostamente indo pra frente
	 */
	private boolean freeNavigationFrente = false;
	
	/**
	 * Vari�vel do modo Free Navigaton - Automodelo supostamente indo pra tr�s
	 */
	private boolean freeNavigationRe = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//*************************************************
    	// Executa primeiro o m�todo da classe herdada (classe Superior - extends)
    	super.onCreate(savedInstanceState);
        
    	//*************************************************
        // Setando qual o Layout (XML) associado a essa Activity --> CarDroiDuino\res\layout
        setContentView(R.layout.control_layout);
        
        //*************************************************
        // Obtendo o Endere�o IP e a Porta enviados pela Classe CarDroiDuinoActivity 
        // para conectar ao Server
        this.serverIPAddress = getIntent().getExtras().getString(SystemProperties.KEY_IP_ADDRESS);
        this.clientServerPort = getIntent().getExtras().getString(SystemProperties.KEY_PORT_NUMBER);
        //*************************************************
        
        //*************************************************
        // Inicializando a Surface para Mostrar os Frames recebidos via
        // TCP/IP do Server
        this.surfaceView = (FrameView) findViewById(R.id.surfaceControl); //(SurfaceView)findViewById(R.id.surfaceControl);  
        //*************************************************
        
        //*************************************************
        // Efetua o Setup do Client - Criacao das Threads
        this.setupClient();
        
        //*************************************************
        // Inicializando o aceler�metro para coleta das
        // dire��es do Carrinho
        this.setupAccelerometer();
        
        //*************************************************
        // Inicializando os Bot�es e itens para Controle do Carrinho
        this.setupControles();
        
        //*************************************************
        //Envia comandos para colocar o automodelo na posi��o inicial
        this.condicaoInicial();
    }
    
    /**
     * Inicializa o Gate que ir� executar e reter as threads trabalhadoras de 
     * envio de comandos e recebimento de Frames 
     */
    private void setupClient(){
    	//*************************************************
    	// Inicializa o Core do Sistema para preparar as Filas para a troca de dados entre as Threads
    	this.systemCore = new CarDroiDuinoCore();
    	try {
    		//*************************************************
    		// Inicializando o Gate que ir� conectar ao Server via TCP/IP e criar as Threads Trabalhadoras
    		this.socketClientGate = new DatagramSocketClientGate(this.systemCore, this.serverIPAddress, Integer.parseInt(this.clientServerPort));
    		//*************************************************
            // AQUI INICIALIZA A THREAD QUE PEGA OS FRAMES DA FILA
    		// DO CORE E OS COLOCA NA TELA DO DISPOSITIVO
    		//TODO: descomentar/comentar
    		this.surfaceView.startImageDrawer(this.systemCore);
            //*************************************************
    	} catch (Exception e){
			new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
		}    
    }
    
    /**
     * Efetua a configura��o do Listener para Come�ar a capturar os
     * Valores do Aceler�metros
     */
    private void setupAccelerometer(){
    	//*************************************************
    	// Inicializa o gerenciador de sensores
    	this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	//*************************************************
    	// Capturando o Aceler�metro para registrar no Listener
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //*************************************************
        // Registrando essa Classe (que implementa o Listener de Sensores) como Listener para o Aceler�metro
        // A partir daqui come�a a ativar o m�todo onSensorChanged executado sempre que muda o valor do sensor
        // A taxa de coleta de valores do sensor foi setada para taxa de GAMEs
        this.sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    

	/**
     * Efetua a configura��o dos Bot�es e Itens para Controle do Carrinho
     */
    private void setupControles(){
    	//*************************************************
    	// Capturando bot�o de Ir para Frente
    	ImageButton btnFrente = (ImageButton) findViewById(R.id.btnFrente);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnFrente.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (!modoFreeNavigation){
					//*************************************************
					//S� executa se o modo Free Navigation estiver desligado
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						//*************************************************
						// Se apertar, manda o carrinho pra Frente
						controleFrente();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						//*************************************************
						// Se soltar, manda o carrinho Parar
						controleParar();
					}
				}	
				return true;
			}
		});
    	    	
    	//*************************************************
    	// Capturando bot�o de Ir para Tr�s
    	ImageButton btnRe = (ImageButton) findViewById(R.id.btnRe);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnRe.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (!modoFreeNavigation){
					//*************************************************
					//S� executa se o modo Free Navigation estiver desligado
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						//*************************************************
						// Se apertar, manda o carrinho pra Tr�s
						controleRe();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						//*************************************************
						// Se soltar, manda o carrinho Parar
						controleParar();
					}	
				}
				return true;
			}
		});
    	    	
    	//*************************************************
    	// Capturando bot�o de Troca de Marcha para Cima
    	ImageButton btnMarchaUP = (ImageButton) findViewById(R.id.btnMarchaAumenta);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnMarchaUP.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					//*************************************************
					// Se apertar, manda aumentar a marcha do carrinho
					controleMarchaAumentar();
				}
				return true;
			}
		});
    	
    	//*************************************************
    	// Capturando bot�o de Troca de Marcha para Baixo
    	ImageButton btnMarchaDown = (ImageButton) findViewById(R.id.btnMarchaReduz);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnMarchaDown.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					//*************************************************
					// Se apertar, manda diminuir a marcha do carrinho
					controleMarchaDiminuir();
				}
				return true;
			}
		});
    	
    	//*************************************************
    	// Capturando bot�o de Ligar Flash do aparelho
    	ImageButton btnLanterna = (ImageButton) findViewById(R.id.btnFarol);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnLanterna.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					//*************************************************
					// Se apertar, manda ligar ou desligar a lanterna
					ligarDesligarLanterna();
				}
				return false;
			}
		});
    	
    	//*************************************************
    	// Capturando bot�o para ligar/desligar o Modo Free Navigation
    	ToggleButton btnFreeNavigation = (ToggleButton) findViewById(R.id.btnFreeNavigation);
    	//*************************************************
    	//Configurando Eventos do Bot�o
    	btnFreeNavigation.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					//*************************************************
					// Liga o Modo Free Navigation
					ligarDesligarFreeNavigation();
				}
				return false;
			}
		});	
    }
    
    /**
     * Envia o Comando para a Fila do Core que encaminha para o Server via TCP/IP
     * @param comando String com o Comando para o Carrinho
     */
    private void enviarComandoCarrinho(String comando){
    	if (this.systemCore != null)
    		this.systemCore.addDataToCarControlQueue(comando.getBytes());
    }
	
	/**
	 * Evento disparado ao Mudar a Precis�o da Leitura do Sensor
	 * Implementa��o do Listener de Sensores
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//**************************************************
		// Interface obriga a impementar - mas nao ha 
		// utilidade no momento - Fica Fazio por eqto
		//**************************************************
	}

	/**
	 * Evento disparado ao Mudar o valor dos Sensores Monitorados,
	 * neste caso, o Aceler�metro
	 * Implementa��o do Listener de Sensores
	 */
	public void onSensorChanged(SensorEvent event) {
		//*************************************************
		// Se n�o for o Sensor de Aceler�metro ent�o sai fora
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
		
		//*************************************************
		// Ajuste da frequencia de leitura - Se o valor de amostras a 
		// ignorar nao foi atingido, incrementa o contador e sai da funcao
//		if (this.accelerAmostrasIgnoradas < SystemProperties.ACELER_AMOSTRAS_IGNORAR){
//			this.accelerAmostrasIgnoradas++;
//			return;
//		}else{
//			this.accelerAmostrasIgnoradas = 0;
//		}
			
		
		//*************************************************
		// event possui uma propriedade values que � um 
		// array de  floats. 
		// Posi��o 0: Eixo X
		// Posi��o 1: Eixo Y
		// Posi��o 2: Eixo Z
		//*************************************************
		
		//*************************************************
		// Enviando o Comando de Direcionais para Carrinho
		this.controleEsquerdaDireita(event.values[1]);
		
		//*************************************************
		// Se o Modo Free Navigation estiver ativado ent�o come�a a controlar
		// para Frente e para Tr�s pelo aceler�metro
		if (this.modoFreeNavigation){
			//*****************************************************
			//Calcula os Pontos do modo Free Navifation quando esse � iniciado
			if(this.freeNavigationAdquirirLimites){
				this.freeNavigationCalcularPontos(event.values[2]);
				this.freeNavigationAdquirirLimites = false;
			}
			
			//*****************************************************
			//Envia a Leitura do aceler�metro para gerar os comandos
			this.controleFreeNavigation(event.values[2]);
		}
	}
	
	/**
	 * Envia o Comando de Esquerda e Direita para o Carrinho de Acordo com o valor da acelera��o
	 * do Eixo Y do Aceler�metro do dispositivo
	 * Esquerda: -9 � 0 m/s�
	 * Direita: 0 � 9 m/s�
	 * @param aceleracao Valor de -9 � 9 representando a inclina��o do Controle
	 */
	private void controleEsquerdaDireita(float aceleracaoEixoY){
		
		//******************************************************
		//Se n�o atingiu as condi��es necess�rias ent�o cancela a rotina e n�o envia comando algum
		if (!this.controleVerificaVariacaoEixoY(aceleracaoEixoY))
			return;
		
		String comando = "";
		//** MODIFICADO ***********************************
		// Pega o Valor da Acelera��o e Multiplica por 10 
		// Em seguida obt�m o Valor Absoluto (positivo)
		// Por exemplo: aceleracaoEixoY = -7.564
		// Multiplica por 10 = -75.64
		// Valor absoluto = 75.64
		// Transformando em Integer = 75 --> Logo deve vira 75 graus
		// int grausVirar = this.validarAnguloDirecao((int) Math.abs(aceleracaoEixoY*10));
		//** MODIFICADO ***********************************
		
		//*************************************************
		// Divide o valor da Acelera��o pelo valor M�ximo de Leitura do Aceler�metro
		// Em seguida multiplica pelo m�ximo de convers�o do volante e tira o valor inteiro
		// Por Exemplo:  aceleracaoEixoY = -7.564
		// Divide por 10 = -0,764
		// Multiploca por 90 = -68,76
		// Valor absoluto = 68,76
		// Transformando em Integer = 68 --> Logo deve vira 68 graus
		int grausVirar = this.validarAnguloDirecao((int) Math.abs((aceleracaoEixoY/SystemProperties.CONTROLE_ACELEROMETRO_FUNDO_ESCALA_SUPERIOR)*SystemProperties.CONTROLE_GRAUS_MAX));
		
		//*************************************************
		// Verificando para qual lado deve virar o Carrinho
		// Esqueda --> Quando a acelera��o no Eixo Y for menor que 0
		// Direita --> Quando a acelera��o no Eixo Y for maior ou igual a 0
		if(aceleracaoEixoY < 0){
			comando = SystemProperties.COMANDO_ESQUERDA + String.format("%02d", grausVirar);
		}else{
			comando = SystemProperties.COMANDO_DIREITA + String.format("%02d", grausVirar);
		}
		
		//*************************************************
		// Envia o comando para a Fila do Core para depois ser enviado ao Server
		//TODO: Descomentar/Comentar
		this.enviarComandoCarrinho(comando);		
	}
	
	/**
	 * Verifica e valida o Angulo obtido pela aceleracao do acelerometro
	 * @param angulo angulo obtido
	 * @return angulo validado
	 */
	private int validarAnguloDirecao(int angulo){
		if (angulo > SystemProperties.CONTROLE_GRAUS_MAX)
			return SystemProperties.CONTROLE_GRAUS_MAX;
		else
			return angulo;
	}
	
	/**
	 * Envia o Comando para o Carrinho andar para Frente
	 */
	private void controleFrente(){
		this.enviarComandoCarrinho(SystemProperties.COMANDO_FRENTE);
	}
	
	/**
	 * Envia o Comando para o Carrinho parar de Andar
	 */
	private void controleParar(){
		this.enviarComandoCarrinho(SystemProperties.COMANDO_PARAR);
	}
	
	/**
	 * Envia o Comando para o Carrinho andar de R�
	 */
	private void controleRe(){
		this.enviarComandoCarrinho(SystemProperties.COMANDO_RE);
	}
	
	/**
	 * Aumenta e envia a marcha para o Carrinho
	 */
	private void controleMarchaAumentar(){
		if (this.controleMarcha < SystemProperties.CONTROLE_NUM_MARCHA_MAX){
			this.controleMarcha++;
			this.enviarComandoCarrinho(SystemProperties.COMANDO_MARCHA + SystemProperties.COMANDO_MARCHA_CARACTERES[this.controleMarcha]); //String.format("%02d",this.controleMarcha));
			EditText txtMarcha = (EditText) findViewById(R.id.txtMarcha);
			txtMarcha.setText(String.valueOf(this.controleMarcha));
		}
	}
	
	/**
	 * Diminui e envia a marcha para o Carrinho
	 */
	private void controleMarchaDiminuir(){
		if (this.controleMarcha > SystemProperties.CONTROLE_NUM_MARCHA_MIN){
			this.controleMarcha--;
			this.enviarComandoCarrinho(SystemProperties.COMANDO_MARCHA + SystemProperties.COMANDO_MARCHA_CARACTERES[this.controleMarcha]);//String.format("%02d",this.controleMarcha));
			EditText txtMarcha = (EditText) findViewById(R.id.txtMarcha);
			txtMarcha.setText(String.valueOf(this.controleMarcha));
		}
	}
	
	/**
	 * Enviar comando para Ligar/Desligar a Lanterna do Flash do Dispositivo Server
	 */
	private void ligarDesligarLanterna(){
		//******************************************************
		//Envia comando para Ligar/Desligar a Lanterna no Server
		this.enviarComandoCarrinho(SystemProperties.COMANDO_LANTERNA_SERVER);	
	}
	
	/**
	 * Liga/Desliga o Modo Free Navigation de Controle do Automodelo
	 */
	private void ligarDesligarFreeNavigation(){
		//******************************************************
		//Inverte o Status da Flag de Controle 
		//True: M�do Ligado
		//False: M�do Desligado
		this.modoFreeNavigation = !this.modoFreeNavigation;
		
		//******************************************************
		//Se foi habilitado ent�o manda adquirir os Limites -> True
		this.freeNavigationAdquirirLimites = this.modoFreeNavigation;
	
		//******************************************************
		//Se foi desabilitado ent�o mandar parar o automodelo por segunra�a
		if (!this.modoFreeNavigation){
			this.controleParar();
		}
	}
	
	/**
	 * Controle pelo Modo Free Navigation - O Automodelo � controlado pela acelera��o da gravidade
	 * no Eixo Z do aceler�metro do Controle
	 * @param aceleracaoEixoZ - Acelera��o da gravidade no Eixo Z do aceler�metro
	 */
	private void controleFreeNavigation(float aceleracaoEixoZ){
		
		//******************************************************
		//Se n�o atingiu as condi��es necess�rias ent�o cancela a rotina e n�o envia comando algum
		if (!this.controleVerificaVariacaoEixoZ(aceleracaoEixoZ))
			return;
		
		if(this.valorCalculadoEixoZ > this.freeNavigationPontoFrente && this.freeNavigationRe){
			//******************************************************
			//Automodelo indo pra tr�s e inclina��o do controle para frente- inten��o do usu�rio em parar
			this.controleParar();
			this.freeNavigationRe = false;
			
		}else if (this.valorCalculadoEixoZ > this.freeNavigationPontoFrente){
			//******************************************************
			//Automodelo parado ou j� se movimentando pra frente - inclina��o do controle pra frente
			this.controleFrente();
			this.freeNavigationFrente = true;
			
		}else if (this.valorCalculadoEixoZ < this.freeNavigationPontoRe && this.freeNavigationFrente){
			//******************************************************
			// Automodelo indo para frente e inclina��o do controle para frenter - inten��o do usu�rio em parar
			this.controleParar();
			this.freeNavigationFrente = false;
			
		}else if (this.valorCalculadoEixoZ < this.freeNavigationPontoRe){
			//******************************************************
			//Automodelo parado ou j� se movimentando pra tr�s - inclina��o do controle pra tr�s
			this.controleRe();
			this.freeNavigationRe = true;
		}
	}
	
	/**
	 * Efetua os C�lculos para o Controle pela Diferen�a entre duas m�dias de leituras - Se a diferen�a
	 * for maior do que o estabelecido ent�o a varia��o � consider�vel para o controle
	 * @param aceleracaoEixoZ - Acelera��o da gravidade no Eixo Z do aceler�metro
	 * @return True se a varia��o for significativa
	 */
	private boolean controleVerificaVariacaoEixoZ(float aceleracaoEixoZ){
		
		if (this.contaLidoEixoZ_1 < SystemProperties.CONTROLE_EIXO_Z_QTD_LEITURAS_1){
			//******************************************************
			//Acumula o valor lido das primeiras leituras
			this.acumuladoLidoEixoZ_1 += aceleracaoEixoZ;
			this.contaLidoEixoZ_1++;
		}
		
		if (this.contaLidoEixoZ_1 >= SystemProperties.CONTROLE_EIXO_Z_QTD_LEITURAS_1){
			//******************************************************
			//Se terminou de adquirir o 1� acumulado ent�o come�a a adquirir o 2�
			if (this.contaLidoEixoZ_2 < SystemProperties.CONTROLE_EIXO_Z_QTD_LEITURAS_2){
				//******************************************************
				//Acumula o valor lido das segundas leituras
				this.acumuladoLidoEixoZ_2 += aceleracaoEixoZ;
				this.contaLidoEixoZ_2++;
			}

			if (this.contaLidoEixoZ_2 >= SystemProperties.CONTROLE_EIXO_Z_QTD_LEITURAS_2){
				//******************************************************
				//A m�dia do Segundo acumulado � o valor que ser� enviado para a gera��o dos comandos
				this.valorCalculadoEixoZ = this.acumuladoLidoEixoZ_2/this.contaLidoEixoZ_2;
				
				//******************************************************
				//Calcula a diferen�a entre as m�dias do Primeiro e do Segundo acumulado para obter a varia��o
				float thresHoldCalculado = Math.abs((this.acumuladoLidoEixoZ_1/this.contaLidoEixoZ_1) - this.valorCalculadoEixoZ);
				
				if(thresHoldCalculado >= SystemProperties.CONTROLE_EIXO_Z_THRESHOLD){
					//******************************************************
					//Se a varia��o for maior ou igual do que o estabelecido ent�o reseta as leituras calcula os comandos 
					this.acumuladoLidoEixoZ_1 = 0;
					this.contaLidoEixoZ_1 = 0;
					this.acumuladoLidoEixoZ_2 = 0;
					this.contaLidoEixoZ_2 = 0;
					
					return true;
				}else{
					//******************************************************
					//Se a varia��o for menor do que estabelico ent�o reseta apenas o segundo acumulado
					this.acumuladoLidoEixoZ_2 = 0;
					this.contaLidoEixoZ_2 = 0;
				}
			}
		}
		//******************************************************
		//Retorna false como default se nunca atingir as condi��es necess�rias
		return false;
	}
	
	/**
	 * Efetua os C�lculos para o Controle pela Diferen�a entre duas m�dias de leituras - Se a diferen�a
	 * for maior do que o estabelecido ent�o a varia��o � consider�vel para o controle
	 * @param aceleracaoEixoY - Acelera��o da gravidade no Eixo Y do aceler�metro
	 * @return True se a varia��o for significativa
	 */
	private boolean controleVerificaVariacaoEixoY(float aceleracaoEixoY){
		
		if (this.contaLidoEixoY_1 < SystemProperties.CONTROLE_EIXO_Y_QTD_LEITURAS_1){
			//******************************************************
			//Acumula o valor lido das primeiras leituras
			this.acumuladoLidoEixoY_1 += aceleracaoEixoY;
			this.contaLidoEixoY_1++;
		}
		
		if (this.contaLidoEixoY_1 >= SystemProperties.CONTROLE_EIXO_Y_QTD_LEITURAS_1){
			//******************************************************
			//Se terminou de adquirir o 1� acumulado ent�o come�a a adquirir o 2�
			if (this.contaLidoEixoY_2 < SystemProperties.CONTROLE_EIXO_Y_QTD_LEITURAS_2){
				//******************************************************
				//Acumula o valor lido das segundas leituras
				this.acumuladoLidoEixoY_2 += aceleracaoEixoY;
				this.contaLidoEixoY_2++;
			}

			if (this.contaLidoEixoY_2 >= SystemProperties.CONTROLE_EIXO_Y_QTD_LEITURAS_2){
				//******************************************************
				//A m�dia do Segundo acumulado � o valor que ser� enviado para a gera��o dos comandos
				this.valorCalculadoEixoY = this.acumuladoLidoEixoY_2/this.contaLidoEixoY_2;
				
				//******************************************************
				//Calcula a diferen�a entre as m�dias do Primeiro e do Segundo acumulado para obter a varia��o
				float thresHoldCalculado = Math.abs((this.acumuladoLidoEixoY_1/this.contaLidoEixoY_1) - this.valorCalculadoEixoY);
				
				if(thresHoldCalculado >= SystemProperties.CONTROLE_EIXO_Y_THRESHOLD){
					//******************************************************
					//Se a varia��o for maior ou igual do que o estabelecido ent�o reseta as leituras calcula os comandos 
					this.acumuladoLidoEixoY_1 = 0;
					this.contaLidoEixoY_1 = 0;
					this.acumuladoLidoEixoY_2 = 0;
					this.contaLidoEixoY_2 = 0;
					
					return true;
				}else{
					//******************************************************
					//Se a varia��o for menor do que estabelico ent�o reseta apenas o segundo acumulado
					this.acumuladoLidoEixoY_2 = 0;
					this.contaLidoEixoY_2 = 0;
				}
			}
		}		
		//******************************************************
		//Retorna false como default se nunca atingir as condi��es necess�rias
		return false;
	}
	
	/**
	 * Calcula os Pontos ao ativar o modo Free Navigation com o aceler�metro
	 * ALTERADO - 24/09/2012 - N�o calcula mais os pontos - Pontos definidos nas propriedades do Sistemas
	 * @param valorInicialEixoZ - Valor inicial do Eixo Z do Aceler�metro
	 */
	private void freeNavigationCalcularPontos(float valorInicialEixoZ){
		
		//*****************************************************
		//
		// ATEN��O - Alterado em 24/09/2012 devido a dificuldade de controle pelo FreeNavigation previamente criado - Os Textos e 
		// c�digos comentados abaixo n�o s�o mais v�lidos, mas est�o presentes para hist�rico de altera��es
		//
		//O Ponto Inicial do eixo Z est� proximo ao meio do intervalo de 
		// pontos em que o automodelo estar� parado - Considerando que
		// o automodelo ficar� parado num valor equivalente a 25% do 
		// range de leitura o Ponto de R� est� a 15% do range menos o valor
		// do Ponto inicial, assim como o valor do Ponto da marcha 1 est� 
		// a 10% do range de leitura mais o valor inicial.
		//*****************************************************
		// ALTERADO - 24/09/2012 - CARREGA O PONTO DAS PROPRIEDADES DO SISTEMA
		this.freeNavigationPontoRe =  SystemProperties.CONTROLE_FREE_NAV_PONTO_RE_DEFAULT;//(float) (valorInicialEixoZ - (SystemProperties.CONTROLE_ACELEROMETRO_RANGE_TOTAL*0.4));
		
		//*****************************************************
		//Checando se o Ponto calculado n�o est� fora da escala do aceler�metro
		//if ((SystemProperties.CONTROLE_ACELEROMETRO_FUNDO_ESCALA_SUPERIOR + this.freeNavigationPontoRe) < 0){
			//this.freeNavigationPontoRe = SystemProperties.CONTROLE_FREE_NAV_PONTO_RE_DEFAULT;
		//}
		
		//******************************************************
		//Calculando Ponto de Marcha 1 - 10% mais o Ponto Inicial
		// ALTERADO - 24/09/2012 - CARREGA O PONTO DAS PROPRIEDADES DO SISTEMA
		this.freeNavigationPontoFrente = SystemProperties.CONTROLE_FREE_NAV_PONTO_M1_DEFAULT;//(float) (valorInicialEixoZ + (SystemProperties.CONTROLE_ACELEROMETRO_RANGE_TOTAL*0.4));
		
		//*****************************************************
		//Checando se o Ponto calculado n�o est� fora da escala do aceler�metro
		//if ((SystemProperties.CONTROLE_ACELEROMETRO_FUNDO_ESCALA_SUPERIOR - this.freeNavigationPontoFrente) < 0){
			//this.freeNavigationPontoFrente = SystemProperties.CONTROLE_FREE_NAV_PONTO_M1_DEFAULT;
		//}
		
		//******************************************************
		//Calculando Ponto de Marcha 2 - Ponto de Marcha 1 mais 50% do que sobrou entre o Ponto da Marcha 1 e o Fundo de Escala Superior
		//this.freeNavigationPontoM2 = (float) (this.freeNavigationPontoM1 + ((fundoEscalaSuperiorAcelerometro - this.freeNavigationPontoM1)*0.5));
		
		//******************************************************
		//Calculando Ponto de Marcha 3 - Ponto de Marcha 2 mais 30% do que sobrou entre o Ponto da Marcha 1 e o Fundo de Escala Superior
		//this.freeNavigationPontoM3 = (float) (this.freeNavigationPontoM2 + ((fundoEscalaSuperiorAcelerometro - this.freeNavigationPontoM1)*0.3));
	}
	
	/**
	 * Envia os comandos para condi��o inicial do automodelo ao ativar o controle
	 * Alinhar para frente e selecionar marcha neutra
	 */
	private void condicaoInicial(){
		this.enviarComandoCarrinho(SystemProperties.COMANDO_MARCHA + SystemProperties.COMANDO_MARCHA_CARACTERES[0]);
		this.enviarComandoCarrinho(SystemProperties.COMANDO_DIREITA + String.format("%02d", 0));
	}
	
	/**
	 * Mata as Threads dos Gates
	 */
	private void killGates(){
		//*********************************************
		//Matando as Threads do Server
		this.socketClientGate.turnOff();
	}
	
	/**
	 * MATA TUDOOO!!!!!!!!!!!!! 
	 */
	@Override
	protected void onDestroy() {
		//*********************************************
		//Metodo superior
		super.onDestroy();
		//*********************************************
		//Matando as Threads
		this.killGates();
	}
	
//	@Override
//	protected void onPause(){
//		//*********************************************
//		//Metodo superior
//		super.onPause();
//		//*********************************************
//	}
//	
//	@Override
//	protected void onStop(){
//		//*********************************************
//		//Metodo superior
//		super.onStop();
//		//*********************************************
//	}
}
