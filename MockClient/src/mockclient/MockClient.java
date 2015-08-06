package mockclient;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Ryan Burns
 */
public class MockClient extends Application {
    private static Stage MAIN_STAGE;
    private final TableView<TableEntry> table;
    private final ObservableList<TableEntry> data;
  
    public MockClient() {
        table = new TableView();
        data = FXCollections.observableArrayList(new TableEntry[] {
            new TableEntry("07/20/2015", "07/20/2015", "FRAUD BALANCE TRANSFER",
                "$3.50", "Chicago", 439),
            new TableEntry("06/30/2015", "06/31/2015",
                    "INTEREST CREDIT ADJUSTMENT", "$33.07", "Atlanta", 600),
            new TableEntry("06/30/2015", "06/30/2015", "TRANSACTION FEE CREDIT",
                "$0.28", "New York", 404),
            new TableEntry("05/23/2015", "05/23/2015",
                "OVERLIMIT FEE CREDIT ADJUSTMENT", "$176.49",
                "Paris", 402),
            new TableEntry("12/25/2014", "12/27/2014", "PAYMENT REVERSAL",
                "$31.84", "Chicago", 223)
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        MAIN_STAGE = stage;

        Scene scene = new Scene(new Group());
        stage.setTitle("Mock Client");
        stage.setWidth(900);
        stage.setHeight(600);

        ChoiceBox choiceBox = setupChoiceBox();
        setupColumns();

        table.setItems(data);

        VBox vbox = new VBox();
        vbox.setPrefSize(800, 500);
        vbox.setLayoutX(50);
        vbox.setLayoutY(50);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.getChildren().addAll(new Node[] {choiceBox, table});

        ((Group)scene.getRoot()).getChildren().addAll(new Node[] {vbox});

        stage.setScene(scene);
        stage.show();
    }

    private void setupColumns() {
        TableColumn column1 = new TableColumn("Trans Date");
        column1.setPrefWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory("transDate"));

        TableColumn column2 = new TableColumn("Post Date");
        column2.setPrefWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory("postDate"));

        TableColumn column3 = new TableColumn("Merchant");
        column3.setPrefWidth(400);
        column3.setCellValueFactory(new PropertyValueFactory("merchant"));

        TableColumn column4 = new TableColumn("Amount");
        column4.setPrefWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory("amount"));

        TableColumn column5 = new TableColumn("City");
        column5.setPrefWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory("city"));

        table.getColumns().addAll(new TableColumn[]
                {column1, column2, column3, column4, column5});
    }

    private ChoiceBox setupChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(
            FXCollections.observableArrayList(
                    new String[] {"English", "French"}));
        choiceBox.setValue("English");
        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                for (TableEntry entry : data) {
                    entry.setMerchant(get((String) newValue, 
                            Integer.toString(entry.getTransactionCode())));
                }
                table.setItems(data);

                ObservableList<TableEntry> items = table.getItems();
                if (items == null || items.size() == 0) {
                    return;
                }
                TableEntry item = table.getItems().get(0);
                items.remove(0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        items.add(0, item);
                    }
                });
            }
        });
        return choiceBox;
    }

    private String get(String language, String tCode) {
        String description = null;
        try {
            String url = "http://localhost:8080/TransDescripAPI/"
                    + language + "/" + tCode;
      
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, null,
                            String.class, new Object[0]);
      
            HttpStatus status = response.getStatusCode();
            description = ((String)response.getBody()).split("\"")[3];
            System.out.println(status.toString());
        } catch (Exception e) {
            System.out.println("Exception");
        }
        return description;
    }

    public static class TableEntry {
        private final SimpleStringProperty transDate;
        private final SimpleStringProperty postDate;
        private final SimpleStringProperty merchant;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty city;
        private int transactionCode;
    
        private TableEntry(String transDate, String postDate, String merchant,
                String amount, String city, int transactionCode) {
            this.transDate = new SimpleStringProperty(transDate);
            this.postDate = new SimpleStringProperty(postDate);
            this.merchant = new SimpleStringProperty(merchant);
            this.amount = new SimpleStringProperty(amount);
            this.city = new SimpleStringProperty(city);
            this.transactionCode = transactionCode;
        }
    
        public String getTransDate() {
            return this.transDate.get();
        }
    
        public String getPostDate() {
            return this.postDate.get();
        }

        public String getMerchant() {
            return this.merchant.get();
        }

        public String getAmount() {
            return this.amount.get();
        }

        public String getCity() {
            return this.city.get();
        }

        public int getTransactionCode() {
            return this.transactionCode;
        }
    
        public void setTransDate(String transDate) {
            this.transDate.set(transDate);
        }

        public void setPostDate(String postDate) {
            this.postDate.set(postDate);
        }

        public void setMerchant(String merchant) {
            this.merchant.set(merchant);
        }

        public void setAmount(String amount) {
            this.amount.set(amount);
        }

        public void setCity(String city) {
            this.city.set(city);
        }

        public void setTransactionCode(int transactionCode) {
            this.transactionCode = transactionCode;
        }
    }
}