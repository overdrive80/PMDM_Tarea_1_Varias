package edu.uoc.countdowntimerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtCuentaAtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCuentaAtras=findViewById(R.id.txtCountDown);
        iniciaCuentaAtras();
    }
    private void iniciaCuentaAtras(){
        CountDownTimer crono=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Actualiza el TextView con la cantidad de segundos (milisegundos/1000) restantes
                txtCuentaAtras.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                //fin de la cuenta atrás
                txtCuentaAtras.setText("terminado!");
            }
        };
        crono.start();  //iniciamos el crono
    }
}