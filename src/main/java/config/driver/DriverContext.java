package config.driver;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

import static config.reporteWeb.HtmlReport.*;
import static utils.Web.esperarElemento;
import static utils.Web.visualizarObjeto;

public class DriverContext {

    private static DriverManager driverManager = new DriverManager();
    private static String ambienteURL = "";
    public static final String TRAZA = "Traza: ";

    public static String getAmbienteURL() {
        return ambienteURL;
    }

    public static void setAmbienteURL(String ambienteURL) {
    }


    public static String setUp(String nav, String ambURL) {
        setAmbienteURL(ambURL);
        System.out.println(ambURL);
        return driverManager.resolveDriver(nav, ambURL);
    }

    public static void setUpMobile(String nav, String dispositivo, String grupo, String versionSO, String ambURL, String testName, String navegador, String username, String key) {
        System.out.println("Iniciando Test");
        System.out.println(nav);
        setAmbienteURL(ambURL);
        System.out.println(ambURL);
        System.out.println("driver context");
        driverManager.resolveDriverMobile(nav, dispositivo, grupo, versionSO, ambURL, testName, navegador, username, key);
    }

    public static WebDriver getDriver() {
        return driverManager.getDriver();
    }

    public static void setDriverTimeout(Integer tiempo) {
        driverManager.getDriver().manage().timeouts().implicitlyWait((long) tiempo, TimeUnit.SECONDS);
    }

    public static void quitDriver() {
        driverManager.getDriver().quit();
    }


    // Realiza un click al elemento recibido
    public static void click(WebElement element) {
        if (esperarElemento(element)) {
            try {
                element.click();
            } catch (Exception e) {
                addWebReportImage("Error al cliquear el elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Click con paso automatico en el reporte
    public static void click(WebElement element, String descripcionDelPaso) {
        if (esperarElemento(element)) {
            try {
                // Se hace click en el elemento
                element.click();
                // Se agrega el paso al reporte con la descripcion parametrizada
                addWebReportImage(descripcionDelPaso, "", Status.PASS);
            } catch (Exception e) {
                addWebReportImage("Error al cliquear el elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Realiza un click al elemento recibido pero solo con un intento con tiempo determinado de espera
    public static void click(WebElement element, int segundos) {
        if (visualizarObjeto(element, segundos)) {
            try {
                element.click();
            } catch (Exception e) {
                addWebReportImage("Error al cliquear el elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Realiza un click alternativo con JS
    public static void clickJS(WebElement element) {
        if (esperarElemento(element)) {
            try {
                JavascriptExecutor executor = (JavascriptExecutor) DriverContext.getDriver();
                executor.executeScript("arguments[0].click();", element);
            } catch (Exception e) {
                addWebReportImage("Error al cliquear el elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Realiza un click alternativo con JS con reporte automatico
    public static void clickJS(WebElement element, String descripcionDelPaso) {
        if (esperarElemento(element)) {
            try {
                JavascriptExecutor executor = (JavascriptExecutor) DriverContext.getDriver();
                executor.executeScript("arguments[0].click();", element);
                // Se agrega el paso al reporte con la descripcion parametrizada
                addWebReportImage(descripcionDelPaso, "", Status.PASS);
            } catch (Exception e) {
                addWebReportImage("Error al cliquear el elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Envia datos a un element de tipo input
    public static void senKeys(WebElement element, String dato) {
        if (esperarElemento(element)) {
            try {
                element.sendKeys(dato);
            } catch (Exception e) {
                addWebReportImage("Error al setear el dato: " + dato + " en el elemento: " + element,
                        TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Envia keys a un element de tipo input
    public static void senKeys(WebElement element, Keys dato) {
        if (esperarElemento(element)) {
            try {
                element.sendKeys(dato);
            } catch (Exception e) {
                addWebReportImage("Error al setear el dato: " + dato + " en el elemento: " + element,
                        TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Send Keys con paso automatico en el reporte
    public static void senKeys(WebElement element, String dato, String descripcionDelPaso) {
        if (esperarElemento(element)) {
            try {
                // Se envia el dato al elemento
                element.sendKeys(dato);
                // Se agrega el paso al reporte
                addWebReportImage(descripcionDelPaso, "", Status.PASS);
            } catch (Exception e) {
                addWebReportImage("Error al setear el dato: " + dato + " en el elemento: " + element,
                        TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }

    // Send Keys con paso automatico en el reporte
    public static void senKeys(WebElement element, Keys tecla, String descripcionDelPaso) {
        if (esperarElemento(element)) {
            try {
                // Se envia el dato al elemento
                element.sendKeys(tecla);
                // Se agrega el paso al reporte
                addWebReportImage(descripcionDelPaso, "", Status.PASS);
            } catch (Exception e) {
                addWebReportImage("Error al setear el dato: " + tecla + " en el elemento: " + element,
                        TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Envia datos a un element de tipo input pero solo con un intento con tiempo determinado de espera
    public static void senKeys(WebElement element, String dato, int segundos) {
        if (visualizarObjeto(element, segundos)) {
            try {
                element.sendKeys(dato);
            } catch (Exception e) {
                addWebReportImage("Error al setear el dato: " + dato + " en el elemento: " + element,
                        TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Limpia un element de tipo input
    public static void clear(WebElement element) {
        if (esperarElemento(element)) {
            try {
                element.clear();
            } catch (Exception e) {
                addWebReportImage("Error al realizar clear en el elemento " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReportImage("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Limpia un element de tipo input pero solo con un intento con tiempo determinado de espera
    public static void clear(WebElement element, int segundos) {
        if (visualizarObjeto(element, segundos)) {
            try {
                element.clear();
            } catch (Exception e) {
                addWebReport("Error al realizar clear en el elemento " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Obtiene el texto contenido dentro de un elemento
    public static String getText(WebElement element) {
        String dato = null;
        if (esperarElemento(element)) {
            try {
                dato = element.getText();
            } catch (Exception e) {
                addWebReport("Error al obtener el texto del elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
        return dato;

    }


    // Obtiene el texto contenido dentro de un elemento pero solo con un intento con tiempo determinado de espera
    public static String getText(WebElement element, int segundos) {
        String dato = null;
        if (visualizarObjeto(element, segundos)) {
            try {
                dato = element.getText();
            } catch (Exception e) {
                addWebReport("Error al obtener el texto del elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
        return dato;

    }


    // Selecciona un elemento de tipo select con la opcion del texto visible
    public static void selectByVisibleText(WebElement element, String dato) {
        if (esperarElemento(element)) {
            try {
                Select select = new Select(element);
                select.selectByVisibleText(dato);
            } catch (Exception e) {
                addWebReport("Error al obtener el texto del elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Selecciona un elemento de tipo select con la opcion de seleccionar por index
    public static void selectByIndex(WebElement element, int index) {
        if (esperarElemento(element)) {
            try {
                Select select = new Select(element);
                select.selectByIndex(index);
            } catch (Exception e) {
                addWebReport("Error al obtener el texto del elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Selecciona un elemento de tipo select con la opcion de seleccionar por valor
    public static void selectByValue(WebElement element, int index) {
        if (esperarElemento(element)) {
            try {
                Select select = new Select(element);
                select.selectByIndex(index);
            } catch (Exception e) {
                addWebReport("Error al obtener el texto del elemento: " + element, TRAZA + e, Status.FAIL);
            }
        } else addWebReport("No se pudo encontrar el elemento: " + element, "", Status.FAIL);
    }


    // Espera
    public static void esperar(int segundos) {
        System.out.println("Se pausa la ejecucion por " + segundos + " segundos.");
        getDriver().manage().timeouts().implicitlyWait(segundos, TimeUnit.SECONDS);
    }


    // Hace un scroll hacia abajo una cantidad determinada de pixeles dentro de la seccion actual seleccionada por selenium
    public static void scrollDown(int pixeles) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverContext.getDriver();
            js.executeScript("window.scrollBy(0," + pixeles + ")");
        } catch (Exception e) {
            addWebReport("Ocurrio un error al intentar hacer scroll hacia abajo", TRAZA + e, Status.FAIL);
        }
    }


    // Hace un scroll hacia arriba una cantidad determinada de pixeles dentro de la seccion actual seleccionada por selenium
    public static void scrollUp(int pixeles) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverContext.getDriver();
            js.executeScript("window.scrollBy(0," + pixeles * -1 + ")");
        } catch (Exception e) {
            addWebReport("Ocurrio un error al intentar hacer scroll hacia abajo", TRAZA + e, Status.FAIL);
        }
    }


    // Realiza un scroll hasta encontrar el elemento seleccionado
    public static void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverContext.getDriver();
            js.executeScript("arguments[0].scrollIntoView();", element);
        } catch (Exception e) {
            addWebReport("Ocurrio un error al intentar hacer scroll hacia el elemento: " + element, TRAZA + e, Status.FAIL);
        }

    }


}
