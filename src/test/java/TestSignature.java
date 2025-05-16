import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TestSignature {

    public static void main(String[] args) throws Exception {
        String data = "HTTP Method\\n\n" +
                "     URL Path\\n\n" +
                "     Timestamp\\n\n" +
                "     Nonce\\n\n" +
                "     Body\\n";
        System.out.println(sign(data, "7Uhb6ygv5tfc4rdx3esz2wa1q0pl9ok2"));
    }

    public static String sign(String data, String apiKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(spec);
        byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(result);
    }
}
