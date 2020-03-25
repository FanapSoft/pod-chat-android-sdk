package com.example.chat.application.chatexample.token;

import android.util.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Converts a private or public RSA key from the XML Security format (used also
 * in .NET) to the RSA key format in PEM format.
 * <p>
 * The output key is written on standard output.
 * <p>
 * In case of private key, the output format is the "traditional" format of the
 * key, i.e. NOT the newer PKCS#8 format. The output keys are unencrypted. To
 * convert to PKCS#8 and/or encrypt the private key, use openssl with the
 * -topk8 option:
 * openssl pkcs8 -topk8 -in privkey_rsa.pem -out privkey.pem
 * <p>
 * Created by matous.borak@platanus.cz, 2008.
 * See http://www.platanus.cz/blog/xml_key_to_pem_en.html
 * <p>
 * Heavily inspired by the PvkConvert utility made by Michel Gallant, see
 * http://www.jensign.com/JavaScience/PvkConvert/. Thanks for it!
 */
public class XMLSec2PEM {

    private static final int PRIVATE_KEY = 1;
    private static final int PUBLIC_KEY = 2;
    private static final String[] PRIVATE_KEY_XML_NODES = {"Modulus", "Exponent", "P", "Q", "DP", "DQ", "InverseQ", "D"};
    private static final String[] PUBLIC_KEY_XML_NODES = {"Modulus", "Exponent"};

   /* public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Usage:\n  java XMLSec2PEM <XMLSecurityRSAKeyValueFile.xml>");
            System.exit(0);
        }

        try {

            Document XMLSecKeyDoc = parseXMLFile(args[0]);
            System.out.print("Determining the key type: ");
            int keyType = getKeyType(XMLSecKeyDoc);
            if (keyType == PRIVATE_KEY || keyType == PUBLIC_KEY) {
                System.out.println("seems to be a " + (keyType == PRIVATE_KEY ? "private" : "public") + " XML Security key");
            } else {
                System.exit(1);
            }

            System.out.print("Checking the XML file structure: ");
            if (checkXMLRSAKey(keyType, XMLSecKeyDoc)) {
                System.out.println("OK");
            } else {
                System.exit(1);
            }

            String pem = "";
            System.out.println("Outputting the resulting key:\n");
            if (keyType == PRIVATE_KEY) {
                pem = convertXMLRSAPrivateKeyToPEM(XMLSecKeyDoc);
                System.out.println("-----BEGIN PRIVATE KEY-----");
                System.out.println(pem);
                System.out.println("-----END PRIVATE KEY-----");
            } else {
                pem = convertXMLRSAPublicKeyToPEM(XMLSecKeyDoc);
                System.out.println("-----BEGIN PUBLIC KEY-----");
                System.out.println(pem);
                System.out.println("-----END PUBLIC KEY-----");
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }*/

    private static int getKeyType(Document xmldoc) {

        Node root = xmldoc.getFirstChild();
        if (!root.getNodeName().equals("RSAKeyValue")) {
            System.out.println("Expecting <RSAKeyValue> node, encountered <" + root.getNodeName() + ">");
            return 0;
        }
        NodeList children = root.getChildNodes();
        if (children.getLength() == PUBLIC_KEY_XML_NODES.length) {
            return PUBLIC_KEY;
        }
        return PRIVATE_KEY;

    }

    private static boolean checkXMLRSAKey(int keyType, Document xmldoc) {

        Node root = xmldoc.getFirstChild();
        NodeList children = root.getChildNodes();
        String[] wantedNodes = {};
        if (keyType == PRIVATE_KEY) {
            wantedNodes = PRIVATE_KEY_XML_NODES;
        } else {
            wantedNodes = PUBLIC_KEY_XML_NODES;
        }
        for (int j = 0; j < wantedNodes.length; j++) {
            String wantedNode = wantedNodes[j];
            boolean found = false;
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeName().equals(wantedNode)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Cannot find node <" + wantedNode + ">");
                return false;
            }
        }
        return true;

    }

    public static String convertXMLRSAPrivateKeyToPEM(String xmldoc) {

        Node root = null;
        try {
            root = convertStringToDoc(xmldoc).getFirstChild();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        NodeList children = root.getChildNodes();

        BigInteger modulus = null, exponent = null, primeP = null, primeQ = null,
                primeExponentP = null, primeExponentQ = null,
                crtCoefficient = null, privateExponent = null;

        for (int i = 0; i < children.getLength(); i++) {

            Node node = children.item(i);
            String textValue = node.getTextContent();
            if (node.getNodeName().equals("Modulus")) {
                modulus = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("Exponent")) {
                exponent = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("P")) {
                primeP = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("Q")) {
                primeQ = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("DP")) {
                primeExponentP = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("DQ")) {
                primeExponentQ = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("InverseQ")) {
                crtCoefficient = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("D")) {
                privateExponent = new BigInteger(b64decode(textValue));
            }

        }

        try {

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                    modulus, exponent, privateExponent, primeP, primeQ,
                    primeExponentP, primeExponentQ, crtCoefficient);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            return b64encode(key.getEncoded());

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    private static String convertXMLRSAPublicKeyToPEM(Document xmldoc) {

        Node root = xmldoc.getFirstChild();
        NodeList children = root.getChildNodes();

        BigInteger modulus = null, exponent = null;

        for (int i = 0; i < children.getLength(); i++) {

            Node node = children.item(i);
            String textValue = node.getTextContent();
            if (node.getNodeName().equals("Modulus")) {
                modulus = new BigInteger(b64decode(textValue));
            } else if (node.getNodeName().equals("Exponent")) {
                exponent = new BigInteger(b64decode(textValue));
            }

        }

        try {

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(keySpec);
            return b64encode(key.getEncoded());

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    private static Document parseXMLFile(String filename) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));
            return document;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    private static final String b64encode(byte[] data) {
        String b64str = Base64.encodeToString(data, Base64.DEFAULT).trim();
        return b64str;
    }

    private static final byte[] b64decode(String data) {
        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        return bytes;
    }

    private static Document convertStringToDoc(String key) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document d1 = builder.parse(new InputSource(new StringReader(key)));
        return d1;

    }


}

