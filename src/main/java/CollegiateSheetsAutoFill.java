
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class CollegiateSheetsAutoFill {

    public static void main(String... args) throws IOException, GeneralSecurityException {
        //Basic variables for knowing where to look in and update the sheet
        int START_ROW = 3;
        int END_ROW = 573;
        String START_COLUMN = "S";
        String END_COLUMN = "AD";
        String HYPERLINK_COLUMN = "C";
        boolean DO_NOT_INCLUDE_BLANKS_IN_SR = false;
        boolean DO_NOT_INCLUDE_BLANKS_IN_BATTLETAGS = false;
        boolean updateBattleTags = false;
        String sheetName = "'Fall 2021 (Current)'!";
        int sheetNumber = 0;
        String sheetID = "1CwH9-N_yVECQl-UHVK5_PkHE-YeBSyS-ZA0gNfU2IZI";

        //APIs, objects, and calculated variables needed for later
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();
        AutoFillAPI autoFillAPI = new AutoFillAPI();
        GetBattleTagsUtil getBattleTagsUtil = new GetBattleTagsUtil();
        String START_COL_ROW = START_COLUMN + String.valueOf(START_ROW);
        String END_COL_ROW = END_COLUMN + String.valueOf(END_ROW);

        if (updateBattleTags) {
            //Grab the urls for each team, grab the battletags based off that url, and then upload
            //the btags to the sheet
            System.out.println("\nGetting Hyperlink data from range: " + sheetName + HYPERLINK_COLUMN + ":" + HYPERLINK_COLUMN);
            Sheets.Spreadsheets.Get hyperlinkRequest = sheetsService.spreadsheets()
                    .get(sheetID)
                    .setRanges(Arrays.asList(sheetName + HYPERLINK_COLUMN + ":" + HYPERLINK_COLUMN))
                    .setIncludeGridData(true);
            Spreadsheet hyperlinkResponse = hyperlinkRequest.execute();
            GridData mainSheetGridData = hyperlinkResponse.getSheets().get(sheetNumber).getData().get(0);
            System.out.println("Response received from google: Acquired Hyperlink data");
            System.out.println();

            List<ValueRange> bTagData = new ArrayList<>();

            //Logic for the battletag push system
            for (int currRow = START_ROW; currRow <= END_ROW; currRow = currRow + 2) {
                System.out.print("Getting BattleTags for the team in row "+ currRow +": ");
                CellData currentCellData = mainSheetGridData.getRowData().get(currRow - 1).getValues().get(0);
                String gameBattleLink = currentCellData.getHyperlink();
                System.out.println(gameBattleLink);
                if (gameBattleLink == null) {
                    System.out.println("No Hyperlink was found in " + sheetName + HYPERLINK_COLUMN + String.valueOf(currRow) + ". Skipping Row");
                } else {
                    List<Object> listOfBTags = getBattleTagsUtil.getBTagsFromGameBattles(gameBattleLink);
                    if (!DO_NOT_INCLUDE_BLANKS_IN_BATTLETAGS) {
                        for (int x = listOfBTags.size(); x <= 11; x++) {
                            listOfBTags.add("");
                        }
                    }
                    bTagData.add(new ValueRange()
                            .setRange(sheetName + START_COLUMN + currRow + ":" + END_COLUMN + currRow)
                            .setValues(Arrays.asList(listOfBTags)));
                }
            }

            System.out.println("\nAll SRs successfully obtained. Attempting to upload/update sheets with bTagData:");
            BatchUpdateValuesRequest bTagBatchBody = new BatchUpdateValuesRequest()
                    .setValueInputOption("USER_ENTERED")
                    .setData(bTagData);
            BatchUpdateValuesResponse bTagBatchResult = sheetsService.spreadsheets().values()
                    .batchUpdate(sheetID, bTagBatchBody)
                    .execute();
            System.out.println("Response received from google: Successfully uploaded BattleTags");
        }


        //Firstly grab the spreadsheet data. This just grabs every value from the start
        //column and row down to the end column and row
        System.out.println("\nGetting BattleTag data from range: " + sheetName + START_COL_ROW + ":" + END_COL_ROW);
        Sheets.Spreadsheets.Values.Get bTagRequest = sheetsService.spreadsheets().values()
                .get(sheetID, sheetName + START_COL_ROW + ":" + END_COL_ROW);
        ValueRange bTagResponse = bTagRequest.execute();
        System.out.println("Response received from google: Acquired BattleTag data: " + bTagResponse);
        System.out.println();

        //Grab the Cell Values from the Sheets response. This is in List<List<Object>> format
        List<List<Object>> bTags = bTagResponse.getValues();
        //Set up the data array. This will store the data and cells that they correspond with
        //as a ValueRange object to be sent to the sheets later
        List<ValueRange> srData = new ArrayList<>();
        List<String> unknownBTags = new ArrayList<>();
        int arrayRowTracker = 0;//(array starts at 0 not at START_ROW)

        //TL;DR iterate through each row and then update each column. The bTag in each column is then run
        //through the AutoFillAPI and then is added with its associating cell location to the srData list
        for (int row = START_ROW; row <= END_ROW; row = row+2) {
            //Array containing all the current row's values for each column
            List<Object> currRowBTags = bTags.get(arrayRowTracker);
            String currColumn = START_COLUMN;

            //Iterate through each column in the row. This starts with the START_COLUMN given
            //and ends when it reaches either the end of the array or END_COLUMN
            for (int column = 0; column < currRowBTags.size() && !currColumn.equals(END_COLUMN); column++) {
                String bTag = (String) currRowBTags.get(column); //Grab bTag String
                System.out.print("Getting data for bTag " + bTag);
                String highestSR;
                //Check if the value from the sheets has data/bTag
                if (!bTag.equals("")) {
                    highestSR = autoFillAPI.getHighestHistoricalSR(bTag); //If it does, get rank
                } else {
                    highestSR = ""; //If not add a blank
                }
                //Catch unknown or bad character BattleTags
                if (highestSR.contains("Unknown BattleTag")) {
                    unknownBTags.add(bTag + " at " + currColumn + String.valueOf(row));
                    highestSR = "";
                }
                //Check if we are including blanks or not. If we aren't skip every blank
                if (!(DO_NOT_INCLUDE_BLANKS_IN_SR && highestSR.equals(""))) {
                    //Add SR data to, well, srData
                    srData.add(new ValueRange()
                            .setRange(sheetName + currColumn + String.valueOf(row+1))
                            .setValues(Arrays.asList(
                                    Arrays.asList(highestSR))));
                }
                currColumn = iterateColumn(currColumn);
            }
            arrayRowTracker = arrayRowTracker+2;
        }
        System.out.println("\nAll SRs successfully obtained. Attempting to upload/update sheets with srData:");
        //System.out.println(srData);
        //Update data to the sheets
        BatchUpdateValuesRequest srBatchBody = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(srData);
        BatchUpdateValuesResponse srBatchResult = sheetsService.spreadsheets().values()
                .batchUpdate(sheetID, srBatchBody)
                .execute();
        System.out.println("Response received from google: Successfully uploaded SRs");
        System.out.println("\nBattleTags that were not able to be found are included in 'Unknown BattleTags.txt' "
                +"in the resources folder");
        FileWriter writer = null;
        try {
            writer = new FileWriter("C:\\Users\\isaac\\Documents\\Program Projects\\Personal Projects\\CollegiateOW_AutoFill\\src\\main\\resources\\Unknown BattleTags.txt");
            for (String bTagInfo:unknownBTags) {
                writer.write("\n" + bTagInfo);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Since sheets goes from Z to AA, this iterates a String using Sheets column format
     * MAX currently is AZ, but can be expanded further.
     */

    public static String iterateColumn(String currColumn){
        if (currColumn.equals("Z")) {
            return "AA";
        } else if (currColumn.length() == 1) {
            char nextColumn = currColumn.charAt(0);
            nextColumn++;
            return String.valueOf(nextColumn);
        } else if (currColumn.length() == 2) {
            if (currColumn.charAt(1) == 'Z') {
                char nextColumn = currColumn.charAt(0);
                nextColumn++;
                return nextColumn+"A";
            } else {
                char nextColumn = currColumn.charAt(1);
                nextColumn++;
                return String.valueOf(currColumn.charAt(0)) + (nextColumn);
            }
        } else {
            return "OUTSIDE OF ITERATE RANGE";
        }
    }
}
