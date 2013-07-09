/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.dialogs;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.ui.components.ImageListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Show an image in a preview dialog.
 * @author Barry Becker
 */
public class ImagePreviewDialog extends AbstractDialog
                                implements ActionListener {

    private BufferedImage image_;

    /** Constructor */
    public ImagePreviewDialog(BufferedImage img) {

        image_ = img;
        this.setResizable(true);
        setTitle(AppContext.getLabel("IMAGE_PREVIEW"));
        this.setModal( true );
        showContent();
    }


    @Override
    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(createImagePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private ImageListPanel createImagePanel() {
        ImageListPanel imagePanel = new ImageListPanel();
        imagePanel.setMaxNumSelections(1);
        imagePanel.setPreferredSize(new Dimension(700, 400));
        imagePanel.setSingleImage(image_);
        return imagePanel;
    }

    /**
     *  create the buttons that go at the bottom ( eg OK, Cancel, ...)
     */
    protected  JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton(cancelButton, AppContext.getLabel("CANCEL"), "Cancel image prview" );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

}
