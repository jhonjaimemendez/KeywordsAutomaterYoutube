/**

 * Clase: ControladorLogin
 * 
 * @version  1.0
 * 
 * @since 7-1-2020
 * 
 * @autor Ing. Jhon Mendez
 *
 * Copyrigth: Juan Pablo Gonzalez Camera
 */

package com.keywords.vista;

import static com.keywords.componentes.Utilidades.getImage;
import static com.keywords.componentes.Utilidades.getRutaCSS;
import static com.keywords.componentes.Utilidades.setRectangle2D;
import static com.keywords.componentes.Utilidades.setUrlConfiguracionCSSAlert;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Esta clase controla la interfaz grafica generada desde el FXML
 * 
 */

public class Principal extends Application {

	@Override
	public void start(Stage stage) {

		try {

			// Se carga el FRMX
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Principal.fxml"));
			Parent primeraEscena = (Parent) fxmlLoader.load();

			stage.setTitle("Keyword Autocomplete");
			stage.getIcons().add(getImage("recursos/imagenes/icono.png"));
			stage.initStyle(StageStyle.UNIFIED);

			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			stage.setX((screenBounds.getWidth() - 750) / 2);
			stage.setY((screenBounds.getHeight() - 500) / 2);

			Scene scene = new Scene(primeraEscena);
			stage.setScene(scene);
			stage.show();

			PrincipalController principalController = fxmlLoader.<PrincipalController>getController();
			principalController.setStage(stage);

			setRectangle2D(screenBounds);

			configurarParametros();

		} catch (Exception e) {

			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error: ", e);
		}
	}

	private void configurarParametros() throws IOException {

		URL url = this.getClass().getResource(getRutaCSS() + "alert.css");
		setUrlConfiguracionCSSAlert(url.toExternalForm());

	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
