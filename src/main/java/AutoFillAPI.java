import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class AutoFillAPI {

    public AutoFillAPI() {
    }

    public JSONObject getProfileData(String battleTag) {
        //Change all #'s in bTags to -'s
        //String battleTagBracketFilter = battleTag.substring(1,battleTag.length()-1);
        String battleTagNoHash = battleTag.replace('#','-');
        //Find the appropriate URL to access the ow-api
        String owAPIURL = "https://ow-api.com/v1/stats/pc/us/".concat(battleTagNoHash).concat("/profile");
        System.out.println(" " + owAPIURL);
        JSONObject profileData = new JSONObject();
        int response;
        //Get the JSON object from the URL (uses the before URL and joins the data sent into a JSONObject
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new URL(owAPIURL).openStream()))) {
            profileData = new JSONObject(br.lines().collect(Collectors.joining()));
        } catch (MalformedURLException e) {
            System.out.println("ERROR: Malformed or Incorrect URL was given");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("BattleTag " + battleTag + " does not exist");
            JSONObject unknownBTagJSON = new JSONObject();
            unknownBTagJSON.put("UnknownTag", "Unknown BattleTag");
            return unknownBTagJSON;
        }
        //Specific catch for if a � character appears in a BattleTag
        try {
            if (profileData.getString("error").equals("Failed to decode platform API response: invalid character '<' looking for beginning of value")) {
                System.out.println("BattleTag " + battleTag + " does not exist");
                JSONObject unknownBTagJSON = new JSONObject();
                unknownBTagJSON.put("UnknownTag", "Unknown BattleTag");
                return unknownBTagJSON;
            }
        } catch(JSONException e) {}

        //Return the newly found JSON object with the profile data
        return profileData;
    }

    public String getHighestPlayoverwatchSR(JSONObject profileData) {
        //Check if there is a valid BattleTag
        try {
            if (profileData.getString("UnknownTag").equals("Unknown BattleTag")) {
                return profileData.getString("UnknownTag");
            }
        } catch (JSONException e) {}
        //Grab the SR for each role from the JSON object
        if (isPrivateProfile(profileData)) {
            return "";
        } else {
            //First try catch loop is to catch if there is a ratings array
            //if there is, we grab the ratings, if not we output ""
            try {
                int tankSR;
                int dpsSR;
                int supSR;
                //Now for each role, we check if its array is there. If not, then we default
                //to zero. But if one defaults to zero, the naming scheme doesn't matter anymore
                JSONArray roleSRs = profileData.getJSONArray("ratings");
                try {
                    tankSR = (int) roleSRs.getJSONObject(0).get("level");
                } catch (JSONException e) {
                    tankSR = 0;
                }
                try {
                    dpsSR = (int) roleSRs.getJSONObject(1).get("level");
                } catch (JSONException e) {
                    dpsSR = 0;
                }
                try {
                    supSR = (int) roleSRs.getJSONObject(2).get("level");
                } catch (JSONException e)  {
                    supSR = 0;
                }
                //Find out which SR is highest. It follows the order of preference
                //of tank > dps > sup if there is a tie
                //(important in case I need to specify which role the highest SR is)
                if (tankSR >= dpsSR && tankSR >= supSR) {
                    return String.valueOf(tankSR);
                } else if (dpsSR > tankSR && dpsSR >= supSR) {
                    return String.valueOf(dpsSR);
                } else {
                    return String.valueOf(supSR);
                }
            } catch (JSONException e) {
                return "";
            }

        }
    }

    //Alternative method that just requires the battleTag
    public String getHighestPlayoverwatchSR(String battleTag) {
        //Get the profile JSON
        JSONObject profileData = getProfileData(battleTag);
        return getHighestPlayoverwatchSR(profileData);
    }

    private boolean isPrivateProfile(JSONObject profileData) {
        return profileData.getBoolean("private");
    }

    public String getHighestOverbuffSR(String battleTag) {
        String battleTagNoHash = battleTag.replace('#', '-');
        Document overbuffHTML;
        try {
            overbuffHTML = Jsoup.connect("https://www.overbuff.com/players/pc/" + battleTagNoHash).get();
        } catch (IOException e) {
            System.out.println("Invalid Overbuff URL: Bad BattleTag or invalid character");
            return "Unknown BattleTag";
        }
        String dpsSR = overbuffHTML.select("body > div > div.container.main > div.row.layout-content > div > div.row.with-sidebar > div.columns.four > div:nth-child(2) > div > section > article > table > tbody > tr:nth-child(1) > td:nth-child(3)").text().replace(",", "");
        String supSR = overbuffHTML.select("body > div > div.container.main > div.row.layout-content > div > div.row.with-sidebar > div.columns.four > div:nth-child(2) > div > section > article > table > tbody > tr:nth-child(2) > td:nth-child(3)").text().replace(",", "");
        String tankSR = overbuffHTML.select("body > div > div.container.main > div.row.layout-content > div > div.row.with-sidebar > div.columns.four > div:nth-child(2) > div > section > article > table > tbody > tr:nth-child(2) > td:nth-child(3)").text().replace(",", "");
        String overallSR = overbuffHTML.select("body > div.skin-container.seemsgood > div.container.main > div.row.layout-content > div > div.layout-header > div:nth-child(1) > div:nth-child(2) > div.layout-header-secondary.layout-header-secondary-player > dl:nth-child(1) > dd > span > span").text();
        int dpsSRInt = convertToInt(dpsSR);
        int supSRInt = convertToInt(supSR);
        int tankSRInt = convertToInt(tankSR);

        if (dpsSR.equals("") && supSR.equals("") && tankSR.equals("")) {
            if (overallSR.length() > 0) {
                if (overallSR.length() == 3) {
                    return overallSR.substring(0,3);
                }
                return overallSR.substring(0,4);
            }
            return overallSR;
        }

        if (tankSRInt >= dpsSRInt && tankSRInt >= supSRInt) {
            return tankSR;
        } else if (dpsSRInt > tankSRInt && dpsSRInt >= supSRInt) {
            return dpsSR;
        } else {
            return supSR;
        }
    }

    private int convertToInt(String s) {
        if (s.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }

    public String getHighestHistoricalSR(String battleTag) {
        String playOverwatchMaxSR = getHighestPlayoverwatchSR(battleTag);
        if (playOverwatchMaxSR.equals("Unknown BattleTag")) {
            String overbuffMaxSR = getHighestOverbuffSR(battleTag);
            if (overbuffMaxSR.equals("") || overbuffMaxSR.equals("Unknown BattleTag")) {
                return playOverwatchMaxSR;
            } else {
                return overbuffMaxSR;
            }
        } else if (playOverwatchMaxSR.equals("")) {
            return getHighestOverbuffSR(battleTag);
        } else {
            return playOverwatchMaxSR;
        }
    }
}
