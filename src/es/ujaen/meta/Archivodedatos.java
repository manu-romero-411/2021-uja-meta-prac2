package es.ujaen.meta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Archivodedatos {

    private String nombre;
    private int matriz1[][];
    private int matriz2[][];

    public Archivodedatos(String rutaarchivo) {
        String linea;
        nombre = rutaarchivo.split(".dat")[0];
        FileReader f = null;
        try {
            f = new FileReader(rutaarchivo);
            BufferedReader b = new BufferedReader(f);
            linea = b.readLine();
            String[] splitTam = linea.split(" ");
            int numero = Integer.parseInt(splitTam[1]);
            matriz1 = new int[numero][numero];
            matriz2 = new int[numero][numero];
            linea = b.readLine();
            for (int i = 0; i < numero; i++) {
                linea = b.readLine();
                String[] split = linea.split(" ");
                int errores = 0;
                for (int j = 0; j < split.length; j++) {
                    try {
                        matriz1[i][j - errores] = Integer.parseInt(split[j]);
                    } catch (NumberFormatException ex) {
                        errores++;
                    }
                }
            }
            linea = b.readLine();
            for (int i = 0; i < numero; i++) {
                linea = b.readLine();
                String[] split = linea.split(" ");
                int errores = 0;
                for (int j = 0; j < split.length; j++) {
                    try {
                        matriz2[i][j - errores] = Integer.parseInt(split[j]);
                    } catch (NumberFormatException ex) {
                        errores++;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public String getNombre() {
        return nombre;
    }

    public int[][] getMatriz1() {
        return matriz1;
    }

    public int[][] getMatriz2() {
        return matriz2;
    }

}
