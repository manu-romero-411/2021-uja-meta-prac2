package es.ujaen.meta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public class AGG_OX2_Clase3_Grupo9 {
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

    public AGG_OX2_Clase3_Grupo9(Random random, long seed, int longitudLRC, Archivodedatos archivo, int tamPoblacion, int evaluaciones, float probCruce, float probMutacion,
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
                cruceOX2(seleccionados);
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
        int mejorCoste = Integer.MAX_VALUE;
        for (int i = 0; i < torneos.size(); i++) {
            int cos = calculaCosteConjunto(poblacion.get(torneos.get(i)));
            if (cos < mejorCoste) {
                for (int j = 0; j < poblacion.get(torneos.get(i)).size(); j++) {
                    mejor.set(j, poblacion.get(torneos.get(i)).get(j));
                }
                mejorCoste = cos;
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
            contEv++; // VAMOS A EVALUAR TODOS LOS ELEMENTOS DE LA POBLACIÓN EN BUSCA DEL MEJOR
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

    private ArrayList<ArrayList<Integer>> cruceOX2(ArrayList<ArrayList<Integer>> seleccionados) {
        ArrayList<ArrayList<Integer>> auxSel = new ArrayList<>();
        for (int j = 0; j < seleccionados.size() - 1; j = j + 2) {
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

            for (int i = 0; i < padre1.size(); i++) {
                if (boolPadre.poll()) {
                    auxVec1.set(i, padre1.get(i));
                } else {
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
            auxSel.add(auxVec2);
            auxSel.add(auxVec1);
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
            log = new Log("logs/" + nombre + "_" + seed + "_AGGOX2_poblacionInicial");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce OX2 | Tamaño de la población: " + tamPoblacion + "| Población inicial\n\n");
        } else if (generacion + 1 == evaluaciones) {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGOX2_poblacionFinal");
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce OX2 | Tamaño de la población: " + tamPoblacion + "| Población final (generación " + contGen + ")\n\n");
        } else {
            log = new Log("logs/" + nombre + "_" + seed + "_AGGOX2_poblacion_" + (generacion + 1));
            log.addTexto("Archivo de datos: " + archivo.getNombre() + " | Algoritmo: Genético Generacional con cruce OX2 | Tamaño de la población: " + tamPoblacion + "| Generación: " + (generacion + 1) + "\n\n");
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
