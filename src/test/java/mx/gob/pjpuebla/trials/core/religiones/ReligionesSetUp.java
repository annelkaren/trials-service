package mx.gob.pjpuebla.trials.core.religiones;

public class ReligionesSetUp {

    private ReligionesSetUp(){

    }

    public static Religiones createReligiones(){
        return new Religiones()
                .setId(1)
                .setNombre("Católica");
    }
}
