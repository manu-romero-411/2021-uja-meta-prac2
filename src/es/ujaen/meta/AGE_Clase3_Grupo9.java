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
        ArrayList<ArrayList<Integer>> pobantes = poblacion;
        reemplazamiento();
        ArrayList<ArrayList<Integer>> pobdespues = poblacion;

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
                if (torneos.size() == 0) {
                    for (int j = 0; j < tamTorneoReemplazamiento; j++) {
                        torneos.add(random.nextInt(tamPoblacion));
                    }
                } else {
                    for (int j = 0; j < tamTorneoReemplazamiento; j++) {
                        torneos.set(j, random.nextInt(tamPoblacion));
                    }
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
        System.out.println();
    }

    private ArrayList<ArrayList<Integer>> cruceOX(ArrayList<ArrayList<Integer>> seleccionados) {
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

            Queue<Integer> auxQueue1 = new LinkedList<>();
            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int j = 0; j < padre1.size(); j++) {
                auxVec1.add(-1);
            }
            Queue<Integer> auxQueue2 = new LinkedList<>();
            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int j = 0; j < padre2.size(); j++) {
                auxVec2.add(-1);
            }

            //Añade los valores de enmedio a la queue
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue1.add(padre1.get(j));
            }

            //Mete los valores de la queue en un vector auxiliar
            for (int j = aleatorioA; !auxQueue1.isEmpty(); j++) {
                auxVec1.set(j, auxQueue1.poll());
            }

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioB + 1; contador < padre1.size(); contador++, contador2++) {
                boolean esta = false;
                for (int j = 0; j < auxVec1.size() && !esta; j++) {
                    if (auxVec1.get(j) == padre2.get(contador2 % padre1.size())) {
                        esta = true;
                    }
                }
                if (!esta) {
                    auxQueue1.add(padre1.get(contador2 % padre1.size()));
                }
            }

            //Saca los valores de la queue y los pone en la posicion que este vacia
            for (int j = aleatorioB + 1; !auxQueue1.isEmpty(); j++) {
                auxVec1.set(j % padre2.size(), auxQueue1.poll());
            }

            auxSel.add(auxVec1);

            //Segundo hijo
            //Añade los valores de enmedio a la queue
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue2.add(padre1.get(j));
            }

            //Mete los valores de la queue en un vector auxiliar
            for (int j = aleatorioA; !auxQueue2.isEmpty(); j++) {
                auxVec2.set(j, auxQueue2.poll());
            }

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
                auxVec2.set(j % padre1.size(), auxQueue2.poll());
            }
            auxSel.add(auxVec2);
        }
        if (probMutacion * evaluaciones >= random.nextInt(101)) {
            mutacion(auxSel);
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
            aleatorioA = 5;
            aleatorioB = 6;
            System.out.println("AleatorioA: " + aleatorioA + " AleatorioB: " + aleatorioB);
            ArrayList<Pair<Integer, Integer>> posiciones = new ArrayList<>();

            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int j = 0; j < padre1.size(); j++) {
                auxVec1.add(-1);
            }

            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int j = 0; j < padre2.size(); j++) {
                auxVec2.add(-1);
            }

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                posiciones.add(new Pair<>(seleccionados.get(i).get(j), seleccionados.get(i + 1).get(j)));
            }
//
//            for (int j = 0; j < posiciones.size(); j++) {
//                auxVec1.set(posiciones.get(j).fst, posiciones.get(j).snd);
//            }

            Queue<Integer> auxQueue1 = new LinkedList<>();
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue1.add(padre2.get(j));
            }

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxVec1.set(j, auxQueue1.poll());
            }

            for (int j = aleatorioB + 1, cont = 0; cont < auxVec1.size() - (aleatorioB - aleatorioA + 1); j++, cont++) {

                auxVec1.set(j % auxVec1.size(), padre1.get(j % auxVec1.size()));
//                boolean esta = true;
//                for (int k = 0; k < auxVec1.size() && esta; k++) {
//                    if (auxVec1.get(k) != padre2.get(j) && auxVec1.get(j) == -1) {
//                        auxVec1.set(j, padre2.get(j));
//                        esta = false;
//                    }
//                }
//                if (!esta) {
//                    auxQueue1.add(padre1.get(j));
//                }
            }

            System.out.println("auxVec1");
            debugMuestraArray(padre1);
            debugMuestraArray(padre2);
            debugMuestraArray(auxVec1);
            
            for (int j = 0; j < posiciones.size(); j++) {
                auxVec1.set(posiciones.get(j).snd, posiciones.get(j).fst);
            }


            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
//            for (int contador = 0, contador2 = aleatorioA + 1; contador < padre1.size(); contador++, contador2++) {
//                boolean esta = false;
//                for (int j = 0; j < auxVec1.size() && !esta; j++) {
//                    if (auxVec1.get(j) == padre2.get(contador2 % padre1.size())) {
//                        esta = true;
//                    }
//                }
//                if (!esta) {
//                    auxQueue1.add(padre2.get(contador2 % padre1.size()));
//                }
//            }
//            for (int contador = 0, contador2 = aleatorioA + 1; contador < padre1.size(); contador++, contador2++) {
//                boolean esta = false;
//                for (int j = 0; j < auxVec1.size() && !esta; j++) {
//                    if (auxVec1.get(j) == padre2.get(contador2 % padre1.size())) {
//                        esta = true;
//                    }
//                }
//                if (!esta) {
//                    auxVec1.set(contador2 % padre1.size(), padre2.get(contador2 % padre1.size()));
//                }else{
//                    auxQueue1.add(padre2.get(contador2 % padre1.size()));
//                }
//            }
//            //Saca los valores de la queue y los pone en la posicion que este vacia
////            for (int j = aleatorioA + 1; !auxQueue1.isEmpty(); j++) {
////                if (auxVec1.get(j % padre2.size()) == -1) {
////                    auxVec1.set(j % padre2.size(), auxQueue1.poll());
////                }
////            }
            System.out.println("auxVec1");
            debugMuestraArray(padre1);
            debugMuestraArray(padre2);
            debugMuestraArray(auxVec1);

            auxSel.add(auxVec1);

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec2.set(posiciones.get(j).snd, posiciones.get(j).fst);
            }

            Queue<Integer> auxQueue2 = new LinkedList<>();

            //Comprueba si esta metido en el vector auxiliar respecto el segundo seleccionado
            for (int contador = 0, contador2 = aleatorioA + 1; contador < padre2.size(); contador++, contador2++) {
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
            for (int j = aleatorioA + 1; !auxQueue2.isEmpty(); j++) {
                if (auxVec2.get(j % padre1.size()) == -1) {
                    auxVec2.set(j % padre1.size(), auxQueue2.poll());
                }
            }
            System.out.println("auxVec2");

            debugMuestraArray(padre1);
            debugMuestraArray(padre2);
            debugMuestraArray(auxVec2);
            auxSel.add(auxVec2);

        }

        if (probMutacion * evaluaciones >= random.nextInt(101)) {
            mutacion(auxSel);
        }

        return auxSel;
    }

    private void mutacion(ArrayList<ArrayList<Integer>> elementoAMutar) {
        for (int i = 0; i < elementoAMutar.size(); i++) {
            int pos1, pos2;
            do {
                pos1 = random.nextInt(elementoAMutar.size());
                pos2 = random.nextInt(elementoAMutar.size());
            } while (pos1 == pos2);

            int aux = elementoAMutar.get(i).get(pos1);
            elementoAMutar.get(i).set(pos2, elementoAMutar.get(i).get(pos1));
            elementoAMutar.get(i).set(pos1, aux);
        }
    }

    private void sortPairArray(ArrayList<Pair<Integer, Integer>> arr) {
        boolean ordenado = false;
        while (!ordenado) {
            if (arr.size() == 2) {
                // SI HAY DOS ELEMENTOS EL FOR NO VA BIEN Y ES MEJOR HACER LA COMPARACIÓN A MANO
                if (arr.get(0).snd > arr.get(1).snd) {
                    Pair<Integer, Integer> aux = new Pair(arr.get(1).fst, arr.get(1).snd);
                    arr.set(0, arr.get(1));
                    arr.set(1, aux);
                }
                ordenado = true;
            } else {
                if (arr.size() < 2) {
                    // NO TIENE SENTIDO ORDENAR SI EN EL ARRAYLIST SOLO HAY UN ELEMENTO
                    ordenado = true;
                } else {
                    if (arr.size() > 2) {
                        for (int i = 1; i < arr.size() - 1; ++i) {
                            if (arr.get(i).snd > arr.get(i + 1).snd) {
                                Pair<Integer, Integer> aux = new Pair(arr.get(i).fst, arr.get(i).snd);
                                arr.set(i, arr.get(i + 1));
                                arr.set(i + 1, aux);
                            }
                            if (arr.get(i).snd < arr.get(i - 1).snd) {
                                Pair<Integer, Integer> aux = new Pair(arr.get(i).fst, arr.get(i).snd);
                                arr.set(i, arr.get(i - 1));
                                arr.set(i - 1, aux);
                            }
                        }
                        ordenado = true;
                        for (int i = 0; i < arr.size() - 1 && ordenado; ++i) {
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
