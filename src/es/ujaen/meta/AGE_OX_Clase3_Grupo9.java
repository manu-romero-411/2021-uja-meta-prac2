/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.util.*;

import com.sun.tools.javac.util.Pair;

/**
 *
 * @author admin
 */
public class AGE_OX_Clase3_Grupo9 {

    private long tiempoInicio;
    private long tiempoFin;
    private final Random random;
    private final long seed;
    private final int longitudLRC;
    private final ArrayList<Pair<Integer, Integer>> LRC;
    private Log log;
    private final String modoLog;
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

    public AGE_OX_Clase3_Grupo9(Random random, long seed, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
                             int vecesSeleccion, int tamTorneoSeleccion, int tamTorneoReemplazamiento, int vecesTorneoReemplazamiento, String modoLog) {
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
        this.vecesTorneoReemplazamiento = vecesTorneoReemplazamiento;
        this.conjunto = new ArrayList<>();
        this.poblacion = new ArrayList<>();
        this.LRC = new ArrayList<>();
        this.log = null;
        this.modoLog = modoLog;
    }

    public void hazGeneticoEstacionario() {
        this.tiempoInicio=System.currentTimeMillis();
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        guardarLog(-1);
        for (int i = 0; i < evaluaciones/tamTorneoReemplazamiento; ++i) {
            ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>();
            ArrayList<ArrayList<Integer>> torneoSel = new ArrayList<>(seleccion());
            for (int j = 0; j < torneoSel.size(); j++) {
                seleccionados.add(j, torneoSel.get(j));

            }
            if (random.nextFloat() < probCruce) {
                cruceOX(seleccionados); //Cruces y mutación a la vez
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

        System.out.println("La mejor solución para " + archivo.getNombre() + " es la " + mejorSol + ", coste " + costeMin + ":");
        debugMuestraArray(poblacion.get(mejorSol));
        System.out.println("Terminado (tiempo: " + (tiempoFin-tiempoInicio) + " ms)");
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
            //ArrayList<Integer> repetidos = new ArrayList<>();
            ArrayList<Integer> individuos = new ArrayList<>();
            for (int i = 0; i < conjunto.size(); i++) {
                individuos.add(-1);
            }

            for (int i = 0; i < longitudLRC; i++) {
                individuos.set(LRC.get(i).fst, LRC.get(i).snd);
            }

            for (int i = 0; i < conjunto.size(); ++i) {
                if (individuos.get(i) == -1) {
                    do {
                        boolean repetido = false;
                        int num = random.nextInt(conjunto.size());

                        // Comprobamos si el aleatorio que hemos generado está antes de la posición donde lo queremos poner
                        for (int k = 0; k < i && repetido == false; ++k) {
                            if (num == individuos.get(k)) {
                                repetido = true;
                            }
                        }

                        // Comprobamos si el aleatorio que hemos generado está después de la posición donde lo queremos poner
                        for (int k = i + 1; k < conjunto.size() && repetido == false; ++k) {
                            if (num == individuos.get(k)) {
                                repetido = true;
                            }
                        }

                        // Si el aleatorio generado todavía no está en el individuo (sea valor de la LRC o aleatorio anterior)
                        // se introduce en esta posición, i
                        if (repetido == false) {
                            individuos.set(i, num);
                        }
                        // El proceso anterior se realiza dentro de la misma posición siempre que ésta sea igual a -1
                    } while (individuos.get(i) == -1);
                }
            }
            // Una vez generada el individuo, se añade a la población
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

            do {
                torneos = generadorArrayIntAleatorios(tamTorneoSeleccion, tamPoblacion);
            } while (!arrayIntAleatoriosGeneradoBien(torneos));

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

    private void reemplazamiento(ArrayList<ArrayList<Integer>> cruzados) {
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
        int adsa = 0;
        for (int i = 0; i < poblacion.size(); i++) {
            for (int j = 0; j < seleccionados.size(); j++) {
                int contador = 0;
                for (int k = 0; k < seleccionados.size(); k++) {
                    if (poblacion.get(i).get(k) == seleccionados.get(j).get(k)) {
                        contador++;
                    }
                }
                if (contador == seleccionados.size()) {
                    log = new Log("logs" + "_reemplazo" + adsa++);
                    log.addTexto("Reemplazo: " + poblacion.get(i));
                    log.addTexto("\n");

                    boolean reemplaza = false;
                    for (int k = 0; k < cruzados.size() && !reemplaza; k++) {

                        if (reemplazaPoblacion(poblacion.get(i), cruzados.get(k))) {

                            cruzados.remove(k);
                            reemplaza = true;
                        }

                    }
                }
            }

        }
    }

    private boolean reemplazaPoblacion(ArrayList<Integer> seleccionado, ArrayList<Integer> cruzado) {
        log.addTexto("Por: " + cruzado);
        //log.guardaLog();
        if (calculaCosteConjunto(seleccionado) < calculaCosteConjunto(cruzado)) {
            return false;
        } else {

            for (int i = 0; i < seleccionado.size(); i++) {
                seleccionado.set(i, cruzado.get(i));
            }
        }
        return true;
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
        if (random.nextFloat() < probMutacion) {
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
            array.add(-1);
        }

        for (int i = 0; i < cuantos; ++i) {
            do {
                boolean repetido = false;
                int num = random.nextInt(mod);
                for (int j = 0; j < i && repetido == false; ++j) {
                    if (num == array.get(j)) {
                        repetido = true;
                    }
                }
                if (repetido == false) {
                    array.set(i, num);
                }
            } while (array.get(i) == -1);
        }
        return array;
    }

    private void guardarLog(int generacion){
        String nombre = archivo.getNombre().split("/")[1];
        if (generacion == -1){
            log=new Log("logs/" + nombre + "_" + seed + "_AGEOX_poblacionInicial");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Estacionario con cruce OX | Tamaño de la población: " + tamPoblacion + "| Población inicial\n\n");
        } else if (generacion+1 == evaluaciones) {
            log=new Log("logs/" + nombre + "_" + seed + "_AGEOX_poblacionFinal");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Estacionario con cruce OX | Tamaño de la población: " + tamPoblacion + "| Población final\n\n");
        } else {
            log=new Log("logs/" + nombre + "_" + seed + "_AGEOX_poblacion_" + (generacion+1));
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Estacionario con cruce OX | Tamaño de la población: " + tamPoblacion + "| Generación: " + (generacion+1) + "\n\n");
        }

        for (int j = 0; j < poblacion.size(); ++j){
            log.addTexto("(" + calculaCosteConjunto(poblacion.get(j)) + ") " + poblacion.get(j).toString());
            log.addTexto("\n");
        }

        int costeMin = Integer.MAX_VALUE;
        int mejorSol = -1;
        for (int i = 0; i < poblacion.size(); ++i){
            int costeSel = calculaCosteConjunto(poblacion.get(i));
            if (costeSel < costeMin){
                costeMin = costeSel;
                mejorSol = i;
            }
        }
        log.addTexto("\n\nMejor individuo de esta generación: " + mejorSol + " (" + costeMin + ")");
        if (generacion + 1 == evaluaciones) {
            tiempoFin = System.currentTimeMillis();
            long tiempo = tiempoFin-tiempoInicio;
            log.addTexto("\nTiempo de ejecución del algoritmo para este archivo y semilla: " + tiempo + " ms");
        }
        log.setModo(modoLog); // AHORA SE PUEDE PONER EN EL config.txt SI QUEREMOS QUE EL LOG SEA SalidaLog=log O SalidaLog=stdout
        log.guardaLog();
    }
}
