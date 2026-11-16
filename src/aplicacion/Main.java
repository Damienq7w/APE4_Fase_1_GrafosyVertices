package aplicacion;

import estructuras.RedSocialGrafo;
import java.util.Scanner;

/**
 * APE 4 - Fase 1: Reto de grafos y vertices. Programa principal.
 * Menu interactivo (switch) sobre la red social "Los Enlazados" y resolucion del reto
 * (6 preguntas + 19 verificaciones automaticas).
 *
 * Compilar:  javac -d bin src/modelo/*.java src/estructuras/*.java src/aplicacion/*.java
 * Menu:      java -cp bin aplicacion.Main
 * Reto:      java -cp bin aplicacion.Main --reto
 *
 * Autor: Cunalata Mendoza Damian Alexander
 */
public class Main {

    // menu interactivo
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--reto")) {
            System.exit(reto() == 0 ? 0 : 1);
        }
        Scanner sc = new Scanner(System.in);
        RedSocialGrafo red = RedSocialGrafo.losEnlazados();
        System.out.println("==================================================");
        System.out.println("   RED SOCIAL \"LOS ENLAZADOS\" - APE 4 FASE 1");
        System.out.println("==================================================");
        System.out.println("Se cargo la red del reto: 6 usuarios y 5 amistades.");

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero(sc, "Opcion: ", 0, 10);
            System.out.println();
            switch (opcion) {
                case 1 -> {
                    String id = leerTexto(sc, "ID de la persona (ej. U7): ");
                    String nombre = leerTexto(sc, "Nombre: ");
                    System.out.println(red.registrarPersona(id, nombre));
                }
                case 2 -> {
                    String a = leerTexto(sc, "ID del primer usuario: ");
                    String b = leerTexto(sc, "ID del segundo usuario: ");
                    System.out.println(red.proponerAmistad(a, b));
                }
                case 3 -> {
                    String a = leerTexto(sc, "ID del primer usuario: ");
                    String b = leerTexto(sc, "ID del segundo usuario: ");
                    try {
                        System.out.println("existeRelacion(" + a + ", " + b + ") = "
                                + (red.existeRelacion(a, b) ? "verdadero (son amigos)" : "falso (no son amigos)"));
                    } catch (IllegalArgumentException e) {
                        System.out.println("ERROR de validacion: " + e.getMessage());
                    }
                }
                case 4 -> {
                    String id = leerTexto(sc, "ID del usuario: ");
                    try {
                        System.out.println("vecinos(" + id + ") = " + red.vecinos(id) + " ; grado(" + id + ") = " + red.grado(id));
                    } catch (IllegalArgumentException e) {
                        System.out.println("ERROR de validacion: " + e.getMessage());
                    }
                }
                case 5 -> {
                    red.mostrarVE();
                    red.mostrarListas();
                }
                case 6 -> red.mostrarMatriz();
                case 7 -> red.mostrarGrados();
                case 8 -> red.mostrarCicloYComponentes();
                case 9 -> reto();
                case 10 -> {
                    red = RedSocialGrafo.losEnlazados();
                    System.out.println("Red reiniciada: 6 usuarios y 5 amistades.");
                }
                case 0 -> System.out.println("Programa finalizado.");
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("------------------ MENU ------------------");
        System.out.println(" 1. Registrar persona");
        System.out.println(" 2. Proponer amistad");
        System.out.println(" 3. Consultar existeRelacion");
        System.out.println(" 4. Ver vecinos y grado de un usuario");
        System.out.println(" 5. Mostrar G = (V, E) y listas de adyacencia");
        System.out.println(" 6. Mostrar matriz de adyacencia y densidad");
        System.out.println(" 7. Mostrar grados (aislado, mayor grado, suma)");
        System.out.println(" 8. Buscar ciclo y componentes conexos");
        System.out.println(" 9. Resolver el reto (6 preguntas + verificacion)");
        System.out.println("10. Reiniciar la red \"Los Enlazados\"");
        System.out.println(" 0. Salir");
        System.out.println("------------------------------------------");
    }

    /** Lee un entero entre min y max; repite si la entrada es invalida. Al terminar la entrada devuelve 0. */
    private static int leerEntero(Scanner sc, String mensaje, int min, int max) {
        while (true) {
            String linea = leerTexto(sc, mensaje);
            if (linea == null) {
                return 0;
            }
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("Error: ingrese un numero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: '" + linea + "' no es un numero.");
            }
        }
    }

    /** Lee una linea (sin espacios al inicio y al final). Devuelve null si ya no hay entrada. */
    private static String leerTexto(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        if (!sc.hasNextLine()) {
            System.out.println();
            return null;
        }
        String linea = sc.nextLine().trim();
        if (System.console() == null) {
            System.out.println(linea);   // con entrada desde archivo, repite lo leido para que la salida se entienda
        }
        return linea;
    }

    // reto: 6 preguntas + verificaciones
    private static int aprobadas;
    private static int fallidas;

    /** Responde las 6 preguntas del reto sobre una red nueva y verifica las condiciones. Devuelve las fallidas. */
    public static int reto() {
        aprobadas = 0;
        fallidas = 0;
        RedSocialGrafo red = RedSocialGrafo.losEnlazados();

        titulo("1) G = (V, E)");
        red.mostrarVE();
        verificar("Exactamente 6 vertices", red.getNumPersonas() == 6);
        verificar("Exactamente 5 aristas, contadas una vez", red.getNumAmistades() == 5);

        titulo("2) Simple, no dirigido y no ponderado");
        red.mostrarMatriz();
        int[][] m = red.matrizAdyacencia();
        boolean sinLazos = true;
        boolean simetrica = true;
        for (int i = 0; i < m.length; i++) {
            sinLazos &= m[i][i] == 0;
            for (int j = 0; j < m.length; j++) {
                simetrica &= m[i][j] == m[j][i];
            }
        }
        verificar("Simple: diagonal en 0 (sin lazos)", sinLazos);
        verificar("Simple: sin duplicados (cada pareja una sola vez)", red.sinDuplicados());
        verificar("No dirigido: matriz simetrica (amistad mutua)", simetrica);
        System.out.println("No ponderado: las aristas solo guardan al amigo, sin peso (solo existe o no existe).");

        titulo("3) Grados");
        int suma = red.mostrarGrados();
        int gradoMayor = red.gradoMaximo();
        verificar("Hay al menos un usuario aislado (grado 0)", !red.aislados().isEmpty());
        verificar("Hay al menos un usuario con grado >= 3", gradoMayor >= 3);
        verificar("Suma de grados = 2 x numero de aristas", suma == 2 * red.getNumAmistades());

        titulo("4) Ciclo y conexion");
        verificar("Existe al menos un ciclo de 3 o mas vertices", red.mostrarCicloYComponentes() != null);

        titulo("5) existeRelacion, vecinos y grado");
        for (String[] par : new String[][]{{"U1", "U2"}, {"U4", "U5"}, {"U2", "U4"}, {"U5", "U6"}, {"U2", "U1"}}) {
            System.out.println("existeRelacion(" + par[0] + ", " + par[1] + ") = "
                    + (red.existeRelacion(par[0], par[1]) ? "verdadero" : "falso"));
        }
        System.out.println("vecinos(U1) = " + red.vecinos("U1") + " ; grado(U1) = " + red.grado("U1"));
        System.out.println("vecinos(U6) = " + red.vecinos("U6") + " ; grado(U6) = " + red.grado("U6") + "  (aislado, pero existe)");
        try {
            red.vecinos("U9");
            verificar("vecinos(U9) debe dar error de validacion", false);
        } catch (IllegalArgumentException e) {
            System.out.println("vecinos(U9) -> ERROR de validacion: " + e.getMessage());
            verificar("Un ID inexistente da error y no se confunde con un aislado", true);
        }
        verificar("existeRelacion(U1, U2) es verdadero", red.existeRelacion("U1", "U2"));
        verificar("existeRelacion(U2, U4) es falso", !red.existeRelacion("U2", "U4"));
        verificar("Reciprocidad: existeRelacion(U2, U1) es verdadero", red.existeRelacion("U2", "U1"));

        titulo("6) Operaciones que deben rechazarse");
        for (String[] par : new String[][]{{"U4", "U4"}, {"U3", "U2"}, {"U1", "U7"}}) {
            String r = red.proponerAmistad(par[0], par[1]);
            System.out.println("proponerAmistad(" + par[0] + ", " + par[1] + ") -> " + r);
            verificar("Se rechaza proponerAmistad(" + par[0] + ", " + par[1] + ")", r.startsWith("RECHAZADA"));
        }
        String r1 = red.registrarPersona("U1", "Otra persona");
        String r2 = red.registrarPersona("U7", "   ");
        System.out.println("registrarPersona(U1, Otra persona) -> " + r1);
        System.out.println("registrarPersona(U7, \"   \") -> " + r2);
        verificar("Se rechaza un ID duplicado", r1.startsWith("RECHAZADA (R6)"));
        verificar("Se rechaza un nombre vacio", r2.startsWith("RECHAZADA (R7)"));
        System.out.println("Despues de los rechazos: |V| = " + red.getNumPersonas() + ", |E| = " + red.getNumAmistades());
        verificar("El grafo no cambio (6 vertices y 5 aristas)", red.getNumPersonas() == 6 && red.getNumAmistades() == 5);

        titulo("RESULTADO DE LAS VERIFICACIONES");
        System.out.println(aprobadas + " aprobadas, " + fallidas + " fallidas.");
        return fallidas;
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            aprobadas++;
        } else {
            fallidas++;
        }
        System.out.println("  [" + (condicion ? "OK" : "FALLA") + "] " + descripcion);
    }
}
