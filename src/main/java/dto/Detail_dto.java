
package dto;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Detail_dto implements Serializable {
    private static final long serialVersionUID = 1L;

    // ===== 端末情報 =====
    private int id;
    private String assetNumber;
    private String serialNumber;
    private String innoHin;
    private String tel;
    private Date contractDate;
    private Date contractPeriod;
    private Date terminationDate;
    private String rentalCompany;
    private Integer tanka;
    private String udid;
    private String macAddress;

    // ===== 利用者追加フォーム =====
    private String companyId;
    private String departmentId;
    private String ownerNum;
    private String ownerName;
    private String employeeId;
    private Date distributionDate;
    private String longRangeUser;
    private boolean inventory;
    private Date returnDate;

    // ===== 書類 =====
    private String receiptPdf;
    private String returnPdf;

    // ===== 表示用 =====
    private String companyName;
    private String departmentName;

    // ===== 故障有無 =====
    private boolean faultFlag;

    // ===== プルダウン =====
    private List<String[]> companyOptions;
    private List<String[]> departmentOptions;
    private List<String[]> employeeOptions;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAssetNumber() {
        return assetNumber;
    }
    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getInnoHin() {
        return innoHin;
    }
    public void setInnoHin(String innoHin) {
        this.innoHin = innoHin;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    public Date getContractDate() {
        return contractDate;
    }
    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getContractPeriod() {
        return contractPeriod;
    }
    public void setContractPeriod(Date contractPeriod) {
        this.contractPeriod = contractPeriod;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getRentalCompany() {
        return rentalCompany;
    }
    public void setRentalCompany(String rentalCompany) {
        this.rentalCompany = rentalCompany;
    }

    public Integer getTanka() {
        return tanka;
    }
    public void setTanka(Integer tanka) {
        this.tanka = tanka;
    }

    public String getUdid() {
        return udid;
    }
    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getOwnerNum() {
        return ownerNum;
    }
    public void setOwnerNum(String ownerNum) {
        this.ownerNum = ownerNum;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getDistributionDate() {
        return distributionDate;
    }
    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }

    public String getLongRangeUser() {
        return longRangeUser;
    }
    public void setLongRangeUser(String longRangeUser) {
        this.longRangeUser = longRangeUser;
    }

    public boolean isInventory() {
        return inventory;
    }
    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }

    public Date getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReceiptPdf() {
        return receiptPdf;
    }
    public void setReceiptPdf(String receiptPdf) {
        this.receiptPdf = receiptPdf;
    }

    public String getReturnPdf() {
        return returnPdf;
    }
    public void setReturnPdf(String returnPdf) {
        this.returnPdf = returnPdf;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public boolean isFaultFlag() {
        return faultFlag;
    }
    public void setFaultFlag(boolean faultFlag) {
        this.faultFlag = faultFlag;
    }

    public List<String[]> getCompanyOptions() {
        return companyOptions;
    }
    public void setCompanyOptions(List<String[]> companyOptions) {
        this.companyOptions = companyOptions;
    }

    public List<String[]> getDepartmentOptions() {
        return departmentOptions;
    }
    public void setDepartmentOptions(List<String[]> departmentOptions) {
        this.departmentOptions = departmentOptions;
    }

    public List<String[]> getEmployeeOptions() {
        return employeeOptions;
    }
    public void setEmployeeOptions(List<String[]> employeeOptions) {
        this.employeeOptions = employeeOptions;
    }
}
