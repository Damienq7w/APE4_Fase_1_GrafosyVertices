package modelo;

/**
 * Vertice: una persona de la red social, con su propia lista de amigos (lista de adyacencia).
 *
 * Autor: Cunalata Mendoza Damian Alexander
 */
public class Persona {
    private final String id;
    private final String nombre;
    private NodoAmistad primerAmigo;   // inicio de su lista de adyacencia
    private Persona siguiente;         // siguiente persona en la lista de vertices

    public Persona(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoAmistad getPrimerAmigo() {
        return primerAmigo;
    }

    public void setPrimerAmigo(NodoAmistad primerAmigo) {
        this.primerAmigo = primerAmigo;
    }

    public Persona getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Persona siguiente) {
        this.siguiente = siguiente;
    }
}
