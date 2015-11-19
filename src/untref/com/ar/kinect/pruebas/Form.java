package untref.com.ar.kinect.pruebas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class Form extends JFrame {

	private Kinect kinect;
	private Timer timer;
	private SensorData data;
	private JLabel labelPrincipalImagen;
	private JLabel label_x_value;
	private JLabel label_y_value;
	private JLabel label_color_value;
	private JLabel label_altura_value;
	private JLabel label_distancia_value;
	private JRadioButton radioColor;
	private JRadioButton radioProfundidad;
	private JRadioButton radioCurvasNivel;
	private JRadioButton radioAmbos;	
	private JComboBox<String> combo_DistanciaObstaculos;
	private JComboBox<String> combo_cantPixeles;
	private JComboBox<String> combo_coloresContorno;
	private JComboBox<String> combo_NivelesDeAltura;
	private JLabel label_NivelesDeAltura;	
	private JLabel label_DistanciaObstaculos;
	private JTextField input_IntervaloEntreCurvas;
	private JTextField input_Rango_de_Profundidad;
	private JLabel label_IntervaloEntreCurvas;
	private JLabel label_BarridoPixeles;
	private JLabel label_ColoresContorno;
	private JScrollBar scrollBar;
	private float alpha;
	private boolean testing;

	public Form(Boolean esTest) {
		testing = esTest;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setupUI();
		this.pack();

		if (!testing) {
			setupKinect();
		}

		this.setVisible(true);
	}

	public void start() {
		this.timer = new Timer();
		long period = (1 / 10) * 1000;
		period = 100;
		this.timer.scheduleAtFixedRate(new Tarea(this), 0, period);
	}

	private void setupUI() {

		this.setContentPane(new JPanel(new GridBagLayout()));
		GridBagConstraints c;

		JPanel panelImagen = new JPanel(new GridBagLayout());
		panelImagen.setPreferredSize(new Dimension(640 + 50, 480 + 50));		

		labelPrincipalImagen = new JLabel();
		labelPrincipalImagen.setBorder(BorderFactory.createLineBorder(Color.black));
		labelPrincipalImagen.setOpaque(true);
		labelPrincipalImagen.setBackground(Color.gray);

		panelImagen.add(labelPrincipalImagen);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 100;
		this.getContentPane().add(panelImagen, c);

		labelPrincipalImagen.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

				updateValues(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		this.construirMatrizResultados();

		this.contruirPanelEventos();

			}

//**************************************************************Agregado**********************************************************************	
	
	//Construir del panel de acciones
	private void contruirPanelEventos(){
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Entrada de datos"));
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panel);
		
		radioColor = builder.contruirRadioButton("Color", 0, 0);
		radioColor.setSelected(true);
		radioProfundidad = builder.contruirRadioButton("Profundidad", 0, 1);
		radioAmbos = builder.contruirRadioButton("Ambos", 0, 2);
		radioCurvasNivel = builder.contruirRadioButton("Curvas de Nivel", 0, 3);		
		scrollBar = builder.construirScrollBar(0, 5);
		alpha = 0.50f;
		
		label_NivelesDeAltura = builder.contruirLabel("Niveles de altura cada:", 0, 4);
		String[] opciones = {"15","25","50","75","100"};
		combo_NivelesDeAltura = builder.construirCombo(0, 4, opciones);
		
		label_IntervaloEntreCurvas = builder.contruirLabel("Curvas cada:", 0, 5);
		input_IntervaloEntreCurvas = builder.construirInputText(0, 5);		
		input_IntervaloEntreCurvas.setText("10");		
		
		label_DistanciaObstaculos = builder.contruirLabel("Obstaculos a:", 0, 6);
		String[] opcionesCombo = {"0.7","1","1.5","2","2.5","3"};
		combo_DistanciaObstaculos = builder.construirCombo(0, 6, opcionesCombo);					
		
		label_BarridoPixeles = builder.contruirLabel("Cant. Pixeles", 0, 7);
		String[] opcionesComboPixeles = {"5","10","15"};
		combo_cantPixeles = builder.construirCombo(0, 7, opcionesComboPixeles);
		combo_cantPixeles.setSelectedIndex(1);	
		
		label_ColoresContorno = builder.contruirLabel("Contorno:", 0, 8);
		String[] opcionesComboColores = {"Azul","Amarillo","Naranja","Rojo","Verde"};
		//Si agrega otro color al combo modificar metodo #getColorContorno()
		combo_coloresContorno = builder.construirCombo(0, 8, opcionesComboColores);
		
		builder.contruirLabel("Rango distancia(cm):", 0, 9);
		input_Rango_de_Profundidad = builder.construirInputText(0, 9);		
		input_Rango_de_Profundidad.setText("4");
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		this.getContentPane().add(panel, c);
		
		ButtonGroup radioButtons = new ButtonGroup();
		radioButtons.add(radioColor);
		radioButtons.add(radioProfundidad);
		radioButtons.add(radioAmbos);
		radioButtons.add(radioCurvasNivel);		
		
		Container contentPane = this.getContentPane();
		JScrollPane scrollPane = new JScrollPane(contentPane);
		scrollPane.setBorder(null);
		this.setContentPane(scrollPane);

	}
	
	//contruye Matriz de resultados
	private void construirMatrizResultados(){
		construirFila(1,0,120,50,"X");
		construirFila(1,1,120,50,"Y");
		construirFila(1,2,120,50,"Color (R,G,B)");
		construirFila(1,3,120,50,"Distancia");
		construirFila(1,4,120,50,"Altura");
		label_x_value = construirFila(2,0,120,50,"");
		label_y_value = construirFila(2,1,120,50,"");
		label_color_value = construirFila(2,2,120,50,"");
		label_distancia_value = construirFila(2,3,120,50,"");
		label_altura_value = construirFila(2,4,120,50,"");
	}
	
	//ConstruirFila
	private JLabel construirFila(int posX, int posY,int dimensionX, int dimensionY,String nombreDeFila){
		
		JLabel label = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		
		label.setPreferredSize(new Dimension(dimensionX, dimensionY));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setHorizontalAlignment(JLabel.CENTER);
		if (nombreDeFila != "")
			label.setText(nombreDeFila);
		else	
			c.insets = new Insets(0, 0, 0, 5);
		
		
		c.gridx = posX;
		c.gridy = posY;		
		this.getContentPane().add(label, c);
		
		return label;
		
	}
	
	public void setAlpha(float val){
		this.alpha = val;
	}
	
	
	private void setVisibleFiltrosProfundidad(boolean bool){		
		combo_DistanciaObstaculos.setVisible(bool);
		label_DistanciaObstaculos.setVisible(bool);
		combo_cantPixeles.setVisible(bool);
		label_BarridoPixeles.setVisible(bool);
		
	}
	
		
	private void setVisibilidadFiltroDeColoresContorno(boolean bool){				
		combo_coloresContorno.setVisible(bool);
		label_ColoresContorno.setVisible(bool);
	}
	
	private void setVisibilidadFiltrosCurvasDenivel(boolean bool){				
		input_IntervaloEntreCurvas.setVisible(bool);
		label_IntervaloEntreCurvas.setVisible(bool);
		combo_NivelesDeAltura.setVisible(bool);
		label_NivelesDeAltura.setVisible(bool);
	}
	
	private Color getColorContorno(){
		
		String nombre_color = this.combo_coloresContorno.getSelectedItem().toString();
		Color colorContorno = Color.GREEN ;
		
		if (nombre_color == "Amarillo")
			colorContorno = Color.YELLOW;
		else if (nombre_color == "Azul")
			colorContorno = Color.BLUE;
		else if (nombre_color == "Rojo")
			colorContorno = Color.RED;
		else if (nombre_color == "Naranja")
			colorContorno = Color.ORANGE;
		
		return colorContorno;
	}
	
//***************************************************************************************************************************************************
	
	private void updateValues(MouseEvent e) {

		label_x_value.setText(String.valueOf(e.getX()));
		label_y_value.setText(String.valueOf(e.getY()));
		Color color = data.getColorEnPixel(e.getX(), e.getY());
		label_color_value.setText("(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
		label_distancia_value.setText(String.valueOf(data.getDistancia(e.getX(), e.getY()) / 100) + " cm");
		
		int rango_altura = Integer.parseInt(input_Rango_de_Profundidad.getText().isEmpty()?"4":input_Rango_de_Profundidad.getText())*100;		
		label_altura_value.setText(String.valueOf(data.getAltura(e.getX(), e.getY(),rango_altura)/100) + " cm");

	}

	private void setupKinect() {
		construirKinect();
		startKinect();
		esperarUmbralInicioKinect();
		chequearInicializacionKinect();
		setearAnguloDeElevacionDefault();
	}

	private void setearAnguloDeElevacionDefault() {
		kinect.setElevationAngle(0);
	}

	private void chequearInicializacionKinect() {
		if (!kinect.isInitialized()) {
			System.out.println("Falla al inicializar la kinect.");
			System.exit(1);
		}
	}

	private void esperarUmbralInicioKinect() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void construirKinect() {
		kinect = new Kinect();
	}

	private void startKinect() {
		kinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ
				| Kinect.PLAYER_INDEX);
	}
		
	
	public void actualizar() {
		if (!testing) {
			data = new SensorDataProduction(kinect);
		} else {
			data = new SensorDataTesting();
		}
				
		BufferedImage imagen = null;		
				
		if (radioColor.isSelected()) {
			imagen = data.getImagenColor();
		}
		
		
		
		if (radioProfundidad.isSelected()) {
			
			this.setVisibleFiltrosProfundidad(true);
			this.setVisibilidadFiltroDeColoresContorno(true);
			float dist = Float.parseFloat(combo_DistanciaObstaculos.getSelectedItem().toString());
			int cantPixeles = Integer.parseInt(combo_cantPixeles.getSelectedItem().toString());
			
			data.setPixelColorPorProfundidad(dist * 10000, cantPixeles,this.getColorContorno());
			imagen = data.getImagenProfundidad();
			
		}else{ 
			
			this.setVisibleFiltrosProfundidad(false);
			this.setVisibilidadFiltroDeColoresContorno(false);
		
		}
		
		if (radioAmbos.isSelected()) {
			
			imagen = both(data.getImagenColor(), data.getImagenProfundidad());
			scrollBar.setVisible(true);
		
		}else scrollBar.setVisible(false);
				
		if (radioCurvasNivel.isSelected()){
			
			this.setVisibilidadFiltrosCurvasDenivel(true);				
			
			int intervalo = Integer.parseInt(input_IntervaloEntreCurvas.getText().isEmpty()?"3":input_IntervaloEntreCurvas.getText());			
			data.pintarCurvaNivel(intervalo * 100);
			imagen =data.getImagenColor();
			
		}else this.setVisibilidadFiltrosCurvasDenivel(false);
		
		labelPrincipalImagen.setIcon(new ImageIcon(imagen));
	}

	private BufferedImage both(BufferedImage color, BufferedImage profundidad) {

		BufferedImage image = color;
		BufferedImage overlay = profundidad;

		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = combined.createGraphics();
		g.drawImage(image, 0, 0, null);

		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha);
		g.setComposite(ac);

		g.drawImage(overlay, 0, 0, null);

		return combined;
	}
}
