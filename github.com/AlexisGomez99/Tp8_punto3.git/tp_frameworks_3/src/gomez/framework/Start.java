package gomez.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.Button.ButtonRenderer;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import gomez.utilizacion.Accion;

public class Start {

	private Map<Integer, Accion> acciones = new HashMap<Integer, Accion>();
	private Accion accion;
	DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
	Screen screen = null;

	public Start(String path) {
		Properties prop = new Properties();
		try (InputStream configFile = getClass().getResourceAsStream(path);) {
			prop.load(configFile);
			String valor = prop.getProperty("acciones");
			String[] valores = valor.split(";");
			Class<?> clase;
			for (int i = 0; i < valores.length; i++) {
				clase = Class.forName(valores[i]);
				acciones.put(i + 1, (Accion) clase.getDeclaredConstructor().newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void desplegarMenu() {
		try {
			this.screen = terminalFactory.createScreen();
			this.screen.startScreen();

			final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
			final Window window = new GUIAppWindow(acciones);

			textGUI.addWindowAndWait(window);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (screen != null) {
				try {
					screen.stopScreen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
	private static class GUIAppWindow extends BasicWindow {
		GUIAppWindow(Map<Integer,Accion> acciones) {
			ArrayList<Window.Hint> hints = new ArrayList<>();
			hints.add(Window.Hint.CENTERED);
			setHints(hints);

			Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

			Iterator it = acciones.keySet().iterator();
			while(it.hasNext()){
				Integer key= (Integer) it.next();
				XButton b = new XButton(new String(key+"."+acciones.get(key).nombreItemMenu()+" "+ acciones.get(key).descripcionItemMenu()));
				b.addListener(new ButtonHandler(acciones.get(key)));
				mainPanel.addComponent(b);
				this.setComponent(mainPanel);
			}
			
			XButton b = new XButton(new String("0.Salir"));
			b.addListener(new ButtonHandler(null));
			mainPanel.addComponent(b);
			this.setComponent(mainPanel);
		}

		private class XButton extends Button {

			public XButton(String label) {
				super(label);
			}

			@Override
			protected ButtonRenderer createDefaultRenderer() {
				return new Button.FlatButtonRenderer();
			}
		}

		private class ButtonHandler implements Button.Listener {

			private Accion accion;

			ButtonHandler(Accion accion) {
				this.accion = accion;
			}

			public void onTriggered(Button button) {
				if(accion!=null) {
					accion.ejecutar();
				}
				else
					dispose();
			}
		}

		public void dispose() {
			this.close();
		}
	}
}
