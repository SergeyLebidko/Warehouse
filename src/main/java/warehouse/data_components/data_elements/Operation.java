package warehouse.data_components.data_elements;

public class Operation implements DataElement {

    private Integer id;
    private int documentId;
    private int catalogId;
    private String catalogName;
    private int count;

    public Operation(Integer id, int documentId, int catalogId, String catalogName, int count) {
        this.id = id;
        this.documentId = documentId;
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.count = count;
    }

    public Operation(int documentId, int catalogId, String catalogName, int count) {
        id = null;
        this.documentId = documentId;
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public int getDocumentId() {
        return documentId;
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
