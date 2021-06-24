package gomez.main;

import gomez.utilizacion.Accion;

public class AccionUno implements Accion {
	@Override
	public void ejecutar() {
		System.out.println("Ejecutando AccionUno...");
	}

	@Override
	public String nombreItemMenu() {
		return "Accion 1";
	}

	@Override
	public String descripcionItemMenu() {
		return "Esto es para traer los twitts de Maradona...";
	}

}
