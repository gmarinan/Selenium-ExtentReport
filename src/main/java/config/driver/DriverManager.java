package config.driver;

import config.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;


public class DriverManager {

    private DesiredCapabilities capabilities = new DesiredCapabilities();
    private WebDriver webDriver;

    protected String resolveDriver(String nav, String ambURL) {
        System.out.println("\n[ Sistema operativo >>> " + System.getProperty("os.name").toLowerCase() + " ]\n");
        System.out.println("[ iniciando WebDriver ]");
        String detalleNavegador = "";
        switch (nav.toLowerCase()) {
            case "chrome":
                detalleNavegador = "Chrome";
                Constants.browser = detalleNavegador;
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--ignore-certificate-errors");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--remote-allow-origins=*");
                options.setCapability("goog:loggingPrefs", logPrefs);
                this.webDriver = new ChromeDriver(options);
                //this.capabilities.setBrowserName("Chrome");
                this.webDriver.manage().window().maximize();
                break;
            case "explorer":
                detalleNavegador = "Explorer";
                Constants.browser = detalleNavegador;
                this.capabilities.setCapability("enablePersistentHover", false);
                this.capabilities.setCapability("requireWindowFocus", false);
                this.capabilities.setCapability("unexpectedAlertBehaviour", true);
                this.capabilities.setCapability("acceptSslCerts", true);
                this.capabilities.setCapability("ignoreProtectedModeSettings", true);
                this.capabilities.setJavascriptEnabled(true);

                //Forzamos la descarga del driver de 32bits y evitar lentitudes

                //Seccion para enlazar edge a internet explorer en modo compatibilidad
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                ieOptions.attachToEdgeChrome();
                ieOptions.ignoreZoomSettings();
                ieOptions.introduceFlakinessByIgnoringSecurityDomains();
                ieOptions.withEdgeExecutablePath("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");
                //Fin de Seccion

                this.webDriver = new InternetExplorerDriver(ieOptions);
                this.capabilities.setBrowserName("Explorer");
                this.webDriver.manage().window().maximize();
                break;
            case "firefox":
                detalleNavegador = "Firefox";
                Constants.browser = detalleNavegador;
                this.webDriver = new FirefoxDriver();
                //this.capabilities.setBrowserName("Firefox");
                this.webDriver.manage().window().maximize();
                break;
            case "edge":
                detalleNavegador = "Edge";
                Constants.browser = detalleNavegador;
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--remote-allow-origins=*");
                this.webDriver = new EdgeDriver(edgeOptions);
                this.capabilities.setBrowserName("Microsoft Edge");
                this.webDriver.manage().window().maximize();
                break;
            default:
                System.out.println("No es posible lanzar el navegador, no se reconoce el navegador: " + nav);
        }
        if (this.webDriver != null){
            detalleNavegador += " " + ((RemoteWebDriver) this.webDriver).getCapabilities().getBrowserVersion();
            System.out.println("[ " + detalleNavegador + " ]");
            this.webDriver.get(ambURL);
        } else {

        }
        return detalleNavegador;
    }

    protected void resolveDriverMobile(String nav, String dispositivo, String grupo, String versionSO, String ambURL, String nameTest, String navegador, String userName, String key) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar fecha = new GregorianCalendar();
        boolean buildtag = false;

        System.out.println("\nTest Iniciado:" + nameTest + "\n");
        System.out.println("Sistema operativo ->" + System.getProperty("os.name").toLowerCase());
        System.out.println(nav);
        String[] tags = {"Automatizacion", nav, grupo, navegador};

        switch (nav) {
            case "Android":
                String buildJenkinsAndroid = System.getProperty("build");
                if (buildJenkinsAndroid == null) {
                    String fechaa = dateFormat.format(fecha.getTime());
                    buildJenkinsAndroid = "Build Android " + fechaa;
                } else {
                    buildtag = true;
                }
                try {
                    capabilities.setCapability("build", buildJenkinsAndroid);
                    capabilities.setCapability("name", nameTest);
                    capabilities.setCapability("projectName", "Portal_Paciente_Auto");
                    capabilities.setCapability("platformName", "Android");
                    capabilities.setCapability("deviceName", dispositivo);
                    capabilities.setCapability("platformVersion", versionSO);
                    capabilities.setCapability("browserName", navegador);
                    capabilities.setCapability("version", "latest");
                    //capabilities.setCapability("appiumVersion","1.17.0");
                    capabilities.setCapability("geoLocation", "CL");
                    capabilities.setCapability("tags", tags);
                    ;
                 /*
                 ChromeOptions options = new ChromeOptions();
                 options.addArguments("--disable-notifications");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                 */

                    if (buildtag) {
                        String[] buildTagList = {buildJenkinsAndroid};
                        capabilities.setCapability("buildTags", buildTagList);
                    }
                    capabilities.setCapability("network", true); // To enable network logs
                    capabilities.setCapability("visual", false); // To enable step by step screenshot
                    capabilities.setCapability("video", true); // To enable video recording
                    capabilities.setCapability("console", true); // To capture console logs
                    capabilities.setCapability("timezone", "UTC-04:00");
                    System.out.println("Conectando a Lambdatest");
                    this.webDriver = new RemoteWebDriver(new URL("https://" + userName + ":" + key + "@hub.lambdatest.com/wd/hub"), capabilities);
                    //this.driver= new AndroidDriver (new URL("https://"+AUTOMATE_USERNAME+":"+AUTOMATE_ACCESS_KEY+"@hub.lambdatest.com/wd/hub"),capabilities);

                    System.out.println("Conexion correcta");
                } catch (MalformedURLException e) {
                    System.out.println("URL invalida para la conexion");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Error al conectar a lambdatest " + e.getMessage());
                    Assert.fail("Fail: " + e.getMessage());
                }

                break;
            case "IOS":
                String buildJenkinsIphone = System.getProperty("build");

                if (buildJenkinsIphone == null) {
                    String fechaa = dateFormat.format(fecha.getTime());
                    buildJenkinsIphone = "Build Iphone " + fechaa;
                } else {
                    buildtag = true;
                }

                try {

            /*
                   SafariOptions options = new SafariOptions();
                   options.setUseTechnologyPreview(true);
                   capabilities.setCapability(SafariOptions.CAPABILITY, options);
                   */
                    capabilities.setCapability("version", "latest");
                    capabilities.setCapability("build", buildJenkinsIphone);
                    capabilities.setCapability("name", nameTest);
                    capabilities.setCapability("projectName", "Portal_Paciente_Auto");
                    capabilities.setCapability("platformName", "iOS");
                    capabilities.setCapability("deviceName", dispositivo);
                    capabilities.setCapability("platformVersion", versionSO);
                    capabilities.setCapability("browserName", navegador);
                    capabilities.setCapability("appiumVersion", "1.21.0");
                    if (buildtag) {
                        String[] buildTagList = {buildJenkinsIphone};
                        capabilities.setCapability("buildTags", buildTagList);
                    }
                    capabilities.setCapability("geoLocation", "CL");
                    capabilities.setCapability("tags", tags);
                    capabilities.setCapability("network", true); // To enable network logs
                    capabilities.setCapability("visual", false); // To enable step by step screenshot
                    capabilities.setCapability("video", true); // To enable video recording
                    capabilities.setCapability("console", true); // To capture console logs
                    capabilities.setCapability("timezone", "UTC-04:00");
                    System.out.println("Conectando a Lambdatest");


                    this.webDriver = new RemoteWebDriver(new URL("https://" + userName + ":" + key + "@hub.lambdatest.com/wd/hub"), capabilities);

                    System.out.println("Conexion correcta");
                } catch (MalformedURLException e) {
                    System.out.println("URL invalida para la conexion");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Error al conectar a lambdatest " + e.getMessage());
                    Assert.fail("Fail: " + e.getMessage());
                }

                break;
            default:
                System.out.println("No es posible lanzar el navegador, no se reconoce el navegador: " + nav);
                break;
        }

        this.webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        //this.driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
        this.webDriver.get(ambURL);
    }


    protected WebDriver getDriver() {
        return this.webDriver;
    }

    protected void setDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

}
