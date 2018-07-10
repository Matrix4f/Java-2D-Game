package bz.matrix4f.x10.game.networking.client;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class ServerInfo {

    private int port;
    private String displayName, ip;

    public ServerInfo(int port, String displayName, String ip) {
        this.port = port;
        this.displayName = displayName;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
