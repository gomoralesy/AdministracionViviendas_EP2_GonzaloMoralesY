package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DepartamentosDaoJson implements ViviendaDAO {
    private final Path archivo = Path.of("departamentos.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Departamento> departamentos = new ArrayList<>();

    public DepartamentosDaoJson() {cargar();}

    private void cargar() {
        try {
            if (Files.exists(archivo) && Files.size(archivo) > 0) {
                try (Reader reader = Files.newBufferedReader(archivo)){
                    Type type = new TypeToken<List<Departamento>>(){}.getType();
                    List<Departamento> leidos = gson.fromJson(reader,type);
                    departamentos.clear();
                    if (leidos != null) departamentos.addAll(leidos);
                }
            }
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("No se pudo leer departamentos", e);
        }
    }

    private void persistir() {
        try (Writer writer = Files.newBufferedWriter(archivo)){
            gson.toJson(departamentos, writer);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar los departamentos", e);
        }
    }

    @Override
    public List<Vivienda> listar() {return new ArrayList<>(departamentos);}

    @Override
    public void guardar(Vivienda vivienda) {
        if  (!(vivienda instanceof Departamento departamento)) {
            throw new IllegalArgumentException(vivienda.getClass().getSimpleName() + " no es un departamento");
        }

        boolean repetido = departamentos.stream().anyMatch(d -> d.getCodigo().equals(departamento.getCodigo()));
        if (repetido) {
            throw new IllegalArgumentException(vivienda.getClass().getSimpleName() + " ya existe un departamento con ese código.");
        }

        departamentos.add(departamento);
        persistir();
    }

    @Override
    public void eliminar(String codigo) {

        boolean eliminado = departamentos.removeIf(d -> d.getCodigo().equals(codigo));
        if (eliminado) {
            persistir();
        } else {
            throw new IllegalArgumentException(codigo + " no se puede eliminar el código. No se encuentra registrado.");
        }
    }
}

