
package dto;

import java.io.Serializable;

public class Fault_count_row_dto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String companyId;
    private String companyName;
    private String departmentId;
    private String departmentName;
    private String employeeId;
    private String employeeName;
    private int faultCount;

    // 表示用の集計値
    private int companyTotal;
    private int departmentTotal;

    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getFaultCount() {
        return faultCount;
    }
    public void setFaultCount(int faultCount) {
        this.faultCount = faultCount;
    }

    public int getCompanyTotal() {
        return companyTotal;
    }
    public void setCompanyTotal(int companyTotal) {
        this.companyTotal = companyTotal;
    }

    public int getDepartmentTotal() {
        return departmentTotal;
    }
    public void setDepartmentTotal(int departmentTotal) {
        this.departmentTotal = departmentTotal;
    }
}
