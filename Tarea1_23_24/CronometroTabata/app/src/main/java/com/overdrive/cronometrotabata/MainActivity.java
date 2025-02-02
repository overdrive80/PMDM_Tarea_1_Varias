package com.overdrive.cronometrotabata;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer beep, gong;
    private ImageButton btnIniciar;
    private EditText txtSeries, txtTrabajo, txtDescanso;
    private int numSeries, tiempoTrabajo, tiempoDescanso;
    private TextView txtCuentaAtras, txtSeriesResta, txtEstado;
    private ConstraintLayout layoutTabata;
    private int seriesRestantes;
    private String msgSeries;
    private boolean enEjecucion = false;
    private long totalExecutionTime = 0;  // Tiempo total de ejecución

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutTabata), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarRecursos();
    }

    private void cargarRecursos() {
        layoutTabata = findViewById(R.id.layoutTabata);

        // Botón. Quitamos el efecto sonoro
        btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setSoundEffectsEnabled(false);

        btnIniciar.setOnClickListener(v -> {
            // Oculta el teclado antes de realizar otras acciones
            ocultarTeclado();

            // Evitamos múltiples pulsaciones
            if (enEjecucion) {
                return;
            }

            boolean datosCorrectos = validarDatos();

            if (datosCorrectos) {
                iniciarCronometro();
            }
        });

        // EDIT TEXT
        txtSeries = findViewById(R.id.txtSeries);
        txtTrabajo = findViewById(R.id.txtTrabajo);
        txtDescanso = findViewById(R.id.txtDescanso);

        // TEXT VIEW
        txtCuentaAtras = findViewById(R.id.txtCuentaATras);
        txtSeriesResta = findViewById(R.id.txtSeriesResta);
        txtEstado = findViewById(R.id.txtEstado);
    }

    private boolean validarDatos() {
        // Recuperamos los valores de los EditText
        try {
            numSeries = Integer.parseInt(txtSeries.getText().toString());
            tiempoTrabajo = Integer.parseInt(txtTrabajo.getText().toString());
            tiempoDescanso = Integer.parseInt(txtDescanso.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los datos introducidos no son válidos", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    private void iniciarCronometro() {
        msgSeries = getString(R.string.seriesresta);
        seriesRestantes = numSeries;
        txtSeriesResta.setText(String.format(Locale.getDefault(),"%s %d",msgSeries ,seriesRestantes));

        // Iniciamos la cuenta atrás de trabajo
        enEjecucion = true;
        btnIniciar.setEnabled(false);

        iniciarSerie("trabajo");
    }

    public void playBeep() {
        if (beep != null) {
            beep.release();
        }
        // Cargar música
        beep = MediaPlayer.create(this, R.raw.beep);
        beep.setVolume(0.5f, 0.5f); // Ajusta el volumen
        beep.start();
    }

    public void playGong() {
        if (gong != null) {
            gong.release();
        }
        // Cargar música
        gong = MediaPlayer.create(this, R.raw.gong);
        gong.setVolume(0.5f, 0.5f); // Ajusta el volumen
        gong.start();
    }

    private void finalizarCronometro() {
        enEjecucion = false;  // Marca que el cronómetro ha terminado

        // Habilitar el botón nuevamente
        btnIniciar.setEnabled(true);

        // Muestra el tiempo total de ejecución en el log
        Toast.makeText(getApplicationContext(), "Tiempo total de ejecución: " + totalExecutionTime / 1000 + " sg", Toast.LENGTH_LONG).show();

        totalExecutionTime = 0;
    }

    private void iniciarSerie(String modo) {
        long serieStartTime = System.currentTimeMillis();  // Marca el tiempo de inicio de la serie actual

        if (modo.equals("trabajo")) {
            txtCuentaAtras.setText(String.valueOf(tiempoTrabajo));
            txtEstado.setText(R.string.estado_trabajo);
            layoutTabata.setBackgroundResource(R.drawable.fondo_app_verde);
            playBeep();

            CountDownTimer crono = new CountDownTimer(tiempoTrabajo * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txtCuentaAtras.setText(String.valueOf(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    long serieEndTime = System.currentTimeMillis();  // Marca el tiempo de fin de la serie
                    totalExecutionTime += (serieEndTime - serieStartTime);  // Acumula el tiempo transcurrido

                    iniciarSerie("descanso");
                }
            };
            crono.start();  // Iniciamos el cronómetro

        } else {
            txtCuentaAtras.setText(String.valueOf(tiempoDescanso));
            txtEstado.setText(R.string.estado_descanso);
            layoutTabata.setBackgroundResource(R.drawable.fondo_app_rojo);
            playBeep();

            CountDownTimer crono = new CountDownTimer(tiempoDescanso * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txtCuentaAtras.setText(String.valueOf(millisUntilFinished / 1000 + 1));
                }

                @Override
                public void onFinish() {
                    long serieEndTime = System.currentTimeMillis();  // Marca el tiempo de fin de la serie
                    totalExecutionTime += (serieEndTime - serieStartTime);  // Acumula el tiempo transcurrido

                    seriesRestantes--;

                    if (seriesRestantes == 0) {
                        txtEstado.setText(R.string.fin);
                        txtSeriesResta.setText(String.valueOf(seriesRestantes));
                        layoutTabata.setBackgroundColor(Color.WHITE);
                        txtCuentaAtras.setText(R.string.tiempo_inicial);
                        playGong();
                        finalizarCronometro();  // Finaliza el cronómetro
                    } else {
                        txtSeriesResta.setText(String.format(Locale.getDefault(),"%s %d",msgSeries ,seriesRestantes));
                        iniciarSerie("trabajo");
                    }
                }
            };
            crono.start();  // Iniciamos el cronómetro
        }
    }

    private void ocultarTeclado() {
        // Obtén el InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        // Obtén la vista actual del enfoque
        View vistaActual = getCurrentFocus();

        // Si hay una vista enfocada, ocultar el teclado
        if (vistaActual != null) {
            imm.hideSoftInputFromWindow(vistaActual.getWindowToken(), 0);
        }
    }

}
