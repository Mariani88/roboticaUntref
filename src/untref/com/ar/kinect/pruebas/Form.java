package untref.com.ar.kinect.pruebas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
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
	private JLabel label_distancia_value;
	private JRadioButton radioColor;
	private JRadioButton radioProfundidad;
	private JRadioButton radioAmbos;
	private JComboBox<String> combo_DistanciaObstaculos;
	private JComboBox<String> combo_cantPixeles;
	private JLabel label_DistanciaObstaculos;
	private JLabel label_BarridoPixeles;
	private float alpha;
	private boolean testing;
	//private JPanel panelEventos;

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
		labelPrincipalImagen.setBorder(BorderFactory
				.createLineBorder(Color.black));
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

		ButtonGroup radioButtons = new ButtonGroup();
		radioButtons.add(radioColor);
		radioButtons.add(radioProfundidad);
		radioButtons.add(radioAmbos);		

		Container contentPane = this.getContentPane();
		JScrollPane scrollPane = new JScrollPane(contentPane);
		scrollPane.setBorder(null);
		this.setContentPane(scrollPane);
	}

//**************************************************************Agregado**********************************************************************	
	
	//Construir del panel de acciones
	private JPanel contruirPanelEventos(){
		
		GridBagConstraints c;
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Entrada de datos"));

		radioColor = new JRadioButton("Color");
		radioProfundidad = new JRadioButton("Profundidad");
		radioAmbos = new JRadioButton("Ambos");
		radioColor.setFocusPainted(false);
		radioProfundidad.setFocusPainted(false);
		radioAmbos.setFocusPainted(false);
		radioColor.setSelected(true);
		
		//Agregado
		String[] opcionesCombo = {"0.7","1","1.5","2","2.5","3"};
		combo_DistanciaObstaculos = new JComboBox<String>(opcionesCombo);//Este combo solo se deberia mostrar cuando se selecciona "Profundidad"
		combo_DistanciaObstaculos.setSelectedIndex(0);//Por defecto el valor seleccionado es 0.7 m.		
		combo_DistanciaObstaculos.setPreferredSize(new Dimension(75,25));;
		
		String[] opcionesComboPixeles = {"5","10","15"};
		combo_cantPixeles = new JComboBox<String>(opcionesComboPixeles);
		combo_cantPixeles.setSelectedIndex(1);		
		combo_cantPixeles.setPreferredSize(new Dimension(75,25));;
		
		label_DistanciaObstaculos = new JLabel();
		label_DistanciaObstaculos.setText("Obstaculos a:");
		
		label_BarridoPixeles = new JLabel();
		label_BarridoPixeles.setText("Cant. Pixeles:");
				
		c = this.posicionarElemento(0,0,new Insets(5, 5, 5, 5));
		c.weightx = 1;		
		this.getContentPane().add(panel, c);
		panel.add(radioColor, c);
		
		c = this.posicionarElemento(0,1,new Insets(5, 5, 5, 5));		
		this.getContentPane().add(panel, c);
		panel.add(radioProfundidad, c);
		
		c = this.posicionarElemento(0,2,new Insets(5, 5, 5, 5));		
		this.getContentPane().add(panel, c);
		panel.add(radioAmbos, c);				
		
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 4;
		this.getContentPane().add(panel, c);
		panel.add(label_DistanciaObstaculos, c);
		
		c = this.posicionarElemento(0,4,new Insets(5, 5, 5, 5));
		c.anchor = GridBagConstraints.EAST;
		this.getContentPane().add(panel, c);
		panel.add(combo_DistanciaObstaculos, c);
				
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 5;
		this.getContentPane().add(panel, c);
		panel.add(label_BarridoPixeles, c);
		
		c = this.posicionarElemento(0,5,new Insets(5, 5, 5, 5));
		c.anchor = GridBagConstraints.EAST;
		this.getContentPane().add(panel, c);
		panel.add(combo_cantPixeles, c);
		
		final JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		scrollBar.setMaximum(100);
		scrollBar.setMinimum(0);
		scrollBar.setVisibleAmount(0);
		scrollBar.setValue(50);
		alpha = 0.50f;
		scrollBar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {

				alpha = (float) scrollBar.getValue() / 100;
			}
		});
		
		c = this.posicionarElemento(0,3,new Insets(5, 5, 5, 5));	
		c.fill = GridBagConstraints.HORIZONTAL;		
		panel.add(scrollBar, c);

		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		this.getContentPane().add(panel, c);
		
		return panel;
	}
	
	//contruye Matriz de resultados
	private void construirMatrizResultados(){
		construirFila(1,0,100,50,"X");
		construirFila(1,1,100,50,"Y");
		construirFila(1,2,100,50,"Color (R,G,B)");
		construirFila(1,3,100,50,"Distancia");
		label_x_value = construirFila(2,0,100,50,"");
		label_y_value = construirFila(2,1,100,50,"");
		label_color_value = construirFila(2,2,100,50,"");
		label_distancia_value = construirFila(2,3,100,50,"");
	}
	
	//ConstruirFila
	private JLabel construirFila(int posX, int posY,int dimensionX, int dimensionY,String nombreDeFila){
		
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(dimensionX, dimensionY));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setHorizontalAlignment(JLabel.CENTER);
		if (nombreDeFila != ""){label.setText(nombreDeFila);}

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = posX;
		c.gridy = posY;
		this.getContentPane().add(label, c);
		
		return label;
		
	}
	
	//Evita repeticion de codigo
	private GridBagConstraints posicionarElemento(int posX, int posY, Insets ins){
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = posX;
		c.gridy = posY;
		c.insets = ins;
		
		return c;
		
	}

//***************************************************************************************************************************************************
	
	private void updateValues(MouseEvent e) {

		label_x_value.setText(String.valueOf(e.getX()));
		label_y_value.setText(String.valueOf(e.getY()));
		Color color = data.getColorEnPixel(e.getX(), e.getY());
		label_color_value.setText("(" + color.getRed() + "," + color.getGreen()
				+ "," + color.getBlue() + ")");
		label_distancia_value.setText(String.valueOf(data.getDistancia(
				e.getX(), e.getY()) / 100) + " cm");
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
		
	private void setEstadoVisibleDeFiltrosAdicionales(boolean bool){		
		combo_DistanciaObstaculos.setVisible(bool);
		label_DistanciaObstaculos.setVisible(bool);
		combo_cantPixeles.setVisible(bool);
		label_BarridoPixeles.setVisible(bool);
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
			this.setEstadoVisibleDeFiltrosAdicionales(true);
			float dist = Float.parseFloat(combo_DistanciaObstaculos.getSelectedItem().toString());
			int cantPixeles = Integer.parseInt(combo_cantPixeles.getSelectedItem().toString());
			
			data.setPixelColorPorProfundidad(dist * 10000, cantPixeles);
			imagen = data.getImagenProfundidad();			
			
		}else{
			this.setEstadoVisibleDeFiltrosAdicionales(false);
		}
		
		if (radioAmbos.isSelected()) {
			imagen = both(data.getImagenColor(), data.getImagenProfundidad());
		}

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
