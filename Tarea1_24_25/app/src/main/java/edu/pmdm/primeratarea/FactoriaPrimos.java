package edu.pmdm.primeratarea;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Un número primo es aquel que tiene dos divisores positivos distintos: él mismo y la unidad.
    El número 1 no es primo ya que tiene un solo divisor positivo: él mismo.
 */

public class FactoriaPrimos {

    private static Integer[] primosBase = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
    private static List<Integer> primos  = new ArrayList<>(Arrays.asList(primosBase));

    public FactoriaPrimos() {    }

    public static int getPrimo(int posicion) {
        int primo = 0;

        // Si es alguno de los primos base, lo devuelve
        if (posicion <= primos.size()){
            primo = primos.get(posicion-1);
            Log.d("Primo", String.valueOf(primo));
            return primo;
        }

        // Buscamos primos hasta alcanzar la posicion
        int ultimaPosicion = primos.size();
        int numEvaluar = primos.get(ultimaPosicion-1);
        int primoBuscado = 0;

        while (primos.size() < posicion){
            numEvaluar += 1;

            if (esPrimo(numEvaluar)){
                primos.add(numEvaluar);
                primoBuscado = numEvaluar;

                ultimaPosicion += 1;
                Log.d("Primo", "Posicion: " + ultimaPosicion + ", primo: " + numEvaluar);
            }
        }
        return primoBuscado;
    }

    /**
     * Un metodo que devuelve true si el número pasado por argumento es primo
     * Un número primo sólo es divisible entre sí mismo y uno.
     */
    private static boolean esPrimo(int numero){
        int divisor = 2;

        while ( divisor < numero){
            if ( numero % divisor == 0){
                return false;
            }
            divisor += 1;
        }
        return true;
    }
}
