package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static config.driver.DriverContext.getDriver;
import static config.reporteWeb.HtmlReport.addErrorMessage;
import static config.reporteWeb.HtmlReport.fail;

public class Web {

    public static boolean visualizarObjeto(WebElement element, int timeout) {
        try {
            System.out.println("Valida si es visible el elemento: [ " + element + " ]");
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOf(element));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            System.out.println("\t>>> elemento visible.");
            return true;
        } catch (Exception e) {
            System.out.println("\t>>> elemento NO visible.");
            return false;
        }
    }

    public static void scrollDown() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void cambiarPestanaDerecha() {
        ArrayList<String> tabs = new ArrayList<String>(getDriver().getWindowHandles());
        System.out.println("cantidad pestanas>>> " + tabs.size());
        getDriver().switchTo().window(tabs.get(1));
    }

    public static boolean validaElemento(WebElement objecto) {
        boolean estado = false;
        boolean elemento = false;
        int intentos = 0;
        do {
            elemento = esperarElemento(objecto);

        } while (elemento && (intentos < 10));

        if (!elemento) {
            Assert.fail("Elemento no disponible");

        } else {
            estado = true;
            Assert.assertTrue(estado);
        }

        return estado;
    }
    public static boolean esperarElemento(WebElement objeto) {
        return esperarElemento(objeto, true);
    }

    public static boolean esperarElemento(WebElement objeto, boolean fatal) {
        boolean existe = false;
        int intentos = 0;
        while (!existe & intentos < 10) {
            System.out.println("Esperando... [ " + (intentos + 1) + " de 10 intentos]");
            existe = visualizarObjeto(objeto, 5);
            if (!existe)
                ++intentos;
        }

        if (intentos >= 10 && fatal) {
            addErrorMessage("No se pudo encontrar el WebElement");
            fail("No se pudo encontrar el WebElement" + objeto);
            Assert.fail("[ Elemento no encontrado: " + objeto + " ]");
        }

        return existe;
    }

    public static boolean esperaImplicita(WebElement objeto) {
        try {
            System.out.println("Esperando visualizar elemento: [" + objeto + " ]");
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            System.out.println ("\t>>> elemento visible.");
            return true;
        } catch (Exception e) {
            System.out.println("\t>>> elemento NO visible.");
            return false;
        }
    }

    public static boolean esperaElementoImplicita(WebElement objeto) {
        int intentos = 1;
        while (intentos <= 3) {
            System.out.println("Esperando... [ " + intentos + " de 3 ]");
            if (!esperaImplicita(objeto))
                intentos++;
            else
                return true;
        }
        return false;
    }

    public static boolean isEnabled(WebElement element) throws NoSuchElementException {
        System.out.println("Comprobaci\u00F3n atributo 'enabled' del elemento: " + element + "]");
        boolean enabled = element.getAttribute("enabled").trim().equals("true");
        System.out.println("\t >>> elemento " + (enabled ? "habilitado" : "deshabilitado"));
        return enabled;
    }

    public static boolean validarEnable(WebElement objeto, int segundos) {
        System.out.println("Se validar\u00E1 elemento: [" + objeto + "] se encuentre habilitado en " + segundos + " segundos.");
        int milisegundos = segundos * 1000;
        boolean habilitado = false;
        for (int i = 1; i < 10; ++i) {
            try {
                if (isEnabled(objeto)) {
                    habilitado = true;
                    break;
                } else {
                    Thread.sleep(milisegundos);
                }
            } catch (Exception e) {
                System.out.println("Ocurri\u00F3 una excepci\u00F3n durante la validaci\u00F3n del atributo 'enabled', mas detalles: " + e.getMessage());
                break;
            }
        }
        return habilitado;
    }

    public static void waitVisibility(WebDriver driver, WebElement element, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitClickable(WebDriver driver, WebElement element, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitSelected(WebDriver driver, WebElement element, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.elementToBeSelected(element));
    }

    public static void waitAlert(WebDriver driver, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.alertIsPresent());
    }

    public static void waitVisibilitys(WebDriver driver, List<WebElement> elements, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public static void waitInvisibility(WebDriver driver, WebElement element, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public static void elementosInvisibles(WebDriver driver, By element, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(element));
    }

    public static void notTextToBePresentInElement(WebDriver driver, String mensaje, WebElement elemento, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(elemento, mensaje)));
    }

    public static void textToBePresentInElement(WebDriver driver, String mensaje, WebElement elemento, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.textToBePresentInElement(elemento, mensaje));
    }

}

