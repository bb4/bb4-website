package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.ui.GradientButton;
import com.becker.ui.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.MessageFormat;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
final class OrdersDialog extends OptionsDialog implements ActionListener
{
    private GalacticPlayer player_;
    private Galaxy galaxy_;
    private GradientButton newOrderButton_;

    private OrdersTable ordersTable_;
    private GradientButton okButton_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public OrdersDialog( JFrame parent, GalacticPlayer player, Galaxy galaxy )
    {
        player_ = player;
        galaxy_ = galaxy;
        ordersTable_ = new OrdersTable(player.getOrders());

        parent_ = parent;
        commonInit();

        initUI();
    }

    protected void initUI()
    {
        setResizable( true );

        JPanel buttonsPanel = createButtonsPanel();

        // the table title and add button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(createMarginBorder());

        String[] arg = {player_.getName()};
        String title =  MessageFormat.format(GameContext.getLabel("CURRENT_ORDERS"), (String[])arg);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(player_.getColor().darker());
        titlePanel.add(titleLabel, BorderLayout.WEST);

        newOrderButton_ = new GradientButton(GameContext.getLabel("NEW_ORDER"));
        newOrderButton_.addActionListener(this);
        titlePanel.add(newOrderButton_, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(ordersTable_.getTable());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);


        this.getContentPane().add(mainPanel);
        this.getContentPane().repaint();
        this.pack();
    }

    public String getTitle()
    {
        return GameContext.getLabel("CURRENT_ORDERS_TITLE");
    }

    /**
     *  create the OK Cancel buttons that go at the botton
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    /**
     *
     * @return  the orders in the table
     */
    public List getOrders()
    {
        return ordersTable_.getOrders();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == okButton_) {
            this.setVisible(false);
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
        else if (source == newOrderButton_) {
            addOrder();
        }
    }

    /**
     * add another row to the end of the table.
     */
    private void addOrder()
    {
        // open a dlg to get an order
        OrderDialog orderDialog = new OrderDialog(player_, galaxy_, ordersTable_.getCurrentOutGoingShips());
        orderDialog.setLocationRelativeTo( this );

        boolean canceled = orderDialog.showDialog();
        if ( !canceled ) { // newGame a game with the newly defined options
            //  boardViewer_.startNewGame();
            Order order = orderDialog.getOrder();
            if (order!=null)
                ordersTable_.addRow(order);
        }
    }

}

