package utils;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.*;

import static config.Constants.pathArchivo;

public class Email {


    public static void leerEmail(String host, String user, String password, String asunto)
    {
        try {
            //create properties field
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("imaps"); //pop3
            store.connect(host, user, password);
            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();

            System.out.println("N° de mensajes sin leer : " + emailFolder.getUnreadMessageCount());

            for (int a = 0; a < messages.length; a++) {

                if(!messages[a].isSet(Flags.Flag.SEEN)) {
                    //System.out.println("sin leerrr "+(a+1));

                    if (messages[a].getSubject().contains(asunto)) {
                        toWrite(messages[a]);
                        messages[a].setFlag(Flags.Flag.SEEN, true);
                    }
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void leerMail(String host, String user, String password, String asunto)
    {
        try {
            //create properties field
            Properties properties = new Properties();

            properties.setProperty("mail.pop3.starttls.enable", "false");

            properties.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3.socketFactory.fallback", "false");

            properties.setProperty("mail.pop3.port", "995");
            properties.setProperty("mail.pop3.socketFactory.port", "995");
            //properties.setProperty("mail.pop3.host", host);

            Session emailSession = Session.getInstance(properties);
            //observar que hace
            //emailSession.setDebug(true);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("imaps"); //pop3
            store.connect("imap.gmail.com", user, password);
            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();

            System.out.println("N° de mensajes sin leer : " + emailFolder.getUnreadMessageCount());

            for (int i = 0; i < messages.length; i++) {

                if(!messages[i].isSet(Flags.Flag.SEEN)) {
                    //System.out.println("sin leerrr "+(a+1));

                    if (messages[i].getSubject().contains(asunto)) {
                        toWrite(messages[i]);
                        messages[i].setFlag(Flags.Flag.SEEN, true);
                    }
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void borrar(){
        File fichero = new File(pathArchivo.concat("\\tmp\\archivo.html"));
        if (fichero.delete())
            System.out.println("El fichero ha sido borrado satisfactoriamente");
        else
            System.out.println("El fichero no puede ser borrado o no existe");
    }

    public static void toWrite(Part p) throws IOException, MessagingException {
        Object content = p.getContent();
        FileWriter myWriter = null;
        myWriter = new FileWriter(pathArchivo.concat("\\tmp\\archivo.html"));

        if (content instanceof String)
        {
            myWriter.write((String)content);
            //System.out.println(">>> "+body);
        }
        else if (content instanceof Multipart)
        {
            Multipart mp = (Multipart)content;
            myWriter.write((String) mp.getBodyPart(0).getContent());
                //System.out.println("multi "+mp.getBodyPart(0).getContent());
        }
        myWriter.close();
    }
    public static boolean validaExisteArchivo(String archivo){
        boolean existe = false;
        File carpeta = new File(pathArchivo.concat( "\\descargas" ));

        for (final File f : carpeta.listFiles()) {
            if (f.isFile()) {
                if (f.getName().matches( archivo+".pdf" )) {
                    System.out.println("archivo encontrado");
                    existe = true;
                }else {
                    System.out.println("No existe el archivo en la ruta");
                }
            }
        }
        return existe;
    }


    public String rutSinDV(String rut){
        String[] partesPass = rut.split( "-" );
        String password = partesPass[0];
        return  password;
    }


    public static String downloadEmailAttachments(String userName, String password, String asunto)  {
        String host = "pop.gmail.com";
        String port = "995";
        String attachFiles = "";
        Properties properties = new Properties();
        String saveDirectory = pathArchivo.concat("\\descargas\\");
        // server setting
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);

        // SSL setting
        properties.setProperty("mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.socketFactory.port",
                String.valueOf(port));

        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore("pop3");
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();
            Object content = null;
            for (int i = 0; i < arrayMessages.length; i++) {

                if (arrayMessages[i].getSubject().contains(asunto)) {
                    System.out.println("Mensaje recibido, descargando...");
                    Message message = arrayMessages[i];
                    Address[] fromAddress = message.getFrom();
                    String contentType = message.getContentType();
                    String messageContent = "";

                    if (contentType.contains("multipart")) {
                        // content may contain attachments
                        Multipart multiPart = (Multipart) message.getContent();
                        int numberOfParts = multiPart.getCount();
                        for (int partCount = 0; partCount < numberOfParts; partCount++) {
                            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                // this part is attachment
                                String fileName = part.getFileName();
                                attachFiles += fileName + ", ";
                                part.saveFile(saveDirectory + File.separator + fileName);
                            } else {
                                // this part may be the message content
                                messageContent = part.getContent().toString();
                            }
                        }

                        if (attachFiles.length() > 1) {
                            attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                        }
                    } else if (contentType.contains("text/plain")
                            || contentType.contains("text/html")) {
                            content = message.getContent();
                        if (content != null) {
                            messageContent = content.toString();
                        }
                    }
                    System.out.println("documento pdf descargado: " + attachFiles);
                    //arrayMessages[i].setFlag(Flags.Flag.SEEN, true);
                }

            }
            // disconnect
            folderInbox.close(true);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for pop3.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return attachFiles;
    }

    public static void deleteMessages(String userName, String password, String asunto) {
        String host = "pop.gmail.com";
        String port = "995";
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);

        // SSL setting
        properties.setProperty("mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.socketFactory.port",
                String.valueOf(port));

        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore("pop3"); //imap
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();

            for (int i = 0; i < arrayMessages.length; i++) {
                Message message = arrayMessages[i];
                Address[] subject = message.getFrom();
                System.out.println(">>> "+subject);
                if ( message.toString().contains( asunto ) ) {

                    System.out.println(">>> "+message.getFrom());
                    message.setFlag(Flags.Flag.DELETED, true);
                    //System.out.println("Marked DELETE for message: " + asunto);
                }

            }
            boolean expunge = true;
            folderInbox.close(expunge);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
    }

    public static void borrarMensajes(String userName, String password,String subjectToDelete) {
        Properties properties = new Properties();

        // server setting
        String port = "993";
        String host = "imap.gmail.com";
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", port);

        // SSL setting
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port", String.valueOf(port));



        try {
            Session session = Session.getDefaultInstance(properties);
            // connects to the message store
            Store store = session.getStore("imap");
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            // opens the trash folder
            Folder folderBin = store.getFolder("[Gmail]/All Mail");
            folderBin.open(Folder.READ_WRITE);

            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();

            Map<String, Integer> map = new HashMap<String, Integer>();

            for (int i = 0; i < arrayMessages.length; i++) {
                Message message = arrayMessages[i];
                String subject = message.getSubject();

                if (map.containsKey(subject)) {
                    map.put(subject, map.get(subject) + 1);
                    if (map.get(subject) > 3) {
                        System.out.println(subject);
                    }
                } else {
                    map.put(subject, 1);
                }


                if (subject != null && subject.contains(subjectToDelete)) {
                    message.setFlag(Flags.Flag.DELETED, true); folderInbox.copyMessages(new
                            Message[] {message}, folderBin);
                    System.out.println("Marked DELETE for message: " + subject); }

                System.out.println(subject);

            }

            boolean expunge = true;
            folderInbox.close(expunge);

            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
    }
}
