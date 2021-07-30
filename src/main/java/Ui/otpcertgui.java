package Ui;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
//import com.sun.org.apache.bcel.internal.generic.AALOAD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileWriter;   // Import the FileWriter class


public class otpcertgui extends javax.swing.JFrame {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    static String beneficiarycert() throws UnirestException, NoSuchAlgorithmException, ParseException {
        //otp registration
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter mobile no ");
        String mobile = sc.next();

        String bodi = "{\r\n    \"mobile\": \"" + mobile + "\"\r\n}";
        System.err.println(bodi);
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://cdn-api.co-vin.in/api/v2/auth/public/generateOTP")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(bodi)
                .asString();
        System.out.println(response.getStatus());
        System.out.println(response.getBody());
        //txnid
        Object obj = new JSONParser().parse(response.getBody());
        JSONObject json = (JSONObject) obj;
        String txn_id = (String) json.get("txnId");
        System.out.print("Enter otp");
        String otp = sc.next();
        String otphash = toHexString(getSHA(otp));
        Unirest.setTimeouts(0, 0);
        String bodi2 = "{\n    \"otp\": \"" + otphash + "\",\n    \"txnId\": \"" + txn_id + "\"\n}";
        System.err.println(bodi2);
        HttpResponse<String> response1 = Unirest.post("https://cdn-api.co-vin.in/api/v2/auth/public/confirmOTP")
                .header("accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")

                .header("Content-Type", "application/json")
                .body(bodi2)
                .asString();
        System.out.println(response1.getStatus());
        System.out.println(response1.getBody());
        Object obj1 = new JSONParser().parse(response1.getBody());
        JSONObject json1 = (JSONObject) obj1;
        String token = (String) json1.get("token");
        return token;

    }

    static void Downloadcertificate() throws UnirestException, NoSuchAlgorithmException, ParseException {
        String token = beneficiarycert();
        Unirest.setTimeouts(0, 0);
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter beneficiary id ");
        String s = sc.next();
        HttpResponse<String> response = Unirest.get("https://cdn-api.co-vin.in/api/v2/registration/certificate/public/download?beneficiary_reference_id=" + s)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")

                .header("accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .asString();
        System.out.println(response.getStatus());
        System.out.println(response.getBody());
        try {
            File myObj = new File("cert.pdf");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("cert.pdf");
            myWriter.write(response.getBody());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }


    public static void main(String args[]) throws UnirestException, NoSuchAlgorithmException, ParseException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(otpcertgui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(otpcertgui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(otpcertgui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(otpcertgui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        Downloadcertificate();


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new otpcertgui().setVisible(true);
            }
        });
    }
}