package bz.matrix4f.x10.game.networking.server;

import bz.matrix4f.x10.game.networking.server.display.ServerUI;

public class ServerStarter {

	public static void main(String[] args) {
		ServerUI.launch(args);
	}

	public static void launchStage2(ServerUI ui) {
		Server server = new Server();
		server.setGui(ui);
		server.thread().start();
	}
}
