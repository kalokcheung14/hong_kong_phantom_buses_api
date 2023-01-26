package me.kalok.busgotlost;

import me.kalok.busgotlost.model.Coordinate;
import me.kalok.busgotlost.util.DistanceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
class BusGotLostApplicationTests {
	@Autowired
	DistanceUtil distanceUtil;

	@Test
	void contextLoads() {
	}

	@Test
	void testDistanceCalculation() {
		// Sample coordinates
		Coordinate coordinate1 = new Coordinate(22.370750, 114.119103);
		Coordinate coordinate2 = new Coordinate(22.373429, 114.122922);

		double result = distanceUtil.calculateDistance(coordinate1, coordinate2);
		// Result should be approximately 492.9
		then(String.format("%.1f", result)).isEqualTo("492.9");
	}
}
