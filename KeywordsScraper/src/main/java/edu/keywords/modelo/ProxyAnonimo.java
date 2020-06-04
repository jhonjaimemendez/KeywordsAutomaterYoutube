package edu.keywords.modelo;

public class ProxyAnonimo {

	private String direccionIP;
	private int puerto;
	
	public ProxyAnonimo(String direccionIP, int puerto) {
		
		this.direccionIP = direccionIP;
		this.puerto = puerto;
	
	}

	public String getDireccionIP() {
		return direccionIP;
	}

	public int getPuerto() {
		return puerto;
	}
	
}
