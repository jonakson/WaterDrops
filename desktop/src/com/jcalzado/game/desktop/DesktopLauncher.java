package com.jcalzado.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jcalzado.game.WaterDrops;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// Fijamos el nombre, ancho y alto de la ventana de ejecución.
		config.title = "Water Drops";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new WaterDrops(), config);
	}
}
