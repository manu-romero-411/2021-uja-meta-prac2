/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

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
        System.out.println(config.getArchivos());

        //Añade a la lista de archivos los diferentes archivos de datos
        for (int i = 0; i < config.getArchivos().size(); i++) {
            Archivodedatos archivo = new Archivodedatos(config.getArchivos().get(i));
            arrayA.add(archivo);
        }

        // EJECUTAMOS LOS ALGORITMOS INDICADOS EN EL config.txt, CON LAS SEMILLAS INDICADAS, Y SOBRE LOS ARCHIVOS INDICADOS.
        for (int i = 0; i < config.getSemillas().size(); ++i) {
            Random random = new Random(config.getSemillas().get(i));
            for (int j = 0; j < arrayA.size(); ++j) {
                if (config.getAlgoritmos()[4]) {
                    System.out.println("Ejecución " + i + " del algoritmo genético estacionario (cruce OX) para archivo " + arrayA.get(j).getNombre().split("/"));
                    AGE_OX_Clase3_Grupo9 genetico = new AGE_OX_Clase3_Grupo9(random, config.getSemillas().get(i), config.getLongitudLRC(), arrayA.get(j), config.getGenTamPoblacion(),
                            config.getGenNumEvaluaciones(), config.getGenProbCruceEstacionario(), config.getGenProbMutacion(), config.getGen_tamSeleccionEstacionario(),
                            config.getGen_tamTorneoSeleccionEstacionario(), config.getGen_tamTorneoReemplazamientoEstacionario(), config.getGen_vecesTorneoReemplazamientoEstacionario(), config.getSalidaLog());
                    genetico.hazGeneticoEstacionario();
                }
                if (config.getAlgoritmos()[5]) {
                    System.out.println("Ejecución " + i + " del algoritmo genético estacionario (cruce PMX) para archivo " + arrayA.get(j).getNombre().split("/"));
                    AGE_PMX_Clase3_Grupo9 genetico = new AGE_PMX_Clase3_Grupo9(random, config.getSemillas().get(i), config.getLongitudLRC(), arrayA.get(j), config.getGenTamPoblacion(),
                            config.getGenNumEvaluaciones(), config.getGenProbCruceEstacionario(), config.getGenProbMutacion(), config.getGen_tamSeleccionEstacionario(),
                            config.getGen_tamTorneoSeleccionEstacionario(), config.getGen_tamTorneoReemplazamientoEstacionario(), config.getGen_vecesTorneoReemplazamientoEstacionario(), config.getSalidaLog());
                    genetico.hazGeneticoEstacionario();
                }
                if (config.getAlgoritmos()[6]) {
                    System.out.println("Ejecución " + i + " del algoritmo genético generacional (cruce OX2) para archivo " + arrayA.get(j).getNombre().split("/"));
                    AGG_OX2_Clase3_Grupo9 genetico = new AGG_OX2_Clase3_Grupo9(random, config.getSemillas().get(i), config.getLongitudLRC(), arrayA.get(j), config.getGenTamPoblacion(),
                            config.getGenNumEvaluaciones(), config.getGenProbCruceGeneracional(), config.getGenProbMutacion(), config.getGen_tamSeleccionGeneracional(),
                            config.getGen_tamTorneoSeleccionGeneracional(), config.getGen_tamReemplazamientoGeneracional(), config.getSalidaLog());
                    genetico.hazGeneticoGeneracional();
                }
                if (config.getAlgoritmos()[7]) {
                    System.out.println("Ejecución " + i + " del algoritmo genético generacional (cruce PMX) para archivo " + arrayA.get(j).getNombre().split("/"));
                    AGG_PMX_Clase3_Grupo9 genetico = new AGG_PMX_Clase3_Grupo9(random, config.getSemillas().get(i), config.getLongitudLRC(), arrayA.get(j), config.getGenTamPoblacion(),
                            config.getGenNumEvaluaciones(), config.getGenProbCruceGeneracional(), config.getGenProbMutacion(), config.getGen_tamSeleccionGeneracional(),
                            config.getGen_tamTorneoSeleccionGeneracional(), config.getGen_tamReemplazamientoGeneracional(), config.getSalidaLog());
                    genetico.hazGeneticoGeneracional();
                }
            }
        }
    }
}
