package warehouse.data_components.data_elements;

public class CatalogElement implements SimpleDataElement{

    private Integer id;
    private String name;

    public CatalogElement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CatalogElement(String name) {
        id = null;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String nextName){
        name = nextName;
    }

    @Override
    public String toString() {
        return name;
    }

}
