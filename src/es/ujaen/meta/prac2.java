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

        for(int i = 0; i < config.getSemillas().size(); ++i){
            Random random = new Random(config.getSemillas().get(i));
            for(int j = 0; j < arrayA.size(); ++j){
                System.out.println("Ejecución " + i + " para archivo ");
            }
        }

        System.out.println("GENETICO");
        System.out.println("GENÉTICO ESTACIONARIO PMX");
  /*      for (int i = 0; i < arrayA.size(); i++) {
            AGEPMX_Clase3_Grupo9 genetico = new AGEPMX_Clase3_Grupo9(random, config.getSemillas().get(0), config.getLonguitudLRC(), arrayA.get(i), config.getGenTamPoblacion(),
                    config.getGenNumEvaluaciones(), config.getGenProbCruceEstacionaio(), config.getGenProbMutacion(), config.getGen_tamSeleccionEstacionario(),
                    config.getGen_tamTorneoSeleccionEstacionario(), config.getGen_tamTorneoReemplazamientoEstacionario(), config.getGen_vecesTorneoReemplazamientoEstacionario() config.getSalidaLog);
            genetico.hazGeneticoEstacionario();
        }

        System.out.println("GENÉTICO ESTACIONARIO OX");
        for (int i = 0; i < arrayA.size(); i++) {
            AGEOX_Clase3_Grupo9 genetico = new AGEOX_Clase3_Grupo9(random, config.getSemillas().get(0), config.getLonguitudLRC(), arrayA.get(i), config.getGenTamPoblacion(),
                    config.getGenNumEvaluaciones(), config.getGenProbCruceEstacionario(), config.getGenProbMutacion(), config.getGen_tamSeleccionEstacionario(),
                    config.getGen_tamTorneoSeleccionEstacionario(), config.getGen_tamTorneoReemplazamientoEstacionario(), config.getGen_vecesTorneoReemplazamientoEstacionario(), config.getSalidaLog());
            genetico.hazGeneticoEstacionario();
        }*/
        System.out.println("GENÉTICO GENERACIONAL OX2");
        for (int i = 0; i < arrayA.size(); i++) {
            AGGOX2_Clase3_Grupo9 genetico = new AGGOX2_Clase3_Grupo9(new Random(config.getSemillas().get(0)), config.getSemillas().get(0), config.getLonguitudLRC(), arrayA.get(i), config.getGenTamPoblacion(),
                    config.getGenNumEvaluaciones(), config.getGenProbCruceGeneracional(), config.getGenProbMutacion(), config.getGen_tamSeleccionGeneracional(),
                    config.getGen_tamTorneoSeleccionGeneracional(), config.getGen_tamReemplazamientoGeneracional(), config.getSalidaLog());
            genetico.hazGeneticoGeneracional();
        }
    }
}
