package com.becker.game.common.ui;

import com.becker.ui.GUIUtil;
import com.becker.ui.GradientButton;
import com.becker.game.common.GameContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;

/**
 * A dialog to show version information and instructions on how to play the game.
 *
 * @author Barry Becker
 */
final class HelpDialog extends JDialog implements ActionListener
{

    private final JPanel overviewPanel = new JPanel();

    private final JPanel bottomButtonPanel = new JPanel();
    private final GradientButton okButton = new GradientButton();
    private final JLabel logo = new JLabel();

    private final GridLayout gridLayout1 = new GridLayout();

    // these get replaced
    private static String gameName_ = GameContext.getLabel("GAME_TUTORIAL");
    private static String comments_ = GameContext.getLabel("AUTHOR");
    private static final String version_ = GameContext.getLabel("VERSION");
    private static final String copyright_ = GameContext.getLabel("COPYRIGHT");

    private String overviewText_ = null;

    /**
     * Constructor
     * @param parent the parent frame. (used for positioning).
     * @param gameName name of the game (checkers, go,... etc )
     * @param comments supplementary info
     * @param text game specific instructions to display.
     */
    public HelpDialog( Frame parent, String gameName, String comments, String text)
    {
        super( parent );

        gameName_ = gameName;
        comments_ = comments;
        overviewText_ = text;
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logo.setIcon(GUIUtil.getIcon(GameContext.GAME_ROOT+"common/ui/images/help.gif"));
        this.setLocationRelativeTo( parent );
        pack();
    }

    /**
     * builds the ui.
     */
    private void initGUI()
    {
        Object[] arg = {gameName_};
        this.setTitle( MessageFormat.format(GameContext.getLabel("ABOUT"), arg));

        setResizable( false );
        overviewPanel.setLayout( new BorderLayout() );

        FlowLayout flowLayout1 = new FlowLayout();
        bottomButtonPanel.setLayout( flowLayout1 );

        JPanel logoInsetsPanel = new JPanel();
        logoInsetsPanel.setLayout( flowLayout1 );
        logoInsetsPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        gridLayout1.setRows( 4 );
        gridLayout1.setColumns( 1 );

        okButton.setText( GameContext.getLabel("OK") );
        okButton.addActionListener( this );

        JTextArea overviewTextArea = createOverviewTextArea();

        JPanel summaryPanel = createSummaryPanel();

        logo.setForeground(Color.GREEN);

        logoInsetsPanel.add( logo, null );
        summaryPanel.add( logoInsetsPanel, BorderLayout.WEST );
        this.getContentPane().add( overviewPanel, null );

        overviewPanel.add( overviewTextArea, BorderLayout.CENTER );

        bottomButtonPanel.add( okButton, null );
        overviewPanel.add( bottomButtonPanel, BorderLayout.SOUTH );
        overviewPanel.add( summaryPanel, BorderLayout.NORTH );
        //overviewPanel.setMaximumSize(new Dimension(500,800));
    }

    /**
     * @return the body overview text area.
     */
    private JTextArea createOverviewTextArea()
    {

        final JTextArea overviewTextArea = new JTextArea();
        overviewTextArea.setText( overviewText_ );
        overviewTextArea.setBorder(
             BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                                                BorderFactory.createEmptyBorder(5,5,5,5)));
        overviewTextArea.setMaximumSize(new Dimension(500,700));
        overviewTextArea.setPreferredSize(new Dimension(450,300));

        overviewTextArea.setWrapStyleWord(true);
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setAutoscrolls(true);
        overviewTextArea.setEditable(false);
        return overviewTextArea;
    }

    private JPanel createSummaryPanel()
    {
        final JPanel summaryPanel = new JPanel();

        summaryPanel.setLayout( new BorderLayout() );

        final JPanel summaryInsetsPanel = new JPanel();
        final JLabel label1 = new JLabel();
        final JLabel label2 = new JLabel();
        final JLabel label3 = new JLabel();
        final JLabel label4 = new JLabel();
        label1.setText( gameName_ );
        label2.setText( version_ );
        label3.setText( copyright_ );
        label4.setText( comments_ );

        summaryInsetsPanel.add( label1, null );
        summaryInsetsPanel.add( label2, null );
        summaryInsetsPanel.add( label3, null );
        summaryInsetsPanel.add( label4, null );

        summaryInsetsPanel.setLayout( gridLayout1 );
        summaryInsetsPanel.setBorder( new EmptyBorder( 10, 60, 10, 10 ) );

        summaryPanel.add( summaryInsetsPanel, BorderLayout.CENTER );

        return summaryPanel;
    }

    /*
    protected void processWindowEvent( WindowEvent e )
    {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            cancel();
        }
        super.processWindowEvent( e );
    }*/

    private void cancel()
    {
        dispose();
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() == okButton ) {
            cancel();
        }
    }
}
