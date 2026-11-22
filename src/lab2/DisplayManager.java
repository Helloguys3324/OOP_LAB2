package lab2;

import lab1.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayManager {
    private String name;
    private List<Display> displays;
    private Logger logger;

    public DisplayManager(String name, Logger logger) {
        this.name = name;
        this.displays = new ArrayList<>();
        this.logger = logger;
        this.logger.trace("Manager % created", name);
    }

    public String getName() {
        return name;
    }

    public int getDisplayCount() {
        return displays.size();
    }

    public boolean assignDisplay(Display d) {
        if (displays.contains(d)) {
            logger.warn("% already has % - skipping", name, d.getModel());
            return false;
        }
        displays.add(d);
        logger.trace("Assigned % to %", d.getModel(), name);
        return true;
    }

    public void assist() {
        if (displays.isEmpty()) {
            logger.warn("% has no displays right now", name);
            return;
        }

        if (displays.size() == 1) {
            Display only = displays.get(0);
            logger.warn("% only has one display (%), nothing to compare", name, only.getModel());
            return;
        }

        logger.info("% is comparing % displays:", name, displays.size());
        for (int i = 0; i < displays.size() - 1; i++) {
            Display first = displays.get(i);
            Display second = displays.get(i + 1);
            first.compareWithMonitor(second);
        }
    }

    public Display buyDisplay(Display d) {
        boolean wasRemoved = displays.remove(d);
        if (!wasRemoved) {
            return null;
        }

        logger.info("% sold %", name, d.getModel());
        if (displays.isEmpty()) {
            logger.warn("% has no displays left", name);
        }
        return d;
    }
}
