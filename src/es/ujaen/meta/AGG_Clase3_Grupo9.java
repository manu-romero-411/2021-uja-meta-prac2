/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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

    public void hazGeneticoGeneracional() {
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
        reemplazamiento();
        cruceOX2(seleccionados);
        crucePMX(seleccionados);
        reemplazamiento();

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
        for (int i = 0; i < tamPoblacion; i++) {
            ArrayList<Integer> aux = new ArrayList<>(seleccionados.get(i));
            ArrayList<Integer> torneos = new ArrayList<>();
            boolean aleatorioDiferentes = false;
            boolean estaTorneo = false;
            while (!aleatorioDiferentes && estaTorneo) {
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

                for (int j = 0; j < mejorTorneo(torneos).size(); j++) {
                    aux.set(j, mejorTorneo(torneos).get(j));
                }

                int cont = 0;
                for (int j = 0; j < seleccionados.size() && !estaTorneo; j++) {
                    for (int k = 0; k < aux.size(); k++) {
                        if (seleccionados.get(j).get(k) == aux.get(k)) {
                            cont++;
                        }
                    }
                    if (cont == aux.size()) {
                        estaTorneo = true;
                    }
                }
            }
            seleccionados.add(aux);
        }
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

    private ArrayList<ArrayList<Integer>> cruceOX2(ArrayList<ArrayList<Integer>> seleccionados) {
        ArrayList<ArrayList<Integer>> auxSel = new ArrayList<>();
        for (int j = 0; j < seleccionados.size() - 1; j = j + 2) {

            ArrayList<Integer> padre1 = new ArrayList<>(seleccionados.get(j));
            ArrayList<Integer> padre2 = new ArrayList<>(seleccionados.get(j + 1));

            ArrayList<Integer> listaAleatorios = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                listaAleatorios.set(i, random.nextInt(padre1.size()));
                for (int k = 0; k < i; ++k) {
                    while (listaAleatorios.get(i) == listaAleatorios.get(k)) {
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
        return auxSel;
    }

    private ArrayList<ArrayList<Integer>> crucePMX(ArrayList<ArrayList<Integer>> seleccionados) {
        ArrayList<ArrayList<Integer>> auxSel = new ArrayList<>();
        for (int i = 0; i < seleccionados.size(); i = i + 2) {

            ArrayList<Integer> padre1 = new ArrayList<>(seleccionados.get(i));
            ArrayList<Integer> padre2 = new ArrayList<>(seleccionados.get(i + 1));
            int aleatorioA, aleatorioB;
            do {
                aleatorioA = random.nextInt(seleccionados.get(i).size() - 2) + 1;
                aleatorioB = random.nextInt(seleccionados.get(i).size() - 2) + 1;
            } while (aleatorioA == aleatorioB);
            if (aleatorioA > aleatorioB) {
                int aux;
                aux = aleatorioB;
                aleatorioB = aleatorioA;
                aleatorioA = aux;
            }
            ArrayList<Pair<Integer, Integer>> posiciones = new ArrayList<>();

            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int j = 0; j < padre1.size(); j++) {
                auxVec1.add(-1);
            }

            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int j = 0; j < padre2.size(); j++) {
                auxVec2.add(-1);
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec1.set(posiciones.get(j).fst, posiciones.get(j).snd);
            }

            Queue<Integer> auxQueue1 = new LinkedList<>();

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < padre1.size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec1.size() && !esta; j++) {
                    if (auxVec1.get(j) == padre2.get(contador2 % padre1.size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue1.add(padre2.get(contador2 % padre1.size()));
                }
            }
            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue1.isEmpty(); j++) {
                if (auxVec1.get(j % padre2.size()) == -1) {
                    auxVec1.set(j % padre2.size(), auxQueue1.poll());
                }
            }
            auxSel.add(auxVec1);
            if (probMutacion * evaluaciones >= random.nextInt(101)) {
                mutacion(auxSel.get(i));
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec2.set(posiciones.get(j).snd, posiciones.get(j).fst);
            }

            Queue<Integer> auxQueue2 = new LinkedList<>();

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < padre2.size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec2.size() && !esta; j++) {
                    if (auxVec2.get(j) == padre1.get(contador2 % padre2.size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue2.add(padre1.get(contador2 % padre2.size()));
                }
            }

            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue2.isEmpty(); j++) {
                if (auxVec2.get(j % padre1.size()) == -1) {
                    auxVec2.set(j % padre1.size(), auxQueue2.poll());
                }
            }
            auxSel.add(auxVec2);
            if (probMutacion * evaluaciones >= random.nextInt(101)) {
                mutacion(auxSel.get(i + 1));
            }
        }
        return auxSel;
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
