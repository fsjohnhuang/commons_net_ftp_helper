/**
 * @Title FTPHelper.java
 * @Package commons_net_ftp_helper
 * @author huangzhian
 * @data 2014年10月21日 上午10:55:54
 * @version v1.0
 */
package commons_net_ftp_helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @ClassName FTPHelper
 * @Description 
 * @author huangzhian
 * @date 2014年10月21日 上午10:55:54
 */
public class FTPHelper {
	final String controlEncoding = "GBK";
	final int port = 21;

	FTPClient ftpClient;
	
	public FTPHelper(){}
	
	public boolean open(String ip, int port, String user, String pw){
		boolean flag = true;
		if (ftpClient == null){
			int reply;
			try{
				ftpClient = new FTPClient();
				/*
				 * ftpClient.enterLocalPassiveMode()
				 * 设置使用被动模式传输
				 */
				ftpClient.setControlEncoding(controlEncoding);
				ftpClient.connect(ip, port);
				ftpClient.login(user, pw);
				reply = ftpClient.getReplyCode();
				
				if (!FTPReply.isPositiveCompletion(reply)){
					ftpClient.disconnect();
					System.err.println("拒绝");
					flag = false;
				}
				/*
				 * 默认使用ASCII作为传输模式，不能传输二进制模式，必须设置为二进制文件传输模式
				 * 且需要登录后才能调用该方法，因为会向服务器发送TYPE I命令
				 */
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				/*
				 * 向 服务器发送MODE I命令
				 */
				ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			}	
			catch(SocketException e){
				flag = false;
				e.printStackTrace();
				System.err.println("失败");
			}
			catch(IOException e){
				flag = false;
				e.printStackTrace();
				System.err.println("失败");
			}
		}

		return flag;
	}
	
	public void close(){
		try{
			if (ftpClient != null){
				ftpClient.logout();
				ftpClient.disconnect();
				ftpClient = null;
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize(){
		close();
	}

	/**
	 * 改变远程的工作目录
	 * @param dir 远程目录
	 */
	public void cd(String dir){
		try{
			if ("..".equals(dir)){
				ftpClient.changeToParentDirectory();
			}
			else if (!".".equals(dir)){
				ftpClient.changeWorkingDirectory(dir);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * 罗列远程目录下的文件信息
	 * @param dir 远程目录
	 * @return 文件信息
	 */
	public String[] ls(String dir){
		String[] entries = new String[]{};
		try{
			entries = ftpClient.listNames(dir);
			if (entries != null && entries.length > 0){
				entries = new String[]{};
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return entries;
	}

	/**
	 * 从远程服务器拷贝目录到本地
	 * @param remote 远程目录
	 * @param local 本地目录
	 */
	public void mirror(String remote, String local){
		if (remote.endsWith("/"))
			remote = remote.substring(0, remote.length() - 2);
		if (local.endsWith(File.separator))
			local = local.substring(0, local.length() - 2);
		
		try{
			FTPFile[] entries = ftpClient.listFiles(remote);
			for (FTPFile entry : entries){
				String name = entry.getName();
				if (name.equals(".") || name.equals("..")) continue;

				String currRemote = remote + "/" + name;
				String currLocal = local + File.separator + name;

				if (entry.getType() == FileType.FILE.getCode()){
					OutputStream os = new FileOutputStream(currLocal);
					ftpClient.retrieveFile(currRemote, os);
					os.close();
				}
				else if(entry.getType() == FileType.DIR.getCode()){
					File file = new File(currLocal);
					if (!file.exists() || !file.isDirectory()){
						file.mkdir();
					}
					mirror(currRemote, currLocal);
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 从远程服务器拷贝文件到本地
	 * @param remote 远程文件
	 * @param local 本地文件
	 */
	public void get(String remote, String local){
		try{
			OutputStream os = new FileOutputStream(local);
			ftpClient.retrieveFile(remote, os);
			os.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * 将本地文件拷贝到远程服务器
	 * @param local 本地文件
	 * @param remote 远程文件
	 * @return 
	 */
	public boolean put(String local, String remote){
		boolean flag = true;
		try{
			InputStream is = new FileInputStream(local);
			flag = ftpClient.storeFile(remote, is);
			is.close();
		}
		catch(IOException e){
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 在远程服务器上创建目录
	 * @param dir 被创建的目录
	 */
	public boolean mkdir(String dir){
		boolean flag = true;
		try{
			ftpClient.makeDirectory(dir);
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 删除远程文件
	 * @param file 远程文件
	 * @return
	 */
	public boolean rm(String file){
		boolean flag = true;
		try{
			flag = ftpClient.deleteFile(file);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除远程目录
	 * @param dir
	 * @return
	 */
	public boolean rmdir(String dir){
		boolean flag = true;
		try{
			FTPFile[] entries = ftpClient.listFiles(dir);
			for (FTPFile entry : entries){
				String name = entry.getName();
				String currPath = dir + "/" + name;
				if (entry.getType() == FileType.FILE.getCode()){
					flag = rm(currPath);
				}
				else if(entry.getType() == FileType.DIR.getCode()
						&& !".".equals(name) && !"..".equals(name)){
					flag = rmdir(currPath);
				}
				if (!flag) return flag;
			}
			
			if (flag){
				flag = ftpClient.removeDirectory(dir);
			}
		}
		catch(IOException e){
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
}
