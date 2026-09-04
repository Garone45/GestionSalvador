package Entidad;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class Producto implements Serializable {

    @DocumentId
    private String id;
    private String nombre;

    @PropertyName("precio_venta")
    private double precio_venta;

    private String categoria;
    private String tipo_venta;

    private double cantidad = 0.0;

    public Producto() {
    }

    // Constructor con los datos
    public Producto(String id, String nombre, double precio_venta, String categoria, String tipo_venta) {
        this.id = id;
        this.nombre = nombre;
        this.precio_venta = precio_venta;
        this.categoria = categoria;
        this.tipo_venta = tipo_venta;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @PropertyName("precio_venta")
    public double getPrecio_venta() {
        return precio_venta;
    }

    @PropertyName("precio_venta")
    public void setPrecio_venta(double precio_venta) {
        this.precio_venta = precio_venta;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipo_venta() {
        return tipo_venta;
    }

    public void setTipo_venta(String tipo_venta) {
        this.tipo_venta = tipo_venta;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
    // Devuelve el salto de cantidad según cómo se vende
    public double getPasoCantidad() {
        if ("KG".equalsIgnoreCase(tipo_venta)) {
            return 0.5; // Kilos suben/bajan de a medio kilo
        }
        return 1.0; // Paquete y Unidad suben/bajan de a 1 entero
    }

    // Devuelve el texto formateado para mostrar en pantalla o tickets
    public String getTextoCantidad() {
        if ("KG".equalsIgnoreCase(tipo_venta)) {
            // Ej: "1.5 kg" o "0.5 kg"
            return String.format(java.util.Locale.US, "%.1f kg", cantidad);
        } else if ("PAQUETE".equalsIgnoreCase(tipo_venta)) {
            // Ej: "2 paq."
            return String.format(java.util.Locale.US, "%.0f paq.", cantidad);
        } else {
            // Ej: "3 un."
            return String.format(java.util.Locale.US, "%.0f un.", cantidad);
        }
    }

    // Devuelve si permite o no decimales
    public boolean permiteDecimales() {
        return "KG".equalsIgnoreCase(tipo_venta);
    }

}