package warehouse.data_components.data_elements;

import warehouse.data_components.DocumentTypes;

import java.util.ArrayList;
import java.util.Date;

public class Document implements DataElement{

    private Integer id;
    private Date date;
    private DocumentTypes type;
    private Integer contractorId;
    private String contractorName;
    private ArrayList<Operation> operationList;

    public Document(){
        this(null, null, null, null, null);
    }

    public Document(Integer id, Date date, DocumentTypes type, Integer contractorId, String contractorName) {
        operationList = new ArrayList<>();
        this.id = id;
        this.date = date;
        this.type = type;
        this.contractorId = contractorId;
        this.contractorName = contractorName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(DocumentTypes type) {
        this.type = type;
    }

    public void setContractorId(Integer contractorId) {
        this.contractorId = contractorId;
    }

    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }

    public void setOperationList(ArrayList<Operation> operationList) {
        this.operationList = operationList;
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

    public Integer getContractorId() {
        return contractorId;
    }

    public String getContractorName() {
        return contractorName;
    }

    public ArrayList<Operation> getOperationList() {
        return operationList;
    }

}
