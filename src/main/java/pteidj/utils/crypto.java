package pteidj.utils;

import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Constants;

import javax.security.auth.callback.CallbackHandler;
import java.io.ByteArrayInputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Andre on 25/01/2016.
 */
public class Crypto {

    public static void main (String[] args){
        String str = "hello";
        Crypto crypto = new Crypto();
        byte[] ciph = crypto.Sign("hello");
        crypto.Verify(ciph,str);


    }

    public void Verify(byte[] toverify, String result){
        String osName = System.getProperty("os.name");

        String pkcs11config = "name=CartaoCidadao" + "\n" + "library=C:/WINDOWS/system32/pteidpkcs11.dll";

        if(osName.startsWith("Windows")) {
            pkcs11config = "name=CartaoCidadao" + "\n" + "library=C:/WINDOWS/system32/pteidpkcs11.dll";
        }
        else{
            pkcs11config = "name=CartaoCidadao" + "\n" + "library=/usr/local/libpteidpkcs11.so";
        }

        byte[] pkcs11configBytes = pkcs11config.getBytes();
        ByteArrayInputStream configStream = new ByteArrayInputStream(pkcs11configBytes);

        Provider p = new SunPKCS11(configStream);
        Security.addProvider(p);

        Signature verifier = null;

        try {

            //PublicKey pub_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            PublicKey pub_key = getAuthenticationPK();
            verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(pub_key);
            verifier.update(result.getBytes());
            boolean ok = verifier.verify(toverify);

        }catch(NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }catch (InvalidKeyException exception){
            exception.printStackTrace();
        }catch(SignatureException exception){
            exception.printStackTrace();
        }




    }

    public byte[] Sign(String tosig){
        try {
            String osName = System.getProperty("os.name");


            String pkcs11config = "name=GemPC" + "\n"
                    + "library=C:/WINDOWS/system32/pteidpkcs11.dll";

            byte[] pkcs11configBytes = pkcs11config.getBytes();
            ByteArrayInputStream configStream = new ByteArrayInputStream(pkcs11configBytes);

            Provider p = new SunPKCS11(configStream);
            Security.addProvider(p);
            CallbackHandler cmdLineHdlr = new com.sun.security.auth.callback.TextCallbackHandler();
            KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", p,
                    new KeyStore.CallbackHandlerProtection(cmdLineHdlr));
            KeyStore ks = builder.getKeyStore();
            String assinaturaCertifLabel = "CITIZEN AUTHENTICATION CERTIFICATE";

            Key key = ks.getKey(assinaturaCertifLabel, null);
            PublicKey pub_key = ks.getCertificate(assinaturaCertifLabel).getPublicKey();

            CK_MECHANISM mechanism = new CK_MECHANISM();
            mechanism.mechanism = PKCS11Constants.CKM_RSA_PKCS;
            mechanism.pParameter = null;



            Signature sig = Signature.getInstance("SHA1withRSA",p);
            sig.initSign((PrivateKey)key) ;
            sig.update(tosig.getBytes());
            byte[] signedHash = sig.sign();


            return signedHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getAuthenticationPK(){
        Provider p = providerInit();

        CallbackHandler cmdLineHdlr = new com.sun.security.auth.callback.TextCallbackHandler();
        KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", p,
                new KeyStore.CallbackHandlerProtection(cmdLineHdlr));
        KeyStore ks = null;
        PublicKey pub_key = null;
        try {
            ks = builder.getKeyStore();
            String assinaturaCertifLabel = "CITIZEN AUTHENTICATION CERTIFICATE";
            Key key = ks.getKey(assinaturaCertifLabel, null);
            pub_key = ks.getCertificate(assinaturaCertifLabel).getPublicKey();

        }catch(KeyStoreException exception){
            exception.printStackTrace();
        }catch(NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }catch(UnrecoverableKeyException exception){
            exception.printStackTrace();
        }


        return pub_key;

    }

    public Provider providerInit(){
        String osName = System.getProperty("os.name");


        String pkcs11config = "name=GemPC" + "\n"
                + "library=C:/WINDOWS/system32/pteidpkcs11.dll";

        byte[] pkcs11configBytes = pkcs11config.getBytes();
        ByteArrayInputStream configStream = new ByteArrayInputStream(pkcs11configBytes);
        Provider p = new SunPKCS11(configStream);
        Security.addProvider(p);

        return p;
    }
}
