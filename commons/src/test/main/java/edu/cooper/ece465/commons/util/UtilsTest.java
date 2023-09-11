import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import org.testng.annotations.*;

public class UtilsTest {

    @Test
    public void test1() {
        Assert.assertEquals(true, true);
    }

    @BeforeMethod
	public void beforeMethod() {
		System.out.println("Starting Test On UtilsTest");
	}
	
	@AfterMethod
	public void afterMethod() {
		 driver.close();
		System.out.println("Finished Test On UtilsTest");
	}
}
