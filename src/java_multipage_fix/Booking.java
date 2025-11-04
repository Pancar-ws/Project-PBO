public class Booking {
    private String roomNo, guestName;
    private boolean active;

    public Booking(String roomNo, String guestName, boolean active) {
        this.roomNo = roomNo;
        this.guestName = guestName;
        this.active = active;
    }

    public String getRoomNo() { return roomNo; }
    public String getGuestName() { return guestName; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
}