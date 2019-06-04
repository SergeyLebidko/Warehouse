package warehouse.data_components.data_elements;

public interface SimpleDataElement extends DataElement {

    Integer getId();
    String getName();
    void setName(String nextName);

}
