package warehouse.data_access_components;

public class CatalogElement implements SimpleDataElement{

    private int id;
    private String name;

    public CatalogElement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
