package com.becker.game.common;


/**
 * Immutable class representing meta info about a game plugin.
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
public class GamePlugin {

    private String name_;
    private String label_;
    private String msgBundleBase_;
    private String panelClass_;
    private String controllerClass_;
    private boolean isDefault_;

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

    public String toString() {
        StringBuilder bldr = new StringBuilder(name_);
        bldr.append('('+ getLabel());
        bldr.append("panelClass="+panelClass_+' ');
        bldr.append("controllerClass="+controllerClass_+")\n");
        return bldr.toString();
    }

}
