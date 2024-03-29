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
import com.mitake.core.StockShareInfo;
import com.mitake.core.TopLiquidShareHolder;
import com.mitake.core.TopShareHolder;
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
import com.mitake.core.request.StockShareInfoRequest;
import com.mitake.core.request.TopLiquidShareHolderRequest;
import com.mitake.core.request.TopShareHolderRequest;
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
import com.mitake.core.response.StockShareInfoResponse;
import com.mitake.core.response.TopLiquidShareHolderResponse;
import com.mitake.core.response.TopShareHolderResponse;

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
 *最新十大股东
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_TOPSHAREHOLDERTEST_1)
public class F10_TopShareHolderTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_TOPSHAREHOLDERTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_TopShareHolderTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_TopShareHolderTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            TopShareHolderRequest request = new TopShareHolderRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<TopShareHolderResponse>() {
                @Override
                public void callback(TopShareHolderResponse topShareHolderResponse) {
                    try {
                        assertNotNull(topShareHolderResponse.list);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<JSONObject> items=new ArrayList<>();
                    if (topShareHolderResponse.list!=null){
                        try {
                            for (TopShareHolder item : topShareHolderResponse.list) {
                                JSONObject uploadObj_1 = new JSONObject();
                                if (quoteNumbers1.equals("g")){
                                    uploadObj_1.put("SHNO_",item.SHNO_);
                                    uploadObj_1.put("SHNAME_",item.SHNAME_);
                                    uploadObj_1.put("SHCODE_",item.SHCODE_);
                                    uploadObj_1.put("PCTTOTALSHAREUR_",item.PCTTOTALSHAREUR_);
                                    uploadObj_1.put("DIFF_",item.DIFF_);
                                    uploadObj_1.put("HOLDASHAREUR_",item.HOLDASHAREUR_);
                                    uploadObj_1.put("ENDDATE_",item.ENDDATE_);
                                }
                                if (quoteNumbers1.equals("d")){
                                    uploadObj_1.put("SHNAME_",item.SHNAME_);
                                    uploadObj_1.put("PCTTOTALSHAREUR_",item.PCTTOTALSHAREUR_);
                                    uploadObj_1.put("DIFF_",item.DIFF_);
                                    uploadObj_1.put("ENDDATE_",item.ENDDATE_);
                                    uploadObj_1.put("SHCODE_",item.SHCODE_);
                                    uploadObj_1.put("HOLDASHAREUR_",item.HOLDASHAREUR_);
                                }
                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(item.ENDDATE_,uploadObj_1);
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
