/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.progressdemo;

import com.barrybecker4.ui.application.ApplicationFrame;

import javax.swing.JPanel;

/**
 * Demonstrates proper use of java ProgressMonitor for long running tasks.
 */
public class ProgressMonitorDemo extends ApplicationFrame {


    public ProgressMonitorDemo() {
        super("Progress Monitor Demo");
    }

    @Override
    protected void createUI() {
        JPanel contentPane = new ProgressMonitorPanel();
        setContentPane(contentPane);

        super.createUI();
    }

    public static void main(String[] args) {
        new ProgressMonitorDemo();
    }
}
