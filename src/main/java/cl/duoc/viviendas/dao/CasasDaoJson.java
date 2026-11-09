package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
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

public class CasasDaoJson implements ViviendaDAO {
    private final Path archivo = Path.of("casas.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Casa> casas = new ArrayList<>();

    public CasasDaoJson() {cargar();}

    private void cargar() {
        try {
            if (Files.exists(archivo) && Files.size(archivo) > 0) {
                try (Reader reader = Files.newBufferedReader(archivo)) {
                    Type type = new TypeToken<List<Casa>>() {
                    }.getType();
                    List<Casa> leidos = gson.fromJson(reader, type);
                    casas.clear();
                    if (leidos != null) {casas.addAll(leidos);}
                }
            }
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("No se pudo leer casas", e);
        }

    }

    private void persistir() {
        try (Writer writer = Files.newBufferedWriter(archivo)) {
            gson.toJson(casas, writer);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la casas", e);
        }
    }

    @Override
    public List<Vivienda> listar() {return new ArrayList<>(casas);}

    @Override
    public void guardar(Vivienda vivienda) {
        if (!(vivienda instanceof Casa casa)) {
            throw new IllegalArgumentException(vivienda.getClass().getName() + " no es un casa");
        }

        boolean repetido = casas.stream().anyMatch(c -> c.getCodigo().equals(casa.getCodigo()));
        if (repetido) {
            throw new IllegalArgumentException("Codigo: "+casa.getCodigo()+", ya existe una casa con ese código.");
        }

        casas.add(casa);
        persistir();
    }

    @Override
    public void eliminar(String codigo) {

        boolean eliminado = casas.removeIf(c -> c.getCodigo().equals(codigo));
        if (eliminado) {
            persistir();
        } else {
            throw new IllegalArgumentException(codigo + " no se puede eliminar la casa. No se encuentra registrada.");
        }
    }
}