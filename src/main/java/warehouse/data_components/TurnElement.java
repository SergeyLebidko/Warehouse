package warehouse.data_components;

public class TurnElement implements DataElement {

    private Integer catalogId;
    private String catalogName;
    private Integer beginCount;
    private Integer incCount;
    private Integer decCount;
    private Integer endCount;

    public TurnElement(Integer catalogId, String catalogName, Integer beginCount, Integer incCount, Integer decCount, Integer endCount) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.beginCount = beginCount;
        this.incCount = incCount;
        this.decCount = decCount;
        this.endCount = endCount;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public Integer getBeginCount() {
        return beginCount;
    }

    public Integer getIncCount() {
        return incCount;
    }

    public Integer getDecCount() {
        return decCount;
    }

    public Integer getEndCount() {
        return endCount;
    }

}
