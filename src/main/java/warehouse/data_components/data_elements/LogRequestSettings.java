package warehouse.data_components.data_elements;

import warehouse.data_components.DocumentTypes;

import java.util.Date;

public class LogRequestSettings {

    private Date beginDate;
    private Date endDate;
    private Integer contractorId;
    private DocumentTypes documentType;
    private Integer catalogId;

    public LogRequestSettings() {
        beginDate = null;
        endDate = null;
        contractorId = null;
        documentType = null;
        catalogId = null;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getContractorId() {
        return contractorId;
    }

    public void setContractorId(Integer contractorId) {
        this.contractorId = contractorId;
    }

    public DocumentTypes getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypes documentType) {
        this.documentType = documentType;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }
}
