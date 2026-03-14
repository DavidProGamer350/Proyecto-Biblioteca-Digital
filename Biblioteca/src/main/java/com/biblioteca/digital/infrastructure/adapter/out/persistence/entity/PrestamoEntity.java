package com.biblioteca.digital.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "prestamos")
public class PrestamoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion_esperada")
    private LocalDate fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDate fechaDevolucionReal;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "codigo_prestamo", unique = true, length = 50)
    private String codigoPrestamo;

    @Column(name = "multas_acumuladas")
    private Integer multasAcumuladas;

    @Column(name = "notificacion_email", length = 200)
    private String notificacionEmail;

    @Column(name = "requiere_aprobacion")
    private Boolean requiereAprobacion;

    @Column(name = "motivo_rechazo", length = 300)
    private String motivoRechazo;

    // Constructor vacío
    public PrestamoEntity() {}

    // GETTERS/SETTERS (igual que BookEntity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLibroId() { return libroId; }
    public void setLibroId(Long libroId) { this.libroId = libroId; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }

    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getCodigoPrestamo() { return codigoPrestamo; }
    public void setCodigoPrestamo(String codigoPrestamo) { this.codigoPrestamo = codigoPrestamo; }

    public Integer getMultasAcumuladas() { return multasAcumuladas; }
    public void setMultasAcumuladas(Integer multasAcumuladas) { this.multasAcumuladas = multasAcumuladas; }

    public String getNotificacionEmail() { return notificacionEmail; }
    public void setNotificacionEmail(String notificacionEmail) { this.notificacionEmail = notificacionEmail; }

    public Boolean getRequiereAprobacion() { return requiereAprobacion; }
    public void setRequiereAprobacion(Boolean requiereAprobacion) { this.requiereAprobacion = requiereAprobacion; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
