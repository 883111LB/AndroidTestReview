package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.Importantnotice;
import com.mitake.core.MainFinaIndexHas;
import com.mitake.core.NewsList;
import com.mitake.core.QuoteItem;
import com.mitake.core.StockBulletinItem;
import com.mitake.core.StockNewsItem;
import com.mitake.core.StockShareInfo;
import com.mitake.core.TopLiquidShareHolder;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MainFinaDataNasRequest;
import com.mitake.core.request.MainFinaIndexNasRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.NewsListRequest;
import com.mitake.core.request.NewsType;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.StockBulletinListRequest;
import com.mitake.core.request.StockShareInfoRequest;
import com.mitake.core.request.TopLiquidShareHolderRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
import com.mitake.core.response.MainFinaIndexNasResponse;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.NewsListResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.StockBulletinListResponse;
import com.mitake.core.response.StockShareInfoResponse;
import com.mitake.core.response.TopLiquidShareHolderResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *财经资讯列表
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_NEWSLISTTEST_1)
public class F10_NewsListTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_NEWSLISTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_NewsListTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    //NewsType
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_NewsListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("newsType");
        final String quoteNumbers1 = rule.getParam().optString("updateType");
        final String quoteNumbers2 = rule.getParam().optString("newsID");
        final String quoteNumbers3 = rule.getParam().optString("src");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String newsID;
        if(quoteNumbers2.equals("null")){
            newsID= null;
        }else {
            newsID= quoteNumbers2;
        }
            NewsListRequest request = new NewsListRequest();
            request.send(quoteNumbers,Integer.parseInt(quoteNumbers1),newsID,quoteNumbers3,new IResponseInfoCallback<NewsListResponse>() {
                @Override
                public void callback(NewsListResponse newsListResponse) {
                    try {
                        assertNotNull(newsListResponse.list);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    if (newsListResponse.list!=null){
                        try {
                            for (NewsList item : newsListResponse.list) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("ID_", item.ID_);
                                uploadObj_1.put("INIPUBDATE_", item.INIPUBDATE_);
                                uploadObj_1.put("REPORTTITLE_", item.REPORTTITLE_);
                                uploadObj_1.put("MEDIANAME_", item.MEDIANAME_);
                                uploadObj_1.put("ABSTRACTFORMAT_", item.ABSTRACTFORMAT_);
//                                uploadObj_1.put("OVERPAGE_", item.OVERPAGE_);
//                                uploadObj_1.put("COUNT_", item.COUNT_);
                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(item.INIPUBDATE_,uploadObj_1);
                            }
                            result.complete(uploadObj);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
