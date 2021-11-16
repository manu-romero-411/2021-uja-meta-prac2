/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.lang.reflect.Array;
import java.util.*;

import com.sun.tools.javac.util.Pair;

/**
 *
 * @author admin
 */
public class AGE_Clase3_Grupo9 {

    private final Random random;
    private final int longitudLRC;
    private final ArrayList<Pair<Integer, Integer>> LRC;
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
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());

        if (probCruce * 100 >= random.nextInt(101)) {
            cruceOX(seleccionados); //Cruces y mutacion a la vez

        }
        if (probCruce * 100 >= random.nextInt(101)) {
            crucePMX(seleccionados); //Cruces y mutacion a la vez
        }
        System.out.println("POBLACIÓN ANTES:");
        for (int i = 0; i < poblacion.size(); ++i) {
            System.out.print("COSTE " + calculaCosteConjunto(poblacion.get(i)) + " -> ");
            debugMuestraArray(poblacion.get(i));
        }
        reemplazamiento();

        System.out.println("POBLACIÓN AHORA:");
        for (int i = 0; i < poblacion.size(); ++i) {
            System.out.print("COSTE " + calculaCosteConjunto(poblacion.get(i)) + " -> ");
            debugMuestraArray(poblacion.get(i));
        }

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

    private ArrayList<ArrayList<Integer>> seleccion() {
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();
        for (int i = 0; i < vecesSeleccion; i++) {
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

    private void debugMuestraArray(ArrayList<Integer> debug) {
        for (int i = 0; i < debug.size(); i++) {
            System.out.print(debug.get(i) + " ");
        }
        System.out.println("");
    }

    private void debugMuestraMensaje(String debug) {
        System.out.println(debug);
    }

    private void reemplazamiento() {
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();
        for (int i = 0; i < vecesTorneoReemplazamiento; i++) {
            ArrayList<Integer> torneos = new ArrayList<>();
            boolean aleatorioDiferentes = false;
            while (!aleatorioDiferentes) {
                for (int j = 0; j < tamTorneoReemplazamiento; j++) {
                    torneos.add(random.nextInt(tamPoblacion));
                }
                aleatorioDiferentes = true;

                for (int j = 0; j < tamTorneoReemplazamiento && aleatorioDiferentes; j++) {
                    int cont = tamTorneoReemplazamiento - 1;
                    for (int k = j + 1; cont > 0 && aleatorioDiferentes; cont--, k++) {
                        if (torneos.get(j) == torneos.get(k % tamTorneoReemplazamiento)) {
                            aleatorioDiferentes = false;
                        }
                    }
                }
            }
            seleccionados.add(peorTorneo(torneos));
        }
        ArrayList<Pair<Integer, Integer>> indicesPobActualPeores = new ArrayList<>();
        for (int m = 0; m < poblacion.size(); ++m) {
            Pair<Integer, Integer> par = new Pair(m, calculaCosteConjunto(poblacion.get(m)));
            indicesPobActualPeores.add(par);
        }
        sortPairArray(indicesPobActualPeores);

        ArrayList<Pair<Integer, Integer>> indicesSeleccionados = new ArrayList<>();
        for (int m = 0; m < seleccionados.size(); ++m) {
            Pair<Integer, Integer> par = new Pair(m, calculaCosteConjunto(seleccionados.get(m)));
            indicesSeleccionados.add(par);
        }
        sortPairArray(indicesSeleccionados);

        //LOS PEORES VAN AL FINAL
        for (int m = indicesPobActualPeores.size() - 1; m > 0; --m) {
            boolean noreemplazado = true;
            for (int j = 0; j < indicesSeleccionados.size() && noreemplazado; ++j) {
                // LA CONDICIÓN QUE SE DEBE CUMPLIR PARA EL REEMPLAZO ES ESTA
                if (indicesSeleccionados.get(j).snd < indicesPobActualPeores.get(m).snd) {
                    ArrayList<Integer> nuevo = new ArrayList<>();
                    for (int k = 0; k < seleccionados.get(indicesSeleccionados.get(j).fst).size(); ++k) {
                        nuevo.add(seleccionados.get(indicesSeleccionados.get(j).fst).get(k));
                    }
                    poblacion.set(indicesPobActualPeores.get(m).fst, nuevo);
                    indicesSeleccionados.remove(j);
                    noreemplazado = false;
                }
            }
        }
    }

    private void cruceOX(ArrayList<ArrayList<Integer>> seleccionados) {
        for (int i = 0; i < seleccionados.size(); i = i + 2) {
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

            Queue<Integer> auxQueue1 = new LinkedList<>();
            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int j = 0; j < seleccionados.get(i).size(); j++) {
                auxVec1.add(-1);
            }
            Queue<Integer> auxQueue2 = new LinkedList<>();
            ArrayList<Integer> auxVec2 = new ArrayList<>(seleccionados.get(i + 1));
            for (int j = 0; j < seleccionados.get(i + 1).size(); j++) {
                auxVec2.add(-1);
            }

            //Añade los valores de enmedio a la queue
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue1.add(seleccionados.get(i).get(j));
            }

            //Mete los valores de la queue en un vector auxiliar
            for (int j = aleatorioA; !auxQueue1.isEmpty(); j++) {
                auxVec1.set(j, auxQueue1.poll());
            }

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < seleccionados.get(i).size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec1.size() && !esta; j++) {
                    if (auxVec1.get(j) == seleccionados.get(i + 1).get(contador2 % seleccionados.get(i).size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue1.add(seleccionados.get(i + 1).get(contador2 % seleccionados.get(i).size()));
                }
            }

            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue1.isEmpty(); j++) {
                auxVec1.set(j % seleccionados.get(i + 1).size(), auxQueue1.poll());
            }

            //Segundo hijo
            //Añade los valores de enmedio a la queue
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue2.add(seleccionados.get(i + 1).get(j));
            }

            //Mete los valores de la queue en un vector auxiliar
            for (int j = aleatorioA; !auxQueue2.isEmpty(); j++) {
                auxVec2.set(j, auxQueue2.poll());
            }

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < seleccionados.get(i + 1).size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec2.size() && !esta; j++) {
                    if (auxVec2.get(j) == seleccionados.get(i).get(contador2 % seleccionados.get(i + 1).size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue2.add(seleccionados.get(i).get(contador2 % seleccionados.get(i + 1).size()));
                }
            }

            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue2.isEmpty(); j++) {
                auxVec2.set(j % seleccionados.get(i).size(), auxQueue2.poll());
            }

//            //Mutaciones
//            if (probMutacion * evaluaciones >= random.nextInt(101)) {
//                ArrayList<Integer> mutado = new ArrayList<>(auxVec1);
//                mutacion(mutado);
//                seleccionados.set(i + 1, mutado);
//            } else {
//                seleccionados.set(i + 1, auxVec1);
//            }
//            if (probMutacion * evaluaciones >= random.nextInt(101)) {
//                ArrayList<Integer> mutado = new ArrayList<>(auxVec2);
//                mutacion(mutado);
//                seleccionados.set(i, mutado);
//            } else {
//                seleccionados.set(i, auxVec2);
//            }
        }

    }

    private void crucePMX(ArrayList<ArrayList<Integer>> seleccionados) {
        for (int i = 0; i < seleccionados.size(); i = i + 2) {
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
            for (int j = 0; j < seleccionados.get(i).size(); j++) {
                auxVec1.add(-1);
            }

            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int j = 0; j < seleccionados.get(i + 1).size(); j++) {
                auxVec2.add(-1);
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec1.set(posiciones.get(j).fst, posiciones.get(j).snd);
            }

            Queue<Integer> auxQueue1 = new LinkedList<>();

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < seleccionados.get(i).size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec1.size() && !esta; j++) {
                    if (auxVec1.get(j) == seleccionados.get(i + 1).get(contador2 % seleccionados.get(i).size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue1.add(seleccionados.get(i + 1).get(contador2 % seleccionados.get(i).size()));
                }
            }
            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue1.isEmpty(); j++) {
                if (auxVec1.get(j % seleccionados.get(i + 1).size()) == -1) {
                    auxVec1.set(j % seleccionados.get(i + 1).size(), auxQueue1.poll());
                }
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec2.set(posiciones.get(j).snd, posiciones.get(j).fst);
            }

            Queue<Integer> auxQueue2 = new LinkedList<>();

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < seleccionados.get(i + 1).size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec2.size() && !esta; j++) {
                    if (auxVec2.get(j) == seleccionados.get(i).get(contador2 % seleccionados.get(i + 1).size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue2.add(seleccionados.get(i).get(contador2 % seleccionados.get(i + 1).size()));
                }
            }

            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue2.isEmpty(); j++) {
                if (auxVec2.get(j % seleccionados.get(i).size()) == -1) {
                    auxVec2.set(j % seleccionados.get(i).size(), auxQueue2.poll());
                }
            }
        }
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

    private void sortPairArray(ArrayList<Pair<Integer, Integer>> arr) {
        boolean ordenado = false;
        while (!ordenado) {
            if (arr.size() == 2){
                // SI HAY DOS ELEMENTOS EL FOR NO VA BIEN Y ES MEJOR HACER LA COMPARACIÓN A MANO
                if (arr.get(0).snd > arr.get(1).snd){
                    Pair<Integer, Integer> aux = new Pair(arr.get(1).fst, arr.get(1).snd);
                    arr.set(0,arr.get(1));
                    arr.set(1,aux);
                }
                ordenado = true;
            } else {
                if (arr.size() < 2){
                    // NO TIENE SENTIDO ORDENAR SI EN EL ARRAYLIST SOLO HAY UN ELEMENTO
                    ordenado = true;
                } else {
                    if (arr.size() > 2){
                        for (int i = 1; i < arr.size() - 1; ++i) {
                            if (arr.get(i).snd > arr.get(i + 1).snd) {
                                Pair<Integer, Integer> aux = new Pair(arr.get(i).fst, arr.get(i).snd);
                                arr.set(i,arr.get(i+1));
                                arr.set(i+1,aux);
                            }
                            if (arr.get(i).snd < arr.get(i - 1).snd) {
                                Pair<Integer, Integer> aux = new Pair(arr.get(i).fst, arr.get(i).snd);
                                arr.set(i,arr.get(i-1));
                                arr.set(i-1,aux);
                            }
                        }
                        ordenado = true;
                        for (int i = 0; i < arr.size()-1 && ordenado; ++i) {
                            if (arr.get(i).snd > arr.get(i + 1).snd) {
                                ordenado = false;
                            }
                        }
                    }
                }
            }
        }
        System.out.println();
    }
}
