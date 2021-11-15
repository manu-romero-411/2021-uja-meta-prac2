/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

/**
 *
 * @author admin
 */
public class AGG_Clase3_Grupo9 {

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

    public AGG_Clase3_Grupo9(Random random, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
            int vecesSeleccion, int tamTorneoSeleccion, int tamTorneoReemplazamiento) {
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
        this.conjunto = new ArrayList<>();
        this.poblacion = new ArrayList<>();
        this.LRC = new ArrayList<>();
    }

    public void hazGeneticoEstacionario() {
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
        reemplazamiento();
        cruceOX2(seleccionados.get(1), seleccionados.get(2));
        crucePMX(seleccionados.get(1), seleccionados.get(2));
    }

    private void iniciaConjunto() {
        for (int i = 0; i < archivo.getMatriz1().length; i++) {
            conjunto.add(0);
        }
    }

    private void creaLRC() {
        int i = 0;
        while (i < longitudLRC) {
            boolean contenido = false;
            int flujo = random.nextInt(archivo.getMatriz1().length);
            int distancia = random.nextInt(archivo.getMatriz2().length);
            Pair<Integer, Integer> aux = new Pair<>(flujo, distancia);
            for (int j = 0; j < LRC.size() && !contenido; j++) {
                if (LRC.get(j).fst == aux.fst || LRC.get(j).snd == aux.snd) {
                    contenido = true;
                }
            }
            if (!contenido) {
                LRC.add(aux);
                i++;
            }
        }
    }

    private void creaPoblacionInicial() {

        for (int j = 0; j < tamPoblacion; j++) {
            ArrayList<Integer> repetidos = new ArrayList<>();
            ArrayList<Integer> individuos = new ArrayList<>();
            for (int i = 0; i < conjunto.size(); i++) {
                individuos.add(-1);
            }

            for (int i = 0; i < longitudLRC; i++) {
                individuos.set(LRC.get(i).fst, LRC.get(i).snd);
                repetidos.add(LRC.get(i).fst);
            }

            int i = 0;
            while (i < conjunto.size()) {
                if (!repetidos.contains(i)) {
                    boolean diferente = false;
                    int aleatorio = 0;
                    while (!diferente) {
                        aleatorio = random.nextInt(conjunto.size());
                        diferente = true;
                        for (int k = 0; k < individuos.size() && diferente; k++) {
                            if (aleatorio == individuos.get(k)) {
                                diferente = false;
                            }
                        }
                    }
                    individuos.set(i, aleatorio);
                    repetidos.add(i);
                    i++;

                } else {
                    i++;
                }
            }
            poblacion.add(individuos);

        }
    }

    private int calculaCosteConjunto(ArrayList<Integer> conjunto) {
        int coste = 0;
        for (int i = 0; i < conjunto.size(); i++) {
            for (int j = 0; j < conjunto.size(); j++) {
                coste += archivo.getMatriz1()[i][j] * archivo.getMatriz2()[conjunto.get(i)][conjunto.get(j)];
            }
        }
        return coste;
    }

    private ArrayList<Integer> evolucion() {
        ArrayList<Integer> elite = new ArrayList<>(conjunto);
        int mejorValorElite = calculaCosteConjunto(elite);
        for (int i = 0; i < tamPoblacion; i++) {
            if (calculaCosteConjunto(poblacion.get(i)) > mejorValorElite) {
                for (int j = 0; j < elite.size(); j++) {
                    elite.set(i, poblacion.get(i).get(j));
                    mejorValorElite = calculaCosteConjunto(elite);
                }
            }
        }
        return elite;
    }

    private ArrayList<ArrayList<Integer>> seleccion() {
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();
        ArrayList<Integer> torneos = new ArrayList<>();
        boolean aleatorioDiferentes = false;
        while (!aleatorioDiferentes) {
            for (int j = 0; j < tamTorneoSeleccion; j++) {
                torneos.add(random.nextInt(tamPoblacion));
            }
            aleatorioDiferentes = true;

            for (int j = 0; j < tamTorneoSeleccion && aleatorioDiferentes; j++) {
                int cont = tamTorneoSeleccion - 1;
                for (int k = j + 1; cont > 0 && aleatorioDiferentes; cont--, k++) {
                    if (torneos.get(j) == torneos.get(k % tamTorneoSeleccion)) {
                        aleatorioDiferentes = false;
                    }
                }
            }
        }
        seleccionados.add(mejorTorneo(torneos));
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

    private ArrayList<Integer> peorTorneo(ArrayList<Integer> torneos) {
        ArrayList<Integer> peor = new ArrayList<>();
        for (int i = 0; i < conjunto.size(); i++) {
            peor.add(0);
        }
        int peorCoste = Integer.MAX_VALUE;
        for (int i = 0; i < torneos.size(); i++) {
            if (calculaCosteConjunto(poblacion.get(torneos.get(i))) < peorCoste) {
                for (int j = 0; j < poblacion.get(torneos.get(i)).size(); j++) {
                    peor.set(j, poblacion.get(torneos.get(i)).get(j));
                }
                peorCoste = calculaCosteConjunto(poblacion.get(torneos.get(i)));
            }
        }
        return peor;
    }

    private void reemplazamiento() {
                ArrayList<ArrayList<Integer>> nuevaPob = new ArrayList<>();
        ArrayList<ArrayList<Integer>> reemp = new ArrayList<>();
        for (int k = 0; k < tamPoblacion; ++k) {
            for (int i = 0; i < tamTorneoReemplazamiento; ++i) {
                int ale = random.nextInt(tamPoblacion);
                if (!reemp.contains(poblacion.get(ale))) {
                    reemp.add(poblacion.get(ale));
                }
            }

            // ME QUEDO CON EL PEOR
//            nuevaPob.add(reemp.get(indicePeor));
        }
    }

    private void cruceOX2(ArrayList<Integer> padre1, ArrayList<Integer> padre2) {
        ArrayList<Integer> listaAleatorios = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            listaAleatorios.set(i, random.nextInt(padre1.size()));
            for (int j = 0; j < i; ++j) {
                while (listaAleatorios.get(i) == listaAleatorios.get(j)) {
                    listaAleatorios.set(i, random.nextInt(conjunto.size()));
                }
            }
        }

        // ES POSIBLE QUE ESTE BLOQUE SEA REDUNDANTE
        ArrayList<Integer> elementosCorte = new ArrayList<>();
        for (int i = 0; i < padre1.size(); ++i) {
            if (listaAleatorios.contains(padre1.get(i))) {
                elementosCorte.add(padre1.get(i));
            }
        }

        for (int i = 0; i < padre2.size(); ++i) {
            if (elementosCorte.contains(padre2.get(i))) {
                padre2.set(i, -1);
            }
        }
        for (int i = 0; i < padre2.size(); ++i) {
            if (padre2.get(i) == -1) {
                padre2.set(i, elementosCorte.get(0));
                elementosCorte.remove(0);
            }
        }
    }

    private void crucePMX(ArrayList<Integer> padre1, ArrayList<Integer> padre2) {

    }

    private void mutacion(ArrayList<Integer> elementoAMutar) {
        int pos1, pos2;
        do {
            pos1 = random.nextInt(elementoAMutar.size());
            pos2 = random.nextInt(elementoAMutar.size());
        } while (pos1 == pos2);

        int aux = elementoAMutar.get(pos1);
        elementoAMutar.set(pos2, elementoAMutar.get(pos1));
        elementoAMutar.set(pos1, aux);

    }
}
