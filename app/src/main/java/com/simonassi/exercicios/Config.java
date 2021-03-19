/*
 * Classe: Config
 *
 * Descrição: Classe usada para setar barras de progresso ao longo da aplicação e evitar
 * repetição de código.
 */


package com.simonassi.exercicios;

import android.view.View;
import android.widget.ProgressBar;

public class Config {

    public static void showProgress(ProgressBar mProgressBar,boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

}
