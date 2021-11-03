/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.util.ArrayList;
import java.util.Random;
import javafx.util.Pair;

/**
 *
 * @author admin
 */
public class AGE_Clase3_Grupo9 {

    private final Random random;
    private final int longitudLRC;
    private ArrayList<Pair<Integer, Integer>> LRC;
    private ArrayList<Integer> conjunto;
    private ArrayList<ArrayList<Integer>> poblacion;
    private final Archivodedatos archivo;
    private final int tamPoblacion;
    private final int evaluaciones;
    private final float probCruce;
    private final float probMutacion;

    public AGE_Clase3_Grupo9(Random random, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion) {
        this.random = random;
        this.longitudLRC = longitudLRC;
        this.archivo = archivo;
        this.tamPoblacion = tamPoblacion;
        this.evaluaciones = evaluaciones;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
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
        for (int j = 0; j < tamPoblacion; j++) {
            ArrayList<Integer> individuos = new ArrayList<>();
            for (int i = 0; i < conjunto.size(); i++) {
                individuos.add(0);
            }
            ArrayList<Integer> repetidos = new ArrayList<>();
            ArrayList<Integer> posicion = new ArrayList<>();
            int i = 0;
            for (i = 0; i < longitudLRC; i++) {
                Pair<Integer, Integer> aux = LRC.get(i);
                individuos.set(aux.getKey(), aux.getValue());
                repetidos.add(aux.getValue());
                posicion.add(aux.getKey());
            }
            i = 0;
            while (i < conjunto.size()) {
                if (!posicion.contains(i)) {
                    int aleatorio = random.nextInt(conjunto.size());
                    if (!repetidos.contains(aleatorio)) {
                        individuos.set(i, aleatorio);
                        repetidos.add(aleatorio);
                        i++;
                    }
                } else {
                    i++;
                }
            }
            poblacion.add(individuos);
        }
    }

    private int calculaCosteConjunto(ArrayList<Integer> conjunto, int matrizFlujo[][], int matrizDistancia[][]) {
        int coste = 0;
        for (int i = 0; i < conjunto.size(); i++) {
            for (int j = 0; j < conjunto.size(); j++) {
                coste += matrizFlujo[i][j] * matrizDistancia[conjunto.get(i)][conjunto.get(j)];
            }
        }
        return coste;
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
