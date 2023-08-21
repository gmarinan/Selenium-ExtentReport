package utils;

import config.reporteWeb.HtmlReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtilis {

    private final List<String> fileList;
    private static String OUTPUT_ZIP_FILE = "Folder.zip";
    // private static String SOURCE_FOLDER = System.getProperty("user.dir")+ "\\test\\resources\\reports\\"; // SourceFolder path
    private static String SOURCE_FOLDER = System.getProperty("user.dir")+ "HTMLREPORTS\\reports\\"; // SourceFolder path
    private static String zipDate;

    public ZipUtilis(String output) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();
        zipDate = dateFormat.format(date);
        validarRuta(output);
        //Ruta usuario
        //OUTPUT_ZIP_FILE = System.getProperty("user.home")+"\\"+output+"\\"+HtmlReport.getSuite()+"_"+zipDate+".zip";
        //Ruta C:\
        OUTPUT_ZIP_FILE = output+"\\"+ HtmlReport.getSuite()+"_"+zipDate+".zip";
        System.out.println("OUTPUT: "+ OUTPUT_ZIP_FILE);
        SOURCE_FOLDER = HtmlReport.getReportPath() + HtmlReport.getSuite();
        System.out.println("SOURCE: "+ SOURCE_FOLDER);
        fileList = new ArrayList<>();
    }
    public void generarReporte(){
        generateFileList(ZipUtilis.getSource());
        zipIt(ZipUtilis.getOutput());
    }
    public void validarRuta(String dir){
        System.out.println(dir);

        File ruta = new File(dir);
        if (!ruta.exists()) {
            try {
                System.out.println("Generando ruta: " + dir);
                ruta.mkdirs();
                ruta.mkdir();
            } catch (Exception e) {
                System.out.println("Error al crear el directorio");
                e.printStackTrace();
            }
        }
    }

    public static File getSource(){
        return new File(SOURCE_FOLDER);
    }
    public static String getOutput(){
        return OUTPUT_ZIP_FILE;
    }
    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Creando zip : " + zipFile);

            for (String file: this.fileList) {
                System.out.println("Comprimiendo : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                
                
                FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                int len;
                while ((len = in .read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }

            zos.closeEntry();
            System.out.println("Carpeta de reporte comprimida exitosamente.");
            
            zos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}

