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
import com.mitake.core.QuoteItem;
import com.mitake.core.StockBulletinItem;
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
 *个股/自选公告2
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_STOCKBULLETINLISTTEST_2)
public class F10_StockBulletinListTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_STOCKBULLETINLISTTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_StockBulletinListTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_StockBulletinListTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("stockId");
        final String quoteNumbers1 = rule.getParam().optString("updateType");
        final String quoteNumbers2 = rule.getParam().optString("newsID");
        final String quoteNumbers3 = rule.getParam().optString("src");
        final String quoteNumbers4 = rule.getParam().optString("count");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String newsID;
        if(quoteNumbers2.equals("null")){
            newsID= null;
        }else {
            newsID= quoteNumbers2;
        }
            StockBulletinListRequest request = new StockBulletinListRequest();
            request.sendV2(quoteNumbers,Integer.parseInt(quoteNumbers1),newsID,quoteNumbers3,Integer.parseInt(quoteNumbers4),new IResponseInfoCallback<StockBulletinListResponse>() {
                @Override
                public void callback(StockBulletinListResponse stockBulletinListResponse) {
                    try {
                        assertNotNull(stockBulletinListResponse.list);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    if (stockBulletinListResponse.list!=null){
                        try {
                            for (StockBulletinItem item : stockBulletinListResponse.list) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("PUBDATE_", item.PUBDATE_);
                                uploadObj_1.put("ID_", item.ID_);
                                uploadObj_1.put("TITLE_", item.TITLE_);
                                uploadObj_1.put("dataSource", item.dataSource);
                                uploadObj_1.put("STOCKNAME_", item.STOCKNAME_);
//                                uploadObj_1.put("COUNT_", item.COUNT_);
                                uploadObj_1.put("ISPDF_", item.ISPDF_);
                                uploadObj_1.put("ENTRYDATE", item.ENTRYDATE);
                                uploadObj_1.put("ENTRYTIME", item.ENTRYTIME);
                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(item.PUBDATE_,uploadObj_1);
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
