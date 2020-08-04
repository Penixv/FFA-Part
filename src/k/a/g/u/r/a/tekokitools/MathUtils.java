package k.a.g.u.r.a.tekokitools;

import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MathUtils {

    public static Map<String, Double> fromHighToLow(Map<String, Double> map) {
        final Map<String, Double> sorted = map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sorted;
    }

    public static double Ymax(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getY();
        double blockLoc2 = loc2.getY();
        return Math.max(blockLoc1, blockLoc2);
    }
    public static double Ymin(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getY();
        double blockLoc2 = loc2.getY();
        return Math.min(blockLoc1, blockLoc2);
    }

    public static double Zmin(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getZ();
        double blockLoc2 = loc2.getZ();
        return Math.min(blockLoc1, blockLoc2);
    }

    public static double Zmax(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getZ();
        double blockLoc2 = loc2.getZ();
        return Math.max(blockLoc1, blockLoc2);
    }

    public static double Xmin(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getX();
        double blockLoc2 = loc2.getX();
        return Math.min(blockLoc1, blockLoc2);
    }

    public static double Xmax(Location loc1, Location loc2) {
        double blockLoc1 = loc1.getX();
        double blockLoc2 = loc2.getX();
        return Math.max(blockLoc1, blockLoc2);
    }

}
