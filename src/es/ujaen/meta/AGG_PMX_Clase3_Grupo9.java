/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

/**
 *
 * @author admin
 */
public class AGG_PMX_Clase3_Grupo9 {

    private final Random random;
    private final long seed;
    private Log log;
    private final String modoLog;
    private final int longitudLRC;
    private final ArrayList<Pair<Integer, Integer>> LRC;
    private final ArrayList<Integer> conjunto;
    private final ArrayList<ArrayList<Integer>> poblacion;
    private final Archivodedatos archivo;
    private final int tamPoblacion;
    private final int evaluaciones;
    private final float probCruce;
    private final float probMutacion;
    private final int vecesSeleccion;
    private final int tamTorneoSeleccion;
    private final ArrayList<Integer> elite;

    public AGG_PMX_Clase3_Grupo9(Random random, long seed, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
            int vecesSeleccion, int tamTorneoSeleccion, String modoLog) {
        this.random = random;
        this.seed = seed;
        this.longitudLRC = longitudLRC;
        this.archivo = archivo;
        this.tamPoblacion = tamPoblacion;
        this.evaluaciones = evaluaciones;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.vecesSeleccion = vecesSeleccion;
        this.tamTorneoSeleccion = tamTorneoSeleccion;
        this.conjunto = new ArrayList<>();
        this.poblacion = new ArrayList<>();
        this.LRC = new ArrayList<>();
        this.elite = new ArrayList<>();
        this.log = null;
        this.modoLog = modoLog;
    }

    private void inicializaElite() {
        for (int i = 0; i < archivo.getMatriz1().length; ++i) {
            elite.add(-1);
        }
    }

    public void hazGeneticoGeneracional() {
        inicializaElite();
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        guardarLog(-1);
        for (int i = 0; i < (evaluaciones); ++i) {
            ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
            if (random.nextFloat() < probCruce) {
                crucePMX(seleccionados);
            }
            reemplazamiento(seleccionados);
            System.out.println("\nGeneración " + i + " generada");
        }

        guardarLog(evaluaciones - 1);
        int costeMin = Integer.MAX_VALUE;
        int mejorSol = -1;
        for (int i = 0; i < poblacion.size(); ++i) {
            int costeSel = calculaCosteConjunto(poblacion.get(i));
            if (costeSel < costeMin) {
                costeMin = costeSel;
                mejorSol = i;
            }
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

        nuevaElite(poblacion);
    }

    private void nuevaElite(ArrayList<ArrayList<Integer>> poblacion1) {
        int costeMin = Integer.MAX_VALUE;
        int eliteIt = -1;
        for (int i = 0; i < poblacion1.size(); ++i) {
            int costeBuscado = calculaCosteConjunto(poblacion1.get(i));
            if (costeBuscado < costeMin) {
                eliteIt = i;
                costeMin = costeBuscado;
            }
        }

        for (int i = 0; i < poblacion1.get(eliteIt).size(); ++i) {
            elite.set(i, poblacion1.get(eliteIt).get(i));
        }
    }

    private void cambiaAElite() {
        int indice = 0;
        int peorCoste = Integer.MAX_VALUE;
        for (int i = 0; i < poblacion.size(); i++) {
            if (peorCoste > calculaCosteConjunto(poblacion.get(i))) {
                indice = i;
                peorCoste = calculaCosteConjunto(poblacion.get(i));
            }
        }
        for (int i = 0; i < poblacion.get(indice).size(); i++) {
            poblacion.get(indice).set(i, elite.get(i));
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
        System.out.println();
        nuevaElite(poblacion);
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();

        ArrayList<Integer> torneos = new ArrayList<>();
        while (seleccionados.size() < vecesSeleccion) {
            do {
                torneos = generadorArrayIntAleatorios(tamTorneoSeleccion, tamPoblacion);
            } while (!arrayIntAleatoriosGeneradoBien(torneos));
            ArrayList<Integer> ganador = new ArrayList<>(mejorTorneo(torneos));
            ganador = mejorTorneo(torneos);

            seleccionados.add(ganador);

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

    private void reemplazamiento(ArrayList<ArrayList<Integer>> nuevaPob) {
        for (int i = 0; i < nuevaPob.size(); i++) {
            for (int j = 0; j < nuevaPob.get(i).size(); j++) {
                poblacion.get(i).set(j, nuevaPob.get(i).get(j));
            }
        }

        boolean estaElite = false;
        for (int i = 0; i < poblacion.size() && !estaElite; i++) {
            int contador = 0;
            for (int j = 0; j < poblacion.get(i).size(); j++) {
                if (poblacion.get(i).get(j) == elite.get(j)) {
                    contador++;
                }
            }
            if (contador == elite.size()) {
                estaElite = true;
            }
        }

        if (!estaElite) {
            cambiaAElite();
        }
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

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                posiciones.add(new Pair<>(seleccionados.get(i).get(j), seleccionados.get(i + 1).get(j)));
            }

            Queue<Integer> auxQueue1 = new LinkedList<>();

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue1.add(padre2.get(j));
            }

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxVec1.set(j, auxQueue1.poll());
            }

            for (int j = aleatorioB + 1, cont = 0; cont < auxVec1.size() - (aleatorioB - aleatorioA + 1); j++, cont++) {
                boolean esta = true;
                for (int k = 0; k < auxVec1.size() && esta; k++) {
                    if (auxVec1.get(k) == padre1.get(j % auxVec1.size())) {
                        esta = false;
                    }
                }
                if (esta) {
                    auxVec1.set(j % auxVec1.size(), padre1.get(j % auxVec1.size()));
                } else {
                    auxQueue1.add(padre1.get(j % auxVec1.size()));
                }
            }

            while (!auxQueue1.isEmpty()) {
                boolean esta = true;
                int aux = 0;
                for (int j = 0; j < padre1.size() && esta; j++) {
                    if (padre1.get(j) == auxQueue1.peek()) {
                        aux = j;
                        esta = false;
                    }
                }
                if (!esta) {
                    int auxas = padre1.get(auxQueue1.poll());
                    auxVec1.set(aux, auxas);
                }
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec1.set(posiciones.get(j).snd, posiciones.get(j).fst);
            }

            Queue<Integer> auxQueue2 = new LinkedList<>();
            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxQueue2.add(padre1.get(j));
            }

            for (int j = aleatorioA; j <= aleatorioB; j++) {
                auxVec2.set(j, auxQueue2.poll());
            }

            for (int j = aleatorioB + 1, cont = 0; cont < auxVec2.size() - (aleatorioB - aleatorioA + 1); j++, cont++) {
                boolean esta = true;
                for (int k = 0; k < auxVec2.size() && esta; k++) {
                    if (auxVec2.get(k) == padre2.get(j % auxVec2.size())) {
                        esta = false;
                    }
                }
                if (esta) {
                    auxVec2.set(j % auxVec2.size(), padre2.get(j % auxVec2.size()));
                } else {
                    auxQueue2.add(padre2.get(j % auxVec2.size()));
                }
            }

            while (!auxQueue2.isEmpty()) {
                boolean esta = true;
                int aux = 0;
                for (int j = 0; j < padre2.size() && esta; j++) {
                    if (padre2.get(j) == auxQueue2.peek()) {
                        aux = j;
                        esta = false;
                    }
                }
                if (!esta) {
                    int auxas = padre2.get(auxQueue2.poll());
                    auxVec2.set(aux, auxas);
                }
            }

            for (int j = 0; j < posiciones.size(); j++) {
                auxVec2.set(posiciones.get(j).fst, posiciones.get(j).snd);
            }
            auxSel.add(auxVec2);
            auxSel.add(auxVec2);

        }

        if (random.nextFloat() < probMutacion) {
            mutacion(auxSel);
        }

        return auxSel;
    }

    private void mutacion(ArrayList<ArrayList<Integer>> elementoAMutar) {
        for (int i = 0; i < elementoAMutar.size(); i++) {
            int pos1, pos2;
            do {
                pos1 = random.nextInt(elementoAMutar.get(i).size());
                pos2 = random.nextInt(elementoAMutar.get(i).size());
            } while (pos1 == pos2);

            int aux = elementoAMutar.get(i).get(pos1);
            elementoAMutar.get(i).set(pos2, elementoAMutar.get(i).get(pos1));
            elementoAMutar.get(i).set(pos1, aux);
        }
    }

    private static boolean arrayIntAleatoriosGeneradoBien(ArrayList<Integer> array) {
        for (int i = 0; i < array.size() - 1; ++i) {
            for (int j = i + 1; j < array.size(); ++j) {
                if (array.get(i) == array.get(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Integer> generadorArrayIntAleatorios(int cuantos, int mod) {
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < cuantos; ++i) {
            array.add(random.nextInt(mod));
        }
        return array;
    }

    private void guardarLog(int generacion) {
        String nombre = archivo.getNombre().split("/")[1];
        if (generacion == -1) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGPMX_poblacionInicial");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce PMX | Tamaño de la población: " + tamPoblacion + "| Población inicial\n\n");
        } else if (generacion + 1 == evaluaciones) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGPMX_poblacionFinal");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce PMX | Tamaño de la población: " + tamPoblacion + "| Población final\n\n");
        } else {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGPMX_poblacion_" + (generacion + 1));
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce PMX | Tamaño de la población: " + tamPoblacion + "| Generación: " + (generacion + 1) + "\n\n");
        }

        for (int j = 0; j < poblacion.size(); ++j) {
            log.addTexto("(" + calculaCosteConjunto(poblacion.get(j)) + ") " + poblacion.get(j).toString());
            log.addTexto("\n");
        }

        int costeMin = Integer.MAX_VALUE;
        int mejorSol = -1;
        for (int i = 0; i < poblacion.size(); ++i) {
            int costeSel = calculaCosteConjunto(poblacion.get(i));
            if (costeSel < costeMin) {
                costeMin = costeSel;
                mejorSol = i;
            }
        }
        log.addTexto("\n\nMejor individuo de esta generación: " + mejorSol + " (" + costeMin + ")");
        log.setModo(modoLog); // AHORA SE PUEDE PONER EN EL config.txt SI QUEREMOS QUE EL LOG SEA SalidaLog=log O SalidaLog=stdout
        log.guardaLog();
    }
}
