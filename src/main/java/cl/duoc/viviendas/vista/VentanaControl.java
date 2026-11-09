package cl.duoc.viviendas.vista;

import cl.duoc.viviendas.controller.ViviendaController;
import cl.duoc.viviendas.dao.CasasDaoJson;
import cl.duoc.viviendas.dao.DepartamentosDaoJson;
import cl.duoc.viviendas.model.Vivienda;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class VentanaControl {
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtSuperficie;
    @FXML
    private TextField txtHabitaciones;
    @FXML
    private TextField txtPiso;
    @FXML
    private TableColumn<Vivienda, String> colCodigo;
    @FXML
    private TableColumn<Vivienda, String> colTipo;
    @FXML
    private TableColumn<Vivienda, Double> colSuperficie;
    @FXML
    private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML
    private TableColumn<Vivienda, String> colCostoArriendo;
    @FXML
    private CheckBox chkGastoComun;
    @FXML
    private CheckBox chkEstacionamiento;
    @FXML
    private CheckBox chkPatio;
    @FXML
    private TableView<Vivienda> tabla;
    @FXML
    private Label lblMensaje;

    private ViviendaController logic;

    @FXML
    private void initialize() {
        logic = new ViviendaController(new DepartamentosDaoJson(),  new CasasDaoJson());

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colCostoArriendo.setCellValueFactory(c -> {
            double costo = c.getValue().calcularCostoArriendo();
            String tx = "$" + String.format("%,.0f", costo).replace(",", ".");
            return new SimpleStringProperty(tx);
        });
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        cbTipo.getItems().addAll("Departamento", "Casa");
        txtPiso.disableProperty().bind(cbTipo.valueProperty().isNotEqualTo("Departamento"));
        chkPatio.disableProperty().bind(cbTipo.valueProperty().isNotEqualTo("Casa"));
        chkEstacionamiento.disableProperty().bind(cbTipo.valueProperty().isNotEqualTo("Departamento"));
        chkGastoComun.disableProperty().bind(cbTipo.valueProperty().isNotEqualTo("Departamento"));

        refrescarTabla();
    }

    @FXML
    private void onRegistrar() {
        try {
            String tipo = cbTipo.getValue();
            String codigo = txtCodigo.getText();
            String superficie = txtSuperficie.getText();
            String habitaciones = txtHabitaciones.getText();
            if (tipo == null) {
                lblMensaje.setText("Debe seleccionar un tipo de vivienda.");
                return;
            }
            String mensaje;
            if (tipo.equals("Departamento")) {
                String piso = txtPiso.getText();
                mensaje = logic.registrarDepartamento(codigo, superficie, habitaciones, piso,
                        chkGastoComun.isSelected(), chkEstacionamiento.isSelected());
            } else if (tipo.equals("Casa")) {
                mensaje = logic.registrarCasa(codigo, superficie, habitaciones, chkPatio.isSelected());
            }
            lblMensaje.setText("Vivienda registrada exitosamente.");
            refrescarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException | IllegalStateException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        try {
            Vivienda seleccionada = tabla.getSelectionModel().getSelectedItem();
            String mensaje = logic.eliminar(seleccionada);
            lblMensaje.setText(mensaje);
            refrescarTabla();
        } catch (IllegalArgumentException | IllegalStateException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    private void refrescarTabla() {
        tabla.getItems().setAll(logic.listar());
    }

    private void limpiarFormulario() {
        txtCodigo.clear(); txtSuperficie.clear(); txtHabitaciones.clear(); txtPiso.clear();
        chkGastoComun.setSelected(false); chkEstacionamiento.setSelected(false); chkPatio.setSelected(false);
        cbTipo.setValue(null);
    }
}