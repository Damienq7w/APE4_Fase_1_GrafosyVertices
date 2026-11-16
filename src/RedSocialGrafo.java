import java.util.ArrayList;

/**
 * APE 4 - Fase 1: Reto de grafos y vertices.
 * Red social "Los Enlazados": 6 usuarios (vertices) y 5 amistades (aristas).
 * Grafo simple, no dirigido y no ponderado, con lista de adyacencia de nodos propios.
 * Operaciones y reglas segun el contrato del Grupo 4: registrarPersona, proponerAmistad,
 * existeRelacion, vecinos y grado; reglas R1 (extremo inexistente), R2 (lazo),
 * R3 (reciprocidad), R4 (duplicado), R6 (ID duplicado) y R7 (nombre vacio).
 *
 * Compilar:  javac -d bin src/RedSocialGrafo.java
 * Ejecutar:  java -cp bin RedSocialGrafo
 *
 * Autor: Cunalata Mendoza Damian Alexander
 */
public class RedSocialGrafo {

    // ===================================================================== nodos propios
    /** Vertice: una persona de la red, con su propia lista de amigos. */
    static class Persona {
        final String id;
        final String nombre;
        NodoAmistad primerAmigo;   // inicio de su lista de adyacencia
        Persona siguiente;         // siguiente persona en la lista de vertices

        Persona(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }

    /** Elemento de una lista de adyacencia: apunta al amigo (otro vertice). */
    static class NodoAmistad {
        final Persona amigo;
        NodoAmistad siguiente;

        NodoAmistad(Persona amigo) {
            this.amigo = amigo;
        }
    }

    // ===================================================================== grafo
    private Persona primera;
    private Persona ultima;
    private int numPersonas;
    private int numAmistades;              // cada amistad A-B se cuenta una sola vez
    private final ArrayList<String> aristas = new ArrayList<>();  // "e1 = {Lucia, Andres}", para mostrar E

    /** Crea la red del reto: 6 usuarios y 5 amistades. */
    public static RedSocialGrafo losEnlazados() {
        RedSocialGrafo red = new RedSocialGrafo();
        String[][] personas = {{"U1", "Lucia"}, {"U2", "Andres"}, {"U3", "Camila"},
                               {"U4", "Javier"}, {"U5", "Renata"}, {"U6", "Fabian"}};
        for (String[] p : personas) {
            red.registrarPersona(p[0], p[1]);
        }
        String[][] amistades = {{"U1", "U2"}, {"U2", "U3"}, {"U1", "U3"}, {"U1", "U4"}, {"U4", "U5"}};
        for (String[] a : amistades) {
            red.proponerAmistad(a[0], a[1]);
        }
        return red;
    }

    /** RF01. Registra una persona. Devuelve el resultado como texto. */
    public String registrarPersona(String id, String nombre) {
        if (id == null || id.trim().isEmpty()) {
            return "RECHAZADA: el ID no puede estar vacio.";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "RECHAZADA (R7): el nombre no puede estar vacio.";
        }
        if (buscar(id) != null) {
            return "RECHAZADA (R6): ya existe una persona con el ID " + id.trim() + ".";
        }
        Persona nueva = new Persona(id.trim().toUpperCase(), nombre.trim());
        if (primera == null) {
            primera = nueva;
        } else {
            ultima.siguiente = nueva;
        }
        ultima = nueva;
        numPersonas++;
        return "ACEPTADA: " + nueva.id + " " + nueva.nombre;
    }

    /** RF02. Propone una amistad entre dos personas. Devuelve el resultado como texto. */
    public String proponerAmistad(String idA, String idB) {
        Persona a = buscar(idA);
        Persona b = buscar(idB);
        if (a == null || b == null) {
            return "RECHAZADA (R1): el usuario " + (a == null ? idA : idB) + " no existe; registrelo primero.";
        }
        if (a == b) {
            return "RECHAZADA (R2): un usuario no puede tener una amistad consigo mismo.";
        }
        if (sonAmigos(a, b)) {
            return "RECHAZADA (R4): la amistad entre " + a.nombre + " y " + b.nombre + " ya existe.";
        }
        agregarAlFinal(a, b);   // R3: la amistad se guarda en ambos sentidos
        agregarAlFinal(b, a);
        numAmistades++;
        aristas.add("e" + numAmistades + " = {" + a.nombre + ", " + b.nombre + "}");
        return "ACEPTADA: " + a.nombre + " - " + b.nombre;
    }

    // ===================================================================== salida en consola
    public void mostrarVE() {
        StringBuilder v = new StringBuilder();
        for (Persona p = primera; p != null; p = p.siguiente) {
            v.append(v.length() == 0 ? "" : ", ").append(p.id).append(" ").append(p.nombre);
        }
        System.out.println("V = {" + v + "}");
        System.out.println("E = {" + String.join(", ", aristas) + "}");
        System.out.println("|V| = " + numPersonas + " usuarios (vertices), |E| = " + numAmistades + " amistades (aristas)");
    }

    // ===================================================================== auxiliares
    private Persona buscar(String id) {
        if (id == null) {
            return null;
        }
        for (Persona p = primera; p != null; p = p.siguiente) {
            if (p.id.equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    private boolean sonAmigos(Persona a, Persona b) {
        for (NodoAmistad n = a.primerAmigo; n != null; n = n.siguiente) {
            if (n.amigo == b) {
                return true;
            }
        }
        return false;
    }

    private void agregarAlFinal(Persona origen, Persona amigo) {
        NodoAmistad nuevo = new NodoAmistad(amigo);
        if (origen.primerAmigo == null) {
            origen.primerAmigo = nuevo;
            return;
        }
        NodoAmistad n = origen.primerAmigo;
        while (n.siguiente != null) {
            n = n.siguiente;
        }
        n.siguiente = nuevo;
    }

    // ===================================================================== prueba del registro y las reglas
    public static void main(String[] args) {
        RedSocialGrafo red = losEnlazados();
        red.mostrarVE();
        System.out.println(red.proponerAmistad("U4", "U4"));
        System.out.println(red.proponerAmistad("U3", "U2"));
        System.out.println(red.proponerAmistad("U1", "U7"));
        System.out.println(red.registrarPersona("U1", "Otra persona"));
        System.out.println(red.registrarPersona("U7", "   "));
    }
}
