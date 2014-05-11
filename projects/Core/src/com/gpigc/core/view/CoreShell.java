package com.gpigc.core.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

public class CoreShell extends Shell {
	private static final String CURRENT_CONFIG_FILE = "Current Config File: ";
	private final Text consoleTextView;
	private final ToolItem startButton;
	private String configFilePath = "none chosen";
	private final Label currentConfigFile;
	private final ToolItem configButton;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			CoreShell shell = new CoreShell(display);
			shell.setSize(700,400);
			shell.setMinimumSize(400, 400);
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

	/**
	 * Create the shell.
	 * @param display
	 */
	public CoreShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		Image icon = new Image(display,"images/nosql.png");
		this.setImage(icon);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		gridLayout.marginBottom = 5;
		setLayout(gridLayout);

		ToolBar toolBar_1 = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));


		startButton = new ToolItem(toolBar_1, SWT.CHECK);
		startButton.setEnabled(false);
		runButton();
		getStartButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(startButton.getSelection()){
					stopButton();
				}else{
					runButton();
				}
			}
		});

		new ToolItem(toolBar_1, SWT.SEPARATOR);

		configButton = new ToolItem(toolBar_1, SWT.NONE);
		configButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(CoreShell.this, SWT.OPEN);
				   dialog.setFilterExtensions(new String [] {"*.config"});
				   String newPath = dialog.open();
				   if(newPath != null){
					   configFilePath = newPath;
					   currentConfigFile.setText(CURRENT_CONFIG_FILE + configFilePath);
					   currentConfigFile.pack();
					   startButton.setEnabled(true);
				   }else{
					   startButton.setEnabled(false);
				   }
			}
		});
		configButton.setText("Change Config File");		

		new Label(this, SWT.NONE);
		Group grpGpigcHumsCore = new Group(this, SWT.BORDER);
		grpGpigcHumsCore.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		grpGpigcHumsCore.setText("GPIG-C: HUMS Core Application");
		grpGpigcHumsCore.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_grpGpigcHumsCore = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		gd_grpGpigcHumsCore.horizontalAlignment = SWT.FILL;
		gd_grpGpigcHumsCore.verticalAlignment = SWT.FILL;
		grpGpigcHumsCore.setLayoutData(gd_grpGpigcHumsCore);

		Label lblNewLabel = new Label(grpGpigcHumsCore, SWT.WRAP);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setText("\n  Welcome to our HUMS Core. "
				+ "\n  Select the .config file you wish to use and then hit run. "
				+ "\n  Press stop when finished."
				+ "\n  Any messages will be displayed in the console below."
				+ "\n ");
		
		currentConfigFile = new Label(this, SWT.NONE);
		currentConfigFile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		currentConfigFile.setText(CURRENT_CONFIG_FILE + configFilePath);


		consoleTextView = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		consoleTextView.setForeground(SWTResourceManager.getColor(47, 79, 79));
		getConsoleTextView().setBackground(SWTResourceManager.getColor(255, 255, 255));
		GridData consoleData = new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1);
		consoleData.horizontalAlignment = SWT.FILL;
		consoleData.verticalAlignment = SWT.FILL;
		getConsoleTextView().setLayoutData(consoleData);
		getConsoleTextView().setEditable(false);
		new Label(this, SWT.NONE);

		createContents();
	}




	protected void runButton() {
		startButton.setText("Run ");
		startButton.setImage(new Image(getDisplay(),"images/control_play_blue.png"));		
	}

	protected void stopButton() {
		startButton.setText("Stop");
		startButton.setImage(new Image(getDisplay(),"images/control_stop_blue.png"));		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("GPIG-C: Core GUI");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Text getConsoleTextView() {
		return consoleTextView;
	}

	public ToolItem getStartButton() {
		return startButton;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public ToolItem getConfigButton() {
		return configButton;
	}

}
