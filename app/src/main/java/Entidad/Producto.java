package Entidad;

import com.google.firebase.firestore.DocumentId;
import java.io.Serializable;
public class Producto implements Serializable {

    @DocumentId
    private String id;
    private String nombre;
    private double precio_venta;
    private String categoria;
    private String tipo_venta;

    // 3. ¡NUEVO! El campito para guardar cuánto eligió llevar
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

    public double getPrecio_venta() {
        return precio_venta;
    }

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

    // Getters y Setters para la nueva cantidad
    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}