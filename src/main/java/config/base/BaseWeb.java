package config.base;

import com.aventstack.extentreports.Status;
import com.google.common.base.Throwables;
import utils.ZipUtilis;
import config.driver.DriverContext;
import config.reporteWeb.HtmlReport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.MetodosGenericos;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseWeb {
    public static HtmlReport reportes;
    public WebDriver driver;

    public BaseWeb() {
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
    @Parameters({"browser", "url"})
    public void setUp(ITestContext testContext, String browser, Method method, String url) {
        String detalleNav = DriverContext.setUp(browser, url);
        String testName = testContext.getCurrentXmlTest().getName();
        System.out.println("[ Ejecuci\u00f3n TestCase: " + testName + " ]");
        String[] grupos = method.getAnnotation(Test.class).groups();
        String obtenerDescpr = method.getAnnotation(Test.class).description();
        HtmlReport.newTest(testName, grupos, obtenerDescpr);
        HtmlReport.updateDriver(DriverContext.getDriver());
        HtmlReport.setUsedBrowsers(detalleNav);
    }

    @AfterMethod
    @Parameters({"browser"})
    public void tearDown(ITestResult iTestResult, String browser) {
        if (!iTestResult.isSuccess()) {
            String stackTrace = Throwables.getStackTraceAsString(iTestResult.getThrowable());
            if (browser.toLowerCase(Locale.ROOT).equals("chrome")) {
                List<LogEntry> entries = DriverContext.getDriver().manage().logs().get("performance").getAll();
                System.out.println(entries.size() + " " + "performance" + " log entries found");
                List<String> trazasXHR = new ArrayList<>();
                ;
                trazasXHR.add("Status Text,Status Code,URL");
                for (LogEntry entrie : entries) {
                    if (entrie.toString().contains("\"type\":\"XHR\"") && entrie.toString().matches(".*\"status\":[3-5][0-9][0-9].*")) {
                        String[] parts = entrie.toString().split(",");
                        String statusText = "";
                        String status = "";
                        String url = MetodosGenericos.buscarDatoXHR(parts, "url");
                        for (String dato : parts) {
                            if (dato.matches("\"statusText\".*")) {
                                statusText = dato.replaceAll("\"statusText\":", "").replaceAll("\"", "");
                            }
                            if (dato.matches("\"status\":[3-5][0-9][0-9].*")) {
                                status = dato.replaceAll("\"status\":", "");
                            }
                            if (dato.matches("\"url\".*")) {
                                url = dato.replaceAll("\"url\":", "").replaceAll("}", "").replaceAll("\"", "");
                            }
                        }

                        String fila = statusText + "," + status + "," + url;
                        trazasXHR.add(fila);
                    }

                }
                HtmlReport.addWebReport("Traza XHR", HtmlReport.insertarTabla(trazasXHR), Status.INFO, false);
            }

            if (stackTrace.contains("org.testng.Assert.fail")) {
                HtmlReport.fail("Error de aserci\u00F3n");
                HtmlReport.addInfoAssertion("Error info: " + stackTrace);
            } else {
                HtmlReport.addWarning("El test fallo inesperadamente.");
                HtmlReport.addWarning("Error info: " + stackTrace);
            }

        } else HtmlReport.addInfo("Test finalizado correctamente!");
        DriverContext.quitDriver();
    }

}
