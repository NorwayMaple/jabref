package org.jabref.gui.exporter;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.jabref.gui.DialogService;
import org.jabref.gui.util.BaseDialog;
import org.jabref.gui.util.ControlHelper;
import org.jabref.logic.journals.JournalAbbreviationLoader;
import org.jabref.logic.l10n.Localization;
import org.jabref.preferences.PreferencesService;

import com.airhacks.afterburner.views.ViewLoader;
import org.fxmisc.easybind.EasyBind;

public class ExportCustomizationDialogView extends BaseDialog<Void> {

    @FXML private Button addButton;
    @FXML private Button modifyButton;
    @FXML private Button removeButton;
    @FXML private ButtonType closeButton;
    @FXML private TableView<ExporterViewModel> exporterTable;
    @FXML private TableColumn<ExporterViewModel, String> nameColumn;
    @FXML private TableColumn<ExporterViewModel, String> layoutColumn;
    @FXML private TableColumn<ExporterViewModel, String> extensionColumn;

    @Inject private DialogService dialogService;
    @Inject private PreferencesService preferences;
    @Inject private JournalAbbreviationLoader loader; // Should this be injected?
    private ExportCustomizationDialogViewModel viewModel;

    public ExportCustomizationDialogView() {
        this.setTitle(Localization.lang("Customize Export Formats"));

        ViewLoader.view(this)
                  .load()
                  .setAsDialogPane(this);

        ControlHelper.setAction(closeButton, getDialogPane(), event -> saveAndClose());
    }

    @FXML
    private void addExporter() {
        viewModel.addExporter();
    }

    @FXML
    private void modifyExporter() {
        viewModel.modifyExporter();
    }

    @FXML
    private void removeExporter() {
        viewModel.removeExporters();
    }

    @FXML
    private void initialize() {
        viewModel = new ExportCustomizationDialogViewModel(preferences, dialogService, loader);
        exporterTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        exporterTable.itemsProperty().bind(viewModel.exportersProperty());
        // Unidirectional list binding - this is okay because item selection only fires from the View, not the other way around
        EasyBind.listBind(viewModel.selectedExportersProperty(), exporterTable.getSelectionModel().getSelectedItems());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        layoutColumn.setCellValueFactory(cellData -> cellData.getValue().getLayoutFileName());
        extensionColumn.setCellValueFactory(cellData -> cellData.getValue().getExtension());
    }

    private void closeDialog() {
        close();
    }

    private void saveAndClose() {
        viewModel.saveToPrefs();
        closeDialog();
    }
}