package edu.pmdm.primeratarea;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Button btnCalcular;
    private EditText etPrimo;
    private TextView tvResultado;
    private ExecutorService ejecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarActionBar();

        etPrimo = findViewById(R.id.etPrimo);
        tvResultado = findViewById(R.id.tvResultado);

        btnCalcular = findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(v -> {
            calcularPrimo();
        });

    }

    private void calcularPrimo() {
        // Ocultar el teclado
        TecladoTools.ocultarTeclado(this);

        // Creamos un hilo para no dejar tostada la GUI
        ejecutor = Executors.newSingleThreadExecutor();

        String valor = etPrimo.getText().toString();

        try {
            // Comprobamos valores de entrada
            int posicion = verificarPosicion(valor);

            // Ejecutamos en otro hilo
            ejecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Obtener el primo de la posicion
                    int primo = FactoriaPrimos.getPrimo(posicion);

                    // Actualizar la UI en el hilo principal
                    // Si no se hace así daría una excepcion 'CalledFromWrongThreadException'
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Construir el resultado
                            StringBuilder sb = new StringBuilder();
                            sb.append("El primo número ").append(posicion)
                                    .append(" es el ").append(primo);

                            tvResultado.setText(sb.toString());
                            etPrimo.setError(null);  // Limpiar errores
                        }
                    });
                }
            });

        } catch (IllegalArgumentException e) {
            // Actualizar la UI en caso de error
            etPrimo.setError(e.getMessage());
            tvResultado.setText(""); // Limpiar el resultado
        } catch (Exception e) {
            // Otro error inesperado
            etPrimo.setError("Error inesperado: " + e.getMessage());
            tvResultado.setText(""); // Limpiar el resultado
        } finally {
            // Asegurar que el ExecutorService se cierra
            ejecutor.shutdown();
        }
    }

    private int verificarPosicion(String valor){
        // Verificamos los casos de entrada
        // 1. Nulo o vacío
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException("La posición no puede ser nula o vacía");
        }

        // 2. Tiene 6 dígitos o más
        if (valor.trim().length() >= 6) {
            throw new IllegalArgumentException("La posición no puede tener 6 o más dígitos");
        }

        // 3. No es un número positivo o mayor de cero
        int posicion = Integer.parseInt(valor);
        if (posicion <= 0) {
            throw new IllegalArgumentException("La posición no puede ser negativa o cero");
        }

        return posicion;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void configurarActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setTitle("Primera tarea");
            actionBar.setSubtitle("Números primos");
            actionBar.setIcon(R.drawable.ic_action_bar);

            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

        }
    }

}