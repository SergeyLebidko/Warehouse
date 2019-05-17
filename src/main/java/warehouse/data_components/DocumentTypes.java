package warehouse.data_components;

public enum DocumentTypes {

    COM(1, "Приход"),
    CONS(-1, "Расход");

    private int mul;
    private String name;

    DocumentTypes(int mul, String name) {
        this.name = name;
        this.mul = mul;
    }

    public int getMul() {
        return mul;
    }

    public String getName() {
        return name;
    }

}
