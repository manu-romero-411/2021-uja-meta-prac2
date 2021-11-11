/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.Objects;
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
    private ArrayList<ArrayList<Integer>> poblacion;
    private final Archivodedatos archivo;
    private final int tamPoblacion;
    private final int evaluaciones;
    private final float probCruce;
    private final float probMutacion;
    private final int vecesSeleccion;
    private final int tamTorneoSeleccion;
    private final int tamTorneoReemplazamiento;
    private final int vecesTorneoReemplazamiento;

    public AGE_Clase3_Grupo9(Random random, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
            int vecesSeleccion, int tamTorneoSeleccion, int tamTorneoReemplazamiento, int vecesTorneoReemplazamiento) {
        this.random = random;
        this.longitudLRC = longitudLRC;
        this.archivo = archivo;
        this.tamPoblacion = tamPoblacion;
        this.evaluaciones = evaluaciones;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.vecesSeleccion = vecesSeleccion;
        this.tamTorneoSeleccion = tamTorneoSeleccion;
        this.tamTorneoReemplazamiento = tamTorneoReemplazamiento;
        this.vecesTorneoReemplazamiento = vecesTorneoReemplazamiento;
        this.conjunto = new ArrayList<>();
        this.poblacion = new ArrayList<>();
        this.LRC = new ArrayList<>();
    }

    public void hazGeneticoEstacionario() {
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        Pair<ArrayList<Integer>, ArrayList<Integer>> aux = evolucion();
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
        reemplazamiento();
        cruceOX();
        crucePMX();
        mutacion();

    }

    private void iniciaConjunto() {
        for (int i = 0; i < archivo.getMatriz1().length; i++) {
            conjunto.add(0);
        }

        //DEBUG
        System.out.print("Inicializando conjunto solución: ");
        for (int i = 0; i < conjunto.size(); ++i){
            System.out.print(conjunto.get(i) + " | ");
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

        System.out.println("\nLRC: ");
        for (int j = 0; j < longitudLRC; ++j){
            System.out.println(LRC.get(j).fst + " | "+ LRC.get(j).snd);
        }
    }

    private void creaPoblacionInicial() {
        for (int j = 0; j < tamPoblacion; j++) {
            ArrayList<Integer> individuos = new ArrayList<>();
            for (int i = 0; i < conjunto.size(); i++) {
                individuos.add(0);
            }

            //DEBUG
            System.out.print("\nIniciar individuos: ");
            for (int i = 0; i < individuos.size(); ++i){
                System.out.print(individuos.get(i) + " | ");
            }

            ArrayList<Integer> repetidos = new ArrayList<>();
            ArrayList<Integer> posicion = new ArrayList<>();
            int i = 0;
            for (i = 0; i < longitudLRC; i++) {
                Pair<Integer, Integer> aux = LRC.get(i);
                individuos.set(aux.fst, aux.snd);
                repetidos.add(aux.snd);
                posicion.add(aux.fst);
            }


            //DEBUG
            System.out.print("\nRepetidos: ");
            for (int k = 0; k < repetidos.size(); ++k){
                System.out.print(repetidos.get(k) + " | ");
            }

            //DEBUG
            System.out.print("\nPosicion: ");
            for (int k = 0; k < posicion.size(); ++k){
                System.out.print(posicion.get(k) + " | ");
            }

            //DEBUG
            System.out.print("\nIndividuos ahora: ");
            for (int k = 0; k < individuos.size(); ++k){
                System.out.print(individuos.get(k) + " | ");
            }

            i = 0;
            while (i < conjunto.size()) {
                if (!posicion.contains(i)) {
                    //DEBUG
                    System.out.println("\nPosición " + i + " no contenida en LRC. Lanzar aleatorio");

                    int aleatorio = random.nextInt(conjunto.size());
                    if (i == 17){
                        System.out.println();
                    }
                    //DEBUG
                    System.out.println("Escogida posición " + aleatorio);

                    if (!repetidos.contains(aleatorio)) {
                        //DEBUG
                        System.out.println(aleatorio);
                        System.out.println("Posición " + aleatorio + " no contenida en LRC. Poner en posición " + i + " de individuos");

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
        //DEBUG
        System.out.print("\nPoblación: ");
        for (int k = 0; k < poblacion.size(); ++k){
            for(int l = 0; l < poblacion.get(k).size(); ++l) {
                System.out.print(poblacion.get(k).get(l) + " | ");
            }
            System.out.println();
        }
        System.out.print("");
    }

    private int calculaCosteConjunto(ArrayList<Integer> conjunto) {
        int coste = 0;
        for (int i = 0; i < conjunto.size(); i++) {
            for (int j = 0; j < conjunto.size(); j++) {
                coste += archivo.getMatriz1()[i][j] * archivo.getMatriz2()[conjunto.get(i)][conjunto.get(j)];
            }
        }
        //DEBUG
        System.out.println("Coste de este conjunto: " + coste);
        for (int i = 0; i < conjunto.size(); ++i){
            System.out.print(conjunto.get(i) + " | ");
        }
        System.out.println();
        return coste;
    }

    private Pair<ArrayList<Integer>, ArrayList<Integer>> evolucion() {
        ArrayList<Integer> arrayMenor1 = new ArrayList<>();
        ArrayList<Integer> arrayMenor2 = new ArrayList<>();
        int menor1 = Integer.MAX_VALUE;
        int menor2 = Integer.MAX_VALUE;
        for (int i = 0; i < tamPoblacion; i++) {
            ArrayList<Integer> aux = new ArrayList<>(poblacion.get(i));
            if (calculaCosteConjunto(aux) < menor1 && !arrayMenor1.containsAll(arrayMenor2)) {
                menor1 = calculaCosteConjunto(aux);
                for (int j = 0; j < aux.size(); j++) {
                    arrayMenor1.set(i, aux.get(i));
                }
            }
            if (calculaCosteConjunto(aux) < menor2 && !arrayMenor2.containsAll(arrayMenor1)) {
                menor2 = calculaCosteConjunto(aux);
                for (int j = 0; j < aux.size(); j++) {
                    arrayMenor2.set(i, aux.get(i));
                }
            }
        }
        //DEBUG
        System.out.println("Array1 evolución:");
        for (int i = 0; i < arrayMenor1.size(); ++i){
            System.out.print(arrayMenor1.get(i) + " | ");
        }
        System.out.println("Array2 evolución:");
        for (int i = 0; i < arrayMenor2.size(); ++i){
            System.out.print(arrayMenor2.get(i) + " | ");
        }

        return new Pair<>(arrayMenor1, arrayMenor2);
    }

    private ArrayList<ArrayList<Integer>> seleccion() {
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();
        for (int i = 0; i < vecesSeleccion; i++) {
            ArrayList<Integer> torneos = new ArrayList<>();
            boolean aleatorioDiferentes = false;
            while (!aleatorioDiferentes) {
                for (int j = 0; j < tamTorneoSeleccion; j++) {
                    torneos.add(random.nextInt(tamPoblacion));
                    System.out.println(random.nextInt(tamPoblacion));
                }
                aleatorioDiferentes = true;

                for (int j = 0; j < tamTorneoSeleccion && aleatorioDiferentes; j++) {
                    int cont = tamTorneoSeleccion - 1;
                    for (int k = j + 1; cont > 0 && aleatorioDiferentes; cont--, k++) {
                        if (Objects.equals(torneos.get(j), torneos.get(k % tamTorneoSeleccion))) {
                            aleatorioDiferentes = false;
                            System.out.println(torneos.get(j) + " " + torneos.get(k));
                        }
                    }
                }
            }
            seleccionados.add(mejorTorneo(torneos));
        }

        //DEBUG
        System.out.println("Seleccionados en torneo:");
        for (int k = 0; k < poblacion.size(); ++k){
            for(int l = 0; l < poblacion.get(k).size(); ++l) {
                System.out.print(poblacion.get(k).get(l) + " | ");
            }
            System.out.println();
        }
        System.out.print("");
        return seleccionados;
    }

    private ArrayList<Integer> mejorTorneo(ArrayList<Integer> torneos) {
        ArrayList<Integer> mejor = new ArrayList<>();
        for (int i = 0; i < conjunto.size(); i++) {
            mejor.add(0);
        }
        int mejorCoste = Integer.MIN_VALUE;
        for (int i = 0; i < torneos.size(); i++) {
            if (calculaCosteConjunto(poblacion.get(torneos.get(i))) > mejorCoste) {
                for (int j = 0; j < poblacion.get(torneos.get(i)).size(); j++) {
                    mejor.set(j, poblacion.get(torneos.get(i)).get(j));
                }
                mejorCoste = calculaCosteConjunto(poblacion.get(torneos.get(i)));
            }
        }
        return mejor;
    }

    private void reemplazamiento() {

    }

    private void cruceOX() {

    }

    private void crucePMX() {

    }

    private void mutacion() {

    }
}
