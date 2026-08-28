package Entidad;

import com.google.firebase.firestore.DocumentId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pedido {

    @DocumentId
    private String id;

    // Datos del cliente y entrega
    private String nombreCliente;
    private String direccionCliente;
    private String telefonoCliente;
    private String observaciones;

    // El carrito con la mercadería
    private List<Producto> productos;

    // Datos del ticket
    private String fecha;
    private double total;
    private String estado;

    // Constructor vacío (¡Obligatorio para que Firebase no tire error!)
    public Pedido() {
    }

    // Constructor completo (sin el ID, porque Firebase lo genera solo)
    public Pedido(String nombreCliente, String direccionCliente, String telefonoCliente, String observaciones, List<Producto> productos, String fecha, double total, String estado) {
        this.nombreCliente = nombreCliente;
        this.direccionCliente = direccionCliente;
        this.telefonoCliente = telefonoCliente;
        this.observaciones = observaciones;
        this.productos = productos;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ==========================================
    // MÉTODO AUXILIAR PARA LOS REPORTES
    // ==========================================
    // Convierte el String de la fecha en un objeto Date para poder filtrarlo
    public Date getFechaDate() {
        if (fecha == null || fecha.isEmpty()) {
            return null;
        }

        // Intentamos con formato estándar (ej: dd/MM/yyyy o similar)
        String[] formatos = {"dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd"};

        for (String formato : formatos) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
                return sdf.parse(fecha);
            } catch (ParseException e) {
                // Si falla con este formato, prueba con el siguiente
            }
        }
        return null;
    }
}