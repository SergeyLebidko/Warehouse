package warehouse.data_components.data_elements;

import warehouse.data_components.DocumentTypes;

import java.util.ArrayList;
import java.util.Date;

public class Document implements DataElement{

    private Integer id;
    private Date date;
    private DocumentTypes type;
    private int contractorId;
    private String contractorName;
    private ArrayList<Operation> operationList;

    public Document(Integer id, Date date, DocumentTypes type, int contractorId, String contractorName) {
        operationList = new ArrayList<>();
        this.id = id;
        this.date = date;
        this.type = type;
        this.contractorId = contractorId;
        this.contractorName = contractorName;
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public DocumentTypes getType() {
        return type;
    }

    public int getContractorId() {
        return contractorId;
    }

    public String getContractorName() {
        return contractorName;
    }

    public ArrayList<Operation> getOperationList() {
        return operationList;
    }

}
