/**
 * Clase: Utilidades
 * 
 * @version  1.0
 * 
 * @since 7-10-2019
 * 
 * @autor Ing. Jhon Mendez
 *
 * Copyrigth: Ing. Jhon Mendez
 */

package com.keywords.componentes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.opencsv.CSVWriter;

import edu.keywords.modelo.Video;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Se definen funciones comunes que pueden ser utilizadas en todo el proyecto
 * 
 *
 */

public class Utilidades {

	private static Rectangle2D rentangle2D;

	private static String rutaCSS = "fxml/css/";
	private static String urlConfiguracionCSSAlert;
	private static String archivoPalabrasProposiciones  = "recursos/palabras.dat";
	private static String nombreArchivo = "salida";
	private static String rutaPorDefecto;
	private static final String SQUARE_BUBBLE =
			"M24 1h-24v16.981h4v5.019l7-5.019h13z";

	private static List<String> palabras;


	private static Logger logger;




	/**
	 * Retorna un imagenView a partir del nombre de la imagen
	 * @param nombre
	 * @return 
	 */
	public static ImageView getImageView(String nombre) {

		return new ImageView(getImage(nombre));

	}

	/**
	 *  Retorna la imagen a partir de su nombre
	 * @param nombre
	 * @return 
	 */
	public static Image getImage(String nombre) {

		Image imagen = null;

		try (FileInputStream fileInputStream = new FileInputStream(nombre)) {

			imagen = new Image(fileInputStream);

		} catch (Exception e) {

			System.err.println("Error" +  e);
		}

		return imagen;

	}

	/**
	 * Devuelve eñ tamaño de la pantalla
	 */
	public static Rectangle2D getRectangle2D() {

		return rentangle2D;
	}

	public static void setRectangle2D(Rectangle2D rentangle) {

		rentangle2D = rentangle;
	}


	public static Tooltip getToolTip(String mensaje) {

		Tooltip tooltip = new Tooltip(mensaje);
		tooltip.setStyle("-fx-text-fill: white; -fx-background-color: #00709d; -fx-font-size: 16px; -fx-shape: \"" + SQUARE_BUBBLE + "\";");
		tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
		return tooltip;

	}


	public static String getRutaCSS() {
		return rutaCSS;
	}

	public static void setUrlConfiguracionCSSAlert(String urlConfiguracionCSSAlert) {
		Utilidades.urlConfiguracionCSSAlert = urlConfiguracionCSSAlert;
	}
	/**
	 * 
	 * @param tipo Tipo de alerta
	 * @param mensaje: Mensaje a mostrar
	 */
	public static boolean  mostrarAlerta(AlertType tipo, String mensaje) {

		boolean resultado = false;
		String  titulo = null;

		Alert alert;

		if (tipo == AlertType.CONFIRMATION) {

			alert = new Alert(tipo, mensaje,ButtonType.YES, ButtonType.NO);

			titulo = "Confirmación";

		} else {

			alert = new Alert(tipo);
			alert.setContentText(mensaje);

			if (tipo==AlertType.ERROR)

				titulo = "Error";

			else if (tipo == AlertType.INFORMATION)

				titulo = "Información";


		}

		alert.setTitle("Keywords Autocomplete");
		alert.setHeaderText(titulo);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(urlConfiguracionCSSAlert);
		dialogPane.getStyleClass().add("myDialog");

		Optional<ButtonType> opcionEscogida = alert.showAndWait();

		if (opcionEscogida.get() != null && opcionEscogida.get() == ButtonType.YES)

			resultado = true;

		return resultado;

	}


	/**
	 * 
	 * @param titulo: Titulo del stage(Ventana)
	 * @return Stage
	 */
	public static Stage getStage(String titulo) {

		Stage stage  = new Stage();
		stage.setTitle(titulo);
		stage.getIcons().add(getImage("recursos/imagenes/icono.png"));
		stage.initStyle(StageStyle.UNIFIED);

		stage.setWidth(getRectangle2D().getWidth());
		stage.setHeight(getRectangle2D().getHeight() * 0.98);

		return stage;

	}




	public static String getFechaActual() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");  
		LocalDateTime now = LocalDateTime.now(); 
		return dtf.format(now);

	}



	/**
	 * 
	 * 
	 * @param titulo: Titulo de la ventana 
	 * @param padre:  Stage padre    
	 * @return
	 */
	public static Stage getStageModal(String titulo, Stage padre) {

		Stage stage = new Stage();
		stage.setTitle(titulo);
		stage.getIcons().add(getImage("recursos/imagenes/icono.png"));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(padre);
		return stage;
	}


	public static void escribirArchivoCSV(String ruta,String nombre, List<Video> videos) throws FileNotFoundException, IOException {

		String nombreArchivo = ruta + File.separator + nombre + "-" + Utilidades.getFechaActual() +  ".csv";

		try (FileOutputStream fos = new FileOutputStream(nombreArchivo);
				OutputStreamWriter osw = new OutputStreamWriter(fos, 
						StandardCharsets.UTF_8);
				CSVWriter writer = new CSVWriter(osw)) {

			String[] headerRecord = {"Criterio", "Numero Videos"};
			writer.writeNext(headerRecord);

			for (Video video : videos) {

				writer.writeNext(new String[] {video.getCriterio(), "" + video.getNumeroVideoRelacionados()});

			}


		}   

	}

	public static Logger getLogger() {

		//Codigo tomado de la web
		if (logger == null) {

			logger = Logger.getLogger(Utilidades.class.getName());
			try {

				LogManager.getLogManager().readConfiguration(new FileInputStream("recursos/mylogging.properties"));

			} catch (SecurityException | IOException e1) {
				e1.printStackTrace();
			}
			logger.setLevel(Level.FINE);
			logger.addHandler(new ConsoleHandler());
			//adding custom handler
			logger.addHandler(new MyHandler());
			try {

				Handler fileHandler = new FileHandler("log/logger"+ getFechaActual()+".log");
				fileHandler.setFormatter(new MyFormatter());
				//setting custom filter for FileHandler
				fileHandler.setFilter(new MyFilter());
				logger.addHandler(fileHandler);

			} catch (SecurityException | IOException e) {
				e.printStackTrace();
			}

		}


		return logger;
	}

	public static String getNombreArchivo() {
		return nombreArchivo;
	}

	public static String getRutaPorDefecto() {

		if (rutaPorDefecto == null) {

			String path = new File (".").getAbsolutePath();
			rutaPorDefecto = path.substring(0,path.length() - 1);

		}


		return rutaPorDefecto;
	}


	public static String getPalabrasSinTilde(String oracion) {

		return oracion.replaceAll("á", "a").
				replaceAll("é", "e").
				replaceAll("í", "i").
				replaceAll("ó", "o").
				replaceAll("ú", "u");

	}


	public static List<String> getPalabras() {
	
		if (palabras == null) {
			
			try {

				File file = new File(archivoPalabrasProposiciones);
				Scanner scanner = new Scanner(file);

				palabras = new ArrayList<String>();

				while (scanner.hasNextLine()) {

					String palabra = scanner.nextLine();

					palabras.add(palabra.trim());

				}
				scanner.close();
				
			} catch (FileNotFoundException e) {

				getLogger().log( Level.SEVERE ,"Error en archivo de preposiciones: " + e);
				palabras = new ArrayList<String>();
			}
			
		}

		return palabras;

	}


}
