package st.project.studyWithUs.service.naverService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NaverServiceImpl implements NaverService{

    static String clientId = "quDmxXiN1GypP5C8hzbi"; //애플리케이션 클라이언트 아이디값";
    static String clientSecret = "2DCTt5z1X0"; //애플리케이션 클라이언트 시크릿값";

    public String getNaverToken(String code){
        String access_Token="";
        String reqURL = "https://nid.naver.com/oauth2.0/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+clientId);
            sb.append("&client_secret="+clientSecret);
            sb.append("&code=" + code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();

            br.close();
            bw.close();
        }catch (
                IOException e) {
            e.printStackTrace();
        }
        return access_Token;
    }

    public HashMap<String, Object> getUserInfo (String access_Token) {

        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://openapi.naver.com/v1/nid/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);
            conn.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            System.out.println("========================");
            System.out.println(result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("response").getAsJsonObject();


            String name = properties.getAsJsonObject().get("name").getAsString();
            String email = properties.getAsJsonObject().get("email").getAsString();

            System.out.println(name);
            System.out.println(email);
            userInfo.put("name", name);
            userInfo.put("email", email);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

    public String getKey(){
        String apiURL = "https://openapi.naver.com/v1/captcha/nkey?code=0";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL, requestHeaders);

        JSONParser jsonParser = new JSONParser();
        String key="";
        try {
            Object parse = jsonParser.parse(responseBody);
            JSONObject jsonObject = (JSONObject) parse;
            key = (String) jsonObject.get("key");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return key;
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    public String receiveImg (String key) {
        try {
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha-bin/v1/ncaptcha?key=" + key + "&X-NCP-APIGW-API-KEY-ID" + clientId;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                // 랜덤한 이름으로 파일 생성
                String tempname = Long.valueOf(new Date().getTime()).toString();
                String path = "C:\\StudyWithUs_teamProject\\src\\main\\resources\\static\\teamImage\\";
                File f = new File(path + tempname+ ".jpg");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();
                return tempname+".jpg";
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String resCap(String key, String value) {
        System.out.println(value);
        String code = "1"; // 키 발급시 0,  캡차 이미지 비교시 1로 세팅
        System.out.println("키: " + key + "   value: " + value);
//         String key = ApiExamCaptchaNKey.getKey(); // 캡차 키 발급시 받은 키값
//         String value = "YOUR_INPUT"; // 사용자가 입력한 캡차 이미지 글자값
        String apiURL = "https://openapi.naver.com/v1/captcha/nkey?code=" + code + "&key=" + key + "&value=" + value;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL, requestHeaders);

        return responseBody;
    }


}