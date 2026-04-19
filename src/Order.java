import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    public enum Status { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

    private String          orderId;
    private String          userId;
    private List<OrderItem> items;
    private Status          status;
    private LocalDateTime   createdAt;
    private double          totalAmount;

    public Order(String orderId, String userId) {
        this.orderId   = orderId;
        this.userId    = userId;
        this.items     = new ArrayList<>();
        this.status    = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(String productId, int quantity, double price) {
        items.add(new OrderItem(productId, quantity, price));
        recalculate();
    }

    public boolean removeItem(String productId) {
        boolean removed = items.removeIf(i -> i.productId.equals(productId));
        if (removed) recalculate();
        return removed;
    }

    private void recalculate() {
        totalAmount = items.stream()
            .mapToDouble(i -> i.quantity * i.price)
            .sum();
    }

    public void confirm()  { transition(Status.CONFIRMED); }
    public void ship()     { transition(Status.SHIPPED); }
    public void deliver()  { transition(Status.DELIVERED); }
    public void cancel()   { transition(Status.CANCELLED); }

    private void transition(Status next) {
        System.out.println("Order " + orderId + ": " + status + " -> " + next);
        this.status = next;
    }

    public String         getOrderId()     { return orderId; }
    public String         getUserId()      { return userId; }
    public Status         getStatus()      { return status; }
    public double         getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems()      { return new ArrayList<>(items); }

    public static class OrderItem {
        String productId;
        int    quantity;
        double price;
        OrderItem(String productId, int quantity, double price) {
            this.productId = productId;
            this.quantity  = quantity;
            this.price     = price;
        }
    }
}
