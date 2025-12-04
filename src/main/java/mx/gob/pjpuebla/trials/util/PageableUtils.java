package mx.gob.pjpuebla.trials.util;

import org.springframework.data.domain.Sort;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PageableUtils {

    /**
     * Ordena una lista en memoria basándose en un objeto Sort de Spring.
     * Soporta cualquier tipo de objeto (T).
     * Maneja nulos enviándolos al final (nullsLast).
     */
    public static <T> void ordenarLista(List<T> lista, Sort sort) {

        if (lista == null || lista.isEmpty() || sort == null || sort.isUnsorted()) {
            return;
        }

        Comparator<T> comparator = null;

        for (Sort.Order order : sort) {

            Comparator<T> propertyComparator = (o1, o2) -> {
                Object v1 = getValorPorReflexion(o1, order.getProperty());
                Object v2 = getValorPorReflexion(o2, order.getProperty());

                return safeCompare(v1, v2);
            };

            if (order.isDescending()) {
                propertyComparator = propertyComparator.reversed();
            }

            if (comparator == null) {
                comparator = propertyComparator;
            } else {
                comparator = comparator.thenComparing(propertyComparator);
            }
        }

        if (comparator != null) {
            lista.sort(comparator);
        }
    }

    private static int safeCompare(Object v1, Object v2) {
        if (v1 == null && v2 == null)
            return 0;
        if (v1 == null)
            return 1; // nulos al final
        if (v2 == null)
            return -1;

        if (v1 instanceof Comparable<?> c1 && v2 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked")
            Comparable<Object> left = (Comparable<Object>) c1;
            return left.compareTo(v2);
        }

        // Si no son comparables, los consideramos "iguales" para efectos de sort
        return 0;
    }

    /**
     * Reflexión: Intenta encontrar el getter o el método del Record.
     */
    private static Object getValorPorReflexion(Object obj, String propertyName) {
        if (obj == null)
            return null;
        try {
            // Intento 1: Convención JavaBeans (getNombre)
            String getterName = "get" + capitalize(propertyName);
            try {
                Method method = obj.getClass().getMethod(getterName);
                return method.invoke(obj);
            } catch (NoSuchMethodException e) {
                // Intento 2: Convención Record (nombre() sin get)
                // O si el campo es booleano "isNombre"
                try {
                    Method method = obj.getClass().getMethod(propertyName);
                    return method.invoke(obj);
                } catch (NoSuchMethodException e2) {
                    // Intento 3: boolean isProperty
                    String isName = "is" + capitalize(propertyName);
                    Method method = obj.getClass().getMethod(isName);
                    return method.invoke(obj);
                }
            }
        } catch (Exception e) {
            // Si falla, retornamos null o loggeamos un warning (evita tronar la app)
            // System.err.println("No se pudo ordenar por: " + propertyName);
            return null;
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Convierte una lista completa en memoria a un objeto Page<T> basado en el
     * Pageable.
     * Se encarga de los cálculos de índices para evitar IndexOutOfBoundsException.
     */
    public static <T> Page<T> crearPagina(List<T> listaCompleta, Pageable pageable) {
        // 1. Validaciones de seguridad
        if (listaCompleta == null) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        if (pageable.isUnpaged()) {
            return new PageImpl<>(listaCompleta);
        }

        // 2. Cálculo de índices
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listaCompleta.size());

        // 3. Evitar romper si piden una página que no existe (start > size)
        if (start > listaCompleta.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, listaCompleta.size());
        }

        // 4. Cortar la lista (SubList)
        List<T> content = listaCompleta.subList(start, end);

        // 5. Retornar el objeto Page oficial de Spring
        return new PageImpl<>(content, pageable, listaCompleta.size());
    }
}