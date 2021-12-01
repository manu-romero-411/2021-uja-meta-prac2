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
public class AGG_OX2_Clase3_Grupo9 {

    private final Random random;
    private final long seed;
    private Log log;
    private final String modoLog;
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
    private ArrayList<Integer> elite;

    public AGG_OX2_Clase3_Grupo9(Random random, long seed, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
            int vecesSeleccion, int tamTorneoSeleccion, int tamTorneoReemplazamiento, String modoLog) {
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
        this.tamTorneoReemplazamiento = tamTorneoReemplazamiento;
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
        guardarLog(0);
        for (int j = 0; j < poblacion.size(); j++) {
            System.out.println("Poblacion " + j + " : " + poblacion.get(j));
        }
        for (int i = 0; i < evaluaciones; ++i) {
            ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
            if (random.nextFloat() < probCruce) {
                cruceOX2(seleccionados);
            }
            reemplazamiento(seleccionados);
            guardarLog(i);
            System.out.println("\nGeneración " + i + " generada");
        }
        System.out.println("Terminado");
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

        // INICIAMOS LA ÉLITE. EN EL REEMPLAZAMIENTO TRATAREMOS CON ELLA
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

    // Esta función la llamaremos desde el reemplazamiento, para sustituir el peor de la nueva generación por la élite
    // vigente
    private void eliteReemplaza(ArrayList<ArrayList<Integer>> poblacion1, ArrayList<Integer> elite1) {
        int costeMin = Integer.MIN_VALUE;
        int eliteIt = -1;
        for (int i = 0; i < poblacion1.size(); ++i) {
            if (calculaCosteConjunto(poblacion1.get(i)) > costeMin) {
                eliteIt = i;
            }
        }

        for (int i = 0; i < poblacion1.get(eliteIt).size(); ++i) {
            elite.set(i, elite1.get(i));
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

    /*private ArrayList<Integer> evolucion() {
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
    }*/
    private ArrayList<ArrayList<Integer>> seleccion() {
        System.out.println();
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();

        ArrayList<Integer> torneos = new ArrayList<>();
        while (seleccionados.size() <= tamPoblacion) {
            do {
                torneos = generadorArrayIntAleatorios(tamTorneoSeleccion, tamPoblacion);
            } while (!arrayIntAleatoriosGeneradoBien(torneos));
            ArrayList<Integer> ganador = new ArrayList<>(mejorTorneo(torneos));
            ganador = mejorTorneo(torneos);

            if (!(estaSeleccionado(seleccionados, ganador))) {
                seleccionados.add(ganador);

            }
        }
        for (int j = 0; j < seleccionados.size(); j++) {
            System.out.println("Seleccion " + j + " : " + seleccionados.get(j));
        }
        nuevaElite(poblacion);
        return seleccionados;
    }

    private boolean estaSeleccionado(ArrayList<ArrayList<Integer>> seleccionados, ArrayList<Integer> ganador) {

        for (int i = 0, cont = 0; i < seleccionados.size(); i++) {
            for (int j = 0; j < seleccionados.get(i).size(); j++) {
                if (seleccionados.get(i).get(j) == ganador.get(j)) {
                    cont++;
                }
            }
            if (cont >= seleccionados.size()) {
                return true;
            }
        }
        return false;
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

    private void reemplazamiento(ArrayList<ArrayList<Integer>> nuevaPob) {
        // Habiendo cruzado y mutado toda la población nueva, tenemos que reemplazar la anterior.
        // Primero, tenemos que ver si el élite está en esta nueva población.
        boolean isElite = false;
        int eliteIt2 = -1;
        for (int i = 0; i < nuevaPob.size(); ++i) {
            isElite = true;
            for (int j = 0; j < nuevaPob.get(i).size() && isElite; ++j) {
                int cual1 = elite.get(j);
                int cual2 = nuevaPob.get(i).get(j);
                if (cual1 != cual2) {
                    isElite = false; // LA ÉLITE ANTERIOR NO ESTÁ EN i. SEGUIR BUSCANDO
                    break;
                }
            }
            if (isElite == true) {
                eliteIt2 = i;
                // LA ÉLITE ANTERIOR ESTARÍA EN i
            }
        }

        // Si la élite no está en la población nueva, poner la élite de la población anterior.
        // Si la élite sí está, no hacer nada aquí.
        if (isElite == false) {
            eliteReemplaza(nuevaPob, elite);
        }

        // Buscamos una nueva élite en la nueva población (esto quizás no haga falta hacerlo aquí o ya se haga en la
        // selección)
        //nuevaElite(nuevaPob);
        // Reemplazamos la población (seguramente haya una mejor forma de hacerlo)
        for (int i = 0; i < poblacion.size(); ++i) {
            //for (int j = 0; j < poblacion.get(i).size(); ++j) {
            poblacion.set(i, nuevaPob.get(i));
            //}
        }
    }

    private ArrayList<ArrayList<Integer>> cruceOX2(ArrayList<ArrayList<Integer>> seleccionados) {
        ArrayList<ArrayList<Integer>> auxSel = new ArrayList<>();
        for (int j = 0; j < seleccionados.size() - 1; j = j + 2) {
            if (j == 0) {
                System.out.println();
            }
            ArrayList<Integer> padre1 = new ArrayList<>(seleccionados.get(j));
            ArrayList<Integer> padre2 = new ArrayList<>(seleccionados.get(j + 1));

            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int i = 0; i < padre1.size(); i++) {
                auxVec1.add(padre1.get(i));
            }
            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int i = 0; i < padre2.size(); i++) {
                auxVec2.add(padre2.get(i));
            }

            Queue<Boolean> boolPadre = new LinkedList<>();
            Queue<Integer> cruzados = new LinkedList<>();
            ArrayList<Integer> noEstan = new ArrayList<>();

            for (int i = 0; i < padre1.size(); i++) {
                boolPadre.add(random.nextBoolean());
            }

            Queue<Boolean> copia = new LinkedList<>(boolPadre);

            for (int i = 0; i < padre1.size(); i++) {
                if (boolPadre.poll()) {
                    //System.out.print("V ");
                    auxVec1.set(i, padre1.get(i));
                } else {
                    //System.out.print("F ");
                    noEstan.add(padre1.get(i));
                }
            }

            for (int i = 0; i < padre2.size(); i++) {
                boolean noEsta = false;
                for (int k = 0; k < noEstan.size() && !noEsta; k++) {
                    if (padre2.get(i) == noEstan.get(k)) {
                        cruzados.add(padre2.get(i));
                        noEsta = true;
                    }
                }
            }

            for (int i = 0; i < auxVec1.size(); i++) {
                if (auxVec1.get(i) == -1) {
                    auxVec1.set(i, cruzados.poll());
                }
            }

            noEstan.clear();

            for (int i = 0; i < padre2.size(); i++) {
                boolPadre.add(random.nextBoolean());
            }

            for (int i = 0; i < padre2.size(); i++) {
                if (boolPadre.poll()) {
                    auxVec2.set(i, padre2.get(i));
                } else {
                    noEstan.add(padre2.get(i));
                }
            }

            for (int i = 0; i < padre1.size(); i++) {
                boolean noEsta = false;
                for (int k = 0; k < noEstan.size() && !noEsta; k++) {
                    if (padre1.get(i) == noEstan.get(k)) {
                        cruzados.add(padre1.get(i));
                        noEsta = true;
                    }
                }
            }

            for (int i = 0; i < auxVec2.size(); i++) {
                if (auxVec2.get(i) == -1) {
                    auxVec2.set(i, cruzados.poll());
                }
            }

            noEstan.clear();

            // Ya tenemos los dos vectores cruzados. Meterlos en la población
            for (int i = 0; i < auxVec1.size(); ++i) {
                if (auxVec1.get(i) == null || auxVec2.get(i) == null) {
                    for (int k = 0; k < auxVec1.size(); ++k) {
                        if (!(auxVec1.contains(k) || !auxVec2.contains(k))) {
                            System.out.println(k);
                        } else {
                            System.out.println(k + "está");
                        }
                    }
                }
            }
            auxSel.add(auxVec2);
            auxSel.add(auxVec1);
        }

        // Se realiza la mutación con la nueva población generada
        if (random.nextFloat() < probMutacion) {
            mutacion(auxSel);
        }
        return auxSel;
    }

    private void debugMuestraArray(ArrayList<Integer> debug) {
        for (int i = 0; i < debug.size(); i++) {
            System.out.print(debug.get(i) + " ");
        }
        System.out.println("");
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
        if (generacion == 0) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGOX2_poblacionInicial");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce OX2 | Tamaño de la población: " + tamPoblacion + "| Población inicial\n\n");
        } else {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGOX2_poblacion_" + generacion);
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce OX2 | Tamaño de la población: " + tamPoblacion + "| Generación: " + generacion + "\n\n");
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
