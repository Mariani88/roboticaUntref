package untref.com.ar.kinect.pruebas;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;



public class SensorDataProductionTest {

	@Test
	public void exportarDatosDebeGenerarArchivoCSV (){
		
		SensorDataProduction sensor = new SensorDataProduction ();
		
		sensor.exportarDatos("");
		
		File archivoCSV = new File ("datosExportados.csv"); 
		Assert.assertTrue(archivoCSV.exists());
	}
}