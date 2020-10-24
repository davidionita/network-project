package client;

import client.commands.ClientCommand;
import client.commands.ClientCommandManager;
import logs.FileLogger;
import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.client.ClientMessagePacket;
import packets.client.ClientUsernameRequestPacket;
import packets.server.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * For Java 8, javafx is installed with the JRE. You can run this program normally.
 * For Java 9+, you must install JavaFX separately: https://openjfx.io/openjfx-docs/
 * If you set up an environment variable called PATH_TO_FX where JavaFX is installed
 * you can compile this program with:
 *  Mac/Linux:
 *      > javac --module-path $PATH_TO_FX --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 *  Windows CMD:
 *      > javac --module-path %PATH_TO_FX% --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 *  Windows Powershell:
 *      > javac --module-path $env:PATH_TO_FX --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 * 
 * Then, run with:
 * 
 *  Mac/Linux:
 *      > java --module-path $PATH_TO_FX --add-modules javafx.controls day10_chatgui.ChatGuiClient 
 *  Windows CMD:
 *      > java --module-path %PATH_TO_FX% --add-modules javafx.controls day10_chatgui.ChatGuiClient
 *  Windows Powershell:
 *      > java --module-path $env:PATH_TO_FX --add-modules javafx.controls day10_chatgui.ChatGuiClient
 * 
 * There are ways to add JavaFX to your to your IDE so the compile and run process is streamlined.
 * That process is a little messy for VSCode; it is easiest to do it via the command line there.
 * However, you should open  Explorer -> Java Projects and add to Referenced Libraries the javafx .jar files 
 * to have the syntax coloring and autocomplete work for JavaFX 
 */

class ServerInfo {
    public final String serverAddress;
    public final int serverPort;

    public ServerInfo(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
}

public class ChatGuiClient extends Application {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private Socket socket;
    //private BufferedReader in;
    //private PrintWriter out;
    private static ObjectInputStream socketIn;
    private static ObjectOutputStream socketOut;

    private static Logger logger = new FileLogger();

    public static final boolean DEBUG_MODE = false;
    
    private Stage stage;
    private Label currentUsersList;
    private TextArea messageArea;
    private TextField textInput;
    private Button emojiButton;
    private Button sendButton;

    private ServerInfo serverInfo;
    //volatile keyword makes individual reads/writes of the variable atomic
    // Since username is accessed from multiple threads, atomicity is important 
    private volatile String username = "";
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception, IOException {
        //If ip and port provided as command line arguments, use them
        List<String> args = getParameters().getUnnamed();
        if (args.size() == 2){
            this.serverInfo = new ServerInfo(args.get(0), Integer.parseInt(args.get(1)));
        }
        else {
            //otherwise, use a Dialog.
            Optional<ServerInfo> info = getServerIpAndPort();
            if (info.isPresent()) {
                this.serverInfo = info.get();
            } 
            else{
                Platform.exit();
                return;
            }
        }

        this.stage = primaryStage;
        BorderPane borderPane = new BorderPane();

        currentUsersList = new Label();
        currentUsersList.setWrapText(true);

        HBox culhbox = new HBox();
        culhbox.getChildren().addAll(new Label("Current Users: "), currentUsersList);
        borderPane.setTop(culhbox);

        messageArea = new TextArea();
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        borderPane.setCenter(messageArea);

        //At first, can't send messages - wait for WELCOME!
        textInput = new TextField();
        textInput.setEditable(false);
        textInput.setOnAction(e -> sendMessage());
        emojiButton = new Button(":-)");
        emojiButton.setDisable(false);
        emojiButton.setOnAction(e -> openEmojiKeyboard());
        sendButton = new Button("Send");
        sendButton.setDisable(true);
        sendButton.setOnAction(e -> sendMessage());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(new Label("Message: "), textInput, emojiButton, sendButton);
        HBox.setHgrow(textInput, Priority.ALWAYS);
        borderPane.setBottom(hbox);

        Scene scene = new Scene(borderPane, 400, 500);
        stage.setTitle("Chat Client");
        stage.setScene(scene);
        stage.show();

        ServerListener socketListener = new ServerListener();
        
        //Handle GUI closed event
        stage.setOnCloseRequest(e -> {
            try {
                logger.log("QUIT MANUALLY");
                socketOut.close();
                socketIn.close();
                socket.close();
                logger.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            socketListener.appRunning = false;
            try {
                socket.close(); 
            } catch (IOException ex) {}
        });

        new Thread(socketListener).start();
    }

    // First detect OS type then execute keyboard shortcut to open emoji keyboard
    private void openEmojiKeyboard() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            Robot robot = new Robot();

            if (os.contains("win")) {
                robot.keyPress(KeyEvent.VK_WINDOWS);
                robot.keyPress(KeyEvent.VK_PERIOD);
                robot.keyRelease(KeyEvent.VK_WINDOWS);
                robot.keyRelease(KeyEvent.VK_PERIOD);
            } else if (os.contains("mac")) {
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_META);
                robot.keyRelease(KeyEvent.VK_SPACE);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendMessage() {
        ClientCommandManager commandManager = new ClientCommandManager(logger, new ServerConnectionData(socketIn, socketOut));

        String input = textInput.getText().trim();
        if (input.length() == 0)
            return;
        textInput.clear();

        try {
            logger.log(input, LogType.USER_INPUT);
            String[] inputArray = input.split("\\s+");

            ClientCommand command = commandManager.getCommand(inputArray[0]);

            if(command != null) {
                command.execute(Arrays.asList(inputArray));
            } else {
                ClientMessagePacket message = new ClientMessagePacket(input);
                socketOut.writeObject(message);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Optional<ServerInfo> getServerIpAndPort() {
        // In a more polished product, we probably would have the ip /port hardcoded
        // But this a great way to demonstrate making a custom dialog
        // Based on Custom Login Dialog from https://code.makery.ch/blog/javafx-dialogs-official/

        // Create a custom dialog for server ip / port
        Dialog<ServerInfo> getServerDialog = new Dialog<>();
        getServerDialog.setTitle("Enter Server Info");
        getServerDialog.setHeaderText("Enter your server's IP address and port: ");

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonData.OK_DONE);
        getServerDialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the ip and port labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddress = new TextField();
        ipAddress.setPromptText("e.g. localhost, 127.0.0.1");
        grid.add(new Label("IP Address:"), 0, 0);
        grid.add(ipAddress, 1, 0);

        TextField port = new TextField();
        port.setPromptText("e.g. 54321");
        grid.add(new Label("Port number:"), 0, 1);
        grid.add(port, 1, 1);


        // Enable/Disable connect button depending on whether a address/port was entered.
        Node connectButton = getServerDialog.getDialogPane().lookupButton(connectButtonType);
        connectButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ipAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            connectButton.setDisable(newValue.trim().isEmpty());
        });

        port.textProperty().addListener((observable, oldValue, newValue) -> {
            // Only allow numeric values
            if (! newValue.matches("\\d*"))
                port.setText(newValue.replaceAll("[^\\d]", ""));

            connectButton.setDisable(newValue.trim().isEmpty());
        });

        getServerDialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default.
        Platform.runLater(() -> ipAddress.requestFocus());


        // Convert the result to a ServerInfo object when the login button is clicked.
        getServerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new ServerInfo(ipAddress.getText(), Integer.parseInt(port.getText()));
            }
            return null;
        });

        return getServerDialog.showAndWait();
    }

    private String getName(){
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Enter Chat Name");
        nameDialog.setHeaderText("Please enter your username.");
        nameDialog.setContentText("Name: ");
        
        while(username.equals("")) {
            Optional<String> name = nameDialog.showAndWait();
            if (!name.isPresent() || name.get().trim().equals(""))
                nameDialog.setHeaderText("You must enter a nonempty name: ");
            else if (name.get().trim().contains(" "))
                nameDialog.setHeaderText("The name must have no spaces: ");
            else
            username = name.get().trim();            
        }
        return username;
    }

    class ServerListener implements Runnable {

        volatile boolean appRunning = false;

        public void run() {
            try {
                // Set up the socket for the Gui
                socket = new Socket(serverInfo.serverAddress, serverInfo.serverPort);
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //out = new PrintWriter(socket.getOutputStream(), true);
                socketIn = new ObjectInputStream(socket.getInputStream());
                socketOut = new ObjectOutputStream(socket.getOutputStream());
                
                appRunning = true;
                //Ask the gui to show the username dialog and update username
                //Send to the server
                Platform.runLater(() -> {
                    String username = getName();

                    try {
                        socketOut.writeObject(new ClientUsernameRequestPacket(username));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                });

                //handle all kinds of incoming messages
                String incoming = "";
                Packet input;

                while (appRunning && (input = (Packet) socketIn.readObject()) != null) {

                    if (input instanceof ServerJoinPacket) {
                        ServerJoinPacket status = (ServerJoinPacket) input;
                        logger.log(String.format("'%s' has joined the server! Connected users: %s.", status.username, String.join(", ", status.connectedUsers)), LogType.CONNECTED);

                        String user = status.username;
                        //got welcomed? Now you can send messages!
                        if (user.equals(username)) {
                            Platform.runLater(() -> {
                                stage.setTitle("Chatter - " + username);
                                textInput.setEditable(true);
                                sendButton.setDisable(false);
                                currentUsersList.setText(String.join(", ", status.connectedUsers));
                                messageArea.appendText("Welcome to the chatroom, " + username + "!\n");
                            });
                        }
                        else {
                            Platform.runLater(() -> {
                                currentUsersList.setText(String.join(", ", status.connectedUsers));
                                messageArea.appendText(user + " has joined the chatroom.\n");
                            });
                        }

                    } else if (input instanceof ServerRoutedMessagePacket) {
                        ServerRoutedMessagePacket messagePacket = (ServerRoutedMessagePacket) input;

                        if(messagePacket.isPrivate) {
                            // private messages
                            if(messagePacket.recipients != null) {
                                if(messagePacket.recipients.size() == 0) {
                                    // no one received the message
                                    logger.log("Sending Error: None of the recipients you addressed your message are currently connected.", LogType.ERROR);
                                } else {
                                    // list of recipients only returned to the sender
                                    String recipients = String.join(", ", messagePacket.recipients);
                                    logger.log(String.format("%s%s (Privately)%s @ %s > %s", recipients, ANSI_RED, ANSI_RESET, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                                }
                            } else {
                                logger.log(String.format("%s%s (Privately)%s @ %s > %s", messagePacket.senderUsername, ANSI_RED, ANSI_RESET, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                                Platform.runLater(() -> {
                                    messageArea.appendText(messagePacket.senderUsername + ANSI_RED + " (Privately)" + ANSI_RESET + " @ " + new SimpleDateFormat().format(messagePacket.timestamp) +  " > " + messagePacket.message + "\n");
                                });
                            }
                        } else {
                            // public messages
                            logger.log(String.format("%s @ %s > %s", messagePacket.senderUsername, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                            Platform.runLater(() -> {
                                messageArea.appendText(messagePacket.senderUsername + " @ " + new SimpleDateFormat().format(messagePacket.timestamp) +  " > " + messagePacket.message + "\n");
                            });
                        }

                    } else if (input instanceof ServerDisconnectPacket) {
                        ServerDisconnectPacket status = (ServerDisconnectPacket) input;
                        logger.log(String.format("'%s' has disconnected from the server! Connected users: %s.", status.username, String.join(", ", status.connectedUsers)));

                        String user = status.username;
                        Platform.runLater(() -> {
                            currentUsersList.setText(String.join(", ", status.connectedUsers));
                            messageArea.appendText(user + "has left the chatroom.\n");
                        });
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                if (appRunning)
                    System.out.println(e.getMessage());
                    e.printStackTrace();
            } 
            finally {
                Platform.runLater(() -> {
                    stage.close();
                });
                try {
                    if (socket != null)
                        socket.close();
                }
                catch (IOException e){
                }
            }
        }
    }
}