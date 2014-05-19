package uk.co.gpigc.emitterlauncher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class EmitterShell extends Shell {

	private static final String ICON_PATH = "res/images/nosql.png";
	private static final String STOP_BUTTON_PATH = "res/images/control_stop_blue.png";
	private static final String PLAY_BUTTON_PATH = "res/images/control_play_blue.png";

	private static final String TEST_APP_1_NAME = "TestApp1Emitter.jar";
	private static final String TEST_APP_2_NAME = "TestApp2Emitter.jar";
	private static final String EARTH_APP_NAME = "EarthquakeEmitter.jar";
	private static final String SERVER_APP_NAME = "ResponseTimeEmitter.jar";
	protected static final String TRAFFIC_APP_NAME = "TrafficEmitter.jar";


	private Button serverButton;
	private Button earthButton;
	private Button testApp1Button;
	private Button testApp2Button;
	private static Image playIcon;
	private static Image stopIcon;
	private Image shellIcon;
	private final Map<String, OpenJarThread> threadMap = new HashMap<>();
	private TabFolder tabFolder;
	private TabItem console1Tab;
	private TabItem console2Tab;
	private Text textApp2;
	private Text textApp1;
	private TabItem console3Tab;
	private Text textApp3;
	private TabItem console4Tab;
	private Text textApp4;
	private TabItem console5Tab;
	private Text textApp5;
	private Button trafficButton;
	
	private Button clearButton;
	
	private String emitterDirectory = "./emitters";

	/**
	 * Launch the application.
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			EmitterShell shell = new EmitterShell(display, args);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmitterShell(Display display, String args[]) {
		super(display);
		
		String distDirectory = EmitterShell.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		int indexOfJarName = distDirectory.indexOf("EmitterLauncher.jar");
		distDirectory = distDirectory.substring(5, indexOfJarName);
		
		if (args.length >= 1) {
			emitterDirectory = distDirectory + args[0];
		} else {
			emitterDirectory = distDirectory + emitterDirectory;
		}
		
		this.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				System.exit(0);
			}
		});
		graphicsSetup(display);
		shellSetup(display);
		Group groupComp = groupSetup();
		compositeSetup(groupComp);
		
		File monospacedFontFile = new File(FileUtils.getExpandedFilePath("res/LucidaTypewriterRegular.ttf"));
		getDisplay().loadFont(monospacedFontFile.toString());
		Font monospacedFont = new Font(getDisplay(), "Lucida Sans Typewriter", 10, SWT.NONE);

		tabFolder = new TabFolder(this, SWT.NONE);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		tabFolder.setLayoutData(data);

		console1Tab = new TabItem(tabFolder, SWT.NONE);
		console1Tab.setText("Test App 1");

		textApp1 = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp1.setFont(monospacedFont);
		textApp1.setEditable(false);
		textApp1.setForeground(SWTResourceManager.getColor(47, 79, 79));
		textApp1.setBackground(SWTResourceManager.getColor(255,255,255));
		console1Tab.setControl(textApp1);
		textApp1.addListener(SWT.Modify, new CaretListener(textApp1));

		console2Tab = new TabItem(tabFolder, SWT.NONE);
		console2Tab.setText("Test App 2");

		textApp2 = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp2.setFont(monospacedFont);
		textApp2.setEditable(false);
		textApp2.setForeground(SWTResourceManager.getColor(47, 79, 79));
		console2Tab.setControl(textApp2);
		textApp2.addListener(SWT.Modify, new CaretListener(textApp2));
		textApp2.setBackground(SWTResourceManager.getColor(255,255,255));


		console3Tab = new TabItem(tabFolder, 0);
		console3Tab.setText("Earthquake");

		textApp3 = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp3.setFont(monospacedFont);
		textApp3.setEditable(false);
		textApp3.setForeground(SWTResourceManager.getColor(47, 79, 79));
		console3Tab.setControl(textApp3);
		textApp3.addListener(SWT.Modify, new CaretListener(textApp3));
		textApp3.setBackground(SWTResourceManager.getColor(255,255,255));


		console4Tab = new TabItem(tabFolder, SWT.NONE);
		console4Tab.setText("Response Time");

		textApp4 = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp4.setFont(monospacedFont);
		textApp4.setEditable(false);
		textApp4.setForeground(SWTResourceManager.getColor(47, 79, 79));
		console4Tab.setControl(textApp4);
		textApp4.addListener(SWT.Modify, new CaretListener(textApp4));
		textApp4.setBackground(SWTResourceManager.getColor(255,255,255));


		console5Tab = new TabItem(tabFolder, SWT.NONE);
		console5Tab.setText("Traffic");

		textApp5 = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp5.setFont(monospacedFont);
		textApp5.setEditable(false);
		textApp5.setForeground(SWTResourceManager.getColor(47, 79, 79));
		console5Tab.setControl(textApp5);
		textApp5.addListener(SWT.Modify, new CaretListener(textApp5));
		textApp5.setBackground(SWTResourceManager.getColor(255,255,255));

		clearButton = new Button(this, SWT.PUSH);
		clearButton.setText("Clear Console");
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				clearConsole();
			}
		});

	}

	private void graphicsSetup(Display display) {
		playIcon = new Image(display, FileUtils.getExpandedFilePath(PLAY_BUTTON_PATH));
		stopIcon = new Image(display, FileUtils.getExpandedFilePath(STOP_BUTTON_PATH));
		shellIcon = new Image(display, FileUtils.getExpandedFilePath(ICON_PATH));
	}

	private void compositeSetup(Group groupComp) {
		String os = System.getProperty("os.name").toUpperCase();
		boolean isLinux = os.contains("NUX");
		
		Composite composite = new Composite(groupComp, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData buttonData = new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1);
		buttonData.horizontalAlignment = SWT.FILL;
		buttonData.verticalAlignment = SWT.FILL;
		composite.setLayoutData(buttonData);

		testApp1Button = new Button(composite, SWT.TOGGLE);
		GridData data1 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data1.horizontalAlignment = SWT.FILL;
		data1.verticalAlignment = SWT.FILL;
		testApp1Button.setLayoutData(data1);
		testApp1Button.setImage(playIcon);
		testApp1Button.setText("Test App 1 Emitter");
		testApp1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(emitterDirectory + "/" + TEST_APP_1_NAME, testApp1Button,textApp1);
			}
		});
		testApp1Button.setEnabled(isLinux);

		testApp2Button = new Button(composite, SWT.TOGGLE);
		GridData data2 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data2.horizontalAlignment = SWT.FILL;
		data2.verticalAlignment = SWT.FILL;
		testApp2Button.setLayoutData(data2);
		testApp2Button.setImage(playIcon);
		testApp2Button.setText("Test App 2 Emitter");
		testApp2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(emitterDirectory + "/" + TEST_APP_2_NAME, testApp2Button, textApp2);
			}
		});
		testApp2Button.setEnabled(isLinux);

		earthButton = new Button(composite, SWT.TOGGLE);
		GridData data3 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data3.horizontalAlignment = SWT.FILL;
		data3.verticalAlignment = SWT.FILL;
		earthButton.setLayoutData(data3);
		earthButton.setImage(playIcon);
		earthButton.setText("Earthquake Emitter");
		earthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(emitterDirectory + "/" + EARTH_APP_NAME, earthButton, textApp3);
			}
		});

		serverButton = new Button(composite, SWT.TOGGLE);
		GridData data4 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data4.horizontalAlignment = SWT.FILL;
		data4.verticalAlignment = SWT.FILL;
		serverButton.setLayoutData(data4);
		serverButton.setImage(playIcon);
		serverButton.setText("Response Time Emitter");
		serverButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(emitterDirectory + "/" + SERVER_APP_NAME, serverButton, textApp4);
			}
		});

		trafficButton = new Button(composite, SWT.TOGGLE);
		GridData trafficData = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		trafficData.horizontalAlignment = SWT.FILL;
		trafficData.verticalAlignment = SWT.FILL;
		trafficButton.setLayoutData(trafficData);
		trafficButton.setImage(playIcon);
		trafficButton.setText("Traffic Emitter");
		trafficButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(emitterDirectory + "/" + TRAFFIC_APP_NAME, trafficButton, textApp5);
			}
		});
	}

	protected void buttonSelect(final String jarPath, Button button, Text text) {
		if (button.getSelection()) {
			button.setImage(stopIcon);
			text.append(" >> Starting " + jarPath + "\n");
			threadMap.put(jarPath, new OpenJarThread(jarPath, button,text));
			threadMap.get(jarPath).start();
		} else {
			button.setImage(playIcon);
			if (threadMap.containsKey(jarPath)) {
				threadMap.get(jarPath).stopRunning();
				threadMap.remove(jarPath);
			}
		}
	}

	public static void changeIcon(Button button) {
		if (button.getSelection()) {
			button.setImage(stopIcon);
		} else {
			button.setImage(playIcon);
		}
	}
	
	protected void clearConsole() {
		int selectedTab = tabFolder.getSelectionIndex();
		switch (selectedTab) {
			case 0: textApp1.setText(""); break;
			case 1: textApp2.setText(""); break;
			case 2: textApp3.setText(""); break;
			case 3: textApp4.setText(""); break;
			case 4: textApp5.setText(""); break;
		}
	}

	private Group groupSetup() {
		setLayout(new GridLayout(1, false));
		Group group = new Group(this, SWT.NONE);
		GridData groupData  = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		groupData.horizontalAlignment = SWT.FILL;
		groupData.verticalAlignment = SWT.FILL;
		group.setLayoutData(groupData);

		group.setText("Run");
		group.setLayout(new GridLayout(1, false));

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel
		.setText("\nClick to run the selected emitter, click again to stop. \n ");
		GridData labelData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		labelData.horizontalAlignment = SWT.FILL;
		labelData.verticalAlignment = SWT.FILL;
		lblNewLabel.setLayoutData(labelData);

		Label label = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData sepData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		sepData.horizontalAlignment = SWT.FILL;
		sepData.verticalAlignment = SWT.FILL;
		label.setLayoutData(sepData);
		return group;
	}

	private void shellSetup(Display display) {
		setImage(shellIcon);
		setSize(692, 504);
		setMinimumSize(400, 200);
		setText("GPIG-C: Launcher");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	private class CaretListener implements Listener{
		private Text widg;

		public CaretListener(Text widg){
			this.widg = widg;
		}

		public void handleEvent(Event e){
			widg.setTopIndex(widg.getLineCount() - 1);
		}
	}

}
