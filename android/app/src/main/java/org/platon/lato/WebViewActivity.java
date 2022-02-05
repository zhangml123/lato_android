package org.platon.lato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.platon.wallet.base.BaseActivity;
import com.platon.wallet.db.entity.AssetEntity;
import com.platon.wallet.db.sqlite.AssetDao;
import com.platon.wallet.db.sqlite.TransactionDao;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.engine.WalletManager;
import com.platon.wallet.entity.TransactionStatus;
import com.platon.wallet.entity.TransactionType;
import com.platon.wallet.entity.Wallet;
import org.platon.lato.R;


import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.PlatonCall;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.PlatonTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.platon.wallet.BuildConfig.URL_ALAYA_RPC;
import static com.platon.wallet.BuildConfig.URL_PLATON_TESTNET_RPC;
import static com.platon.wallet.entity.Asset.ASSET_CONTRACT;

@SuppressLint("Registered")
public class WebViewActivity extends BaseActivity {
    public static int WEBVIEW_RESULT_CODE = 1;
    public static String DAPP_URL = "http://199.247.27.165:100";
    private WebView mWebView;
    private ProgressBar progressBar;
    private Button back;
    private Button refresh;
    private Button close;
    private Button clean;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetJavaScriptEnabled", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_webview);
        Wallet walletEntity = WalletManager.getInstance().getWalletList().get(0);
        //String url = DAPP_URL +"/address/"+walletEntity.getAddress();
        Bundle bundle = getIntent().getExtras();
        String url = DAPP_URL +"/address/"+walletEntity.getAddress();
        if(bundle != null){
            if(bundle.getString("path") != null){
                url = DAPP_URL + bundle.getString("path");
                String hash = bundle.getString("hash");
                getContract(hash);
            }
        }

        mWebView =  findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
        close = findViewById(R.id.close);
        clean = findViewById(R.id.clean);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.clearCache(true);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean back = mWebView.canGoBack();
                if(back){
                    mWebView.goBack();
                }else{
                    finish();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getContract(String hash){
        new AsyncTask<Void, Void, String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("StaticFieldLeak")
            @Override
            protected String doInBackground(Void... voids) {
                String address = getContractAddress(hash);
                System.out.println("transactionReceipt.address = "+ address);
                Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                Wallet walletEntity = WalletManager.getInstance().getCurrentWallet();
                final Function function = new Function("symbol",
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
                String encodedFunction = FunctionEncoder.encode(function);

                System.out.println("WebViewActivity 111111111 = ");
                final Function function1 = new Function("totalSupply",
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                String encodedFunction1 = FunctionEncoder.encode(function1);
                PlatonCall ethCall = null;
                PlatonCall ethCall1 = null;
                try {
                    ethCall = (PlatonCall)mWeb3j.platonCall(Transaction.createEthCallTransaction(walletEntity.getAddress(),address, encodedFunction), DefaultBlockParameterName.LATEST).send();
                    ethCall1 = (PlatonCall)mWeb3j.platonCall(Transaction.createEthCallTransaction(walletEntity.getAddress(),address, encodedFunction1), DefaultBlockParameterName.LATEST).send();
                    System.out.println("WebViewActivity 111111111 ethCall= "+ethCall);
                    System.out.println("WebViewActivity 111111111 ethCall1= "+ethCall1);
                } catch (IOException e) {
                    System.out.println("WebViewActivity 111111111 e= "+e);
                    e.printStackTrace();
                }
                System.out.println("WebViewActivity 111111111 ethCall.getValue()= "+ethCall.getValue());
                System.out.println("WebViewActivity 111111111 ethCall1.getValue()= "+ethCall1.getValue());
                if(ethCall != null && ethCall.getValue() != null && ethCall1 != null && ethCall1.getValue() != null){
                    List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters(), 210309L);

                    List<Type> values1 = FunctionReturnDecoder.decode(ethCall1.getValue(), function1.getOutputParameters(), 210309L);
                    System.out.println("WebViewActivity 111111111 values= "+values);
                    System.out.println("WebViewActivity 111111111 values1.)= "+values1);

                    if(!values.isEmpty() && !values1.isEmpty()){
                        System.out.println("AddAssetActivity ethCall. result ="+values.get(0).toString());
                        String symbol = values.get(0).toString();
                        long time = System.currentTimeMillis();
                        String uuid = UUID.randomUUID().toString();
                        AssetDao.insertAssetInfo(
                                new AssetEntity.Builder()
                                        .id(uuid)
                                        .walletId(WalletManager.getInstance().getCurrentWallet().getUuid())
                                        .assetType(ASSET_CONTRACT)
                                        .createTime(time)
                                        .updateTime(time)
                                        .contractAddress(address)
                                        .binary("")
                                        .name("")
                                        .symbol(symbol)
                                        .setHome(true)
                                        .build()
                        );

                        com.platon.wallet.entity.Transaction transaction = new com.platon.wallet.entity.Transaction.Builder()
                                .hash(hash)
                                .timestamp(time)
                                .txType(String.valueOf(TransactionType.CONTRACT_CREATION.getTxTypeValue()))
                                .txReceiptStatus(TransactionStatus.SUCCESSED.getTransactionStatusValue())
                                .from(walletEntity.getAddress())
                                .to(address)
                                .contractAddress(address)
                                .assetId(uuid)
                                .symbol(symbol)
                                .rAddress(walletEntity.getAddress())
                                .value(values1.get(0).getValue().toString())
                                .chainId(NodeManager.getInstance().getChainId())
                                .build();
                        TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                    }
                }
                return "";
            }
            @Override
            protected void onPostExecute(@NonNull String json) {

            }
        }.execute();
    }
    WebChromeClient webChromeClient = new WebChromeClient(){
        public void onProgressChanged(WebView view, int newProgress) {
            System.out.println("onProgressChanged newProgress  = "+newProgress);
            if(newProgress==100){
                progressBar.setVisibility(View.GONE);
            }
            else{
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        }

        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            System.out.println("consoleMessage message; = "+message);
        }

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            System.out.println("consoleMessage.message(); = "+consoleMessage.message());
            return true;
        }
    };
    WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            System.out.println("WebViewClient onPageStarted");
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("WebViewClient onPageStarted");
        }
        @Override
        public void onLoadResource(WebView view, String url) {
        }
        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if ( uri.getScheme().equals("lato")) {
                if (uri.getAuthority().equals("webview")) {
                    System.out.println("js调用了Android的方法");

                    String type= uri.getQueryParameter("type");
                    assert type != null;
                    if(type.equals("deployContract")){
                        System.out.println("deployContract 1");
                        String encodeData= uri.getQueryParameter("encodeData");
                        String gasLimit= uri.getQueryParameter("gasLimit");
                        String gasPrice= uri.getQueryParameter("gasPrice");
                        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                        intent.putExtra("encodeData", encodeData);
                        intent.putExtra("gasLimit", gasLimit);
                        intent.putExtra("gasPrice", gasPrice);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl){
            view.loadUrl("file:///android_assets/error.html");
        }
    };
    @Override

    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

   /* @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == WEBVIEW_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    String hash = intent.getStringExtra("hash");
                    String url = DAPP_URL +"/result/success/msg/"+hash;
                    mWebView.loadUrl(url);
                    //Util.sleep(2000);
                    new AsyncTask<Void, Void, String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected String doInBackground(Void... voids) {
                            String address = getContractAddress(hash);
                            System.out.println("transactionReceipt.address = "+ address);
                            Web3j mWeb3j = Web3j.build(new HttpService(URL_ALAYA_RPC));
                            Wallet walletEntity = WalletManager.getInstance().getWalletList().get(0);
                            final Function function = new Function("getSymbol",
                                    Arrays.<Type>asList(),
                                    Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
                            String encodedFunction = FunctionEncoder.encode(function);

                            System.out.println("WebViewActivity 111111111 = ");
                            final Function function1 = new Function("getTotalSupply",
                                    Arrays.<Type>asList(),
                                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                            String encodedFunction1 = FunctionEncoder.encode(function1);
                            PlatonCall ethCall = null;
                            PlatonCall ethCall1 = null;
                            try {
                                ethCall = (PlatonCall)mWeb3j.platonCall(Transaction.createEthCallTransaction(walletEntity.getAddress(),address, encodedFunction), DefaultBlockParameterName.LATEST).send();
                                ethCall1 = (PlatonCall)mWeb3j.platonCall(Transaction.createEthCallTransaction(walletEntity.getAddress(),address, encodedFunction1), DefaultBlockParameterName.LATEST).send();
                                System.out.println("WebViewActivity 111111111 ethCall= "+ethCall);
                                System.out.println("WebViewActivity 111111111 ethCall1= "+ethCall1);
                            } catch (IOException e) {
                                System.out.println("WebViewActivity 111111111 e= "+e);
                                e.printStackTrace();
                            }
                            System.out.println("WebViewActivity 111111111 ethCall.getValue()= "+ethCall.getValue());
                            System.out.println("WebViewActivity 111111111 ethCall1.getValue()= "+ethCall1.getValue());
                            if(ethCall != null && ethCall.getValue() != null && ethCall1 != null && ethCall1.getValue() != null){
                                List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters(), 210309L);

                                List<Type> values1 = FunctionReturnDecoder.decode(ethCall1.getValue(), function1.getOutputParameters(), 210309L);
                                System.out.println("WebViewActivity 111111111 values= "+values);
                                System.out.println("WebViewActivity 111111111 values1.)= "+values1);
                                System.out.println("WebViewActivity 111111111 values1.)= "+values1.get(0).getValue().toString());

                                if(!values.isEmpty() && !values1.isEmpty()){
                                    System.out.println("AddAssetActivity ethCall. result ="+values.get(0).toString());
                                    String symbol = values.get(0).toString();
                                    long time = System.currentTimeMillis();
                                    String uuid = UUID.randomUUID().toString();
                                    AssetDao.insertAssetInfo(
                                            new AssetEntity.Builder()
                                                    .id(uuid)
                                                    .walletId("1")
                                                    .assetType(ASSET_CONTRACT)
                                                    .createTime(time)
                                                    .updateTime(time)
                                                    .contractAddress(address)
                                                    .binary("")
                                                    .name("")
                                                    .symbol(symbol)
                                                    .build()
                                    );

                                    com.platon.wallet.entity.Transaction transaction = new com.platon.wallet.entity.Transaction.Builder()
                                            .hash(hash)
                                            .timestamp(time)
                                            .txType(String.valueOf(TransactionType.CONTRACT_CREATION.getTxTypeValue()))
                                            .txReceiptStatus(TransactionStatus.SUCCESSED.getTransactionStatusValue())
                                            .from(walletEntity.getAddress())
                                            .to(address)
                                            .contractAddress(address)
                                            .assetId(uuid)
                                            .symbol(symbol)
                                            .rAddress(walletEntity.getAddress())
                                            .value(values1.get(0).getValue().toString())
                                            .chainId(NodeManager.getInstance().getChainId())
                                            .build();
                                    TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                                }
                            }
                            return "";
                        }
                        @Override
                        protected void onPostExecute(@NonNull String json) {

                        }
                    }.execute();
                }
            }
        }
    }*/
    private PlatonGetTransactionReceipt getTransactionReceipt(String hash) throws IOException {
        Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
        return mWeb3j.platonGetTransactionReceipt(hash).send();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getContractAddress(String hash){
        String contractAddress = "";
        try {
            PlatonGetTransactionReceipt receipt = getTransactionReceipt(hash);

            if(receipt != null && !receipt.hasError() ){
                if(receipt.getTransactionReceipt().isPresent()){
                    TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().get();

                    System.out.println("transactionReceipt.getContractAddress2 = "+ transactionReceipt);
                    System.out.println("transactionReceipt.getContractAddress = "+ transactionReceipt.getContractAddress());
                    contractAddress = transactionReceipt.getContractAddress();
                }else{
                    System.out.println("transactionReceipt.getContractAddress2 = empty");
                    //Util.sleep(2000);
                    contractAddress =  getContractAddress(hash);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contractAddress;
    }
}