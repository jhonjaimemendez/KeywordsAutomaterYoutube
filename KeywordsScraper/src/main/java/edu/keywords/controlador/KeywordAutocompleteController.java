/** Clase: KeywordAutocompleteController
 * 
 * @version  1.0
 * 
 * @since 08-04-2020
 * 
 * @autor Ing. Jhon Mendez
 *
 * Copyrigth: Luis Perez
 */

package edu.keywords.controlador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.keywords.scraper.Scraper;

import edu.keywords.modelo.Video;

/**
 * 
 * Define las operaciones que debe realizar el Keywords Autocompleter Youtube
 *
 */

public class KeywordAutocompleteController {

	private Set<String> autoCompleteSearch;

	/**
	 * 
	 * @param keyword: Criterio de busqueda
	 * @return List con los datos de cada criterio y palabra
	 * @throws Exception
	 */
	public List<Video> getVideoCriterio(String keyword,  String letra) throws Exception {

		List<Video> videos = new ArrayList<Video>();

		Scraper.iniciarDrivers();

		Set<String> resultadoAutocomplete = 
				Scraper.autocompleteResults(keyword, letra);


		for (String resultado : resultadoAutocomplete) {

			if (autoCompleteSearch.add(resultado.toLowerCase())) {

				int numeroVideoRelacionados = Scraper.getNumeroVideos(resultado);

				Video video = new Video(resultado, numeroVideoRelacionados , letra,keyword);
				videos.add(video);

			}

		}


		return videos;
	}

	/**
	 * 
	 * @param keyword: Criterio de busqueda
	 * @return List con los datos de cada criterio y palabra
	 * @throws Exception
	 */
	public List<Video> getVideoCriterio(String keyword) throws Exception {

		List<Video> videos = new ArrayList<Video>();

		Scraper.iniciarDrivers();

		Set<String> resultadoAutocomplete = 
				Scraper.autocompleteResults(keyword, "");


		resultadoAutocomplete.add(keyword);
		
		for (String resultado : resultadoAutocomplete) {

			if (autoCompleteSearch.add(resultado.toLowerCase())) {

				int numeroVideoRelacionados = Scraper.getNumeroVideos(resultado);

				Video video = new Video(resultado, numeroVideoRelacionados , "",keyword);
				videos.add(video);

			}

		}


		return videos;
	}


	public void iniciarSet() {

		autoCompleteSearch = new HashSet<String>();

	}


}
