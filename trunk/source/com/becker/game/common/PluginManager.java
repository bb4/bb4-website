package com.becker.game.common;

import com.becker.ui.*;
import com.becker.xml.*;
import org.w3c.dom.*;

import java.net.*;
import java.util.*;

/**
 * Singleton class for loading and managing the GamePlugins.
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
public class PluginManager {

    private static final String PLUGINS_FILE = GameContext.GAME_ROOT +  "plugins.xml";

    private static PluginManager manager_ = null;
    private List<GamePlugin> plugins_;
    // mapping from the name to the plugin
    private Map<String, GamePlugin> hmNameToPlugin_;
    // mapping from the label to the plugin
    private Map<String, GamePlugin> hmLabelToPlugin_;
    private GamePlugin defaultGame_ = null;

    private PluginManager() {
        initialize();
    }

    public static PluginManager getInstance() {
        if (manager_ == null) {
            manager_ = new PluginManager();
        }
        return manager_;
    }

    public List<GamePlugin> getPlugins() {
        return plugins_;
    }

    public GamePlugin getPlugin(String name) {
        return hmNameToPlugin_.get(name);
    }

    public GamePlugin getPluginFromLabel(String name) {
        return hmLabelToPlugin_.get(name);
    }

    public GamePlugin getDefaultPlugin() {
       return defaultGame_;
    }

    private void initialize()  {
        // load plugin games from plugins.xml
        URL url = GUIUtil.getURL(PLUGINS_FILE);
        Document document = DomUtil.parseXMLFile(url);

        Node root = document.getDocumentElement();    // games element
        NodeList children = root.getChildNodes();
        plugins_ = new ArrayList<GamePlugin>();
        hmNameToPlugin_ = new HashMap<String, GamePlugin>();
        hmLabelToPlugin_ = new HashMap<String, GamePlugin>();

        List<GamePlugin> pluginGames = new ArrayList<GamePlugin>();
        int num = children.getLength();
        for (int i=0; i < num; i++) {

            Node n = children.item(i);
            if (("#comment".equals(n.getNodeName())))
                continue;     // skip comment nodes
            String name = DomUtil.getAttribute(n, "name");
            String msgKey = DomUtil.getAttribute(n, "msgKey");
            String msgBundleBase = DomUtil.getAttribute(n, "msgBundleBase");
            String label = GameContext.getLabel(msgKey);
            int port = Integer.parseInt(DomUtil.getAttribute(n, "serverPort"));
            String panelClass =  DomUtil.getAttribute(n, "panelClass");
            String controllerClass =  DomUtil.getAttribute(n, "controllerClass");
            String def = DomUtil.getAttribute(n, "default", "false");
            boolean isDefault = Boolean.parseBoolean(def);
            GamePlugin plugin = new GamePlugin(name, label, msgBundleBase, port, panelClass, controllerClass, isDefault);
            plugins_.add(plugin);
            hmNameToPlugin_.put(plugin.getName(), plugin);
            hmLabelToPlugin_.put(plugin.getLabel(), plugin);
            if (isDefault) {
                defaultGame_ = plugin;
            }
        }
        if (defaultGame_ == null) {
            defaultGame_ = plugins_.get(0);
        }
        //System.out.println("plugins="+plugins_);
    }
}
