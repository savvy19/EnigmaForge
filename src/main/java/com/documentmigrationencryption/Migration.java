package com.documentmigrationencryption;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.documentmigrationencryption.enigmaforge.EnigmaForge;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Component
public class Migration {

    static List<String> list = new ArrayList<>();
    public static void decryptPdf(String inputPath, String outputPath, String password) throws IOException {
        try {
            InputStream inputStream = Files.newInputStream(Paths.get(inputPath));
            OutputStream outputStream = Files.newOutputStream(Paths.get(outputPath));
            PdfReader reader = new PdfReader(inputStream, password.getBytes());
            PdfStamper stamper = new PdfStamper(reader, outputStream);

            for(int i = 1; i <= reader.getNumberOfPages(); ++i) {
                PdfImportedPage page = stamper.getImportedPage(reader, i);
                PdfContentByte canvas = stamper.getOverContent(i);
                canvas.addTemplate(page, 0.0F, 0.0F);
            }

            stamper.close();
            reader.close();
        } catch (DocumentException var10) {
            throw new RuntimeException(var10);
        }
    }

    public static String extractTextFromPdf(String outputPath) throws IOException {
        PdfReader reader = new PdfReader(outputPath);
        String text = PdfTextExtractor.getTextFromPage(reader, 1);
        reader.close();
        String[] arrOfStr = text.split(" ", 2);
        return arrOfStr[0];
    }

    public static String get_key_value(String password, String inputPath) throws IOException {
        String outputPath = inputPath.replace("material_encrypted.pdf", "material.pdf");
        File file = new File(inputPath);
        if (file.exists()) {
            decryptPdf(inputPath, outputPath, password);
            String key = extractTextFromPdf(outputPath);
            File key_file = new File(outputPath);
            key_file.delete();
            return key;
        }
        else {
            return null;
        }
    }
    public static void consoleRunner() throws Exception {

        String aesKey,bfKey,desKey;
        Scanner scanner = new Scanner(System.in);
        HashMap<String,String> encrypKeys;
        ThreeLevelEncryption threeLevelEncryption = new ThreeLevelEncryption();
        EnigmaForge enigmaForge =new EnigmaForge();
        do {
        // String  applicationName ="AppX" or any product that is there; //take input on this
        System.out.print("Enter application name:");
        String applicationName = scanner.next();

        // int compId = 1 ;   //Company.findByCompanyId(companyId)?.id;   //take input on this
        System.out.print("Enter company ID:");
        int compId = scanner.nextInt();
        //D:\fileLocation\REP00000001\CaseMaster\CRI00000141\CaseDocuments
        System.out.print("Enter folder path:");
        String folderPath = scanner.next();

        System.out.print("Enter Mode in Capital: ");
        String mode = scanner.next();




        start(folderPath);


        encrypKeys = ApplicationPropertiesReader.get_details();

        String worpspaceString = encrypKeys.get("Product.PDF_PATH_WORKSPACE") + encrypKeys.get("ENCRYPTION_KEY_FILE")+compId+encrypKeys.get("ENCRYPTION_FILE_EXTENSION");
        String baseString = encrypKeys.get("Product.PDF_PATH_BASE") +  encrypKeys.get("ENCRYPTION_KEY_FILE")+compId+encrypKeys.get("ENCRYPTION_FILE_EXTENSION");
        String warString = encrypKeys.get("Product.PDF_PATH_WAR") +  encrypKeys.get("ENCRYPTION_KEY_FILE")+compId+encrypKeys.get("ENCRYPTION_FILE_EXTENSION");

        switch(mode){
            case "ENCRYPT":
            {
                aesKey = new String(enigmaForge.generateAesKey());
                bfKey = new String(enigmaForge.generateBfKey());
                desKey = new String(enigmaForge.generateDesKey());
               /* aesKey = new String(EnigmaForge.generate_aes_Key());
                bfKey = new String(EnigmaForge.generate_bf_Key());
                desKey = new String(EnigmaForge.generate_des_Key());*/
                threeLevelEncryption.password_pdf(compId, applicationName ,worpspaceString,baseString,warString, aesKey, bfKey, desKey);
                //User com.document_migration.service.Migration call here.

                for (String filepath : list) {

                    ThreeLevelEncryption.encrypt_file(filepath, aesKey, bfKey, desKey);

                }
                break;
            }
            case "DECRYPT":
            {
                HashMap<String,String> encryptionKey ,locationKey;
                encryptionKey= threeLevelEncryption.ENCRYPTION_KEYS(compId);
                locationKey = threeLevelEncryption.pdfLocation(compId);
                aesKey=null;
                bfKey=null;
                desKey=null;
                //    aesKey = Objects.requireNonNull(threeLevelEncryption.get_key_value(encryptionKey.get("ENCRYPTION_KEYS_AES"), configHolder.product.workspace.encryption.key.get(locationKey.get("AES_LOC").toString()).toString() + encrypKeys.get("ENCRYPTION_KEY_FILE") + compId + encrypKeys.get("ENCRYPTION_FILE_EXTENTION"),companyId));
            //    bfKey = Objects.requireNonNull(threeLevelEncryption.get_key_value(encryptionKey.get("ENCRYPTION_KEYS_BLOWFISH"), configHolder.product.workspace.encryption.key.get(locationKey.get("BLOWFISH_LOC").toString()).toString() + encrypKeys.get("ENCRYPTION_KEY_FILE") + compId + encrypKeys.get("ENCRYPTION_FILE_EXTENTION"),companyId));
             //   desKey = Objects.requireNonNull(threeLevelEncryption.get_key_value(encryptionKey.get("ENCRYPTION_KEYS_TRIPLEDES"), configHolder.product.workspace.encryption.key.get(locationKey.get("TRIPLEDES_LOC").toString()).toString() + encrypKeys.get("ENCRYPTION_KEY_FILE") + compId + encrypKeys.get("ENCRYPTION_FILE_EXTENTION"),companyId));


                for (String filepath : list) {

                    ThreeLevelEncryption.decrypt_file(filepath, aesKey, bfKey, desKey);

                }
                break;

            }
            default:
            {
                System.out.println("please enter valid mode");
            }

        }
        System.out.print("Do you want to perform another operation? (yes/no): ");
    } while (scanner.next().equalsIgnoreCase("yes"));

    System.out.println("Program ended.");
    scanner.close();

    }
    public static void encrypt_file(String inputFile, String aesKey,String blowfishKey, String tripledesKey) throws Exception {
        File file = new File(inputFile);
        byte[] bytes = Files.readAllBytes(file.toPath());
        byte [] encry = EnigmaForge.encrypt(bytes, aesKey.getBytes(), blowfishKey.getBytes(), tripledesKey.getBytes());

        Path path = Paths.get(inputFile);
        Files.write(path, encry);

    }
    public static void decrypt_file(String inputFile, String aeskey, String blowfishkey, String tripledesKey) throws Exception {
        File file = new File(inputFile);
        byte[] bytes = Files.readAllBytes(file.toPath());

        byte [] decry = EnigmaForge.decrypt(bytes, aeskey.getBytes(), blowfishkey.getBytes(), tripledesKey.getBytes());

        Path path = Paths.get(inputFile);
        Files.write(path, decry);

    }

    public static void start(String folderPath){
        File folder = new File(folderPath);
        getAllFileList(folder);
    }

    public static void getAllFileList(File folder) {
        for (String fileName : Objects.requireNonNull(folder.list())) {
            File file = new File(folder.getPath() + "/" + fileName);
            if (file.isFile()) {
                list.add(file.getPath());
            } else {
                getAllFileList(file);
            }
        }

    }
}

