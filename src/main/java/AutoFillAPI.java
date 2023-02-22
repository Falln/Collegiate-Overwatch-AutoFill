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

    //No need for a constructor
    public AutoFillAPI() {}

    /**
     * Takes a battletag and gets the all the player data about that player in JSON format 
     * using the Overfast API: https://github.com/TeKrop/overfast-api
     * @param battleTag battletag of the player to look for
     * @return JSONObject containing all the usual profile data on a player
     */
    public JSONObject getProfileData(String battleTag) {
        //Change all #'s in bTags to -'s
        //String battleTagBracketFilter = battleTag.substring(1,battleTag.length()-1);
        String battleTagNoHash = battleTag.replace('#','-');

        //Find the appropriate URL to access the ow-api
        String owAPIURL = "https://overfast-api.tekrop.fr/players/".concat(battleTagNoHash).concat("/summary");
        System.out.println(" " + owAPIURL); //TODO Centralize print statements
        JSONObject profileData = new JSONObject();

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
                JSONObject roleSRs = profileData.getJSONObject("competitive").getJSONObject("pc");
                try {
                    String tankSRDivision = roleSRs.getJSONObject("tank").getString("division");
                    int tankSRInt = roleSRs.getJSONObject("tank").getInt("tier");
                    tankSR = parseOW1RankFromOW2Division(tankSRDivision, tankSRInt);
                } catch (JSONException e) {
                    tankSR = 0;
                }
                try {
                    String dpsSRDivision = roleSRs.getJSONObject("damage").getString("division");
                    int dpsSRInt = roleSRs.getJSONObject("damage").getInt("tier");
                    dpsSR = parseOW1RankFromOW2Division(dpsSRDivision, dpsSRInt);
                } catch (JSONException e) {
                    dpsSR = 0;
                }
                try {
                    String supSRDivision = roleSRs.getJSONObject("support").getString("division");
                    int supSRInt = roleSRs.getJSONObject("support").getInt("tier");
                    supSR = parseOW1RankFromOW2Division(supSRDivision, supSRInt);
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

    /**
     * Takes the OW2 Divisions and Tiers and converts them to the OW1 scale of 1000-4400
     */
    public int parseOW1RankFromOW2Division(String division, int tier) {
        if (division.equalsIgnoreCase("bronze")) {
            switch(tier) {
                case 5:
                    return 1000;
                case 4:
                    return 1100;
                case 3:
                    return 1200;
                case 2:
                    return 1300;
                case 1:
                    return 1400;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("silver")) {
            switch(tier) {
                case 5:
                    return 1500;
                case 4:
                    return 1600;
                case 3:
                    return 1700;
                case 2:
                    return 1800;
                case 1:
                    return 1900;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("gold")) {
            switch(tier) {
                case 5:
                    return 2000;
                case 4:
                    return 2100;
                case 3:
                    return 2200;
                case 2:
                    return 2300;
                case 1:
                    return 2400;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("platinum")) {
            switch(tier) {
                case 5:
                    return 2500;
                case 4:
                    return 2600;
                case 3:
                    return 2700;
                case 2:
                    return 2800;
                case 1:
                    return 2900;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("diamond")) {
            switch(tier) {
                case 5:
                    return 3000;
                case 4:
                    return 3100;
                case 3:
                    return 3200;
                case 2:
                    return 3300;
                case 1:
                    return 3400;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("master")) {
            switch(tier) {
                case 5:
                    return 3500;
                case 4:
                    return 3600;
                case 3:
                    return 3700;
                case 2:
                    return 3800;
                case 1:
                    return 3900;
                default:
                    return 0;
            }
        } else if (division.equalsIgnoreCase("grandmaster")) {
            switch(tier) {
                case 5:
                    return 4000;
                case 4:
                    return 4100;
                case 3:
                    return 4200;
                case 2:
                    return 4300;
                case 1:
                    return 4400;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    //Alternative method that just requires the battleTag
    public String getHighestPlayoverwatchSR(String battleTag) {
        //Get the profile JSON
        JSONObject profileData = getProfileData(battleTag);
        return getHighestPlayoverwatchSR(profileData);
    }

    public boolean isPrivateProfile(JSONObject profileData) {
        return profileData.getString("privacy").equals("private") ? true : false;
    }

    public String getHighestOverbuffSR(String battleTag) {
        //Get the HTML file from overbuff
        String battleTagNoHash = battleTag.replace('#', '-');
        Document overbuffHTML;
        try {
            overbuffHTML = Jsoup.connect("https://www.overbuff.com/players/pc/" + battleTagNoHash).get();
        } catch (IOException e) {
            System.out.println("Invalid Overbuff URL: Bad BattleTag or invalid character");
            return "Unknown BattleTag";
        }

        //Parse the rank icon names (Overbuff currently has it as the "alt" attribute of the rank images)
        String dpsRank = overbuffHTML.select("div.justify-start:nth-child(2) > div:nth-child(2) > img:nth-child(1)").attr("alt");
        String supRank = overbuffHTML.select("div.gap-1:nth-child(3) > div:nth-child(2) > img:nth-child(1)").attr("alt");
        String tankRank = overbuffHTML.select("div.flex-row:nth-child(2) > div:nth-child(1) > div:nth-child(2) > img:nth-child(1)").attr("alt");

        //Parse the tier and division from the rank
        int dpsTier = convertToInt((dpsRank.length() > 0) ? dpsRank.substring(dpsRank.length()-1) : "0");
        int supTier = convertToInt((supRank.length() > 0) ? supRank.substring(supRank.length()-1) : "0");
        int tankTier = convertToInt((tankRank.length() > 0) ? tankRank.substring(tankRank.length()-1) : "0");

        String dpsDivision = dpsRank.substring(0, (dpsRank.length() > 0) ? dpsRank.length()-2 : 0);
        String supDivision = supRank.substring(0, (supRank.length() > 0) ? supRank.length()-2 : 0);
        String tankDivision = tankRank.substring(0, (tankRank.length() > 0) ? tankRank.length()-2 : 0);

        //Parse OW1 rank from the OW2 rank
        int dpsSRInt = parseOW1RankFromOW2Division(dpsDivision, dpsTier);
        int supSRInt = parseOW1RankFromOW2Division(supDivision, supTier);
        int tankSRInt = parseOW1RankFromOW2Division(tankDivision, tankTier);

        //Check if no ranks were found/if they are private profiled (or if they all errored?)
        if(dpsSRInt == 0 && supSRInt == 0 && tankSRInt == 0) {
            return ""; //"" is the default for private profile
        }

        //Check all the SRs and return the highest SR. If they happen the same it will go Tank > DPS > Sup
        if (tankSRInt >= dpsSRInt && tankSRInt >= supSRInt) {
            return Integer.toString(tankSRInt);
        } else if (dpsSRInt > tankSRInt && dpsSRInt >= supSRInt) {
            return Integer.toString(dpsSRInt);
        } else {
            return Integer.toString(supSRInt);
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
