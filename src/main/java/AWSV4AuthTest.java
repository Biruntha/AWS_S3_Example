import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;


public class AWSV4AuthTest {
    public static void testDeleteBucketReplication() throws Exception {
        String hostHeader = "testbuckkkbiru.s3.amazonaws.com";
        String canonicalURI = "/?replication";

        TreeMap<String, String> awsHeaders = new TreeMap<>();
        awsHeaders.put("host", hostHeader);
        awsHeaders.put("x-amz-content-sha256", hash(""));

        AWSSignatureV4Generator aWSV4Auth = new AWSSignatureV4Generator.Builder("xxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxx")
                .regionName("us-east-2")
                .serviceName("s3") // es - elastic search. use your service name
                .httpMethodName("DELETE") //GET, PUT, POST, DELETE, etc...
                .canonicalURI(canonicalURI) //end point
                .queryParametes(null) //query parameters if any
                .awsHeaders(awsHeaders) //aws header parameters
                .payload(null) // payload if any
                .build();

        HttpDelete httpDelete = new HttpDelete("https://" + hostHeader + canonicalURI);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        /* Get header calculated for request */
        Map<String, String> header = aWSV4Auth.getHeaders();
        for (Map.Entry<String, String> entrySet : header.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            httpDelete.setHeader(key,value);
        }

        httpDelete.setHeader("host", hostHeader);
        httpDelete.setHeader("x-amz-content-sha256", hash(""));

        HttpResponse response = httpClient.execute(httpDelete);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Status Code is : " + statusCode);
    }

    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256 algorithm.
     *
     * @param data text to be hashed
     * @return SHA-256 hashed text
     */
    private static String hash(String data) throws Exception {

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new Exception("Error while hashing the string contents." + e);
        }
    }

    public static void main(String[] args) throws Exception {
        testDeleteBucketReplication();
    }
}
