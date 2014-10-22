/**
 * @Title FTPHelper.java
 * @Package commons_net_ftp_helper
 * @author huangzhian
 * @data 2014��10��21�� ����10:55:54
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
 * @date 2014��10��21�� ����10:55:54
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
				 * ����ʹ�ñ���ģʽ����
				 */
				ftpClient.setControlEncoding(controlEncoding);
				ftpClient.connect(ip, port);
				ftpClient.login(user, pw);
				reply = ftpClient.getReplyCode();
				
				if (!FTPReply.isPositiveCompletion(reply)){
					ftpClient.disconnect();
					System.err.println("�ܾ�");
					flag = false;
				}
				/*
				 * Ĭ��ʹ��ASCII��Ϊ����ģʽ�����ܴ��������ģʽ����������Ϊ�������ļ�����ģʽ
				 * ����Ҫ��¼����ܵ��ø÷�������Ϊ�������������TYPE I����
				 */
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				/*
				 * �� ����������MODE I����
				 */
				ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			}	
			catch(SocketException e){
				flag = false;
				e.printStackTrace();
				System.err.println("ʧ��");
			}
			catch(IOException e){
				flag = false;
				e.printStackTrace();
				System.err.println("ʧ��");
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
	 * �ı�Զ�̵Ĺ���Ŀ¼
	 * @param dir Զ��Ŀ¼
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
	 * ����Զ��Ŀ¼�µ��ļ���Ϣ
	 * @param dir Զ��Ŀ¼
	 * @return �ļ���Ϣ
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
	 * ��Զ�̷���������Ŀ¼������
	 * @param remote Զ��Ŀ¼
	 * @param local ����Ŀ¼
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
	 * ��Զ�̷����������ļ�������
	 * @param remote Զ���ļ�
	 * @param local �����ļ�
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
	 * �������ļ�������Զ�̷�����
	 * @param local �����ļ�
	 * @param remote Զ���ļ�
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
	 * ��Զ�̷������ϴ���Ŀ¼
	 * @param dir ��������Ŀ¼
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
	 * ɾ��Զ���ļ�
	 * @param file Զ���ļ�
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
	 * ɾ��Զ��Ŀ¼
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
