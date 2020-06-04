/** Clase: Scraper
 * 
 * @version  1.0
 * 
 * @since 08-04-2020
 * 
 * @autor Ing. Jhon Mendez
 *
 * Copyrigth: Luis Perez
 */


package com.keywords.scraper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * Clase que scrapea los resultado obtenidos a partir de una consulta que se realiza 
 * a la plataforma de youtube
 *
 */


public class Scraper {

	private static final  String url = "https://clients1.google.com/complete/search?client=youtube&hl=en&gs_rn=64&gs_ri=youtube&ds=yt&cp=10&gs_id=b2&q=";
	private static final String regex = "\\[\"(.*?)\",";

	/**
	 * 
	 * @param query: Criterio de busqueda
	 * @return Array con las busquedas autocompletadas
	 * @throws Exception
	 */

	public static List<String> autocompleteResults(String query)  throws Exception {

		List<String> resultado = new ArrayList<String>();

		Response response = Jsoup.connect(url + URLEncoder.encode(query, "UTF-8")).execute();
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(response.body());

		while (matcher.find()) 

			resultado.add(matcher.group(1));

		return resultado;
	}

	/**
	 * 
	 * @param criterio: Criterior de consulta
	 * @return Número de videos filtrados
	 * @throws IOException
	 */
	public static int getNumeroVideos(String criterio) throws IOException {

		int resultado = 0;

		String url   = "http://www.youtube.com/results";
		String query = "allintitle:\"" + criterio + "\"";

		Document doc = Jsoup.connect(url)
				.data("search_query", query)
				.userAgent("Mozilla/5.0")
				.get();


		for (Element a : doc.select(".yt-lockup-title > a[title]")) {

			if (isCriterioContieneTodasPalabraTitulo(criterio, a.attr("title")))

				resultado++;


		}

		return resultado;

	}

	private static boolean isCriterioContieneTodasPalabraTitulo(String criterio, String titulo) {

		String pattern = "\\b"+criterio.toLowerCase()+"\\b";
		
		Pattern p=Pattern.compile(pattern);
		
		Matcher m=p.matcher(titulo.toLowerCase());
		
		
		return m.find();

		
	}
	
	public static ArrayList<ProxyAnomimo> getListProxyAnonimos() {
		
	}


}
