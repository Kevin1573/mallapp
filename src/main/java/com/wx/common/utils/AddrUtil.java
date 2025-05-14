package com.wx.common.utils;

import com.wx.common.exception.BizException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddrUtil {

    private static final String GAODE_GEOCODE_API = "https://restapi.amap.com/v3/geocode/geo?key=%s&address=%s";
    private static final String YOUR_GAODE_KEY = "4589f12fb36cc27c6c047e8377700a93";

    public static Map<String, String> match(String address) {
        String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<area>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<detail>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, area = null, town = null, detail = null;
        Map<String, String> response = new HashMap<>();
        while (m.find()) {
            province = m.group("province");
            response.put("province", province == null ? "" : province.trim());
            city = m.group("city");
            response.put("city", city == null ? "" : city.trim());
            area = m.group("area");
            response.put("area", area == null ? "" : area.trim());
            town = m.group("town");
            response.put("town", town == null ? "" : town.trim());
            detail = m.group("detail");
            response.put("detail", detail == null ? "" : detail.trim());
        }
        return response;
    }

    public static boolean isPhoneNum(String param) {
        String regex = "^1[0-9]{10}$";
        return param.matches(regex);
    }

    public static boolean isName(String name) {
        return name.matches("^[\\u4e00-\\u9fa5]{2,4}$");
    }

    /**
     * 高德地图识别地址
     * @param addr
     * @return
     * @throws Exception
     */
    public static JSONObject matchAddr(String addr) throws Exception {
        String geocodeApi = String.format(GAODE_GEOCODE_API, YOUR_GAODE_KEY, addr);

        URL url = new URL(geocodeApi);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(content.toString());
        if ("10000".equals(jsonObject.getString("infocode"))) {
            return jsonObject.getJSONArray("geocodes").getJSONObject(0);
        } else {
            throw new BizException("地址识别失败: " + jsonObject.getString("info"));
        }
    }

    public static void main(String[] args) throws Exception {
        String address = "北京市市辖区门头沟区龙门新区a6小区4-1-701";
        String geocodeApi = String.format(GAODE_GEOCODE_API, YOUR_GAODE_KEY, address);

        URL url = new URL(geocodeApi);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(content.toString());
        if ("10000".equals(jsonObject.getString("infocode"))) {
            JSONObject geocodes = jsonObject.getJSONArray("geocodes").getJSONObject(0);
            System.out.println("省份: " + geocodes.getString("province"));
            System.out.println("城市: " + geocodes.getString("city"));
            System.out.println("区域: " + geocodes.getString("district"));
            String detail = "";
            if (!"[]".equals(geocodes.getString("street"))) {
                detail = detail + geocodes.getString("street");
            }
            if (!"[]".equals(geocodes.getString("number"))) {
                detail = detail + geocodes.getString("number");
            }
            System.out.println("详细：" + detail);
        } else {
            System.out.println("地址识别失败: " + jsonObject.getString("info"));
        }
    }

}
