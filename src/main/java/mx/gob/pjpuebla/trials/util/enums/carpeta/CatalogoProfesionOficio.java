package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoProfesionOficio {
    OCUPACIONES("Ocupaciones"),
    ACT_ADMINISTRATIVAS("Actividades administrativas"),
    COMERCIO("Comercio"),
    VENTAS("Ventas"),
    SERV_PERSONALES("Servicios personales y de vigilancia"),
    PREPARACION_ALIMENTOS("Preparación y servicio de alimentos y bebidas en establecimientos"),
    TRAB_HOGAR("Trabajos en cuidados personales y del hogar"),
    SERV_VIGILANCIA("Servicios de protección y vigilancia"),
    TRABA_ARMADA("Trabajadores en la Armada, Ejército y Fuerza Aérea"),
    TRABA_AGRICOLAS("Trabajadores en actividades agrícolas, ganaderas, forestales, caza y pesca"),
    TRABA_ARTESANALES("Trabajadores artesanales"),
    TRABA_MINERIA("Trabajadores en la minería, construcción en industria"),
    TRABA_CONSTRUCCIONES("Trabajadores en la extracción y edificación de construcciones"),
    ART_TRAB_METAL("Artesanos y trabajadores en el tratamiento y elaboración de productos de metal"),
    ART_TRAB_MADERA("Artesanos y trabajadores en la elaboración de productos de madera, papel, textiles y de cuero de piel"),
    ART_TRAB_HULE("Artesanos y trabajadores en la elaboración de productos de hule, caucho, pláticos y de sustancias químicas"),
    TRAB_ALIMENTOS_BEBIDAS("Trabajadores en la elaboración y procesamiento de alimentos, bebidas y productos de tabaco"),
    ART_TRAB_CERAMICA("Artesanos y trabajadores en la elaboración de productos de cerámica, vidrio, azulejo y similares"),
    OTROS_TRAB_ARTESANALES("Otros trabajos artesanales no clasificados anteriormente"),
    OP_MAQUINARIA("Operadores de maquinaria industrial, ensambladores, choferes y conductores de transporte"),
    OP_INSTALACIONES("Operadores de instalaciones y maquinaria industrial"),
    ENSAMBLADORES("Ensambladores y montadores de herramientas, maquinaria, productos metálicos y electrónicos"),
    CONDUC_TRANSPORTE_MOVIL("Conductores de transporte y de maquinaria móvil"),
    OTROS_OPE_NO_CLASIF("Otros operadores de maquinaria industrial, ensambladores y conductores de transporte, no clasificados anteriormente"),
    VEND_AMBULANTES("Vendedores ambulantes"),
    TRAB_DOMESTICOS("Trabajadores domésticos, de limpieza, planchadores y otros trabajadores de limpieza"),
    TRAB_PAQUETERIA("Trabajadores de paquetería, de apoyo para espectáculos y repartidores de mercancías"),
    OTROS_TRAB_APOYO("Otros trabajadores en actividades elementales y de apoyo, no clasificados anteriormente"),
    FUNC_DIRECTORES("Funcionarios, directores y jefes"),
    FUNC_AUTORIDADES("Funcionarios y altas autoridades de los sectores público, privado y social"),
    DIR_GERENTES_SERVICIOS("Directores y gerentes en servicios financieros, administrativos y sociales"),
    DIR_GERENTES_TECNOLOGIA("Directores y gerentes en producción y tecnología"),
    DIR_GERENTES_VENTAS("Directores y generentes de ventas"),
    COORD_JEFES_SERVICIOS("Coordinadores y jefes de área en servicios financieros, administrativos y sociales"),
    COORD_JEFES_TECNOLOGIA("Coordinadores y jefes de área en producción y tecnología"),
    COORD_JEFES_VENTAS("Coordinadores y jefes de área de ventas, restaurantes, hoteles y otros establecimientos"),
    PROF_TECNICOS("Profesionistas y técnicos"),
    ESP_CIENCIAS_ECONOM("Especialistas en ciencias económico-administrativas, ciencias sociales, humanidades y artes"),
    INV_ESP_CIENCIAS_EXAC("Investigadores y especialistas en ciencias exactas, biológicas, ingeniería, informática y en telecomunicaciones"),
    PROF_ESP_DOCENCIA("Profesores y especialistas en docencia"),
    MED_ENF_ESP_SALUD("Médicos, enfermeras y otros especialistas en salud"),
    AUX_TEC_CIENCIAS_ECONOM("Auxiliares técnicos en ciencias económico-administrativas, ciencias sociales, humanistas y en artes"),
    AUX_TEC_CIENCIAS_EXAC("Auxiliares técnicos en ciencias exactas, biológicas, ingeniería, informática y en telecomunicaciones"),
    AUX_TEC_EDU_INSTRUCTORES("Auxiliares y técnicos en educación, instructores y capacitadores"),
    ENF_TEC_MED_SALUD("Enfermeras, técnicos en medicina y trabajadores de apoyo en salud"),
    OTROS_ESP_TEC_NO_CLASIF("Otros especialistas y técnicos, no clasificados anteriormente"),
    OCUPACIONES_NO_ESPEC("Ocupaciones no especificadas"),
    NO_IDENTIFICADO("No Identificado");

    private final String etiqueta;

    CatalogoProfesionOficio(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
