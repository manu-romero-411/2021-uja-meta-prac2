/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Manuel
 */
public class prac1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Configurador config = new Configurador(args[0]);
        ArrayList<Archivodedatos> arrayA = new ArrayList<>();
        System.out.println(config.getArchivos());

        //Añade a la lista de archivos los diferentes archivos de datos
        for (int i = 0; i < config.getArchivos().size(); i++) {
            Archivodedatos archivo = new Archivodedatos(config.getArchivos().get(i));
            arrayA.add(archivo);
        }

        for (int j = 0; j < config.getSemillas().size(); j++) {

            Log log = new Log("logs/"+config.getSalidaLog()+"_semilla_"+config.getSemillas().get(j));
            Random random = new Random(config.getSemillas().get(j));

            System.out.println("SEMILLA: "+ config.getSemillas().get(j));
            
            for (int i = 0; i < arrayA.size(); i++) {
                System.out.println("* Ejecutando GREEDY - ARCHIVO " + (i+1) + " EJECUCIÓN " + (j+1));
                AlgGRE_Clase3_Grupo9 greedy = new AlgGRE_Clase3_Grupo9(arrayA.get(i));
                greedy.calculaGreedy();
                //System.out.print(greedy.muestraDatos());
                log.addTexto(greedy.muestraDatos());
            }

            log.addTexto("\n");
            for (int i = 0; i < arrayA.size(); i++) {
                System.out.println("* Ejecutando PRIMER MEJOR ITERATIVO - ARCHIVO " + (i+1) + " EJECUCIÓN " + (j+1));
                AlgPMDLBit_Clase3_Grupo9 primero = new AlgPMDLBit_Clase3_Grupo9(arrayA.get(i), config.getIteraciones());
                primero.calculaPrimerMejor();
                //System.out.print(primero.muestraDatos());
                log.addTexto(primero.muestraDatos());
            }

            log.addTexto("\n");
            for (int i = 0; i < arrayA.size(); i++) {
                System.out.println("* Ejecutando PRIMER MEJOR ALEATORIO - ARCHIVO " + (i+1) + " EJECUCIÓN " + (j+1));
                AlgPMDLBrandom_Clase3_Grupo9 primeroAle = new AlgPMDLBrandom_Clase3_Grupo9(arrayA.get(i), config.getIteraciones(), random);
                primeroAle.calculaPrimeroElMejor();
                //System.out.print(primeroAle.muestraDatos());
                log.addTexto(primeroAle.muestraDatos());
            }

            log.addTexto("\n");
            for (int i = 0; i < arrayA.size(); i++) {
                System.out.println("* Ejecutando MULTIARRANQUE - ARCHIVO " + (i+1) + " EJECUCIÓN " + (j+1));
                AlgMA_Clase3_Grupo9 multiA = new AlgMA_Clase3_Grupo9(arrayA.get(i), config.getIteraciones(),
                        config.getLonguitudLRC(), config.getCandidatosGreedy(), config.getTamLista(), config.getIteracionesEstrategica(), random);
                multiA.calculaMultiarranque();
               // System.out.print(multiA.muestraDatos());
                log.addTexto(multiA.muestraDatos());
            }

            log.guardaLog();
            System.out.println();
        }
        System.out.println("Las ejecuciones han terminado. Los resultados estarán en los logs junto al ejecutable.");
    }
}
