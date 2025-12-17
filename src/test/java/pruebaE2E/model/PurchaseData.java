package pruebaE2E.model;

public class PurchaseData {

    private final String name;
    private final String country;
    private final String city;
    private final String card;
    private final String month;
    private final String year;
    private final String username;
    private final String password;

    public PurchaseData(String name, String country, String city, String card, String month, String year) {
        this(name, country, city, card, month, year, null, null);
    }

    public PurchaseData(String name, String country, String city, String card, String month, String year, String username, String password) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.card = card;
        this.month = month;
        this.year = year;
        this.username = username;
        this.password = password;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getCard() { return card; }
    public String getMonth() { return month; }
    public String getYear() { return year; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
