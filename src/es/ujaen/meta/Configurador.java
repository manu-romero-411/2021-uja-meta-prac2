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

/**
 *
 * @author Usuario
 */
public class Configurador {

    private ArrayList<String> archivos;
    private ArrayList<Long> semillas;
    private Float oscilacionEstrategica;
    private Float iteracionesEstrategica;
    private Integer iteraciones;
    private Integer LonguitudLRC;
    private Integer candidatosGreedy;
    private Integer tamLista;
    private String salidaLog;

    private int gen_tamPoblacion;
    private int gen_numEvaluaciones;
    private float gen_probCruceGeneracional;
    private float gen_probCruceEstacionario;
    private float gen_probMutacion;

    public Configurador(String ruta) {
        archivos = new ArrayList<>();
        semillas = new ArrayList<>();

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

                    case "MA-LonguitudLRC":
                        LonguitudLRC = Integer.parseInt(split[1]);
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

                    case "Iteraciones":
                        iteraciones = Integer.parseInt(split[1]);
                        break;

                    case "SalidaLog":
                        salidaLog = split[1];
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
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> getArchivos() {
        return archivos;
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

    public Integer getLonguitudLRC() {
        return LonguitudLRC;
    }

    public Float getOscilacionEstrategica() {
        return oscilacionEstrategica;
    }

    /**
     * @return the gen_tamPoblacion
     */
    public int getGenTamPoblacion() {
        return gen_tamPoblacion;
    }

    /**
     * @return the gen_numEvaluaciones
     */
    public int getGenNumEvaluaciones() {
        return gen_numEvaluaciones;
    }


    /**
     * @return the gen_probCruceGeneracional
     */
    public float getGenProbCruceGeneracional() {
        return gen_probCruceGeneracional;
    }

    /**
     * @return the gen_probCruceEstacionario
     */
    public float getGenProbCruceEstacionario() {
        return gen_probCruceEstacionario;
    }

    /**
     * @return the gen_probMutacion
     */
    public float getGenProbMutacion() {
        return gen_probMutacion;
    }



}
