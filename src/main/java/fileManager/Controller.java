package fileManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    ListView<FileInfo> filesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Paths.get - способ создания пассов в Java NIO. Pass - то интефейс, нельзя создать объект интерфейса
        Path root = Paths.get("forTests");
        List<FileInfo> files = scanFiles(root);
        filesList.getItems().addAll(files);

        //.setCellFactory()  - генерирует ячейки для ListView
//        filesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
//            @Override
//            public ListCell<FileInfo> call(ListView<FileInfo> fileInfoListView) {
//                return new ListCell<FileInfo>() {
//                    @Override
//                    protected void updateItem(FileInfo item, boolean empty) {
//                        super.updateItem(item, empty);
//                    }
//                };
//            }
//        });
    }

    public void btnExit(ActionEvent actionEvent) {
        Platform.exit();
    }

//создание списка FileInfo по пути с помощью StreamAPI, короткий вариант
    public List<FileInfo> scanFiles(Path root) {
        //Files.list(root)              - запрос списка файлов в директории, возвращается Stream путей
        //.map(FileInfo::new)           - Stream путей преобразуется в FileInfo
        //.collect(Collectors.toList()) - сбор в List и возврат через return
        try {
            return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Files scan exception: " + root);
        }
    }

//    //создание списка FileInfo, длинный вариант
//    public List<FileInfo> scanFiles(Path root) {
//        try {
//            //создание выходного листа
//            List<FileInfo> out = new ArrayList<>();
//            //создание List`а path`ов из корневого каталога
//            List<Path> pathsInRoot = Files.list(root).collect(Collectors.toList());
//            for (Path p : pathsInRoot) {
//                out.add(new FileInfo(p));
//            }
//            return out;
//        } catch (IOException e) {
//            throw new RuntimeException("Files scan exception: " + root);
//        }
//    }

}
