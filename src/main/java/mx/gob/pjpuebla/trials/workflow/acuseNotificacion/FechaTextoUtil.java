package mx.gob.pjpuebla.trials.workflow.acuseNotificacion;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class FechaTextoUtil {

    public static String obtenerFechaEnTexto() {

        LocalDateTime now = LocalDateTime.now();

        int hora = now.getHour();
        int minuto = now.getMinute();
        int dia = now.getDayOfMonth();
        int anio = now.getYear();
        Month mes = now.getMonth();

        String mesTexto = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        return numeroATexto(hora) + " horas "
                + numeroATexto(minuto) + " minutos del "
                + numeroATexto(dia) + " de "
                + mesTexto + " de "
                + numeroATexto(anio);
    }

    private static String numeroATexto(int numero) {

        String[] unidades = {
                "cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve",
                "diez","once","doce","trece","catorce","quince","dieciséis","diecisiete",
                "dieciocho","diecinueve","veinte","veintiuno","veintidós","veintitrés",
                "veinticuatro","veinticinco","veintiséis","veintisiete","veintiocho","veintinueve"
        };

        String[] decenas = {
                "", "", "veinte", "treinta", "cuarenta", "cincuenta",
                "sesenta", "setenta", "ochenta", "noventa"
        };

        String[] centenas = {
                "", "ciento", "doscientos", "trescientos", "cuatrocientos",
                "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
        };

        if (numero < 30) {
            return unidades[numero];
        }

        if (numero < 100) {
            int d = numero / 10;
            int u = numero % 10;

            if (u == 0) return decenas[d];

            return decenas[d] + " y " + unidades[u];
        }

        if (numero == 100) {
            return "cien";
        }

        if (numero < 1000) {
            int c = numero / 100;
            int resto = numero % 100;

            if (resto == 0) return centenas[c];

            return centenas[c] + " " + numeroATexto(resto);
        }

        if (numero < 2000) {
            return "mil " + numeroATexto(numero % 1000);
        }

        if (numero < 1000000) {
            int miles = numero / 1000;
            int resto = numero % 1000;

            String milesTexto = numeroATexto(miles) + " mil";

            if (resto == 0) return milesTexto;

            return milesTexto + " " + numeroATexto(resto);
        }

        return String.valueOf(numero);
    }
}