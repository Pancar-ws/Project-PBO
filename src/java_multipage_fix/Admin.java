public class Admin extends User implements Bookable {
    public Admin(String name, String id) {
        super(name, id);
    }

    @Override
    public void bookRoom() {
        System.out.println("Admin: Booking kamar via interface...");
    }

    @Override
    public void cancelBooking() {
        System.out.println("Admin: Cancel booking via interface...");
    }
}