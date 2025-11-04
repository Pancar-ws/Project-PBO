public class Room {
    private String no, type;
    private double price;
    private boolean available;

    public Room(String no, String type, double price, boolean available) {
        this.no = no;
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public String getNo() { return no; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { available = a; }
    public void setType(String t) { type = t; }
    public void setPrice(double p) { price = p; }
}