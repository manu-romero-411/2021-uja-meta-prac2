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
    private ArrayList<Integer> elite;

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
        this.elite = new ArrayList<>();

    }

    private void inicializaElite(){
        for(int i = 0; i < archivo.getMatriz1().length; ++i){
            elite.add(-1);
        }
    }

    public void hazGeneticoGeneracional() {
        inicializaElite();
        iniciaConjunto();
        creaLRC();
        creaPoblacionInicial();
        ArrayList<ArrayList<Integer>> seleccionados = new ArrayList<>(seleccion());
        cruceOX2(seleccionados);
        crucePMX(seleccionados);
        reemplazamiento(seleccionados);
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

    private void nuevaElite(ArrayList<ArrayList<Integer>> poblacion1){
        int costeMin = Integer.MAX_VALUE;
        int eliteIt = -1;
        for (int i = 0; i < poblacion1.size(); ++i){
            if(calculaCosteConjunto(poblacion1.get(i)) < costeMin){
                eliteIt = i;
                costeMin = calculaCosteConjunto(poblacion1.get(i));
            }
        }

        for (int i = 0; i < poblacion1.get(eliteIt).size(); ++i){
            elite.set(i,poblacion1.get(eliteIt).get(i));
        }
    }

    // Esta función la llamaremos desde el reemplazamiento, para sustituir el peor de la nueva generación por la élite
    // vigente
    private void eliteReemplaza(ArrayList<ArrayList<Integer>> poblacion1, ArrayList<Integer> elite1){
        int costeMin = Integer.MIN_VALUE;
        int eliteIt = -1;
        for (int i = 0; i < poblacion1.size(); ++i){
            if(calculaCosteConjunto(poblacion1.get(i)) > costeMin){
                eliteIt = i;
            }
        }

        for (int i = 0; i < poblacion1.get(eliteIt).size(); ++i){
            elite.set(i,elite1.get(i));
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
            ArrayList<Integer> aux = new ArrayList<>();
            for (int j = 0; j < conjunto.size(); j++) {
                aux.add(-1);
            }
            ArrayList<Integer> torneos = new ArrayList<>();
            boolean aleatorioDiferentes = false;
            boolean estaTorneo = true;
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
                estaTorneo = false;
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

    private void reemplazamiento(ArrayList<ArrayList<Integer>> nuevaPob) {
        // Habiendo cruzado y mutado toda la población nueva, tenemos que reemplazar la anterior.
        // Primero, tenemos que ver si el élite está en esta nueva población.
        boolean isElite = false;
        int eliteIt2 = -1;
        for (int i = 0; i < nuevaPob.size(); ++i){
            isElite = true;
            for (int j = 0; j < nuevaPob.get(i).size() && isElite; ++j){
                int cual1 = elite.get(j);
                int cual2 = nuevaPob.get(i).get(j);
                if (cual1 != cual2) {
                    isElite = false; // LA ÉLITE ANTERIOR NO ESTÁ EN i. SEGUIR BUSCANDO
                    break;
                }
            }
            if (isElite == true){
                eliteIt2 = i;
                // LA ÉLITE ANTERIOR ESTARÍA EN i
            }
        }

        // Si la élite no está en la población nueva, poner la élite de la población anterior.
        // Si la élite sí está, no hacer nada aquí.
        if (isElite == false){
            eliteReemplaza(nuevaPob,elite);
        }

        // Buscamos una nueva élite en la nueva población (esto quizás no haga falta hacerlo aquí o ya se haga en la
        // selección)
        nuevaElite(nuevaPob);

        // Reemplazamos la población (seguramente haya una mejor forma de hacerlo)
        for(int i = 0; i < poblacion.size(); ++i){
            for (int j = 0; j < poblacion.get(i).size(); ++j){
                poblacion.get(i).set(j,nuevaPob.get(i).get(j));
            }
        }
    }

    private ArrayList<ArrayList<Integer>> cruceOX2(ArrayList<ArrayList<Integer>> seleccionados) {
        ArrayList<ArrayList<Integer>> auxSel = new ArrayList<>();
        for (int j = 0; j < seleccionados.size() - 1; j = j + 2) {

            ArrayList<Integer> padre1 = new ArrayList<>(seleccionados.get(j));
            ArrayList<Integer> padre2 = new ArrayList<>(seleccionados.get(j + 1));

            ArrayList<Integer> listaAleatorios = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                listaAleatorios.add(i, random.nextInt(padre1.size()));
                for (int k = 0; k < i; ++k) {
                    while (listaAleatorios.get(i) == listaAleatorios.get(k)) {
                        listaAleatorios.set(i, random.nextInt(conjunto.size()));
                    }
                }
            }

            ArrayList<Pair<Integer,Integer>> paresEscogidosPadre2 = new ArrayList<>();
            ArrayList<Pair<Integer,Integer>> paresOrdenadosPadre1 = new ArrayList<>();

            // ¿Qué elementos de padre2 me ha escogido el aleatorio?
            for (int i = 0; i < padre2.size(); ++i) {
                for (int k = 0; k < listaAleatorios.size(); ++k) {
                    if (listaAleatorios.get(k) == i) {
                        Pair<Integer,Integer> par = new Pair<>(i,padre2.get(i));
                        paresEscogidosPadre2.add(par);
                    }
                }
            }

            // ¿Qué elementos de padre1 están en las posiciones escogidas de padre2?
            for (int i = 0; i < padre1.size(); ++i) {
                for (int k = 0; k < paresEscogidosPadre2.size(); ++k) {
                    int cual1 = paresEscogidosPadre2.get(k).snd;
                    int cual2 = padre1.get(i);

                    if (cual1 == cual2) {
                        Pair<Integer,Integer> par = new Pair<>(i,padre1.get(i));
                        paresOrdenadosPadre1.add(par);
                    }
                }
            }

            // Copiamos el padre1 exceptuando los valores escogidos
            ArrayList<Integer> auxVec1 = new ArrayList<>();
            for (int i = 0; i < padre1.size(); ++i) {
                auxVec1.add(-1);
            }

            for (int i = 0; i < paresEscogidosPadre2.size(); ++i){
                for (int k = 0; k < auxVec1.size(); ++k){
                    if (paresEscogidosPadre2.get(i).snd == auxVec1.get(k)){
                        auxVec1.set(k,-1);
                    }
                }
            }

            // Añadimos los elementos de padre2 en orden en padre1
            int contadorPar = 0;
            for (int i = 0; i < auxVec1.size(); ++i){
                if(auxVec1.get(i) == -1){
                    auxVec1.set(i,paresEscogidosPadre2.get(contadorPar).snd);
                    contadorPar++;
                }
            }

            // Ahora hay que hacer lo mismo pero con padre2
            ArrayList<Integer> auxVec2 = new ArrayList<>();
            for (int i = 0; i < padre2.size(); ++i) {
                auxVec2.add(-1);
            }
            if (j == 4){
                debugMuestraArray(padre1);
            }
            for (int i = 0; i < paresEscogidosPadre2.size(); ++i){
                auxVec2.set(paresEscogidosPadre2.get(i).fst,paresOrdenadosPadre1.get(i).snd);
            }

            for (int i = 0; i < auxVec2.size(); ++i) {
                if (auxVec2.get(i) == -1) {
                    auxVec2.set(i, padre2.get(i));
                }
            }

            // Ya tenemos los dos vectores cruzados. Meterlos en la población
            auxSel.add(auxVec2);
            auxSel.add(auxVec1);

        }

        // Se realiza la mutación con la nueva población generada
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

            System.out.println("auxVec1");
            debugMuestraArray(padre1);
            debugMuestraArray(padre2);
            debugMuestraArray(auxVec1);

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
            //Se hace bien
            auxSel.add(auxVec2);

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
}
