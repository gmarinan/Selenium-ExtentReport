import java.util.List;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import config.base.BaseAPI;
import config.reporteWeb.HtmlReport;
import utils.MetodosGenericos;

public class pruebas extends BaseAPI{

    @Test(groups = {"hola"}, description = "test de busqueda")
    public void prueba() throws ClassNotFoundException {
        List<String> respuesta = MetodosGenericos.consultaGenerica("SELECT * FROM tabla_prueba;","jdbc:mysql://localhost:3306/pruebas","root","");
        
        HtmlReport.addWebReport("Paso", HtmlReport.insertarTabla(respuesta), Status.PASS);
    }
}
