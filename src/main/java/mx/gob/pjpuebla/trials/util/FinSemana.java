package mx.gob.pjpuebla.trials.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class FinSemana {

    private FinSemana(){

    }

    public static Boolean esInhabil(LocalDate fecha){
        return fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static LocalDate proximoDiaHabil(LocalDate fecha){
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY){
            return fecha.plusDays(2);
        }

        return fecha.plusDays(1);
    }
}
