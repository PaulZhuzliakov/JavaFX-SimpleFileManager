package fileManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    ListView<String> filesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filesList.getItems().addAll("wed Fe f fzdxvxfc1","adeasdf zsf dszg ere", "easraw");

    }
    public void btnExit(ActionEvent actionEvent) {
        Platform.exit();
    }
//    public List<FileInfo> scanFiles(Path root) {
//
////        Files.list(root).map(FileInfo::new).collect(Collectors.toList());
//    }
}
