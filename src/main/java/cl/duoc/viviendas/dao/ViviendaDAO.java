package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;
import java.util.List;

public interface ViviendaDAO {
    List<Vivienda> listar();
    void guardar(Vivienda vivienda);
    void eliminar(String codigo);
}
