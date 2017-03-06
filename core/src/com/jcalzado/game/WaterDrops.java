package com.jcalzado.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class WaterDrops extends ApplicationAdapter {

	private Texture imagenGota;
	private Texture imagenCubo;
	private Sound sonidoGota;
	private Music musicaLluvia;
	private OrthographicCamera camara;
	private SpriteBatch batch;
	private Rectangle cubo;
    private Array<Rectangle> gotasLluvia;
    private long tiempoUltimaGota;
    private BitmapFont textoPuntuacion;
    private int contadorPuntos;
	
	@Override
	public void create () {
		// Carga de las imágenes de la Gota y del Cubo.
		imagenGota = new Texture(Gdx.files.internal("gota.png"));
		imagenCubo = new Texture(Gdx.files.internal("cubo.png"));

		// Carga del sonido de la Gota y el de la Lluvia.
		sonidoGota = Gdx.audio.newSound(Gdx.files.internal("gota.wav"));
		musicaLluvia = Gdx.audio.newMusic(Gdx.files.internal("lluvia.mp3"));

		// Inicio del bucle con la música de fondo.
		musicaLluvia.setLooping(true);
		musicaLluvia.play();

		// Creación la cámara.
		camara = new OrthographicCamera();
		camara.setToOrtho(false, 800, 480);

		// Creación el SpriteBatch.
		batch = new SpriteBatch();

		// Creación e inicialización el rectángulo del Cubo.
		cubo = new Rectangle();
		cubo.x = 800/2 - 64/2; // Centramos el centro del Cubo (64/2) en el centro de la pantalla.
		cubo.y = 0;
		cubo.width = 64;
		cubo.height = 64;

        // Creación del Array que contiene las Gotas.
        gotasLluvia = new Array<Rectangle>();

        // Lanzamiento de la primera Gota.
        lanzarGota();

        // Creación de la fuente para mostrar la puntuación.
        textoPuntuacion = new BitmapFont();

        // Inicialización del contador de puntos
        contadorPuntos = 0;
    }

	@Override
	public void render () {
		// Color de fondo de la pantalla.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);

		// Limpeza del búfer de la la tarjeta gráfica.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Actualización de la cámara.
		camara.update();

		// Se indica al batch que se usará el sistema de coordenadas dado por la cámara.
		batch.setProjectionMatrix(camara.combined);

		batch.begin(); // INICIO DEL RENDERIZADO.
        // Renderizado del Cubo.
        batch.draw(imagenCubo, cubo.x, cubo.y);
        // Renderizado de las Gotas.
        for (Rectangle gota: gotasLluvia) {
            batch.draw(imagenGota, gota.x, gota.y);
        }
        // Renderizado de la puntuación.
        textoPuntuacion.draw(batch, "PUNTUACIÓN: " + contadorPuntos, 24, Gdx.graphics.getHeight()-24);
		batch.end(); // FIN DEL RENDERIZADO.

        // Captura de la entrada (Táctil o Ratón) del jugador para mover el Cubo.
        if (Gdx.input.isTouched()) {
            Vector3 posicionTocada = new Vector3();
            posicionTocada.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camara.unproject(posicionTocada);
            cubo.x = posicionTocada.x - 64/2;
            cubo.y = posicionTocada.y - 64/2;
        }

        // Captura de la entrada (Teclado) del jugador para mover el Cubo.
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cubo.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cubo.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cubo.y += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cubo.y -= 200 * Gdx.graphics.getDeltaTime();
        }

        // Delimitación de movimiento del cubo en base a los bordes de la pantalla.
        if (cubo.x < 0) cubo.x = 0;
        if (cubo.x > 800 - 64) cubo.x = 800 - 64;
        if (cubo.y < 0) cubo.y = 0;
        if (cubo.y > 480 - 64) cubo.y = 480 - 64;

        // Cálculo del lanzamiento de la última Gota y lanzamiento de la siguiente.
        if (TimeUtils.nanoTime() - tiempoUltimaGota > 1000000000) {
            lanzarGota();
        }

        // Se hace que las Gotas caigan y al tocar el suelo o el Cubo se eliminan.
        Iterator<Rectangle> iterador = gotasLluvia.iterator();
        while (iterador.hasNext()) {
            Rectangle gotaLluvia = iterador.next();
            gotaLluvia.y -= 200 * Gdx.graphics.getDeltaTime();
            if (gotaLluvia.y + 64 < 0) {
                iterador.remove();
            }
            if (gotaLluvia.overlaps(cubo)) {
                sonidoGota.play();
                iterador.remove();
                contadorPuntos++;
            }
        }
	}
	
	@Override
	public void dispose () {
        imagenGota.dispose();
        imagenCubo.dispose();
        sonidoGota.dispose();
        musicaLluvia.dispose();
        batch.dispose();
        textoPuntuacion.dispose();
    }

    private void lanzarGota() {
        Rectangle gotaLluvia = new Rectangle();
        gotaLluvia.x = MathUtils.random(0, 800-64);
        gotaLluvia.y = 480;
        gotaLluvia.width = 64;
        gotaLluvia.height = 64;
        gotasLluvia.add(gotaLluvia);
        tiempoUltimaGota = TimeUtils.nanoTime();
    }
}
