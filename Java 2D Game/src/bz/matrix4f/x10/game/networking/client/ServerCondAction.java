package bz.matrix4f.x10.game.networking.client;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class ServerCondAction {

    public static enum Action {
        NONE, DELETE, INFO;
    }

    public static Action action = Action.NONE;
}
