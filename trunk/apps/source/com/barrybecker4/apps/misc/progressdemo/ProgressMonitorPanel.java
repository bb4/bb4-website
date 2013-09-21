/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.progressdemo;

import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demonstrates proper use of java ProgressMonitor for long running tasks.
 */
public class ProgressMonitorPanel extends JPanel {

    public final static int ONE_SECOND = 1000;
    public final static int TASK_LENGTH = 550;

    private ProgressMonitor progressMonitor;
    private Timer timer;
    private JButton startButton;
    private LongTask task;
    private ScrollingTextArea taskOutput;

    public ProgressMonitorPanel() {
        task = new LongTask(TASK_LENGTH);
        createUI();
    }

    protected void createUI() {

        startButton = new JButton("Start");
        startButton.addActionListener(new ButtonListener());

        taskOutput = new ScrollingTextArea(10, 40);

        setLayout(new BorderLayout());
        add(startButton, BorderLayout.NORTH);
        add(taskOutput, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a timer.
        timer = new Timer(ONE_SECOND, new TimerListener());
    }

    class ButtonListener implements ActionListener {

        /**
         * Called when the user presses the start button.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            progressMonitor =
                    new ProgressMonitor(ProgressMonitorPanel.this,
                               "Running a Long Task",
                               "", 0, task.getLengthOfTask());
            progressMonitor.setProgress(0);
            progressMonitor.setMillisToDecideToPopup(ONE_SECOND);

            startButton.setEnabled(false);
            task.go();
            timer.start();
        }
    }

    class TimerListener implements ActionListener {

        /**
         * Called each time the Timer is triggered (each second).
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            String newline = "\n";
            if (progressMonitor.isCanceled() || task.done()) {
                progressMonitor.close();
                task.stop();
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                if (task.done()) {
                    taskOutput.append("Task completed." + newline);
                }
                startButton.setEnabled(true);
            } else {
                progressMonitor.setNote(task.getMessage());
                progressMonitor.setProgress(task.getCurrent());
                taskOutput.append(task.getMessage() + newline);
            }
        }
    }
}
