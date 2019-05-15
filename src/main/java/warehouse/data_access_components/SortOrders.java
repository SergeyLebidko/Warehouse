package warehouse.data_access_components;

public enum SortOrders {

    TO_UP(1), NO_ORDER(0), TO_DOWN(-1);

    private int mul;

    SortOrders(int mul) {
        this.mul = mul;
    }

    public int getMul(){
        return mul;
    }

}
