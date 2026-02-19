package dto;

public class User_dto {
	
	  private int userId;
	    private String userName;      // 表示用（ログインは user_id で実施）
	    private String passwordHash;  // 格納されているハッシュ文字列
	    private String roleId;
	    private int isActive;

	    public int getUserId() { return userId; }
	    public void setUserId(int userId) { this.userId = userId; }
	    public String getUserName() { return userName; }
	    public void setUserName(String userName) { this.userName = userName; }
	    public String getPasswordHash() { return passwordHash; }
	    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	    public String getRoleId() { return roleId; }
	    public void setRoleId(String roleId) { this.roleId = roleId; }
	    public int getIsActive() { return isActive; }
	    public void setIsActive(int isActive) { this.isActive = isActive; }
}
