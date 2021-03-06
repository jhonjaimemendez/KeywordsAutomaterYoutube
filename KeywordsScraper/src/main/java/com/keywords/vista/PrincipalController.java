package com.keywords.vista;

import static com.keywords.componentes.Utilidades.getLogger;
import static com.keywords.componentes.Utilidades.mostrarAlerta;
import static com.keywords.componentes.Utilidades.getPalabrasSinTilde;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.keywords.componentes.Utilidades;
import com.keywords.controlador.KeywordAutocompleteController;

import edu.keywords.modelo.Video;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class PrincipalController implements Initializable {

	@FXML
	private JFXTextField  tKeywords,tCarpetaSalida,tArchivo,tArchivoSalida,
	tKeywordsProcesada,tLetraProcesada,tKeywordsFaltantes,
	tKeywordsTotales;

	@FXML
	private JFXButton bCargar,bBuscar,bLimpiar;

	@FXML
	private ProgressBar pNumeroVideos;

	private Stage stagePrincipal;

	private Task<Void> task;

	private float valorProgressBar = 0;
	private float incremento;

	private int numeroPalabrasProcesadas;

	private char letra;
	

	private static KeywordAutocompleteController keywordAutocompleteController;


	public void initialize(URL location, ResourceBundle resources) {


		ImageView imageView = Utilidades.getImageView("recursos/imagenes/excel.png");
		bCargar.setGraphic(imageView);

		imageView = Utilidades.getImageView("recursos/imagenes/explorer.png");
		bBuscar.setGraphic(imageView);

		imageView = Utilidades.getImageView("recursos/imagenes/borrar.png");
		bLimpiar.setGraphic(imageView);

		tArchivoSalida.setText(Utilidades.getNombreArchivo());

		tCarpetaSalida.setText(Utilidades.getRutaPorDefecto());


	}

	@FXML
	private void buscarArchivo() {

		String rutaArchivo = mostrarExploradorArchivo();

		if (rutaArchivo != null)

			tArchivo.setText(rutaArchivo);

	}

	@FXML
	private void buscarCarpetaSalida() {

		String ruta = mostrarExploradorDirectorio();

		if (ruta != null)

			tCarpetaSalida.setText(ruta);


	}

	@FXML
	private void limpiarCampo() {

		tArchivo.setText("");
		tKeywords.requestFocus();
	}

	@FXML
	private void terminar() {

		if (task != null && task.isRunning()) {

			mostrarAlerta(AlertType.INFORMATION, "Tarea cancelada exitosamente");

			boolean opcion = mostrarAlerta(AlertType.CONFIRMATION,
					" ¿Desea cancelar la tarea en ejecución?");

			if (opcion) {

				boolean estado = task.cancel();

				if (estado) {

					mostrarAlerta(AlertType.INFORMATION, "Tarea cancelada exitosamente");
					limpiarFormulario();

				}

			}

		} else {

			mostrarAlerta(AlertType.ERROR, "No existe ninguna tarea que se este ejecutando");

		}

		tKeywords.requestFocus();

	}

	@FXML
	private void cerrarAplicacion() {

		cerrar();

	}

	@FXML
	private void iniciar() {


		if (task == null || !task.isRunning()) {

			if (!tKeywords.getText().isEmpty() && !tArchivo.getText().isEmpty()) {

				mostrarAlerta(AlertType.ERROR, "Debe especificar solo una keyword ó un archivo para la busqueda (Especifico los dos)");
				tKeywords.requestFocus();

			} else if (tKeywords.getText().isEmpty() && tArchivo.getText().isEmpty()) {

				mostrarAlerta(AlertType.ERROR, "Debe especificar una keyword ó un archivo para la busqueda");
				tKeywords.requestFocus();

			} else if (tArchivoSalida.getText().isEmpty()) {

				mostrarAlerta(AlertType.ERROR, "Debe especificar el nombre del archivo de salida");
				tArchivoSalida.requestFocus();

			} else if (!tKeywords.getText().isEmpty()) {

				buscarPorKeywords(tKeywords.getText());

			} else if (!tArchivo.getText().isEmpty()) {

				try {

					buscarPorArchivo();

				} catch (Exception e) {

					mostrarAlerta(AlertType.ERROR, "Error " + e);
				}

			}

		} else {

			mostrarAlerta(AlertType.ERROR, "Debe esperar que se termine de ejecutar la tarea que se encuentra en proceso");
		}

	}


	private void buscarPorKeywords(String keyword) {

		incremento = 1/27F;
		valorProgressBar = incremento;
		getKeywordAutocompleteController().iniciarSet();

		task = new Task<Void>() {

			@Override 
			public Void call() {
				try {

					List<Video> videos = new ArrayList<Video>();

					for(letra='a'; letra<='z'; letra++) {

						Platform.runLater(new Runnable() {

							public void run() {

								valorProgressBar +=incremento;
								pNumeroVideos.setProgress(valorProgressBar);
								tKeywordsProcesada.setText(tKeywords.getText());
								tLetraProcesada.setText(""+letra);
								tKeywordsFaltantes.setText("1");
								tKeywordsTotales.setText("1");

							}
						});

						List<Video> video = getKeywordAutocompleteController().getVideoCriterio(getPalabrasSinTilde(keyword),"" + letra);
						videos.addAll(video);

					}

					Utilidades.escribirArchivoCSV(tCarpetaSalida.getText(), tArchivoSalida.getText() + "-PorKeyword-", videos);

				} catch (Exception e) {

					getLogger().log(Level.SEVERE,  "Error en la generación del archivo a partir del archivo" + e);					
					mostrarAlerta(AlertType.ERROR, "Error en la generación del archivo a partir del archivo" + e);

				}

				return null;
			}


		};

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			public void handle(WorkerStateEvent t) {

				Platform.runLater(new Runnable() {

					public void run() {

						getLogger().log(Level.INFO,  "EArchivo csv generado con exito");
						mostrarAlerta(AlertType.INFORMATION, "Archivo csv generado con exito");
						limpiarFormulario();

					}
				});

			}
		});

		task.setOnFailed(new EventHandler<WorkerStateEvent>() {

			public void handle(WorkerStateEvent t) {

				getLogger().log(Level.SEVERE,  "Error al generar el archivo csv");
				mostrarAlerta(AlertType.ERROR, "Error al generar el archivo csv");
				limpiarFormulario();
			}
		});

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}

	private void cerrar() {

		if (task != null && task.isRunning()) {

			boolean opcion = mostrarAlerta(AlertType.CONFIRMATION,
					"Aun se esta ejecutando una tarea. Desea cerrar el aplicativo");

			if (opcion) {

				stagePrincipal.close();
				getLogger().log( Level.INFO ,"Desmontado Aplicativo ");

			}
		} else

			stagePrincipal.close();

	}


	private void buscarPorArchivo() throws Exception {

		FileInputStream excelFile = new FileInputStream(new File(tArchivo.getText()));
		final Workbook workbook = new XSSFWorkbook(excelFile);

		final Sheet datatypeSheet = workbook.getSheetAt(0);
		Float numeroELementos = new Float(datatypeSheet.getLastRowNum());

		if (numeroELementos > 0) {

			final Iterator<Row> iterator = datatypeSheet.rowIterator();

			incremento = 1/numeroELementos;
			valorProgressBar = 0;

			task = new Task<Void>() {

				@Override 
				public Void call()  {

					List<Video> videos = new ArrayList<Video>();
					numeroPalabrasProcesadas = 0;
					
					String encabezadoPrimeraCelda = "";
					String encabezadoSegundaCelda = "";
					String encabezadoTerceraCelda = "";
					String encabezadoCuartaCelda = "";
					
					Cell segundaCelda = null;
					Cell cuartaCelda = null;
					Cell terceraCelda = null;
					
					while (iterator.hasNext()) {

						final Row currentRow = iterator.next();
						numeroPalabrasProcesadas++;
						
						Iterator<Cell> cellIterator = currentRow.iterator();
						
						Cell primeraCelda = cellIterator.next();
						
						if (currentRow.getRowNum( )> 0) {
					
							segundaCelda = null;
							cuartaCelda = null;
							terceraCelda = null;
							
							try {
								segundaCelda = cellIterator.next();
							} catch (Exception e) {}

							try {
								terceraCelda = cellIterator.next();
							} catch (Exception e) {}

							try {
								cuartaCelda = cellIterator.next();

							} catch (Exception e) {}


							if (primeraCelda.getStringCellValue() != null && !primeraCelda.getStringCellValue().isEmpty()) {

								
								final String keywords = primeraCelda.getStringCellValue();

								Platform.runLater(new Runnable() {

									public void run() {

										valorProgressBar +=incremento;
										pNumeroVideos.setProgress(valorProgressBar);
										tKeywordsProcesada.setText(keywords);
										tKeywordsFaltantes.setText("" + (currentRow.getRowNum()));
										tKeywordsTotales.setText(""+ numeroELementos.intValue());

									}
								});


								try {

									List<Video> video = getKeywordAutocompleteController().getVideoCriterioLibro(getPalabrasSinTilde(getValor(primeraCelda)),
											getValor(segundaCelda),getValor(terceraCelda),getValor(cuartaCelda));


									videos.addAll(video);

								} catch (Exception e) {

									getLogger().log(Level.SEVERE,  "Error proceso generado por archivo" +e);

								}

							}
						} else {
	
							encabezadoPrimeraCelda = getValor(primeraCelda);
							
							try {
								segundaCelda = cellIterator.next();
								encabezadoSegundaCelda = getValor(segundaCelda);
								
							} catch (Exception e) {}

							try {
								terceraCelda = cellIterator.next();
								encabezadoTerceraCelda = getValor(terceraCelda);
							} catch (Exception e) {}

							try {
								cuartaCelda = cellIterator.next();
								encabezadoCuartaCelda = getValor(cuartaCelda);
							} catch (Exception e) {}

							
						}
					}

					try {
						Utilidades.escribirArchivoCSVOpcionArchivo(tCarpetaSalida.getText(), tArchivoSalida.getText() + "-PorArchivoKeywords", videos, 
								encabezadoPrimeraCelda, encabezadoSegundaCelda, encabezadoTerceraCelda, encabezadoCuartaCelda);
						
						workbook.close();

					} catch (Exception e) {

						getLogger().log(Level.SEVERE,  "Error escribiendo el archivo en proceso generado por archivo" +e);


					} 

					return null;
				}
			};

			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				public void handle(WorkerStateEvent t) {

					Platform.runLater(new Runnable() {

						public void run() {

							getLogger().log(Level.INFO,  "Archivo csv generado con exito en proceso generado por archivo");
							mostrarAlerta(AlertType.INFORMATION, "Archivo csv generado con exito");
							limpiarFormulario();

						}
					});

				}
			});

			task.setOnFailed(new EventHandler<WorkerStateEvent>() {

				public void handle(WorkerStateEvent t) {

					getLogger().log(Level.SEVERE,  "Error al general el archivo csv" );
					mostrarAlerta(AlertType.ERROR, "Error al general el archivo csv");
					limpiarFormulario();
				}
			});

			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();

		} else {

			mostrarAlerta(AlertType.ERROR, "El numero de filas del archivo en excel debe ser mayor a 1 (Se incluye el header)");
			tKeywords.requestFocus();

		}
	}

	public void setStage(Stage stagePrincipal) {

		this.stagePrincipal = stagePrincipal;

		stagePrincipal.setOnCloseRequest( event -> {

			cerrar();

		});
	}

	private String mostrarExploradorArchivo() {

		String ruta = null;

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS Archivos (*.xls)", "*.xlsx");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Escoja el archivo donde tiene los keywords");
		File file = fileChooser.showOpenDialog(stagePrincipal);

		if (file != null) 
			ruta = file.getPath();

		return ruta;

	}

	private String mostrarExploradorDirectorio() {

		String ruta = null;

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("JavaFX Projects");

		File defaultDirectory = new File("c:/");
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stagePrincipal);


		if (selectedDirectory != null) 
			ruta = selectedDirectory.getPath();

		return ruta;
	}

	private static KeywordAutocompleteController getKeywordAutocompleteController() {

		if (keywordAutocompleteController == null)
			keywordAutocompleteController = new KeywordAutocompleteController();



		return keywordAutocompleteController;
	}

	private void limpiarFormulario() {

		tArchivo.setText("");
		tCarpetaSalida.setText(Utilidades.getRutaPorDefecto());
		tKeywords.setText("");
		tKeywordsFaltantes.setText("");
		tKeywordsProcesada.setText("");
		pNumeroVideos.setProgress(0);
		tKeywordsTotales.setText("");
		tLetraProcesada.setText("");
		tKeywords.requestFocus();

	}

	private String getValor(Cell cell) {

		String resultado = "";

		try {
			
			DataFormatter df = new DataFormatter();
			resultado = df.formatCellValue(cell);
			
		} catch (Exception e) {}

		return resultado;
	}


}
