/** Clase: Video
 * 
 * @version  1.0
 * 
 * @since 08-04-2020
 * 
 * @autor Ing. Jhon Mendez
 *
 * Copyrigth: Luis Perez
 */

package edu.keywords.modelo;

/**
 * 
 * Modelo de datos para la consulta del Keywords Autocompleter Youtube 
 *
 */

public class Video {

	private String criterio;
	private int numeroVideo;
	private int numeroVideoRelacionados;
	private String letra; //Letra por la cual se esta buscando el criterio
	private String keywords;
	private String datoSegundaColumnaExcel;
	private String datoTerceraColumnaExcel;
	private String datoCuartaColumnaExcel;


	public Video(String criterio, int numeroVideo, int numeroVideoRelacionados,String letra) {

		this.criterio = criterio;
		this.numeroVideo = numeroVideo;
		this.numeroVideoRelacionados = numeroVideoRelacionados;
		this.letra = letra;
	}

	public Video(String criterio, int numeroVideo, int numeroVideoRelacionados, String letra,String keywords) {

		this.criterio = criterio;
		this.numeroVideo = numeroVideo;
		this.letra = letra;
		this.numeroVideoRelacionados = numeroVideoRelacionados;
		this.keywords = keywords;
	}
	
	
	

	public Video(String criterio, int numeroVideo, int numeroVideoRelacionados, String keywords,
			String datoSegundaColumnaExcel, String datoTerceraColumnaExcel, String datoCuartaColumnaExcel) {
		
		
		this.criterio = criterio;
		this.numeroVideo = numeroVideo;
		this.numeroVideoRelacionados = numeroVideoRelacionados;
		this.keywords = keywords;
		this.datoSegundaColumnaExcel = datoSegundaColumnaExcel;
		this.datoTerceraColumnaExcel = datoTerceraColumnaExcel;
		this.datoCuartaColumnaExcel = datoCuartaColumnaExcel;
	}

	public String getCriterio() {
		return  criterio;
	}


	public int getNumeroVideo() {
		return numeroVideo;
	}


	public String getLetra() {
		return letra;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	

	public String getDatoSegundaColumnaExcel() {
		return datoSegundaColumnaExcel;
	}

	public String getDatoTerceraColumnaExcel() {
		return datoTerceraColumnaExcel;
	}

	public String getDatoCuartaColumnaExcel() {
		return datoCuartaColumnaExcel;
	}
	
	public int getNumeroVideoRelacionados() {
		return numeroVideoRelacionados;
	}

	@Override
	public String toString() {

		return "Criterio: " + criterio + " " +" Numero Videos: " + numeroVideo;
	}
}
