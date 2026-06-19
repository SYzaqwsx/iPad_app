
package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class Register_dto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String assetNumber;
    private String serialNumber;
    private String innoHin;
    private String tel;
    private Date contractDate;
    private Date contractPeriod;
    private String rentalCompany;
    private BigDecimal tanka;
    private String udid;
    private String macAddress;

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

    public String getRentalCompany() {
        return rentalCompany;
    }
    public void setRentalCompany(String rentalCompany) {
        this.rentalCompany = rentalCompany;
    }

    public BigDecimal getTanka() {
        return tanka;
    }
    public void setTanka(BigDecimal tanka) {
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
}
