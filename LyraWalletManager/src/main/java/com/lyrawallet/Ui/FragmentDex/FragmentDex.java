package com.lyrawallet.Ui.FragmentDex;

import static java.lang.Integer.getInteger;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Api.ApiWebActions.ApiDex;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.FragmentStaking.FragmentStaking;
import com.lyrawallet.Ui.FragmentStaking.StakingGalleryAdapter;
import com.lyrawallet.Util.Concatenate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentDex extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    DexGalleryAdapter adapter;
    ApiDex.AllSupported supportedTokens = null;
    ApiDex.AllBalances dexWallets = null;

    public FragmentDex() {
        // Required empty public constructor
    }

    public static FragmentDex newInstance(String param1, String param2) {
        FragmentDex fragment = new FragmentDex();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dex, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static class DexEntry {
        String Ticker;
        double SpotQty;
        double DexQty;
        double Fee;

        public void setTicker(String ticker) {
            Ticker = ticker;
        }
        public void setSpotQty(double qty) {
            SpotQty = qty;
        }
        public void setDexQty(double qty) {
            DexQty = qty;
        }
        public void setFee(double fee) {
            Fee = fee;
        }

        public DexEntry(String ticker, double spotQty, double dexQty, double fee) {
            this.Ticker = ticker;
            this.SpotQty = spotQty;
            this.DexQty = dexQty;
            this.Fee = fee;
        }
    }

    public static class ClickListener {
        public void click(int index) {

        }
    }

    void populateGallery(View v) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        ProgressBar progress = v.findViewById(R.id.fragmentDex_ProgressBar);
        progress.setVisibility(View.VISIBLE);
        NetworkWebHttps getSupportedWebHttpsTask = new NetworkWebHttps(null);
        List<DexEntry> list = new ArrayList<>();
        getSupportedWebHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
            @Override
            public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                System.out.println("Static function thread: " + instance.getContent());
                NetworkWebHttps getWalletsWebHttpsTask = new NetworkWebHttps(null);
                getWalletsWebHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
                    @Override
                    public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                        System.out.println("Static function thread: " + instance.getContent());
                        dexWallets = new ApiDex.AllBalances().fromJson(instance.getContent());
                        Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> intHistoryList = Global.getWalletHistory(Concatenate.getHistoryFileName());
                        List<Pair<String, Double>> spotBalances = null;
                        boolean historyAvailable = true;
                        if (intHistoryList == null || intHistoryList.second == null) {
                            historyAvailable = false;
                        } else {
                            if(intHistoryList.second.size() > 0)
                                spotBalances = intHistoryList.second.get(intHistoryList.second.size() - 1).getBalances();
                        }
                        progress.setVisibility(View.GONE);
                        FragmentDex.ClickListener settingsListener = new FragmentDex.ClickListener() {
                            @Override
                            public void click(int index) {
                            }
                        };
                        FragmentDex.ClickListener dexToSpotListener = new FragmentDex.ClickListener() {
                            @Override
                            public void click(int index) {
                                //TODO Dex to SPOT
                            }
                        };
                        FragmentDex.ClickListener spotToDexListener = new FragmentDex.ClickListener() {
                            @Override
                            public void click(int index) {
                                //TODO SPOT to DEX
                            }
                        };
                        FragmentDex.ClickListener depositListener = new FragmentDex.ClickListener() {
                            @Override
                            public void click(int index) {
                                int i = 0;
                                try {
                                    for (; i < supportedTokens.getSuportedList().size(); i++) {
                                        if (supportedTokens.getSuportedList().get(index).getSymbol().equals(dexWallets.getBalancesList().get(i).getExtSymbol()))
                                            break;
                                    }
                                    String ticker = dexWallets.getBalancesList().get(i).getExtSymbol();
                                    String provider = dexWallets.getBalancesList().get(i).getExtProvider();
                                    String depositAddress = dexWallets.getBalancesList().get(i).getExtAddress();

                                    String contractAddress = supportedTokens.getSuportedList().get(index).getContract();
                                    double minDeposit = supportedTokens.getSuportedList().get(index).getMinDeposit();
                                    double depositionFee = supportedTokens.getSuportedList().get(index).getDepositFee();
                                    String conf = supportedTokens.getSuportedList().get(index).getConfirmationInfo();
                                    int confirmations = 0;
                                    try {
                                        if (conf != null) {
                                            if (conf.split(" ")[0] != null) {
                                                confirmations = Integer.decode(conf.split(" ")[0]);
                                            }
                                        }
                                    } catch(NullPointerException ignored) {};
                                    new FragmentManagerUser().goToReceive(ticker, provider, contractAddress, depositAddress, minDeposit, depositionFee, confirmations);
                                } catch (IndexOutOfBoundsException ignored) {}
                            }
                        };
                        FragmentDex.ClickListener withdrawListener = new FragmentDex.ClickListener() {
                            @Override
                            public void click(int index) {
                                try {
                                    int i = 0;
                                    for (; i < supportedTokens.getSuportedList().size(); i++) {
                                        if (supportedTokens.getSuportedList().get(index).getSymbol().equals(dexWallets.getBalancesList().get(i).getExtSymbol()))
                                            break;
                                    }
                                    String ticker = dexWallets.getBalancesList().get(i).getExtSymbol();
                                    double maxAmount = dexWallets.getBalancesList().get(i).getBalances().get(0).second / 100_000_000;
                                    String provider = dexWallets.getBalancesList().get(i).getExtProvider();

                                    String contractAddress = supportedTokens.getSuportedList().get(index).getContract();
                                    double withdrawFee = supportedTokens.getSuportedList().get(index).getWithdrawFee();
                                    new FragmentManagerUser().goToSend(ticker, provider, contractAddress, maxAmount, withdrawFee);
                                } catch (IndexOutOfBoundsException ignored) {}
                            }
                        };
                        RecyclerView tokens_list_recycler = v.findViewById(R.id.fragmentDex_RecyclerView);
                        adapter = new DexGalleryAdapter(list, activity,
                                settingsListener, dexToSpotListener, spotToDexListener, depositListener, withdrawListener);
                        if(tokens_list_recycler != null) {
                            tokens_list_recycler.setAdapter(adapter);
                            tokens_list_recycler.setLayoutManager(
                                    new LinearLayoutManager(activity));
                        }
                        for (int n = 0; n < list.size(); n++) {
                            for (int i = 0; i < dexWallets.getBalancesList().size(); i++) {
                                if(dexWallets.getBalancesList().get(i).getIntSymbol().equals(list.get(n).Ticker)) {
                                    if(dexWallets.getBalancesList().get(i).getBalances().size() > 0) {
                                        adapter.list.get(n).setDexQty(dexWallets.getBalancesList().get(i).getBalances().get(0).second / 100000000);
                                        adapter.notifyItemChanged(n);
                                    }
                                }
                            }
                            if(historyAvailable && spotBalances != null) {
                                for (int i = 0; i < spotBalances.size(); i++) {
                                    if(GlobalLyra.domainToSymbol(spotBalances.get(i).first).equals(list.get(n).Ticker)) {
                                        adapter.list.get(n).setSpotQty(spotBalances.get(i).second);
                                        adapter.notifyItemChanged(n);
                                    }
                                }
                            }
                        }
                    }
                });
                getWalletsWebHttpsTask.execute(String.format("%s%s/GetAllDexWallets/?owner=%s", Global.getDexNetwork(), GlobalLyra.LYRA_NODE_API_URL, Global.getSelectedAccountId()));
                supportedTokens = new ApiDex.AllSupported().fromJson(instance.getContent());
                for (int cnt = 0; cnt < supportedTokens.getSuportedList().size(); cnt++) {
                    DexEntry entry = new DexEntry(String.format("$%s", supportedTokens.getSuportedList().get(cnt).getSymbol()), 0, 0, supportedTokens.getSuportedList().get(cnt).getWithdrawFee());
                    list.add(entry);
                }
            }
        });
        System.out.println(String.format("%s/GetSupportedExtToken/?networkid=%s", Global.getDexNetworkApi(), Global.getCurrentNetworkName().toLowerCase(Locale.ROOT)));
        getSupportedWebHttpsTask.execute(String.format("%s/GetSupportedExtToken/?networkid=%s", Global.getDexNetworkApi(), Global.getCurrentNetworkName().toLowerCase(Locale.ROOT)));

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            return;
        View v = new View((MainActivity) activity);
        View vv = view;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        TextView dexAccountNameTextView = view.findViewById(R.id.dexAccountNameTextView);
        dexAccountNameTextView.setText(String.format("%s/%s", Global.getSelectedAccountName(), Global.getCurrentNetworkName()));

        ImageButton refreshButton = view.findViewById(R.id.fragmentDex_Refresh_ImageButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                populateGallery(vv);
            }
        });
        populateGallery(vv);

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        //LayoutInflater inflater = requireActivity().getLayoutInflater();

        //new Accounts((MainActivity) getActivity()).promptForPassword(getContext(), v.getRootView());
    }
}
