package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.ui.*;
import com.becker.sound.MusicMaker;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Iterator;

/**
 * Play a battle sequence that is stored in a GalacticTurn.
 *
 * @author Barry Becker
 */
final class BattleDialog extends OptionsDialog
                         implements ActionListener

{
    // smaller number means faster battle sequence
    private static final int BATTLE_SPEED = 2000;
    private static final int WIDTH = 250;

    private final JPanel mainPanel_ = new JPanel();
    private final JEditorPane descriptionLabel_ = new JEditorPane();
    private final BattleCanvas canvas_ = new BattleCanvas();

    private final GradientButton startButton_ = new GradientButton();
    private final GradientButton closeButton_ = new GradientButton();
    private JPanel buttonsPanel_;


    private final JLabel infoLabel_ = new JLabel();
    //private boolean paused_ = false;
    //private float scale_ = 1.0f;

    private BattleSimulation battle_;
    private GalaxyViewer viewer_;


    /**
     * constructor - create the Battle dialog.
     * @param parent frame to display relative to
     * @param battle the simulation
     * @param viewer send in the viewer so we can give feedbak about the battle while it is occurring
     */
    public BattleDialog( Frame parent, BattleSimulation battle, GalaxyViewer viewer )
    {
        super( parent );
        this.setResizable(false);
        //if (!GUIUtil.isStandAlone())
        //    this.setAlwaysOnTop(true);   // causes access control exception in applet
        battle_ = battle;
        viewer_ = viewer;
        initUI();
        this.setModal(true);
    }


    public String getTitle()
    {
        return "Battle Sequence";
    }

    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        mainPanel_.setLayout( new BorderLayout() );


        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        infoLabel_.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        infoLabel_.setVerticalAlignment(JLabel.TOP);
        infoLabel_.setPreferredSize( new Dimension( WIDTH, 260 ) );
        infoLabel_.setBackground(new Color(180, 100, 255));

        viewerPanel.add( infoLabel_, BorderLayout.SOUTH);

        JPanel buttonsPanel = createButtonsPanel();

        Planet defendingPlanet =  battle_.getPlanet();
        String text = "There is a battle at "+defendingPlanet.getName()+".\n";

        descriptionLabel_.setEditable(false);
        //descriptionLabel_.setLineWrap(true);
        descriptionLabel_.setContentType("text/html");
        descriptionLabel_.setText(text);

        canvas_.setPreferredSize(new Dimension(WIDTH, 100));
        JPanel canvasPanel = new JPanel();
        canvasPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),
                              BorderFactory.createLineBorder(Color.black, 1)));
        canvasPanel.add(canvas_);

        mainPanel_.add(descriptionLabel_, BorderLayout.NORTH);
        mainPanel_.add(canvasPanel, BorderLayout.CENTER);
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );
        getContentPane().add( mainPanel_ );

        viewer_.showPlanetUnderAttack(battle_.getPlanet(), true);

        int numAttackShips = battle_.getOrder().getFleetSize();
        int numDefendShips = battle_.getPlanet().getNumShips();
        this.refresh(numAttackShips, numDefendShips);
        pack();
    }


    protected JPanel createButtonsPanel()
    {
        buttonsPanel_ = new JPanel( new BorderLayout());
        buttonsPanel_.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        initBottomButton( startButton_, "Fight!", "Begin the battle sequence");
        initBottomButton( closeButton_, GameContext.getLabel("CLOSE"), "Close dialog" );
        //initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel_.add( startButton_, BorderLayout.CENTER);
        buttonsPanel_.add( closeButton_, BorderLayout.EAST );
        closeButton_.setEnabled(false);

        return buttonsPanel_;
    }


    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource();
        if (source == closeButton_) {
            this.setVisible(false);
        }
        else if (source == startButton_) {

            //buttonsPanel_.remove(startButton_);
            //buttonsPanel_.add(closeButton_, BorderLayout.CENTER);
            startButton_.setEnabled(false);
            closeButton_.setEnabled(true);
            this.invalidate();
            this.paint(this.getGraphics());


            //SwingUtilities.invokeLater(doAnimation);
            Thread battle =  new Thread(canvas_);
            SwingUtilities.invokeLater(battle);

            //doAnimation();
        }
    }


    /**
     * refresh the game tree.
     */
    protected void refresh(int attackers, int defenders)
    {
        canvas_.setFleetSizes(attackers, defenders);
    }




    /**
     * Canvas for showing the animation ----------------------------------
     */
    private class BattleCanvas extends Canvas implements Runnable
    {
        int attackers_;
        int defenders_;

        public BattleCanvas()
        {
            //this.setDoubleBuffered(false);
        }

        public void setFleetSizes(int attackers, int defenders)
        {
            attackers_ = attackers;
            defenders_ = defenders;
            this.paint(this.getGraphics());
        }

        public void run()
        {
             Planet destPlanet = battle_.getPlanet();
             int numAttackShips = battle_.getOrder().getFleetSize();
             int numDefendShips = destPlanet.getNumShips();
             //String defender = (destPlanet.getOwner()==null)? "Neutral" : destPlanet.getOwner().getName();

             // play back the move sequence
             List sequence = battle_.getHitSequence();
             if (sequence.isEmpty()) {
                 // reinforced!
                 GameContext.getMusicMaker().playNote( MusicMaker.APPLAUSE, 45, 0, 200, 1000 );
                 GameContext.getMusicMaker().playNote(70, 50, 900);
                 GameContext.getMusicMaker().playNote(90, 40, 1000);
                 descriptionLabel_.setText("Planet "+destPlanet.getName()+" has been reinforced.");
             }
             else {
                 Iterator it = sequence.iterator();
                 if (GameContext.getUseSound())
                     GameContext.getMusicMaker().playNote( MusicMaker.GUNSHOT, 45, 0, 200, 1000 );

                 while (it.hasNext()) {
                     GalacticPlayer p = (GalacticPlayer)it.next();
                     int total = numAttackShips + numDefendShips;
                     int time = 1 + BATTLE_SPEED / (1+total);
                     if (p == battle_.getOrder().getOwner()) {
                         if (GameContext.getUseSound())
                             GameContext.getMusicMaker().playNote(100, time, 800);
                         numAttackShips--;
                     }
                     else {
                         if (GameContext.getUseSound())
                             GameContext.getMusicMaker().playNote(80, time, 800);
                         numDefendShips--;
                     }

                     refresh(numAttackShips, numDefendShips);

                     try {
                         Thread.sleep(time);
                     } catch (InterruptedException e) { e.printStackTrace(); }
                 }
                 assert(numAttackShips == 0 || numDefendShips == 0):
                         "numAttackShips="+numAttackShips+" numDefendShips="+numDefendShips;
                 String winMessage;
                 if (numAttackShips==0)
                     winMessage = "Planet "+destPlanet.getName()+" has successfully defended itself.";
                 else
                     winMessage = battle_.getOrder().getOwner().getName()+ " has conquered planet "+destPlanet.getName();

                 descriptionLabel_.setText( "<html>"+ descriptionLabel_.getText()+ "<b>"+ winMessage +"/b></html>");
             }

             viewer_.showPlanetUnderAttack(battle_.getPlanet(), false);  // battle is done
             //closeButton_.setEnabled(true);

             canvas_.repaint();

         }


        public synchronized void paint(Graphics g) {

            if (g == null)
                return;

            Graphics2D g2 = (Graphics2D)canvas_.getGraphics();

            // background
            g2.setColor( Color.white );
            g2.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            Color attackerColor =  battle_.getOrder().getOwner().getColor();
            g2.setColor(attackerColor);
            g2.fillRect(10, 25, attackers_, 20);

            g2.setColor(attackerColor.darker());
            String attackerString = "Attacker :"+battle_.getOrder().getOwner().getName();
            g2.drawString(attackerString, 10, 20);
            g2.drawString(Integer.toString(attackers_), attackers_+15, 42);
            if (attackers_ > 0)  {
                g2.drawRect(10,25, attackers_, 20);
            }


            Color defenderColor;
            if (battle_.getPlanet().getOwner() == null)
                defenderColor = Planet.NEUTRAL_COLOR;
            else
                defenderColor = battle_.getPlanet().getOwner().getColor();

            g2.setColor(defenderColor);
            g2.fillRect(10,75, defenders_, 20);

            g2.setColor(defenderColor.darker());
            String defenderString = "Defender :"+ battle_.getPlanet().getName();
            g2.drawString(defenderString, 10, 70);
            g2.drawString(Integer.toString(defenders_), defenders_+15, 90);
            if (defenders_ > 0) {
                g2.drawRect(10,75, defenders_, 20);
            }
        }
    }

}

