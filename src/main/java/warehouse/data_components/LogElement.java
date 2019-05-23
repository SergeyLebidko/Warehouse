package warehouse.data_components;

import java.util.Date;

public class LogElement implements DataElement {

    private int documentId;
    private Date date;
    private String contractorName;
    private DocumentTypes documentType;
    private String catalogName;
    private int count;

    public LogElement(int documentId, Date date, String contractorName, DocumentTypes documentType, String catalogName, int count) {
        this.documentId = documentId;
        this.date = date;
        this.contractorName = contractorName;
        this.documentType = documentType;
        this.catalogName = catalogName;
        this.count = count;
    }

    public int getDocumentId() {
        return documentId;
    }

    public Date getDate() {
        return date;
    }

    public String getContractorName() {
        return contractorName;
    }

    public DocumentTypes getDocumentType() {
        return documentType;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public int getCount() {
        return count;
    }

}
