package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.shuttle.ShuttleEngine;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author UnAfraid
 */
public final class ShuttleData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShuttleData.class);

    private final Map<Integer, org.l2j.gameserver.model.shuttle.ShuttleData> _shuttles = new HashMap<>();
    private final Map<Integer, Shuttle> _shuttleInstances = new HashMap<>();

    private ShuttleData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/ShuttleData.xsd");
    }

    @Override
    public synchronized void load() {
        if (!_shuttleInstances.isEmpty()) {
            for (Shuttle shuttle : _shuttleInstances.values()) {
                shuttle.deleteMe();
            }
            _shuttleInstances.clear();
        }
        parseDatapackFile("data/ShuttleData.xml");
        init();
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _shuttles.size() + " Shuttles.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        StatsSet set;
        Node att;
        org.l2j.gameserver.model.shuttle.ShuttleData data;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("shuttle".equalsIgnoreCase(d.getNodeName())) {
                        attrs = d.getAttributes();
                        set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        data = new org.l2j.gameserver.model.shuttle.ShuttleData(set);
                        for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling()) {
                            if ("doors".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("door".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        data.addDoor(parseInteger(attrs, "id"));
                                    }
                                }
                            } else if ("stops".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("stop".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        final ShuttleStop stop = new ShuttleStop(parseInteger(attrs, "id"));

                                        for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling()) {
                                            if ("dimension".equalsIgnoreCase(z.getNodeName())) {
                                                attrs = z.getAttributes();
                                                stop.addDimension(new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z")));
                                            }
                                        }
                                        data.addStop(stop);
                                    }
                                }
                            } else if ("routes".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("route".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        final List<Location> locs = new ArrayList<>();
                                        for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling()) {
                                            if ("loc".equalsIgnoreCase(z.getNodeName())) {
                                                attrs = z.getAttributes();
                                                locs.add(new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z")));
                                            }
                                        }

                                        final VehiclePathPoint[] route = new VehiclePathPoint[locs.size()];
                                        int i = 0;
                                        for (Location loc : locs) {
                                            route[i++] = new VehiclePathPoint(loc);
                                        }
                                        data.addRoute(route);
                                    }
                                }
                            }
                        }
                        _shuttles.put(data.getId(), data);
                    }
                }
            }
        }
    }

    private void init() {
        for (org.l2j.gameserver.model.shuttle.ShuttleData data : _shuttles.values()) {
            final Shuttle shuttle = new Shuttle(new CreatureTemplate(new StatsSet()));
            shuttle.setData(data);
            shuttle.setHeading(data.getLocation().getHeading());
            shuttle.setLocationInvisible(data.getLocation());
            shuttle.spawnMe();
            shuttle.getStat().setMoveSpeed(300);
            shuttle.getStat().setRotationSpeed(0);
            shuttle.registerEngine(new ShuttleEngine(data, shuttle));
            shuttle.runEngine(1000);
            _shuttleInstances.put(shuttle.getObjectId(), shuttle);
        }
    }

    public Shuttle getShuttle(int id) {
        for (Shuttle shuttle : _shuttleInstances.values()) {
            if ((shuttle.getObjectId() == id) || (shuttle.getId() == id)) {
                return shuttle;
            }
        }

        return null;
    }

    public static ShuttleData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ShuttleData INSTANCE = new ShuttleData();
    }
}
