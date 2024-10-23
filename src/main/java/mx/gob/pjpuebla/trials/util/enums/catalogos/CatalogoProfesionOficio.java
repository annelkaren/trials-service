package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoProfesionOficio implements CatalogoEnum {
    OCUPACIONES(0, "Ocupaciones"),
    ACTIVIDEDES_ADMINISTRATIVAS(1, "Actividades administrativas"),
    COMERCIO(2, "Comercio"),
    VENTAS(3, "Ventas"),
    SERVICIOS_PERSONALES_VIGILANCIA(4, "Servicios personales y de vigilancia"),
    PREPARACION_SERVICIO_ALIMENTOS_BEBIDAS_ESTABLECIMIENTOS(5, "Preparación y servicio de alimentos y bebidas en establecimientos"),
    TRABAJOS_CUIDADOS_PERSONALES_DEL_HOGAR(6, "Trabajos en cuidados personales y del hogar"),
    SERVICIOS_DE_PROTECCION_VIGILANCIA(7, "Servicios de protección y vigilancia"),
    TRABAJADORES_DE_ARMADA_EJERCITO_FUERZA_AEREA(8, "Trabajadores en la Armada, Ejército y Fuerza Aérea"),
    TRABAJADORES_ACTIVIDADES_AGRICOLAS_GANADERAS_FORESTALES_CAZA_PESCA(9, "Trabajadores en actividades agrícolas, ganaderas, forestales, caza y pesca"),
    TRABAJADORES_ARTESANALES(10 , "Trabajadores artesanales"),
    TRABAJADORES_EN_MINERIA_CONSTRUCCION_INDUSTRIA(11, "Trabajadores en la minería, construcción en industria"),
    TRABAJADORES_EN_EXTRACCION_EDIFICACION_CONSTRUCCIONES(12, "Trabajadores en la extracción y edificación de construcciones"),
    ARTESANOS_TRABAJADORES_EN_TRATAMIENTO_ELABORACION_PRODUCTOS_DE_METAL(13, "Artesanos y trabajadores en el tratamiento y elaboración de productos de metal"),
    ARTESANOS_TRABAJADORES_EN_ELABORACION_PRODUCTOS_MADERA_PAPEL_TEXTILES_CUERO_PIEL(14, "Artesanos y trabajadores en la elaboración de productos de madera, papel, textiles y de cuero de piel"),
    ARTESANOS_TRABAJADORES_EN_ELABORACION_PRODUCTOS_HULE_CAUCHO_PLASTICON_DE_SUSTANCIAS_QUIMICAS(15, "Artesanos y trabajadores en la elaboración de productos de hule, caucho, pláticos y de sustancias químicas"),
    TRABAJADORES_EN_ELABORACION_PROCESAMIENTO_ALIMENTOS_BEBIDAS_PRODUCTOS_DE_TABACO(16, "Trabajadores en la elaboración y procesamiento de alimentos, bebidas y productos de tabaco"),
    ARTESANOS_TRABAJADORES_EN_ELABORACION_PRODUCTOS_CERAMIAC_VIDRIO_AZULEJO_SIMILARES(17, "Artesanos y trabajadores en la elaboración de productos de cerámica, vidrio, azulejo y similares"),
    OTROS_TRABAJOS_ARTESANALES_NO_CLASIFICADOS_ANTERIORMENTE(18, "Otros trabajos artesanales no clasificados anteriormente"),
    OPERADORES_MAQUINARIA_INDUSTRIAL_ENSAMBLADORES_CHOFERES_CONDUCTORES_TRANSPORTE(19, "Operadores de maquinaria industrial, ensambladores, choferes y conductores de transporte"),
    OPERADORES_DE_INSTALACIONES_MAQUINARIA_INDUSTRIAL(20, "Operadores de instalaciones y maquinaria industrial"),
    ENSAMBLADORES_MONTADORES_DE_HERAMIENTAS_MAQUINARIA_PRODUCTOS_METALICOS_ELECTRONICOS(21, "Ensambladores y montadores de herramientas, maquinaria, productos metálicos y electrónicos"),
    CONDUCTORES_DE_TRANSPORTE_DE_MAQUINARIA_MOVIL(22, "Conductores de transporte y de maquinaria móvil"),
    OTROS_OPERADORES_DE_MAQUINARIA_INDUSTRIAL_ENSAMBLADORES_CONDUCTORES_TRANSPORTE_NO_CLASIFICADOS_ANTERIORMENTO(23, "Otros operadores de maquinaria industrial, ensambladores y conductores de transporte, no clasificados anteriormente"),
    VENDEDORES_AMBULANTES(24, "Vendedores ambulantes"),
    TRABAJADORES_DOMESTICOS_DE_LIMPIEZA_PLANCHADORES_OTROS_TRABAJADORES_LIMPIEZA(25, "Trabajadores domésticos, de limpieza, planchadores y otros trabajadores de limpieza"),
    TRABAJADORES_DE_PAQUETERIA_DE_APOYO_PARA_ESPECTACULOS_REPARTIDORES_DE_MERCANCIAS(26, "Trabajadores de paquetería, de apoyo para espectáculos y repartidores de mercancías"),
    OTROS_TRABAJADORES_EN_ACTIVIDADES_ELEMENTALES_Y_APOYO_NO_CLASIFICADOS_ANTERIORMENTE(27, "Otros trabajadores en actividades elementales y de apoyo, no clasificados anteriormente"),
    FUNCIONARIOS_DIRECTORES_JEFES(28, "Funcionarios, directores y jefes"),
    FUNCIONARIOS_ALTAS_AUTORIDADES_DE_SECTORES_PUBLICO_PRIVADO_SOCIAL(29, "Funcionarios y altas autoridades de los sectores público, privado y social"),
    DIRECTORES_GERENTES_EN_SERVICIOS_FINANCIEROS_ADMINISTRATIVOS_SOCIALES(30, "Directores y gerentes en servicios financieros, administrativos y sociales"),
    DIRECTORES_GERENTES_EN_PRODUCCION_Y_TECNOLOGIA(31, "Directores y gerentes en producción y tecnología"),
    DIRECTORES_GENERENTES_DE_VEN(32,"Directores y generentes de ven"),
    COORDINADORES_JEFES_DE_AREA_EN_SERVICIOS_FINANCIEROS_ADMINISTRATIVOS_SOCIALES(33, "Coordinadores y jefes de área en servicios financieros, administrativos y sociales"),
    COORDINADORES_JEFES_DE_AREA_PROCUCCION_TECNOLOGIA(34, "Coordinadores y jefes de área en producción y tecnología"),
    COORDINADORES_JEFES_DE_AREA_VESTAS_RESTAURANTES_HOTELES_OTROS_ESTABLECLECIMIENTOS(35, "Coordinadores y jefes de área de ventas, restaurantes, hoteles y otros establecimientos"),
    PROFESIONISTAS_TECNICOS(36, "Profesionistas y técnicos"),
    ESPECIALISTAS_EN_CIENCIAS_ECONOMICO_ADMINISTRATIVAS_CIENCIAS_SOCIALES_HUMANIDADES_ARTES(37, "Especialistas en ciencias económico-administrativas, ciencias sociales, humanidades y artes"),
    INVESTIGADORES_ESPECIALISTAS_EN_CIENCIAS_EXACTAS_BIOLOGICAS_INGENIERIA_IFFORMATICA_TELECOMUNICACIONES(38, "Investigadores y especialistas en ciencias exactas, biológicas, ingeniería, informática y en telecomunicaciones"),
    PROFESORES_ESPECIALISTAS_EN_DOCENCIA(39, "Profesores y especialistas en docencia"),
    MEDICOS_ENFERMERAS_OTROS_ESPECIALISTAS_EN_SALUD(40, "Médicos, enfermeras y otros especialistas en salud"),
    AUXILIARES_TECNICOS_EN_CIENCIAS_ECONOMICO_ADMINISTRATIVAS_CIENCIAS_SOCIALES_HUMANISTAS_EN_ARTES(41, "Auxiliares técnicos en ciencias económico-administrativas, ciencias sociales, humanistas y en artes"),
    AUCILIARES_TECNICOS_EN_CIENCIAS_EXACTAS_BIOLOGICAS_INGENIERIA_INFORMATICA_EN_TELECOMUNICACIONES(42, "Auxiliares técnicos en ciencias exactas, biológicas, ingeniería, informática y en telecomunicaciones"),
    AUXILIARES_TECNICOS_EN_EDUCACION_INSTRUCTORES_CAPACITADORES(43, "Auxiliares y técnicos en educación, instructores y capacitadores"),
    ENFERMERAS_TECNICOS_EN_MEDICINA_TRABAJADORES_DE_APOYO_EN_SALUD(44, "Enfermeras, técnicos en medicina y trabajadores de apoyo en salud"),
    OTROS_ESPECIALISTAS_TECNICOS_NO_CLASIFICADOS_ANTERIORMENTE(45, "Otros especialistas y técnicos, no clasificados anteriormente"),
    OCUPACIONES_NO_ESPECIFICADAS(46 , "Ocupaciones no especificadas"),
    NO_IDENTIFICADO(47, "No Identificado");

    private final int clave;
    private final String valor;

    CatalogoProfesionOficio(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
