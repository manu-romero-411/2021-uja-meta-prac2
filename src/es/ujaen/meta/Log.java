/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ujaen.meta;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author admin
 */
public class Log {

    private StringBuilder texto;
    private String ruta;

    public Log(String ruta) {
        this.texto = new StringBuilder();
        this.ruta = ruta;
    }

    public void addTexto(String add) {
        texto.append(add);
    }

    public void guardaLog() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(ruta + ".txt");
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

    public String getTextoString() {
        return texto.toString();
    }

}
