/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

enum listaAlgoritmos{AlgGRE,AlgPMDLBit,AlgPMDLBrandom,AlgMA,AGEOX,AGEPMX,AGGOX2,AGGPMX};
/**
 *
 * @author Usuario
 */
public class Configurador {

    private ArrayList<String> archivos;
    private ArrayList<Long> semillas;

    private boolean[] algoritmos;
    private Float oscilacionEstrategica;
    private Float iteracionesEstrategica;
    private Integer iteraciones;
    private Integer longitudLRC;
    private Integer candidatosGreedy;
    private Integer tamLista;
    private String salidaLog;

    private Integer gen_tamPoblacion;
    private Integer gen_numEvaluaciones;
    private Float gen_probCruceGeneracional;
    private Float gen_probCruceEstacionario;
    private Float gen_probMutacion;
    private Integer gen_tamSeleccionGeneracional;
    private Integer gen_tamSeleccionEstacionario;
    private Integer gen_tamTorneoSeleccionGeneracional;
    private Integer gen_tamTorneoSeleccionEstacionario;
    private Integer gen_tamReemplazamientoGeneracional;
    private Integer gen_tamTorneoReemplazamientoEstacionario;
    private Integer gen_vecesTorneoReemplazamientoEstacionario;

    public Configurador(String ruta) {
        archivos = new ArrayList<>();
        semillas = new ArrayList<>();
        algoritmos = new boolean[8];
        for (int i = 0; i < algoritmos.length; ++i){
            algoritmos[i] = false;
        }

        String linea;
        FileReader f = null;
        try {
            f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while ((linea = b.readLine()) != null) {
                String[] split = linea.split("=");
                switch (split[0]) {
                    case "Archivos":
                        String[] v = split[1].split(" ");
                        for (int i = 0; i < v.length; i++) {
                            archivos.add("data/" + v[i]);
                        }
                        break;

                    case "Semillas":
                        String[] vsemillas = split[1].split(" ");
                        for (int i = 0; i < vsemillas.length; i++) {
                            semillas.add(Long.parseLong(vsemillas[i]));
                        }
                        break;
                    case "Algoritmos":
                        String[] vAlg = split[1].split(",");
                        vAlg[vAlg.length-1] = vAlg[vAlg.length-1].split(" ")[0];
                        for (int i = 0; i < vAlg.length; i++) {
                            switch(vAlg[i]){
                                case "AlgGRE":
                                    algoritmos[0] = true;
                                    break;
                                case "greedy":
                                    algoritmos[0] = true;
                                    break;
                                case "AlgPMDLBit":
                                    algoritmos[1] = true;
                                    break;
                                case "primerMejorIterativo":
                                    algoritmos[1] = true;
                                    break;
                                case "AlgPMDLBrandom":
                                    algoritmos[2] = true;
                                    break;
                                case "primerMejorRandom":
                                    algoritmos[2] = true;
                                    break;
                                case "AlgMA":
                                    algoritmos[3] = true;
                                    break;
                                case "multiarranque":
                                    algoritmos[3] = true;
                                    break;
                                case "AGEOX":
                                    algoritmos[4] = true;
                                    break;
                                case "estacionarioOX":
                                    algoritmos[4] = true;
                                    break;
                                case "AGEPMX":
                                    algoritmos[5] = true;
                                    break;
                                case "estacionarioPMX":
                                    algoritmos[5] = true;
                                    break;
                                case "AGGOX2":
                                    algoritmos[6] = true;
                                    break;
                                case "generacionalOX2":
                                    algoritmos[6] = true;
                                    break;
                                case "AGGPMX":
                                    algoritmos[7] = true;
                                    break;
                                case "generacionalPMX":
                                    algoritmos[7] = true;
                                    break;
                            }
                        }
                        break;

                    case "SalidaLog":
                        salidaLog = split[1];
                        if (salidaLog.equals("log"))
                            if (salidaLog.equals("stdout"))
                                throw new LogInvalidoException("Debes poner en config.txt si quieres salida por \"log\" o por \"stdout\"");
                        break;

                    case "Iteraciones":
                        iteraciones = Integer.parseInt(split[1]);
                        break;

                    case "MA-LonguitudLRC":
                        longitudLRC = Integer.parseInt(split[1]);
                        break;

                    case "MA-CandidatosGreedy":
                        candidatosGreedy = Integer.parseInt(split[1]);
                        break;

                    case "MA-TamListaTabu":
                        tamLista = Integer.parseInt(split[1]);
                        break;

                    case "MA-OscilacionEstrategica":
                        oscilacionEstrategica = Float.parseFloat(split[1]);
                        break;

                    case "MA-IteracionesOscilacion":
                        iteracionesEstrategica = Float.parseFloat(split[1]);
                        break;

                    case "Gen-TamPoblacion":
                        gen_tamPoblacion = Integer.parseInt(split[1]);
                        break;
                    case "Gen-NumEvaluaciones":
                        gen_numEvaluaciones = Integer.parseInt(split[1]);
                        break;
                    case "Gen-ProbCruceGeneracional":
                        gen_probCruceGeneracional = Float.parseFloat(split[1]);
                        break;
                    case "Gen-ProbCruceEstacionario":
                        gen_probCruceEstacionario = Float.parseFloat(split[1]);
                        break;
                    case "Gen-FactorProbabilidadMutacion":
                        gen_probMutacion = Float.parseFloat(split[1]) * gen_tamPoblacion;
                        break;
                    case "Gen-TamSeleccionGeneracional":
                        gen_tamSeleccionGeneracional = Integer.parseInt(split[1]);
                        break;
                    case "Gen-TamSeleccionEstacionario":
                        gen_tamSeleccionEstacionario = Integer.parseInt(split[1]);
                        break;
                    case "Gen-TamTorneoSeleccionGeneracional":
                        gen_tamTorneoSeleccionGeneracional = Integer.parseInt(split[1]);
                        break;
                    case "Gen-TamTorneoSeleccionEstacionario":
                        gen_tamTorneoSeleccionEstacionario = Integer.parseInt(split[1]);
                        break;
                    case "Gen-TamReemplazamientoGeneracional":
                        gen_tamReemplazamientoGeneracional = Integer.parseInt(split[1]);
                        break;
                    case "Gen-TamTorneoReemplazamientoEstacionario":
                        gen_tamTorneoReemplazamientoEstacionario = Integer.parseInt(split[1]);
                        break;
                    case "Gen-VecesTorneoReemplazamientoEstacionario":
                        gen_vecesTorneoReemplazamientoEstacionario = Integer.parseInt(split[1]);
                        break;
                }
            }

        } catch (IOException | LogInvalidoException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> getArchivos() {
        return archivos;
    }

    public boolean[] getAlgoritmos() {
        return algoritmos;
    }

    public ArrayList<Long> getSemillas() {
        return semillas;
    }

    public Integer getTamLista() {
        return tamLista;
    }

    public Integer getIteraciones() {
        return iteraciones;
    }

    public String getSalidaLog() {
        return salidaLog;
    }

    public Integer getCandidatosGreedy() {
        return candidatosGreedy;
    }

    public Float getIteracionesEstrategica() {
        return iteracionesEstrategica;
    }

    public Integer getLongitudLRC() {
        return longitudLRC;
    }

    public Float getOscilacionEstrategica() {
        return oscilacionEstrategica;
    }

    /**
     * @return the gen_tamPoblacion
     */
    public Integer getGenTamPoblacion() {
        return getGen_tamPoblacion();
    }

    /**
     * @return the gen_numEvaluaciones
     */
    public Integer getGenNumEvaluaciones() {
        return getGen_numEvaluaciones();
    }

    /**
     * @return the gen_probCruceGeneracional
     */
    public Float getGenProbCruceGeneracional() {
        return getGen_probCruceGeneracional();
    }

    /**
     * @return the gen_probCruceEstacionario
     */
    public Float getGenProbCruceEstacionario() {
        return getGen_probCruceEstacionario();
    }

    /**
     * @return the gen_probMutacion
     */
    public Float getGenProbMutacion() {
        return getGen_probMutacion();
    }

    public Integer getGen_tamSeleccionEstacionario() {
        return gen_tamSeleccionEstacionario;
    }

    public Integer getGen_tamTorneoSeleccionEstacionario() {
        return gen_tamTorneoSeleccionEstacionario;
    }

    public Integer getGen_tamTorneoReemplazamientoEstacionario() {
        return gen_tamTorneoReemplazamientoEstacionario;
    }
    
    /**
     * @return the gen_tamPoblacion
     */
    public Integer getGen_tamPoblacion() {
        return gen_tamPoblacion;
    }

    /**
     * @return the gen_numEvaluaciones
     */
    public Integer getGen_numEvaluaciones() {
        return gen_numEvaluaciones;
    }

    /**
     * @return the gen_probCruceGeneracional
     */
    public Float getGen_probCruceGeneracional() {
        return gen_probCruceGeneracional;
    }

    /**
     * @return the gen_probCruceEstacionario
     */
    public Float getGen_probCruceEstacionario() {
        return gen_probCruceEstacionario;
    }

    /**
     * @return the gen_probMutacion
     */
    public Float getGen_probMutacion() {
        return gen_probMutacion;
    }

    /**
     * @return the gen_tamSeleccionGeneracional
     */
    public Integer getGen_tamSeleccionGeneracional() {
        return gen_tamSeleccionGeneracional;
    }

    /**
     * @return the gen_tamTorneoSeleccionGeneracional
     */
    public Integer getGen_tamTorneoSeleccionGeneracional() {
        return gen_tamTorneoSeleccionGeneracional;
    }

    /**
     * @return the gen_tamReemplazamientoGeneracional
     */
    public Integer getGen_tamReemplazamientoGeneracional() {
        return gen_tamReemplazamientoGeneracional;
    }

    /**
     * @return the gen_vecesTorneoReemplazamientoEstacionario
     */
    public Integer getGen_vecesTorneoReemplazamientoEstacionario() {
        return gen_vecesTorneoReemplazamientoEstacionario;
    }
}

class LogInvalidoException extends Exception {
    public LogInvalidoException(String message) {
        super(message);
    }
}
