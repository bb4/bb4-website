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
 * Play a battle sequence that is stored in a galactic move.
 *
 * @author Barry Becker
 */
final class BattleDialog extends OptionsDialog
                         implements ActionListener

{
    private final JPanel mainPanel_ = new JPanel();
    private final JTextArea descriptionLabel_ = new JTextArea();
    private final BattleCanvas canvas_ = new BattleCanvas();


    private final GradientButton pauseButton_ = new GradientButton();
    private final GradientButton closeButton_ = new GradientButton();

    private final JLabel infoLabel_ = new JLabel();

    private boolean paused_ = false;

    BattleSimulation battle_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param battle the simulation
     */
    public BattleDialog( JFrame parent, BattleSimulation battle )
    {
        super( parent );
        battle_ = battle;

        initUI();
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
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        infoLabel_.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5,5,5,5)));
        infoLabel_.setVerticalAlignment(JLabel.TOP);
        infoLabel_.setPreferredSize( new Dimension( 200, 260 ) );

        viewerPanel.add( infoLabel_, BorderLayout.SOUTH);

        JPanel buttonsPanel = createButtonsPanel();

        Planet defendingPlanet =  battle_.getPlanet();
        String text = "There is a battle at "+defendingPlanet.getName()+".\n"
                    +"Attacker "+battle_.getOrder().getOwner().getName()+" has "
                    +battle_.getOrder().getFleetSize()+" ships.\n Defender "+defendingPlanet.getName()
                    +" has "+defendingPlanet.getNumShips()+" ships.\n";
        descriptionLabel_.setEditable(false);
        descriptionLabel_.setLineWrap(true);
        descriptionLabel_.setText(text);

        canvas_.setPreferredSize(new Dimension(200, 100));

        mainPanel_.add(descriptionLabel_, BorderLayout.NORTH);
        mainPanel_.add(canvas_, BorderLayout.CENTER);
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel_ );
        getContentPane().repaint();
        pack();
        this.paint(this.getGraphics());
        repaint();

        // do the animation in a separate thread
        Runnable doAnimation = new Runnable() {
            public void run() {
                doBattleAnimation();
            }
        };
        SwingUtilities.invokeLater(doAnimation);

    }

    private void doBattleAnimation()
    {
        Planet destPlanet = battle_.getPlanet();
        int numAttackShips = battle_.getOrder().getFleetSize();
        int numDefendShips = destPlanet.getNumShips();
        String defender = (destPlanet.getOwner()==null)? "Neutral" : destPlanet.getOwner().getName();

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
            GameContext.getMusicMaker().playNote( MusicMaker.GUNSHOT, 45, 0, 200, 1000 );

            while (it.hasNext()) {
                GalacticPlayer p = (GalacticPlayer)it.next();
                int total = numAttackShips + numDefendShips;
                int time = 1+ 3000 / (2+total);
                if (p == battle_.getOrder().getOwner()) {
                    GameContext.getMusicMaker().playNote(100, time, 800);
                    numAttackShips--;
                }
                else {
                    GameContext.getMusicMaker().playNote(80, time, 800);
                    numDefendShips--;
                }
                refresh(numAttackShips, numDefendShips);
            }
            assert(numAttackShips == 0 || numDefendShips == 0):
                    "numAttackShips="+numAttackShips+" numDefendShips="+numDefendShips;
            String winMessage;
            if (numAttackShips==0)
                winMessage = " Planet "+destPlanet.getName()+" has successfully defended itself.";
            else
                winMessage = battle_.getOrder().getOwner().getName()+ " has conquered planet "+destPlanet.getName();

            descriptionLabel_.setText( descriptionLabel_.getText()+ winMessage);
        }

    }

    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( pauseButton_, "Pause", "Pause the Battle" );
        //initBottomButton( stepButton_, "Step", "Step forward through the battle sequence");
        //initBottomButton( continueButton_, "Continue", "Resume animation of the battle sequence");
        initBottomButton( closeButton_, GameContext.getLabel("CLOSE"), "Close dialog, but battle is still recorded" );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( pauseButton_ );
        buttonsPanel.add( closeButton_ );

        return buttonsPanel;
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
        else if (source == pauseButton_) {
            // toggle the paused state
            paused_ = !paused_;
        }
    }


    /**
     * refresh the game tree.
     */
    protected void refresh(int attackers, int defenders)
    {
        canvas_.setFleetSizes(attackers, defenders);
    }



    private class BattleCanvas extends JPanel
    {
        int attackers_;
        int defenders_;

        public BattleCanvas()
        {
            this.setDoubleBuffered(false);
        }

        public void setFleetSizes(int attackers, int defenders)
        {
            attackers_ = attackers;
            defenders_ = defenders;
            this.paint(this.getGraphics());
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D)canvas_.getGraphics();

            // background
            g2.setColor( Color.white );
            g2.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            g2.setColor(battle_.getOrder().getOwner().getColor());
            g2.fillRect(10,10, attackers_, 20);

            g2.setColor(battle_.getOrder().getOwner().getColor().darker());
            g2.drawRect(10,10, attackers_, 20);

            Color c;
            if (battle_.getPlanet().getOwner() == null)
                c = Color.LIGHT_GRAY;
            else
                c = battle_.getPlanet().getOwner().getColor();
            g2.setColor(c);
            g2.fillRect(10,35, defenders_, 20);

            g2.setColor(c.darker());
            g2.drawRect(10,35, defenders_, 20);
        }
    }

}

