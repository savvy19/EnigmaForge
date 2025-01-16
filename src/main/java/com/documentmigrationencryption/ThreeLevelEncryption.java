package com.documentmigrationencryption;

import com.documentmigrationencryption.enigmaforge.EnigmaForge;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ThreeLevelEncryption {
    public static void password_pdf(int company_id, String product, String workspace, String base, String war, String aesKey,String bfKey,String desKey) throws Exception {
        String aes = null, bf= null, tdes=null, aes_loc, bf_loc, tdes_loc;
        switch (product) {
            case "AppX":
             /*   aes = "lIT" + "(" + (company_id * 100) + "KOM" + (company_id * 9) + ")";
                bf = "l__o_k" + "^" + (company_id * -98) + " ti_m___" + company_id * 12 + "~";
                tdes = (company_id * 42) + "k__O_--L  t___  i_m___124" + company_id * -3 + "~";
            */
                aes = "lIT" + "(" + "100" + "KOM" + "9" + ")";
                bf = "l__o_k" + "^" + "-98" + " ti_m___" + "12" + "~";
                tdes = "42" + "k__O_--L  t___  i_m___124" + "-3" + "~";
                break;
            case "AppY":
                aes = "fY" + "KOM" + (company_id * 7) + "lIf" + (company_id * -3);
                bf = "k" + (company_id*8) + "life" + (company_id * -12) + "@~ fly";
                tdes = "fyy" + (company_id*-9) + "kom'!@#*+_" + company_id*-1  + "g";
                break;
            case "AppZ":
                aes = "KoM" + "@" + (company_id * 2) + "aCT~" + (company_id * 5) + "^";
                bf = "Act" + (company_id * 9) + "mok" + (company_id * -4) + "!tr" + (company_id * 3)  + "=";
                tdes = "0~092)"  +  company_id + "tract" + "0172" + "kOM" + "(" + (company_id*425);
                break;
            default:
                new Exception("Please provide correct product name");
                break;

        }
        /*switch (company_id % 6) {
            case 0:
                aes_loc = workspace;
                bf_loc = base;
                tdes_loc = war;
                break;
            case 1:
                aes_loc = war;
                bf_loc = base;
                tdes_loc = workspace;
                break;
            case 2:
                aes_loc = workspace;
                bf_loc = war;
                tdes_loc = base;
                break;
            case 3:
                aes_loc = base;
                bf_loc = workspace;
                tdes_loc = war;//"E:\\WAR\\material.pdf";
                break;
            case 4:
                aes_loc = war;
                bf_loc = workspace;
                tdes_loc = base;
                break;
            default:
                aes_loc = base;
                bf_loc = war;
                tdes_loc = workspace;
                break;
        }*/
        aes_loc = war;
        bf_loc = base;
        tdes_loc = workspace;
        // AES
        // createPdf(new String(EnigmaForge.generate_aes_Key()), aes_loc, aes);
        createPdf(aesKey, aes_loc, aes);
        // BLOWFISH
        //  createPdf(new String(EnigmaForge.generate_bf_Key()), bf_loc , bf);
        createPdf(bfKey, bf_loc , bf);
        // Triple DES
        //  createPdf(new String(EnigmaForge.generate_des_Key()), tdes_loc, tdes);
        createPdf(desKey, tdes_loc, tdes);
    }
    public static void createPdf(String base64String, String filepath, String password)     {
        try {
            System.out.println(password);
            Document document = new Document();
            PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(filepath)));
            document.open();
            Paragraph paragraph = new Paragraph(base64String);
            document.add(paragraph);
            document.close();
            PdfReader reader = new PdfReader(filepath);
            PdfStamper stamper = new PdfStamper(reader, Files.newOutputStream(Paths.get(filepath.replace("material","material_encrypted"))));
            stamper.setEncryption(password.getBytes(), password.getBytes(), 2052, 2);
            stamper.close();
            reader.close();
            File file = new File(filepath);
            file.delete();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

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

    public static void encrypt_file(String inputFile, String aesKey,String blowfishKey, String tripledesKey) throws Exception {
        File file = new File(inputFile);
        byte[] bytes = Files.readAllBytes(file.toPath());
        byte [] encry = com.documentmigrationencryption.enigmaforge.EnigmaForge.encrypt(bytes, aesKey.getBytes(), blowfishKey.getBytes(), tripledesKey.getBytes());
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

    /**
     * @param company_id
     * <p>This action is to creates the encrypted passwords dynamically
     *  for pdf's which are going to store the encrypted keys.</p>
     * @returns HashMap
     * @author Souvik Das
     */
    public static HashMap<String,String> ENCRYPTION_KEYS(long company_id)
    {
        HashMap<String,String> enKeys= new HashMap<String,String>();
        String aes = "lIT" + "(" + (company_id * 100) + "KOM" + (company_id * 9) + ")";
        String bf = "l__o_k" + "^" + (company_id * -98) + " ti_m___" + company_id * 12 + "~";
        String tdes = (company_id * 42) + "k__O_--L  t___  i_m___124" + company_id * -3 + "~";



        enKeys.put("ENCRYPTION_KEYS_AES",aes );
        enKeys.put("ENCRYPTION_KEYS_BLOWFISH",bf );
        enKeys.put("ENCRYPTION_KEYS_TRIPLEDES",tdes );

        System.out.println(aes);
        System.out.println(bf);
        System.out.println(tdes);
        return enKeys;
    }
    /**
     * @param company_id
     * <p>This action is to calculates/decides the path where the PDF location will be </p>
     * @returns HashMap
     * @author Souvik Das
     */
    public static HashMap<String,String> pdfLocation (long company_id)
    {
        HashMap<String,String> locKeys= new HashMap<String,String>();
        String aes_loc, bf_loc, tdes_loc;
        switch ((int)company_id % 6) {
            case 0:
                aes_loc = "workspace";
                bf_loc = "base";
                tdes_loc = "war";
                break;
            case 1:
                aes_loc = "war";
                bf_loc = "base";
                tdes_loc = "workspace";
                break;
            case 2:
                aes_loc = "workspace";
                bf_loc = "war";
                tdes_loc = "base";
                break;
            case 3:
                aes_loc = "base";
                bf_loc = "workspace";
                tdes_loc = "war";//"E:\\WAR\\material.pdf";
                break;
            case 4:
                aes_loc = "war";
                bf_loc = "workspace";
                tdes_loc = "base";
                break;
            default:
                aes_loc = "base";
                bf_loc = "war";
                tdes_loc = "workspace";
                break;
        }

        locKeys.put("AES_LOC",aes_loc );
        locKeys.put("BLOWFISH_LOC",bf_loc );
        locKeys.put("TRIPLEDES_LOC",tdes_loc );

        System.out.println("AES_LOC"+aes_loc);
        System.out.println("BLOWFISH_LOC"+bf_loc);
        System.out.println("TRIPLEDES_LOC"+tdes_loc);
        return locKeys;
    }

}
