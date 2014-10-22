/**
 * @Title FILE.java
 * @Package commons_net_ftp_helper
 * @Description TODO
 * @author huangzhian
 * @data 2014年10月21日 下午12:37:02
 * @version v1.0
 */
package commons_net_ftp_helper;

/**
 * @ClassName FileType
 * @Description 
 * @author huangzhian
 * @date 2014年10月21日 下午12:37:02
 */
public enum FileType {
	FILE(0, "文件"), DIR(1, "目录"), SYMBOL(2, "符号链接"), UNKNOWN(3, "未知类型");

	private int code;
	private String name;
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	private FileType(int code, String name){
		this.code = code;
		this.name = name;
	}
}
