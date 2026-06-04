
package dto;

import java.util.Date;

public class Terminal_ibs_dto {

    private int id;
    private String assetNumber;
    private String serialNumber;
    private String innoHin;
    private Date contractDate;
    private Date contractPeriod;
    private Date terminationDate;
    private int tanka;

    private String companyId;
    private String departmentId;
    private String ownerNum;
    private String ownerName;
    private Date distributionDate;
    
    // ★追加
    private String companyName;
    private String departmentName;

    // getter setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAssetNumber() { return assetNumber; }
    public void setAssetNumber(String assetNumber) { this.assetNumber = assetNumber; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getInnoHin() { return innoHin; }
    public void setInnoHin(String innoHin) { this.innoHin = innoHin; }

    public Date getContractDate() { return contractDate; }
    public void setContractDate(Date contractDate) { this.contractDate = contractDate; }

    public Date getContractPeriod() { return contractPeriod; }
    public void setContractPeriod(Date contractPeriod) { this.contractPeriod = contractPeriod; }

    public Date getTerminationDate() { return terminationDate; }
    public void setTerminationDate(Date terminationDate) { this.terminationDate = terminationDate; }

    public int getTanka() { return tanka; }
    public void setTanka(int tanka) { this.tanka = tanka; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getOwnerNum() { return ownerNum; }
    public void setOwnerNum(String ownerNum) { this.ownerNum = ownerNum; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }


    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    
    public Date getDistributionDate() { return distributionDate; }
    public void setDistributionDate(Date distributionDate) { this.distributionDate = distributionDate; }
}
