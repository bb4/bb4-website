/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.application;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.common.app.CommandLineOptions;
import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.util.GUIUtil;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;
import java.util.Arrays;

/**
 * Base class for programs that you want to be
 * run as applications or resizable applets.
 *
 * @author Barry Becker
 */
public abstract class ApplicationApplet extends JApplet{

    protected ResizableAppletPanel resizablePanel_;

    /**
     * Construct the application.
     */
    public ApplicationApplet() {
        this(new String[] {});
    }

    /**
     * Construct the application.
     */
    public ApplicationApplet(String[] args) {
        GUIUtil.setCustomLookAndFeel();

        String localeName = "ENGLISH";
        if (args.length > 0) {
            CommandLineOptions options = new CommandLineOptions(args);

            if (options.contains("help")) {                          // NON-NLS
                System.out.println("Usage: [-locale <locale>]");     // NON-NLS
            }
            if (options.contains("locale")) {
                // then a locale has been specified
                localeName = options.getValueForOption("locale", "ENGLISH");

            }
        }
        initializeContext(localeName);
    }

    /**
     * initialize. Called by the browser.
     */
    @Override
    public void init() {

        if (!AppContext.isInitialized()) {
            String localeName = getParameter("locale");
            if (localeName == null) {
                localeName = "ENGLISH";
            }
            initializeContext(localeName);
        }

        resizablePanel_ = new ResizableAppletPanel(createMainPanel());
        getContentPane().add(resizablePanel_);
    }


    private void initializeContext(String localeName) {
        String appResources = getClass().getPackage().getName() + ".message";  // NON-NLS
        String commonUiResources = "com.barrybecker4.ui.message";   // NON-NLS
        AppContext.initialize(localeName, Arrays.asList(appResources, commonUiResources), new Log());
    }

    /**
     * create and initialize the application
     * (init required for applet)
     */
    protected abstract JPanel createMainPanel();

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    @Override
    public void setSize( int width, int height ) {

        getContentPane().setSize(width, height);
        if (resizablePanel_ != null) {
            resizablePanel_.setSize( width, height );
        }
    }

    @Override
    public String getName() {
        return AppContext.getLabel("APP_TITLE");  // NON-NLS
    }

    /**
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {
        validate();
    }
}

