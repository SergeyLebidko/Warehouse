package warehouse.data_access_components;

public class ContractorsElement implements SimpleDataElement{

    private int id;
    private String name;

    public ContractorsElement(int id, String name) {
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

}
