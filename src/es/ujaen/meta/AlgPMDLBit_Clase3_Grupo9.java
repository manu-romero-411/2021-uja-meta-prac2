/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class AlgPMDLBit_Clase3_Grupo9 {

    private final long inicio;
    private long fin;
    private ArrayList<Integer> conjunto;
    private int mejorCoste;
    private final Archivodedatos archivo;
    private final int iteraciones;
    private final ArrayList<Boolean> dlb;
    private boolean flagMejora;

    public AlgPMDLBit_Clase3_Grupo9(Archivodedatos archivo, int iteraciones) {
        this.conjunto = new ArrayList<>();
        this.mejorCoste = 0;
        this.archivo = archivo;
        this.iteraciones = iteraciones;
        this.dlb = new ArrayList<>();
        this.flagMejora = true;
        this.inicio = System.currentTimeMillis();
    }

    // Calcula el primero el mejor iterativo
    public void calculaPrimeroElMejor() {
        AlgGRE_Clase3_Grupo9 greedyA = new AlgGRE_Clase3_Grupo9(archivo);
        greedyA.calculaGreedy();
        this.conjunto = greedyA.getConjunto();
        this.mejorCoste = greedyA.getCosteConjunto();
        System.out.println("El mejor coste de " + archivo.getNombre() + " es: " + mejorCoste);
        for (int i = 0; i < conjunto.size(); i++) {
            dlb.add(false);
        }
        mejora();
        mejorCoste = greedyA.calculaCosteConjunto(conjunto, archivo.getMatriz1(), archivo.getMatriz2());
        //muestraDatos();
    }

    private void mejora() {
        boolean dlbCompleto = false;
        int ultMov = 0;
        int cam = 0;
        int k = 0;
        while (k < iteraciones && !dlbCompleto) {
            int i = ultMov;
            if (dlb.get(i % dlb.size()) == false) {
                flagMejora = false;
                int contJ = dlb.size() - 1;
                for (int j = ((i + 1) % dlb.size()); contJ > 0 && flagMejora == false; j++) {
                    if (i % dlb.size() != j % dlb.size()) {
                        if (checkMove(i % dlb.size(), j % dlb.size())) {
                            applyMove(i % dlb.size(), j % dlb.size());
                            dlb.set(i % dlb.size(), false);
                            dlb.set(j % dlb.size(), false);
                            flagMejora = true;
                            ultMov = conjunto.get(j % dlb.size());
                            cam++;
                        }
                    }
                    if (compruebaDLB()) {
                        dlbCompleto = true;
                    }
                    contJ--;
                }

                if (flagMejora == false) {
                    dlb.set(i % dlb.size(), true);
                }
            }
            if (compruebaDLB()) {
                dlbCompleto = true;
            }
            ultMov = (ultMov + 1) % conjunto.size();
            k++;
        }
        System.out.println("ITERACIONES BUENAS: " + cam);
    }

    private boolean compruebaDLB() {
        for (int i = 0; i < dlb.size(); i++) {
            if (!dlb.get(i)) {
                return false;
            }
        }
        return true;
    }

    // Muestra los datos (futuro log)
    public String muestraDatos() {
        fin = System.currentTimeMillis();
        String aux = new String();
        for (int i = 0; i < conjunto.size(); i++) {
            aux += conjunto.get(i) + "  ";
        }
        System.out.println();
        return "PRIMERO EL MEJOR IT \nEl conjunto de archivos de datos " + archivo.getNombre() + " tiene un coste de " + mejorCoste
                + " con un tiempo de ejecucion de: " + (fin - inicio) + " milisegundos y es el siguiente: \n" + aux + "\n";
    }

    // Comprueba si el movimiento mejora
    private boolean checkMove(int r, int s) {
        int matrizF[][] = archivo.getMatriz1();
        int matrizD[][] = archivo.getMatriz2();
        int sum = 0;

        for (int k = 0; k < matrizF.length; k++) {
            if (k != r && k != s) {
                sum += ((matrizF[s][k] * (matrizD[conjunto.get(r)][conjunto.get(k)] - matrizD[conjunto.get(s)][conjunto.get(k)]))
                        + (matrizF[r][k] * (matrizD[conjunto.get(s)][conjunto.get(k)] - matrizD[conjunto.get(r)][conjunto.get(k)]))
                        + (matrizF[k][s] * (matrizD[conjunto.get(k)][conjunto.get(r)] - matrizD[conjunto.get(k)][conjunto.get(s)]))
                        + (matrizF[k][r] * (matrizD[conjunto.get(k)][conjunto.get(s)] - matrizD[conjunto.get(k)][conjunto.get(r)])));
            }
        }
        sum += (matrizF[r][r] * (matrizD[conjunto.get(s)][conjunto.get(s)] - matrizD[conjunto.get(r)][conjunto.get(r)]))
                + (matrizF[s][s] * (matrizD[conjunto.get(r)][conjunto.get(r)] - matrizD[conjunto.get(s)][conjunto.get(s)]))
                + (matrizF[r][s] * (matrizD[conjunto.get(s)][conjunto.get(r)] - matrizD[conjunto.get(s)][conjunto.get(r)]))
                + (matrizF[s][r] * (matrizD[conjunto.get(r)][conjunto.get(s)] - matrizD[conjunto.get(s)][conjunto.get(r)]));

        return (sum < 0);
    }

    // Aplica el movimiento de mejora
    private void applyMove(int r, int s) {
        int valorR = conjunto.get(r);
        conjunto.set(r, conjunto.get(s));
        conjunto.set(s, valorR);
    }

    public ArrayList<Integer> getConjuntos() {
        return conjunto;
    }

}
