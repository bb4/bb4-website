package com.becker.game.common;


/**
 * Immutable class representing meta info about a game plugin.
 * There is a game plugin for each game that we support.
 * @see plugins.xml
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
public class GamePlugin {

    private final String name_;
    private final String label_;
    private final String msgBundleBase_;
    private final String panelClass_;
    private final String controllerClass_;
    private final boolean isDefault_;

    public GamePlugin(String name, String label, String msgBundleBase,
                      String panelClass, String controllerClass,
                      boolean isDefault) {
        name_ = name;
        label_ = label;
        msgBundleBase_ = msgBundleBase;
        panelClass_ = panelClass;
        controllerClass_ = controllerClass;
        isDefault_ = isDefault;
    }


    public String getName() {
        return name_;
    }

    public String getLabel() {
        return label_;
    }

    public String getMsgBundleBase() {
        return msgBundleBase_;
    }

    public String getMsgKey() {
        return label_;
    }

    public String getPanelClass() {
        //return Util.loadClass(panelClass_);
        return panelClass_;
    }

    public String getControllerClass() {
        //return Util.loadClass(controllerClass_);
        return controllerClass_;
    }

    public boolean isDefault() {
        return isDefault_;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(name_);
        bldr.append('('+ getLabel());
        bldr.append("panelClass="+panelClass_+' ');
        bldr.append("controllerClass="+controllerClass_+")\n");
        return bldr.toString();
    }

}
