package med.voll.api.domain.medico;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.direccion.Direccion;

@Table(name = "medicos")
@Entity(name = "Medico")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String nombre;

    private final String telefono;

    @Column(unique = true)
    private final String email;

    @Column(unique = true)
    private final String documento;

    @Convert(converter = EspecialidadConverter.class)
    private final Especialidad especialidad;

    @Embedded
    private final Direccion direccion;

    private final boolean activo;


    
    public Medico(Long id) {
        this.id = id;
        this.nombre = null;
        this.telefono = null;
        this.email = null;
        this.documento = null;
        this.especialidad = null;
        this.direccion = null;
        this.activo = true; // O el valor por defecto que prefieras
    }

    // Constructor adicional para DatosRegistroMedico
    public Medico(final DatosRegistroMedico datosRegistroMedico) {
        this(
                null, // ID autogenerado
                datosRegistroMedico.nombre(),
                datosRegistroMedico.telefono(),
                datosRegistroMedico.email(),
                datosRegistroMedico.documento(),
                Especialidad.fromValue(datosRegistroMedico.especialidad().toValue()),
                new Direccion(datosRegistroMedico.direccion()),
                true
        );
    }

    public Medico actualizarDatos(String nombre, String documento, DatosDireccion direccion) {
        return new Medico(
                this.id, // Conserva el mismo ID
                nombre != null ? nombre : this.nombre, // Mantén el valor actual si es null
                this.telefono, // Teléfono no se actualiza en este método
                this.email,    // Email no se actualiza en este método
                documento != null ? documento : this.documento, // Mantén el valor actual si es null
                this.especialidad, // Especialidad no se actualiza en este método
                direccion != null ? this.direccion.actualizarDatos(direccion) : this.direccion, // Actualiza la dirección si no es null
                this.activo
        );
    }

    // Método para desactivar al médico
    public Medico desactivarMedico() {
        return new Medico(
                this.id,
                this.nombre,
                this.telefono,
                this.email,
                this.documento,
                this.especialidad,
                this.direccion,
                false // Cambiar activo a false
        );
    }
}