package warehouse.data_components;

public class RemaindElement implements DataElement{

    private int catalogId;
    private String catalogName;
    private int count;

    public RemaindElement(int catalogId, String catalogName, int count) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.count = count;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public int getCount() {
        return count;
    }

}
