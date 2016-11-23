package goronald.web.id.backpackerid.Object;

/**
 * Created by Zachary on 11/23/2016.
 */

public class City {
    private String cityName,cityDescription, cityBudget, cityLat,cityLong, cityPhoto;

    public City() {

    }

    public City(String cityName, String cityDescription, String cityBudget, String cityLat, String cityLong, String cityPhoto) {
        this.cityName = cityName;
        this.cityDescription = cityDescription;
        this.cityBudget = cityBudget;
        this.cityLat = cityLat;
        this.cityLong = cityLong;
        this.cityPhoto = cityPhoto;
    }

    public String getCityDescription() {
        return cityDescription;
    }

    public void setCityDescription(String cityDescription) {
        this.cityDescription = cityDescription;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityBudget() {
        return cityBudget;
    }

    public void setCityBudget(String cityBudget) {
        this.cityBudget = cityBudget;
    }

    public String getCityLat() {
        return cityLat;
    }

    public void setCityLat(String cityLat) {
        this.cityLat = cityLat;
    }

    public String getCityLong() {
        return cityLong;
    }

    public void setCityLong(String cityLong) {
        this.cityLong = cityLong;
    }

    public String getCityPhoto() {
        return cityPhoto;
    }

    public void setCityPhoto(String cityPhoto) {
        this.cityPhoto = cityPhoto;
    }
}


