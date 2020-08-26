package fileManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    ListView<FileInfo> filesList;

    @FXML
    TextField pathField;

    //нужно запоминать текущий адрес
    Path root;

    Path selectedCopyFile;

    Path selectedMoveFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Paths.get - способ создания пассов в Java NIO. Path - то интефейс, нельзя создать объект интерфейса
//        Path root = Paths.get("forTests");


        //.setCellFactory()  - генерирует ячейки для ListView
        filesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        //если нет информации о файле или ячейка пустая
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            //%s - строка. ограничение строки не более 30ти символов,"-" - выравнивание по левому краю
                            String formattedFileName = String.format("%-30s", item.getFileName());
                            //%d - число, "," - разделитель
                            String formattedFileLength = String.format("%,d bytes", item.getLength());
                            if (item.getLength() == -1L) {
                                formattedFileLength = String.format("%s", "Директория");
                            }
                            if (item.getLength() == -2L) {
                                formattedFileLength = "";
                            }
                            String text = String.format("%s %-20s", formattedFileName, formattedFileLength);
                            setText(text);
                        }
                    }
                };
            }
        });
        goToPath(Paths.get("forTests"));
    }

    public void btnExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void goToPath(Path path) {
        //запоминаем папку куда перемещаемся
        root = path;
        //путь прописывается в pathField`е
        pathField.setText(root.toAbsolutePath().toString());
        //перед тем как заполнять коллекцию, её нужно почистить
        filesList.getItems().clear();
        //переход на уровень выше. Будет выше директорий, потому что размер ещё меньше
        filesList.getItems().add(new FileInfo(FileInfo.UpToken, -2L));
        //список файлов обновляется на соответствующую папку
        filesList.getItems().addAll(scanFiles(path));
        filesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            //лексографическая сортировка папок и файлов
            //компаратор возвращает 1, если первое число больше второго; -1 если наоборот. 0 если равны;
            public int compare(FileInfo f1, FileInfo f2) {
                if (f1.getFileName().equals(FileInfo.UpToken))
                    return -1;
                //если знаки одинаковые(два положительных или два отрицательных), то это две папки или два файла
                if ((int) Math.signum(f1.getLength()) == (int) Math.signum(f2.getLength())) {
                    return f1.getFileName().compareTo(f2.getFileName());
                }
                return (int) (f1.getLength() - f2.getLength());
            }
        });
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

    //переход в пвпку
    public void filesListClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
            //если двойной клик не был сделан в пустое пространство
            if (fileInfo != null) {
                if (fileInfo.isDirectory()) {
                    Path pathTo = root.resolve(fileInfo.getFileName());
                    goToPath(pathTo);
                }
                if (fileInfo.isUpElement()) {
                    if (root.getParent() != null) {
                        Path pathTo = root.toAbsolutePath().getParent();
                        goToPath(pathTo);
                    }
                }
            }
        }
    }

    //обновление списка файлов. По сути - переход в текущий каталог
    public void refresh() {
        goToPath(root);
    }

    public void copyAction(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
        if (selectedCopyFile == null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }
        //.getSource() - В событии есть информация о том элементе, который вызвал это событие
        //каст события к Button
        if (selectedCopyFile == null) {
            selectedCopyFile = root.resolve(fileInfo.getFileName());
            ((Button) actionEvent.getSource()).setText("Копируется: " + selectedCopyFile);
            return;
        }
        //если этот файл ести и кнопку нажзимают ещё раз
        if (selectedCopyFile != null) {
            //перезаписать существующий в случае совпадения
            try {
                //опируем не просто в папку, а в папку + имя файла .resolve(selectedCopyFile.getFileName())
                Files.copy(selectedCopyFile, root.resolve(selectedCopyFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                //после копирования очищаем временный Путь
                selectedCopyFile = null;
                ((Button) actionEvent.getSource()).setText("Копирование");
                refresh();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно скопировать файл");
                alert.showAndWait();
            }
        }
    }

    public void moveAction(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
        if (selectedMoveFile == null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }
        //.getSource() - В событии есть информация о том элементе, который вызвал это событие
        //каст события к Button
        if (selectedMoveFile == null) {
            selectedMoveFile = root.resolve(fileInfo.getFileName());
            ((Button) actionEvent.getSource()).setText("Перемещается: " + selectedMoveFile);
            return;
        }
        //если этот файл ести и кнопку нажзимают ещё раз
        if (selectedMoveFile != null) {
            //перезаписать существующий в случае совпадения
            try {
                //опируем не просто в папку, а в папку + имя файла .resolve(selectedCopyFile.getFileName())
                Files.move(selectedMoveFile, root.resolve(selectedMoveFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                //после копирования очищаем временный Путь
                selectedMoveFile = null;
                ((Button) actionEvent.getSource()).setText("Перемещение");
                refresh();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно переместить файл");
                alert.showAndWait();
            }
        }
    }

    public void deleteAction(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
        if (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement()) {
            return;
        }
        try {
            Files.delete(root.resolve(fileInfo.getFileName()));
            refresh();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно удалить выбранный файл");
            alert.showAndWait();
        }
    }
}
