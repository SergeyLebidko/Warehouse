package warehouse.data_components.data_elements;

public class DeliveryElement {

    private Integer catalogId;
    private String catalogName;
    private Integer inc;
    private Integer dec;

    public DeliveryElement(Integer catalogId, String catalogName, Integer inc, Integer dec) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.inc = inc;
        this.dec = dec;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public Integer getInc() {
        return inc;
    }

    public Integer getDec() {
        return dec;
    }

}
