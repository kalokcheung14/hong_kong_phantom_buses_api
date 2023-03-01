package me.kalok.busgotlost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stop {
        @JsonProperty("stop")
        String stopId;
        @JsonProperty("name_en")
        String nameEn;
        @JsonProperty("name_tc")
        String nameTc;
        @JsonProperty("lat")
        String latitude;
        @JsonProperty("long")
        String longitude;
        double distance;

        public void setDistance(double distance) {
                this.distance = distance;
        }

        public String getStopId() {
                return stopId;
        }

        public String getNameEn() {
                return nameEn;
        }

        public String getNameTc() {
                return nameTc;
        }

        public String getLatitude() {
                return latitude;
        }

        public String getLongitude() {
                return longitude;
        }

        public double getDistance() {
                return distance;
        }
}
