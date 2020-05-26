package games.emgflappy.project.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import games.emgflappy.project.EMGflappy;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;
		config.addIcon("desktop_icons/flappy_icon128.png", Files.FileType.Internal);
		config.addIcon("desktop_icons/flappy_icon64.png", Files.FileType.Internal);
		config.addIcon("desktop_icons/flappy_icon32.png", Files.FileType.Internal);
		config.addIcon("desktop_icons/flappy_icon16.png", Files.FileType.Internal);
		new LwjglApplication(new EMGflappy(), config);
	}
}
