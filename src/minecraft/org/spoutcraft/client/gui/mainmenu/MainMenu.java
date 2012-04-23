package org.spoutcraft.client.gui.mainmenu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.getspout.commons.ChatColor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.client.gui.MCRenderDelegate;
import org.spoutcraft.client.gui.addon.GuiAddonsLocal;
import org.spoutcraft.client.gui.settings.GameSettingsScreen;
import org.spoutcraft.client.io.CustomTextureManager;
import org.spoutcraft.client.io.FileUtil;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.Addon;
import org.spoutcraft.spoutcraftapi.gui.Button;
import org.spoutcraft.spoutcraftapi.gui.Color;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.gui.GenericTexture;
import org.spoutcraft.spoutcraftapi.gui.Label;
import org.spoutcraft.spoutcraftapi.gui.RenderPriority;
import org.spoutcraft.spoutcraftapi.gui.Texture;
import org.spoutcraft.spoutcraftapi.gui.WidgetAnchor;

import net.minecraft.src.GuiScreen;

public class MainMenu extends GuiScreen{
	final static List<String> splashes = new ArrayList<String>(1000);
	Button singleplayer, multiplayer, textures, addons, about, options, fastLogin;
	Texture background, logo;
	Label splashText;
	final String timeOfDay;
	final List<String> backgrounds;
	
	public MainMenu() {
		splashText = new GenericLabel(getSplashText());
		fastLogin = new GenericButton(ChatColor.GREEN + "Fast Login");
		fastLogin.setVisible(ConfigReader.fastLogin);
		timeOfDay = getTimeFolder();
		
		int picture = 1;
		int pass = 0;
		StringBuilder builder = new StringBuilder();
		backgrounds = new ArrayList<String>();
		while(true) {
			builder.append("/res/splash/");
			builder.append(timeOfDay);
			builder.append("/");
			builder.append(timeOfDay);
			builder.append(picture);
			builder.append(pass == 0 ? ".png" : ".jpg");
			if (CustomTextureManager.getTextureFromJar(builder.toString()) != null) {
				backgrounds.add(builder.toString());
				picture++;
				pass = 0;
			}
			else if (pass == 0) {
				pass++;
			}
			else {
				break;
			}
			builder.setLength(0); //reset
		}
		
		//Randomize background order
		Random rand = new Random();
		//Randomize by swapping the first background with a random background in the list
		//Repeat sufficient times
		for (int i = 0; i < backgrounds.size() * 2; i++) {
			int newIndex = rand.nextInt(backgrounds.size());
			String temp = backgrounds.get(0);
			backgrounds.set(0, backgrounds.get(newIndex));
			backgrounds.set(newIndex, temp);
		}
		
		if (backgrounds.size() == 0) {
			System.out.println("Failed to find any backgrounds for " + timeOfDay);
			backgrounds.add("/res/splash/day/day1.jpg");
		}
		
		background = new BackgroundTexture(backgrounds);
	}
	
	private static String getSplashText() {
		BufferedReader br = null;
		try {
			if (splashes.size() == 0) {
				File splashTextFile = new File(FileUtil.getSpoutcraftDirectory(), "splashes.txt");
				//refresh every day
				if (!splashTextFile.exists() || (System.currentTimeMillis() - splashTextFile.lastModified() > (1L * 24 * 60 * 60 * 1000))) {
					URL test = new URL("http://cdn.spout.org/splashes.txt");
					HttpURLConnection urlConnect = (HttpURLConnection) test.openConnection();
					System.setProperty("http.agent", "");
					urlConnect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
					
					File temp = new File(FileUtil.getSpoutcraftDirectory(), "splashes.temp");
					if (temp.exists()) {
						temp.delete();
					}
					FileUtils.copyInputStreamToFile(urlConnect.getInputStream(), temp);
					FileUtils.moveFile(temp, splashTextFile);
				}
				br = new BufferedReader(new InputStreamReader(new FileInputStream(splashTextFile)));
				String line;
				splashes.clear();
				while ((line = br.readLine()) != null) {
					splashes.add(line);
				}
				br.close();
			}
			return splashes.get((new Random()).nextInt(splashes.size()));
		}
		catch (Exception e) {
			return "I <3 Spout";
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception ignore) { }
			}
		}
	}
	
	private static String getTimeFolder() {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hours < 6)
			return "night";
		if (hours < 12)
			return "day";
		if (hours < 20) {
			return "evening";
		}
		return "night";
	}
	
	public void initGui() {
		Addon spoutcraft = Spoutcraft.getAddonManager().getAddon("Spoutcraft");

		fastLogin.setGeometry(width - 150, height - 205, 100, 20);
		
		singleplayer = new GenericButton("Singleplayer");
		singleplayer.setGeometry(width - 150, height - 180, 100, 20);
		
		multiplayer = new GenericButton("Multiplayer");
		multiplayer.setGeometry(width - 150, height - 155, 100, 20);
		
		textures = new GenericButton("Textures");
		textures.setGeometry(width - 150, height - 130, 100, 20);
		
		addons = new GenericButton("Addons");
		addons.setGeometry(width - 150, height - 105, 100, 20);
		
		about = new GenericButton("About");
		about.setGeometry(Math.min(100, width - 300), height - 105, 65, 20);
		
		options = new GenericButton("Options");
		options.setGeometry(Math.min(175, width - 225), height - 105, 65, 20);

		background.setGeometry(0, 0, width, height);
		background.setPriority(RenderPriority.Highest);
		background.setAnchor(WidgetAnchor.TOP_LEFT);
		background.setLocal(true);
		
		splashText.setGeometry(Math.min(100, width - 275), height - 135, 200, 12);
		splashText.setTextColor(new Color(0x6CC0DC));
		int textWidth = Spoutcraft.getRenderDelegate().getMinecraftFont().getTextWidth(splashText.getText());
		float scale = ((width - 255F) / textWidth);
		splashText.setScale(Math.min(1.5F, scale));
		
		logo = new ScaledTexture("/res/spoutcraft.png");
		((ScaledTexture)logo).setScale(Math.min(1F, (width - 170F) / 256F));
		logo.setGeometry(15, height - 185, 256, 64);
		logo.setLocal(true);
		logo.setDrawAlphaChannel(true);

		this.getScreen().attachWidgets(spoutcraft, singleplayer, multiplayer, textures, addons, about, options, background, logo, splashText, fastLogin);
	}
	
	@Override
	public void buttonClicked(Button btn) {
		if (singleplayer == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.singleplayer.GuiWorldSelection(this));
		}
		if (multiplayer == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.server.GuiFavorites(this));
		}
		if (textures == btn) {
			this.mc.displayGuiScreen(new GuiAddonsLocal());
		}
		if (addons == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.texturepacks.GuiTexturePacks());
		}	
		if (about == btn) {
			this.mc.displayGuiScreen(new org.spoutcraft.client.gui.about.GuiAbout());
		}
		if (options == btn) {
			mc.displayGuiScreen(new GameSettingsScreen(this));
		}
		if (fastLogin == btn) {
			ConfigReader.fastLogin = !ConfigReader.fastLogin;
			ConfigReader.write();
			fastLogin.setText((ConfigReader.fastLogin ? ChatColor.GREEN : ChatColor.RED) + "Fast Login");
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float scroll) {
		super.drawScreen(mouseX, mouseY, scroll);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.server.GuiFavorites(this));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.singleplayer.GuiWorldSelection(this));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			mc.displayGuiScreen(new GuiAddonsLocal());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.texturepacks.GuiTexturePacks());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			mc.displayGuiScreen(new GameSettingsScreen(this));
		}
	}
	
}

class ScaledTexture extends GenericTexture {
	float scale;
	ScaledTexture(String path) {
		super(path);
	}
	
	public ScaledTexture setScale(float scale) {
		this.scale = scale;
		return this;
	}
	
	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScalef(scale, 1F, 1F);
		super.render();
		GL11.glPopMatrix();
	}
}

class BackgroundTexture extends GenericTexture {
	static final int PAN_TIME = 400;
	static final int EXTRA_PAN_TIME = 150;
	static final int HEIGHT_PERCENT = 70;
	static final int WIDTH_PERCENT = 75;
	final List<String> backgrounds;
	final Random rand = new Random();
	int maxPanTime = PAN_TIME;
	int panTime = PAN_TIME;
	int picture = -1;
	
	BackgroundTexture(List<String> backgrounds) {
		super(backgrounds.get(0));
		this.backgrounds = backgrounds;
		cycleBackground();
	}
	
	public void cycleBackground() {
		picture++;
		if (picture >= backgrounds.size()) {
			picture = 0;
		}
		setUrl(backgrounds.get(picture));
		maxPanTime = PAN_TIME + rand.nextInt(EXTRA_PAN_TIME);
		panTime = maxPanTime;
	}
	
	@Override
	public void render() {
		org.newdawn.slick.opengl.Texture tex = CustomTextureManager.getTextureFromJar(getUrl());
		GL11.glPushMatrix();
		if (tex != null) {
			int adjustedX = ((100 - HEIGHT_PERCENT) / 2) * tex.getImageHeight() * panTime;
			adjustedX /= maxPanTime;
			adjustedX /= 100;
			
			int adjustedY = ((100 - WIDTH_PERCENT) / 2) * tex.getImageWidth() * panTime;
			adjustedY /= maxPanTime;
			adjustedY /= 100;
			
			int adjustedHeight = tex.getImageHeight() - adjustedX;
			
			int adjustedWidth = tex.getImageWidth() - adjustedY;
			
			GL11.glScaled(this.getActualWidth() / (adjustedWidth - adjustedX), this.getActualHeight() / (adjustedHeight - adjustedY), 1D);
			GL11.glTranslatef(-adjustedX, -adjustedY, 0F);
			((MCRenderDelegate)Spoutcraft.getRenderDelegate()).drawTexture(tex, adjustedWidth, adjustedHeight, false, -1, -1, true);
			
			if (panTime > 0) {
				panTime--;
			}
			else {
				cycleBackground();
			}
		}
		GL11.glPopMatrix();
	}
}