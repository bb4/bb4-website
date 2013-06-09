/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.application;

import com.barrybecker4.common.AppContext;
import com.barrybecker4.common.CommandLineOptions;
import com.barrybecker4.common.i18n.LocaleType;
import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.util.GUIUtil;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;

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
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * Construct the application.
     */
    public ApplicationApplet(String[] args) {
        this();

        String localeName = "ENGLISH";
        if (args.length > 0) {
            CommandLineOptions options = new CommandLineOptions(args);

            if (options.contains("help")) {
                System.out.println("Usage: [-locale <locale>]");
            }
            if (options.contains("locale")) {
                // then a locale has been specified
                localeName = options.getValueForOption("locale", "ENGLISH");

            }
        }
        initailizeContext(localeName);
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
            initailizeContext(localeName);
        }

        resizablePanel_ = new ResizableAppletPanel(createMainPanel());
        getContentPane().add(resizablePanel_);
    }


    private void initailizeContext(String localeName) {
        String resourceBaseName = getClass().getPackage().getName() + ".message";
        System.out.println("localeName = "+ localeName);
        System.out.println("resourceBase = "+ resourceBaseName);
        AppContext.initialize(localeName, resourceBaseName, new Log());
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

    /**
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {
        System.out.println("applet start");
        validate();
    }
}

