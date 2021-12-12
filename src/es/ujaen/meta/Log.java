package es.ujaen.meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {

    private StringBuilder texto;
    private String ruta;

    public void setModo(String modo) {
        this.modo = modo;
    }

    private String modo;

    public Log(String ruta) {
        this.texto = new StringBuilder();
        this.ruta = ruta;
    }

    public void addTexto(String add) {
        texto.append(add);
    }

    public void guardaLog() {
        if (modo.equals("log")) {
            FileWriter fichero = null;
            PrintWriter pw = null;
            try {
                File carpetaLogs = new File("logs");
                carpetaLogs.mkdir();
                fichero = new FileWriter(ruta + ".txt");
                pw = new PrintWriter(fichero);
                pw.print(texto);
            } catch (IOException e) {

            } finally {
                try {
                    if (fichero != null) {
                        fichero.close();
                    }
                } catch (IOException e2) {
                }
            }
        } else if (modo.equals("stdout")){
            String str = texto.toString();
            System.out.print(str);
        }
    }

    public String getTextoString() {
        return texto.toString();
    }

}

