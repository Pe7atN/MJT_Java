
public class Utils {

    public static final double PI = 3.14; // constant
    private static int radius = 10; // static member
    private String fact5 = "5!"; // non-static member
    // static method
    public static long fact(int n) {
        if (n == 1) { return 1; } else { return n * fact(n - 1); }
    }
    // non-static method
    public String getFact() {
        return fact5;
    }
    public static void main(String[] args) {
        System.out.println("Perimeter is " + 2 * Utils.PI * radius);
        System.out.println(new Utils().getFact() + "=" + Utils.fact(5));
    }
}
