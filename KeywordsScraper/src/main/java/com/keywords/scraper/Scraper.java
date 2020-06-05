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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import edu.keywords.modelo.ProxyAnonimo;
import static com.keywords.componentes.Utilidades.getLogger;

/**
 * 
 * Clase que scrapea los resultado obtenidos a partir de una consulta que se realiza 
 * a la plataforma de youtube
 *
 */


public class Scraper {

	private static final  String url = "https://clients1.google.com/complete/search?client=youtube&hl=en&gs_rn=64&gs_ri=youtube&ds=yt&cp=10&gs_id=b2&q=";
	private static final String regex = "\\[\"(.*?)\",";

	private static WebDriver driver;
	private static List<ProxyAnonimo> proxysAnonimos;

	/**
	 * Se realiza el autocomplete con tres metodos diferentes
	 * 
	 * @param palabra: Keywords
	 * @param letra
	 * @return Array con las busquedas autocompletadas
	 * @throws Exception
	 */
	public static Set<String> autocompleteResults(String keyword, String letra)  
			throws Exception {


		Set<String> resultado = autocompleteResultsOpcionUno(keyword,letra);
		Set<String> resultado1 = autocompleteResultsOpcionDos(keyword,letra);
		Set<String> resultado2 = autocompleteResultsOpcionTres(keyword,letra);

		resultado.addAll(resultado1);
		resultado.addAll(resultado2);

		return resultado;

	}


	/**
	 * Metodo uno de autocomplete se realiza por cliente
	 * @param palabra: Keywords
	 * @param letra
	 * @return Array con las busquedas autocompletadas
	 * @throws Exception
	 */

	public static Set<String> autocompleteResultsOpcionUno(String keyword, String letra )  throws Exception {

		Set<String> resultado = new HashSet<String>();

		Response response = Jsoup.connect(url + URLEncoder.encode(keyword + " " + letra, "UTF-8")).execute();
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(response.body());

		while (matcher.find()) 

			resultado.add(matcher.group(1));

		return resultado;
	}

	/**
	 * Metodo dos de autocomplete se realiza por youtube  
	 * @param keyword
	 * @param letra
	 * @return
	 */
	public static Set<String> autocompleteResultsOpcionDos(String keyword, String letra) {

		Set<String> resultado = new HashSet<String>();

		try {
			String inputText ="input[name=search_query]";
			String nameUl = "sbsb_b";

			System.setProperty("webdriver.chrome.driver","recursos/lib/chromedriver.exe");

			driver.get("https://www.youtube.com/");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(inputText)));

			input.sendKeys(keyword + " ");
			new Actions(driver).moveToElement(input).perform();
			input.sendKeys("" + letra);
			wait.until(ExpectedConditions.elementToBeClickable(By.className(nameUl)));

			WebElement ind = driver.findElement(By.className(nameUl));
			List<WebElement> links = ind.findElements(By.tagName("li"));

			for (int i = 1; i < links.size(); i++) {

				resultado.add(links.get(i).getText());
			}

			input.clear();

		} catch (Exception e) {

			getLogger().log( Level.SEVERE ,"Error autocomplete patron 2 " + e);
		}

		return resultado;

	}


	/**
	 * Metodo tres de autocomplete se realiza por scraping   
	 * @param keyword
	 * @param letra
	 * @return
	 */
	public static Set<String> autocompleteResultsOpcionTres(String keyword, String letra)  {

		Set<String> resultado = null;


		try {

			resultado = getAutocompleteOpcionTres(driver, keyword, letra);

		} catch (Exception e) {

			getLogger().log( Level.SEVERE ,"Error autocomplete patron 3 " + e);
		
		}

		return resultado == null ? new HashSet<String>() : resultado;

	}

	public static Set<String> getAutocompleteOpcionTres(WebDriver driver,
			String palabra, String letra) throws Exception {

		String keyWords ="input[id=keywordInput]";

		driver.get("https://youautocompleteme.io/youtube/");

		Set<String> resultado = new HashSet<String>();

		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(keyWords)));

		input.sendKeys(palabra + " " + letra );
		driver.findElement(By.id("keywordButton")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("keywordButton")));
		wait.until(ExpectedConditions.elementToBeClickable(By.id("downloadButton")));


		for(char letraAux='a'; letraAux<='z'; letraAux++) {

			List<WebElement> div = driver.findElements(By.id("" +letraAux));

			for (WebElement webElement : div) {

				String[] autocompletes = webElement.getText().split("\n");

				for (String sentencias : autocompletes) {
					if (sentencias.length() > 1)
						resultado.add(sentencias);
				}

			}


		}

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

	public static List<ProxyAnonimo> getListProxyAnonimos()  {

		if (proxysAnonimos == null) {

			try {

				proxysAnonimos = new ArrayList<ProxyAnonimo>();

				String url = "https://www.proxynova.com/proxy-server-list/anonymous-proxies/";
				Document documento = Jsoup.connect(url).get();
				Element tabla = documento.select("table").first();

				Elements filas = tabla.select("tr");

				for (Element fila : filas) {

					Elements columnas = fila.select("td");

					if (columnas != null  && columnas.text().length() >0) {

						String direccionIP = columnas.get(0).
								getElementsByTag("script").
								dataNodes().get(0).
								toString().
								replace("document.write('", "").
								replace("');", ""); 

						String puerto = columnas.get(1).text();

						ProxyAnonimo proxyAnonimo = new ProxyAnonimo(direccionIP, Integer.parseInt(puerto));
						proxysAnonimos.add(proxyAnonimo);

					}

				}
			} catch (Exception e) {

				getLogger().log( Level.SEVERE ,"Error generando proxy anonimo " + e);

			} 
		}

		return proxysAnonimos;

	}

	public static Set<String>  buscarOpcionConProxyAnonimos(String palabra, String letra) {

		Set<String> resultado = null;

		System.setProperty("webdriver.chrome.driver","recursos/lib/chromedriver.exe");

		//Se monta prueba con Dos proxys anonimos

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("disable-extensions");

		int index = new Random().nextInt(getListProxyAnonimos().size());
		ProxyAnonimo proxyAnonimo = getListProxyAnonimos().get(index);

		Proxy proxy = new Proxy();
		proxy.setAutodetect(false);
		proxy.setHttpProxy(proxyAnonimo.getDireccionIP() + ":" + proxyAnonimo.getPuerto()); 
		options.setCapability("proxy", proxy); 

		WebDriver driver = new ChromeDriver(options);

		try {

			resultado = getAutocompleteOpcionTres(driver, palabra, letra);

		} catch (Exception e) {

			getLogger().log( Level.SEVERE ,"Error busqueda con proxys anonimos " + e);
		
		}

		driver.close();

		return resultado;

	}

	public static WebDriver iniciarDrivers() {

		if (driver == null) {

			System.setProperty("webdriver.chrome.driver","recursos/lib/chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			options.addArguments("--disable-gpu");
			options.addArguments("disable-extensions");

			driver = new ChromeDriver(options);

		}


		return driver;
	}

	public static void  closeDriver() {

		driver.close();

	}

}
