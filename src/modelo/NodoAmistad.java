package modelo;

/**
 * Nodo de una lista de adyacencia: representa una amistad y apunta al vertice del amigo.
 *
 * Autor: Cunalata Mendoza Damian Alexander
 */
public class NodoAmistad {
    private final Persona amigo;
    private NodoAmistad siguiente;

    public NodoAmistad(Persona amigo) {
        this.amigo = amigo;
    }

    public Persona getAmigo() {
        return amigo;
    }

    public NodoAmistad getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoAmistad siguiente) {
        this.siguiente = siguiente;
    }
}
