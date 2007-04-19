/*
 * This file is part of GatorMail, a web based email client.
 *
 * Copyright (C) 2007 The Open Systems Group / University of Florida
 *
 * GatorMail is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GatorMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GatorMail; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ufl.osg.gatormail.server.state;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

/**
 * Servlet that intializes the webapp's {@link edu.ufl.osg.gatormail.server.state.PrivateStateCipher}.
 *
 * @author Sandy McArthur
 */
public class DefaultPrivateStateEncoderServlet extends HttpServlet {

    private static final int BASE64_OPTIONS = Base64.DONT_BREAK_LINES | Base64.URL_SAFE;

    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        final DefaultPrivateStateCipher dpsc = new DefaultPrivateStateCipher(getSalt(servletConfig), getIterationCount(servletConfig), getPassword(servletConfig));

        // test to make sure the current settings work
        try {
            final Serializable test = new TestObject();
            final String encoded = dpsc.doEncode(test);
            final Serializable test2 = (Serializable)dpsc.doDecode(encoded);
            if (test.equals(test2)) {
                servletConfig.getServletContext().setAttribute(PrivateStateCipher.class.getName(), dpsc);
            } else {
                throw new ServletException("PrivateStateCipher did not encode and decode correctly.");
            }
            
        } catch (GeneralSecurityException e) {
            log("Problem with the encryption paramters.", e);
            throw new ServletException("Problem with the encryption paramters.", e);

        } catch (ServletException se) {
            // no need to wrap this
            throw se;

        } catch (Exception e) {
            // other shouldn't ever happen here
            log(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    private byte[] getSalt(final ServletConfig servletConfig) {
        final String gmSalt;
        final String key = "gm.salt";
        gmSalt = fetchParameter(key, servletConfig);

        final byte[] salt;
        if (gmSalt != null) {
            salt = gmSalt.getBytes();
        } else {
            log("INFO: gm.salt property not set. Generating new salt. Attachment links will not work across server restarts.");
            final Random r = new Random();
            final int size = 8;
            salt = new byte[size];
            r.nextBytes(salt);
        }
        return salt.clone();
    }

    private int getIterationCount(final ServletConfig servletConfig) {
        final String gmCount = fetchParameter("gm.count", servletConfig);

        int saltCount = -1;
        if (gmCount != null) {
            try {
                saltCount = Integer.parseInt(gmCount);
            } catch (NumberFormatException nfe) {
                log("INFO: gm.count property is not a parsable number: " + gmCount);
                nfe.printStackTrace();
            }
        }
        if (saltCount < 0) {
            log("INFO: gm.count property not set. Generating new count. Attachment links will not work across server restarts.");
            saltCount = (int)(Math.random() * 25) + 8;
        }
        return saltCount;
    }

    private char[] getPassword(final ServletConfig servletConfig) {
        final String gmPassword = fetchParameter("gm.password", servletConfig);


        final char[] password;
        if (gmPassword != null) {
            password = gmPassword.toCharArray();
        } else {
            log("INFO: gm.password property not set. Generating new password. Attachment links will not work across server restarts.");
            final Random r = new Random();
            final int size = r.nextInt(4) + 8;
            password = new char[size];
            for (int i=0; i < password.length; i++) {
                password[i] = (char)(r.nextInt(94) + 32); // needs to be in the ascii range
            }
        }
        return password;
    }

    private static String fetchParameter(final String key, final ServletConfig servletConfig) {
        final String value;
        if (servletConfig.getServletContext().getInitParameter(key) != null) {
            value = servletConfig.getServletContext().getInitParameter(key);
        } else {
            value = System.getProperty(key);
        }
        return value;
    }

    private static class DefaultPrivateStateCipher implements PrivateStateCipher {
        private final byte[] salt;
        private final int count;
        private final char[] password;

        public DefaultPrivateStateCipher(final byte[] salt, final int count, final char[] password) {
            this.salt = salt;
            this.count = count;
            this.password = password;
        }

        private Cipher initCipher(final int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
            // XXX: Some of this is cacheable, optimze it someday
            final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
            final SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

            // Create PBE Cipher
            final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(mode, pbeKey, pbeParamSpec);
            return pbeCipher;
        }

        public String encode(final Serializable obj) {
            try {
                return doEncode(obj);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        private String doEncode(final Serializable obj) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
            final Cipher pbeCipher = initCipher(Cipher.ENCRYPT_MODE);

            // Our cleartext
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            final byte[] cleartext = baos.toByteArray();

            // Encrypt the cleartext
            final byte[] ciphertext = pbeCipher.doFinal(cleartext);


            return Base64.encodeBytes(ciphertext, BASE64_OPTIONS);
        }

        public Object decode(final String encodedToken) {
            try {
                return doDecode(encodedToken);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        private Object doDecode(final String encodedToken) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException {
            final byte[] ciphertext = Base64.decode(encodedToken, BASE64_OPTIONS);

            final Cipher pbeCipher = initCipher(Cipher.DECRYPT_MODE);

            final byte[] cleartext = pbeCipher.doFinal(ciphertext);

            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cleartext));

            return ois.readObject();
        }
    }

    public static void main(final String[] args) throws Exception {
        final byte[] salt = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
        final DefaultPrivateStateCipher psc = new DefaultPrivateStateCipher(salt, 12, "abcdefh".toCharArray());
        final TestObject testObject = new TestObject();
        final String c = psc.doEncode(testObject);
        System.out.println("c.length: " + c.length());
        System.out.println("c: " + c);
        System.out.println("Before: " + testObject);
        final Object bar = psc.doDecode(c);
        System.out.println(" After: " + bar);
    }

    private static class TestObject implements Serializable {
        private String username = "12345678" + Math.random();
        private String password = "87654321" + Math.random();
        private String folder = "INBOX" + Math.random();
        private String message = "cid124e3463456536t34" + Math.random();

        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TestObject that = (TestObject)o;

            if (!folder.equals(that.folder)) return false;
            if (!message.equals(that.message)) return false;
            if (!password.equals(that.password)) return false;
            return username.equals(that.username);

        }

        public int hashCode() {
            int result;
            result = username.hashCode();
            result = 31 * result + password.hashCode();
            result = 31 * result + folder.hashCode();
            result = 31 * result + message.hashCode();
            return result;
        }

        public String toString() {
            return "TestObject{" +
                    "folder='" + folder + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
