package me.kalok.busgotlost.util;

import me.kalok.busgotlost.model.Coordinate;
import org.springframework.stereotype.Component;

@Component
public class DistanceUtilImpl implements DistanceUtil {

    // Haversine formula
    @Override
    public double calculateDistance(Coordinate coordinate1, Coordinate coordinate2) {
        double lat1 = coordinate1.latitude();
        double lat2 = coordinate2.latitude();
        double lon1 = coordinate1.longitude();
        double lon2 = coordinate2.longitude();
        double phi1 = degreeToRadian(lat1);
        double phi2 = degreeToRadian(lat2);
        double deltaPhi = degreeToRadian(lat2 - lat1);
        double deltaLambda = degreeToRadian(lon2 - lon1);

        double a = Math.pow(Math.sin(deltaPhi / 2), 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.pow(Math.sin(deltaLambda/2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        // 6371e3 = Radius of Earth in metre
        return  6371e3 * c;
    }

    private double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }
}
