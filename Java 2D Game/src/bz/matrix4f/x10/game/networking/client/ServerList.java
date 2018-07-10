package bz.matrix4f.x10.game.networking.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.cond.Condition;
import bz.matrix4f.x10.game.ui.AlertBox;
import bz.matrix4f.x10.game.ui.UI;
import bz.matrix4f.x10.game.ui.UIButton;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class ServerList {

    private InputStream url;
    private String path;
    private List<ServerInfo> info;

    public ServerList(InputStream url, String path) {
        this.url = url;
        this.path = path;
        info = new ArrayList<>();
        readFile();
    }

    public void readFile() {
        JSONArray array = (JSONArray) JSONValue.parse(new InputStreamReader(url));
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            String text = (String) obj.get("display");
            String ip = (String) obj.get("ip");
            int port = Integer.parseInt(obj.get("port").toString());
            info.add(new ServerInfo(port, text, ip));
        }
    }

    public UIButton[] load(final UI ui) {
        final UIButton[] buttons = new UIButton[info.size()];
        for (int i = 0; i < info.size(); i++) {
            final ServerInfo j = info.get(i);
            final int finalI = i;
            buttons[i] = UIButton.genServerBtn(100, 25 + i * 90, 80, j.getDisplayName(), new
                    Runnable() {
                public void run() {
                    switch (ServerCondAction.action) {
                        case NONE:
                            Game.ip = j.getIp();
                            Game.port = j.getPort();
                            Condition.set(Condition.Type.GAME);
                            break;
                        case DELETE:
                            ui.remove(buttons[finalI]);
                            remove(j);
                            save();
                            ServerCondAction.action = ServerCondAction.Action.NONE;
                            break;
                        case INFO:
                            AlertBox box = new AlertBox("Name: " + j.getDisplayName() + ",  Ip:" + j
                                    .getIp() + ",  Port:" + j.getPort(), "Server Info");
                            box.show();
                            ServerCondAction.action = ServerCondAction.Action.NONE;
                            break;
                    }
                }
            });

        }
        return buttons;
    }

    public void add(ServerInfo info) {
        this.info.add(info);
    }

    public void remove(ServerInfo info) {
        this.info.remove(info);
    }

    @SuppressWarnings("unchecked")
	public void save() {
        JSONArray array = new JSONArray();
        for (ServerInfo serverInfo : info) {
            JSONObject obj = new JSONObject();
            obj.put("display", serverInfo.getDisplayName());
            obj.put("port", serverInfo.getPort());
            obj.put("ip", serverInfo.getIp());
            array.add(obj);
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(array.toJSONString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
