package config.driver;

import config.Constants;

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


public class DriverManager {

    private DesiredCapabilities capabilities = new DesiredCapabilities();
    private WebDriver webDriver;

    
    @SuppressWarnings("deprecation")
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


    protected WebDriver getDriver() {
        return this.webDriver;
    }

    protected void setDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

}
