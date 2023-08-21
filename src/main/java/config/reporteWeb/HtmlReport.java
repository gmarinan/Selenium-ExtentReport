package config.reporteWeb;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HtmlReport {
    private static ExtentReports reports;
    private static ExtentTest test;
    public static ExtentSparkReporter spark;
    private static WebDriver driver;
    private static int paso;
    private static String suite;
    private static ArrayList<String> errorMessages;
    private static ArrayList<String> usedBrowsers;
    private static String URL_REPORT = "HTMLREPORTS\\";

    public HtmlReport(String suiteName) {
        reports = new ExtentReports();
        spark = new ExtentSparkReporter(URL_REPORT + suiteName + "\\ReporteEjecucion.html").viewConfigurer().viewOrder()
                .as(new ViewName[]{
                        ViewName.DASHBOARD,
                        ViewName.CATEGORY,
                        ViewName.TEST,
                        ViewName.AUTHOR,
                        ViewName.DEVICE,
                        ViewName.EXCEPTION,
                        ViewName.LOG
                })
                .apply();

        reports.attachReporter(new ExtentObserver[]{spark});
        reports.setSystemInfo("OS", nombreOS());
        try {
            File json = new File("htmlConfig.json");
            spark.loadJSONConfig(json);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        suite = suiteName;
        paso = 1;
        errorMessages = new ArrayList<>();
        usedBrowsers = new ArrayList<>();

        File screenShotFolder = new File(URL_REPORT + suite + "\\htmlScreenshots");
        if (screenShotFolder.exists()) {
            try {
                FileUtils.forceDelete(screenShotFolder);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ocurrió un error al borrar los screenshots anteriores");
            }
        } else {
            screenShotFolder.mkdir();
        }
    }

    public static void setUsedBrowsers(String browser) {
        if(!usedBrowsers.contains(browser)) {
            usedBrowsers.add(browser);
        }
    }

    private static void addBrowsersToReport() {
        if (!usedBrowsers.isEmpty()) {
            for (String browser : usedBrowsers) {
                reports.setSystemInfo("Navegador usado", browser);
            }
            usedBrowsers.clear();
        }
    }

    public static void setReportPath(String path) {
        URL_REPORT = path;
    }

    public static void newTest(String testName, String[] categories, String url) {
        test = reports.createTest(testName).assignCategory(categories);
        String testPlatform = url.contains("qtest") ? "qTest" : (url.contains("atlassian") ? "Xray" : "");
        test.log(Status.INFO, "Ir a " + testPlatform + " : <a href='" + url + "' target=\"_blank\">" + testName + "</a>");
        paso = 1;
    }

    public static String getSuite() {
        return suite;
    }

    public static String getReportPath() {
        return System.getProperty("user.dir") + "\\" + URL_REPORT;
    }

    public static void updateDriver(WebDriver driv) {
        driver = driv;
    }

    private static String captureScreenShot(WebDriver driver) {
        String imageName = System.currentTimeMillis() + ".png";
        File screenshotFile = (File) ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File targetFile = new File(URL_REPORT + suite + "\\htmlScreenshots", imageName);

        try {
            FileUtils.copyFile(screenshotFile, targetFile);
        } catch (IOException var5) {
            test.log(Status.INFO, "No se pudo obtener el screenshot");
        }

        System.out.println("Screenshot: " + targetFile.getName());
        return targetFile.getName();
    }

    public static void addStep(Status status, String nombrePaso, String descripcion, Boolean screenshot) {
        test.log(Status.INFO, "PASO " + paso + ": " + nombrePaso);
        if (screenshot) {
            String ss = captureScreenShot(driver);
            test.log(status, descripcion, MediaEntityBuilder.createScreenCaptureFromPath("htmlScreenshots\\" + ss).build());
        } else {
            test.log(status, descripcion);
        }

        if (status.toString().equals("Fail")) {
            addErrorMessagesToReport();
        }

        ++paso;
    }

    public static void addInfo(String info) {
        String condition = "Build info";
        if (info.contains(condition)) {
            String[] info1 = info.split(condition);
            test.log(Status.INFO, info1[0]);
        } else {
            test.log(Status.INFO, info);
        }

    }

    public static void addInfoAssertion(String info) {
        String condition = "Build info";
        if (info.contains(condition)) {
            String[] info1 = info.split(condition);
            test.log(Status.INFO, info1[0]);
        } else {
            test.log(Status.INFO, MarkupHelper.createCodeBlock(info));
        }

    }

    public static void addWarning(String info) {
        String condition = "Error info:";
        if (info.contains(condition)) {
            test.log(Status.WARNING, MarkupHelper.createCodeBlock(info));
        } else {
            test.log(Status.WARNING, info);
        }
    }

    public static void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    public static void addErrorMessagesToReport() {
        if (!errorMessages.isEmpty()) {

            for (String message : errorMessages) {
                addInfo(message);
            }
            errorMessages.clear();
        }
    }

    public static void addSystemInfo(String info) {
        String[] infos = info.split(",");
        test.assignCategory(infos);
    }

    public static void endReport() {
        addBrowsersToReport();
        reports.flush();
    }

    public static void fail(Throwable t) {
        test.fail(t);
    }

    public static void fail(String t) {
        test.fail(t);
    }

    public static void reporteObjetoDesplegado(boolean estadoObjeto, String objeto, String vista, boolean fatal) {
        if (estadoObjeto) {
            addStep(Status.PASS, vista, "Elemento encontrado: " + objeto, true);
        } else {
            addStep(Status.FAIL, vista, "Elemento no encontrado: " + objeto, true);
            if (fatal) {
                Assert.fail("[ Elemento no encontrado: " + objeto + " ]");
            }
        }

    }

    public static void addWebReportImage(String nombre, String descripcion, Status status, boolean fatal) {
        addStep(status, nombre, descripcion, true);
        if (fatal) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
    }

    public static void addWebReportImage(String nombre, String descripcion, Status status) {
        addStep(status, nombre, descripcion, true);
        if (status.equals(Status.FAIL)) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
    }

    public static void addWebReport(String nombre, String descripcion, Status status, boolean fatal) {
        addStep(status, nombre, descripcion, false);
        if (fatal) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
    }

    public static void addWebReport(String nombre, String descripcion, Status status) {
        addStep(status, nombre, descripcion, false);
        if (status.equals(Status.FAIL)) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
    }

    public static void addWebReport(String nombre, Markup descripcionMarkup, Status status) {
        test.log(Status.INFO, "PASO " + paso + ": " + nombre);
        test.log(status, descripcionMarkup);
        if (status.equals(Status.FAIL)) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
        ++paso;
    }

    public static void addWebReport(String nombre, Markup descripcion, Status status, boolean fatal) {
        test.log(Status.INFO, "PASO " + paso + ": " + nombre);
        test.log(status, descripcion);
        if (fatal) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
        ++paso;
    }

    public static void addWebReport(String nombre, List<String> descripcion, Status status, boolean fatal) {
        test.log(Status.INFO, "PASO " + paso + ": " + nombre);
        test.log(status, MarkupHelper.createUnorderedList(descripcion));
        if (fatal) {
            Assert.fail("Error FATAL capturado/declarado en el Paso '" + nombre + "'");
        }
        ++paso;
    }

    public static String[][] formatTable(List<String> resultado, int cantidadFilas) {
        List<String[]> arra = new ArrayList<>();
        int cont = 0;
        int limite = cantidadFilas > 0 ? cantidadFilas : resultado.size();
        for (String fila : resultado) {
            if (cont >= limite) break;
            arra.add(fila.split(","));
            cont++;
        }
        return arra.toArray(new String[0][]);
    }

    public static Markup insertarTabla(List<String> resultado) {
        return MarkupHelper.createTable(formatTable(resultado, 10), "table-sm");
    }

    public static Markup insertarTablaCompleta(List<String> resultado) {
        return MarkupHelper.createTable(formatTable(resultado, -1), "table-sm");
    }

    public static Markup insertarBloqueJSON(JSONObject obj) {
        return MarkupHelper.createJsonCodeBlock(obj);
    }

    /**
     * Ejemplo de Uso: <br>
     * <pre> {@code String cadenaJson = "{'foo' : 'bar', 'foos' : ['b','a','r'], 'bar' : {'foo':'bar', 'bar':false,'foobar':1234}}";
     * addWebReport("Nombre del paso con contenido XML", insertarBloqueJSON(cadenaJson), Status.INFO);}</pre>
     *
     * @param jsonString Cadena con contenido JSON
     * @return Markup formateado para ser incrustado en reporte
     */
    public static Markup insertarBloqueJSON(String jsonString) {
        return MarkupHelper.createCodeBlock(jsonString, CodeLanguage.JSON);
    }

    /**
     * Ejemplo de Uso: <br>
     * <pre>{@code String cadenaXML = "<root>" +
     * "\n  <Person>" +
     * "\n    <Name>Joe Doe</Name>" +
     * "\n    <StartDate>2007-01-01</StartDate>" +
     * "\n    <EndDate>2009-01-01</EndDate>" +
     * "\n    <Location>London</Location>" +
     * "\n  </Person>" +
     * "\n</root>";
     * addWebReport("Nombre del paso con contenido XML", insertarBloqueXML(cadenaXML), Status.INFO);}</pre>
     *
     * @param xmlString Cadena con contenido XML
     * @return Markup formateado para ser incrustado en reporte
     */
    public static Markup insertarBloqueXML(String xmlString) {
        return MarkupHelper.createCodeBlock(xmlString, CodeLanguage.XML);
    }

    public static Markup insertarBloqueComparacion(String contenidoIzquierdo, String contenidoDerecho) {
        return MarkupHelper.createCodeBlock(contenidoIzquierdo, contenidoDerecho);
    }

    public static Markup insertarConsulta(String consulta) {
        return MarkupHelper.createCodeBlock(consulta);
    }

    private String nombreOS() {
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "systeminfo");
            builder.redirectErrorStream(true);
            try {
                Process p = builder.start();
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.toLowerCase(Locale.ROOT).contains("nombre del sistema operativo") || line.toLowerCase(Locale.ROOT).contains("os name")) {
                        String[] resultadoVector = line.split(":");
                        String resultado = resultadoVector[1];
                        return resultado.trim();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "INFO NO DISPONIBLE";
            }
        } else {
            return System.getProperty("os.name");
        }
        return null;
    }

}