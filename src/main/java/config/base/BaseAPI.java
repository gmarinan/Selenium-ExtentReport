package config.base;

import com.google.common.base.Throwables;
import utils.ZipUtilis;
import config.driver.DriverContext;
import config.reporteWeb.HtmlReport;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class BaseAPI {
    public static HtmlReport reportes;

    public BaseAPI() {
    }

    @BeforeSuite
    public void beforeSuite(ITestContext testContext) {
        String suiteName = testContext.getCurrentXmlTest().getSuite().getName();
        System.out.println("suiteName: " + suiteName);
        reportes = new HtmlReport(suiteName);
    }

    @AfterSuite
    @Parameters({"carpetaReporte"})
    public void afterSuite(ITestContext testContext, String carpetaReporte) {
        HtmlReport.endReport();
        ZipUtilis zip = new ZipUtilis(carpetaReporte);
        zip.generarReporte();
    }

    @BeforeMethod
    public void setUp(ITestContext testContext, Method method) {
        String testName = testContext.getCurrentXmlTest().getName();
        System.out.println("Ejecuci\u00f3n TestCase: " + testName);
        String[] grupos = method.getAnnotation(Test.class).groups();
        String obtenerDescpr = method.getAnnotation(Test.class).description();
        HtmlReport.newTest(testName, grupos, obtenerDescpr);
        HtmlReport.updateDriver(DriverContext.getDriver());
    }

    @AfterMethod
    public void tearDown(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            String stackTrace = Throwables.getStackTraceAsString(iTestResult.getThrowable());
            if (stackTrace.contains("org.testng.Assert.fail")) {
                HtmlReport.fail("Error de asercion");
                HtmlReport.addInfoAssertion("Error info: " + stackTrace);
            } else {
                HtmlReport.addWarning("El test fallo inesperadamente.");
                HtmlReport.addWarning("Error info: " + stackTrace);
            }

        } else HtmlReport.addInfo("Test finalizado correctamente!");
    }
}
