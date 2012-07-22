/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.progressdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ProgressMonitorDemo extends JFrame {
    public final static int ONE_SECOND = 1000;

    private ProgressMonitor progressMonitor;
    private Timer timer;
    private JButton startButton;
    private LongTask task;
    private JTextArea taskOutput;

    public ProgressMonitorDemo() {
        super("SimpleFrame");
        task = new LongTask(550);

        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(new ButtonListener());

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(startButton, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        //Create a timer.
        timer = new Timer(ONE_SECOND, new TimerListener());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        pack();
        setVisible(true);
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
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
                taskOutput.setCaretPosition(
                    taskOutput.getDocument().getLength());
            }
        }
    }

    /**
     * The actionPerformed method in this class
     * is called when the user presses the start button.
     */
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            progressMonitor = new ProgressMonitor(ProgressMonitorDemo.this,
                                      "Running a Long Task",
                                      "", 0, task.getLengthOfTask());
            progressMonitor.setProgress(0);
            progressMonitor.setMillisToDecideToPopup(2 * ONE_SECOND);

            startButton.setEnabled(false);
            task.go();
            timer.start();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new ProgressMonitorDemo();

    }
}
