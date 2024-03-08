package csv.checker.csvmanager.entity;

import com.google.gson.annotations.SerializedName;

public class HouseSearchResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("searchHouse")
        private SearchHouse[] searchHouses;

        public SearchHouse[] getSearchHouses() {
            return searchHouses;
        }

        public void setSearchHouses(SearchHouse[] searchHouses) {
            this.searchHouses = searchHouses;
        }
    }

    public static class SearchHouse {
        private double score;
        private String addressLabel;
        private Gps gps;
        private Object house;
        private City city;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getAddressLabel() {
            return addressLabel;
        }

        public void setAddressLabel(String addressLabel) {
            this.addressLabel = addressLabel;
        }

        public Gps getGps() {
            return gps;
        }

        public void setGps(Gps gps) {
            this.gps = gps;
        }

        public Object getHouse() {
            return house;
        }

        public void setHouse(Object house) {
            this.house = house;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }
    }

    public static class Gps {
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public static class City {
        private Gps gps;

        public Gps getGps() {
            return gps;
        }

        public void setGps(Gps gps) {
            this.gps = gps;
        }
    }
}