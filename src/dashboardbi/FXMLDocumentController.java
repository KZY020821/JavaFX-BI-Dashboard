/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package dashboardbi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javaPackages.Data2Dashboard;
import javaPackages.ReadLocalJson;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author khorzeyi
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private StackedBarChart<String,Integer> br;
    
    @FXML
    private CheckBox americaCheckBox;

    @FXML
    private CheckBox asiaCheckBox;

    @FXML
    private CheckBox europeCheckBox;
    
    @FXML 
    private CheckBox eliseCheckBox;
    
    @FXML 
    private CheckBox evoraCheckBox;
    
    @FXML 
    private CheckBox exigeCheckBox;
    
    @FXML 
    private Button resetButton;
    
    @FXML
    private ComboBox<String> startQuarterComboBox;

    @FXML
    private ComboBox<String> endQuarterComboBox;
    
    @FXML
    private TableView<Data2Dashboard> tableData;
    
    @FXML
    private TableColumn<Data2Dashboard, String> timeColumn;
    
    @FXML
    private TableColumn<Data2Dashboard, Integer> eliseColumn;
    
    @FXML
    private TableColumn<Data2Dashboard, Integer> evoraColumn;
    
    @FXML
    private TableColumn<Data2Dashboard, Integer> exigeColumn;
    
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    
    
    private ArrayList<ReadLocalJson.DataRecord> originalData;
    private final ObservableList<Data2Dashboard> data = FXCollections.observableArrayList();
    private final List<String> selectedRegions = new ArrayList<>();
    private final List<String> selectedVehicles = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ReadLocalJson readJson = new ReadLocalJson();

        try {
            originalData = readJson.readJson();
        } catch (IOException | JSONException | ParseException e) {
        }

        resetButton.setOnAction(event -> {
            br.getData().clear();
            loadOriginalData();
        });
        

        initializeChart();
        initializeTable();

        americaCheckBox.setOnAction(event -> handleRegionCheckBoxAction("America"));
        asiaCheckBox.setOnAction(event -> handleRegionCheckBoxAction("Asia"));
        europeCheckBox.setOnAction(event -> handleRegionCheckBoxAction("Europe"));

        eliseCheckBox.setOnAction(event -> handleVehicleCheckBoxAction("Elise"));
        evoraCheckBox.setOnAction(event -> handleVehicleCheckBoxAction("Evora"));
        exigeCheckBox.setOnAction(event -> handleVehicleCheckBoxAction("Exige"));
        
        startQuarterComboBox.setItems(FXCollections.observableArrayList(generateQuarterLabels()));
        endQuarterComboBox.setItems(FXCollections.observableArrayList(generateQuarterLabels()));

        startQuarterComboBox.getSelectionModel().selectFirst();
        endQuarterComboBox.getSelectionModel().selectLast();

        startQuarterComboBox.setOnAction(event -> filterData());
        endQuarterComboBox.setOnAction(event -> filterData());

        resetButton.setOnAction(event -> resetData());
    }

    
    private void handleRegionCheckBoxAction(String region) {
        if (selectedRegions.contains(region)) {
            selectedRegions.remove(region);
        } else {
            selectedRegions.add(region);
        }
        filterData();
    }

    private void handleVehicleCheckBoxAction(String vehicle) {
        if (selectedVehicles.contains(vehicle)) {
            selectedVehicles.remove(vehicle);
        } else {
            selectedVehicles.add(vehicle);
        }
        filterData();
    }

    private void resetData() {
        selectedRegions.clear();
        selectedVehicles.clear();
        loadOriginalData();
    }

    private void filterData() {
        br.getData().clear();
        br.setAnimated(false);
        data.clear();
        
        Map<String, Map<String, Integer>> quarterData = new HashMap<>();
        
        String startQuarter = startQuarterComboBox.getSelectionModel().getSelectedItem();
        String endQuarter = endQuarterComboBox.getSelectionModel().getSelectedItem();

        if (startQuarter == null || endQuarter == null) {
            startQuarter = generateQuarterLabels().get(0);
            endQuarter = generateQuarterLabels().get(generateQuarterLabels().size() - 1);
        }

        if (startQuarter.compareTo(endQuarter) > 0) {
            String temp = startQuarter;
            startQuarter = endQuarter;
            endQuarter = temp;
        }

        final String finalStartQuarter = startQuarter;
        final String finalEndQuarter = endQuarter;

        List<String> filteredQuarters = generateQuarterLabels()
                .stream()
                .filter(q -> isBetween(q, finalStartQuarter, finalEndQuarter))
                .collect(Collectors.toList());

        for (ReadLocalJson.DataRecord record : originalData) {
            String quarter = record.getYear() + "Q" + record.getQtr();
            if (selectedRegions.contains(record.getRegion()) && selectedVehicles.contains(record.getVehicle())
                && isBetween(quarter, startQuarter, endQuarter)) {
                if (!quarterData.containsKey(quarter)) {
                    quarterData.put(quarter, new HashMap<>());
                }
                quarterData.get(quarter).put(record.getVehicle(), quarterData.get(quarter).
                        getOrDefault(record.getVehicle(), 0) + record.getQuantity());
            }
        }

        for (String vehicle : getVehicleList(originalData)) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(vehicle);
            for (String quarter : filteredQuarters) { 
                series.getData().add(new XYChart.Data<>(quarter, quarterData.
                        getOrDefault(quarter, new HashMap<>()).getOrDefault(vehicle, 0)));
            }
            br.getData().add(series);
        }
        
        populateTableWithData(quarterData);
        
    }


    private ArrayList<String> getVehicleList(ArrayList<ReadLocalJson.DataRecord> data) {
        ArrayList<String> vehicleList = new ArrayList<>();
        for (ReadLocalJson.DataRecord record : data) {
            if (!vehicleList.contains(record.getVehicle())) {
                vehicleList.add(record.getVehicle());
            }
        }
        return vehicleList;
    }

    private void loadOriginalData() {
        selectedRegions.clear();
        selectedVehicles.clear();
        americaCheckBox.setSelected(true);
        asiaCheckBox.setSelected(true);
        europeCheckBox.setSelected(true);    
        eliseCheckBox.setSelected(true);
        evoraCheckBox.setSelected(true);
        exigeCheckBox.setSelected(true);
        startQuarterComboBox.getSelectionModel().selectFirst();
        endQuarterComboBox.getSelectionModel().selectLast();
        selectedRegions.addAll(Arrays.asList("America", "Asia", "Europe"));
        selectedVehicles.addAll(getVehicleList(originalData));
        filterData();
    }
    
    private void initializeChart() {
        yAxis.setLabel("Number of vehicles");
        xAxis.setLabel("Time");
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(500);
        xAxis.setAutoRanging(true);
        xAxis.setGapStartAndEnd(true);
        br.setAnimated(false);
        loadOriginalData();
    }

    
    private void initializeTable() {
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eliseColumn.setCellValueFactory(cellData -> cellData.getValue().eliseProperty().asObject());
        evoraColumn.setCellValueFactory(cellData -> cellData.getValue().evoraProperty().asObject());
        exigeColumn.setCellValueFactory(cellData -> cellData.getValue().exigeProperty().asObject());

        TableColumn<Data2Dashboard, Integer> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().
                getTotal()).asObject());

        tableData.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableData.getColumns().add(totalColumn);
    }


    private ArrayList<String> generateQuarterLabels() {
        ArrayList<String> quarterLabels = new ArrayList<>();
        for (int year = 2016; year <= 2018; year++) {
            for (int quarter = 1; quarter <= 4; quarter++) {
                quarterLabels.add(year + "Q" + quarter);
            }
        }
        return quarterLabels;
    }
    
    private boolean isBetween(String quarter, String startQuarter, String endQuarter) {
        return quarter.compareTo(startQuarter) >= 0 && quarter.compareTo(endQuarter) <= 0;
    }
    
    private void populateTableWithData(Map<String, Map<String, Integer>> quarterData) {
        data.clear();

        for (Map.Entry<String, Map<String, Integer>> entry : quarterData.entrySet()) {
            String quarter = entry.getKey();
            Map<String, Integer> vehicleData = entry.getValue();
            int elise = vehicleData.getOrDefault("Elise", 0);
            int evora = vehicleData.getOrDefault("Evora", 0);
            int exige = vehicleData.getOrDefault("Exige", 0);
            data.add(new Data2Dashboard(quarter, elise, evora, exige));
        }
        data.sort(Comparator.comparing(Data2Dashboard::getTime));
        tableData.setItems(data);
        addTotalRow();
        tableData.setItems(data);
    }

    private void addTotalRow() {
        int totalElise = data.stream().mapToInt(Data2Dashboard::getElise).sum();
        int totalEvora = data.stream().mapToInt(Data2Dashboard::getEvora).sum();
        int totalExige = data.stream().mapToInt(Data2Dashboard::getExige).sum();
        data.add(new Data2Dashboard("Total", totalElise, totalEvora, totalExige));
    }   
}


