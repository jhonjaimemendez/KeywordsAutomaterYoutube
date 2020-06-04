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
	private int numeroVideoRelacionados;
	private char letra; //Letra por la cual se esta buscando el criterio
	
	
	public Video(String criterio, int numeroVideoRelacionados, char letra) {
		
		this.criterio = criterio;
		this.numeroVideoRelacionados = numeroVideoRelacionados;
		this.letra = letra;
	}


	public String getCriterio() {
		return  criterio;
	}


	public int getNumeroVideoRelacionados() {
		return numeroVideoRelacionados;
	}


	public char getLetra() {
		return letra;
	}
	
	@Override
	public String toString() {
	
		return "Criterio: " + criterio + " " +" Numero Videos: " + numeroVideoRelacionados;
	}
}
