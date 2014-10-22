/**
 * @Title FTPTest.java
 * @Package commons_net_ftp_helper.test
 * @Description TODO
 * @author huangzhian
 * @data 2014年10月21日 上午11:14:58
 * @version v1.0
 */
package commons_net_ftp_helper.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.ServiceMode;

import org.junit.Before;
import org.junit.Test;

import commons_net_ftp_helper.FTPHelper;

/**
 * @ClassName FTPTest
 * @Description
 * @author huangzhian
 * @date 2014年10月21日 上午11:14:58
 */
public class FTPTest {
	FTPHelper ftpHelper;

	public FTPTest() {
		String ip = "10.252.36.217", user = "lpp", pw = "gmcc123";
		int port = 9100;
		ftpHelper = new FTPHelper();
        ftpHelper.open(ip, port, user, pw);
	}

	@Test
	public void testMirror() {
		ftpHelper.mirror("src", "d:\\test");
	}

	@Test
	public void testGet(){
		ftpHelper.get("src/e.txt", "d:\\test\\d.txt");
	}

	@Test
	@SuppressWarnings("unused")
	public void testRmdir() throws NoSuchMethodException, SecurityException{
		Method m = this.getClass().getMethod("testRmdir", new Class<?>[]{});
		boolean ok = ftpHelper.rmdir("src");
	}
}
