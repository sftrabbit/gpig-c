package uk.co.gpigc.emitterlauncher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.StyledText;

public class EmitterShell extends Shell {

	private static final String ICON_PATH = "res/images/nosql.png";
	private static final String STOP_BUTTON_PATH = "res/images/control_stop_blue.png";
	private static final String PLAY_BUTTON_PATH = "res/images/control_play_blue.png";

	//private static final String TEST_APP_1_PATH = "../TestApp1Emitter/bin/TestApp1Emitter.jar";
	private static final String TEST_APP_1_PATH = "res/TestApp1Emitter.jar";

	//private static final String TEST_APP_2_PATH = "../TestApp2Emitter/bin/TestApp2Emitter.jar";
	private static final String TEST_APP_2_PATH = "res/TestApp2Emitter.jar";

	//private static final String EARTH_APP_PATH = "../EarthEmitter/bin/EarthEmitter.jar";
	private static final String EARTH_APP_PATH = "res/EarthMonitor.jar";

	//private static final String SERVER_APP_PATH = "../ServerEmitter/bin/ServerEmitter.jar";
	private static final String SERVER_APP_PATH = "res/ServerEmitter.jar";


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
	private StyledText textApp2;
	private StyledText textApp1;
	private TabItem console3Tab;
	private StyledText textApp3;
	private TabItem console4Tab;
	private StyledText textApp4;

	/**
	 * Launch the application.
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			EmitterShell shell = new EmitterShell(display);
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

	public EmitterShell(Display display) {
		super(display);
		graphicsSetup(display);
		shellSetup(display);
		Group groupComp = groupSetup();
		compositeSetup(groupComp);

		tabFolder = new TabFolder(this, SWT.NONE);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		tabFolder.setLayoutData(data);

		console1Tab = new TabItem(tabFolder, SWT.NONE);
		console1Tab.setText("Console 1 ");

		textApp1 = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp1.setEditable(false);
		console1Tab.setControl(textApp1);

		console2Tab = new TabItem(tabFolder, SWT.NONE);
		console2Tab.setText("Console 2");

		textApp2 = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp2.setEditable(false);
		console2Tab.setControl(textApp2);
		
		console3Tab = new TabItem(tabFolder, 0);
		console3Tab.setText("Console 3");
		
		textApp3 = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp3.setEditable(false);
		console3Tab.setControl(textApp3);
		
		console4Tab = new TabItem(tabFolder, SWT.NONE);
		console4Tab.setText("Console 4");
		
		textApp4 = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textApp4.setEditable(false);
		console4Tab.setControl(textApp4);
	}

	private void graphicsSetup(Display display) {
		playIcon = new Image(display, PLAY_BUTTON_PATH);
		stopIcon = new Image(display, STOP_BUTTON_PATH);
		shellIcon = new Image(display, ICON_PATH);
	}

	private void compositeSetup(Group groupComp) {
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
		testApp1Button.setText("1) First Test App");
		testApp1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//String appPath = getClass().getResource(TEST_APP_1_PATH).toExternalForm();
				//System.out.println("This is Rosy's appPath: " + appPath);
				buttonSelect(TEST_APP_1_PATH, testApp1Button,textApp1);
			}
		});

		testApp2Button = new Button(composite, SWT.TOGGLE);
		GridData data2 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data2.horizontalAlignment = SWT.FILL;
		data2.verticalAlignment = SWT.FILL;
		testApp2Button.setLayoutData(data2);
		testApp2Button.setImage(playIcon);
		testApp2Button.setText("2) Second Test App");
		testApp2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(TEST_APP_2_PATH, testApp2Button, textApp2);
			}
		});

		earthButton = new Button(composite, SWT.TOGGLE);
		GridData data3 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data3.horizontalAlignment = SWT.FILL;
		data3.verticalAlignment = SWT.FILL;
		earthButton.setLayoutData(data3);
		earthButton.setImage(playIcon);
		earthButton.setText("3) Earthquake Monitor");
		earthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(EARTH_APP_PATH, earthButton, textApp3);
			}
		});

		serverButton = new Button(composite, SWT.TOGGLE);
		GridData data4 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		data4.horizontalAlignment = SWT.FILL;
		data4.verticalAlignment = SWT.FILL;
		serverButton.setLayoutData(data4);
		serverButton.setImage(playIcon);
		serverButton.setText("4) Server Monitor");
		serverButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				buttonSelect(SERVER_APP_PATH, serverButton, textApp4);
			}
		});
	}

	protected void buttonSelect(final String jarPath, Button button, StyledText console) {
		if (button.getSelection()) {
			button.setImage(stopIcon);
			threadMap.put(jarPath, new OpenJarThread(jarPath, button,console));
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
		setSize(400, 300);
		setMinimumSize(400, 200);
		setText("GPIG-C: Launcher");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
