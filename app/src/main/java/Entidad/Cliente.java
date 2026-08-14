package Entidad;
public class Cliente {

    // Tus variables (siempre en private por buenas prácticas)
    private String nombre;
    private String apellido;
    private String calle;
    private String altura;
    private String localidad;
    private String telefono;

    private String numero;

    // 1. Constructor vacío (¡Súper importante! Firestore lo necesita sí o sí para leer datos)
    public Cliente() {
    }

    // 2. Constructor con todos los datos (Te va a servir para cuando quieras guardar un cliente nuevo)
    public Cliente(String nombre, String apellido, String calle, String altura, String localidad, String numero, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.calle = calle;
        this.altura = altura;
        this.localidad = localidad;
        this.numero = numero;
        this.telefono = telefono;
    }

    // 3. Getters (Para leer los datos y mostrarlos en el RecyclerView)
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCalle() { return calle; }
    public String getAltura() { return altura; }
    public String getLocalidad() { return localidad; }
    public String getTelefono() { return telefono; }
    public String getNumero() { return telefono; }

    // 4. Setters (Para que Firestore o tu app le puedan asignar los valores)
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCalle(String calle) { this.calle = calle; }
    public void setAltura(String altura) { this.altura = altura; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public void setNumero(String numero) { this.numero = numero; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
}