package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import java.util.ArrayList;
import java.util.List;

public class ViviendaController {
    private final ViviendaDAO departamentoDao;
    private final ViviendaDAO casaDao;

    public ViviendaController(ViviendaDAO departamentoDao, ViviendaDAO casaDao) {
        this.departamentoDao = departamentoDao;
        this.casaDao = casaDao;
    }

    public List<Vivienda> listar() {
        ArrayList<Vivienda> lista = new ArrayList<>();
        lista.addAll(departamentoDao.listar());
        lista.addAll(casaDao.listar());
        return lista;
    }
    public String registrarDepartamento(String codigo, String superficie, String habitaciones, String piso, boolean gastoAlDia, boolean estacionamiento) {
        String cod = leerCodigo(codigo);
        double sup = leerSuperficie(superficie);
        int hab = leerHabitaciones(habitaciones);
        int ps = leerPiso(String.valueOf(piso));
        verificarCodigoNuevo(cod);
        Departamento departamento = new Departamento(cod, sup, hab, ps, gastoAlDia);
        if (estacionamiento) {
            departamento.asignarEstacionamiento();
        }
        departamentoDao.guardar(departamento);
        return cod + " (Departamento) registado correctamente";
    }

    public String registrarCasa(String codigo, String superficie, String habitaciones,
                               boolean patio) {
        String cod = leerCodigo(codigo);
        double sup = leerSuperficie(superficie);
        int hab = leerHabitaciones(habitaciones);
        verificarCodigoNuevo(cod);
        Casa casa = new Casa(cod, sup, hab, patio);
        casaDao.guardar(casa);
        return cod + " (Casa) registado correctamente";
    }

    public String eliminar(Vivienda seleccionada) {
        if (seleccionada == null) {
            throw new IllegalArgumentException("Debe seleccionar una vivienda para eliminar.");
        }
        if (seleccionada instanceof Departamento) {
            departamentoDao.eliminar(seleccionada.getCodigo());
        } else if (seleccionada instanceof Casa) {
            casaDao.eliminar(seleccionada.getCodigo());
        }
        return seleccionada.getCodigo()+" eliminada correctamente.";
    }

    private String leerCodigo(String codigo) {
        if  (codigo == null || codigo.trim().isEmpty()) {
            throw new  IllegalArgumentException("CÓDIGO: No debe estar vacío.");
        }
        return codigo.trim();
    }

    private double leerSuperficie(String superficie) {
        double valor;
        try {
            valor = Double.parseDouble(superficie.trim().replace(",","."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("SUPERFICIE: Debe ser un número.");
        }
        if (valor < 20 || valor > 500) {
            throw new IllegalArgumentException("SUPERFICIE: Debe ser entre 20 y 500 m2.");
        }
        return valor;
    }

    private int leerHabitaciones(String habitaciones) {
        int valor;
        try {
            valor = Integer.parseInt(habitaciones.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("HABITACIONES: Debe ser un número entero.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("HABITACIONES: Deben ser mayor a 0.");
        }
        return valor;
    }
    private int leerPiso(String piso) {
        int valor;
        try {
            valor = Integer.parseInt(piso.trim());
        } catch (NumberFormatException e) {
            throw  new IllegalArgumentException("PISO: Debe ser un número entero.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("PISO: Debe ser mayor a 0.");
        }
        return valor;
    }

    private void verificarCodigoNuevo(String cod) {
        for (Vivienda v : listar()) {
            if  (v.getCodigo().equalsIgnoreCase(cod)) {
                throw new IllegalArgumentException("Codigo: "+cod+" , ya se encuentra registrado.");
            }
        }
    }
}
