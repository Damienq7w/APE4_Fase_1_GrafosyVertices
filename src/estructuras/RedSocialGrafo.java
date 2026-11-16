package estructuras;

import java.util.ArrayList;
import modelo.NodoAmistad;
import modelo.Persona;

/**
 * Red social "Los Enlazados" como grafo simple, no dirigido y no ponderado,
 * implementado con una lista de adyacencia de nodos propios.
 * Operaciones y reglas segun el contrato del Grupo 4: registrarPersona, proponerAmistad,
 * existeRelacion, vecinos y grado; reglas R1 (extremo inexistente), R2 (lazo),
 * R3 (reciprocidad), R4 (duplicado), R6 (ID duplicado) y R7 (nombre vacio).
 *
 * Autor: Cunalata Mendoza Damian Alexander
 */
public class RedSocialGrafo {

    // grafo
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
            ultima.setSiguiente(nueva);
        }
        ultima = nueva;
        numPersonas++;
        return "ACEPTADA: " + nueva.getId() + " " + nueva.getNombre();
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
            return "RECHAZADA (R4): la amistad entre " + a.getNombre() + " y " + b.getNombre() + " ya existe.";
        }
        agregarAlFinal(a, b);   // R3: la amistad se guarda en ambos sentidos
        agregarAlFinal(b, a);
        numAmistades++;
        aristas.add("e" + numAmistades + " = {" + a.getNombre() + ", " + b.getNombre() + "}");
        return "ACEPTADA: " + a.getNombre() + " - " + b.getNombre();
    }

    /** RF03. Verdadero si hay una arista directa entre A y B. Error si un ID no existe. */
    public boolean existeRelacion(String idA, String idB) {
        return sonAmigos(validar(idA), validar(idB));
    }

    /** RF04. Lista de vecinos (amigos directos). Error si el ID no existe. */
    public ArrayList<String> vecinos(String id) {
        ArrayList<String> lista = new ArrayList<>();
        for (NodoAmistad n = validar(id).getPrimerAmigo(); n != null; n = n.getSiguiente()) {
            lista.add(n.getAmigo().getNombre());
        }
        return lista;
    }

    /** RF05. Grado = cantidad de vecinos. Error si el ID no existe. */
    public int grado(String id) {
        return vecinos(id).size();
    }

    // analisis del grafo
    /** Busca un ciclo con DFS. Devuelve el recorrido (por ejemplo "Lucia -> Andres -> Camila -> Lucia") o null. */
    public String buscarCiclo() {
        Persona[] padre = new Persona[numPersonas];
        boolean[] visitado = new boolean[numPersonas];
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            if (!visitado[indice(p)]) {
                String ciclo = dfsCiclo(p, null, visitado, padre);
                if (ciclo != null) {
                    return ciclo;
                }
            }
        }
        return null;
    }

    private String dfsCiclo(Persona actual, Persona desde, boolean[] visitado, Persona[] padre) {
        visitado[indice(actual)] = true;
        padre[indice(actual)] = desde;
        for (NodoAmistad n = actual.getPrimerAmigo(); n != null; n = n.getSiguiente()) {
            Persona v = n.getAmigo();
            if (!visitado[indice(v)]) {
                String ciclo = dfsCiclo(v, actual, visitado, padre);
                if (ciclo != null) {
                    return ciclo;
                }
            } else if (v != desde) {
                // arista de retroceso: se reconstruye el ciclo subiendo por los padres
                String camino = v.getNombre();
                for (Persona p = actual; p != v; p = padre[indice(p)]) {
                    camino = p.getNombre() + " -> " + camino;
                }
                return v.getNombre() + " -> " + camino;
            }
        }
        return null;
    }

    /** Componentes conexos (grupos de usuarios unidos por algun camino), con DFS. */
    public ArrayList<String> componentes() {
        ArrayList<String> grupos = new ArrayList<>();
        boolean[] visitado = new boolean[numPersonas];
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            if (!visitado[indice(p)]) {
                ArrayList<String> grupo = new ArrayList<>();
                marcar(p, visitado, grupo);
                grupos.add("{" + String.join(", ", grupo) + "}");
            }
        }
        return grupos;
    }

    private void marcar(Persona p, boolean[] visitado, ArrayList<String> grupo) {
        visitado[indice(p)] = true;
        grupo.add(p.getNombre());
        for (NodoAmistad n = p.getPrimerAmigo(); n != null; n = n.getSiguiente()) {
            if (!visitado[indice(n.getAmigo())]) {
                marcar(n.getAmigo(), visitado, grupo);
            }
        }
    }

    /** Matriz de adyacencia n x n (comprobacion propuesta por el Grupo 4). */
    public int[][] matrizAdyacencia() {
        int[][] m = new int[numPersonas][numPersonas];
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            for (NodoAmistad n = p.getPrimerAmigo(); n != null; n = n.getSiguiente()) {
                m[indice(p)][indice(n.getAmigo())] = 1;
            }
        }
        return m;
    }

    /** Densidad = aristas / parejas posibles = E / (V(V-1)/2). */
    public double densidad() {
        return numPersonas < 2 ? 0 : numAmistades / (numPersonas * (numPersonas - 1) / 2.0);
    }

    /** Usuarios con grado 0. */
    public ArrayList<String> aislados() {
        ArrayList<String> lista = new ArrayList<>();
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            if (p.getPrimerAmigo() == null) {
                lista.add(p.getNombre());
            }
        }
        return lista;
    }

    // salida en consola
    public void mostrarVE() {
        StringBuilder v = new StringBuilder();
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            v.append(v.length() == 0 ? "" : ", ").append(p.getId()).append(" ").append(p.getNombre());
        }
        System.out.println("V = {" + v + "}");
        System.out.println("E = {" + String.join(", ", aristas) + "}");
        System.out.println("|V| = " + numPersonas + " usuarios (vertices), |E| = " + numAmistades + " amistades (aristas)");
    }

    public void mostrarListas() {
        System.out.println("Listas de adyacencia:");
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            ArrayList<String> amigos = vecinos(p.getId());
            System.out.println("  " + p.getId() + " " + p.getNombre() + " -> "
                    + (amigos.isEmpty() ? "(sin amigos)" : String.join(" -> ", amigos)));
        }
    }

    public void mostrarMatriz() {
        int[][] m = matrizAdyacencia();
        System.out.println("Matriz de adyacencia (1 = son amigos):");
        StringBuilder cabecera = new StringBuilder("      ");
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            cabecera.append(String.format("%-3s", p.getId()));
        }
        System.out.println(cabecera);
        int fila = 0;
        for (Persona p = primera; p != null; p = p.getSiguiente(), fila++) {
            StringBuilder linea = new StringBuilder(String.format("  %-4s", p.getId()));
            for (int col = 0; col < m.length; col++) {
                linea.append(String.format("%-3d", m[fila][col]));
            }
            System.out.println(linea);
        }
        System.out.printf("Densidad = %d / %d parejas posibles = %.1f %%%n",
                numAmistades, numPersonas * (numPersonas - 1) / 2, densidad() * 100);
    }

    /** Imprime vecinos y grado de cada usuario. Devuelve la suma de grados. */
    public int mostrarGrados() {
        int suma = 0;
        int gradoMayor = -1;
        String mayor = "-";
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            int g = grado(p.getId());
            System.out.println("grado(" + p.getId() + " " + p.getNombre() + ") = " + g + "   vecinos = " + vecinos(p.getId()));
            suma += g;
            if (g > gradoMayor) {
                gradoMayor = g;
                mayor = p.getNombre();
            }
        }
        ArrayList<String> sinAmigos = aislados();
        System.out.println("Usuario aislado: " + (sinAmigos.isEmpty() ? "ninguno" : String.join(", ", sinAmigos))
                + " | Mayor grado: " + mayor + " (" + gradoMayor + ")");
        System.out.println("Suma de grados = " + suma + " ; 2 x |E| = " + (2 * numAmistades));
        return suma;
    }

    /** Imprime el ciclo encontrado y los componentes conexos. Devuelve el ciclo o null. */
    public String mostrarCicloYComponentes() {
        String ciclo = buscarCiclo();
        System.out.println("Ciclo encontrado (DFS): " + (ciclo == null ? "ninguno" : ciclo));
        ArrayList<String> grupos = componentes();
        System.out.println("Componentes conexos: " + grupos);
        System.out.println(grupos.size() <= 1 ? "Todos los usuarios estan conectados."
                : "No todos estan conectados: hay " + grupos.size() + " componentes.");
        return ciclo;
    }

    // auxiliares
    private Persona buscar(String id) {
        if (id == null) {
            return null;
        }
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    /** Un ID inexistente es un error de validacion, distinto de una persona aislada (grado 0). */
    private Persona validar(String id) {
        Persona p = buscar(id);
        if (p == null) {
            throw new IllegalArgumentException("el ID " + id + " no existe en la red");
        }
        return p;
    }

    private boolean sonAmigos(Persona a, Persona b) {
        for (NodoAmistad n = a.getPrimerAmigo(); n != null; n = n.getSiguiente()) {
            if (n.getAmigo() == b) {
                return true;
            }
        }
        return false;
    }

    private void agregarAlFinal(Persona origen, Persona amigo) {
        NodoAmistad nuevo = new NodoAmistad(amigo);
        if (origen.getPrimerAmigo() == null) {
            origen.setPrimerAmigo(nuevo);
            return;
        }
        NodoAmistad n = origen.getPrimerAmigo();
        while (n.getSiguiente() != null) {
            n = n.getSiguiente();
        }
        n.setSiguiente(nuevo);
    }

    /** Posicion 0..n-1 de la persona en la lista de vertices. */
    private int indice(Persona buscada) {
        int i = 0;
        for (Persona p = primera; p != buscada; p = p.getSiguiente()) {
            i++;
        }
        return i;
    }

    // consultas usadas por la aplicacion
    public int getNumPersonas() {
        return numPersonas;
    }

    public int getNumAmistades() {
        return numAmistades;
    }

    /** Mayor grado de la red (0 si esta vacia). */
    public int gradoMaximo() {
        int mayor = 0;
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            mayor = Math.max(mayor, grado(p.getId()));
        }
        return mayor;
    }

    /** Revisa que ningun amigo aparezca dos veces en la lista de adyacencia de una persona. */
    public boolean sinDuplicados() {
        for (Persona p = primera; p != null; p = p.getSiguiente()) {
            for (NodoAmistad a = p.getPrimerAmigo(); a != null; a = a.getSiguiente()) {
                for (NodoAmistad b = a.getSiguiente(); b != null; b = b.getSiguiente()) {
                    if (a.getAmigo() == b.getAmigo()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
