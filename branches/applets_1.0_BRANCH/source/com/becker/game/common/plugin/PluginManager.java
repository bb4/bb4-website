package com.becker.game.common.plugin;

import com.becker.common.xml.DomUtil;
import com.becker.game.common.GameContext;
import com.becker.ui.GUIUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class for loading and managing the GamePlugins.
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
public class PluginManager {

    private static final String PLUGINS_FILE = GameContext.GAME_ROOT +  "plugins.xml";

    private static PluginManager manager_ = null;
    private List<GamePlugin> plugins_;
    /** mapping from the name to the plugin */
    private Map<String, GamePlugin> hmNameToPlugin_;
    /** mapping from the label to the plugin */
    private Map<String, GamePlugin> hmLabelToPlugin_;
    private GamePlugin defaultGame_;

    /**
     *  load plugin games from plugins.xml
     */
    private PluginManager() {

        URL url = GUIUtil.getURL(PLUGINS_FILE);
        GameContext.log(1, "about to parse url="+url +"\n plugin file location="+PLUGINS_FILE);
        Document xmlDocument = DomUtil.parseXML(url);

        initializePlugins(xmlDocument);
    }

    /**
     * Get the pligins from the xml document
     * @param document parsed xml from the plugins.xml file.
     */
    private void initializePlugins(Document document) {
        Node root = document.getDocumentElement();    // games element
        NodeList children = root.getChildNodes();
        plugins_ = new ArrayList<GamePlugin>();
        hmNameToPlugin_ = new HashMap<String, GamePlugin>();
        hmLabelToPlugin_ = new HashMap<String, GamePlugin>();
        GamePlugin defaultGame = null;

        int num = children.getLength();
        for (int i = 0; i < num; i++) {

            Node n = children.item(i);

            if (!"#comment".equals(n.getNodeName())) {

                GamePlugin plugin = createPlugin(n);

                plugins_.add(plugin);
                hmNameToPlugin_.put(plugin.getName(), plugin);
                hmLabelToPlugin_.put(plugin.getLabel(), plugin);
                if (plugin.isDefault()) {
                    defaultGame = plugin;
                }
            }
        }
        if (defaultGame == null) {
            defaultGame = plugins_.get(0);
        }
        defaultGame_ = defaultGame;
    }

    /**
     * @param node
     * @return a plugin object that we created from the xml node.
     */
    private GamePlugin createPlugin(Node node) {
        String name = DomUtil.getAttribute(node, "name");
        String msgKey = DomUtil.getAttribute(node, "msgKey");
        String msgBundleBase = DomUtil.getAttribute(node, "msgBundleBase");

        String label = GameContext.getLabel(msgKey);

        String panelClass =  DomUtil.getAttribute(node, "panelClass");
        String controllerClass =  DomUtil.getAttribute(node, "controllerClass");
        String def = DomUtil.getAttribute(node, "default", "false");
        boolean isDefault = Boolean.parseBoolean(def);
        return new GamePlugin(name, label, msgBundleBase, panelClass, controllerClass, isDefault);
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
}
