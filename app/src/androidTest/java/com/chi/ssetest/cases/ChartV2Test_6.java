package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//走势数据 方法六 传入isNeedAfterHours
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CHARTV2TEST_6)
public class ChartV2Test_6 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CHARTV2TEST_6;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ChartV2Test_6", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("ChartV2Test_6", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().getString("CODES");
        final String Types = rule.getParam().getString("Chart_Types");
        final String isNeedAfterHours = rule.getParam().getString("isNeedAfterHours");

//        ChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
//            final int a=i;
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();

            quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    ChartRequestV2 request = new ChartRequestV2();
                    request.send(quoteItem,Types, new IResponseInfoCallback() {
                        @Override
                        public void callback(Response response) {
                            ChartResponse chartResponse = (ChartResponse) response;
                            try {
                                assertNotNull(chartResponse.historyItems);
                                if (Boolean.parseBoolean(isNeedAfterHours)){
                                    assertNotNull(chartResponse.afterHoursChartResponse.historyItems);
                                }
                            } catch (AssertionError e) {
                                result.completeExceptionally(e);
                            }
                            CopyOnWriteArrayList<OHLCItem> list=chartResponse.historyItems;
                            JSONObject uploadObj = new JSONObject();
                            // TODO fill uploadObj with QuoteResponse value
                            try {
                                for (int k=0;k<list.size();k++) {
                                    JSONObject uploadObj_1 = new JSONObject();
                                    //存储到JSON
                                    uploadObj_1.put("datetime",list.get(k).datetime);
                                    uploadObj_1.put("closePrice",list.get(k).closePrice);
                                    uploadObj_1.put("tradeVolume",list.get(k).tradeVolume);
                                    uploadObj_1.put("averagePrice",list.get(k).averagePrice);
                                    uploadObj_1.put("md",list.get(k).getMd());
                                    uploadObj_1.put("openInterest",list.get(k).openInterest);
                                    uploadObj_1.put("iopv",list.get(k).iopv);
                                    uploadObj_1.put("iopvPre",list.get(k).iopvPre);
                                    uploadObj.put(list.get(k).datetime,uploadObj_1);
                                }
                                if (Boolean.parseBoolean(isNeedAfterHours)){
                                    if (null!=chartResponse.afterHoursChartResponse.historyItems){
                                        CopyOnWriteArrayList<OHLCItem> list1=chartResponse.afterHoursChartResponse.historyItems;
                                        for (int i=0;i<list1.size();i++){
                                            JSONObject uploadObj_1 = new JSONObject();
                                            uploadObj_1.put("datetime",list1.get(i).datetime);
                                            uploadObj_1.put("closePrice",list1.get(i).closePrice);
                                            uploadObj_1.put("tradeVolume",list1.get(i).tradeVolume);
                                            uploadObj_1.put("reference_price",list1.get(i).reference_price);
//                                        Log.d("panh", String.valueOf(uploadObj_1));
                                            uploadObj.put(list1.get(i).datetime,uploadObj_1);
                                        }
                                    }
                                }
                                Log.d("data", String.valueOf(uploadObj));
                                result.complete(uploadObj);
                            } catch (JSONException e) {
                                result.completeExceptionally(e);
                            }

                        }
                        @Override
                        public void exception(ErrorInfo errorInfo) {
                            result.completeExceptionally(new Exception(errorInfo.toString()));
                        }
                    }, Boolean.parseBoolean(isNeedAfterHours));
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
