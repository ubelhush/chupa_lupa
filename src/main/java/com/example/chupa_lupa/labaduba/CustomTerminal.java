package com.example.chupa_lupa.labaduba;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomTerminal extends Application {
    private static String currentDirectory = System.getProperty("user.dir");

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        TextArea outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        ScrollPane scrollPane = new ScrollPane(outputTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        TextField inputTextField = new TextField();
        inputTextField.setOnAction(event -> {
            String command = inputTextField.getText();
            outputTextArea.appendText(">> " + command + "\n");
            outputTextArea.appendText(commands(command));
            inputTextField.clear();
        });
        root.setBottom(inputTextField);
        BorderPane.setMargin(inputTextField, new Insets(10));

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("JavaFX Terminal");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private String commands(String input) {
        //Scanner scanner = new Scanner(System.in);
        String result= currentDirectory + "> ";
        //String input = scanner.nextLine();
        String[] parts = input.split(" ");
        String commandName = parts[0];

        switch (commandName) {
            case "find":
                if (parts.length != 2) {
                    result="Usage: find <search_term> \n";
                    break;
                }
                String searchTerm = parts[1];
                result="Searching for files and directories matching '" + searchTerm + "'..."+"\n";
                List<String> foundItems = findItems(currentDirectory, searchTerm);
                if (foundItems.isEmpty()) {
                    result="No items found.\n";
                } else {
                    result="Found items:\n";
                    for (String item : foundItems) {
                        result=item+"\n";
                    }
                }
                break;
            case "kill":
                if (parts.length != 2) {
                    result="Usage: kill <process_id>\n";
                    break;
                }
                int processId = Integer.parseInt(parts[1]);
                try {
                    Process process = Runtime.getRuntime().exec("kill " + processId);
                    process.waitFor();
                    result="Process with ID " + processId + " killed.\n";
                } catch (IOException | InterruptedException e) {
                    result="Error killing process: " + e.getMessage()+"\n";
                }
                break;
            case "ps":
                try {
                    Process process = Runtime.getRuntime().exec("tasklist");
                    Scanner processScanner = new Scanner(process.getInputStream());
                    while (processScanner.hasNextLine()) {
                        result=processScanner.nextLine()+"\n";
                    }
                } catch (IOException e) {
                    result="Error listing processes: " + e.getMessage()+"\n";
                }
                break;
            case "ls":
                String directoryPath = parts.length > 1 ? parts[1] : currentDirectory;
                File directory = new File(directoryPath);
                if (directory.exists() && directory.isDirectory()) {
                    result="Listing files and directories in " + directoryPath + ":\n";
                    File[] files = directory.listFiles();
                    for (File file : files) {
                        result=file.getName()+"\n";
                    }
                } else {
                    result="Directory " + directoryPath + " not found.\n";
                }
                break;
            case "mkdir":
                if (parts.length != 2) {
                    result="Usage: mkdir <directory_name>\n";
                    break;
                }
                String newDirectoryName = parts[1];
                File newDirectory = new File(newDirectoryName);
                if (!newDirectory.exists()) {
                    boolean created = newDirectory.mkdir();
                    if (created) {
                        result="Directory " + newDirectoryName + " created successfully.\n";
                    } else {
                        result="Failed to create directory " + newDirectoryName + ".\n";
                    }
                } else {
                    result="Directory " + newDirectoryName + " already exists.\n";
                }
                break;
            case "cd":
                if (parts.length != 2) {
                    result="Usage: cd <directory_path>\n";
                    break;
                }
                String targetDirectory = parts[1];
                File newDir = new File(targetDirectory);
                if (newDir.exists() && newDir.isDirectory()) {
                    currentDirectory = newDir.getAbsolutePath();
                    result="Changed directory to " + currentDirectory+"\n";
                } else {
                    result="Directory " + targetDirectory + " not found.\n";
                }
                break;
            case "pwd":
                result="Current directory: " + currentDirectory+"\n";
                break;
            case "echo":
                String message = input.substring(input.indexOf(" ") + 1);
                result=message+"\n";
                break;
            case "clear":
                clearScreen();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                result="Unknown command: " + commandName+"\n";
                break;
        }

        return result;

    }

    private static List<String> findItems(String directoryPath, String searchTerm) {
        List<String> foundItems = new ArrayList<>();
        findItemsRecursive(new File(directoryPath), searchTerm, foundItems);
        return foundItems;
    }

    private static void findItemsRecursive(File directory, String searchTerm, List<String> foundItems) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(searchTerm)) {
                    foundItems.add(file.getAbsolutePath());
                }
                if (file.isDirectory()) {
                    findItemsRecursive(file, searchTerm, foundItems);
                }
            }
        }
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
