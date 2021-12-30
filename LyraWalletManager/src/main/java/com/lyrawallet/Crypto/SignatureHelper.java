package com.lyrawallet.Crypto;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;

import java.io.IOException;

public class SignatureHelper {
    public static byte[] ConvertDerToP1393(byte[] bcSignature) throws IOException {
        ASN1InputStream decoder = new ASN1InputStream(bcSignature);
        ASN1Sequence seq = (ASN1Sequence) decoder.readObject();
        ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
        ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
        //ASN1 like to put a 0 value byte ate the beginning of the array, check and eliminate it.
        byte[] rc = new byte[32];
        if(r.getValue().toByteArray()[0] == 0x00) {
            System.arraycopy(r.getValue().toByteArray(), 1, rc, 0, 32);
        } else {
            System.arraycopy(r.getValue().toByteArray(), 0, rc, 0, 32);
        }
        byte[] sc = new byte[32];
        if(s.getValue().toByteArray()[0] == 0x00) {
            System.arraycopy(s.getValue().toByteArray(), 1, sc, 0, 32);
        } else {
            System.arraycopy(s.getValue().toByteArray(), 0, sc, 0, 32);
        }
        decoder.close();
        byte[] signature = new byte[rc.length + sc.length];
        System.arraycopy(rc, 0, signature, 0, rc.length);
        System.arraycopy(sc, 0, signature, rc.length, sc.length);
        return signature;
    }

    public static byte[] derSign(byte[] signature) throws IOException {
        /*byte[] r = Arrays.copyOf(signature, signature.length / 2);
        byte[] s = Arrays.copyOfRange(signature, signature.length / 2, signature.length / 2);
        //MemoryStream stream = new MemoryStream();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ASN1OutputStream der = new ASN1OutputStream((OutputStream) stream);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(new BigInteger(1, r)));
        v.add(new ASN1Integer(new BigInteger(1, s)));
        der.writeObject(new DERSequence(v));
        return stream.toByteArray();*/
        byte[] sigOut = new byte[signature.length];
        int i;
        for(i=0; i<signature.length / 2; ++i) {
            sigOut[i] = signature[(signature.length / 2) - i - 1];
        }
        for(i=signature.length / 2; i<signature.length; ++i) {
            sigOut[i] = signature[signature.length + (signature.length / 2) - i - 1];
        }
        return sigOut;
    }
}
