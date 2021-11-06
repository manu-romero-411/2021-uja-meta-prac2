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
public class prac2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Configurador config = new Configurador(args[0]);
        ArrayList<Archivodedatos> arrayA = new ArrayList<>();
        Log log = new Log(config.getSalidaLog());
        Random random = new Random(config.getSemillas().get(0));
        System.out.println(config.getArchivos());

        //Añade a la lista de archivos los diferentes archivos de datos
        for (int i = 0; i < config.getArchivos().size(); i++) {
            Archivodedatos archivo = new Archivodedatos(config.getArchivos().get(i));
            arrayA.add(archivo);
        }

        System.out.println("GENETICO");
        for (int i = 0; i < arrayA.size(); i++) {
            AGE_Clase3_Grupo9 genetico = new AGE_Clase3_Grupo9(random,config.getLonguitudLRC(),arrayA.get(i),config.getGenTamPoblacion(),config.getGenNumEvaluaciones(),config.getGenProbCruceEstacionario(),config.getGenProbMutacion());
            genetico.hazGeneticoEstacionario();
            //System.out.print(genetico.muestraDatos());
            //log.addTexto(genetico.muestraDatos());
        }

        log.guardaLog();
    }

    public static void guardarArchivo(String ruta, String texto) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(ruta);
            pw = new PrintWriter(fichero);
            pw.print(texto);
        } catch (IOException e) {

        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException e2) {
            }
        }
    }

    public static void muestraArray(int array[]) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf(array[i] + " ");
        }
        System.out.println("");
    }

    public static void muestraArray(ArrayList<Integer> array) {
        for (int i = 0; i < array.size(); i++) {
            System.out.printf(array.get(i) + " ");
        }
        System.out.println("");
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
