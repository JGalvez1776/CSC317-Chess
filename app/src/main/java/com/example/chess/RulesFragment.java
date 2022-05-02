/*
 * @author: Min Tran
 * @author: Jaygee Galvez
 * @description: This fragment handles the rules screen, which opens a webpage of chess rules.
 */

package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.fragment.app.Fragment;

public class RulesFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_rules;

    /**
     * Upon view creation, setups layout, and returns inflated view.
     * @param inflater - layout inflater
     * @param container - view group container
     * @param savedInstanceState - saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        View inflatedView = inflater.inflate(LAYOUT, container, false);

        // open rules webpage
        WebView webView = inflatedView.findViewById(R.id.web_view);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.loadUrl("https://www.chess.com/learn-how-to-play-chess");

        return inflatedView;
    }
}
