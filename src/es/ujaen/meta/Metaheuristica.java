/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Usuario
 */
public class Metaheuristica implements Runnable {

    private Random aleatorio;
    private Archivodedatos archivo;
    private StringBuilder log;
    private CountDownLatch cdl;

    public Metaheuristica(Archivodedatos archivo, CountDownLatch cdl, Long semilla) {
        this.archivo = archivo;
        this.cdl = cdl;
        aleatorio = new Random(semilla);
        log = new StringBuilder();
    }

    @Override
    public void run() {
        //Inicializacion aleatoria de la primera solucion
        log.append("El coste de la solucion inicial es X");
        long tiempoinicial = System.currentTimeMillis();

        //Ejecucion de la metaheuristica
        int coste = 0;

        log.append("Iteracion Y \nCoste mejor X \nSe acepta solucion generada con coste XXX.....");

        long tiempofinal = System.currentTimeMillis();
        //Finalizacion de la metaheuristica
        log.append("El costo final es X \n Duraccion" + (tiempofinal - tiempoinicial) / 1000 + " segundos");
        cdl.countDown();
    }

    public String getLog() {
        return log.toString();
    }
    
    public static void muestraArray(int array[]) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf(array[i] + " ");
        }
    }

    public static void muestraMatriz(int matriz[][]) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                System.out.printf(matriz[i][j] + " ");
            }
            System.out.println("");
        }
    }
}
