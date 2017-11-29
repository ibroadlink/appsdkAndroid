package cn.com.broadlink.sdk;

import android.util.Log;

import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by zhuxuyang on 15/11/11.
 */
final class BLPyramidTrustManager implements X509TrustManager {
    private static final String SIGNATRUE_RELEASE = "-15516004116834876760320427327935225609402551617221733970275888461311334067919743723148590515080584604560681984788936874983234734633412573462929958346063306320645170737134392630047145541657335386538188606322147548558457845131032523474540844139749605590077248771885478388588394504723148699109805046977860111504963826668324426918132278525658023916953673688390387855843638954306745609637402442163524385430014063648513606577796855908985583791052525410830006019678299973092677804766999646445462656273241931128745428463782717374088381176753091049677763162192928048301623809001965926889561100017752172571827179886500546702879";


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if(chain.length == 0){
            throw new CertificateException();
        }
        X509Certificate cert = chain[0];
        byte[] signatrue = cert.getSignature();
        BigInteger bigSignatrue = new BigInteger(signatrue);

        String finalSignatrue = SIGNATRUE_RELEASE;

        if(bigSignatrue.compareTo(new BigInteger(finalSignatrue)) != 0){
            throw new CertificateException();
        }
        // Make sure that it hasn't expired.
        cert.checkValidity();
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
