package es.ujaen.meta;

import java.util.ArrayList;
import java.util.Random;

public class AlgPMDLBrandom_Clase3_Grupo9 {

    private final long inicio;
    private long fin;
    private ArrayList<Integer> conjunto;
    private int mejorCoste;
    private final Archivodedatos archivo;
    private final int iteraciones;
    private final Random random;
    private ArrayList<Boolean> dlb;
    private boolean flagMejora;

    public AlgPMDLBrandom_Clase3_Grupo9(Archivodedatos archivo, int iteraciones, Random random) {
        this.conjunto = new ArrayList<>();
        this.mejorCoste = 0;
        this.archivo = archivo;
        this.iteraciones = iteraciones;
        this.random = random;
        this.dlb = new ArrayList<>();
        this.flagMejora = true;
        this.inicio = System.currentTimeMillis();
    }

    // Calcula el primero el mejor random
    public void calculaPrimeroElMejor() {
        AlgGRE_Clase3_Grupo9 greedy = new AlgGRE_Clase3_Grupo9(archivo);
        greedy.calculaGreedy();
        this.conjunto = greedy.getConjunto();
        this.mejorCoste = greedy.getCosteConjunto();
        iniciaDLB();
        mejora();
        mejorCoste = calculaCosteConjunto(conjunto);
    }

    private void iniciaDLB() {
        for (int i = 0; i < conjunto.size(); i++) {
            dlb.add(false);
        }
    }

    private void mejora() {
        int ultMov = random.nextInt(dlb.size());
        boolean dlbCompleto = false;
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
                        }
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
    }

    public int calculaCosteConjunto(ArrayList<Integer> conjunto) {
        int coste = 0;
        for (int i = 0; i < conjunto.size(); i++) {
            for (int j = 0; j < conjunto.size(); j++) {
                coste += archivo.getMatriz1()[i][j] * archivo.getMatriz2()[conjunto.get(i)][conjunto.get(j)];
            }
        }
        return coste;
    }

    public String muestraDatos() {
        fin = System.currentTimeMillis();
        String aux = new String();
        for (int i = 0; i < conjunto.size(); i++) {
            aux += conjunto.get(i) + "  ";
        }
        //System.out.println();
        return "PRIMERO EL MEJOR RANDOM \nEl conjunto de archivos de datos " + archivo.getNombre() + " tiene un coste de " + mejorCoste
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

    private boolean compruebaDLB() {
        for (int i = 0; i < dlb.size(); i++) {
            if (!dlb.get(i)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Integer> getConjuntos() {
        return conjunto;
    }

}
