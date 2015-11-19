package untref.com.ar.kinect.pruebas;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface SensorData {

	BufferedImage getImagenColor();
	
	Color getColorEnPixel(int x, int y);
	
	float getDistancia(int x, int y);
	
	double getAltura(int x, int y, int rango_altura);
	
	BufferedImage getImagenProfundidad();
	
	void setPixelColorPorProfundidad(float dist, int cantPixeles, Color colorContorno);
	
	public void pintarCurvaNivel(int distEntreCurvas);
	
}
