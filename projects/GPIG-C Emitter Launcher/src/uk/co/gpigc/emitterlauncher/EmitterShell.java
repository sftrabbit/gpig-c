package uk.co.gpigc.emitterlauncher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class EmitterShell extends Shell {

	private static final String ICON_PATH = "/images/nosql.png";
	private static final String STOP_BUTTON_PATH = "/images/control_stop_blue.png";
	private static final String PLAY_BUTTON_PATH = "/images/control_play_blue.png";
	private static final String TEST_APP_1_PATH = "emitterJars/testApp1.jar";
	private static final String TEST_APP_2_PATH = "emitterJars/testApp2.jar";
	private static final String EARTH_APP_PATH = "emitterJars/earthEmitter.jar";
	private static final String SERVER_APP_PATH = "emitterJars/serverMonitor.jar";

	private Button serverButton;
	private Button earthButton;
	private Button testApp1Button;
	private Button testApp2Button;
	private static Image playIcon;
	private static Image stopIcon;
	private Image shellIcon;
	private final Map<String, OpenJarThread> threadMap = new HashMap<>();

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
	}

	private void graphicsSetup(Display display) {
		InputStream playIconInputStream = getClass().getResourceAsStream(PLAY_BUTTON_PATH);
		playIcon = new Image(display, playIconInputStream);
		InputStream stopIconInputStream = getClass().getResourceAsStream(STOP_BUTTON_PATH);
		stopIcon = new Image(display, stopIconInputStream);
		InputStream shellIconInputStream = getClass().getResourceAsStream(ICON_PATH);
		shellIcon = new Image(display, shellIconInputStream);
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
				buttonSelect(TEST_APP_1_PATH, testApp1Button);
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
				buttonSelect(TEST_APP_2_PATH, testApp2Button);
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
				buttonSelect(EARTH_APP_PATH, earthButton);
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
				buttonSelect(SERVER_APP_PATH, serverButton);
			}
		});
	}

	protected void buttonSelect(final String jarPath, Button button) {
		if (button.getSelection()) {
			button.setImage(stopIcon);
			threadMap.put(jarPath, new OpenJarThread(jarPath, button));
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
		Group group = new Group(this, SWT.NONE);
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
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginWidth = 10;
		fillLayout.marginHeight = 10;
		setLayout(fillLayout);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
