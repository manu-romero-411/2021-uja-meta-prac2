/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import com.sun.tools.javac.util.Pair;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author admin
 */
public class AGE_Clase3_Grupo9 {

    private final Random random;
    private final int longitudLRC;
    private ArrayList<Pair<Integer, Integer>> LRC;
    private ArrayList<Integer> conjunto;
    private final Archivodedatos archivo;

    public AGE_Clase3_Grupo9(Random random, int longitudLRC, Archivodedatos archivo) {
        this.random = random;
        this.longitudLRC = longitudLRC;
        this.archivo = archivo;
    }

    public void hazGeneticoEstacionario() {
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
    }

    private void iniciaConjunto() {
        for (int i = 0; i < archivo.getMatriz1().length; i++) {
            conjunto.add(0);
        }
    }

    private void creaLRC() {
        int i = 0;
        while (i < longitudLRC) {
            int flujo = random.nextInt(archivo.getMatriz1().length);
            int distancia = random.nextInt(archivo.getMatriz2().length);
            Pair<Integer, Integer> aux = new Pair<>(flujo, distancia);
            if (!LRC.contains(aux)) {
                LRC.add(aux);
                i++;
            }
        }
    }

    private void creaPoblacionInicial() {
        ArrayList<Integer> repetidos = new ArrayList<>();
        ArrayList<Integer> posicion = new ArrayList<>();
        int i = 0;
        for (i = 0; i < longitudLRC; i++) {
            Pair<Integer, Integer> aux = LRC.get(i);
            conjunto.set(aux.fst, aux.snd);
            repetidos.add(aux.snd);
            posicion.add(aux.fst);
        }
        i = 0;
        while (i < conjunto.size()) {
            if (!posicion.contains(i)) {
                int aleatorio = random.nextInt(conjunto.size());
                if (!repetidos.contains(aleatorio)) {
                    conjunto.set(i, aleatorio);
                    repetidos.add(aleatorio);
                    i++;
                }
            } else {
                i++;
            }
        }
    }

    private void evolucion() {

    }

    private void seleccion() {

    }

    private void reemplazamiento() {

    }

    private void cruce() {

    }

    private void mutacion() {

    }
}
