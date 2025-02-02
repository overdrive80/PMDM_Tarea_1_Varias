package edu.pmdm.primeratarea;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class TecladoTools {
    /**
     * Oculta el teclado virtual.
     * @version 0.1
     *
     * @param context El contexto de la aplicación.
     */
    public static void ocultarTeclado(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            Window window = ((Activity) context).getWindow();
            View focusView = window.getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            } else {
                // Si no hay una vista con foco, oculta el teclado para la ventana actual
                imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(), 0);
            }
        }
    }
}
