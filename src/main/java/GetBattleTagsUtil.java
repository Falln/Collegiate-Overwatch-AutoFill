import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class GetBattleTagsUtil {

    WebClient webClient;

    public GetBattleTagsUtil() {
        webClient = gethtmlUnitClient();
        WebWindow currentWebWindow = webClient.getCurrentWindow();
        currentWebWindow.setOuterWidth(100);
        currentWebWindow.setOuterHeight(100);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    public List<Object> getBTagsFromGameBattles(String gameBattlesURL) {
        if (!gameBattlesURL.contains("gamebattles.majorleaguegaming.com")) {
            System.out.println("Current link is ignored as it is not a GameBattles link");
            return new ArrayList<>();
        }
        HtmlPage gameBattlesPage = getHtlmPage(gameBattlesURL);
        webClient.waitForBackgroundJavaScript(5000);
        List<HtmlSpan> bTagsHtlmSpan = gameBattlesPage.getByXPath("//span[@_ngcontent-serverapp-c393='']");
        List<Object> bTags = new ArrayList<>();
        for (HtmlSpan htmlSpan:bTagsHtlmSpan) {
            bTags.add(htmlSpan.asNormalizedText());
        }
        return bTags;
    }

    public HtmlPage getHtlmPage(String URL) {
        HtmlPage resultPage = null;
        try {
            resultPage = webClient.getPage(URL);
        } catch (MalformedURLException e) {
            System.out.println("BAD GAMEBATTLES URL");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultPage;
    }

    static public WebClient gethtmlUnitClient() {
        WebClient webClient;
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setIncorrectnessListener(new IncorrectnessListener() {
            @Override
            public void notify(String arg0, Object arg1) {
            }
        });
        webClient.setCssErrorHandler(new CSSErrorHandler() {

            @Override
            public void warning(CSSParseException e) throws CSSException {

            }

            @Override
            public void error(CSSParseException e) throws CSSException {

            }

            @Override
            public void fatalError(CSSParseException e) throws CSSException {

            }
        });
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

            @Override
            public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void scriptException(HtmlPage arg0, ScriptException arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void warn(String s, String s1, int i, String s2, int i1) {

            }
        });
        webClient.setHTMLParserListener(new HTMLParserListener() {

            @Override
            public void warning(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
                // TODO Auto-generated method stub

            }

            @Override
            public void error(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
                // TODO Auto-generated method stub

            }
        });
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        return webClient;

    }
}
