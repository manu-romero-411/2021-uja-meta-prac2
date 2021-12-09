package es.ujaen.meta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public class AGG_PMX_Clase3_Grupo9 {
    private long tiempoInicio;
    private long tiempoFin;
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
    private int contEv;
    private int contGen;
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
        this.contEv = 0;
        this.contGen = 0;
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
        this.tiempoInicio = System.currentTimeMillis();
        inicializaElite();
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        guardarLog(-1);
        while (contEv < evaluaciones){
            ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
            if (random.nextFloat() < probCruce) {
                crucePMX(seleccionados);
            }
            reemplazamiento(seleccionados);
            contGen++;
        }

        guardarLog(evaluaciones - 1);
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

            for (int i = 0; i < LRC.size(); i++) {
                individuos.set(LRC.get(i).fst, LRC.get(i).snd);
            }

            for (int i = 0; i < conjunto.size(); ++i) {
                if (individuos.get(i) == -1) {
                    do {
                        boolean repetido = false;
                        int num = random.nextInt(conjunto.size());

                        // Comprobamos si el aleatorio que hemos generado está antes de la posición donde lo queremos poner
                        for (int k = 0; k < i && !repetido; ++k) {
                            if (num == individuos.get(k)) {
                                repetido = true;
                            }
                        }

                        // Comprobamos si el aleatorio que hemos generado está después de la posición donde lo queremos poner
                        for (int k = i + 1; k < conjunto.size() && !repetido; ++k) {
                            if (num == individuos.get(k)) {
                                repetido = true;
                            }
                        }

                        // Si el aleatorio generado todavía no está en el individuo (sea valor de la LRC o aleatorio anterior)
                        // se introduce en esta posición, i
                        if (!repetido) {
                            individuos.set(i, num);
                        }
                        // El proceso anterior se realiza dentro de la misma posición siempre que ésta sea igual a -1
                    } while (individuos.get(i) == -1);
                }
            }
            // Una vez generada el individuo, se añade a la población
            contEv++; // ESTAMOS EVALUANDO CADA ELEMENTO NUEVO DE LA POBLACIÓN INICIAL
            poblacion.add(individuos);
        }
        contGen++;
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
            contEv++; // VAMOS A EVALUAR TODOS LOS ELEMENTOS DE LA POBLACIÓN EN BUSCA DEL MEJOR
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
                torneos = generadorAleatorios(tamTorneoSeleccion, tamPoblacion);
            } while (!aleatoriosBien(torneos));
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
            auxSel.add(auxVec1);
            auxSel.add(auxVec2);

        }
        //Aleatorio para ver si muta la poblacion
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

    private static boolean aleatoriosBien(ArrayList<Integer> aleatorios) {
        for (int i = 0; i < aleatorios.size() - 1; ++i) {
            for (int j = i + 1; j < aleatorios.size(); ++j) {
                if (aleatorios.get(i) == aleatorios.get(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Integer> generadorAleatorios(int cuantos, int tam) {
        ArrayList<Integer> aleatorios = new ArrayList<>();
        for (int i = 0; i < cuantos; ++i) {
            aleatorios.add(-1);
        }

        for (int i = 0; i < cuantos; ++i) {
            do {
                boolean repetido = false;
                int num = random.nextInt(tam);
                for (int j = 0; j < i && !repetido; ++j) {
                    if (num == aleatorios.get(j)) {
                        repetido = true;
                    }
                }
                if (!repetido) {
                    aleatorios.set(i, num);
                }
            } while (aleatorios.get(i) == -1);
        }
        return aleatorios;
    }

    private void guardarLog(int generacion) {
        String nombre = archivo.getNombre().split("/")[1];
        if (generacion == -1) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGPMX_poblacionInicial");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce PMX | Tamaño de la población: " + tamPoblacion + "| Población inicial\n\n");
        } else if (generacion + 1 == evaluaciones) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGPMX_poblacionFinal");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce PMX | Tamaño de la población: " + tamPoblacion + "| Población final (generación " + contGen + ")\n\n");
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
        if (generacion + 1 == evaluaciones) {
            tiempoFin = System.currentTimeMillis();
            long tiempo = tiempoFin-tiempoInicio;
            log.addTexto("\nTiempo de ejecución del algoritmo para este archivo y semilla: " + tiempo + " ms");
        }
        log.setModo(modoLog); // AHORA SE PUEDE PONER EN EL config.txt SI QUEREMOS QUE EL LOG SEA SalidaLog=log O SalidaLog=stdout
        log.guardaLog();
    }
}
