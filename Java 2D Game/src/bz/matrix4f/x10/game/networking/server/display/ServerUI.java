package bz.matrix4f.x10.game.networking.server.display;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bz.matrix4f.x10.game.map.MapLoader;
import bz.matrix4f.x10.game.networking.packet.Packet0gMapLoader;
import bz.matrix4f.x10.game.networking.server.Server;
import bz.matrix4f.x10.game.networking.server.ServerStarter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Matrix4f on 5/28/2016.
 */
public class ServerUI extends Application implements EventHandler<ActionEvent> {

    public static final String HTML_PREFIX = "<html><head><style>" +
            "body {" +
            "font-family:Corbel;\n" +
            "font-size:14px;\n" +
            "border:1px solid lightgray;\n" +
            "margin:0px;\n" +
            "padding:10px 10px 10px 10px;\n" +
            "line-height: 1.5;\n" +
            "} ::selection {" +
            "background-color:#9AD3E6;" +
            "}" +
            "</style></head><body>";
    public static final String HTML_SUFFIX = "</body></html>";
    public static final String STARTING_CONSOLE_TEXT_RAW = "====Server Console====<br>";
    public static final String STARTING_CONSOLE_TEXT = HTML_PREFIX + STARTING_CONSOLE_TEXT_RAW +
            HTML_SUFFIX;

    private Button runCmd;
    private ComboBox<String> cmdField;
    private ListView<String> clientList;
    private WebView view;
    private WebEngine engine;
    private TextArea consoleArea;

    private List<String> players = new ArrayList<>();
    private String consoleHTML = STARTING_CONSOLE_TEXT;
    private String console = STARTING_CONSOLE_TEXT_RAW;

    public static ServerUI This;
    private Server server;

    private static final String syntaxBan = "ban <player> <reason:wspace>";
    private static final String syntaxKick = "kick <player> <reason>";
    private static final String syntaxUnban = "unban <player>";
    private static final String syntaxChat = "chat <msg>";
    private static final String syntaxClearlog = "clearlog";
    private static final String syntaxSetmap = "setmap <filename>";

    public static void launch(String... args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage win) throws Exception {
        This = this;
        String[] items = new String[]{syntaxBan, syntaxKick, syntaxUnban, syntaxSetmap, syntaxClearlog, syntaxChat};
        GridPane pane = new GridPane();
        Scene scene = new Scene(pane, 500, 400);
        pane.setHgap(15);
        pane.setVgap(15);

        runCmd = new Button();
        runCmd.setText("Run");
        runCmd.setOnAction(this);

        cmdField = new ComboBox<>(FXCollections.observableArrayList(items));
        cmdField.setPromptText("Enter a command");
        cmdField.setEditable(true);

        clientList = new ListView<>();
        clientList.setFocusTraversable(false);
        clientList.setMaxWidth(120);
        clientList.setMaxHeight(125);

        view = new WebView();
        view.setMaxWidth(275);
        view.setMaxHeight(150);
        view.requestFocus();
        engine = view.getEngine();
        engine.loadContent(STARTING_CONSOLE_TEXT);

        consoleArea = new TextArea("====Command Console====\n");
        consoleArea.setMaxWidth(275);
        consoleArea.setMaxHeight(100);
        consoleArea.setFocusTraversable(false);
        consoleArea.setEditable(false);

        //LABELS
        Label playerLbl = new Label("Player List");
        //END-LABELS

        pane.add(cmdField, 1, 1);
        pane.add(runCmd, 2, 1);

        pane.add(view, 1, 2, 2, 2);
        pane.add(playerLbl, 3, 2);
        pane.add(clientList, 3, 3);

        pane.add(consoleArea, 1, 4, 2, 1);

        //Remove focus from the ComboBox, showing its "Enter a command" text
        Platform.runLater(new Runnable() {
            public void run() {
                view.requestFocus();
            }
        });

        win.setResizable(false);
        win.setScene(scene);
        win.show();
        win.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                server.stop();
            }
        });
        ServerStarter.launchStage2(This);
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() == runCmd)
            processCmd(cmdField.getSelectionModel().getSelectedItem());
    }

    public void processCmd(String cmd) {
        if(cmd == null || cmd.equals(""))
            return;
        cmd = cmd.trim();
        String[] tokens = cmd.split("[ ]");
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        int argsLength = args.length;
        String type = tokens[0];
        String argsStr = (tokens.length > 1) ? cmd.substring(type.length() + 1) : "";

        switch(type) {
            case "ban":
                runBan(args, argsStr, argsLength);
                break;
            case "chat":
                runChat(args, argsStr, argsLength);
                break;
            case "unban":
                runUnban(args, argsStr, argsLength);
                break;
            case "clearlog":
                console = STARTING_CONSOLE_TEXT_RAW;
                consoleHTML = STARTING_CONSOLE_TEXT;
                engine.loadContent(consoleHTML);
                consoleArea.setText("====Command Console====\n");
                break;
            case "setmap":
                runSetmap(args, argsStr, argsLength);
                break;
            case "kick":
                runKick(args, argsStr, argsLength);
                break;
        }
    }

    private void runChat(String[] args, String argsStr, int argsLength) {
        String msg = argsStr;
        server.sendChatMsg(msg);
    }

    private void runSetmap(String[] args, String argsStr, int argsLength) {
        String filename = argsStr;
        File file = new File(filename);
        if(argsLength != 1) {
            cmdLog("[Error] Usage: " + syntaxSetmap);
            return;
        }
        if(!file.exists()) {
            cmdLog("[Error] File not found: '" + filename + "'");
            return;
        }
        String mapdata = MapLoader.readMapFormat(file);
        server.setMapdata(mapdata);
        server.sendPacketToAllClients(new Packet0gMapLoader(mapdata));
    }

    private void runUnban(String[] args, String argsStr, int argsLength) {
        if(argsLength != 1) {
            cmdLog("[Error] Usage: " + syntaxUnban);
            return;
        }
        String username = args[0];
        server.getBannedPlayers().unbanPlayer(username);
        cmdLog("[Success] Un-banned " + username + ".");
    }

    private void runBan(String[] args, String argsStr, int argsLength) {
        String reason = "";
        if(argsLength == 0) {
            cmdLog("[Error] Usage: " + syntaxBan);
            return;
        }
        String username = args[0];
        if(argsLength >= 2)
            reason = argsStr.substring(username.length() + 1);
        server.getBannedPlayers().banPlayer(username, reason, server);
        cmdLog("[Success] Banned " + username + ". Reason: " + reason);
    }

    private void runKick(String[] args, String argsStr, int argsLength) {
        String reason = "";
        if(argsLength == 0) {
            cmdLog("[Error] Usage: " + syntaxKick);
            return;
        }
        String username = args[0];
        if(argsLength >= 2)
            reason = argsStr.substring(username.length() + 1);
        server.kickPlayer(username, reason);
        cmdLog("[Success] Kicked " + username + ". Reason: " + reason);
    }

    public void cmdLog(String msg) {
        consoleArea.appendText(msg + "\n");
    }

    public void log(String msg) {
        console += msg + "<br>";
        consoleHTML = HTML_PREFIX + ColorHandler.convertToHTML(console) + HTML_SUFFIX;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.loadContent(consoleHTML);
            }
        });
    }

    public void addPlayer(String name) {
        players.add(name);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.setItems(FXCollections.observableArrayList(players));
            }
        });
    }

    public void removePlayer(String name) {
        players.remove(name);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.setItems(FXCollections.observableArrayList(players));
            }
        });
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
