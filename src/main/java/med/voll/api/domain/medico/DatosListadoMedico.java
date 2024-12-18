package med.voll.api.domain.medico;

public record DatosListadoMedico(
        Long id,
        String nombre,
        Especialidad especialidad,
        String documento,
        String email
) {
    public DatosListadoMedico(Medico medico){
        this(medico.getId(), medico.getNombre(), Especialidad.fromValue(medico.getEspecialidad().toValue().toString()), medico.getDocumento(), medico.getEmail());
    }
}
