package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;
import com.becker.common.EnumeratedType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class GameOptionsDialog extends OptionsDialog implements ActionListener, ItemListener
{

    /**
     * the options get set directly on the game controller that is passed in.
     */
    protected final GameController controller_;

    protected JPanel controllerParamPanel_ = null;
    protected JPanel debugParamPanel_ = null;
    protected JPanel lookAndFeelParamPanel_ = null;
    protected JPanel localePanel_ = null;

    // debug params
    protected JTextField dbgLevelField_ = null;
    protected final JTextField logFileField_ = null;
    protected JRadioButton consoleOutputButton_ = null;  // output radio button group
    protected JRadioButton windowOutputButton_ = null;  // output radio button group
    protected JRadioButton fileOutputButton_ = null;  // output radio button group
    protected int logDestination_;
    protected final GradientButton logFileButton_ = null;
    protected JCheckBox profileCheckbox_ = null;

    // look and feel params
    protected JCheckBox soundCheckbox_ = null;
    protected JCheckBox imagesCheckbox_ = null;
    protected JButton boardColorButton_ = null;
    protected JButton gridColorButton_ = null;

    protected JComboBox localeComboBox_ = null;

    protected GradientButton okButton_ = new GradientButton();

    // constructor
    public GameOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent);
        controller_ = controller;
        initUI();
    }

    protected void initUI()
    {
        setResizable( true );
        mainPanel_.setLayout( new BorderLayout() );
        // contains tabs for Algorithm, Debugging, and Look and Feel
        JTabbedPane tabbedPanel = new JTabbedPane();

        controllerParamPanel_ = createControllerParamPanel();
        debugParamPanel_ = createDebugParamPanel();
        lookAndFeelParamPanel_ = createLookAndFeelParamPanel();
        localePanel_ = createLocalePanel();

        JPanel buttonsPanel = createButtonsPanel();

        if (controllerParamPanel_!=null)
            tabbedPanel.add( controllerParamPanel_.getName(), controllerParamPanel_ );
        tabbedPanel.add( GameContext.getLabel("DEBUG"), debugParamPanel_ );
        tabbedPanel.add( GameContext.getLabel("LOOK_AND_FEEL"), lookAndFeelParamPanel_ );
        tabbedPanel.add( GameContext.getLabel("LOCALE"), localePanel_ );


        mainPanel_.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.getContentPane().repaint();
        this.pack();
    }

    public String getTitle()
    {
        return GameContext.getLabel("GAME_OPTIONS");
    }

    // create the OK Cancel buttons that go at the botton
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("USE_OPTIONS") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("RESUME") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    /**
     * @return general game options tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        return null;
    }

    /**
     * @return debug params tab panel
     */
    protected JPanel createDebugParamPanel()
    {
        JPanel p = createDebugOptionsPanel();

        addDebugLevel(p);
        addLoggerSection(p);
        addProfileCheckBox(p);

        return p;
    }

    protected JPanel createDebugOptionsPanel()
    {
        JPanel p =  new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GameContext.getLabel("DEBUG_OPTIONS") ) );

        JLabel label = new JLabel( "     " );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );
        return p;
    }

    protected void addDebugLevel(JPanel p)
    {
        // debug level
        dbgLevelField_ = new JTextField( Integer.toString( GameContext.getDebugMode() ) );
        dbgLevelField_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        //dbgLevelField_.addActionListener(this);
        JPanel p1 =
                new NumberInputPanel( GameContext.getLabel("DEBUG_LEVEL"), dbgLevelField_,
                                      GameContext.getLabel("DEBUG_LEVEL_TIP") );
        p.add( p1 );
    }

    /**
     * add a section for loggin options to panel p.
     * @param p  the panel to add to
     */
    protected void addLoggerSection(JPanel p)
    {
        // radio buttons for where to send the log info
        JLabel logLabel = new JLabel( GameContext.getLabel("SEND_LOG_OUTPUT") );
        logLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( logLabel );

        ButtonGroup buttonGroup = new ButtonGroup();
        consoleOutputButton_ = new JRadioButton( GameContext.getLabel("CONSOLE") );
        windowOutputButton_ = new JRadioButton( GameContext.getLabel("SEPARATE_WINDOW"));
        fileOutputButton_ = new JRadioButton( GameContext.getLabel("THIS_FILE") );

        p.add( createRadioButtonPanel( consoleOutputButton_, buttonGroup, true ) );
        p.add( createRadioButtonPanel( windowOutputButton_, buttonGroup, false ) );
        p.add( createRadioButtonPanel( fileOutputButton_, buttonGroup, false ) );
        logDestination_ = GameContext.getLogger().getDestination();
        switch (logDestination_) {
            case Log.LOG_TO_CONSOLE:
                consoleOutputButton_.setSelected( true );
                break;
            case Log.LOG_TO_WINDOW:
                windowOutputButton_.setSelected( true );
                break;
            case Log.LOG_TO_FILE:
                fileOutputButton_.setSelected( true );
                break;
        }
    }

    protected void addProfileCheckBox(JPanel p)
    {
        // show profile info option
        profileCheckbox_ = new JCheckBox( GameContext.getLabel("SHOW_PROFILE_STATS"), GameContext.isProfiling() );
        profileCheckbox_.setToolTipText( GameContext.getLabel("SHOW_PROFILE_STATS_TIP") );
        profileCheckbox_.addActionListener( this );
        profileCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( profileCheckbox_ );
    }


    protected JPanel createRadioButtonPanel( JRadioButton radioButton, ButtonGroup buttonGroup, boolean selected )
    {
        JPanel panelEntry = new JPanel( new BorderLayout() );
        panelEntry.setAlignmentX( Component.LEFT_ALIGNMENT );

        radioButton.setSelected( selected );
        radioButton.addItemListener( this );
        buttonGroup.add( radioButton );

        JLabel l = new JLabel( "    " );
        l.setBackground( new Color( 255, 255, 255, 0 ) );
        panelEntry.add( l, BorderLayout.WEST );  // indent it

        panelEntry.add( radioButton );

        return panelEntry;
    }

    /**
     * @return look & feel params tab panel
     */
    protected JPanel createLookAndFeelParamPanel()
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GameContext.getLabel("DEBUG_OPTIONS") ) );

        JLabel label = new JLabel( "     " );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        // sound option
        soundCheckbox_ = new JCheckBox( GameContext.getLabel("USE_SOUND"), GameContext.getUseSound() );
        soundCheckbox_.setToolTipText( GameContext.getLabel("USE_SOUND_TIP") );
        soundCheckbox_.addActionListener( this );
        soundCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( soundCheckbox_ );

        // use images
        imagesCheckbox_ = new JCheckBox( GameContext.getLabel("USE_IMAGES"), GameContext.getDebugMode() > 1 );
        imagesCheckbox_.setToolTipText( GameContext.getLabel("USE_IMAGES_TIP") );
        imagesCheckbox_.setEnabled( false );  // not yet implemented
        imagesCheckbox_.addActionListener( this );
        imagesCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( imagesCheckbox_ );

        //call super methods to add color select entries
        boardColorButton_ = new JButton("...");
        GameBoardViewer v = ((GameBoardViewer)controller_.getViewer());
        boardColorButton_.setBackground(v.getBackground());
        gridColorButton_ = new JButton("...");
        gridColorButton_.setBackground(v.getGridColor());
        JPanel boardColorPanel = new ColorInputPanel(GameContext.getLabel("SELECT_BOARD_COLOR"),
                                                     GameContext.getLabel("SELECT_BOARD_COLOR_TIP"),
                                                     boardColorButton_);
        JPanel gridColorPanel = new ColorInputPanel(GameContext.getLabel("SELECT_GRID_COLOR"),
                                                    GameContext.getLabel("SELECT_GRID_COLOR_TIP"),
                                                    gridColorButton_);
        p.add( boardColorPanel );
        p.add( gridColorPanel );

        return p;
    }

    /**
     *  This panel allows the user to set the desired locale through the ui.
     * @return locale tab panel.
     */
     protected JPanel createLocalePanel()
     {
         JPanel p = new JPanel();
         p.setLayout( new BorderLayout() );
         p.setAlignmentX( Component.LEFT_ALIGNMENT );
         p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GameContext.getLabel("LOCALE_OPTIONS") ) );

         //JLabel label = new JLabel( "     " );
         //label.setAlignmentX( Component.LEFT_ALIGNMENT );
         //p.add( label );

         localeComboBox_ = new JComboBox(); //  GameContext.getLabel("LOCALE")); //, GameContext.getDefaultLocaleName() );
         localeComboBox_.setToolTipText( GameContext.getLabel("LOCALE_TIP") );

         // add the available locales to the dropdown
         EnumeratedType locales = LocaleType.getAvailableLocales();
         for (int i=0; i<locales.getNames().length; i++) {
             String item = GameContext.getLabel(locales.getValue(i).getName());
             localeComboBox_.addItem(item);
         }
         localeComboBox_.setSelectedItem(GameContext.getDefaultLocaleName());
         localeComboBox_.addActionListener( this );
         localeComboBox_.setAlignmentX( Component.LEFT_ALIGNMENT );
         p.add( localeComboBox_,  BorderLayout.NORTH );

         JPanel filler = new JPanel();
         p.add(filler, BorderLayout.CENTER);
         return p;
     }


    /**
     * ok button pressed.
     */
    protected void ok()
    {
        Integer dbgLevel = new Integer( dbgLevelField_.getText() );
        if ( dbgLevel.intValue() >= 0 )
            GameContext.setDebugMode( dbgLevel.intValue() );
        GameContext.setProfiling( profileCheckbox_.isSelected() );
        GameContext.getLogger().setDestination( logDestination_ );

        GameBoardViewer v = ((GameBoardViewer)controller_.getViewer());
        v.setBackground( boardColorButton_.getBackground() );
        v.setGridColor( gridColorButton_.getBackground() );

        GameContext.setUseSound( soundCheckbox_.isSelected() );

        EnumeratedType locales = LocaleType.getAvailableLocales();
        GameContext.setLocale((LocaleType)locales.getValue(localeComboBox_.getSelectedIndex()));
        this.setVisible( false );
    }

    /**
     * called when a button has been pressed
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
        else if (source == localeComboBox_) {
            GameContext.log(0, "locale="+localeComboBox_.getSelectedItem());
        }
    }


    /**
     * Invoked when a radio button has changed its selection state.
     */
    public void itemStateChanged( ItemEvent e )
    {
        if ( consoleOutputButton_ != null && consoleOutputButton_.isSelected() )
            logDestination_ = Log.LOG_TO_CONSOLE;
        else if ( windowOutputButton_ != null && windowOutputButton_.isSelected() )
            logDestination_ = Log.LOG_TO_WINDOW;
        else if ( fileOutputButton_ != null && fileOutputButton_.isSelected() )
            logDestination_ = Log.LOG_TO_FILE;
    }


}